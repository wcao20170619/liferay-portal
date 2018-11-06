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
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.geolocation.GeoLocationPoint;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.field.FieldRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.math.BigDecimal;

import java.text.Format;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Wade Cao
 */
public class DocumentBuilderImpl implements DocumentBuilder {

	public DocumentBuilderImpl(
		FieldRegistry fieldRegistry, DocumentTranslator documentTranslator) {

		_fieldRegistry = fieldRegistry;
		_documentTranslator = documentTranslator;
	}

	@Override
	public void add(double latitude, double longitude) {
		add(Field.GEO_LOCATION, latitude, longitude);
	}

	@Override
	public void add(Field field) {
		_fields.put(field.getName(), field);
	}

	@Override
	public void add(String name, BigDecimal value) {
		add(name, String.valueOf(value));
	}

	@Override
	public void add(String name, BigDecimal[] values) {
		if (values == null) {
			return;
		}

		add(name, ArrayUtil.toStringArray(values));
	}

	@Override
	public void add(String name, boolean value) {
		add(name, String.valueOf(value));
	}

	@Override
	public void add(String name, Boolean value) {
		add(name, String.valueOf(value));
	}

	@Override
	public void add(String name, boolean[] values) {
		if (values == null) {
			return;
		}

		add(name, ArrayUtil.toStringArray(values));
	}

	@Override
	public void add(String name, Boolean[] values) {
		if (values == null) {
			return;
		}

		add(name, ArrayUtil.toStringArray(values));
	}

	@Override
	public void add(String name, byte[] bytes, String fileExt)
		throws IOException {

		InputStream is = new UnsyncByteArrayInputStream(bytes);

		add(name, is, fileExt);
	}

	@Override
	public void add(String name, Date value) {
		if (value == null) {
			return;
		}

		add(name, new Date[] {value});
	}

	@Override
	public void add(String name, Date[] values) {
		if (values == null) {
			return;
		}

		String[] datesString = new String[values.length];
		Long[] datesTime = new Long[values.length];

		for (int i = 0; i < values.length; i++) {
			Format dateFormat = _getDateFormat();

			datesString[i] = dateFormat.format(values[i]);

			datesTime[i] = values[i].getTime();
		}

		Field field = createField(name, datesString);

		field.setDates(values);
	}

	@Override
	public void add(String name, double value) {
		add(name, String.valueOf(value));
	}

	@Override
	public void add(String name, double latitude, double longitude) {
		Field field = new Field(name);

		field.setGeoLocationPoint(new GeoLocationPoint(latitude, longitude));

		add(field);
	}

	@Override
	public void add(String name, Double value) {
		add(name, String.valueOf(value));
	}

	@Override
	public void add(String name, double[] values) {
		if (values == null) {
			return;
		}

		add(name, ArrayUtil.toStringArray(values));
	}

	@Override
	public void add(String name, Double[] values) {
		if (values == null) {
			return;
		}

		add(name, ArrayUtil.toStringArray(values));
	}

	@Override
	public void add(String name, File file, String fileExt) throws IOException {
		InputStream is = new FileInputStream(file);

		add(name, is, fileExt);
	}

	@Override
	public void add(String name, float value) {
		add(name, String.valueOf(value));
	}

	@Override
	public void add(String name, Float value) {
		add(name, String.valueOf(value));
	}

	@Override
	public void add(String name, float[] values) {
		if (values == null) {
			return;
		}

		add(name, ArrayUtil.toStringArray(values));
	}

	@Override
	public void add(String name, Float[] values) {
		if (values == null) {
			return;
		}

		add(name, ArrayUtil.toStringArray(values));
	}

	@Override
	public void add(String name, InputStream is, String fileExt)
		throws IOException {

		addText(name, FileUtil.extractText(is, fileExt));
	}

	@Override
	public void add(
			String name, InputStream is, String fileExt, int maxStringLength)
		throws IOException {

		addText(name, FileUtil.extractText(is, fileExt, maxStringLength));
	}

	@Override
	public void add(String name, int value) {
		add(name, String.valueOf(value));
	}

	@Override
	public void add(String name, int[] values) {
		if (values == null) {
			return;
		}

		add(name, ArrayUtil.toStringArray(values));
	}

	@Override
	public void add(String name, Integer value) {
		add(name, String.valueOf(value));
	}

	@Override
	public void add(String name, Integer[] values) {
		if (values == null) {
			return;
		}

		add(name, ArrayUtil.toStringArray(values));
	}

	@Override
	public void add(
		String field, Locale siteDefaultLocale, Map<Locale, String> map) {

		for (Map.Entry<Locale, String> entry : map.entrySet()) {
			Locale locale = entry.getKey();

			if (locale.equals(siteDefaultLocale)) {
				add(field, entry.getValue());
			}

			String languageId = LocaleUtil.toLanguageId(locale);

			add(field, languageId, entry.getValue());
		}
	}

	@Override
	public void add(String name, Locale locale, String[] values) {
		if (values == null) {
			return;
		}

		String fieldName = name.concat(
			StringPool.UNDERLINE).concat(locale.toString());

		Field field = createField(fieldName, values);

		if (_fieldRegistry.isTextField(name)) {
			field.setTokenized(true);

			_createSortableTextField(fieldName, false, values);
		}
	}

	@Override
	public void add(String name, long value) {
		add(name, String.valueOf(value));
	}

	@Override
	public void add(String name, Long value) {
		add(name, String.valueOf(value));
	}

	@Override
	public void add(String name, long[] values) {
		if (values == null) {
			return;
		}

		add(name, ArrayUtil.toStringArray(values));
	}

	@Override
	public void add(String name, Long[] values) {
		if (values == null) {
			return;
		}

		add(name, ArrayUtil.toStringArray(values));
	}

	@Override
	public void add(String name, Map<Locale, String> values) {
		add(name, values, false);
	}

	@Override
	public void add(
		String name, Map<Locale, String> values, boolean lowerCase) {

		if ((values == null) || values.isEmpty()) {
			return;
		}

		if (lowerCase) {
			Map<Locale, String> lowerCaseValues = new HashMap<>(values.size());

			for (Map.Entry<Locale, String> entry : values.entrySet()) {
				String value = GetterUtil.getString(entry.getValue());

				lowerCaseValues.put(
					entry.getKey(), StringUtil.toLowerCase(value));
			}

			values = lowerCaseValues;
		}

		createField(name, values);
	}

	@Override
	public void add(
		String name, Map<Locale, String> values, boolean lowerCase,
		boolean sortable) {

		if ((values == null) || values.isEmpty()) {
			return;
		}

		if (lowerCase) {
			Map<Locale, String> lowerCaseValues = new HashMap<>(values.size());

			for (Map.Entry<Locale, String> entry : values.entrySet()) {
				String value = GetterUtil.getString(entry.getValue());

				lowerCaseValues.put(
					entry.getKey(), StringUtil.toLowerCase(value));
			}

			values = lowerCaseValues;
		}

		createField(name, values, sortable);
	}

	@Override
	public void add(String name, short value) {
		add(name, String.valueOf(value));
	}

	@Override
	public void add(String name, Short value) {
		add(name, String.valueOf(value));
	}

	@Override
	public void add(String name, short[] values) {
		if (values == null) {
			return;
		}

		add(name, ArrayUtil.toStringArray(values));
	}

	@Override
	public void add(String name, Short[] values) {
		if (values == null) {
			return;
		}

		add(name, ArrayUtil.toStringArray(values));
	}

	@Override
	public void add(String name, String value) {
		add(name, value, false);
	}

	@Override
	public void add(String name, String value, boolean lowerCase) {
		Field field = createKeywordField(name, value, lowerCase);

		if (_fieldRegistry.isTextField(name)) {
			field.setTokenized(true);
			_createSortableTextField(name, false, value);
		}
	}

	@Override
	public void add(String name, String languageId, String value) {
		if (Validator.isNull(value)) {
			return;
		}

		String nameLanguageId = LocalizationUtil.getLocalizedName(
			name, languageId);

		Field field = createField(nameLanguageId, value);

		if (_fieldRegistry.isTextField(name)) {
			field.setTokenized(true);

			_createSortableTextField(nameLanguageId, false, value);
		}
	}

	@Override
	public void add(String name, String languageId, String[] values) {
		if (values == null) {
			return;
		}

		String nameLanguageId = LocalizationUtil.getLocalizedName(
			name, languageId);

		Field field = createField(nameLanguageId, values);

		if (_fieldRegistry.isTextField(name)) {
			field.setTokenized(true);

			_createSortableTextField(name, false, values);
		}
	}

	@Override
	public void add(String name, String[] values) {
		if (values == null) {
			return;
		}

		Field field = createField(name, values);

		if (_fieldRegistry.isTextField(name)) {
			field.setTokenized(true);
			_createSortableTextField(name, false, values);
		}
	}

	@Override
	public void addLocalizedText(String name, Map<Locale, String> values) {
		if ((values == null) || values.isEmpty()) {
			return;
		}

		Field field = createField(name, values);

		field.setTokenized(true);
	}

	public void addText(String name, String value) {
		if (Validator.isNull(value)) {
			return;
		}

		Field field = createField(name, value);

		field.setTokenized(true);

		_createSortableTextField(name, false, value);
	}

	public void addText(String name, String[] values) {
		if (values == null) {
			return;
		}

		Field field = createField(name, values);

		field.setTokenized(true);

		_createSortableTextField(name, false, values);
	}

	@Override
	public void addUID(String portletId, long field1) {
		addUID(portletId, String.valueOf(field1));
	}

	@Override
	public void addUID(String portletId, long field1, String field2) {
		addUID(portletId, field1, field2);
	}

	@Override
	public void addUID(String portletId, Long field1) {
		addUID(portletId, field1);
	}

	@Override
	public void addUID(String portletId, Long field1, String field2) {
		addUID(portletId, field1, field2);
	}

	@Override
	public void addUID(String portletId, String field1) {
		addUID(portletId, field1, null);
	}

	@Override
	public void addUID(String portletId, String field1, String field2) {
		addUID(portletId, field1, field2, null);
	}

	@Override
	public void addUID(
		String portletId, String field1, String field2, String field3) {

		addUID(portletId, field1, field2, field3, null);
	}

	@Override
	public void addUID(
		String portletId, String field1, String field2, String field3,
		String field4) {

		String uid = Field.getUID(portletId, field1, field2, field3, field4);

		add(Field.UID, uid);
	}

	@Override
	public Document build() {
		return new DocumentImpl(_fields);
	}

	@Override
	public com.liferay.portal.kernel.search.Document build(
		com.liferay.portal.kernel.search.Document document) {

		Document searchDocument = build();

		return _documentTranslator.translate(
			searchDocument, document, _fieldRegistry);
	}

	@Override
	public void remove(String name) {
		_fields.remove(name);
	}

	public com.liferay.portal.kernel.search.Document translate(
		Document searchDocument,
		com.liferay.portal.kernel.search.Document document) {

		return _documentTranslator.translate(
			searchDocument, document, _fieldRegistry);
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

	protected Field doGetField(String name, boolean createIfNew) {
		Field field = _fields.get(name);

		if ((field == null) && createIfNew) {
			field = new Field(name);

			_fields.put(name, field);
		}

		return field;
	}

	private void _createSortableTextField(
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

	private void _createSortableTextField(
		String name, boolean typify, String[] values) {

		if (values.length == 0) {
			return;
		}

		_createSortableTextField(
			name, typify, Collections.min(Arrays.<String>asList(values)));
	}

	private Format _getDateFormat() {
		if (_dateFormat == null) {
			_dateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
				_INDEX_DATE_FORMAT_PATTERN);
		}

		return _dateFormat;
	}

	private static final String _INDEX_DATE_FORMAT_PATTERN = PropsUtil.get(
		PropsKeys.INDEX_DATE_FORMAT_PATTERN);

	private static final int _SORTABLE_TEXT_FIELDS_TRUNCATED_LENGTH =
		GetterUtil.getInteger(
			PropsUtil.get(
				PropsKeys.INDEX_SORTABLE_TEXT_FIELDS_TRUNCATED_LENGTH));

	private static Format _dateFormat;

	private final DocumentTranslator _documentTranslator;
	private final FieldRegistry _fieldRegistry;
	private Map<String, Field> _fields = new HashMap<>();

}