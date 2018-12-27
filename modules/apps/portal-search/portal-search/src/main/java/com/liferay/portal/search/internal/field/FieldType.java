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

import com.liferay.petra.string.StringBundler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wade Cao
 */
public class FieldType {

	public static final String BINARY = "BINARY";

	public static final String BOOLEAN = "BOOLEAN";

	public static final String DATE = "DATE";

	public static final String GEO_POINT = "GEO_POINT";

	public static final String GEO_SHAPE = "GEO_SHAPE";

	public static final String NUMERIC = "NUMERIC";

	public static final String RANGE = "RANGE";

	public static final String STRING_ANALYZED = "STRING_ANALYZED";

	public static final String STRING_NOT_ANALYZED = "STRING_NOT_ANALYZED";

	public static FieldType getFieldType(String name) {
		return _fieldTypeMap.get(name);
	}

	public String getDataType() {
		return dataType;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(5);

		sb.append("{dataType=");
		sb.append(dataType);
		sb.append(", name=");
		sb.append(name);
		sb.append("}");

		return sb.toString();
	}

	protected String dataType;
	protected String name;

	private static void _addFieldType(String dataType1, String name1) {
		FieldType value = new FieldType() {
			{
				dataType = dataType1;
				name = name1;
			}
		};

		_fieldTypeMap.put(name1, value);
	}

	private static final List<String> _binaryDatatype = Arrays.asList("binary");
	private static final List<String> _booleanDatatype = Arrays.asList(
		"boolean");
	private static final List<String> _dateDatatype = Arrays.asList("date");
	private static final Map<String, FieldType> _fieldTypeMap = new HashMap<>();
	private static final List<String> _geoPointDatatype = Arrays.asList(
		"geo_point");
	private static final List<String> _geoShapeDatatype = Arrays.asList(
		"geo_shape");
	private static final List<String> _numericDatatype = Arrays.asList(
		"long", "integer", "short", "byte", "double", "float", "half_float",
		"scaled_float");
	private static final List<String> _rangeDatatype = Arrays.asList(
		"integer_range", "float_range", "long_range", "double_range",
		"date_range");
	private static final List<String> _stringAnalyzedDatatype = Arrays.asList(
		"text");
	private static final List<String> _stringNotAnalyzedDatatype =
		Arrays.asList("keyword");

	static {
		_stringAnalyzedDatatype.forEach(
			string -> _addFieldType(STRING_ANALYZED, string));
		_stringNotAnalyzedDatatype.forEach(
			string -> _addFieldType(STRING_NOT_ANALYZED, string));
		_numericDatatype.forEach(string -> _addFieldType(NUMERIC, string));
		_dateDatatype.forEach(string -> _addFieldType(DATE, string));
		_booleanDatatype.forEach(string -> _addFieldType(BOOLEAN, string));
		_binaryDatatype.forEach(string -> _addFieldType(BINARY, string));
		_rangeDatatype.forEach(string -> _addFieldType(RANGE, string));
		_geoPointDatatype.forEach(string -> _addFieldType(GEO_POINT, string));
		_geoShapeDatatype.forEach(string -> _addFieldType(GEO_SHAPE, string));
	}

}