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
import com.liferay.portal.search.geolocation.EnvelopeShapeBuilder;
import com.liferay.portal.search.geolocation.ShapeBuilderTranslator;

/**
 * @author Michael C. Han
 */
public class EnvelopeShapeBuilderImpl
	extends ShapeBuilderImpl implements EnvelopeShapeBuilder {

	@Override
	public <T> T accept(ShapeBuilderTranslator<T> shapeBuilderTranslator) {
		return shapeBuilderTranslator.translate(this);
	}

	@Override
	public Coordinate getBottomRight() {
		return _bottomRight;
	}

	@Override
	public Coordinate getTopLeft() {
		return _topLeft;
	}

	protected EnvelopeShapeBuilderImpl() {
	}

	protected EnvelopeShapeBuilderImpl(
		EnvelopeShapeBuilderImpl envelopeShapeBuilderImpl) {

		addCoordinates(envelopeShapeBuilderImpl.getCoordinates());
		_topLeft = envelopeShapeBuilderImpl._topLeft;
		_bottomRight = envelopeShapeBuilderImpl._bottomRight;
	}

	protected void setBottomRight(Coordinate bottomRight) {
		_bottomRight = bottomRight;
	}

	protected void setTopLeft(Coordinate topLeft) {
		_topLeft = topLeft;
	}

	private Coordinate _bottomRight;
	private Coordinate _topLeft;

}