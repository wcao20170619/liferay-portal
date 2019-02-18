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

package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.search;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.filter.FilterTranslator;
import com.liferay.portal.kernel.search.query.QueryTranslator;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.AggregationTranslator;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregationTranslator;
import com.liferay.portal.search.elasticsearch6.internal.facet.FacetTranslator;
import com.liferay.portal.search.elasticsearch6.internal.stats.StatsTranslator;
import com.liferay.portal.search.engine.adapter.search.BaseSearchRequest;
import com.liferay.portal.search.stats.StatsRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.rescore.QueryRescorerBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	immediate = true, service = CommonSearchRequestBuilderAssembler.class
)
public class CommonSearchRequestBuilderAssemblerImpl
	implements CommonSearchRequestBuilderAssembler {

	@Override
	public void assemble(
		SearchRequestBuilder searchRequestBuilder,
		BaseSearchRequest baseSearchRequest) {

		setAggregations(searchRequestBuilder, baseSearchRequest);
		setExplain(searchRequestBuilder, baseSearchRequest);
		setFacets(searchRequestBuilder, baseSearchRequest);
		setIndices(searchRequestBuilder, baseSearchRequest);
		setMinScore(searchRequestBuilder, baseSearchRequest);
		setPipelineAggregations(searchRequestBuilder, baseSearchRequest);
		setPostFilter(searchRequestBuilder, baseSearchRequest);
		setQuery(searchRequestBuilder, baseSearchRequest);
		setRequestCache(searchRequestBuilder, baseSearchRequest);
		setRescorer(searchRequestBuilder, baseSearchRequest);
		setStatsRequests(searchRequestBuilder, baseSearchRequest);
		setTimeout(searchRequestBuilder, baseSearchRequest);
		setTrackTotalHits(searchRequestBuilder, baseSearchRequest);
	}

	protected QueryBuilder getQueryBuilder(
		BaseSearchRequest searchSearchRequest) {

		Query query = searchSearchRequest.getQuery();

		QueryBuilder queryBuilder = _queryTranslator.translate(query, null);

		if ((query.getPreBooleanFilter() == null) ||
			(query instanceof BooleanQuery)) {

			return queryBuilder;
		}

		// LPS-86537 the following is only present to allow for backwards
		// compatibility.  Not all Query should have filters allowed according
		// to Elasticsearch's API.

		// See related note in BooleanQueryTranslatorImpl

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder.filter(
			_filterTranslator.translate(query.getPreBooleanFilter(), null));
		boolQueryBuilder.must(queryBuilder);

		return boolQueryBuilder;
	}

	protected void setAggregations(
		SearchRequestBuilder searchRequestBuilder,
		BaseSearchRequest baseSearchRequest) {

		Map<String, Aggregation> aggregationsMap =
			baseSearchRequest.getAggregationsMap();

		if (MapUtil.isNotEmpty(aggregationsMap)) {
			Collection<Aggregation> aggregations = aggregationsMap.values();

			aggregations.forEach(
				aggregation -> {
					AggregationBuilder aggregationBuilder =
						_aggregationTranslator.translate(aggregation);

					searchRequestBuilder.addAggregation(aggregationBuilder);
				});
		}
	}

	@Reference(target = "(search.engine.impl=Elasticsearch)", unbind = "-")
	protected void setAggregationTranslator(
		AggregationTranslator<AggregationBuilder> aggregationTranslator) {

		_aggregationTranslator = aggregationTranslator;
	}

	protected void setExplain(
		SearchRequestBuilder searchRequestBuilder,
		BaseSearchRequest baseSearchRequest) {

		searchRequestBuilder.setExplain(baseSearchRequest.isExplain());
	}

	protected void setFacets(
		SearchRequestBuilder searchRequestBuilder,
		BaseSearchRequest baseSearchRequest) {

		_facetTranslator.translate(
			searchRequestBuilder, baseSearchRequest.getQuery(),
			baseSearchRequest.getFacets(),
			baseSearchRequest.isBasicFacetSelection());
	}

	@Reference(unbind = "-")
	protected void setFacetTranslator(FacetTranslator facetTranslator) {
		_facetTranslator = facetTranslator;
	}

	@Reference(target = "(search.engine.impl=Elasticsearch)", unbind = "-")
	protected void setFilterTranslator(
		FilterTranslator<QueryBuilder> filterTranslator) {

		_filterTranslator = filterTranslator;
	}

	protected void setIndices(
		SearchRequestBuilder searchRequestBuilder,
		BaseSearchRequest baseSearchRequest) {

		searchRequestBuilder.setIndices(baseSearchRequest.getIndexNames());
	}

	protected void setMinScore(
		SearchRequestBuilder searchRequestBuilder,
		BaseSearchRequest baseSearchRequest) {

		if (baseSearchRequest.getMinimumScore() > 0) {
			searchRequestBuilder.setMinScore(
				baseSearchRequest.getMinimumScore());
		}
	}

	protected void setPipelineAggregations(
		SearchRequestBuilder searchRequestBuilder,
		BaseSearchRequest baseSearchRequest) {

		Map<String, PipelineAggregation> pipelineAggregationsMap =
			baseSearchRequest.getPipelineAggregationsMap();

		if (MapUtil.isNotEmpty(pipelineAggregationsMap)) {
			Collection<PipelineAggregation> pipelineAggregations =
				pipelineAggregationsMap.values();

			pipelineAggregations.forEach(
				pipelineAggregation -> {
					PipelineAggregationBuilder pipelineAggregationBuilder =
						_pipelineAggregationTranslator.translate(
							pipelineAggregation);

					searchRequestBuilder.addAggregation(
						pipelineAggregationBuilder);
				});
		}
	}

	@Reference(target = "(search.engine.impl=Elasticsearch)", unbind = "-")
	protected void setPipelineAggregationTranslator(
		PipelineAggregationTranslator<PipelineAggregationBuilder>
			pipelineAggregationTranslator) {

		_pipelineAggregationTranslator = pipelineAggregationTranslator;
	}

	protected void setPostFilter(
		SearchRequestBuilder searchRequestBuilder,
		BaseSearchRequest baseSearchRequest) {

		if (baseSearchRequest.getPostFilter() != null) {
			QueryBuilder postFilterQueryBuilder = _filterTranslator.translate(
				baseSearchRequest.getPostFilter(), null);

			searchRequestBuilder.setPostFilter(postFilterQueryBuilder);
		}
	}

	protected void setQuery(
		SearchRequestBuilder searchRequestBuilder,
		BaseSearchRequest baseSearchRequest) {

		searchRequestBuilder.setQuery(getQueryBuilder(baseSearchRequest));
	}

	@Reference(target = "(search.engine.impl=Elasticsearch)", unbind = "-")
	protected void setQueryTranslator(
		QueryTranslator<QueryBuilder> queryTranslator) {

		_queryTranslator = queryTranslator;
	}

	protected void setRequestCache(
		SearchRequestBuilder searchRequestBuilder,
		BaseSearchRequest baseSearchRequest) {

		if (baseSearchRequest.isRequestCache()) {
			searchRequestBuilder.setRequestCache(
				baseSearchRequest.isRequestCache());
		}
	}

	protected void setRescorer(
		SearchRequestBuilder searchRequestBuilder,
		BaseSearchRequest baseSearchRequest) {

		Query query = baseSearchRequest.getRescoreQuery();

		if (query == null) {
			return;
		}

		searchRequestBuilder.setRescorer(
			new QueryRescorerBuilder(_queryTranslator.translate(query, null)));
	}

	protected void setStatsRequests(
		SearchRequestBuilder searchRequestBuilder,
		BaseSearchRequest baseSearchRequest) {

		List<StatsRequest> statsRequests = baseSearchRequest.getStatsRequests();

		if (!ListUtil.isEmpty(statsRequests)) {
			statsRequests.forEach(
				statsRequest -> _statsTranslator.populateRequest(
					searchRequestBuilder, statsRequest));
		}
	}

	@Reference(unbind = "-")
	protected void setStatsTranslator(StatsTranslator statsTranslator) {
		_statsTranslator = statsTranslator;
	}

	protected void setTimeout(
		SearchRequestBuilder searchRequestBuilder,
		BaseSearchRequest baseSearchRequest) {

		if (baseSearchRequest.getTimeoutInMilliseconds() > 0) {
			searchRequestBuilder.setTimeout(
				TimeValue.timeValueMillis(
					baseSearchRequest.getTimeoutInMilliseconds()));
		}
	}

	protected void setTrackTotalHits(
		SearchRequestBuilder searchRequestBuilder,
		BaseSearchRequest baseSearchRequest) {

		searchRequestBuilder.setTrackTotalHits(
			baseSearchRequest.isTrackTotalHits());
	}

	private AggregationTranslator<AggregationBuilder> _aggregationTranslator;
	private FacetTranslator _facetTranslator;
	private FilterTranslator<QueryBuilder> _filterTranslator;
	private PipelineAggregationTranslator<PipelineAggregationBuilder>
		_pipelineAggregationTranslator;
	private QueryTranslator<QueryBuilder> _queryTranslator;
	private StatsTranslator _statsTranslator;

}