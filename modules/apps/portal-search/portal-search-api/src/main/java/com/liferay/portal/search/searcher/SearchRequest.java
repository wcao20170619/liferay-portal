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

package com.liferay.portal.search.searcher;

import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregation;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.stats.StatsRequest;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Holds the parameters used when performing a search. Build the search request
 * with the {@link SearchRequestBuilder}.
 *
 * @author André de Oliveira
 */
@ProviderType
public interface SearchRequest {

	public Map<String, Aggregation> getAggregationsMap();

	public List<ComplexQueryPart> getComplexQueryParts();

	public List<String> getEntryClassNames();

	public String getFederatedSearchKey();

	public List<SearchRequest> getFederatedSearchRequests();

	public Integer getFrom();

	public List<String> getIndexes();

	public List<Class<?>> getModelIndexerClasses();

	public String getPaginationStartParameterName();

	public Map<String, PipelineAggregation> getPipelineAggregationsMap();

	public Query getPostFilterQuery();

	public Query getQuery();

	public String getQueryString();

	/**
	 * Provides a secondary query to reorder the top documents returned.
	 *
	 * @return the rescore query
	 */
	public Query getRescoreQuery();

	public Integer getSize();

	public List<Sort> getSorts();

	/**
	 * Provides the metric aggregations to be computed for each field.
	 *
	 * @return the stats that are enabled for each field
	 */
	public List<StatsRequest> getStatsRequests();

	public boolean isBasicFacetSelection();

	public boolean isEmptySearchEnabled();

	/**
	 * Returns <code>true</code> if the explanation for how each hit's score is
	 * computed.
	 *
	 * @return <code>true</code> if the scores are explained; <code>false</code>
	 *         otherwise
	 */
	public boolean isExplain();

	/**
	 * Returns <code>true</code> if the search engine's response string is
	 * included with the returned results.
	 *
	 * @return <code>true</code> if the response string is included;
	 *         <code>false</code> otherwise
	 */
	public boolean isIncludeResponseString();

}