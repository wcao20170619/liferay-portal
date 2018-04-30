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

package com.liferay.portal.search.web.internal.custom.facet.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.facet.type.CustomFacetFactory;
import com.liferay.portal.search.web.internal.custom.facet.constants.CustomFacetPortletKeys;
import com.liferay.portal.search.web.internal.facet.display.builder.CustomSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.CustomSearchFacetDisplayContext;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;
import com.liferay.portal.search.web.search.request.SearchSettings;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
		"com.liferay.portlet.css-class-wrapper=portlet-custom-facet",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=Custom Facet",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/custom/facet/view.jsp",
		"javax.portlet.name=" + CustomFacetPortletKeys.CUSTOM_FACET,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = {Portlet.class, PortletSharedSearchContributor.class}
)
public class CustomFacetPortlet
	extends MVCPortlet implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		CustomFacetPortletPreferences customFacetPortletPreferences =
			new CustomFacetPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		Facet facet = buildFacet(
			customFacetPortletPreferences, portletSharedSearchSettings);

		addFacet(facet, portletSharedSearchSettings);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		CustomSearchFacetDisplayContext customSearchFacetDisplayContext =
			buildDisplayContext(portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, customSearchFacetDisplayContext);

		if (customSearchFacetDisplayContext.isRenderNothing()) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		super.render(renderRequest, renderResponse);
	}

	protected void addFacet(
		Facet facet, PortletSharedSearchSettings portletSharedSearchSettings) {

		String portletId = portletSharedSearchSettings.getPortletId();

		String facetKey = getCustomFacetKey(portletId);

		SearchContext searchContext =
			portletSharedSearchSettings.getSearchContext();

		searchContext.setAttribute(facetKey, facet.getFieldName());

		Map<String, Facet> facets = searchContext.getFacets();

		facets.put(facetKey, facet);
	}

	protected CustomSearchFacetDisplayContext buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		String portletId = getPortletId(renderRequest);

		String facetKey = getCustomFacetKey(portletId);

		SearchSettings searchSettings =
			portletSharedSearchResponse.getSearchSettings();

		SearchContext searchContext = searchSettings.getSearchContext();

		String fieldName = (String)searchContext.getAttribute(facetKey);

		Facet facet = null;

		if ((fieldName == null) || StringPool.BLANK.equals(fieldName)) {
			facet = customFacetFactory.newInstance(null);
		}
		else {
			facet = portletSharedSearchResponse.getFacet(fieldName);
		}

		CustomFacetPortletPreferences customFacetPortletPreferences =
			new CustomFacetPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		CustomSearchFacetDisplayBuilder customSearchFacetDisplayBuilder =
			new CustomSearchFacetDisplayBuilder();

		customSearchFacetDisplayBuilder.setFacet(facet);
		customSearchFacetDisplayBuilder.setFrequenciesVisible(
			customFacetPortletPreferences.isFrequenciesVisible());
		customSearchFacetDisplayBuilder.setFrequencyThreshold(
			customFacetPortletPreferences.getFrequencyThreshold());
		customSearchFacetDisplayBuilder.setMaxTerms(
			customFacetPortletPreferences.getMaxTerms());

		String parameterName = customFacetPortletPreferences.getParameterName();

		customSearchFacetDisplayBuilder.setParamName(parameterName);

		customSearchFacetDisplayBuilder.setFieldLabel(
			customFacetPortletPreferences.getFieldLabel());
		customSearchFacetDisplayBuilder.setFieldToAggregate(
			customFacetPortletPreferences.getFieldToAggregate());

		Optional<List<String>> fieldsOptional = getParameterValues(
			parameterName, portletSharedSearchResponse, renderRequest);

		fieldsOptional.ifPresent(
			customSearchFacetDisplayBuilder::setParamValues);

		return customSearchFacetDisplayBuilder.build();
	}

	protected Facet buildFacet(
		CustomFacetPortletPreferences customFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		CustomFacetBuilder customFacetBuilder = new CustomFacetBuilder(
			customFacetFactory);

		customFacetBuilder.setFieldToAggregate(
			customFacetPortletPreferences.getFieldToAggregate());
		customFacetBuilder.setSearchContext(
			portletSharedSearchSettings.getSearchContext());

		Optional<String[]> parameterValuesOptional =
			portletSharedSearchSettings.getParameterValues(
				customFacetPortletPreferences.getParameterName());

		parameterValuesOptional.ifPresent(
			customFacetBuilder::setSelectedFields);

		return customFacetBuilder.build();
	}

	protected String getCustomFacetKey(String portletId) {
		return CustomFacetPortletPreferences.PREFERENCE_KEY_FIELD_TO_AGGREGATE +
			"." + portletId;
	}

	protected Optional<List<String>> getParameterValues(
		String parameterName,
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		Optional<String[]> parameterValuesOptional =
			portletSharedSearchResponse.getParameterValues(
				parameterName, renderRequest);

		return parameterValuesOptional.map(Arrays::asList);
	}

	protected String getPortletId(RenderRequest renderRequest) {
		return _portal.getPortletId(renderRequest);
	}

	@Reference
	protected CustomFacetFactory customFacetFactory;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	@Reference
	private Portal _portal;

}