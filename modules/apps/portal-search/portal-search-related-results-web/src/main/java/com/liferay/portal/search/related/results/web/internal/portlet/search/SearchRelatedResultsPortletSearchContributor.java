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

package com.liferay.portal.search.related.results.web.internal.portlet.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.query.MoreLikeThisQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.related.results.web.internal.builder.DocumentUIDBuilder;
import com.liferay.portal.search.related.results.web.internal.constants.SearchRelatedResultsPortletKeys;
import com.liferay.portal.search.related.results.web.internal.portlet.SearchRelatedResultsPortletPreferences;
import com.liferay.portal.search.related.results.web.internal.portlet.SearchRelatedResultsPortletPreferencesImpl;
import com.liferay.portal.search.related.results.web.internal.util.SearchStringUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
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
	property = "javax.portlet.name=" + SearchRelatedResultsPortletKeys.SEARCH_RELATED_RESULTS,
	service = PortletSharedSearchContributor.class
)
public class SearchRelatedResultsPortletSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		String uid = new DocumentUIDBuilder().currentURL(
			_portal.getCurrentURL(
				portletSharedSearchSettings.getRenderRequest())
		).groupId(
			portletSharedSearchSettings.getThemeDisplay(
			).getScopeGroupId()
		).build();

		if (Validator.isBlank(uid)) {
			return;
		}

		SearchRelatedResultsPortletPreferences
			searchRelatedResultsPortletPreferences =
				new SearchRelatedResultsPortletPreferencesImpl(
					portletSharedSearchSettings.
						getPortletPreferencesOptional());

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(
				searchRelatedResultsPortletPreferences.
					getFederatedSearchKeyOptional());

		searchRequestBuilder.emptySearchEnabled(true);

		searchRequestBuilder.addComplexQueryPart(
			_complexQueryPartBuilderFactory.builder(
			).query(
				createMoreLikeThisQuery(
					portletSharedSearchSettings,
					searchRelatedResultsPortletPreferences, uid)
			).build());
	}

	protected MoreLikeThisQuery createMoreLikeThisQuery(
		PortletSharedSearchSettings portletSharedSearchSettings,
		SearchRelatedResultsPortletPreferences
			searchRelatedResultsPortletPreferences,
		String uid) {

		Optional<String> fieldOptional = SearchStringUtil.maybe(
			searchRelatedResultsPortletPreferences.getFields());

		String[] fields = SearchStringUtil.splitAndUnquote(fieldOptional);

		String[] fieldLocalized = getLocalizedFields(
			portletSharedSearchSettings, fields);

		String[] fieldsAll = ArrayUtil.append(fieldLocalized, fields);

		MoreLikeThisQuery moreLikeThis = _queries.moreLikeThis(
			fieldsAll, StringPool.BLANK);

		moreLikeThis.addDocumentIdentifier(
			_queries.documentIdentifier(
				searchRelatedResultsPortletPreferences.getIndexName(),
				searchRelatedResultsPortletPreferences.getDocType(), uid));

		return setMoreLikeThisParameters(
			moreLikeThis, portletSharedSearchSettings,
			searchRelatedResultsPortletPreferences);
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
		SearchRelatedResultsPortletPreferences
			searchRelatedResultsPortletPreferences) {

		moreLikeThis.setMaxQueryTerms(
			searchRelatedResultsPortletPreferences.getMaxQueryTerms());
		moreLikeThis.setMinTermFrequency(
			searchRelatedResultsPortletPreferences.getMinTermFrequency());
		moreLikeThis.setMinDocFrequency(
			searchRelatedResultsPortletPreferences.getMinDocFrequency());
		moreLikeThis.setMaxDocFrequency(
			searchRelatedResultsPortletPreferences.getMaxDocFrequency());
		moreLikeThis.setMinWordLength(
			searchRelatedResultsPortletPreferences.getMinWordLength());
		moreLikeThis.setMaxWordLength(
			searchRelatedResultsPortletPreferences.getMaxWordLength());

		String stopWords =
			searchRelatedResultsPortletPreferences.getStopWords();

		if (!Validator.isBlank(stopWords)) {
			moreLikeThis.addStopWord(stopWords);
		}

		String analyzer = searchRelatedResultsPortletPreferences.getAnalyzer();

		if (!Validator.isBlank(analyzer)) {
			moreLikeThis.addStopWord(analyzer);
		}

		String minShouldMatch =
			searchRelatedResultsPortletPreferences.getMinShouldMatch();

		if (!Validator.isBlank(minShouldMatch)) {
			moreLikeThis.setMinShouldMatch(minShouldMatch);
		}

		Float termBoost = getFloatValue(
			searchRelatedResultsPortletPreferences.getTermBoost());

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
	private Queries _queries;

}