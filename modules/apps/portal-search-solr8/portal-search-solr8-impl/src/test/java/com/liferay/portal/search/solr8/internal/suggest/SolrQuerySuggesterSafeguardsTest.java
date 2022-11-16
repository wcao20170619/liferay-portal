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

package com.liferay.portal.search.solr8.internal.suggest;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.solr8.internal.SolrQuerySuggester;
import com.liferay.portal.search.solr8.internal.connection.SolrClientManager;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class SolrQuerySuggesterSafeguardsTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testErrorReturnsEmptyResults() throws Exception {
		SolrQuerySuggester solrQuerySuggester = createSolrQuerySuggester();

		String[] querySuggestions = solrQuerySuggester.suggestKeywordQueries(
			createSearchContext(), 0);

		Assert.assertEquals(
			Arrays.toString(querySuggestions), 0, querySuggestions.length);
	}

	protected SearchContext createSearchContext() {
		return new SearchContext() {
			{
				setKeywords(RandomTestUtil.randomString());
			}
		};
	}

	protected SolrQuerySuggester createSolrQuerySuggester() {
		SolrQuerySuggester solrQuerySuggester = new SolrQuerySuggester();

		ReflectionTestUtil.setFieldValue(
			solrQuerySuggester, "_solrClientManager",
			Mockito.mock(SolrClientManager.class));

		return solrQuerySuggester;
	}

}