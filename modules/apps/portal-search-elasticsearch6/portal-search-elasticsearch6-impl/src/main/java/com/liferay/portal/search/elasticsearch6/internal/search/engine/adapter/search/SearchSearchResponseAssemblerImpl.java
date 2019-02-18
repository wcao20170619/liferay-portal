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

import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.AggregationResultTranslator;
import com.liferay.portal.search.aggregation.AggregationResults;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregationResultTranslator;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.AggregationResultTranslatorFactory;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.ElasticsearchAggregationResultTranslator;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.ElasticsearchAggregationResultsTranslator;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.PipelineAggregationResultTranslatorFactory;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.pipeline.ElasticsearchPipelineAggregationResultTranslator;
import com.liferay.portal.search.elasticsearch6.internal.search.response.SearchResponseTranslator;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;

import java.util.Map;
import java.util.stream.Stream;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, service = SearchSearchResponseAssembler.class)
public class SearchSearchResponseAssemblerImpl
	implements AggregationResultTranslatorFactory,
			   PipelineAggregationResultTranslatorFactory,
			   SearchSearchResponseAssembler {

	@Override
	public void assemble(
		SearchRequestBuilder searchRequestBuilder,
		SearchResponse searchResponse, SearchSearchRequest searchSearchRequest,
		SearchSearchResponse searchSearchResponse) {

		_commonSearchResponseAssembler.assemble(
			searchRequestBuilder, searchResponse, searchSearchRequest,
			searchSearchResponse);

		addAggregations(
			searchResponse, searchSearchResponse, searchSearchRequest);

		SearchHits searchHits = searchResponse.getHits();

		searchSearchResponse.setCount(searchHits.totalHits);

		_searchResponseTranslator.populate(
			searchSearchResponse, searchResponse, searchSearchRequest);
	}

	@Override
	public AggregationResultTranslator createAggregationResultTranslator(
		org.elasticsearch.search.aggregations.Aggregation
			elasticsearchAggregation) {

		return new ElasticsearchAggregationResultTranslator(
			elasticsearchAggregation, _aggregationResults);
	}

	@Override
	public PipelineAggregationResultTranslator
		createPipelineAggregationResultTranslator(
			org.elasticsearch.search.aggregations.Aggregation
				elasticsearchAggregation) {

		return new ElasticsearchPipelineAggregationResultTranslator(
			elasticsearchAggregation, _aggregationResults);
	}

	protected void addAggregations(
		SearchResponse searchResponse,
		SearchSearchResponse searchSearchResponse,
		SearchSearchRequest searchSearchRequest) {

		Aggregations elasticsearchAggregations =
			searchResponse.getAggregations();

		if (elasticsearchAggregations == null) {
			return;
		}

		Map<String, Aggregation> aggregationsMap =
			searchSearchRequest.getAggregationsMap();

		Map<String, PipelineAggregation> pipelineAggregationsMap =
			searchSearchRequest.getPipelineAggregationsMap();

		ElasticsearchAggregationResultsTranslator
			elasticsearchAggregationResultsTranslator =
				new ElasticsearchAggregationResultsTranslator(
					this, this, aggregationsMap::get,
					pipelineAggregationsMap::get);

		Stream<AggregationResult> stream =
			elasticsearchAggregationResultsTranslator.translate(
				elasticsearchAggregations);

		stream.forEach(searchSearchResponse::addAggregationResult);
	}

	@Reference(unbind = "-")
	protected void setAggregationResults(
		AggregationResults aggregationResults) {

		_aggregationResults = aggregationResults;
	}

	@Reference(unbind = "-")
	protected void setCommonSearchResponseAssembler(
		CommonSearchResponseAssembler commonSearchResponseAssembler) {

		_commonSearchResponseAssembler = commonSearchResponseAssembler;
	}

	@Reference(unbind = "-")
	protected void setSearchResponseTranslator(
		SearchResponseTranslator searchResponseTranslator) {

		_searchResponseTranslator = searchResponseTranslator;
	}

	private AggregationResults _aggregationResults;
	private CommonSearchResponseAssembler _commonSearchResponseAssembler;
	private SearchResponseTranslator _searchResponseTranslator;

}