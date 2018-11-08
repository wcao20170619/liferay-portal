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

package com.liferay.portal.search.test.util.indexing;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcher;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.search.test.util.SearchMapUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;

/**
 * @author Miguel Angelo Caldas Gallindo
 */
public abstract class BaseIndexingTestCase {

	public BaseIndexingTestCase() {
		Class<?> clazz = getClass();

		_entryClassName = StringUtil.toLowerCase(clazz.getSimpleName());
	}

	@Before
	public void setUp() throws Exception {
		_documentFixture.setUp();

		_indexingFixture = createIndexingFixture();

		Assume.assumeTrue(_indexingFixture.isSearchEngineAvailable());

		_indexingFixture.setUp();

		_indexSearcher = _indexingFixture.getIndexSearcher();
		_indexWriter = _indexingFixture.getIndexWriter();
	}

	@After
	public void tearDown() throws Exception {
		if (!_indexingFixture.isSearchEngineAvailable()) {
			return;
		}

		try {
			_indexWriter.deleteEntityDocuments(
				createSearchContext(), _entryClassName);
		}
		catch (SearchException se) {
		}

		_documentFixture.tearDown();

		_indexingFixture.tearDown();
	}

	protected static SearchContext createSearchContext() {
		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(COMPANY_ID);
		searchContext.setGroupIds(new long[] {GROUP_ID});

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setHitsProcessingEnabled(true);
		queryConfig.setScoreEnabled(false);

		searchContext.setStart(QueryUtil.ALL_POS);

		return searchContext;
	}

	protected static <K, V> Map<K, V> toMap(K key, V value) {
		return Collections.singletonMap(key, value);
	}

	protected void addDocument(DocumentCreationHelper documentCreationHelper)
		throws Exception {

		Document document = DocumentFixture.newDocument(
			COMPANY_ID, GROUP_ID, _entryClassName);

		documentCreationHelper.populate(document);

		_indexWriter.addDocument(createSearchContext(), document);
	}

	protected void addDocuments(
			Function<String, DocumentCreationHelper> function,
			Collection<String> values)
		throws Exception {

		for (String value : values) {
			addDocument(function.apply(value));
		}
	}

	protected void assertSearch(Consumer<IndexingTestHelper> consumer)
		throws Exception {

		IdempotentRetryAssert.retryAssert(
			10, TimeUnit.SECONDS,
			() -> {
				consumer.accept(new IndexingTestHelper());

				return null;
			});
	}

	protected abstract IndexingFixture createIndexingFixture() throws Exception;

	protected Query getDefaultQuery() {
		Map<String, String> map = SearchMapUtil.join(
			toMap(Field.COMPANY_ID, String.valueOf(COMPANY_ID)),
			toMap(Field.ENTRY_CLASS_NAME, _entryClassName));

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		map.forEach(
			(key, value) -> booleanQueryImpl.add(
				new TermQueryImpl(key, value), BooleanClauseOccur.MUST));

		return booleanQueryImpl;
	}

	protected String getEntryClassName() {
		return _entryClassName;
	}

	protected IndexSearcher getIndexSearcher() {
		return _indexSearcher;
	}

	protected IndexWriter getIndexWriter() {
		return _indexWriter;
	}

	protected Hits search(SearchContext searchContext) {
		return search(searchContext, getDefaultQuery());
	}

	protected Hits search(SearchContext searchContext, Query query) {
		try {
			return _indexSearcher.search(searchContext, query);
		}
		catch (SearchException se) {
			Throwable t = se.getCause();

			if (t instanceof RuntimeException) {
				throw (RuntimeException)t;
			}

			throw new RuntimeException(se);
		}
	}

	protected long searchCount(SearchContext searchContext, Query query) {
		try {
			return _indexSearcher.searchCount(searchContext, query);
		}
		catch (SearchException se) {
			Throwable t = se.getCause();

			if (t instanceof RuntimeException) {
				throw (RuntimeException)t;
			}

			throw new RuntimeException(se);
		}
	}

	protected void setPreBooleanFilter(Filter filter, Query query) {
		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.add(filter, BooleanClauseOccur.MUST);

		query.setPreBooleanFilter(booleanFilter);
	}

	protected static final long COMPANY_ID = RandomTestUtil.randomLong();

	protected static final long GROUP_ID = RandomTestUtil.randomLong();

	protected class IndexingTestHelper {

		public IndexingTestHelper() {
			_searchContext = createSearchContext();
		}

		public void assertResultCount(int expected) {
			Document[] documents = _hits.getDocs();

			Assert.assertEquals(
				Arrays.toString(documents), expected, documents.length);
		}

		public void assertValues(
			String fieldName, List<String> expectedValues) {

			DocumentsAssert.assertValues(
				(String)_searchContext.getAttribute("queryString"),
				_hits.getDocs(), fieldName, expectedValues);
		}

		public void define(Consumer<SearchContext> consumer) {
			consumer.accept(_searchContext);
		}

		public String getQueryString() {
			return (String)_searchContext.getAttribute("queryString");
		}

		public SearchContext getSearchContext() {
			return _searchContext;
		}

		public void search() {
			_hits = BaseIndexingTestCase.this.search(
				_searchContext, getQuery());
		}

		public long searchCount() {
			return BaseIndexingTestCase.this.searchCount(
				_searchContext, getQuery());
		}

		public void setFilter(Filter filter) {
			_filter = filter;
		}

		public void setPostFilter(Filter postFilter) {
			_postFilter = postFilter;
		}

		public void setQuery(Query query) {
			_query = query;
		}

		public void setQueryContributor(QueryContributor queryContributor) {
			_queryContributor = queryContributor;
		}

		public void setSearchContextAttribute(String name, Serializable value) {
			_searchContext.setAttribute(name, value);
		}

		public void verify(Consumer<Hits> consumer) {
			consumer.accept(_hits);
		}

		protected Query getQuery() {
			Query query = _query;

			if (query == null) {
				query = getDefaultQuery();
			}

			if (_queryContributor != null) {
				_queryContributor.contribute(query);
			}

			if (_filter != null) {
				setPreBooleanFilter(_filter, query);
			}

			if (_postFilter != null) {
				query.setPostFilter(_postFilter);
			}

			return query;
		}

		private Filter _filter;
		private Hits _hits;
		private Filter _postFilter;
		private Query _query;
		private QueryContributor _queryContributor;
		private final SearchContext _searchContext;

	}

	private final DocumentFixture _documentFixture = new DocumentFixture();
	private final String _entryClassName;
	private IndexingFixture _indexingFixture;
	private IndexSearcher _indexSearcher;
	private IndexWriter _indexWriter;

}