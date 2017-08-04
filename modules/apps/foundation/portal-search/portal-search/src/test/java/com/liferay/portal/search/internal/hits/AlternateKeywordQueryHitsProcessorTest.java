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
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class AlternateKeywordQueryHitsProcessorTest {

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

		Mockito.when(
			facetedSearcherManager.createFacetedSearcher()
		).thenReturn(
			facetedSearcher
		);

		Mockito.when(
			facetedSearcher.search((SearchContext)Matchers.any())
		).thenReturn(
			Mockito.mock(Hits.class)
		);

		AlternateKeywordQueryHitsProcessor comp =
			new AlternateKeywordQueryHitsProcessor();

		comp.indexSearcherHelper = indexSearcherHelper;
		comp.facetedSearcherManager = facetedSearcherManager;

		SearchContext searchContext = new SearchContext();

		Hits hits = new HitsImpl();

		//set hits length to be greater than 0; process function return true
		hits.setLength(1);

		boolean actualRet = comp.process(searchContext, hits);

		Assert.assertTrue(actualRet);

		//set hits length back to be 0 and set hits.spellCheckResults to be null
		hits.setLength(0);
		hits.setSpellCheckResults(null);

		actualRet = comp.process(searchContext, hits);

		Assert.assertTrue(actualRet);

		//set hits.spellCheckResults to be not null
		Map<String, List<String>> spellCheckResults = new HashMap<>();

		hits.setSpellCheckResults(spellCheckResults);

		actualRet = comp.process(searchContext, hits);

		Assert.assertTrue(actualRet);
	}

	@Mock
	protected FacetedSearcher facetedSearcher;

	@Mock
	protected FacetedSearcherManager facetedSearcherManager;

	@Mock
	protected IndexSearcherHelper indexSearcherHelper;

}