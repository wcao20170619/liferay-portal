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
import com.liferay.portal.search.geolocation.MultipleLineStringShapeBuilder;
import com.liferay.portal.search.geolocation.ShapeBuilderTranslator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Michael C. Han
 */
public class MultipleLineStringShapeBuilderImpl
	extends ShapeBuilderImpl implements MultipleLineStringShapeBuilder {

	@Override
	public <T> T accept(ShapeBuilderTranslator<T> shapeBuilderTranslator) {
		return shapeBuilderTranslator.translate(this);
	}

	@Override
	public List<LineStringShapeBuilder> getLineStringShapeBuilders() {
		return Collections.unmodifiableList(_lineStringShapeBuilders);
	}

	protected MultipleLineStringShapeBuilderImpl() {
	}

	protected MultipleLineStringShapeBuilderImpl(
		MultipleLineStringShapeBuilderImpl multipleLineStringShapeBuilderImpl) {

		addCoordinates(multipleLineStringShapeBuilderImpl.getCoordinates());
		_lineStringShapeBuilders.addAll(
			multipleLineStringShapeBuilderImpl._lineStringShapeBuilders);
	}

	protected void addLineStringShapeBuilder(
		LineStringShapeBuilder lineStringShapeBuilder) {

		_lineStringShapeBuilders.add(lineStringShapeBuilder);
	}

	protected void addLineStringShapeBuilders(
		LineStringShapeBuilder... lineStringShapeBuilders) {

		Collections.addAll(_lineStringShapeBuilders, lineStringShapeBuilders);
	}

	protected void addLineStringShapeBuilders(
		List<LineStringShapeBuilder> lineStringShapeBuilders) {

		_lineStringShapeBuilders.addAll(lineStringShapeBuilders);
	}

	private final List<LineStringShapeBuilder> _lineStringShapeBuilders =
		new ArrayList<>();

}