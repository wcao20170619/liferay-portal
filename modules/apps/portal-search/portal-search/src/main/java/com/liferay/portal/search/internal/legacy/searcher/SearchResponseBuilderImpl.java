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

package com.liferay.portal.search.internal.legacy.searcher;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.SearchResponseBuilder;
import com.liferay.portal.search.stats.StatsResponse;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author André de Oliveira
 */
public class SearchResponseBuilderImpl implements SearchResponseBuilder {

	public SearchResponseBuilderImpl(SearchContext searchContext) {
		_searchContext = searchContext;
	}

	@Override
	public SearchResponseBuilder aggregationResultsMap(
		Map<String, AggregationResult> aggregationResultsMap) {

		_searchContext.setAttribute(
			_AGGREGATION_RESULTS_MAP,
			new LinkedHashMap<>(aggregationResultsMap));

		return this;
	}

	@Override
	public SearchResponse build() {
		return new SearchResponseImpl();
	}

	@Override
	public SearchResponseBuilder requestString(String requestString) {
		_searchContext.setAttribute(_QUERY_STRING, requestString);
		_searchContext.setAttribute(_REQUEST_STRING, requestString);

		return this;
	}

	@Override
	public SearchResponseBuilder responseString(String responseString) {
		_searchContext.setAttribute(_RESPONSE_STRING, responseString);

		return this;
	}

	@Override
	public SearchResponseBuilder statsResponseMap(
		Map<String, StatsResponse> map) {

		_searchContext.setAttribute(_STATS_RESPONSE_MAP, new HashMap<>(map));

		return this;
	}

	public class SearchResponseImpl implements SearchResponse {

		@Override
		public AggregationResult getAggregationResult(String name) {
			Map<String, AggregationResult> map =
				(Map<String, AggregationResult>)
					_searchContext.getAttribute(_AGGREGATION_RESULTS_MAP);

			if (map != null) {
				return map.get(name);
			}

			return null;
		}

		@Override
		public Map<String, AggregationResult> getAggregationResultsMap() {
			Map<String, AggregationResult> map =
				(Map<String, AggregationResult>)
					_searchContext.getAttribute(_AGGREGATION_RESULTS_MAP);

			if (map != null) {
				return map;
			}

			return Collections.emptyMap();
		}

		@Override
		public String getRequestString() {
			return GetterUtil.getString(
				_searchContext.getAttribute(_REQUEST_STRING));
		}

		@Override
		public String getResponseString() {
			return GetterUtil.getString(
				_searchContext.getAttribute(_RESPONSE_STRING));
		}

		@Override
		public Map<String, StatsResponse> getStatsResponseMap() {
			Serializable serializable = _searchContext.getAttribute(
				_STATS_RESPONSE_MAP);

			if (serializable != null) {
				return (Map<String, StatsResponse>)serializable;
			}

			return Collections.emptyMap();
		}

	}

	private static final String _AGGREGATION_RESULTS_MAP =
		"aggregation.results.map";

	private static final String _QUERY_STRING = "queryString";

	private static final String _REQUEST_STRING = "request.string";

	private static final String _RESPONSE_STRING = "response.string";

	private static final String _STATS_RESPONSE_MAP = "stats.response.map";

	private final SearchContext _searchContext;

}