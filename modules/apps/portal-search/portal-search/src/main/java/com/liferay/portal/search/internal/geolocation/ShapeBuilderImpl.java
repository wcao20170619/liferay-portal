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

import com.liferay.portal.search.geolocation.Coordinate;
import com.liferay.portal.search.geolocation.ShapeBuilder;
import com.liferay.portal.search.geolocation.ShapeBuilderTranslator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Michael C. Han
 */
public abstract class ShapeBuilderImpl implements ShapeBuilder {

	@Override
	public abstract <T> T accept(
		ShapeBuilderTranslator<T> shapeBuilderTranslator);

	@Override
	public List<Coordinate> getCoordinates() {
		return Collections.unmodifiableList(_coordinates);
	}

	protected void addCoordinate(Coordinate coordinate) {
		_coordinates.add(coordinate);
	}

	protected void addCoordinates(Coordinate... coordinates) {
		Collections.addAll(_coordinates, coordinates);
	}

	protected void addCoordinates(List<Coordinate> coordinates) {
		_coordinates.addAll(coordinates);
	}

	private final List<Coordinate> _coordinates = new ArrayList<>();

}