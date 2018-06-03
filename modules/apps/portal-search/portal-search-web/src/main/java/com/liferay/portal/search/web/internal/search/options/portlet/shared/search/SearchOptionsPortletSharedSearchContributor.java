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

package com.liferay.portal.search.web.internal.search.options.portlet.shared.search;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.generic.BooleanClauseImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.search.web.internal.display.context.Keywords;
import com.liferay.portal.search.web.internal.display.context.SearchScope;
import com.liferay.portal.search.web.internal.search.options.constants.SearchOptionsPortletKeys;
import com.liferay.portal.search.web.internal.search.options.portlet.SearchOptionsPortletPreferences;
import com.liferay.portal.search.web.internal.search.options.portlet.SearchOptionsPortletPreferencesImpl;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 */
@Component(
	immediate = true,
	property = "javax.portlet.name=" + SearchOptionsPortletKeys.SEARCH_OPTIONS
)
public class SearchOptionsPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchOptionsPortletPreferences searchOptionsPortletPreferences =
			new SearchOptionsPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		setKeywords(
			searchOptionsPortletPreferences, portletSharedSearchSettings);

		filterByThisSite(
			searchOptionsPortletPreferences, portletSharedSearchSettings);
	}

	@SuppressWarnings("unchecked")
	protected void filterByThisSite(
		SearchOptionsPortletPreferences searchOptionsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		Optional<Long> groupIdOptional = getThisSiteGroupId(
			searchOptionsPortletPreferences, portletSharedSearchSettings);

		groupIdOptional.ifPresent(
			groupId -> {
				portletSharedSearchSettings.addCondition(
					new BooleanClauseImpl(
						new TermQueryImpl(
							Field.GROUP_ID, String.valueOf(groupId)),
						BooleanClauseOccur.MUST));
			});
	}

	protected long getScopeGroupId(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		ThemeDisplay themeDisplay =
			portletSharedSearchSettings.getThemeDisplay();

		return themeDisplay.getScopeGroupId();
	}

	protected Optional<SearchScope> getSearchScope(
		SearchOptionsPortletPreferences searchOptionsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		String parameterName =
			searchOptionsPortletPreferences.getScopeParameterName();

		Optional<String> parameterValueOptional =
			portletSharedSearchSettings.getParameter(parameterName);

		Optional<SearchScope> searchScopeOptional = parameterValueOptional.map(
			SearchScope::getSearchScope);

		return searchScopeOptional;
	}

	protected Optional<Long> getThisSiteGroupId(
		SearchOptionsPortletPreferences searchOptionsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		Optional<SearchScope> searchScopeOptional = getSearchScope(
			searchOptionsPortletPreferences, portletSharedSearchSettings);

		Optional<SearchScope> thisSiteSearchScopeOptional =
			searchScopeOptional.filter(SearchScope.THIS_SITE::equals);

		return thisSiteSearchScopeOptional.map(
			searchScope -> getScopeGroupId(portletSharedSearchSettings));
	}

	protected boolean isLuceneSyntax(
		SearchOptionsPortletPreferences searchOptionsPortletPreferences,
		Keywords keywords) {

		if (keywords.isLuceneSyntax()) {
			return true;
		}

		return false;
	}

	protected void setEmptySerchEnabled(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchContext searchContext =
			portletSharedSearchSettings.getSearchContext();

		searchContext.setAttribute(
			SearchOptionsPortletPreferences.PREFERENCE_KEY_EMPTY_SEARCH_ENABLED,
			Boolean.TRUE);
	}

	protected void setKeywords(
		SearchOptionsPortletPreferences searchOptionsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		Optional<String> optional = portletSharedSearchSettings.getParameter(
			searchOptionsPortletPreferences.getKeywordsParameterName());

		optional.ifPresent(
			value -> {
				Keywords keywords = new Keywords(value);

				portletSharedSearchSettings.setKeywords(keywords.getKeywords());

				if (isLuceneSyntax(searchOptionsPortletPreferences, keywords)) {
					setLuceneSyntax(portletSharedSearchSettings);
				}
			});

		portletSharedSearchSettings.setKeywordsParameterName(
			searchOptionsPortletPreferences.getKeywordsParameterName());

		if (searchOptionsPortletPreferences.isEmptySearchEnabled()) {
			setEmptySerchEnabled(portletSharedSearchSettings);
		}
	}

	protected void setLuceneSyntax(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchContext searchContext =
			portletSharedSearchSettings.getSearchContext();

		searchContext.setAttribute("luceneSyntax", Boolean.TRUE);
	}

}