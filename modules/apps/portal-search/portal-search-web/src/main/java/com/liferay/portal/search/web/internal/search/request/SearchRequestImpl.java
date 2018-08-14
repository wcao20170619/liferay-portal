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

package com.liferay.portal.search.web.internal.search.request;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.web.internal.search.results.FederatedSearchResultsImpl;
import com.liferay.portal.search.web.search.request.FederatedSearcher;
import com.liferay.portal.search.web.search.request.SearchRequest;
import com.liferay.portal.search.web.search.request.SearchSettings;
import com.liferay.portal.search.web.search.request.SearchSettingsContributor;
import com.liferay.portal.search.web.search.result.FederatedSearchResults;

import java.util.HashSet;
import java.util.Set;

/**
 * @author André de Oliveira
 */
public class SearchRequestImpl implements SearchRequest { //move to portal-search

	public SearchRequestImpl(
		SearchContextBuilder searchContextBuilder,
		SearchContainerBuilder searchContainerBuilder,
		FacetedSearcherManager facetedSearcherManager) {

		_searchContextBuilder = searchContextBuilder;
		_searchContainerBuilder = searchContainerBuilder;
		_facetedSearcherManager = facetedSearcherManager;
	}

	@Override
	public void addSearchSettingsContributor(
		SearchSettingsContributor searchSettingsContributor) {

		_searchSettingsContributors.add(searchSettingsContributor);
	}

	@Override
	public void addFederatedSearcher(FederatedSearcher federatedSearcher) {
		_federatedSearchers.add(federatedSearcher);
	}

	@Override
	public void removeSearchSettingsContributor(
		SearchSettingsContributor searchSettingsContributor) {

		_searchSettingsContributors.remove(searchSettingsContributor);
	}

	@Override
	public SearchResponseImpl search() {
		SearchContext searchContext = buildSearchContext();

		SearchSettingsImpl searchSettingsImpl = buildSettings(searchContext);

		SearchContainer<Document> searchContainer = buildSearchContainer(
			searchSettingsImpl);

		searchContext.setEnd(searchContainer.getEnd());
		searchContext.setStart(searchContainer.getStart());

		FederatedSearchResults
			federatedSearchResults = new FederatedSearchResultsImpl();

		String[] selectedSources = GetterUtil.getStringValues(
			searchContext.getAttribute(
				SearchContextAttributes.ATTRIBUTE_KEY_FEDERATED_SEARCH_SOURCES));

		for (FederatedSearcher federatedSearcher : _federatedSearchers) {
			if (ArrayUtil.contains(selectedSources, federatedSearcher.getSource())) {
				federatedSearchResults.add(
					federatedSearcher.getSource(),
					federatedSearcher.getHits(searchContext));
			}
		}

		Hits hits = search(searchContext);

		searchContainer.setResults(hits.toList());

		searchContainer.setSearch(true);

		searchContainer.setTotal(hits.getLength());

		SearchResponseImpl searchResponseImpl = buildSearchResponse(
			hits, federatedSearchResults, searchContext, searchContainer, searchSettingsImpl);

		return searchResponseImpl;
	}

	protected SearchContainer<Document> buildSearchContainer(
		SearchSettingsImpl searchSettingsImpl) {

		return _searchContainerBuilder.getSearchContainer(searchSettingsImpl);
	}

	protected SearchContext buildSearchContext() {
		SearchContext searchContext = _searchContextBuilder.getSearchContext();

		searchContext.setAttribute("filterExpired", Boolean.TRUE);
		searchContext.setAttribute("paginationType", "more");

		return searchContext;
	}

	protected SearchResponseImpl buildSearchResponse(
		Hits hits, FederatedSearchResults federatedSearchResults, SearchContext searchContext,
		SearchContainer<Document> searchContainer,
		SearchSettings searchSettings) {

		SearchResponseImpl searchResponseImpl = new SearchResponseImpl();

		searchResponseImpl.setDocuments(hits.toList());
		searchResponseImpl.setFederatedSearchResults(federatedSearchResults);
		searchResponseImpl.setHits(hits);
		searchResponseImpl.setKeywords(searchContext.getKeywords());
		searchResponseImpl.setPaginationDelta(searchContainer.getDelta());
		searchResponseImpl.setPaginationStart(searchContainer.getCur());
		searchResponseImpl.setQueryString(
			(String)searchContext.getAttribute("queryString"));
		searchResponseImpl.setSearchContainer(searchContainer);
		searchResponseImpl.setSearchContext(searchContext);
		searchResponseImpl.setSearchSettings(searchSettings);
		searchResponseImpl.setTotalHits(hits.getLength());

		return searchResponseImpl;
	}

	protected SearchSettingsImpl buildSettings(SearchContext searchContext) {
		SearchSettingsImpl searchSettingsImpl = new SearchSettingsImpl(
			searchContext);

		_searchSettingsContributors.forEach(
			searchContributor -> searchContributor.contribute(
				searchSettingsImpl));

		return searchSettingsImpl;
	}

	protected Hits search(
		FacetedSearcher facetedSearcher, SearchContext searchContext) {

		try {
			return facetedSearcher.search(searchContext);
		}
		catch (SearchException se) {
			throw new RuntimeException(se);
		}
	}

	protected Hits search(SearchContext searchContext) {
		FacetedSearcher facetedSearcher =
			_facetedSearcherManager.createFacetedSearcher();

		return search(facetedSearcher, searchContext);
	}

	private final FacetedSearcherManager _facetedSearcherManager;
	private final SearchContainerBuilder _searchContainerBuilder;
	private final SearchContextBuilder _searchContextBuilder;
	private final Set<SearchSettingsContributor> _searchSettingsContributors =
		new HashSet<>();
	private final Set<FederatedSearcher> _federatedSearchers =
		new HashSet<>();

}