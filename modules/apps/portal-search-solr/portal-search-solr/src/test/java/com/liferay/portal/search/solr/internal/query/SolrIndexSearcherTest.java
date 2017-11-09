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

package com.liferay.portal.search.solr.internal.query;

import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.generic.StringQuery;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.search.solr.internal.SolrIndexingFixture;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelpers;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.util.LocalizationImpl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * @author Wade Cao
 */
public class SolrIndexSearcherTest extends BaseIndexingTestCase {

	@Override
	public void setUp() throws Exception {
		super.setUp();

		LocalizationUtil localizationUtil = new LocalizationUtil();

		localizationUtil.setLocalization(new LocalizationImpl());
	}

	@Test
	public void testSearchResultWithEllipses() throws Exception {
		addDocuments("alpha");

		assertSearch(
			"title:alpha", Arrays.asList("<liferay-hl>alpha</liferay-hl>..."));
	}

	protected void addDocuments(String... fieldValues) throws Exception {
		for (String fieldValue : fieldValues) {
			addDocument(
				DocumentCreationHelpers.singleText(_FIELD_NAME, fieldValue));
		}
	}

	protected void assertSearch(String query, List<String> expectedValues)
		throws Exception {

		SearchContext searchContext = createSearchContext();

		searchContext.getQueryConfig().setHighlightEnabled(true);

		searchContext.getQueryConfig().addHighlightFieldNames(_FIELD_NAME);

		searchContext.getQueryConfig().addSelectedFieldNames(_FIELD_NAME);

		searchContext.getQueryConfig().setHighlightRequireFieldMatch(true);

		StringQuery stringQuery = _createStringQuery(query, searchContext);

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS,
			() -> {
				Hits hits = search(searchContext, stringQuery);

				DocumentsAssert.assertValues(
					query, hits.getDocs(), "snippet_title", expectedValues);

				return null;
			});
	}

	@Override
	protected IndexingFixture createIndexingFixture() throws Exception {
		return new SolrIndexingFixture();
	}

	private StringQuery _createStringQuery(
		String query, SearchContext searchContext) {

		StringQuery stringQuery = new StringQuery(query);

		searchContext.getQueryConfig().addHighlightFieldNames(_FIELD_NAME);

		stringQuery.setQueryConfig(searchContext.getQueryConfig());

		return stringQuery;
	}

	private static final String _FIELD_NAME = "title";

}