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

package com.liferay.portal.search.internal.query;

import com.liferay.portal.kernel.search.geolocation.GeoDistance;
import com.liferay.portal.kernel.search.geolocation.GeoLocationPoint;
import com.liferay.portal.search.geolocation.ShapeBuilder;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.script.Script;

import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = Queries.class)
public class QueriesImpl implements Queries {

	@Override
	public Query booleanQuery() {
		return new BooleanQueryImpl();
	}

	@Override
	public Query boostingQuery(Query positiveQuery, Query negativeQuery) {
		return new BoostingQueryImpl(positiveQuery, negativeQuery);
	}

	@Override
	public Query commonTermsQuery(String field, String text) {
		return new CommonTermsQueryImpl(field, text);
	}

	@Override
	public Query constantScoreQuery(Query query) {
		return new ConstantScoreQueryImpl(query);
	}

	@Override
	public Query customQuery(Query delegate) {
		return new CustomQueryImpl(delegate);
	}

	@Override
	public Query dateRangeTermQuery(
		String field, boolean includesLower, boolean includesUpper,
		String startDate, String endDate) {

		return new DateRangeTermQueryImpl(
			field, includesLower, includesUpper, startDate, endDate);
	}

	@Override
	public Query disMaxQuery() {
		return new DisMaxQueryImpl();
	}

	@Override
	public Query existsQuery(String field) {
		return new ExistsQueryImpl(field);
	}

	@Override
	public Query functionScoreQuery(Query query) {
		return new FunctionScoreQueryImpl(query);
	}

	@Override
	public Query fuzzyQuery(String field, String value) {
		return new FuzzyQueryImpl(field, value);
	}

	@Override
	public Query geoBoundingBoxQuery(
		String field, GeoLocationPoint topLeftGeoLocationPoint,
		GeoLocationPoint bottomRightGeoLocationPoint) {

		return new GeoBoundingBoxQueryImpl(
			field, topLeftGeoLocationPoint, bottomRightGeoLocationPoint);
	}

	@Override
	public Query geoDistanceQuery(
		String field, GeoLocationPoint pinGeoLocationPoint,
		GeoDistance geoDistance) {

		return new GeoDistanceQueryImpl(
			field, pinGeoLocationPoint, geoDistance);
	}

	@Override
	public Query geoDistanceRangeQuery(
		String field, boolean includesLower, boolean includesUpper,
		GeoDistance lowerBoundGeoDistance, GeoLocationPoint pinGeoLocationPoint,
		GeoDistance upperBoundGeoDistance) {

		return new GeoDistanceRangeQueryImpl(
			field, includesLower, includesUpper, lowerBoundGeoDistance,
			pinGeoLocationPoint, upperBoundGeoDistance);
	}

	@Override
	public Query geoPolygonQuery(String field) {
		return new GeoPolygonQueryImpl(field);
	}

	@Override
	public Query geoShapeQuery(String field, ShapeBuilder shapeBuilder) {
		return new GeoShapeQueryImpl(field, shapeBuilder);
	}

	@Override
	public Query geoShapeQuery(
		String field, String indexedShapeId, String indexedShapeType) {

		return new GeoShapeQueryImpl(field, indexedShapeId, indexedShapeType);
	}

	@Override
	public Query idsQuery() {
		return new IdsQueryImpl();
	}

	@Override
	public Query matchAllQuery() {
		return new MatchAllQueryImpl();
	}

	@Override
	public Query matchPhrasePrefixQuery(String field, Object value) {
		return new MatchPhrasePrefixQueryImpl(field, value);
	}

	@Override
	public Query matchPhraseQuery(String field, Object value) {
		return new MatchPhraseQueryImpl(field, value);
	}

	@Override
	public Query matchQuery(String field, Object value) {
		return new MatchQueryImpl(field, value);
	}

	@Override
	public Query moreLikeThisQuery(List<String> likeTexts) {
		return new MoreLikeThisQueryImpl(likeTexts);
	}

	@Override
	public Query moreLikeThisQuery(String... likeTexts) {
		return new MoreLikeThisQueryImpl(likeTexts);
	}

	@Override
	public Query multiMatchQuery(Object value, Set<String> fields) {
		return new MultiMatchQueryImpl(value, fields);
	}

	@Override
	public Query multiMatchQuery(Object value, String... fields) {
		return new MultiMatchQueryImpl(value, fields);
	}

	@Override
	public Query nestedQuery(String path, Query query) {
		return new NestedQueryImpl(path, query);
	}

	@Override
	public Query percolateQuery(String field, List<String> documentJSONs) {
		return new PercolateQueryImpl(field, documentJSONs);
	}

	@Override
	public Query prefixQuery(String field, String prefix) {
		return new PrefixQueryImpl(field, prefix);
	}

	@Override
	public Query rangeTermQuery(
		String field, boolean includesLower, boolean includesUpper) {

		return new RangeTermQueryImpl(field, includesLower, includesUpper);
	}

	@Override
	public Query rangeTermQuery(
		String field, boolean includesLower, boolean includesUpper,
		Object lowerBound, Object upperBound) {

		return new RangeTermQueryImpl(
			field, includesLower, includesUpper, lowerBound, upperBound);
	}

	@Override
	public Query regexQuery(String field, String regex) {
		return new RegexQueryImpl(field, regex);
	}

	@Override
	public Query scriptQuery(Script script) {
		return new ScriptQueryImpl(script);
	}

	@Override
	public Query simpleStringQuery(String query) {
		return new SimpleStringQueryImpl(query);
	}

	@Override
	public Query stringQuery(String query) {
		return new StringQueryImpl(query);
	}

	@Override
	public Query termQuery(String field, Object value) {
		return new TermQueryImpl(field, value);
	}

	@Override
	public Query termsQuery(String field) {
		return new TermsQueryImpl(field);
	}

	@Override
	public Query termsSetQuery(String fieldName, List<Object> values) {
		return new TermsSetQueryImpl(fieldName, values);
	}

	@Override
	public Query wildcardQuery(String field, String value) {
		return new WildcardQueryImpl(field, value);
	}

}