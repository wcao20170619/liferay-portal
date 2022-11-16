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

package com.liferay.portal.search.solr8.internal.search.engine.adapter.search;

import com.liferay.portal.kernel.search.query.QueryTranslator;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.engine.adapter.search.SearchRequestExecutor;
import com.liferay.portal.search.internal.groupby.GroupByResponseFactoryImpl;
import com.liferay.portal.search.internal.hits.SearchHitBuilderFactoryImpl;
import com.liferay.portal.search.internal.hits.SearchHitsBuilderFactoryImpl;
import com.liferay.portal.search.internal.legacy.document.DocumentBuilderFactoryImpl;
import com.liferay.portal.search.internal.legacy.groupby.GroupByRequestFactoryImpl;
import com.liferay.portal.search.internal.legacy.stats.StatsRequestBuilderFactoryImpl;
import com.liferay.portal.search.internal.legacy.stats.StatsResultsTranslatorImpl;
import com.liferay.portal.search.internal.stats.StatsResponseBuilderFactoryImpl;
import com.liferay.portal.search.solr8.internal.connection.SolrClientManager;
import com.liferay.portal.search.solr8.internal.facet.FacetProcessor;
import com.liferay.portal.search.solr8.internal.filter.BooleanFilterTranslatorImpl;
import com.liferay.portal.search.solr8.internal.filter.DateRangeFilterTranslatorImpl;
import com.liferay.portal.search.solr8.internal.filter.DateRangeTermFilterTranslatorImpl;
import com.liferay.portal.search.solr8.internal.filter.ExistsFilterTranslatorImpl;
import com.liferay.portal.search.solr8.internal.filter.GeoBoundingBoxFilterTranslatorImpl;
import com.liferay.portal.search.solr8.internal.filter.GeoDistanceFilterTranslatorImpl;
import com.liferay.portal.search.solr8.internal.filter.GeoDistanceRangeFilterTranslatorImpl;
import com.liferay.portal.search.solr8.internal.filter.GeoPolygonFilterTranslatorImpl;
import com.liferay.portal.search.solr8.internal.filter.MissingFilterTranslatorImpl;
import com.liferay.portal.search.solr8.internal.filter.PrefixFilterTranslatorImpl;
import com.liferay.portal.search.solr8.internal.filter.QueryFilterTranslatorImpl;
import com.liferay.portal.search.solr8.internal.filter.RangeTermFilterTranslatorImpl;
import com.liferay.portal.search.solr8.internal.filter.SolrFilterTranslator;
import com.liferay.portal.search.solr8.internal.filter.TermFilterTranslatorImpl;
import com.liferay.portal.search.solr8.internal.filter.TermsFilterTranslatorImpl;
import com.liferay.portal.search.solr8.internal.groupby.DefaultGroupByTranslator;
import com.liferay.portal.search.solr8.internal.search.response.DefaultSearchSearchResponseAssemblerHelperImpl;
import com.liferay.portal.search.solr8.internal.search.response.SearchSearchResponseAssemblerHelper;
import com.liferay.portal.search.solr8.internal.sort.SolrSortFieldTranslator;
import com.liferay.portal.search.solr8.internal.stats.DefaultStatsTranslator;
import com.liferay.portal.search.solr8.internal.stats.StatsTranslator;

import org.apache.solr.client.solrj.SolrQuery;

/**
 * @author Bryan Engler
 */
public class SearchRequestExecutorFixture {

	public SearchRequestExecutor getSearchRequestExecutor() {
		return _searchRequestExecutor;
	}

	public void setUp() {
		_searchRequestExecutor = createSearchRequestExecutor(
			_solrClientManager, _facetProcessor, _queryTranslator);
	}

	protected static BaseSearchResponseAssembler
		createBaseSearchResponseAssembler() {

		BaseSearchResponseAssemblerImpl baseSearchResponseAssemblerImpl =
			new BaseSearchResponseAssemblerImpl();

		ReflectionTestUtil.setFieldValue(
			baseSearchResponseAssemblerImpl, "_statsTranslator",
			createStatsTranslator());

		return baseSearchResponseAssemblerImpl;
	}

	protected static BaseSolrQueryAssembler createBaseSolrQueryAssembler(
		FacetProcessor<SolrQuery> facetProcessor,
		QueryTranslator<String> queryTranslator) {

		BaseSolrQueryAssemblerImpl baseSolrQueryAssemblerImpl =
			new BaseSolrQueryAssemblerImpl();

		ReflectionTestUtil.setFieldValue(
			baseSolrQueryAssemblerImpl, "_queryTranslator", queryTranslator);
		ReflectionTestUtil.setFieldValue(
			baseSolrQueryAssemblerImpl, "_statsTranslator",
			createStatsTranslator());
		ReflectionTestUtil.setFieldValue(
			baseSolrQueryAssemblerImpl, "_filterTranslator",
			createSolrFilterTranslator());
		ReflectionTestUtil.setFieldValue(
			baseSolrQueryAssemblerImpl, "_facetProcessor", facetProcessor);

		return baseSolrQueryAssemblerImpl;
	}

	protected static CountSearchRequestExecutor
		createCountSearchRequestExecutor(
			SolrClientManager solrClientManager,
			FacetProcessor<SolrQuery> facetProcessor,
			QueryTranslator<String> queryTranslator) {

		CountSearchRequestExecutorImpl countSearchRequestExecutorImpl =
			new CountSearchRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			countSearchRequestExecutorImpl, "_baseSearchResponseAssembler",
			createBaseSearchResponseAssembler());
		ReflectionTestUtil.setFieldValue(
			countSearchRequestExecutorImpl, "_baseSolrQueryAssembler",
			createBaseSolrQueryAssembler(facetProcessor, queryTranslator));
		ReflectionTestUtil.setFieldValue(
			countSearchRequestExecutorImpl, "_solrClientManager",
			solrClientManager);

		return countSearchRequestExecutorImpl;
	}

	protected static SearchRequestExecutor createSearchRequestExecutor(
		SolrClientManager solrClientManager,
		FacetProcessor<SolrQuery> facetProcessor,
		QueryTranslator<String> queryTranslator) {

		SolrSearchRequestExecutor solrSearchRequestExecutor =
			new SolrSearchRequestExecutor();

		ReflectionTestUtil.setFieldValue(
			solrSearchRequestExecutor, "_countSearchRequestExecutor",
			createCountSearchRequestExecutor(
				solrClientManager, facetProcessor, queryTranslator));
		ReflectionTestUtil.setFieldValue(
			solrSearchRequestExecutor, "_multisearchSearchRequestExecutor",
			new MultisearchSearchRequestExecutorImpl());
		ReflectionTestUtil.setFieldValue(
			solrSearchRequestExecutor, "_searchSearchRequestExecutor",
			createSearchSearchRequestExecutor(
				solrClientManager, facetProcessor, queryTranslator));

		return solrSearchRequestExecutor;
	}

	protected static SearchSearchRequestExecutor
		createSearchSearchRequestExecutor(
			SolrClientManager solrClientManager,
			FacetProcessor<SolrQuery> facetProcessor,
			QueryTranslator<String> queryTranslator) {

		SearchSearchRequestExecutorImpl searchSearchRequestExecutorImpl =
			new SearchSearchRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			searchSearchRequestExecutorImpl, "_searchSearchResponseAssembler",
			createSearchSearchResponseAssembler());
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestExecutorImpl, "_searchSolrQueryAssembler",
			createSearchSolrQueryAssembler(facetProcessor, queryTranslator));
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestExecutorImpl, "_solrClientManager",
			solrClientManager);

		return searchSearchRequestExecutorImpl;
	}

	protected static SearchSearchResponseAssembler
		createSearchSearchResponseAssembler() {

		SearchSearchResponseAssemblerImpl searchSearchResponseAssemblerImpl =
			new SearchSearchResponseAssemblerImpl();

		ReflectionTestUtil.setFieldValue(
			searchSearchResponseAssemblerImpl, "_baseSearchResponseAssembler",
			createBaseSearchResponseAssembler());
		ReflectionTestUtil.setFieldValue(
			searchSearchResponseAssemblerImpl,
			"_searchSearchResponseAssemblerHelper",
			createSearchSearchResponseAssemblerHelper());

		return searchSearchResponseAssemblerImpl;
	}

	protected static SearchSearchResponseAssemblerHelper
		createSearchSearchResponseAssemblerHelper() {

		DefaultSearchSearchResponseAssemblerHelperImpl
			defaultSearchSearchResponseAssemblerHelperImpl =
				new DefaultSearchSearchResponseAssemblerHelperImpl();

		ReflectionTestUtil.setFieldValue(
			defaultSearchSearchResponseAssemblerHelperImpl,
			"_documentBuilderFactory", new DocumentBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			defaultSearchSearchResponseAssemblerHelperImpl,
			"_groupByResponseFactory", new GroupByResponseFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			defaultSearchSearchResponseAssemblerHelperImpl,
			"_searchHitBuilderFactory", new SearchHitBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			defaultSearchSearchResponseAssemblerHelperImpl,
			"_searchHitsBuilderFactory", new SearchHitsBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			defaultSearchSearchResponseAssemblerHelperImpl, "_statsTranslator",
			createStatsTranslator());
		ReflectionTestUtil.setFieldValue(
			defaultSearchSearchResponseAssemblerHelperImpl,
			"_statsResultsTranslator", new StatsResultsTranslatorImpl());

		return defaultSearchSearchResponseAssemblerHelperImpl;
	}

	protected static SearchSolrQueryAssembler createSearchSolrQueryAssembler(
		FacetProcessor<SolrQuery> facetProcessor,
		QueryTranslator<String> queryTranslator) {

		SearchSolrQueryAssemblerImpl searchSolrQueryAssemblerImpl =
			new SearchSolrQueryAssemblerImpl();

		ReflectionTestUtil.setFieldValue(
			searchSolrQueryAssemblerImpl, "_baseSolrQueryAssembler",
			createBaseSolrQueryAssembler(facetProcessor, queryTranslator));
		ReflectionTestUtil.setFieldValue(
			searchSolrQueryAssemblerImpl, "_groupByRequestFactory",
			new GroupByRequestFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			searchSolrQueryAssemblerImpl, "_groupByTranslator",
			new DefaultGroupByTranslator());
		ReflectionTestUtil.setFieldValue(
			searchSolrQueryAssemblerImpl, "_sortFieldTranslator",
			new SolrSortFieldTranslator());
		ReflectionTestUtil.setFieldValue(
			searchSolrQueryAssemblerImpl, "_statsRequestBuilderFactory",
			new StatsRequestBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			searchSolrQueryAssemblerImpl, "_statsTranslator",
			createStatsTranslator());

		return searchSolrQueryAssemblerImpl;
	}

	protected static SolrFilterTranslator createSolrFilterTranslator() {
		SolrFilterTranslator solrFilterTranslator = new SolrFilterTranslator();

		ReflectionTestUtil.setFieldValue(
			solrFilterTranslator, "_booleanFilterTranslator",
			new BooleanFilterTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			solrFilterTranslator, "_dateRangeFilterTranslator",
			new DateRangeFilterTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			solrFilterTranslator, "_dateRangeTermFilterTranslator",
			new DateRangeTermFilterTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			solrFilterTranslator, "_existsFilterTranslator",
			new ExistsFilterTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			solrFilterTranslator, "_geoBoundingBoxFilterTranslator",
			new GeoBoundingBoxFilterTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			solrFilterTranslator, "_geoDistanceFilterTranslator",
			new GeoDistanceFilterTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			solrFilterTranslator, "_geoDistanceRangeFilterTranslator",
			new GeoDistanceRangeFilterTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			solrFilterTranslator, "_geoPolygonFilterTranslator",
			new GeoPolygonFilterTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			solrFilterTranslator, "_missingFilterTranslator",
			new MissingFilterTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			solrFilterTranslator, "_prefixFilterTranslator",
			new PrefixFilterTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			solrFilterTranslator, "_queryFilterTranslator",
			new QueryFilterTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			solrFilterTranslator, "_rangeTermFilterTranslator",
			new RangeTermFilterTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			solrFilterTranslator, "_termFilterTranslator",
			new TermFilterTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			solrFilterTranslator, "_termsFilterTranslator",
			new TermsFilterTranslatorImpl());

		return solrFilterTranslator;
	}

	protected static StatsTranslator createStatsTranslator() {
		DefaultStatsTranslator defaultStatsTranslator =
			new DefaultStatsTranslator();

		ReflectionTestUtil.setFieldValue(
			defaultStatsTranslator, "_statsResponseBuilderFactory",
			new StatsResponseBuilderFactoryImpl());

		return defaultStatsTranslator;
	}

	protected void setFacetProcessor(FacetProcessor<SolrQuery> facetProcessor) {
		_facetProcessor = facetProcessor;
	}

	protected void setQueryTranslator(QueryTranslator<String> queryTranslator) {
		_queryTranslator = queryTranslator;
	}

	protected void setSolrClientManager(SolrClientManager solrClientManager) {
		_solrClientManager = solrClientManager;
	}

	private FacetProcessor<SolrQuery> _facetProcessor;
	private QueryTranslator<String> _queryTranslator;
	private SearchRequestExecutor _searchRequestExecutor;
	private SolrClientManager _solrClientManager;

}