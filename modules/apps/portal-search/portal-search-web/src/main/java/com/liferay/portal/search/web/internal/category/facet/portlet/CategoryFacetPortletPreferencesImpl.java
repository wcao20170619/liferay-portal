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

package com.liferay.portal.search.web.internal.category.facet.portlet;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.web.internal.helper.PortletPreferencesHelper;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author Lino Alves
 */
public class CategoryFacetPortletPreferencesImpl
	implements CategoryFacetPortletPreferences {

	public CategoryFacetPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferencesOptional) {

		_portletPreferencesHelper = new PortletPreferencesHelper(
			portletPreferencesOptional);
	}

	@Override
	public String getDisplayStyle() {
		return _portletPreferencesHelper.getString(
			CategoryFacetPortletPreferences.PREFERENCE_KEY_DISPLAY_STYLE,
			"cloud");
	}

	@Override
	public int getFrequencyThreshold() {
		return _portletPreferencesHelper.getInteger(
			CategoryFacetPortletPreferences.PREFERENCE_KEY_FREQUENCY_THRESHOLD,
			1);
	}

	@Override
	public int getMaxTerms() {
		return _portletPreferencesHelper.getInteger(
			CategoryFacetPortletPreferences.PREFERENCE_KEY_MAX_TERMS, 10);
	}

	@Override
	public String getOrder() {
		return _portletPreferencesHelper.getString(
			CategoryFacetPortletPreferences.PREFERENCE_KEY_ORDER, "count:desc");
	}

	@Override
	public String getParameterName() {
		return _portletPreferencesHelper.getString(
			CategoryFacetPortletPreferences.PREFERENCE_KEY_PARAMETER_NAME,
			"category");
	}

	@Override
	public String[] getVocabularyIds() {
		String vocabularyIds = _portletPreferencesHelper.getString(
			CategoryFacetPortletPreferences.PREFERENCE_VOCABULARY_IDS, null);

		return StringUtil.split(vocabularyIds);
	}

	@Override
	public boolean isFrequenciesVisible() {
		return _portletPreferencesHelper.getBoolean(
			CategoryFacetPortletPreferences.PREFERENCE_KEY_FREQUENCIES_VISIBLE,
			true);
	}

	private final PortletPreferencesHelper _portletPreferencesHelper;

}