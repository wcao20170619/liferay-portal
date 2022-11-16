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

package com.liferay.portal.search.solr8.internal.search.engine.adapter.search;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.query.QueryTranslator;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.search.BaseSearchRequest;
import com.liferay.portal.search.solr8.internal.AggregationFilteringFacetProcessorContext;
import com.liferay.portal.search.solr8.internal.FacetProcessorContext;
import com.liferay.portal.search.solr8.internal.facet.CompositeFacetProcessor;
import com.liferay.portal.search.solr8.internal.facet.FacetProcessor;
import com.liferay.portal.search.solr8.internal.facet.FacetUtil;
import com.liferay.portal.search.solr8.internal.filter.FilterTranslator;
import com.liferay.portal.search.solr8.internal.query.translator.SolrQueryTranslator;
import com.liferay.portal.search.solr8.internal.stats.StatsTranslator;
import com.liferay.portal.search.stats.StatsRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.solr.client.solrj.SolrQuery;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(immediate = true, service = BaseSolrQueryAssembler.class)
public class BaseSolrQueryAssemblerImpl implements BaseSolrQueryAssembler {

	@Override
	public void assemble(
		SolrQuery solrQuery, BaseSearchRequest baseSearchRequest) {

		setExplain(solrQuery, baseSearchRequest);
		setFacets(solrQuery, baseSearchRequest);
		setFilterQueries(solrQuery, baseSearchRequest);
		setQuery(solrQuery, baseSearchRequest);
		setStatsRequests(solrQuery, baseSearchRequest);
	}

	protected void addFilterQuery(
		List<String> filterQueries, Facet facet, String tag) {

		BooleanClause<Filter> booleanClause =
			facet.getFacetFilterBooleanClause();

		if (booleanClause == null) {
			return;
		}

		String filterString = translate(booleanClause);

		filterQueries.add(
			StringBundler.concat(
				"{!tag=", tag, StringPool.CLOSE_CURLY_BRACE, filterString));
	}

	protected void excludeTags(
		Map<String, JSONObject> map, String excludeTagsString) {

		for (JSONObject jsonObject : map.values()) {
			jsonObject.put("excludeTags", excludeTagsString);
		}
	}

	protected String getExcludeTagsString(
		String tag, FacetProcessorContext facetProcessorContext) {

		Optional<String> optional =
			facetProcessorContext.getExcludeTagsStringOptional();

		return optional.orElse(tag);
	}

	protected Map<String, JSONObject> getFacetParameters(Facet facet) {
		return _facetProcessor.processFacet(facet);
	}

	protected String getFacetString(Map<String, JSONObject> jsonObjects) {
		Set<Map.Entry<String, JSONObject>> entrySet = jsonObjects.entrySet();

		Stream<Map.Entry<String, JSONObject>> stream = entrySet.stream();

		return stream.map(
			entry -> StringBundler.concat(
				StringPool.QUOTE, entry.getKey(), StringPool.QUOTE,
				StringPool.COLON, entry.getValue())
		).collect(
			Collectors.joining(
				StringPool.COMMA, StringPool.OPEN_CURLY_BRACE,
				StringPool.CLOSE_CURLY_BRACE)
		);
	}

	protected String getQueryString(BaseSearchRequest baseSearchRequest) {
		com.liferay.portal.search.query.Query query =
			baseSearchRequest.getQuery();

		if (query != null) {
			return _solrQueryTranslator.translate(query);
		}

		Query legacyQuery = baseSearchRequest.getQuery71();

		if (legacyQuery != null) {
			return _queryTranslator.translate(legacyQuery, null);
		}

		return null;
	}

	protected void setExplain(
		SolrQuery solrQuery, BaseSearchRequest baseSearchRequest) {

		if (baseSearchRequest.isExplain()) {
			solrQuery.setShowDebugInfo(true);
		}
	}

	protected void setFacets(
		SolrQuery solrQuery, BaseSearchRequest baseSearchRequest) {

		Map<String, Facet> facets = baseSearchRequest.getFacets();

		Map<String, JSONObject> jsonObjects = new LinkedHashMap<>();

		List<String> postFilterQueries = new ArrayList<>();

		FacetProcessorContext facetProcessorContext =
			AggregationFilteringFacetProcessorContext.newInstance(
				facets, baseSearchRequest.isBasicFacetSelection());

		for (Facet facet : facets.values()) {
			if (facet.isStatic()) {
				continue;
			}

			String tag = FacetUtil.getAggregationName(facet);

			addFilterQuery(postFilterQueries, facet, tag);

			Map<String, JSONObject> facetParameters = getFacetParameters(facet);

			excludeTags(
				facetParameters,
				getExcludeTagsString(tag, facetProcessorContext));

			jsonObjects.putAll(facetParameters);
		}

		if (!jsonObjects.isEmpty()) {
			solrQuery.add("json.facet", getFacetString(jsonObjects));
		}

		if (!postFilterQueries.isEmpty()) {
			solrQuery.setFilterQueries(
				ArrayUtil.toStringArray(postFilterQueries));
		}
	}

	protected void setFilterQueries(
		SolrQuery solrQuery, BaseSearchRequest baseSearchRequest) {

		List<String> filterQueries = new ArrayList<>();

		Query query = baseSearchRequest.getQuery71();

		if (query != null) {
			_add(filterQueries, query.getPreBooleanFilter());
			_add(filterQueries, query.getPostFilter());

			if (query instanceof BooleanQuery) {
				_add(filterQueries, (BooleanQuery)query);
			}
		}

		_addAll(filterQueries, solrQuery.getFilterQueries());

		if (!filterQueries.isEmpty()) {
			solrQuery.setFilterQueries(filterQueries.toArray(new String[0]));

			if (Validator.isBlank(solrQuery.getQuery())) {
				solrQuery.setQuery("*:*");
			}
		}
	}

	protected void setQuery(
		SolrQuery solrQuery, BaseSearchRequest baseSearchRequest) {

		String queryString = getQueryString(baseSearchRequest);

		if (!Validator.isBlank(queryString)) {
			solrQuery.setQuery(queryString);
		}
	}

	protected void setStatsRequests(
		SolrQuery solrQuery, BaseSearchRequest baseSearchRequest) {

		for (StatsRequest statsRequest : baseSearchRequest.getStatsRequests()) {
			_statsTranslator.populateRequest(solrQuery, statsRequest);
		}
	}

	protected String translate(BooleanClause<Filter> booleanClause) {
		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.add(
			booleanClause.getClause(), booleanClause.getBooleanClauseOccur());

		return _filterTranslator.translate(booleanFilter);
	}

	private void _add(
		Collection<String> filterQueries, BooleanQuery booleanQuery) {

		for (BooleanClause<Query> booleanClause : booleanQuery.clauses()) {
			Query booleanClauseQuery = booleanClause.getClause();

			_add(filterQueries, booleanClauseQuery.getPreBooleanFilter());

			if (booleanClauseQuery instanceof BooleanQuery) {
				_add(filterQueries, (BooleanQuery)booleanClauseQuery);
			}
		}
	}

	private void _add(Collection<String> filterQueries, Filter filter) {
		if (filter != null) {
			filterQueries.add(_filterTranslator.translate(filter));
		}
	}

	private void _addAll(
		List<String> filterQueries, String[] facetPostFilterQueries) {

		if (!ArrayUtil.isEmpty(facetPostFilterQueries)) {
			Collections.addAll(filterQueries, facetPostFilterQueries);
		}
	}

	@Reference(service = CompositeFacetProcessor.class)
	private FacetProcessor<SolrQuery> _facetProcessor;

	@Reference(target = "(search.engine.impl=Solr)")
	private FilterTranslator<String> _filterTranslator;

	@Reference(target = "(search.engine.impl=Solr)")
	private QueryTranslator<String> _queryTranslator;

	private final SolrQueryTranslator _solrQueryTranslator =
		new SolrQueryTranslator();

	@Reference
	private StatsTranslator _statsTranslator;

}