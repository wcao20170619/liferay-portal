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

package com.liferay.portal.search.web.internal.user.facet.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.facet.user.UserFacetFactory;
import com.liferay.portal.search.web.internal.facet.display.builder.UserSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.UserSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.user.facet.constants.UserFacetPortletKeys;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.fragment.entry.processor.portlet.alias=search-facet-user",
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-user-facet",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=User Facet",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/user/facet/view.jsp",
		"javax.portlet.name=" + UserFacetPortletKeys.USER_FACET,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = Portlet.class
)
public class UserFacetPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		UserSearchFacetDisplayContext userSearchFacetDisplayContext =
			buildDisplayContext(portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, userSearchFacetDisplayContext);

		if (userSearchFacetDisplayContext.isRenderNothing()) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		super.render(renderRequest, renderResponse);
	}

	protected UserSearchFacetDisplayContext buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		Facet facet = portletSharedSearchResponse.getFacet(getFieldName());

		UserFacetConfiguration userFacetConfiguration =
			new UserFacetConfigurationImpl(facet.getFacetConfiguration());

		UserFacetPortletPreferences userFacetPortletPreferences =
			new UserFacetPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		UserSearchFacetDisplayBuilder userSearchFacetDisplayBuilder =
			new UserSearchFacetDisplayBuilder();

		userSearchFacetDisplayBuilder.setFacet(facet);
		userSearchFacetDisplayBuilder.setFrequenciesVisible(
			userFacetPortletPreferences.isFrequenciesVisible());
		userSearchFacetDisplayBuilder.setFrequencyThreshold(
			userFacetConfiguration.getFrequencyThreshold());
		userSearchFacetDisplayBuilder.setMaxTerms(
			userFacetConfiguration.getMaxTerms());

		String parameterName = userFacetPortletPreferences.getParameterName();

		userSearchFacetDisplayBuilder.setParamName(parameterName);

		Optional<List<String>> usersOptional = getParameterValues(
			URLCodec.encodeURL(parameterName), portletSharedSearchResponse,
			renderRequest);

		usersOptional.ifPresent(userSearchFacetDisplayBuilder::setParamValues);

		return userSearchFacetDisplayBuilder.build();
	}

	protected String getFieldName() {
		Facet facet = userFacetFactory.newInstance(null);

		return facet.getFieldName();
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

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	@Reference
	protected UserFacetFactory userFacetFactory;

}