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

package com.liferay.portal.search.ranking.web.internal.request;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;

import java.util.List;
import java.util.Optional;

/**
 * @author Wade Cao
 */
public class SearchRankingRequest {

	public SearchRankingRequest(
		SearchContext searchContext,
		SearchContainer<Document> searchContainer,
		Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory) {

		_searchContext = searchContext;
		_searchContainer = searchContainer;
		_searcher = searcher;
		_searchRequestBuilderFactory = searchRequestBuilderFactory;
	}

	public SearchRankingResponse search() {
		SearchContext searchContext = buildSearchContext();

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.getSearchRequestBuilder(searchContext);

		SearchRankingSettings searchRankingSettings = buildSettings(
			searchRequestBuilder, searchContext);

		SearchContainer<Document> searchContainer = buildSearchContainer(
			searchRankingSettings);

		searchContext.setEnd(searchContainer.getEnd());
		searchContext.setStart(searchContainer.getStart());

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		populateSearchContainer(searchContainer, searchResponse);

		SearchRankingResponse searchRankingResponse = new SearchRankingResponse();

		searchResponse.withHits(
			hits -> {
				searchRankingResponse.setDocuments(hits.toList());
				searchRankingResponse.setHits(hits);
				searchRankingResponse.setTotalHits(hits.getLength());
			});

		searchRankingResponse.setKeywords(searchContext.getKeywords());
		searchRankingResponse.setPaginationDelta(searchContainer.getDelta());
		searchRankingResponse.setPaginationStart(searchContainer.getCur());
		searchRankingResponse.setSearchContainer(searchContainer);
		searchRankingResponse.setSearchResponse(searchResponse);
		searchRankingResponse.setSearchRankingSettings(searchRankingSettings);

		return searchRankingResponse;
	}

	protected static void populateSearchContainer(
		SearchContainer<Document> searchContainer,
		SearchResponse searchResponse) {

		searchContainer.setSearch(true);

		searchResponse.withHits(
			hits -> {
				searchContainer.setResults(hits.toList());
				searchContainer.setTotal(hits.getLength());
			});
	}

	protected SearchContext buildSearchContext() {
		
		_searchContext.setAttribute("filterExpired", Boolean.TRUE);
		_searchContext.setAttribute("paginationType", "more");

		return _searchContext;
	}

	protected SearchRankingSettings buildSettings(
		SearchRequestBuilder searchRequestBuilder,
		SearchContext searchContext) {

		SearchRankingSettings searchRankingSettings = new SearchRankingSettings(
			searchRequestBuilder, searchContext);

		return searchRankingSettings;
	}
	
	protected SearchContext buildSearchContext(ThemeDisplay themeDisplay) {

		_searchContext.setCompanyId(themeDisplay.getCompanyId());
		_searchContext.setLayout(themeDisplay.getLayout());
		_searchContext.setLocale(themeDisplay.getLocale());
		_searchContext.setTimeZone(themeDisplay.getTimeZone());
		_searchContext.setUserId(themeDisplay.getUserId());

		QueryConfig queryConfig = _searchContext.getQueryConfig();

		queryConfig.setCollatedSpellCheckResultEnabled(false);
		queryConfig.setLocale(themeDisplay.getLocale());

		return _searchContext;
	}
	
	protected SearchContainer<Document> buildSearchContainer(
		SearchRankingSettings searchRankingSettings) {

		Optional<Integer> paginationStartOptional =
			searchRankingSettings.getPaginationStart();

		Optional<Integer> paginationDeltaOptional =
			searchRankingSettings.getPaginationDelta();

		DisplayTerms displayTerms = null;
		DisplayTerms searchTerms = null;

		int cur = paginationStartOptional.orElse(0);

		int delta = paginationDeltaOptional.orElse(
			SearchContainer.DEFAULT_DELTA);

		List<String> headerNames = null;
		String emptyResultsMessage = null;
		String cssClass = null;

		_searchContainer.setHeaderNames(headerNames);
		_searchContainer.setEmptyResultsMessage(emptyResultsMessage);
		_searchContainer.setCssClass(cssClass);
		_searchContainer.setDelta(delta);
		
		return _searchContainer;
	}

	private final SearchContext _searchContext;
	private final SearchContainer<Document> _searchContainer;
	private final Searcher _searcher;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;

}
