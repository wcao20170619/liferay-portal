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

package com.liferay.portal.search.web.internal.search.morelikethis.portlet;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.permission.PortletPermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.legacy.document.DocumentBuilderFactory;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.summary.SummaryBuilderFactory;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactory;
import com.liferay.portal.search.web.internal.display.context.SearchResultPreferences;
import com.liferay.portal.search.web.internal.document.DocumentFormPermissionChecker;
import com.liferay.portal.search.web.internal.document.DocumentFormPermissionCheckerImpl;
import com.liferay.portal.search.web.internal.result.display.builder.AssetRendererFactoryLookup;
import com.liferay.portal.search.web.internal.result.display.builder.SearchResultContentDisplayBuilder;
import com.liferay.portal.search.web.internal.result.display.builder.SearchResultSummaryDisplayBuilder;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultContentDisplayContext;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext;
import com.liferay.portal.search.web.internal.search.morelikethis.constants.SearchMoreLikeThisPortletKeys;
import com.liferay.portal.search.web.internal.search.morelikethis.display.context.SearchMoreLikeThisPortletDisplayBuilder;
import com.liferay.portal.search.web.internal.search.morelikethis.display.context.SearchMoreLikeThisPortletDisplayContext;
import com.liferay.portal.search.web.internal.search.results.portlet.SearchResultPreferencesImpl;
import com.liferay.portal.search.web.internal.search.results.portlet.SearchResultsPortletPreferences;
import com.liferay.portal.search.web.internal.search.results.portlet.SearchResultsPortletPreferencesImpl;
import com.liferay.portal.search.web.internal.search.results.portlet.SearchResultsSummariesHolder;
import com.liferay.portal.search.web.internal.util.SearchPortletPermissionUtil;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.search.result.SearchResultImageContributor;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
	    "com.liferay.portlet.css-class-wrapper=portlet-search-more-like-this",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=Search More Like This",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/META-INF/resources/",
		"javax.portlet.init-param.view-template=/search/morelikethis/view.jsp",
		"javax.portlet.name=" + SearchMoreLikeThisPortletKeys.MORE_LIKE_THIS_PORTLET,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = Portlet.class
)

public class SearchMoreLikeThisPortlet extends MVCPortlet {
	
	@Override
	public void render(
		RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

//		if (!SearchPortletPermissionUtil.containsConfiguration(
//				portletPermission, renderRequest, portal)) {
//
//			renderRequest.setAttribute(
//				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
//		}
//		
		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);
		
//		SearchMoreLikeThisPortletPreferences searchMoreLikeThisPortletPreferences =
//			new SearchMoreLikeThisPortletPreferencesImpl(
//				portletSharedSearchResponse.getPortletPreferences(renderRequest));
		
		HttpServletRequest httpServletRequest = portal.getHttpServletRequest(
				renderRequest);
		
		SearchResultContentDisplayContext searchResultContentDisplayContext = 
			getSearchResultContentDisplayContext(
				renderRequest, renderResponse,
				httpServletRequest,
				portletSharedSearchResponse);
		
		renderRequest.setAttribute(
			"view_content",
			searchResultContentDisplayContext);
		
	    SearchMoreLikeThisPortletDisplayContext searchMoreLikeThisPortletDisplayContext =
	    	new SearchMoreLikeThisPortletDisplayBuilder(
	    		complexQueryPartBuilderFactory,
	    		httpServletRequest, 
	    		indexNameBuilder,
	    		language,
	    		portletSharedSearchResponse,
				queries, renderRequest, renderResponse, 
//				searcher, searchRequestBuilderFactory
				searchEngineAdapter
			).build();
		
		renderRequest.setAttribute(
			SearchMoreLikeThisPortletKeys.MORE_LIKE_THIS_PORTLET_DISPLAY_CONTEXT,
			searchMoreLikeThisPortletDisplayContext);
		
		super.render(renderRequest, renderResponse);
	}
	
	protected SearchResultContentDisplayContext getSearchResultContentDisplayContext(
			RenderRequest renderRequest, RenderResponse renderResponse,
			HttpServletRequest httpServletReques,
			PortletSharedSearchResponse portletSharedSearchResponse)  {
		SearchResultContentDisplayBuilder searchResultContentDisplayBuilder = new SearchResultContentDisplayBuilder();

		ThemeDisplay themeDisplay = portletSharedSearchResponse.getThemeDisplay(
				renderRequest);
	
		String assetEntryId = getParameterValue("assetEntryId", 
				portletSharedSearchResponse,
				renderRequest);
		
		String assetType = getParameterValue("assetType", 
				portletSharedSearchResponse,
				renderRequest);
	
		PermissionChecker permissionChecker =
                PermissionThreadLocal.getPermissionChecker();

		 
		searchResultContentDisplayBuilder.setAssetEntryId(GetterUtil.getLong(assetEntryId));
		searchResultContentDisplayBuilder.setLocale(themeDisplay.getLocale());
		searchResultContentDisplayBuilder.setPermissionChecker(permissionChecker);
		searchResultContentDisplayBuilder.setPortal(portal);
		searchResultContentDisplayBuilder.setRenderRequest(renderRequest);
		searchResultContentDisplayBuilder.setRenderResponse(renderResponse);
		searchResultContentDisplayBuilder.setType(GetterUtil.getString(assetType));

		SearchResultContentDisplayContext searchResultContentDisplayContext = null;
		
		try {
			searchResultContentDisplayContext = searchResultContentDisplayBuilder.build();
		} catch (Exception e) {
		}
	
		return searchResultContentDisplayContext;
	}
	
	protected String getParameterValue(
		String parameterName, 
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {
		Optional<String> valueOptional =
				portletSharedSearchResponse.getParameter(parameterName, renderRequest);

		return valueOptional.orElse(StringPool.BLANK);
	}
	
	protected void addSearchResultImageContributor(
		SearchResultImageContributor searchResultImageContributor) {

		_searchResultImageContributors.add(searchResultImageContributor);
	}
	
	protected String getCurrentURL(RenderRequest renderRequest) {
		return portal.getCurrentURL(renderRequest);
	}

	protected HttpServletRequest getHttpServletRequest(
		RenderRequest renderRequest) {

		LiferayPortletRequest liferayPortletRequest =
			portal.getLiferayPortletRequest(renderRequest);

		return liferayPortletRequest.getHttpServletRequest();
	}

	
	@Reference
	protected AssetEntryLocalService assetEntryLocalService;

	protected AssetRendererFactoryLookup assetRendererFactoryLookup;

	@Reference
	protected DocumentBuilderFactory documentBuilderFactory;
	
	@Reference
	protected FastDateFormatFactory fastDateFormatFactory;

	
	@Reference
	protected Portal portal;
	
	protected boolean imageRequested;
	
	@Reference
	protected IndexerRegistry indexerRegistry;
	
	@Reference
	protected ResourceActions resourceActions;
	
	@Reference
	protected SummaryBuilderFactory summaryBuilderFactory;


	@Reference
	protected PortletPermission portletPermission;
	
	@Reference
	protected Language language;

	@Reference
	protected Queries queries;
	
//	@Reference
//	protected Searcher searcher;
//	
//	@Reference
//	protected SearchRequestBuilderFactory searchRequestBuilderFactory;
	
	@Reference
	protected SearchEngineAdapter searchEngineAdapter;
	
	@Reference
	protected IndexNameBuilder indexNameBuilder;
	
	@Reference
	protected ComplexQueryPartBuilderFactory complexQueryPartBuilderFactory;
	
	@Reference
    protected PortletSharedSearchRequest portletSharedSearchRequest;
	
	private final Set<SearchResultImageContributor>
		_searchResultImageContributors = new HashSet<>();
	
}