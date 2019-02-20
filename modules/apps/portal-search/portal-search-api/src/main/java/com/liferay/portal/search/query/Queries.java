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

package com.liferay.portal.search.query;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.search.geolocation.GeoDistance;
import com.liferay.portal.kernel.search.geolocation.GeoLocationPoint;
import com.liferay.portal.search.geolocation.ShapeBuilder;
import com.liferay.portal.search.script.Script;

import java.util.List;
import java.util.Set;

/**
 * @author Wade Cao
 */
@ProviderType
public interface Queries {

	public Query booleanQuery();

	public Query boostingQuery(Query positiveQuery, Query negativeQuery);

	public Query commonTermsQuery(String field, String text);

	public Query constantScoreQuery(Query query);

	public Query customQuery(Query delegate);

	public Query dateRangeTermQuery(
		String field, boolean includesLower, boolean includesUpper,
		String startDate, String endDate);

	public Query disMaxQuery();

	public Query existsQuery(String field);

	public Query functionScoreQuery(Query query);

	public Query fuzzyQuery(String field, String value);

	public Query geoBoundingBoxQuery(
		String field, GeoLocationPoint topLeftGeoLocationPoint,
		GeoLocationPoint bottomRightGeoLocationPoint);

	public Query geoDistanceQuery(
		String field, GeoLocationPoint pinGeoLocationPoint,
		GeoDistance geoDistance);

	public Query geoDistanceRangeQuery(
		String field, boolean includesLower, boolean includesUpper,
		GeoDistance lowerBoundGeoDistance, GeoLocationPoint pinGeoLocationPoint,
		GeoDistance upperBoundGeoDistance);

	public Query geoPolygonQuery(String field);

	public Query geoShapeQuery(String field, ShapeBuilder shapeBuilder);

	public Query geoShapeQuery(
		String field, String indexedShapeId, String indexedShapeType);

	public Query idsQuery();

	public Query matchAllQuery();

	public Query matchPhrasePrefixQuery(String field, Object value);

	public Query matchPhraseQuery(String field, Object value);

	public Query matchQuery(String field, Object value);

	public Query moreLikeThisQuery(List<String> likeTexts);

	public Query moreLikeThisQuery(String... likeTexts);

	public Query multiMatchQuery(Object value, Set<String> fields);

	public Query multiMatchQuery(Object value, String... fields);

	public Query nestedQuery(String path, Query query);

	public Query percolateQuery(String field, List<String> documentJSONs);

	public Query prefixQuery(String field, String prefix);

	public Query rangeTermQuery(
		String field, boolean includesLower, boolean includesUpper);

	public Query rangeTermQuery(
		String field, boolean includesLower, boolean includesUpper,
		Object lowerBound, Object upperBound);

	public Query regexQuery(String field, String regex);

	public Query scriptQuery(Script script);

	public Query simpleStringQuery(String query);

	public Query stringQuery(String query);

	public Query termQuery(String field, Object value);

	public Query termsQuery(String field);

	public Query termsSetQuery(String fieldName, List<Object> values);

	public Query wildcardQuery(String field, String value);

}