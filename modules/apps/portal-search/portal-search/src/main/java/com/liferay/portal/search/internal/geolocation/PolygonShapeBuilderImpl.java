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

import com.liferay.portal.search.geolocation.LineStringShapeBuilder;
import com.liferay.portal.search.geolocation.Orientation;
import com.liferay.portal.search.geolocation.PolygonShapeBuilder;
import com.liferay.portal.search.geolocation.ShapeBuilderTranslator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Michael C. Han
 */
public class PolygonShapeBuilderImpl
	extends ShapeBuilderImpl implements PolygonShapeBuilder {

	@Override
	public <T> T accept(ShapeBuilderTranslator<T> shapeBuilderTranslator) {
		return shapeBuilderTranslator.translate(this);
	}

	@Override
	public List<LineStringShapeBuilder> getHolesLineStringShapeBuilders() {
		return Collections.unmodifiableList(_holesLineStringShapeBuilders);
	}

	@Override
	public Orientation getOrientation() {
		return _orientation;
	}

	@Override
	public LineStringShapeBuilder getShell() {
		return _shell;
	}

	protected PolygonShapeBuilderImpl() {
	}

	protected PolygonShapeBuilderImpl(
		PolygonShapeBuilderImpl polygonShapeBuilderImpl) {

		addCoordinates(polygonShapeBuilderImpl.getCoordinates());
		_holesLineStringShapeBuilders.addAll(
			polygonShapeBuilderImpl._holesLineStringShapeBuilders);
		_orientation = polygonShapeBuilderImpl._orientation;
		_shell = polygonShapeBuilderImpl._shell;
	}

	protected void addHolesLineStringShapeBuilder(
		LineStringShapeBuilder lineStringShapeBuilder) {

		_holesLineStringShapeBuilders.add(lineStringShapeBuilder);
	}

	protected void addHolesLineStringShapeBuilders(
		LineStringShapeBuilder... lineStringShapeBuilders) {

		Collections.addAll(
			_holesLineStringShapeBuilders, lineStringShapeBuilders);
	}

	protected void setOrientation(Orientation orientation) {
		_orientation = orientation;
	}

	protected void setShell(LineStringShapeBuilder shell) {
		_shell = shell;
	}

	private final List<LineStringShapeBuilder> _holesLineStringShapeBuilders =
		new ArrayList<>();
	private Orientation _orientation;
	private LineStringShapeBuilder _shell;

}