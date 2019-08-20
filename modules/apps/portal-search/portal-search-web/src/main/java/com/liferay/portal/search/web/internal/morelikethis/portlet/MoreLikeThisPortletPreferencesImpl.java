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

package com.liferay.portal.search.web.internal.morelikethis.portlet;

import com.liferay.portal.search.web.internal.util.PortletPreferencesHelper;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author Wade Cao
 */
public class MoreLikeThisPortletPreferencesImpl
	implements MoreLikeThisPortletPreferences {

	public MoreLikeThisPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferencesOptional) {

		_portletPreferencesHelper = new PortletPreferencesHelper(
			portletPreferencesOptional);
	}

	@Override
	public String getAnalyzer() {
		return _portletPreferencesHelper.getString(
			PREFERENCE_KEY_ANALYZER, null);
	}

	@Override
	public String getDocType() {
		return _portletPreferencesHelper.getString(
			PREFERENCE_KEY_DOC_TYPE, null);
	}

	@Override
	public Optional<String> getFederatedSearchKeyOptional() {
		return Optional.of(MORE_LIKE_THIS);
	}

	@Override
	public String getFederatedSearchKeyString() {
		return getFederatedSearchKeyOptional().orElse(MORE_LIKE_THIS);
	}

	@Override
	public String getFields() {
		return _portletPreferencesHelper.getString(
			PREFERENCE_KEY_FIELDS, "title,description,content");
	}

	@Override
	public String getIndexName() {
		return _portletPreferencesHelper.getString(
			PREFERENCE_KEY_INDEX_NAME, null);
	}

	@Override
	public int getMaxDocFrequency() {
		return _portletPreferencesHelper.getInteger(
			PREFERENCE_KEY_MAX_DOC_FREQUENCY, 0);
	}

	@Override
	public int getMaxQueryTerms() {
		return _portletPreferencesHelper.getInteger(
			PREFERENCE_KEY_MAX_QUERY_TERMS, 25);
	}

	@Override
	public int getMaxWordLength() {
		return _portletPreferencesHelper.getInteger(
			PREFERENCE_KEY_MAX_WORD_LENGTH, 0);
	}

	@Override
	public int getMinDocFrequency() {
		return _portletPreferencesHelper.getInteger(
			PREFERENCE_KEY_MIN_DOC_FREQUENCY, 5);
	}

	@Override
	public String getMinShouldMatch() {
		return _portletPreferencesHelper.getString(
			PREFERENCE_KEY_MIN_SHOULD_MATCH, "30%");
	}

	@Override
	public int getMinTermFrequency() {
		return _portletPreferencesHelper.getInteger(
			PREFERENCE_KEY_MIN_TERM_FREQUENCY, 2);
	}

	@Override
	public int getMinWordLength() {
		return _portletPreferencesHelper.getInteger(
			PREFERENCE_KEY_MIN_WORD_LENGTH, 0);
	}

	@Override
	public String getStopWords() {
		return _portletPreferencesHelper.getString(
			PREFERENCE_KEY_STOP_WORDS, null);
	}

	@Override
	public String getTermBoost() {
		return _portletPreferencesHelper.getString(
			PREFERENCE_KEY_TERM_BOOST, "0");
	}

	private final PortletPreferencesHelper _portletPreferencesHelper;

}