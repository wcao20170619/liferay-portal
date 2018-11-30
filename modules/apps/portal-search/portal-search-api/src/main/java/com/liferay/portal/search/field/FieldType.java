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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;

/**
 * @author Wade Cao
 */
public class FieldType {
	
	public static String STRING_ANALYZED = "STRING_ANALYZED";
	public static String STRING_NOT_ANALYZED = "STRING_NOT_ANALYZED";
	public static String NUMERIC = "NUMERIC";
	public static String DATE = "DATE";
	public static String BOOLEAN = "BOOLEAN";
	public static String GEO_POINT = "GEO_POINT";
	public static String GEO_SHAPE = "GEO_SHAPE";
	public static String BINARY = "BINARY";
	public static String RANGE = "RANGE";
	
	private static Map<String, FieldType> _fieldTypeMap = 
			new HashMap<String, FieldType>();
	
	private static List<String> _string_analyzed_datatype = Arrays.asList("text");
	private static List<String> _string_not_analyzed_datatype = Arrays.asList("keyword");
	private static List<String> _numeric_datatype = Arrays.asList("long", "integer", "short", 
		"byte", "double", "float", "half_float", "scaled_float");
	private static List<String> _date_datatype = Arrays.asList("date");
	private static List<String> _boolean_datatype = Arrays.asList("boolean");
	private static List<String> _binary_datatype = Arrays.asList("binary");
	private static List<String> _range_datatype  = Arrays.asList("integer_range", 
		"float_range", "long_range", "double_range", "date_range");
	private static List<String> _geo_point_datatype = Arrays.asList("geo_point");
	private static List<String> _geo_shape_datatype = Arrays.asList("geo_shape");
	
	static {
		int[] idx = { 0 };
		
		_string_analyzed_datatype.forEach(string->{			
			new FieldType(idx[0]++, STRING_ANALYZED, string);
		});
		_string_not_analyzed_datatype.forEach(string->{
			new FieldType(idx[0]++, STRING_NOT_ANALYZED, string);
		});
		_numeric_datatype.forEach(string->{
			new FieldType(idx[0]++, NUMERIC, string);
		});
		_date_datatype.forEach(string->{
			new FieldType(idx[0]++, DATE, string);
		});
		_boolean_datatype.forEach(string->{
			new FieldType(idx[0]++, BOOLEAN, string);
		});
		_binary_datatype.forEach(string->{
			new FieldType(idx[0]++, BINARY, string);
		});
		_range_datatype.forEach(string->{
			new FieldType(idx[0]++, RANGE, string);
		});
		_geo_point_datatype.forEach(string->{
			new FieldType(idx[0]++, GEO_POINT, string);
		});
		_geo_shape_datatype.forEach(string->{
			new FieldType(idx[0]++, GEO_SHAPE, string);
		});
	}
	
	public static FieldType getFieldType(String name) {
		return _fieldTypeMap.get(name);
	}
	
	private FieldType(final long id, String dataType, String name) {
		_id = id;
		_dataType = dataType;
		_name = name;
		_fieldTypeMap.put(name,this);
	}
	
	@Override
    public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }

        if (!(obj instanceof FieldType)) {
             return false;
        }
        
        FieldType fieldType = (FieldType)obj;
        
        if (_id == fieldType.getId()) {
        	return true;
        }
        
        return false;
	}
	
	@Override
	public int hashCode() {
		return HashUtil.hash(0, _id);
	}
	
	 @Override
     public String toString() {
		 StringBundler sb = new StringBundler(7);
		 
		 sb.append("{id=");
         sb.append(_id);
         sb.append(", dataType=");
         sb.append(_dataType);
         sb.append(", name=");
         sb.append(_name);
         sb.append("}");
         
         return sb.toString();
	 }
	
	public String getDateType() {
		return _dataType;
	}
	
	public long getId() {
		return _id;
	}
	
	public String getName() {
		return _name;
	}
	
	private long _id;
	private String _dataType;
	private String _name;
}
