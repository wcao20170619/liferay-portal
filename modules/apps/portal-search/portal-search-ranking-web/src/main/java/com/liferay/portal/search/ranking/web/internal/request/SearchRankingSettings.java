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

import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilder;

import java.util.Optional;

/**
 * @author Wade Cao
 */
public class SearchRankingSettings {

	public SearchRankingSettings(
		SearchRequestBuilder searchRequestBuilder,
		SearchContext searchContext) {

		_searchRequestBuilder = searchRequestBuilder;
		_searchContext = searchContext;
	}

	public void addCondition(BooleanClause<Query> booleanClause) {
		BooleanClause<Query>[] booleanClauses =
			_searchContext.getBooleanClauses();

		if (booleanClauses == null) {
			booleanClauses = new BooleanClause[] {booleanClause};
		}
		else {
			booleanClauses = ArrayUtil.append(booleanClauses, booleanClause);
		}

		_searchContext.setBooleanClauses(booleanClauses);
	}

	public Optional<Integer> getPaginationDelta() {
		return Optional.ofNullable(_paginationDelta);
	}


	public Optional<Integer> getPaginationStart() {
		return Optional.ofNullable(_paginationStart);
	}

	public QueryConfig getQueryConfig() {
		return _searchContext.getQueryConfig();
	}

	public SearchContext getSearchContext() {
		return _searchContext;
	}

	public SearchRequestBuilder getSearchRequestBuilder() {
		return _searchRequestBuilder;
	}

	public void setKeywords(String keywords) {
		_searchContext.setKeywords(keywords);
	}

	public void setPaginationDelta(int paginationDelta) {
		_paginationDelta = paginationDelta;
	}

	public void setPaginationStart(int paginationStart) {
		_paginationStart = paginationStart;
	}


	private Integer _paginationDelta;
	private Integer _paginationStart;
	private final SearchContext _searchContext;
	private final SearchRequestBuilder _searchRequestBuilder;

}

