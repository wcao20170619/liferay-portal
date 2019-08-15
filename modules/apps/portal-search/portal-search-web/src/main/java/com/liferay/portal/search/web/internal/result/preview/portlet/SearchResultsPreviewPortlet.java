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

package com.liferay.portal.search.web.internal.result.preview.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.web.internal.result.display.builder.SearchResultContentDisplayBuilder;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultContentDisplayContext;
import com.liferay.portal.search.web.internal.result.preview.portlet.constants.SearchResultsPreviewPortletKeys;
import com.liferay.portal.search.web.internal.util.RecommenderHelper;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import java.io.IOException;

import java.util.List;
import java.util.Optional;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-search-results-preview",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=Search Result Preview",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/META-INF/resources/",
		"javax.portlet.init-param.view-template=/result/preview/view.jsp",
		"javax.portlet.name=" + SearchResultsPreviewPortletKeys.SEARCH_RESULTS_PREVIEW,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = Portlet.class
)
public class SearchResultsPreviewPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		SearchResultContentDisplayContext searchResultContentDisplayContext =
			buildSearchResultContentDisplayContext(
				renderRequest, renderResponse, portletSharedSearchResponse);

		renderRequest.setAttribute(
			SearchResultsPreviewPortletKeys.SEARCH_RESULTS_VIEW_CONTENT,
			searchResultContentDisplayContext);

		RecommenderHelper recommenderHelper = new RecommenderHelper(
			portletSharedSearchResponse);

		List<Document> documents =
			recommenderHelper.getMoreLikeThisSearchDocuments(renderRequest);

		renderRequest.setAttribute("morelikethis", documents);

		super.render(renderRequest, renderResponse);
	}

	protected SearchResultContentDisplayContext
		buildSearchResultContentDisplayContext(
			RenderRequest renderRequest, RenderResponse renderResponse,
			PortletSharedSearchResponse portletSharedSearchResponse) {

		ThemeDisplay themeDisplay = portletSharedSearchResponse.getThemeDisplay(
			renderRequest);

		String assetEntryId = getParameterValue(
			"assetEntryId", portletSharedSearchResponse, renderRequest);

		String assetType = getParameterValue(
			"assetType", portletSharedSearchResponse, renderRequest);

		if (Validator.isBlank(assetEntryId) || Validator.isBlank(assetType)) {
			return new SearchResultContentDisplayContext();
		}

		SearchResultContentDisplayBuilder searchResultContentDisplayBuilder =
			new SearchResultContentDisplayBuilder();

		searchResultContentDisplayBuilder.setAssetEntryId(
			GetterUtil.getLong(assetEntryId));
		searchResultContentDisplayBuilder.setLocale(themeDisplay.getLocale());
		searchResultContentDisplayBuilder.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		searchResultContentDisplayBuilder.setPortal(portal);
		searchResultContentDisplayBuilder.setRenderRequest(renderRequest);
		searchResultContentDisplayBuilder.setRenderResponse(renderResponse);
		searchResultContentDisplayBuilder.setType(
			GetterUtil.getString(assetType));

		SearchResultContentDisplayContext searchResultContentDisplayContext =
			null;

		try {
			searchResultContentDisplayContext =
				searchResultContentDisplayBuilder.build();
		}
		catch (Exception e) {
			searchResultContentDisplayContext =
				new SearchResultContentDisplayContext();
		}

		return searchResultContentDisplayContext;
	}

	protected String getCurrentURL(RenderRequest renderRequest) {
		return portal.getCurrentURL(renderRequest);
	}

	protected String getParameterValue(
		String parameterName,
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		Optional<String> valueOptional =
			portletSharedSearchResponse.getParameter(
				parameterName, renderRequest);

		return valueOptional.orElse(StringPool.BLANK);
	}

	@Reference
	protected Portal portal;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

}