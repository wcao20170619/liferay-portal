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

import com.liferay.portal.search.geolocation.MultiPolygonShapeBuilder;
import com.liferay.portal.search.geolocation.Orientation;
import com.liferay.portal.search.geolocation.PolygonShapeBuilder;
import com.liferay.portal.search.geolocation.ShapeBuilderTranslator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Michael C. Han
 */
public class MultiPolygonShapeBuilderImpl
	extends ShapeBuilderImpl implements MultiPolygonShapeBuilder {

	@Override
	public <T> T accept(ShapeBuilderTranslator<T> shapeBuilderTranslator) {
		return shapeBuilderTranslator.translate(this);
	}

	public Orientation getOrientation() {
		return _orientation;
	}

	@Override
	public List<PolygonShapeBuilder> getPolygonShapeBuilders() {
		return Collections.unmodifiableList(_polygonShapeBuilders);
	}

	protected MultiPolygonShapeBuilderImpl() {
	}

	protected MultiPolygonShapeBuilderImpl(
		MultiPolygonShapeBuilderImpl multiPolygonShapeBuilderImpl) {

		addCoordinates(multiPolygonShapeBuilderImpl.getCoordinates());
		_orientation = multiPolygonShapeBuilderImpl._orientation;
		_polygonShapeBuilders.addAll(
			multiPolygonShapeBuilderImpl._polygonShapeBuilders);
	}

	protected void addPolygonShapeBuilder(
		PolygonShapeBuilder polygonShapeBuilder) {

		_polygonShapeBuilders.add(polygonShapeBuilder);
	}

	protected void addPolygonShapeBuilders(
		PolygonShapeBuilder... polygonShapeBuilders) {

		Collections.addAll(_polygonShapeBuilders, polygonShapeBuilders);
	}

	protected void setOrientation(Orientation orientation) {
		_orientation = orientation;
	}

	private Orientation _orientation;
	private final List<PolygonShapeBuilder> _polygonShapeBuilders =
		new ArrayList<>();

}