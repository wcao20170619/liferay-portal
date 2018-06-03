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

package com.liferay.portal.search.web.internal.search.options.portlet;

import com.liferay.portal.search.web.internal.display.context.SearchScopePreference;
import com.liferay.portal.search.web.internal.util.PortletPreferencesHelper;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author Wade Cao
 */
public class SearchOptionsPortletPreferencesImpl
	implements SearchOptionsPortletPreferences {

	public SearchOptionsPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferencesOptional) {

		_portletPreferencesHelper = new PortletPreferencesHelper(
			portletPreferencesOptional);
	}

	@Override
	public String getKeywordsParameterName() {
		return _portletPreferencesHelper.getString(
			SearchOptionsPortletPreferences.
				PREFERENCE_KEY_KEYWORDS_PARAMETER_NAME,
			"q");
	}

	@Override
	public String getScopeParameterName() {
		return _portletPreferencesHelper.getString(
			SearchOptionsPortletPreferences.PREFERENCE_KEY_SCOPE_PARAMETER_NAME,
			"scope");
	}

	@Override
	public SearchScopePreference getSearchScopePreference() {
		Optional<String> valueOptional = _portletPreferencesHelper.getString(
			SearchOptionsPortletPreferences.PREFERENCE_KEY_SEARCH_SCOPE);

		Optional<SearchScopePreference> searchScopePreferenceOptional =
			valueOptional.map(SearchScopePreference::getSearchScopePreference);

		return searchScopePreferenceOptional.orElse(
			SearchScopePreference.EVERYTHING);
	}

	@Override
	public String getSearchScopePreferenceString() {
		SearchScopePreference searchScopePreference =
			getSearchScopePreference();

		return searchScopePreference.getPreferenceString();
	}

	@Override
	public boolean isEmptySearchEnabled() {
		return _portletPreferencesHelper.getBoolean(
			SearchOptionsPortletPreferences.PREFERENCE_KEY_EMPTY_SEARCH_ENABLED,
			false);
	}

	private final PortletPreferencesHelper _portletPreferencesHelper;

}