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

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.search.searcher.SearchResponse;

import java.util.List;
import java.util.Optional;

/**
 * @author Wade Cao
 */
public class SearchRankingResponse {

	public List<Document> getDocuments() {
		return _documents;
	}

	public Optional<String> getKeywordsOptional() {
		return Optional.ofNullable(_keywords);
	}

	public int getPaginationDelta() {
		return _paginationDelta;
	}

	public int getPaginationStart() {
		return _paginationStart;
	}

	public String getQueryString() {
		return _searchResponse.getRequestString();
	}

	public SearchContainer<Document> getSearchContainer() {
		return _searchContainer;
	}

	public SearchResponse getSearchResponse() {
		return _searchResponse;
	}

	public int getTotalHits() {
		return _totalHits;
	}

	public void setDocuments(List<Document> documents) {
		_documents = documents;
	}

	public void setKeywords(String keywords) {
		_keywords = keywords;
	}

	public void setPaginationDelta(int paginationDelta) {
		_paginationDelta = paginationDelta;
	}

	public void setPaginationStart(int paginationStart) {
		_paginationStart = paginationStart;
	}

	public void setSearchContainer(SearchContainer<Document> searchContainer) {
		_searchContainer = searchContainer;
	}

	public void setSearchResponse(SearchResponse searchResponse) {
		_searchResponse = searchResponse;
	}

	public void setTotalHits(int totalHits) {
		_totalHits = totalHits;
	}

	private List<Document> _documents;
	private String _keywords;
	private int _paginationDelta;
	private int _paginationStart;
	private SearchContainer<Document> _searchContainer;
	private SearchResponse _searchResponse;
	private int _totalHits;

}