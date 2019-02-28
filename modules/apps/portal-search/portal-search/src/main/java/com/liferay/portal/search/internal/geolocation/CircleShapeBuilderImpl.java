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
import com.liferay.portal.search.geolocation.GeoDistance;
import com.liferay.portal.search.geolocation.ShapeBuilderTranslator;

/**
 * @author Michael C. Han
 */
public class CircleShapeBuilderImpl
	extends ShapeBuilderImpl implements CircleShapeBuilder {

	public CircleShapeBuilderImpl(GeoDistance radius, Coordinate center) {
		_radius = radius;
		_center = center;
	}

	@Override
	public <T> T accept(ShapeBuilderTranslator<T> shapeBuilderTranslator) {
		return shapeBuilderTranslator.translate(this);
	}

	public Coordinate getCenter() {
		return _center;
	}

	public GeoDistance getRadius() {
		return _radius;
	}

	public void setCenter(Coordinate center) {
		_center = center;
	}

	public void setRadius(GeoDistance radius) {
		_radius = radius;
	}

	private Coordinate _center;
	private GeoDistance _radius;

}