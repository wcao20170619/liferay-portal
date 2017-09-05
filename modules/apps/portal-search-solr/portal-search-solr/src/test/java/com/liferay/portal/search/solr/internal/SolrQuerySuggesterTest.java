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

package com.liferay.portal.search.solr.internal;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.solr.connection.SolrClientManager;

import java.util.Arrays;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class SolrQuerySuggesterTest {

	@Test
	public void testErrorReturnsEmptyResults() throws Exception {
		SolrQuerySuggester solrQuerySuggester = createSolrQuerySuggester();

		String[] querySuggestions = solrQuerySuggester.suggestKeywordQueries(
			createSearchContext(), 0);

		Assert.assertEquals(
			Arrays.toString(querySuggestions), 0, querySuggestions.length);
	}

	@Test
	public void testGetValidKeyword() throws Exception {
		//return null for three characters with white spaces
		String keyword = SolrQuerySuggester.getValidKeyword("A  B");

		Assert.assertNull(keyword);
		//return string with trimmed value
		keyword = SolrQuerySuggester.getValidKeyword(
			" I have been trimmed.   ");

		Assert.assertEquals("I have been trimmed.", keyword);
		//test embedded multiple white spaces
		keyword = SolrQuerySuggester.getValidKeyword(
			"I have    multiple    white spaces.");

		Assert.assertEquals("I have multiple white spaces.", keyword);
	}

	protected SearchContext createSearchContext() {
		return new SearchContext() {
			{
				setKeywords(RandomTestUtil.randomString());
			}
		};
	}

	protected SolrQuerySuggester createSolrQuerySuggester() throws Exception {
		QueryResponse queryResponse = Mockito.mock(QueryResponse.class);

		Mockito.when(
			queryResponse.getResults()
		).thenReturn(
			Mockito.mock(SolrDocumentList.class)
		);

		SolrClient solrClient = Mockito.mock(SolrClient.class);

		Mockito.when(
			solrClient.query(Mockito.any(SolrParams.class))
		).thenReturn(
			queryResponse
		);

		SolrClientManager solrClientManager = Mockito.mock(
			SolrClientManager.class);

		Mockito.when(
			solrClientManager.getSolrClient()
		).thenReturn(
			solrClient
		);

		return new SolrQuerySuggester() {
			{
				setSolrClientManager(solrClientManager);
			}
		};
	}

}