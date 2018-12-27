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

package com.liferay.portal.search.internal.document;

import com.liferay.portal.search.document.Field;
import com.liferay.portal.search.document.FieldBuilder;

import java.util.Arrays;

/**
 * @author André de Oliveira
 */
public class FieldBuilderImpl implements FieldBuilder {

	@Override
	public Field build() {
		return new FieldImpl(_fieldImpl);
	}

	@Override
	public FieldBuilder name(String name) {
		_fieldImpl.setName(name);

		return this;
	}

	@Override
	public FieldBuilder value(Object value) {
		_fieldImpl.setValues(Arrays.asList(value));

		return this;
	}

	@Override
	public FieldBuilder values(Object[] values) {
		_fieldImpl.setValues(Arrays.asList(values));

		return this;
	}

	private final FieldImpl _fieldImpl = new FieldImpl();

}