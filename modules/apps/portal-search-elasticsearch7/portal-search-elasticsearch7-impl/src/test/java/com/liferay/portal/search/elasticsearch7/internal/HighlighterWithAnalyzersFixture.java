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

package com.liferay.portal.search.elasticsearch7.internal;

import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcher;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.connection.IndexCreationHelper;
import com.liferay.portal.search.elasticsearch7.internal.connection.IndexCreator;
import com.liferay.portal.search.elasticsearch7.internal.connection.IndexName;
import com.liferay.portal.search.elasticsearch7.internal.facet.DefaultFacetProcessor;
import com.liferay.portal.search.elasticsearch7.internal.facet.FacetProcessor;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.ElasticsearchEngineAdapterFixture;
import com.liferay.portal.search.elasticsearch7.internal.util.ResourceUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CreateIndexResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.util.LocalizationImpl;

import org.elasticsearch.action.search.SearchRequestBuilder;

import org.junit.Assert;

/**
 * @author Wade Cao
 */
public class HighlighterWithAnalyzersFixture implements IndexingFixture {

	public HighlighterWithAnalyzersFixture() {
	}

	public void createIndex(String suffix) {
		CreateIndexRequest createIndexRequest = new CreateIndexRequest(
			_indexName);

		createIndexRequest.setSource(_getSource(suffix));

		CreateIndexResponse createIndexResponse = _searchEngineAdapter.execute(
			createIndexRequest);

		Assert.assertTrue(createIndexResponse.isAcknowledged());
	}

	@Override
	public long getCompanyId() {
		return 0;
	}

	@Override
	public IndexSearcher getIndexSearcher() {
		return _indexSearcher;
	}

	@Override
	public IndexWriter getIndexWriter() {
		return _indexWriter;
	}

	@Override
	public SearchEngineAdapter getSearchEngineAdapter() {
		return _searchEngineAdapter;
	}

	@Override
	public boolean isSearchEngineAvailable() {
		return true;
	}

	public Hits search(Query query, SearchContext searchContext) {
		try {
			return _indexSearcher.search(searchContext, query);
		}
		catch (SearchException searchException) {
			_handle(searchException);

			throw new RuntimeException(searchException);
		}
	}

	public void setIndexCreationHelper(
		IndexCreationHelper indexCreationHelper) {

		_indexCreationHelper = indexCreationHelper;
	}

	public HighlighterWithAnalyzersFixture setIndexName(String indexName) {
		_indexName = indexName;

		return this;
	}

	public HighlighterWithAnalyzersFixture setTestClass(Class<?> testClass) {
		_testClass = testClass;

		return this;
	}

	@Override
	public void setUp() throws Exception {
		_elasticsearchFixture.setUp();

		ElasticsearchEngineAdapterFixture elasticsearchEngineAdapterFixture =
			ElasticsearchIndexingFixture.
				createElasticsearchEngineAdapterFixture(
					_elasticsearchFixture, getFacetProcessor());

		elasticsearchEngineAdapterFixture.setUp();

		SearchEngineAdapter searchEngineAdapter =
			elasticsearchEngineAdapterFixture.getSearchEngineAdapter();

		IndexNameBuilder indexNameBuilder = createIndexNameBuilder();

		Localization localization = new LocalizationImpl();

		ElasticsearchIndexSearcher elasticsearchIndexSearcher =
			ElasticsearchIndexingFixture.createIndexSearcher(
				_elasticsearchFixture, searchEngineAdapter, indexNameBuilder,
				localization);

		IndexWriter indexWriter =
			ElasticsearchIndexingFixture.createIndexWriter(
				_elasticsearchFixture, searchEngineAdapter, indexNameBuilder,
				localization);

		_indexSearcher = elasticsearchIndexSearcher;
		_indexWriter = indexWriter;
		_searchEngineAdapter = searchEngineAdapter;

		elasticsearchIndexSearcher.activate(
			_elasticsearchFixture.getElasticsearchConfigurationProperties());

		createIndex(_indexName);
	}

	public void tearDown() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	protected static IndexNameBuilder createIndexNameBuilder() {
		return new IndexNameBuilder() {

			@Override
			public String getIndexName(long companyId) {
				return _indexName;
			}

		};
	}

	protected void createIndex(IndexNameBuilder indexNameBuilder) {
		IndexCreator indexCreator = new IndexCreator() {
			{
				setElasticsearchClientResolver(_elasticsearchFixture);
				setIndexCreationHelper(_indexCreationHelper);
				setLiferayMappingsAddedToIndex(_liferayMappingsAddedToIndex);
			}
		};

		indexCreator.createIndex(
			new IndexName(indexNameBuilder.getIndexName(0)));
	}

	protected FacetProcessor<SearchRequestBuilder> getFacetProcessor() {
		if (_facetProcessor != null) {
			return _facetProcessor;
		}

		return new DefaultFacetProcessor();
	}

	protected Class<?> getTestClass() {
		return _testClass;
	}

	protected void setElasticsearchFixture(
		ElasticsearchFixture elasticsearchFixture) {

		_elasticsearchFixture = elasticsearchFixture;
	}

	protected void setFacetProcessor(
		FacetProcessor<SearchRequestBuilder> facetProcessor) {

		_facetProcessor = facetProcessor;
	}

	protected void setLiferayMappingsAddedToIndex(
		boolean liferayMappingsAddedToIndex) {

		_liferayMappingsAddedToIndex = liferayMappingsAddedToIndex;
	}

	private String _getSource(String suffix) {
		return ResourceUtil.getResourceAsString(
			getTestClass(), "dependencies/" + suffix + ".json");
	}

	private void _handle(SearchException searchException) {
		Throwable t = searchException.getCause();

		if (t instanceof RuntimeException) {
			throw (RuntimeException)t;
		}

		if (t != null) {
			throw new RuntimeException(t);
		}
	}

	private static ElasticsearchFixture _elasticsearchFixture;
	private static String _indexName;

	private FacetProcessor<SearchRequestBuilder> _facetProcessor;
	private IndexCreationHelper _indexCreationHelper;
	private IndexSearcher _indexSearcher;
	private IndexWriter _indexWriter;
	private boolean _liferayMappingsAddedToIndex;
	private SearchEngineAdapter _searchEngineAdapter;
	private Class<?> _testClass;

}