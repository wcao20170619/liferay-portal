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
 * @author Michael C. Han
 */
@ProviderType
public interface MultipleLineStringShapeBuilder extends ShapeBuilder {

	@Override
	public <T> T accept(ShapeBuilderTranslator<T> shapeBuilderTranslator);

	public List<LineStringShapeBuilder> getLineStringShapeBuilders();

	@ProviderType
	public interface Builder extends ShapeBuilder.Builder {

		public MultipleLineStringShapeBuilder build();

		public void lineStringShapeBuilder(
			LineStringShapeBuilder lineStringShapeBuilder);

		public void lineStringShapeBuilders(
			LineStringShapeBuilder... lineStringShapeBuilders);

		public void lineStringShapeBuilders(
			List<LineStringShapeBuilder> lineStringShapeBuilders);

	}

}