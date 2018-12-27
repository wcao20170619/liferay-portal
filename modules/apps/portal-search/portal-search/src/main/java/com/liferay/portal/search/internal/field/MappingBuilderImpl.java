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

package com.liferay.portal.search.internal.field;

import com.liferay.portal.search.field.Mapping;
import com.liferay.portal.search.field.MappingBuilder;

/**
 * @author André de Oliveira
 */
public class MappingBuilderImpl implements MappingBuilder {

	@Override
	public MappingBuilder analyzer(String analyzer) {
		_mappingImpl.setAnalyzer(analyzer);

		return this;
	}

	@Override
	public Mapping build() {
		return new MappingImpl(_mappingImpl);
	}

	@Override
	public MappingBuilder format(String format) {
		_mappingImpl.setFormat(format);

		return this;
	}

	@Override
	public MappingBuilder store(boolean store) {
		_mappingImpl.setStore(store);

		return this;
	}

	@Override
	public MappingBuilder termVector(String termVector) {
		_mappingImpl.setTermVector(termVector);

		return this;
	}

	@Override
	public MappingBuilder type(String type) {
		_mappingImpl.setType(type);

		return this;
	}

	private final MappingImpl _mappingImpl = new MappingImpl();

}