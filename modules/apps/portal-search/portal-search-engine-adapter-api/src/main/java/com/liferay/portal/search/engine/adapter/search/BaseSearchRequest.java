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

package com.liferay.portal.search.engine.adapter.search;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregation;
import com.liferay.portal.search.stats.StatsRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael C. Han
 */
@ProviderType
public abstract class BaseSearchRequest {

	public void addAggregation(Aggregation aggregation) {
		_aggregationsMap.put(aggregation.getName(), aggregation);
	}

	public void addPipelineAggregation(
		PipelineAggregation pipelineAggregation) {

		_pipelineAggregationsMap.put(
			pipelineAggregation.getName(), pipelineAggregation);
	}

	public Map<String, Aggregation> getAggregationsMap() {
		return Collections.unmodifiableMap(_aggregationsMap);
	}

	public Map<String, Facet> getFacets() {
		return _facets;
	}

	public String[] getIndexNames() {
		return _indexNames;
	}

	public float getMinimumScore() {
		return _minimumScore;
	}

	public Map<String, PipelineAggregation> getPipelineAggregationsMap() {
		return Collections.unmodifiableMap(_pipelineAggregationsMap);
	}

	public Filter getPostFilter() {
		return _postFilter;
	}

	public Query getQuery() {
		return _query;
	}

	public Query getRescoreQuery() {
		return _rescoreQuery;
	}

	public List<StatsRequest> getStatsRequests() {
		return Collections.unmodifiableList(_statsRequests);
	}

	public long getTimeoutInMilliseconds() {
		return _timeoutInMilliseconds;
	}

	public boolean isBasicFacetSelection() {
		return _basicFacetSelection;
	}

	public boolean isExplain() {
		return _explain;
	}

	public boolean isIncludeResponseString() {
		return _includeResponseString;
	}

	public boolean isRequestCache() {
		return _requestCache;
	}

	public boolean isTrackTotalHits() {
		return _trackTotalHits;
	}

	public void putAllFacets(Map<String, Facet> facets) {
		_facets.putAll(facets);
	}

	public void putFacet(String fieldName, Facet facet) {
		_facets.put(fieldName, facet);
	}

	public void setBasicFacetSelection(boolean basicFacetSelection) {
		_basicFacetSelection = basicFacetSelection;
	}

	public void setExplain(boolean explain) {
		_explain = explain;
	}

	public void setIncludeResponseString(boolean includeResponseString) {
		_includeResponseString = includeResponseString;
	}

	public void setIndexNames(String... indexNames) {
		_indexNames = indexNames;
	}

	public void setMinimumScore(float minimumScore) {
		_minimumScore = minimumScore;
	}

	public void setPostFilter(Filter postFilter) {
		_postFilter = postFilter;
	}

	public void setQuery(Query query) {
		_query = query;
	}

	public void setRequestCache(boolean requestCache) {
		_requestCache = requestCache;
	}

	public void setRescoreQuery(Query rescoreQuery) {
		_rescoreQuery = rescoreQuery;
	}

	public void setStatsRequests(Collection<StatsRequest> statsRequests) {
		_statsRequests = new ArrayList<>(statsRequests);
	}

	public void setTimeoutInMilliseconds(long timeoutInMilliseconds) {
		_timeoutInMilliseconds = timeoutInMilliseconds;
	}

	public void setTrackTotalHits(boolean trackTotalHits) {
		_trackTotalHits = trackTotalHits;
	}

	private final Map<String, Aggregation> _aggregationsMap =
		new LinkedHashMap<>();
	private boolean _basicFacetSelection;
	private boolean _explain;
	private final Map<String, Facet> _facets = new LinkedHashMap<>();
	private boolean _includeResponseString;
	private String[] _indexNames;
	private float _minimumScore;
	private final Map<String, PipelineAggregation> _pipelineAggregationsMap =
		new LinkedHashMap<>();
	private Filter _postFilter;
	private Query _query;
	private boolean _requestCache;
	private Query _rescoreQuery;
	private List<StatsRequest> _statsRequests = Collections.emptyList();
	private long _timeoutInMilliseconds;
	private boolean _trackTotalHits = true;

}