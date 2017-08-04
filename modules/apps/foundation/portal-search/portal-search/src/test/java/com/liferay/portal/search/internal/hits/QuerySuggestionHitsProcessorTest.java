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

package com.liferay.portal.search.internal.hits;

import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.HitsImpl;
import com.liferay.portal.kernel.search.IndexSearcherHelper;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Wade Cao
 */
@RunWith(PowerMockRunner.class)
public class QuerySuggestionHitsProcessorTest {

	@Before
	public void setUp() throws Exception {
		PropsUtil.setProps(Mockito.mock(Props.class));
	}

	@Test
	public void testProcess() throws Exception {
		Mockito.when(
			indexSearcherHelper.suggestKeywordQueries(
				(SearchContext)Matchers.any(), Matchers.anyInt())
		).thenReturn(
			new String[] {"a", "b"}
		);

		QuerySuggestionHitsProcessor comp = new QuerySuggestionHitsProcessor();

		comp.indexSearcherHelper = indexSearcherHelper;

		SearchContext searchContext = new SearchContext();

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setCollatedSpellCheckResultEnabled(false);

		Hits hits = new HitsImpl();

		//!queryConfig.isCollatedSpellCheckResultEnabled()
		boolean actualRet = comp.process(searchContext, hits);

		Assert.assertTrue(actualRet);

		//queryConfig setup
		queryConfig.setCollatedSpellCheckResultEnabled(true);
		hits.setLength(
			queryConfig.getCollatedSpellCheckResultScoresThreshold() + 1);

		actualRet = comp.process(searchContext, hits);

		Assert.assertTrue(actualRet);

		hits.setLength(1);
		searchContext.setKeywords("abc");
		actualRet = comp.process(searchContext, hits);

		Assert.assertTrue(actualRet);
	}

	@Mock
	protected IndexSearcherHelper indexSearcherHelper;

}