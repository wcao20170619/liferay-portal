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

package com.liferay.portal.search.internal.geolocation;

import com.liferay.portal.search.geolocation.CircleShapeBuilder;
import com.liferay.portal.search.geolocation.Coordinate;
import com.liferay.portal.search.geolocation.DistanceUnit;
import com.liferay.portal.search.geolocation.EnvelopeShapeBuilder;
import com.liferay.portal.search.geolocation.GeoDistance;
import com.liferay.portal.search.geolocation.GeoLocationPoint;
import com.liferay.portal.search.geolocation.GeometryCollectionShapeBuilder;
import com.liferay.portal.search.geolocation.LineStringShapeBuilder;
import com.liferay.portal.search.geolocation.MultiPointShapeBuilder;
import com.liferay.portal.search.geolocation.MultiPolygonShapeBuilder;
import com.liferay.portal.search.geolocation.MultipleLineStringShapeBuilder;
import com.liferay.portal.search.geolocation.Orientation;
import com.liferay.portal.search.geolocation.PointShapeBuilder;
import com.liferay.portal.search.geolocation.PolygonShapeBuilder;
import com.liferay.portal.search.geolocation.ShapeBuilder;
import com.liferay.portal.search.geolocation.ShapeBuilders;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = ShapeBuilders.class)
public class ShapeBuildersImpl implements ShapeBuilders {

	@Override
	public CircleShapeBuilder circleShapeBuilder(
		GeoDistance radius, Coordinate center) {

		CircleShapeBuilder.Builder builder = getCircleShapeBuilderBuilder();

		builder.center(center);
		builder.radius(radius);

		return builder.build();
	}

	@Override
	public Coordinate coordinate(double x, double y) {
		return new CoordinateImpl(x, y);
	}

	@Override
	public Coordinate coordinate(double x, double y, double z) {
		return new CoordinateImpl(x, y, z);
	}

	@Override
	public EnvelopeShapeBuilder envelopeShapeBuilder(
		Coordinate topLeft, Coordinate bottomRight) {

		EnvelopeShapeBuilder.Builder builder = getEnvelopeShapeBuilderBuilder();

		builder.topLeft(topLeft);
		builder.bottomRight(bottomRight);

		return builder.build();
	}

	@Override
	public GeoDistance geoDistance(double distance) {
		return new GeoDistanceImpl(distance);
	}

	@Override
	public GeoDistance geoDistance(double distance, DistanceUnit distanceUnit) {
		return new GeoDistanceImpl(distance, distanceUnit);
	}

	@Override
	public GeoLocationPoint geoLocationPoint(
		double latitude, double longitude) {

		return new GeoLocationPointImpl(latitude, longitude);
	}

	@Override
	public GeoLocationPoint geoLocationPoint(String geoHash) {
		return new GeoLocationPointImpl(geoHash);
	}

	@Override
	public CircleShapeBuilder.Builder getCircleShapeBuilderBuilder() {
		CircleShapeBuilder.Builder builder = new CircleShapeBuilder.Builder() {

			@Override
			public CircleShapeBuilder build() {
				return new CircleShapeBuilderImpl(_circleShapeBuilderImpl);
			}

			@Override
			public void center(Coordinate center) {
				_circleShapeBuilderImpl.setCenter(center);
			}

			@Override
			public void coordinate(Coordinate coordinate) {
				_circleShapeBuilderImpl.addCoordinate(coordinate);
			}

			@Override
			public void coordinates(Coordinate... coordinates) {
				_circleShapeBuilderImpl.addCoordinates(coordinates);
			}

			@Override
			public void coordinates(List<Coordinate> coordinates) {
				_circleShapeBuilderImpl.addCoordinates(coordinates);
			}

			@Override
			public void radius(GeoDistance radius) {
				_circleShapeBuilderImpl.setRadius(radius);
			}

			private final CircleShapeBuilderImpl _circleShapeBuilderImpl =
				new CircleShapeBuilderImpl();

		};

		return builder;
	}

	@Override
	public EnvelopeShapeBuilder.Builder getEnvelopeShapeBuilderBuilder() {
		EnvelopeShapeBuilder.Builder builder =
			new EnvelopeShapeBuilder.Builder() {

				@Override
				public void bottomRight(Coordinate bottomRight) {
					_envelopeShapeBuilderImpl.setBottomRight(bottomRight);
				}

				@Override
				public EnvelopeShapeBuilder build() {
					return new EnvelopeShapeBuilderImpl(
						_envelopeShapeBuilderImpl);
				}

				@Override
				public void coordinate(Coordinate coordinate) {
					_envelopeShapeBuilderImpl.addCoordinate(coordinate);
				}

				@Override
				public void coordinates(Coordinate... coordinates) {
					_envelopeShapeBuilderImpl.addCoordinates(coordinates);
				}

				@Override
				public void coordinates(List<Coordinate> coordinates) {
					_envelopeShapeBuilderImpl.addCoordinates(coordinates);
				}

				@Override
				public void topLeft(Coordinate topLeft) {
					_envelopeShapeBuilderImpl.setTopLeft(topLeft);
				}

				private final EnvelopeShapeBuilderImpl
					_envelopeShapeBuilderImpl = new EnvelopeShapeBuilderImpl();

			};

		return builder;
	}

	@Override
	public GeometryCollectionShapeBuilder.Builder
		getGeometryCollectionShapeBuilderBuilder() {

		GeometryCollectionShapeBuilder.Builder builder =
			new GeometryCollectionShapeBuilder.Builder() {

				@Override
				public GeometryCollectionShapeBuilder build() {
					return new GeometryCollectionShapeBuilderImpl(
						_geometryCollectionShapeBuilderImpl);
				}

				@Override
				public void coordinate(Coordinate coordinate) {
					_geometryCollectionShapeBuilderImpl.addCoordinate(
						coordinate);
				}

				@Override
				public void coordinates(Coordinate... coordinates) {
					_geometryCollectionShapeBuilderImpl.addCoordinates(
						coordinates);
				}

				@Override
				public void coordinates(List<Coordinate> coordinates) {
					_geometryCollectionShapeBuilderImpl.addCoordinates(
						coordinates);
				}

				@Override
				public void shapeBuilder(ShapeBuilder shapeBuilder) {
					_geometryCollectionShapeBuilderImpl.addShapeBuilder(
						shapeBuilder);
				}

				@Override
				public void shapeBuilders(ShapeBuilder... shapeBuilders) {
					_geometryCollectionShapeBuilderImpl.addShapeBuilders(
						shapeBuilders);
				}

				private final GeometryCollectionShapeBuilderImpl
					_geometryCollectionShapeBuilderImpl =
						new GeometryCollectionShapeBuilderImpl();

			};

		return builder;
	}

	@Override
	public LineStringShapeBuilder.Builder getLineStringShapeBuilderBuilder() {
		LineStringShapeBuilder.Builder builder =
			new LineStringShapeBuilder.Builder() {

				@Override
				public LineStringShapeBuilder build() {
					return new LineStringShapeBuilderImpl(
						_lineStringShapeBuilderImpl);
				}

				@Override
				public void coordinate(Coordinate coordinate) {
					_lineStringShapeBuilderImpl.addCoordinate(coordinate);
				}

				@Override
				public void coordinates(Coordinate... coordinates) {
					_lineStringShapeBuilderImpl.addCoordinates(coordinates);
				}

				@Override
				public void coordinates(List<Coordinate> coordinates) {
					_lineStringShapeBuilderImpl.addCoordinates(coordinates);
				}

				private final LineStringShapeBuilderImpl
					_lineStringShapeBuilderImpl =
						new LineStringShapeBuilderImpl();

			};

		return builder;
	}

	@Override
	public MultipleLineStringShapeBuilder.Builder
		getMultipleLineStringShapeBuilderBuilder() {

		MultipleLineStringShapeBuilder.Builder builder =
			new MultipleLineStringShapeBuilder.Builder() {

				@Override
				public MultipleLineStringShapeBuilder build() {
					return new MultipleLineStringShapeBuilderImpl(
						_multipleLineStringShapeBuilderImpl);
				}

				@Override
				public void coordinate(Coordinate coordinate) {
					_multipleLineStringShapeBuilderImpl.addCoordinate(
						coordinate);
				}

				@Override
				public void coordinates(Coordinate... coordinates) {
					_multipleLineStringShapeBuilderImpl.addCoordinates(
						coordinates);
				}

				@Override
				public void coordinates(List<Coordinate> coordinates) {
					_multipleLineStringShapeBuilderImpl.addCoordinates(
						coordinates);
				}

				@Override
				public void lineStringShapeBuilder(
					LineStringShapeBuilder lineStringShapeBuilder) {

					_multipleLineStringShapeBuilderImpl.
						addLineStringShapeBuilder(lineStringShapeBuilder);
				}

				@Override
				public void lineStringShapeBuilders(
					LineStringShapeBuilder... lineStringShapeBuilders) {

					_multipleLineStringShapeBuilderImpl.
						addLineStringShapeBuilders(lineStringShapeBuilders);
				}

				@Override
				public void lineStringShapeBuilders(
					List<LineStringShapeBuilder> lineStringShapeBuilders) {

					_multipleLineStringShapeBuilderImpl.
						addLineStringShapeBuilders(lineStringShapeBuilders);
				}

				private final MultipleLineStringShapeBuilderImpl
					_multipleLineStringShapeBuilderImpl =
						new MultipleLineStringShapeBuilderImpl();

			};

		return builder;
	}

	@Override
	public MultiPointShapeBuilder.Builder getMultiPointShapeBuilderBuilder() {
		MultiPointShapeBuilder.Builder builder =
			new MultiPointShapeBuilder.Builder() {

				@Override
				public MultiPointShapeBuilder build() {
					return new MultiPointShapeBuilderImpl(
						_multiPointShapeBuilderImpl);
				}

				@Override
				public void coordinate(Coordinate coordinate) {
					_multiPointShapeBuilderImpl.addCoordinate(coordinate);
				}

				@Override
				public void coordinates(Coordinate... coordinates) {
					_multiPointShapeBuilderImpl.addCoordinates(coordinates);
				}

				@Override
				public void coordinates(List<Coordinate> coordinates) {
					_multiPointShapeBuilderImpl.addCoordinates(coordinates);
				}

				private final MultiPointShapeBuilderImpl
					_multiPointShapeBuilderImpl =
						new MultiPointShapeBuilderImpl();

			};

		return builder;
	}

	@Override
	public MultiPolygonShapeBuilder.Builder
		getMultiPolygonShapeBuilderBuilder() {

		MultiPolygonShapeBuilder.Builder builder =
			new MultiPolygonShapeBuilder.Builder() {

				@Override
				public MultiPolygonShapeBuilder build() {
					return new MultiPolygonShapeBuilderImpl(
						_multiPolygonShapeBuilderImpl);
				}

				@Override
				public void coordinate(Coordinate coordinate) {
					_multiPolygonShapeBuilderImpl.addCoordinate(coordinate);
				}

				@Override
				public void coordinates(Coordinate... coordinates) {
					_multiPolygonShapeBuilderImpl.addCoordinates(coordinates);
				}

				@Override
				public void coordinates(List<Coordinate> coordinates) {
					_multiPolygonShapeBuilderImpl.addCoordinates(coordinates);
				}

				@Override
				public void orientation(Orientation orientation) {
					_multiPolygonShapeBuilderImpl.setOrientation(orientation);
				}

				@Override
				public void polygonShapeBuilder(
					PolygonShapeBuilder polygonShapeBuilder) {

					_multiPolygonShapeBuilderImpl.addPolygonShapeBuilder(
						polygonShapeBuilder);
				}

				@Override
				public void polygonShapeBuilders(
					PolygonShapeBuilder... polygonShapeBuilders) {

					_multiPolygonShapeBuilderImpl.addPolygonShapeBuilders(
						polygonShapeBuilders);
				}

				private final MultiPolygonShapeBuilderImpl
					_multiPolygonShapeBuilderImpl =
						new MultiPolygonShapeBuilderImpl();

			};

		return builder;
	}

	@Override
	public PointShapeBuilder.Builder getPointShapeBuilderBuilder() {
		PointShapeBuilder.Builder builder = new PointShapeBuilder.Builder() {

			@Override
			public PointShapeBuilder build() {
				return new PointShapeBuilderImpl(_pointShapeBuilderImpl);
			}

			@Override
			public void coordinate(Coordinate coordinate) {
				_pointShapeBuilderImpl.addCoordinate(coordinate);
			}

			@Override
			public void coordinates(Coordinate... coordinates) {
				_pointShapeBuilderImpl.addCoordinates(coordinates);
			}

			@Override
			public void coordinates(List<Coordinate> coordinates) {
				_pointShapeBuilderImpl.addCoordinates(coordinates);
			}

			private final PointShapeBuilderImpl _pointShapeBuilderImpl =
				new PointShapeBuilderImpl();

		};

		return builder;
	}

	@Override
	public PolygonShapeBuilder.Builder getPolygonShapeBuilderBuilder() {
		PolygonShapeBuilder.Builder builder =
			new PolygonShapeBuilder.Builder() {

				@Override
				public PolygonShapeBuilder build() {
					return new PolygonShapeBuilderImpl(
						_polygonShapeBuilderImpl);
				}

				@Override
				public void coordinate(Coordinate coordinate) {
					_polygonShapeBuilderImpl.addCoordinate(coordinate);
				}

				@Override
				public void coordinates(Coordinate... coordinates) {
					_polygonShapeBuilderImpl.addCoordinates(coordinates);
				}

				@Override
				public void coordinates(List<Coordinate> coordinates) {
					_polygonShapeBuilderImpl.addCoordinates(coordinates);
				}

				@Override
				public void holesLineStringShapeBuilder(
					LineStringShapeBuilder lineStringShapeBuilder) {

					_polygonShapeBuilderImpl.addHolesLineStringShapeBuilder(
						lineStringShapeBuilder);
				}

				@Override
				public void holesLineStringShapeBuilders(
					LineStringShapeBuilder... lineStringShapeBuilders) {

					_polygonShapeBuilderImpl.addHolesLineStringShapeBuilders(
						lineStringShapeBuilders);
				}

				@Override
				public void orientation(Orientation orientation) {
					_polygonShapeBuilderImpl.setOrientation(orientation);
				}

				@Override
				public void shell(LineStringShapeBuilder shell) {
					_polygonShapeBuilderImpl.setShell(shell);
				}

				private final PolygonShapeBuilderImpl _polygonShapeBuilderImpl =
					new PolygonShapeBuilderImpl();

			};

		return builder;
	}

	@Override
	public LineStringShapeBuilder lineStringShapeBuilder(
		List<Coordinate> coordinates) {

		LineStringShapeBuilder.Builder builder =
			getLineStringShapeBuilderBuilder();

		builder.coordinates(coordinates);

		return builder.build();
	}

	@Override
	public MultiPointShapeBuilder multiPointShapeBuilder(
		List<Coordinate> coodinates) {

		MultiPointShapeBuilder.Builder builder =
			getMultiPointShapeBuilderBuilder();

		builder.coordinates(coodinates);

		return builder.build();
	}

	@Override
	public MultiPolygonShapeBuilder multiPolygonShapeBuilder(
		Orientation orientation) {

		MultiPolygonShapeBuilder.Builder builder =
			getMultiPolygonShapeBuilderBuilder();

		builder.orientation(orientation);

		return builder.build();
	}

	@Override
	public PointShapeBuilder pointShapeBuilder(Coordinate coordinate) {
		PointShapeBuilder.Builder builder = getPointShapeBuilderBuilder();

		builder.coordinate(coordinate);

		return builder.build();
	}

	@Override
	public PolygonShapeBuilder polygonShapeBuilder(
		LineStringShapeBuilder shell, Orientation orientation) {

		PolygonShapeBuilder.Builder builder = getPolygonShapeBuilderBuilder();

		builder.shell(shell);
		builder.orientation(orientation);

		return builder.build();
	}

}