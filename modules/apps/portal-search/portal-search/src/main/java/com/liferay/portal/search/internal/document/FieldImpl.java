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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author André de Oliveira
 */
public class FieldImpl implements Field {

	public FieldImpl() {
		_values = new ArrayList(1);
	}

	public FieldImpl(FieldImpl field) {
		_name = field._name;
		_values = new ArrayList(field._values);
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public List<Object> getValues() {
		return Collections.unmodifiableList(_values);
	}

	@Override
	public String toString() {
		if (_values.size() == 1) {
			return String.valueOf(_values.get(0));
		}

		return String.valueOf(_values);
	}

	protected void setName(String name) {
		_name = name;
	}

	protected void setValues(Collection<Object> values) {
		_values = new ArrayList(values);
	}

	private String _name;
	private List<Object> _values;

}