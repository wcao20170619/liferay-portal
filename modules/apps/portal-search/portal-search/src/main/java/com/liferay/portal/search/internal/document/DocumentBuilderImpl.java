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
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.field.FieldType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.math.BigDecimal;

import java.text.Format;

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
		_documentBuilderHelper = new DocumentBuilderHelper();
	}

	@Override
	public void add(double latitude, double longitude) {
		add(Field.GEO_LOCATION, latitude, longitude);
	}

	@Override
	public void add(Field field) {
		_documentBuilderHelper.add(field);
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
			Format dateFormat = _documentBuilderHelper.getDateFormat();

			datesString[i] = dateFormat.format(values[i]);

			datesTime[i] = values[i].getTime();
		}

		if (_fieldRegistry.isTheFieldType(name, FieldType.DATE)) {
			_documentBuilderHelper.createSortableNumericField(
				name, false, datesTime);
		}

		Field field = _documentBuilderHelper.createField(name, datesString);

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

		Field field = _documentBuilderHelper.createField(fieldName, values);

		if (_fieldRegistry.isTheFieldType(name, FieldType.STRING_ANALYZED)) {
			field.setTokenized(true);

			_documentBuilderHelper.createSortableTextField(
				fieldName, false, values);
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

		_documentBuilderHelper.createField(name, values);
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

		_documentBuilderHelper.createField(name, values, sortable);
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
		Field field = _documentBuilderHelper.createKeywordField(
			name, value, lowerCase);

		if (_fieldRegistry.isTheFieldType(name, FieldType.STRING_ANALYZED)) {
			field.setTokenized(true);
			_documentBuilderHelper.createSortableTextField(name, false, value);
		}
	}

	@Override
	public void add(String name, String languageId, String value) {
		if (Validator.isNull(value)) {
			return;
		}

		String nameLanguageId = LocalizationUtil.getLocalizedName(
			name, languageId);

		Field field = _documentBuilderHelper.createField(nameLanguageId, value);

		if (_fieldRegistry.isTheFieldType(name, FieldType.STRING_ANALYZED)) {
			field.setTokenized(true);

			_documentBuilderHelper.createSortableTextField(
				nameLanguageId, false, value);
		}
	}

	@Override
	public void add(String name, String languageId, String[] values) {
		if (values == null) {
			return;
		}

		String nameLanguageId = LocalizationUtil.getLocalizedName(
			name, languageId);

		Field field = _documentBuilderHelper.createField(
			nameLanguageId, values);

		if (_fieldRegistry.isTheFieldType(name, FieldType.STRING_ANALYZED)) {
			field.setTokenized(true);

			_documentBuilderHelper.createSortableTextField(name, false, values);
		}
	}

	@Override
	public void add(String name, String[] values) {
		if (values == null) {
			return;
		}

		Field field = _documentBuilderHelper.createField(name, values);

		if (_fieldRegistry.isTheFieldType(name, FieldType.STRING_ANALYZED)) {
			field.setTokenized(true);
			_documentBuilderHelper.createSortableTextField(name, false, values);
		}
	}

	@Override
	public void addLocalizedText(String name, Map<Locale, String> values) {
		if ((values == null) || values.isEmpty()) {
			return;
		}

		Field field = _documentBuilderHelper.createField(name, values);

		field.setTokenized(true);
	}

	@Override
	public void addNumber(String name, BigDecimal value) {
		_documentBuilderHelper.createNumberField(name, value);
	}

	@Override
	public void addNumber(String name, BigDecimal[] values) {
		_documentBuilderHelper.createNumberField(name, values);
	}

	@Override
	public void addNumber(String name, double value) {
		_documentBuilderHelper.createNumberField(name, Double.valueOf(value));
	}

	@Override
	public void addNumber(String name, Double value) {
		_documentBuilderHelper.createNumberField(name, value);
	}

	@Override
	public void addNumber(String name, double[] values) {
		if (values == null) {
			return;
		}

		_documentBuilderHelper.createNumberField(
			name, ArrayUtil.toArray(values));
	}

	@Override
	public void addNumber(String name, Double[] values) {
		_documentBuilderHelper.createNumberField(name, values);
	}

	@Override
	public void addNumber(String name, float value) {
		_documentBuilderHelper.createNumberField(name, Float.valueOf(value));
	}

	@Override
	public void addNumber(String name, Float value) {
		_documentBuilderHelper.createNumberField(name, value);
	}

	@Override
	public void addNumber(String name, float[] values) {
		if (values == null) {
			return;
		}

		_documentBuilderHelper.createNumberField(
			name, ArrayUtil.toArray(values));
	}

	@Override
	public void addNumber(String name, Float[] values) {
		_documentBuilderHelper.createNumberField(name, values);
	}

	@Override
	public void addNumber(String name, int value) {
		_documentBuilderHelper.createNumberField(name, Integer.valueOf(value));
	}

	@Override
	public void addNumber(String name, int[] values) {
		if (values == null) {
			return;
		}

		_documentBuilderHelper.createNumberField(
			name, ArrayUtil.toArray(values));
	}

	@Override
	public void addNumber(String name, Integer value) {
		_documentBuilderHelper.createNumberField(name, value);
	}

	@Override
	public void addNumber(String name, Integer[] values) {
		_documentBuilderHelper.createNumberField(name, values);
	}

	@Override
	public void addNumber(String name, long value) {
		_documentBuilderHelper.createNumberField(name, Long.valueOf(value));
	}

	@Override
	public void addNumber(String name, Long value) {
		_documentBuilderHelper.createNumberField(name, value);
	}

	@Override
	public void addNumber(String name, long[] values) {
		if (values == null) {
			return;
		}

		_documentBuilderHelper.createNumberField(
			name, ArrayUtil.toArray(values));
	}

	@Override
	public void addNumber(String name, Long[] values) {
		_documentBuilderHelper.createNumberField(name, values);
	}

	@Override
	public void addNumber(String name, String value) {
		_documentBuilderHelper.createNumberField(name, Long.valueOf(value));
	}

	@Override
	public void addNumber(String name, String[] values) {
		if (values == null) {
			return;
		}

		Long[] longs = new Long[values.length];

		for (int i = 0; i < values.length; i++) {
			longs[i] = Long.valueOf(values[i]);
		}

		_documentBuilderHelper.createNumberField(name, longs);
	}

	public void addText(String name, String value) {
		if (Validator.isNull(value)) {
			return;
		}

		Field field = _documentBuilderHelper.createField(name, value);

		field.setTokenized(true);

		_documentBuilderHelper.createSortableTextField(name, false, value);
	}

	public void addText(String name, String[] values) {
		if (values == null) {
			return;
		}

		Field field = _documentBuilderHelper.createField(name, values);

		field.setTokenized(true);

		_documentBuilderHelper.createSortableTextField(name, false, values);
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
		return new DocumentImpl(_documentBuilderHelper.getFields());
	}

	@Override
	public com.liferay.portal.kernel.search.Document build(
		com.liferay.portal.kernel.search.Document document) {

		Document searchDocument = build();

		return _documentTranslator.translate(
			searchDocument, document, _fieldRegistry);
	}

	@Override
	public boolean hasField(String name) {
		return _documentBuilderHelper.hasField(name);
	}

	@Override
	public void remove(String name) {
		_documentBuilderHelper.remove(name);
	}

	public com.liferay.portal.kernel.search.Document translate(
		Document searchDocument,
		com.liferay.portal.kernel.search.Document document) {

		return _documentTranslator.translate(
			searchDocument, document, _fieldRegistry);
	}

	private final DocumentBuilderHelper _documentBuilderHelper;
	private final DocumentTranslator _documentTranslator;
	private final FieldRegistry _fieldRegistry;

}