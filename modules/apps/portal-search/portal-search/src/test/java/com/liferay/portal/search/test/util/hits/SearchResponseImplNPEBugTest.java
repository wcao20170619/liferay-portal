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

package com.liferay.portal.search.test.util.hits;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.search.ExpandoQueryContributor;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.HitsImpl;
import com.liferay.portal.kernel.search.IndexSearcherHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.internal.hits.SearchHitsBuilderFactoryImpl;
import com.liferay.portal.search.internal.hits.SearchHitsImpl;
import com.liferay.portal.search.internal.indexer.PreFilterContributorHelper;
import com.liferay.portal.search.internal.legacy.searcher.SearchRequestBuilderFactoryImpl;
import com.liferay.portal.search.internal.legacy.searcher.SearchResponseBuilderFactoryImpl;
import com.liferay.portal.search.internal.query.QueriesImpl;
import com.liferay.portal.search.internal.searcher.FacetedSearcherImpl;
import com.liferay.portal.search.internal.searcher.SearcherImpl;
import com.liferay.portal.search.internal.test.util.DocumentFixture;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Wade Cao
 */
public class SearchResponseImplNPEBugTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		Registry registry = new BasicRegistryImpl();

		RegistryUtil.setRegistry(registry);
	}

	@AfterClass
	public static void tearDownClass() {
		RegistryUtil.setRegistry(null);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		_documentFixture = new DocumentFixture();

		_documentFixture.setUp();

		_queries = new QueriesImpl();

		_searcher = new SearcherImpl() {
			{
				facetedSearcherManager = _facetedSearcherManager;
				indexerRegistry = _indexerRegistry;
				searchResponseBuilderFactory =
					new SearchResponseBuilderFactoryImpl();
				searchHitsBuilderFactory = new SearchHitsBuilderFactoryImpl();
			}
		};

		_searchRequestBuilderFactory = new SearchRequestBuilderFactoryImpl();

		Mockito.doReturn(
			_createFacetedSearcher()
		).when(
			_facetedSearcherManager
		).createFacetedSearcher();

		Mockito.doReturn(
			_indexer
		).when(
			_indexerRegistry
		).getIndexer(
			Mockito.<Class<?>>any()
		);
	}

	@Test
	public void testMultiIndexerSearch() {
		SearchContext searchContext = new SearchContext();

		SearchHits searchHits = _getSearchHitsByModelIndexerClasses(
			searchContext);

		Assert.assertNotNull(searchHits);
	}

	@Test
	public void testSingleIndexerSearch() throws Exception {
		SearchContext searchContext = new SearchContext();

		Mockito.doReturn(
			_getHits(searchContext)
		).when(
			_indexer
		).search(
			Mockito.anyObject()
		);

		SearchHits searchHits = _getSearchHitsByModelIndexerClasses(
			searchContext, AssetEntry.class);

		Assert.assertNotNull(searchHits);
	}

	private FacetedSearcherImpl _createFacetedSearcher() {
		return new FacetedSearcherImpl(
			_expandoQueryContributor, _indexerRegistry, _indexSearcherHelper,
			_preFilterContributorHelper, _searchEngineHelper);
	}

	private Hits _getHits(SearchContext searchContext) {
		searchContext.setAttribute(
			"search.response.search.hits", _searchHitsImpl);

		return new HitsImpl();
	}

	private SearchHits _getSearchHitsByModelIndexerClasses(
		SearchContext searchContext, Class<?>... classes) {

		long companyId = 1234;

		BooleanQuery booleanQuery = _queries.booleanQuery();

		booleanQuery.addMustQueryClauses(
			_queries.term(Field.COMPANY_ID, companyId));

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.getSearchRequestBuilder(searchContext);

		SearchRequest searchRequest = null;

		if (classes == null) {
			searchRequest = searchRequestBuilder.entryClassNames(
				AssetEntry.class.getName()
			).query(
				booleanQuery
			).withSearchContext(
				sc -> sc.setCompanyId(companyId)
			).build();
		}
		else {
			searchRequest = searchRequestBuilder.entryClassNames(
				AssetEntry.class.getName()
			).modelIndexerClasses(
				classes
			).query(
				booleanQuery
			).withSearchContext(
				sc -> sc.setCompanyId(companyId)
			).build();
		}

		SearchResponse searchResponse = _searcher.search(searchRequest);

		return searchResponse.getSearchHits();
	}

	private DocumentFixture _documentFixture;

	@Mock
	private ExpandoQueryContributor _expandoQueryContributor;

	@Mock
	private FacetedSearcherManager _facetedSearcherManager;

	private final Indexer<?> _indexer = Mockito.mock(Indexer.class);

	@Mock
	private IndexerRegistry _indexerRegistry;

	@Mock
	private IndexSearcherHelper _indexSearcherHelper;

	@Mock
	private PreFilterContributorHelper _preFilterContributorHelper;

	private Queries _queries;

	@Mock
	private SearchEngineHelper _searchEngineHelper;

	private Searcher _searcher;

	@Mock
	private SearchHitsImpl _searchHitsImpl;

	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}