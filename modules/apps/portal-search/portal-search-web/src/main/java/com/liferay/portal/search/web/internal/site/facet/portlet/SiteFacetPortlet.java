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

package com.liferay.portal.search.web.internal.site.facet.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.facet.site.SiteFacetFactory;
import com.liferay.portal.search.web.internal.facet.display.builder.ScopeSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.ScopeSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.site.facet.constants.SiteFacetPortletKeys;
import com.liferay.portal.search.web.internal.util.SearchOptionalUtil;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.search.request.SearchSettings;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.fragment.entry.processor.portlet.alias=search-facet-site",
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-site-facet",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=Site Facet",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/site/facet/view.jsp",
		"javax.portlet.name=" + SiteFacetPortletKeys.SITE_FACET,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = Portlet.class
)
public class SiteFacetPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		ScopeSearchFacetDisplayContext siteFacetPortletDisplayContext =
			buildDisplayContext(portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, siteFacetPortletDisplayContext);

		if (siteFacetPortletDisplayContext.isRenderNothing()) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		super.render(renderRequest, renderResponse);
	}

	protected ScopeSearchFacetDisplayContext buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		Facet facet = portletSharedSearchResponse.getFacet(getFieldName());

		ScopeFacetConfiguration siteFacetConfiguration =
			new ScopeFacetConfigurationImpl(facet.getFacetConfiguration());

		SiteFacetPortletPreferences siteFacetPortletPreferences =
			new SiteFacetPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		ScopeSearchFacetDisplayBuilder scopeSearchFacetDisplayBuilder =
			new ScopeSearchFacetDisplayBuilder();

		scopeSearchFacetDisplayBuilder.setFacet(facet);

		SearchOptionalUtil.copy(
			() -> getFilteredGroupIdsOptional(portletSharedSearchResponse),
			scopeSearchFacetDisplayBuilder::setFilteredGroupIds);

		scopeSearchFacetDisplayBuilder.setFrequencyThreshold(
			siteFacetConfiguration.getFrequencyThreshold());
		scopeSearchFacetDisplayBuilder.setFrequenciesVisible(
			siteFacetPortletPreferences.isFrequenciesVisible());
		scopeSearchFacetDisplayBuilder.setGroupLocalService(groupLocalService);
		scopeSearchFacetDisplayBuilder.setLocale(
			getLocale(portletSharedSearchResponse, renderRequest));
		scopeSearchFacetDisplayBuilder.setMaxTerms(
			siteFacetConfiguration.getMaxTerms());

		String parameterName = siteFacetPortletPreferences.getParameterName();

		scopeSearchFacetDisplayBuilder.setParameterName(parameterName);

		SearchOptionalUtil.copy(
			() -> getParameterValuesOptional(
				URLCodec.encodeURL(parameterName), portletSharedSearchResponse,
				renderRequest),
			scopeSearchFacetDisplayBuilder::setParameterValues);

		return scopeSearchFacetDisplayBuilder.build();
	}

	protected String getFieldName() {
		Facet facet = siteFacetFactory.newInstance(new SearchContext());

		return facet.getFieldName();
	}

	protected Optional<long[]> getFilteredGroupIdsOptional(
		PortletSharedSearchResponse portletSharedSearchResponse) {

		SearchSettings searchSettings =
			portletSharedSearchResponse.getSearchSettings();

		SearchContext searchContext = searchSettings.getSearchContext();

		return Optional.ofNullable(searchContext.getGroupIds());
	}

	protected Locale getLocale(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		ThemeDisplay themeDisplay = portletSharedSearchResponse.getThemeDisplay(
			renderRequest);

		return themeDisplay.getLocale();
	}

	protected Optional<List<String>> getParameterValuesOptional(
		String parameterName,
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		Optional<String[]> parameterValuesOptional =
			portletSharedSearchResponse.getParameterValues(
				parameterName, renderRequest);

		return parameterValuesOptional.map(Arrays::asList);
	}

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	@Reference
	protected SiteFacetFactory siteFacetFactory;

}