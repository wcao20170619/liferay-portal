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

package com.liferay.portal.search.web.internal.morelikethis.portlet.shared.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.query.MoreLikeThisQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.web.internal.morelikethis.DocumentUIDBuilder;
import com.liferay.portal.search.web.internal.morelikethis.constants.MoreLikeThisPortletKeys;
import com.liferay.portal.search.web.internal.morelikethis.portlet.MoreLikeThisPortletPreferences;
import com.liferay.portal.search.web.internal.morelikethis.portlet.MoreLikeThisPortletPreferencesImpl;
import com.liferay.portal.search.web.internal.portlet.preferences.PortletPreferencesLookup;
import com.liferay.portal.search.web.internal.util.SearchStringUtil;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(
	immediate = true,
	property = "javax.portlet.name=" + MoreLikeThisPortletKeys.MORE_LIKE_THIS_PORTLET,
	service = PortletSharedSearchContributor.class
)
public class MoreLikeThisPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		String uid = new DocumentUIDBuilder().currentURL(
			_portal.getCurrentURL(
				portletSharedSearchSettings.getRenderRequest())
		).build();

		if (Validator.isBlank(uid)) {
			return;
		}

		MoreLikeThisPortletPreferences moreLikeThisPortletPreferences =
			new MoreLikeThisPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferencesOptional());

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(
				moreLikeThisPortletPreferences.getFederatedSearchKeyOptional());

		searchRequestBuilder.emptySearchEnabled(true);

		searchRequestBuilder.addComplexQueryPart(
			_complexQueryPartBuilderFactory.builder(
			).query(
				createMoreLikeThisQuery(
					portletSharedSearchSettings, moreLikeThisPortletPreferences,
					uid)
			).build());
	}

	protected MoreLikeThisQuery createMoreLikeThisQuery(
		PortletSharedSearchSettings portletSharedSearchSettings,
		MoreLikeThisPortletPreferences moreLikeThisPortletPreferences,
		String uid) {

		Optional<String> fieldOptional = SearchStringUtil.maybe(
			moreLikeThisPortletPreferences.getFields());

		String[] fieldLocalized = getLocalizedFields(
			portletSharedSearchSettings,
			SearchStringUtil.splitAndUnquote(fieldOptional));

		MoreLikeThisQuery moreLikeThis = _queries.moreLikeThis(
			fieldLocalized, StringPool.BLANK);

		moreLikeThis.addDocumentIdentifier(
			_queries.documentIdentifier(
				moreLikeThisPortletPreferences.getIndexName(),
				moreLikeThisPortletPreferences.getDocType(), uid));

		return setMoreLikeThisParameters(
			moreLikeThis, portletSharedSearchSettings,
			moreLikeThisPortletPreferences);
	}

	protected Float getFloatValue(String value) {
		Float retValue = null;

		try {
			retValue = Float.valueOf(value);
		}
		catch (Exception e) {
		}

		return retValue;
	}

	protected Locale[] getLocales(ThemeDisplay themeDisplay) {
		long groupId = themeDisplay.getScopeGroupId();

		if (groupId <= 0) {
			Set<Locale> locales = _language.getCompanyAvailableLocales(
				themeDisplay.getCompanyId());

			return locales.toArray(new Locale[0]);
		}

		Set<Locale> locales = _language.getAvailableLocales(groupId);

		return locales.toArray(new Locale[0]);
	}

	protected String[] getLocalizedFieldNames(String prefix, Locale[] locales) {
		Set<String> fieldNames = new HashSet<>();

		for (Locale locale : locales) {
			fieldNames.add(Field.getLocalizedName(locale, prefix));
		}

		return fieldNames.toArray(new String[0]);
	}

	protected String[] getLocalizedFields(
		PortletSharedSearchSettings portletSharedSearchSettings,
		String... fields) {

		Locale[] locales = getLocales(
			portletSharedSearchSettings.getThemeDisplay());

		Set<String> fieldNames = new HashSet<>();

		for (String field : fields) {
			field = StringUtil.trim(field);

			if (isLocalizedField(field)) {
				Collections.addAll(
					fieldNames, getLocalizedFieldNames(field, locales));
			}
			else {
				fieldNames.add(field);
			}
		}

		return fieldNames.toArray(new String[0]);
	}

	protected boolean isLocalizedField(String field) {
		if (Field.TITLE.equals(field) || Field.DESCRIPTION.equals(field) ||
			Field.CONTENT.equals(field)) {

			return true;
		}

		return false;
	}

	protected MoreLikeThisQuery setMoreLikeThisParameters(
		MoreLikeThisQuery moreLikeThis,
		PortletSharedSearchSettings portletSharedSearchSettings,
		MoreLikeThisPortletPreferences moreLikeThisPortletPreferences) {

		moreLikeThis.setMaxQueryTerms(
			moreLikeThisPortletPreferences.getMaxQueryTerms());
		moreLikeThis.setMinTermFrequency(
			moreLikeThisPortletPreferences.getMinTermFrequency());
		moreLikeThis.setMinDocFrequency(
			moreLikeThisPortletPreferences.getMinDocFrequency());
		moreLikeThis.setMaxDocFrequency(
			moreLikeThisPortletPreferences.getMaxDocFrequency());
		moreLikeThis.setMinWordLength(
			moreLikeThisPortletPreferences.getMinWordLength());
		moreLikeThis.setMaxWordLength(
			moreLikeThisPortletPreferences.getMaxWordLength());

		String stopWords = moreLikeThisPortletPreferences.getStopWords();

		if (!Validator.isBlank(stopWords)) {
			moreLikeThis.addStopWord(stopWords);
		}

		String analyzer = moreLikeThisPortletPreferences.getAnalyzer();

		if (!Validator.isBlank(analyzer)) {
			moreLikeThis.addStopWord(analyzer);
		}

		String minShouldMatch =
			moreLikeThisPortletPreferences.getMinShouldMatch();

		if (!Validator.isBlank(minShouldMatch)) {
			moreLikeThis.setMinShouldMatch(minShouldMatch);
		}

		Float termBoost = getFloatValue(
			moreLikeThisPortletPreferences.getTermBoost());

		if (termBoost != null) {
			moreLikeThis.setTermBoost(termBoost);
		}

		return moreLikeThis;
	}

	@Reference
	private ComplexQueryPartBuilderFactory _complexQueryPartBuilderFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortletPreferencesLookup _portletPreferencesLookup;

	@Reference
	private Queries _queries;

}