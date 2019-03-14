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

import com.liferay.portal.search.geolocation.GeometryCollectionShapeBuilder;
import com.liferay.portal.search.geolocation.ShapeBuilder;
import com.liferay.portal.search.geolocation.ShapeBuilderTranslator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Michael C. Han
 */
public class GeometryCollectionShapeBuilderImpl
	extends ShapeBuilderImpl implements GeometryCollectionShapeBuilder {

	@Override
	public <T> T accept(ShapeBuilderTranslator<T> shapeBuilderTranslator) {
		return shapeBuilderTranslator.translate(this);
	}

	@Override
	public List<ShapeBuilder> getShapeBuilders() {
		return Collections.unmodifiableList(_shapeBuilders);
	}

	protected GeometryCollectionShapeBuilderImpl() {
	}

	protected GeometryCollectionShapeBuilderImpl(
		GeometryCollectionShapeBuilderImpl geometryCollectionShapeBuilderImpl) {

		addCoordinates(geometryCollectionShapeBuilderImpl.getCoordinates());
		_shapeBuilders.addAll(
			geometryCollectionShapeBuilderImpl._shapeBuilders);
	}

	protected void addShapeBuilder(ShapeBuilder shapeBuilder) {
		_shapeBuilders.add(shapeBuilder);
	}

	protected void addShapeBuilders(ShapeBuilder... shapeBuilders) {
		Collections.addAll(_shapeBuilders, shapeBuilders);
	}

	private final List<ShapeBuilder> _shapeBuilders = new ArrayList<>();

}