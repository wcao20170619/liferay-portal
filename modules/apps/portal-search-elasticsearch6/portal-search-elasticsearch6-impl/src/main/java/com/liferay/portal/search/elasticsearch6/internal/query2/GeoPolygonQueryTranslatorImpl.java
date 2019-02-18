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

package com.liferay.portal.search.elasticsearch6.internal.query2;

import com.liferay.portal.kernel.search.geolocation.GeoLocationPoint;
import com.liferay.portal.search.elasticsearch6.internal.query2.geolocation.GeoValidationMethodTranslator;
import com.liferay.portal.search.query.GeoPolygonQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.GeoPolygonQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = GeoPolygonQueryTranslator.class)
public class GeoPolygonQueryTranslatorImpl
	implements GeoPolygonQueryTranslator {

	@Override
	public QueryBuilder translate(GeoPolygonQuery geoPolygonQuery) {
		Set<GeoLocationPoint> geoLocationPoints =
			geoPolygonQuery.getGeoLocationPoints();

		List<GeoPoint> geoPoints = new ArrayList<>(geoLocationPoints.size());

		geoLocationPoints.forEach(
			geoLocationPoint -> geoPoints.add(
				new GeoPoint(
					geoLocationPoint.getLatitude(),
					geoLocationPoint.getLongitude())));

		GeoPolygonQueryBuilder geoPolygonQueryBuilder =
			QueryBuilders.geoPolygonQuery(
				geoPolygonQuery.getField(), geoPoints);

		if (geoPolygonQuery.getGeoValidationMethod() != null) {
			geoPolygonQueryBuilder.setValidationMethod(
				_geoValidationMethodTranslator.translate(
					geoPolygonQuery.getGeoValidationMethod()));
		}

		if (geoPolygonQuery.getIgnoreUnmapped() != null) {
			geoPolygonQueryBuilder.ignoreUnmapped(
				geoPolygonQuery.getIgnoreUnmapped());
		}

		return geoPolygonQueryBuilder;
	}

	private final GeoValidationMethodTranslator _geoValidationMethodTranslator =
		new GeoValidationMethodTranslator();

}