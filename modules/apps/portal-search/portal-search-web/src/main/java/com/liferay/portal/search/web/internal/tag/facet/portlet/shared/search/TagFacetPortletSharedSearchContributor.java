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

package com.liferay.portal.search.web.internal.tag.facet.portlet.shared.search;

import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.search.facet.tag.AssetTagNamesFacetFactory;
import com.liferay.portal.search.web.internal.tag.facet.builder.AssetTagsFacetBuilder;
import com.liferay.portal.search.web.internal.tag.facet.constants.TagFacetPortletKeys;
import com.liferay.portal.search.web.internal.tag.facet.portlet.TagFacetPortletPreferences;
import com.liferay.portal.search.web.internal.tag.facet.portlet.TagFacetPortletPreferencesImpl;
import com.liferay.portal.search.web.internal.util.SearchOptionalUtil;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 */
@Component(
	immediate = true,
	property = "javax.portlet.name=" + TagFacetPortletKeys.TAG_FACET
)
public class TagFacetPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		TagFacetPortletPreferences tagFacetPortletPreferences =
			new TagFacetPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		Facet facet = buildFacet(
			tagFacetPortletPreferences, portletSharedSearchSettings);

		portletSharedSearchSettings.addFacet(facet);
	}

	protected Facet buildFacet(
		TagFacetPortletPreferences tagFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		AssetTagsFacetBuilder assetTagsFacetBuilder = new AssetTagsFacetBuilder(
			assetTagNamesFacetFactory);

		assetTagsFacetBuilder.setFrequencyThreshold(
			tagFacetPortletPreferences.getFrequencyThreshold());
		assetTagsFacetBuilder.setMaxTerms(
			tagFacetPortletPreferences.getMaxTerms());
		assetTagsFacetBuilder.setSearchContext(
			portletSharedSearchSettings.getSearchContext());

		SearchOptionalUtil.copy(
			() -> portletSharedSearchSettings.getParameterValues(
				URLCodec.encodeURL(
					tagFacetPortletPreferences.getParameterName())),
			assetTagsFacetBuilder::setSelectedTagNames);

		return assetTagsFacetBuilder.build();
	}

	@Reference
	protected AssetTagNamesFacetFactory assetTagNamesFacetFactory;

}