/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.search.related.results.web.internal.portlet;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.legacy.document.DocumentBuilderFactory;
import com.liferay.portal.search.related.results.web.internal.builder.SearchRelatedResultsDocumentDisplayContextBuilder;
import com.liferay.portal.search.related.results.web.internal.constants.SearchRelatedResultsPortletKeys;
import com.liferay.portal.search.related.results.web.internal.display.context.SearchRelatedResultsDisplayContext;
import com.liferay.portal.search.related.results.web.internal.display.context.SearchRelatedResultsDocumentDisplayContext;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.summary.SummaryBuilderFactory;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-search-related-results",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.init-param.template-path=/META-INF/resources/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + SearchRelatedResultsPortletKeys.SEARCH_RELATED_RESULTS,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class SearchRelatedResultsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			_portletSharedSearchRequest.search(renderRequest);

		SearchRelatedResultsPortletPreferences
			searchRelatedResultsPortletPreferences =
				new SearchRelatedResultsPortletPreferencesImpl(
					portletSharedSearchResponse.getPortletPreferences(
						renderRequest));

		SearchResponse searchResponse =
			portletSharedSearchResponse.getFederatedSearchResponse(
				searchRelatedResultsPortletPreferences.
					getFederatedSearchKeyOptional());

		int maxItemDisplay =
			searchRelatedResultsPortletPreferences.getMaxItemDisplay();

		if ((searchResponse != null) &&
			(searchResponse.getTotalHits() > maxItemDisplay)) {

			renderRequest.setAttribute("maxItemDisplay", maxItemDisplay);

			portletSharedSearchResponse = _portletSharedSearchRequest.search(
				renderRequest);
		}

		SearchRelatedResultsDisplayContext searchRelatedResultsDisplayContext =
			null;

		try {
			searchRelatedResultsDisplayContext = buildDisplayContext(
				portletSharedSearchResponse, renderRequest, renderResponse,
				searchRelatedResultsPortletPreferences);
		}
		catch (ConfigurationException ce) {
		}
		catch (Exception e) {
			throw new PortletException(e);
		}

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			searchRelatedResultsDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	protected SearchRelatedResultsDisplayContext buildDisplayContext(
			PortletSharedSearchResponse portletSharedSearchResponse,
			RenderRequest renderRequest, RenderResponse renderResponse,
			SearchRelatedResultsPortletPreferences
				searchRelatedResultsPortletPreferences)
		throws Exception {

		SearchRelatedResultsDisplayContext searchRelatedResultsDisplayContext =
			new SearchRelatedResultsDisplayContext(
				getHttpServletRequest(renderRequest));

		SearchResponse searchResponse =
			portletSharedSearchResponse.getFederatedSearchResponse(
				searchRelatedResultsPortletPreferences.
					getFederatedSearchKeyOptional());

		if (searchResponse == null) {
			return searchRelatedResultsDisplayContext;
		}

		searchRelatedResultsDisplayContext.setTotalHits(
			searchResponse.getTotalHits());

		List<Document> legacyDocuments = searchResponse.getDocuments71();

		searchRelatedResultsDisplayContext.setDocuments(legacyDocuments);

		searchRelatedResultsDisplayContext.
			setSearchRelatedResultsDocumentDisplayContexts(
				buildSearchRelatedResultsDocumentDisplayContexts(
					legacyDocuments, renderRequest, renderResponse,
					portletSharedSearchResponse.getThemeDisplay(
						renderRequest)));

		return searchRelatedResultsDisplayContext;
	}

	protected List<SearchRelatedResultsDocumentDisplayContext>
			buildSearchRelatedResultsDocumentDisplayContexts(
				List<Document> documents, RenderRequest renderRequest,
				RenderResponse renderResponse, ThemeDisplay themeDisplay)
		throws Exception {

		List<SearchRelatedResultsDocumentDisplayContext>
			searchRelatedResultsDocumentDisplayContexts = new ArrayList<>();

		for (Document document : documents) {
			searchRelatedResultsDocumentDisplayContexts.add(
				doBuildSummary(
					document, renderRequest, renderResponse, themeDisplay));
		}

		return searchRelatedResultsDocumentDisplayContexts;
	}

	protected SearchRelatedResultsDocumentDisplayContext doBuildSummary(
			Document document, RenderRequest renderRequest,
			RenderResponse renderResponse, ThemeDisplay themeDisplay)
		throws Exception {

		SearchRelatedResultsDocumentDisplayContextBuilder
			searchRelatedResultsDocumentDisplayContextBuilder =
				new SearchRelatedResultsDocumentDisplayContextBuilder();

		searchRelatedResultsDocumentDisplayContextBuilder.
			setAssetEntryLocalService(
				_assetEntryLocalService
			).setDocument(
				document
			).setDocumentBuilderFactory(
				_documentBuilderFactory
			).setFastDateFormatFactory(
				_fastDateFormatFactory
			).setHighlightEnabled(
				false
			).setIndexerRegistry(
				_indexerRegistry
			).setLocale(
				themeDisplay.getLocale()
			).setPortal(
				_portal
			).setRenderRequest(
				renderRequest
			).setRenderResponse(
				renderResponse
			).setResourceActions(
				_resourceActions
			).setSummaryBuilderFactory(
				_summaryBuilderFactory
			).setThemeDisplay(
				themeDisplay
			);

		return searchRelatedResultsDocumentDisplayContextBuilder.build();
	}

	protected HttpServletRequest getHttpServletRequest(
		RenderRequest renderRequest) {

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(renderRequest);

		return liferayPortletRequest.getHttpServletRequest();
	}
	
	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private DocumentBuilderFactory _documentBuilderFactory;

	@Reference
	private FastDateFormatFactory _fastDateFormatFactory;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private PortletSharedSearchRequest _portletSharedSearchRequest;

	@Reference
	private ResourceActions _resourceActions;

	@Reference
	private SummaryBuilderFactory _summaryBuilderFactory;

}