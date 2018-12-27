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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wade Cao
 */
public class MappingImpl implements Mapping {

	public MappingImpl() {
	}

	public MappingImpl(MappingImpl mappingImpl) {
		_analyzer = mappingImpl._analyzer;
		_format = mappingImpl._format;
		_store = mappingImpl._store;
		_termVector = mappingImpl._termVector;
		_type = mappingImpl._type;
	}

	public void addField(String name, Mapping field) {
		_fields.put(name, field);
	}

	@Override
	public String getAnalyzer() {
		return _analyzer;
	}

	public Mapping getField(String name) {
		return _fields.get(name);
	}

	public Map<String, Mapping> getFields() {
		return _fields;
	}

	@Override
	public String getFormat() {
		return _format;
	}

	@Override
	public String getTermVector() {
		return _termVector;
	}

	@Override
	public String getType() {
		return _type;
	}

	public boolean isFieldTypeMatched(String fieldType) {
		/*if ((_fieldType != null) &&
			Objects.equals(_fieldType.getDataType(), fieldType)) {

			return true;
		}*/

		return false;
	}

	@Override
	public boolean isStore() {
		return _store;
	}

	public Mapping removeField(String name) {
		return _fields.remove(name);
	}

	public void setAnalyzer(String analyzer) {
		_analyzer = analyzer;
	}

	public void setFormat(String format) {
		_format = format;
	}

	public void setStore(boolean store) {
		_store = store;
	}

	public void setTermVector(String termVector) {
		_termVector = termVector;
	}

	public void setType(String type) {
		_type = type;
	}

	private String _analyzer;
	private Map<String, Mapping> _fields = new HashMap<>();
	private String _format;
	private boolean _store;
	private String _termVector;
	private String _type;

}