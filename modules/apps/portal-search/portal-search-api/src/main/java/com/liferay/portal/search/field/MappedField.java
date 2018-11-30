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

package com.liferay.portal.search.field;

import com.liferay.petra.string.StringBundler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Wade Cao
 */
public class MappedField {

	public MappedField() {
		setFieldType(FieldType.getFieldType("text"));
		setStored(false);
		setTermVector(null);
		setFormat(null);
		setBoost(1.0f);
	}

	public MappedField(FieldType fieldType) {
		this();
		_fieldType = fieldType;
	}

	public void addField(String name, MappedField field) {
		_fields.put(name, field);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof MappedField)) {
			return false;
		}

		MappedField mappedFieldType = (MappedField)o;

		if ((_boost == mappedFieldType.getBoost()) &&
			Objects.equals(_fieldType, mappedFieldType.getFieldType()) &&
			Objects.equals(_fields, mappedFieldType.getFields()) &&
			Objects.equals(_analyzer, mappedFieldType.getAnalyzer()) &&
			(_stored == mappedFieldType.isStored()) &&
			Objects.equals(_termVector, mappedFieldType.getTermVector()) &&
			Objects.equals(_format, mappedFieldType.getFormat())) {

			return true;
		}

		return false;
	}

	public String getAnalyzer() {
		return _analyzer;
	}

	public float getBoost() {
		return _boost;
	}

	public MappedField getField(String name) {
		return _fields.get(name);
	}

	public Map<String, MappedField> getFields() {
		return _fields;
	}

	public FieldType getFieldType() {
		return _fieldType;
	}

	public String getFormat() {
		return _format;
	}

	public String getMappingSource() {
		String retValue = "{";

		if (_fieldType != null) {
			retValue += "\"type\": \"" + _fieldType.getName() + "\"";
		}

		if (_analyzer != null) {
			retValue += ",\n \"analyzer\": \"" + _analyzer + "\"";
		}

		if (_boost != 1.0f) {
			retValue += ",\n \"boost\": " + (int)_boost;
		}

		if (_stored) {
			retValue += ",\n \"store\": true";
		}

		if (_termVector != null) {
			retValue += ",\n \"term_vector\": \"" + _termVector + "\"";
		}

		if (_format != null) {
			retValue += ",\n \"format\": \"" + _format + "\"";
		}

		if (!_fields.isEmpty()) {
			retValue += ",\n \"fields\": {" + _getFieldsMappingSource() + "}";
		}

		retValue += "}";

		return retValue;
	}

	public String getMappingSource(String name) {
		StringBundler sb = new StringBundler(4);

		sb.append("\"");
		sb.append(name);
		sb.append("\": ");
		sb.append(getMappingSource());

		return sb.toString();
	}

	public String getTermVector() {
		return _termVector;
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			_fieldType.hashCode(), _boost, _analyzer, _stored, _termVector,
			_format, _fields);
	}

	public boolean isFieldTypeMatched(String fieldType) {
		if (((_fieldType != null) &&
			 _fieldType.getDateType().equals(fieldType))) {

			return true;
		}

		return false;
	}

	public boolean isStored() {
		return _stored;
	}

	public MappedField removeField(String name) {
		return _fields.remove(name);
	}

	public void setAnalyzer(String analyzer) {
		_analyzer = analyzer;
	}

	public void setBoost(float boost) {
		_boost = boost;
	}

	public void setFieldType(FieldType fieldType) {
		_fieldType = fieldType;
	}

	public void setFormat(String format) {
		_format = format;
	}

	public void setStored(boolean stored) {
		_stored = stored;
	}

	public void setTermVector(String termVector) {
		_termVector = termVector;
	}

	private String _getFieldsMappingSource() {
		String[] retValue = new String[1];

		_fields.forEach(
			(name, field) -> {
			StringBundler sb = new StringBundler(4);

			if ((name != null) && (field != null)) {
				if (retValue[0] == null) {
					sb.append("\"");
					sb.append(name);
					sb.append("\": ");
					sb.append(field.getMappingSource());

					retValue[0] = sb.toString();
				}
				else {
					sb.append(",\n \"");
					sb.append(name);
					sb.append("\": ");
					sb.append(field.getMappingSource());

					retValue[0] += sb.toString();
				}
			}
		});

		return retValue[0];
	}

	private String _analyzer;
	private float _boost;
	private Map<String, MappedField> _fields = new HashMap<>();
	private FieldType _fieldType;
	private String _format;
	private boolean _stored;
	private String _termVector;

}