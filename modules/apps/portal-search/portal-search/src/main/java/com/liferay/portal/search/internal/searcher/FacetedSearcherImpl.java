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

package com.liferay.portal.search.internal.searcher;

import com.liferay.portal.kernel.search.BaseSearcher;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcherHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchAllQuery;
import com.liferay.portal.kernel.search.generic.StringQuery;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.internal.expando.ExpandoQueryContributorHelper;
import com.liferay.portal.search.internal.indexer.PreFilterContributorHelper;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class FacetedSearcherImpl
	extends BaseSearcher implements FacetedSearcher {

	public FacetedSearcherImpl(
		ExpandoQueryContributorHelper expandoQueryContributorHelper,
		IndexerRegistry indexerRegistry,
		IndexSearcherHelper indexSearcherHelper,
		PreFilterContributorHelper preFilterContributorHelper,
		SearchEngineHelper searchEngineHelper) {

		_expandoQueryContributorHelper = expandoQueryContributorHelper;
		_indexerRegistry = indexerRegistry;
		_indexSearcherHelper = indexSearcherHelper;
		_preFilterContributorHelper = preFilterContributorHelper;
		_searchEngineHelper = searchEngineHelper;
	}

	protected void addSearchExpando(
			BooleanQuery booleanQuery, SearchContext searchContext,
			String keywords, Collection<String> classNames)
		throws Exception {

		_expandoQueryContributorHelper.setAndSearch(
			searchContext.isAndSearch());
		_expandoQueryContributorHelper.setBooleanQuery(booleanQuery);
		_expandoQueryContributorHelper.setClassNamesStream(classNames.stream());
		_expandoQueryContributorHelper.setCompanyId(
			searchContext.getCompanyId());
		_expandoQueryContributorHelper.setKeywords(keywords);
		_expandoQueryContributorHelper.setLocale(searchContext.getLocale());

		_expandoQueryContributorHelper.contribute();
	}

	protected void addSearchKeywords(
			BooleanQuery searchQuery, String keywords, boolean luceneSyntax,
			Map<String, Indexer<?>> entryClassNameIndexerMap,
			SearchContext searchContext)
		throws Exception {

		if (Validator.isNull(keywords)) {
			return;
		}

		if (luceneSyntax) {
			searchQuery.add(new StringQuery(keywords), BooleanClauseOccur.MUST);
		}
		else {
			addSearchLocalizedTerm(
				searchQuery, searchContext, Field.ASSET_CATEGORY_TITLES, false);

			searchQuery.addTerm(Field.ASSET_TAG_NAMES, keywords);

			searchQuery.addTerms(Field.KEYWORDS, keywords);

			addSearchExpando(
				searchQuery, searchContext, keywords,
				entryClassNameIndexerMap.keySet());
		}
	}

	@Override
	protected BooleanQuery createFullQuery(
			BooleanFilter fullQueryBooleanFilter, SearchContext searchContext)
		throws Exception {

		BooleanQuery searchQuery = new BooleanQueryImpl();

		String keywords = searchContext.getKeywords();

		boolean luceneSyntax = GetterUtil.getBoolean(
			searchContext.getAttribute("luceneSyntax"));

		Map<String, Indexer<?>> entryClassNameIndexerMap =
			_getEntryClassNameIndexerMap(
				_getEntryClassNames(searchContext),
				searchContext.getSearchEngineId());

		addSearchKeywords(
			searchQuery, keywords, luceneSyntax, entryClassNameIndexerMap,
			searchContext);

		_addSearchTerms(
			searchQuery, fullQueryBooleanFilter, luceneSyntax,
			entryClassNameIndexerMap, searchContext);

		_addPreFilters(
			fullQueryBooleanFilter, entryClassNameIndexerMap, searchContext);

		Map<String, Facet> facets = searchContext.getFacets();

		BooleanFilter facetBooleanFilter = new BooleanFilter();

		for (Facet facet : facets.values()) {
			BooleanClause<Filter> facetClause =
				facet.getFacetFilterBooleanClause();

			if (facetClause != null) {
				facetBooleanFilter.add(
					facetClause.getClause(),
					facetClause.getBooleanClauseOccur());
			}
		}

		BooleanQuery fullQuery = new BooleanQueryImpl();

		if (facetBooleanFilter.hasClauses()) {
			fullQuery.setPostFilter(facetBooleanFilter);
		}

		if (fullQueryBooleanFilter.hasClauses()) {
			fullQuery.setPreBooleanFilter(fullQueryBooleanFilter);
		}

		if (searchQuery.hasClauses()) {
			fullQuery.add(searchQuery, BooleanClauseOccur.MUST);
		}

		BooleanClause<Query>[] booleanClauses =
			searchContext.getBooleanClauses();

		if (booleanClauses != null) {
			for (BooleanClause<Query> booleanClause : booleanClauses) {
				fullQuery.add(
					booleanClause.getClause(),
					booleanClause.getBooleanClauseOccur());
			}
		}

		_postProcessFullQuery(
			entryClassNameIndexerMap, fullQuery, searchContext);

		return fullQuery;
	}

	@Override
	protected Hits doSearch(SearchContext searchContext)
		throws SearchException {

		try {
			searchContext.setSearchEngineId(getSearchEngineId());

			BooleanFilter queryBooleanFilter = new BooleanFilter();

			queryBooleanFilter.addRequiredTerm(
				Field.COMPANY_ID, searchContext.getCompanyId());

			Query fullQuery = createFullQuery(
				queryBooleanFilter, searchContext);

			if (!fullQuery.hasChildren()) {
				BooleanFilter preBooleanFilter =
					fullQuery.getPreBooleanFilter();

				Filter postFilter = fullQuery.getPostFilter();

				fullQuery = new MatchAllQuery();

				fullQuery.setPreBooleanFilter(preBooleanFilter);

				fullQuery.setPostFilter(postFilter);
			}

			QueryConfig queryConfig = searchContext.getQueryConfig();

			fullQuery.setQueryConfig(queryConfig);

			return _indexSearcherHelper.search(searchContext, fullQuery);
		}
		catch (Exception e) {
			throw new SearchException(e);
		}
	}

	@Override
	protected boolean isUseSearchResultPermissionFilter(
		SearchContext searchContext) {

		String[] entryClassNames = _getEntryClassNames(searchContext);

		if (ArrayUtil.isEmpty(entryClassNames)) {
			return super.isFilterSearch();
		}

		for (String entryClassName : entryClassNames) {
			Indexer<?> indexer = _indexerRegistry.getIndexer(entryClassName);

			if (indexer == null) {
				continue;
			}

			if (indexer.isFilterSearch()) {
				return true;
			}
		}

		return super.isFilterSearch();
	}

	private void _addIndexerProvidedSearchTerms(
			BooleanQuery searchQuery, Indexer<?> indexer,
			BooleanFilter booleanFilter, boolean luceneSyntax,
			SearchContext searchContext)
		throws Exception {

		if (!luceneSyntax) {
			indexer.postProcessSearchQuery(
				searchQuery, booleanFilter, searchContext);
		}

		for (IndexerPostProcessor indexerPostProcessor :
				indexer.getIndexerPostProcessors()) {

			indexerPostProcessor.postProcessSearchQuery(
				searchQuery, booleanFilter, searchContext);
		}
	}

	private void _addPreFilters(
		BooleanFilter booleanFilter,
		Map<String, Indexer<?>> entryClassNameIndexerMap,
		SearchContext searchContext) {

		_preFilterContributorHelper.contribute(
			booleanFilter, entryClassNameIndexerMap, searchContext);
	}

	private void _addSearchTerms(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			boolean luceneSyntax,
			Map<String, Indexer<?>> entryClassNameIndexerMap,
			SearchContext searchContext)
		throws Exception {

		for (Indexer<?> indexer : entryClassNameIndexerMap.values()) {
			_addIndexerProvidedSearchTerms(
				searchQuery, indexer, fullQueryBooleanFilter, luceneSyntax,
				searchContext);
		}
	}

	private Map<String, Indexer<?>> _getEntryClassNameIndexerMap(
		String[] entryClassNames, String searchEngineId) {

		Map<String, Indexer<?>> entryClassNameIndexerMap = new LinkedHashMap<>(
			entryClassNames.length);

		for (String entryClassName : entryClassNames) {
			Indexer<?> indexer = _indexerRegistry.getIndexer(entryClassName);

			if (indexer == null) {
				continue;
			}

			if (!searchEngineId.equals(indexer.getSearchEngineId())) {
				continue;
			}

			entryClassNameIndexerMap.put(entryClassName, indexer);
		}

		return entryClassNameIndexerMap;
	}

	private String[] _getEntryClassNames(SearchContext searchContext) {
		String[] entryClassNames = searchContext.getEntryClassNames();

		if (!ArrayUtil.isEmpty(entryClassNames)) {
			return entryClassNames;
		}

		return _searchEngineHelper.getEntryClassNames();
	}

	private void _postProcessFullQuery(
			Map<String, Indexer<?>> entryClassNameIndexerMap,
			BooleanQuery fullQuery, SearchContext searchContext)
		throws Exception {

		for (Indexer<?> indexer : entryClassNameIndexerMap.values()) {
			for (IndexerPostProcessor indexerPostProcessor :
					indexer.getIndexerPostProcessors()) {

				indexerPostProcessor.postProcessFullQuery(
					fullQuery, searchContext);
			}
		};
	}

	private final ExpandoQueryContributorHelper _expandoQueryContributorHelper;
	private final IndexerRegistry _indexerRegistry;
	private final IndexSearcherHelper _indexSearcherHelper;
	private final PreFilterContributorHelper _preFilterContributorHelper;
	private final SearchEngineHelper _searchEngineHelper;

}