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

package com.liferay.portal.search.test.util.query;

import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.FuzzyQuery;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Wade Cao
 */
public abstract class BaseFuzzyQueryTestCase extends BaseIndexingTestCase {

	@Test
	public void testFuzzyQuery() {
		addDocuments("eclipse", "java", "liferay", "language");

		assertSearch("eclipsee", Arrays.asList("eclipse"));
	}

	protected void addDocuments(String... values) {
		addDocuments(
			value -> DocumentCreationHelpers.singleText(_FIELD_NAME, value),
			Arrays.asList(values));
	}

	protected void assertSearch(String value, List<String> expectedValues) {
		assertSearch(
			indexingTestHelper -> {
				FuzzyQuery fuzzyQuery = queries.fuzzy(_FIELD_NAME, value);

				SearchSearchRequest searchSearchRequest =
					new SearchSearchRequest();

				searchSearchRequest.setIndexNames(
					String.valueOf(getCompanyId()));
				searchSearchRequest.setQuery(fuzzyQuery);

				SearchEngineAdapter searchEngineAdapter =
					getSearchEngineAdapter();

				SearchSearchResponse searchSearchResponse =
					searchEngineAdapter.execute(searchSearchRequest);

				SearchHits searchHits = searchSearchResponse.getSearchHits();

				Assert.assertEquals(
					"Total hits", expectedValues.size(),
					searchHits.getTotalHits());

				List<SearchHit> searchHitsList = searchHits.getSearchHits();

				Assert.assertEquals(
					"Retrieved hits", expectedValues.size(),
					searchHitsList.size());

				List<String> actualValues = new ArrayList<>();

				searchHitsList.forEach(
					searchHit -> {
						Document document = searchHit.getDocument();

						actualValues.add(document.getString(_FIELD_NAME));
					});

				Assert.assertEquals(
					"Retrieved hits ->" + actualValues,
					expectedValues.toString(), actualValues.toString());
			});
	}

	private static final String _FIELD_NAME = "title";

}