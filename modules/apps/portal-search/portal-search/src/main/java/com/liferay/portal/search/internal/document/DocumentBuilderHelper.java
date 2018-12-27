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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.text.Format;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Wade Cao
 */
public class DocumentBuilderHelper {

	protected void add(Field field) {
		_fields.put(field.getName(), field);
	}

	protected Field createField(String name) {
		return doGetField(name, true);
	}

	protected Field createField(
		String name, boolean sortable, String... values) {

		Field field = createField(name);

		field.setSortable(sortable);
		field.setValues(values);

		return field;
	}

	protected Field createField(
		String name, Map<Locale, String> localizedValues) {

		return createField(name, localizedValues, false);
	}

	protected Field createField(
		String name, Map<Locale, String> localizedValues, boolean sortable) {

		Field field = createField(name);

		field.setLocalizedValues(localizedValues);
		field.setSortable(sortable);

		return field;
	}

	protected Field createField(String name, String... values) {
		return createField(name, false, values);
	}

	protected Field createKeywordField(
		String name, String value, boolean lowerCase) {

		if (lowerCase && Validator.isNotNull(value)) {
			value = StringUtil.toLowerCase(value);
		}

		return createField(name, value);
	}

	protected void createNumberField(
		String name, boolean typify, Number value) {

		if (value == null) {
			return;
		}

		String valueString = String.valueOf(value);

		_createSortableNumericField(
			name, typify, valueString, value.getClass());

		createField(name, valueString);
	}

	protected <T extends Number & Comparable<? super T>> void createNumberField(
		String name, boolean typify,
		@SuppressWarnings("unchecked") T... values) {

		if (values == null) {
			return;
		}

		createSortableNumericField(name, typify, values);

		createField(name, ArrayUtil.toStringArray(values));
	}

	protected void createNumberField(String name, Number value) {
		createNumberField(name, false, value);
	}

	protected <T extends Number & Comparable<? super T>> void createNumberField(
		String name, @SuppressWarnings("unchecked") T... values) {

		createNumberField(name, false, values);
	}

	protected void createNumberFieldWithTypedSortable(
		String name, Number value) {

		createNumberField(name, true, value);
	}

	protected <T extends Number & Comparable<? super T>> void
		createNumberFieldWithTypedSortable(
			String name, @SuppressWarnings("unchecked") T... values) {

		createNumberField(name, true, values);
	}

	protected <T extends Number & Comparable<? super T>> void
		createSortableNumericField(
			String name, boolean typify,
			@SuppressWarnings("unchecked") T... values) {

		if ((values == null) || (values.length == 0)) {
			return;
		}

		T minValue = Collections.min(Arrays.asList(values));

		_createSortableNumericField(
			name, typify, String.valueOf(minValue), minValue.getClass());
	}

	protected void createSortableTextField(
		String name, boolean typify, String value) {

		if (typify) {
			name = name.concat(StringPool.UNDERLINE).concat("String");
		}

		String truncatedValue = value;

		if (value.length() > _SORTABLE_TEXT_FIELDS_TRUNCATED_LENGTH) {
			truncatedValue = value.substring(
				0, _SORTABLE_TEXT_FIELDS_TRUNCATED_LENGTH);
		}

		createKeywordField(
			Field.getSortableFieldName(name), truncatedValue, true);
	}

	protected void createSortableTextField(
		String name, boolean typify, String[] values) {

		if (values.length == 0) {
			return;
		}

		createSortableTextField(
			name, typify, Collections.min(Arrays.<String>asList(values)));
	}

	protected Field doGetField(String name, boolean createIfNew) {
		Field field = _fields.get(name);

		if ((field == null) && createIfNew) {
			field = new Field(name);

			_fields.put(name, field);
		}

		return field;
	}

	protected Format getDateFormat() {
		return _dateFormat;
	}

	protected Map<String, Field> getFields() {
		return _fields;
	}

	protected boolean hasField(String name) {
		if (_fields.containsKey(name)) {
			return true;
		}

		return false;
	}

	protected void remove(String name) {
		_fields.remove(name);
	}

	private void _createSortableNumericField(
		String name, boolean typify, String value,
		Class<? extends Number> clazz) {

		if (typify) {
			name = name.concat(StringPool.UNDERLINE).concat("Number");
		}

		Field field = createField(Field.getSortableFieldName(name), value);

		field.setNumeric(true);
		field.setNumericClass(clazz);
	}

	private static final String _INDEX_DATE_FORMAT_PATTERN = PropsUtil.get(
		PropsKeys.INDEX_DATE_FORMAT_PATTERN);

	private static final int _SORTABLE_TEXT_FIELDS_TRUNCATED_LENGTH =
		GetterUtil.getInteger(
			PropsUtil.get(
				PropsKeys.INDEX_SORTABLE_TEXT_FIELDS_TRUNCATED_LENGTH));

	private static final Format _dateFormat =
		FastDateFormatFactoryUtil.getSimpleDateFormat(
			_INDEX_DATE_FORMAT_PATTERN);

	private final Map<String, Field> _fields = new HashMap<>();

}