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

package com.liferay.portal.search.facet.internal.faceted.searcher;

import com.liferay.expando.kernel.util.ExpandoBridgeFactory;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcherHelper;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.search.SearchPermissionChecker;
import com.liferay.portal.kernel.search.hits.HitsProcessorRegistryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.search.permission.SearchPermissionFilterContributor;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.RegistryUtil;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Wade Cao
 */
@PrepareForTest(HitsProcessorRegistryUtil.class)
@RunWith(PowerMockRunner.class)
public class FacetedSearcherImplTest extends PowerMockito {

	@Before
	public void setUp() throws Exception {
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		_expandoBridgeFactory = mock(ExpandoBridgeFactory.class);
		_groupLocalService = mock(GroupLocalService.class);
		_indexerRegistry = mock(IndexerRegistry.class);
		_indexSearcherHelper = mock(IndexSearcherHelper.class);
		_searchEngineHelper = mock(SearchEngineHelper.class);

		_searchPermissionChecker = mock(SearchPermissionChecker.class);

		_searchPermissionFilterContributors = new ArrayList<>(1);

		_searchPermissionFilterContributors.add(
			mock(SearchPermissionFilterContributor.class));

		Props props = mock(Props.class);

		PropsUtil.setProps(props);

		PowerMockito.mockStatic(HitsProcessorRegistryUtil.class);

		PowerMockito.when(
			HitsProcessorRegistryUtil.process(
				Mockito.any(SearchContext.class), Mockito.any(Hits.class))
		).thenReturn(
			true
		);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testSearch() throws Exception {
		FacetedSearcherImpl facetedSearcher = new FacetedSearcherImpl(
			_expandoBridgeFactory, _groupLocalService, _indexerRegistry,
			_indexSearcherHelper, _searchEngineHelper,
			_searchPermissionFilterContributors, _searchPermissionChecker);

		Hits hits = mock(Hits.class);

		//set fake length
		hits.setLength(321);

		when(
			_indexSearcherHelper.search(
				Mockito.any(SearchContext.class), Mockito.any(Query.class))
		).thenReturn(
			hits
		);

		String[] entryClassNames = {"myclass"};

		when(
			_searchEngineHelper.getEntryClassNames()
		).thenReturn(
			entryClassNames
		);

		SearchContext searchContext = new SearchContext();

		Hits myHits = facetedSearcher.search(searchContext);

		Assert.assertEquals(
			hits.toString(), hits.getLength(), myHits.getLength());
	}

	private ExpandoBridgeFactory _expandoBridgeFactory;
	private GroupLocalService _groupLocalService;
	private IndexerRegistry _indexerRegistry;
	private IndexSearcherHelper _indexSearcherHelper;
	private SearchEngineHelper _searchEngineHelper;
	private SearchPermissionChecker _searchPermissionChecker;
	private Collection<SearchPermissionFilterContributor>
		_searchPermissionFilterContributors;

}