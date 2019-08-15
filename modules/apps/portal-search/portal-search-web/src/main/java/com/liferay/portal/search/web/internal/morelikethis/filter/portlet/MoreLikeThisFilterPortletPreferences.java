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

package com.liferay.portal.search.web.internal.morelikethis.filter.portlet;

import java.util.Optional;

/**
 * @author Wade Cao
 */
public interface MoreLikeThisFilterPortletPreferences {

	public static final String MORE_LIKE_THIS = "morelikethis";

	public static final String PREFERENCE_KEY_ANALYZER = "analyzer";

	public static final String PREFERENCE_KEY_DOC_TYPE = "docType";

	public static final String PREFERENCE_KEY_FIELDS = "fields";

	public static final String PREFERENCE_KEY_INDEX_NAME = "indexName";

	public static final String PREFERENCE_KEY_MAX_DOC_FREQUENCY =
		"maxDocFrequency";

	public static final String PREFERENCE_KEY_MAX_QUERY_TERMS = "maxQueryTerms";

	public static final String PREFERENCE_KEY_MAX_WORD_LENGTH = "maxWordLength";

	public static final String PREFERENCE_KEY_MIN_DOC_FREQUENCY =
		"minDocFrequency";

	public static final String PREFERENCE_KEY_MIN_SHOULD_MATCH =
		"minShouldMatch";

	public static final String PREFERENCE_KEY_MIN_TERM_FREQUENCY =
		"minTermFrequency";

	public static final String PREFERENCE_KEY_MIN_WORD_LENGTH = "minWordLength";

	public static final String PREFERENCE_KEY_STOP_WORDS = "stopWords";

	public static final String PREFERENCE_KEY_TERM_BOOST = "termBoost";

	public String getAnalyzer();

	public String getDocType();

	public Optional<String> getFederatedSearchKeyOptional();

	public String getFederatedSearchKeyString();

	public String getFields();

	public String getIndexName();

	public int getMaxDocFrequency();

	public int getMaxQueryTerms();

	public int getMaxWordLength();

	public int getMinDocFrequency();

	public String getMinShouldMatch();

	public int getMinTermFrequency();

	public int getMinWordLength();

	public String getStopWords();

	public String getTermBoost();

}