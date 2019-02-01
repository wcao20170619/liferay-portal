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

package com.liferay.portal.search.test.util.query2;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search2.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search2.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.TermsSetQuery;
import com.liferay.portal.search.script.Script;
import com.liferay.portal.search.sort.FieldSort;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael C. Han
 */
public abstract class BaseTermsSetQueryTestCase extends BaseIndexingTestCase {

	@Override
	public void setUp() throws Exception {
		super.setUp();

		index(2, "SomeUser1", "SomeUser2");
		index(3, "SomeUser1", "SomeUser2", "SomeUser3", "SomeUser4");
		index(2, "SomeUser2", "SomeUser3", "SomeUser4", "SomeUser5");
		index(3, "SomeUser3", "SomeUser4", "SomeUser5", "SomeUser6");
		index(2, "SomeUser6", "SomeUser7", "SomeUser8", "SomeUser9");
	}

	@Test
	public void testTermsSetQueryWithField() {
		SearchSearchRequest searchRequest = new SearchSearchRequest() {
			{
				addSorts(
					new FieldSort(Field.UID) {
						{
							setSortOrder(SortOrder.ASC);
						}
					});

				setIndexNames("_all");

				setQuery(
					new TermsSetQuery(
						Field.USER_NAME,
						Arrays.asList("SomeUser2", "SomeUser3", "SomeUser4")) {

						{
							setMinimumShouldMatchField(Field.PRIORITY);
						}

					});
			}
		};

		List<List<String>> expected = Arrays.asList(
			Arrays.asList("SomeUser1", "SomeUser2", "SomeUser3", "SomeUser4"),
			Arrays.asList("SomeUser2", "SomeUser3", "SomeUser4", "SomeUser5"));

		assertSearch(searchRequest, Field.USER_NAME, expected);
	}

	@Test
	public void testTermsSetQueryWithScript() {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest() {
			{
				addSorts(
					new FieldSort(Field.UID) {
						{
							setSortOrder(SortOrder.ASC);
						}
					});

				setIndexNames("_all");

				Script script = new Script(
					"painless",
					"Math.min(params.num_terms, doc['priority'].value)");

				setQuery(
					new TermsSetQuery(
						Field.USER_NAME,
						Arrays.asList("SomeUser2", "SomeUser3", "SomeUser4")) {

						{
							setMinimumShouldMatchScript(script);
						}

					});
			}
		};

		List<List<String>> expected = Arrays.asList(
			Arrays.asList("SomeUser1", "SomeUser2", "SomeUser3", "SomeUser4"),
			Arrays.asList("SomeUser2", "SomeUser3", "SomeUser4", "SomeUser5"));

		assertSearch(searchSearchRequest, Field.USER_NAME, expected);
	}

	protected void assertSearch(
		SearchSearchRequest searchRequest, String fieldName,
		List<List<String>> expected) {

		assertSearch(
			indexingTestHelper -> {
				SearchEngineAdapter searchEngineAdapter =
					getSearchEngineAdapter();

				SearchSearchResponse searchSearchResponse =
					searchEngineAdapter.execute(searchRequest);

				SearchHits searchHits = searchSearchResponse.getSearchHits();

				List<SearchHit> searchHitList = searchHits.getSearchHits();

				Stream<SearchHit> stream = searchHitList.stream();

				Assert.assertEquals(
					String.valueOf(expected),
					String.valueOf(
						stream.map(
							searchHit -> getFieldValues(searchHit, fieldName)
						).collect(
							Collectors.toList()
						)));

				Assert.assertEquals(
					"Total hits", expected.size(), searchHits.getTotalHits());
			});
	}

	protected List<Object> getFieldValues(
		SearchHit searchHit, String fieldName) {

		Document document = searchHit.getDocument();

		return document.getFieldValues(fieldName);
	}

	protected SearchHit getSearchHit(List<SearchHit> searchHitList, int i2) {
		return searchHitList.get(i2);
	}

	protected void index(int priority, String... userNames) {
		addDocument(
			document -> {
				document.addKeyword(Field.USER_NAME, userNames);
				document.addNumber(Field.PRIORITY, priority);
			});
	}

}