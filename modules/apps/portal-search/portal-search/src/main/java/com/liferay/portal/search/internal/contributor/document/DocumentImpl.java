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

package com.liferay.portal.search.internal.contributor.document;

import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.Field;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class DocumentImpl implements Document {

	public void addField(Field field) {
		_fields.put(field.getName(), field);
	}

	public Field getField(String name) {
		return _fields.get(name);
	}

	public Map<String, Field> getFields() {
		return Collections.unmodifiableMap(_fields);
	}

	public Object getFieldValue(String name) {
		Field field = _fields.get(name);

		if (field == null) {
			return null;
		}

		return field.getValue();
	}

	public List<Object> getFieldValues(String name) {
		Field field = _fields.get(name);

		if (field == null) {
			return null;
		}

		return field.getValues();
	}

	private final Map<String, Field> _fields = new HashMap<>();

}