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

package com.liferay.portal.search.geolocation;

import aQute.bnd.annotation.ProviderType;

import java.util.List;

/**
 * @author Wade Cao
 */
@ProviderType
public interface ShapeBuilders {

	public CircleShapeBuilder circleShapeBuilder(
		GeoDistance radius, Coordinate center);

	public Coordinate coordinate(double x, double y);

	public Coordinate coordinate(double x, double y, double z);

	public EnvelopeShapeBuilder envelopeShapeBuilder(
		Coordinate topLeft, Coordinate bottomRight);

	public GeoDistance geoDistance(double distance);

	public GeoDistance geoDistance(double distance, DistanceUnit distanceUnit);

	public GeoLocationPoint geoLocationPoint(double latitude, double longitude);

	public GeoLocationPoint geoLocationPoint(String geoHash);

	public CircleShapeBuilder.Builder getCircleShapeBuilderBuilder();

	public EnvelopeShapeBuilder.Builder getEnvelopeShapeBuilderBuilder();

	public GeometryCollectionShapeBuilder.Builder
		getGeometryCollectionShapeBuilderBuilder();

	public LineStringShapeBuilder.Builder getLineStringShapeBuilderBuilder();

	public MultipleLineStringShapeBuilder.Builder
		getMultipleLineStringShapeBuilderBuilder();

	public MultiPointShapeBuilder.Builder getMultiPointShapeBuilderBuilder();

	public MultiPolygonShapeBuilder.Builder
		getMultiPolygonShapeBuilderBuilder();

	public PointShapeBuilder.Builder getPointShapeBuilderBuilder();

	public PolygonShapeBuilder.Builder getPolygonShapeBuilderBuilder();

	public LineStringShapeBuilder lineStringShapeBuilder(
		List<Coordinate> coordinates);

	public MultiPointShapeBuilder multiPointShapeBuilder(
		List<Coordinate> coodinates);

	public MultiPolygonShapeBuilder multiPolygonShapeBuilder(
		Orientation orientation);

	public PointShapeBuilder pointShapeBuilder(Coordinate coordinate);

	public PolygonShapeBuilder polygonShapeBuilder(
		LineStringShapeBuilder shell, Orientation orientation);

}