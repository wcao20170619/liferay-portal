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

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.FieldBuilder;
import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.field.Mapping;
import com.liferay.portal.search.geolocation.GeoPoint;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Wade Cao
 */
public class DocumentBuilderImpl implements DocumentBuilder {

	public DocumentBuilderImpl(FieldRegistry fieldRegistry) {
		_fieldRegistry = fieldRegistry;
		_documentBuilderHelper = new DocumentBuilderHelper();
	}

	@Override
	public DocumentBuilder addDate(String name, String value) {
		guardType(name, "date");

		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addDates(String name, String... values) {
		guardType(name, "date");

		addFieldValue(name, values);

		return this;
	}

	@Override
	public DocumentBuilder addDouble(String name, double value) {
		guardType(name, "double");

		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addDoubles(String name, Double... values) {
		guardType(name, "double");

		FieldBuilder fieldBuilder = new FieldBuilderImpl();

		_documentImpl.addField(
			fieldBuilder.name(
				name
			).values(
				values
			).build());

		return this;
	}

	@Override
	public DocumentBuilder addFloat(String name, float value) {
		guardType(name, "float");

		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addFloats(String name, Float... values) {
		guardType(name, "float");

		addFieldValues(name, values);

		return this;
	}

	@Override
	public DocumentBuilder addGeoPoint(String name, GeoPoint value) {
		guardType(name, "geo_point");

		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addGeoPoints(String name, GeoPoint... values) {
		guardType(name, "geo_point");

		addFieldValue(name, values);

		return this;
	}

	@Override
	public DocumentBuilder addInteger(String name, int value) {
		guardType(name, "integer");

		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addIntegers(String name, Integer... values) {
		guardType(name, "integer");

		addFieldValues(name, values);

		return this;
	}

	@Override
	public DocumentBuilder addLong(String name, long value) {
		guardType(name, "long");

		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addLongs(String name, Long... values) {
		guardType(name, "long");

		addFieldValues(name, values);

		return this;
	}

	@Override
	public DocumentBuilder addString(String name, String value) {
		guardTypeString(name);

		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addStrings(String name, String... values) {
		guardTypeString(name);

		addFieldValues(name, values);

		return this;
	}

	@Override
	public DocumentBuilder addUncheckedValue(String name, Object value) {
		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addUncheckedValues(String name, Object[] values) {
		addFieldValues(name, values);

		return this;
	}

	@Override
	public Document build() {
		return new DocumentImpl(_documentImpl);
	}

	/*@Override
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

		_documentBuilderHelper.createSortableNumericField(
			name, false, datesTime);

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

			if (_fieldRegistry.isSortableTextField(name)) {
				_documentBuilderHelper.createSortableTextField(
					name, false, value);
			}
		}
	}

	@Override
	public void add(String name, String languageId, String value) {
		if (Validator.isNull(value)) {
			return;
		}

		String nameLanguageId = LocalizationUtil.getLocalizedName(
			name, languageId);

		add(nameLanguageId, value);
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
	public boolean hasField(String name) {
		return _documentBuilderHelper.hasField(name);
	}

	@Override
	public void remove(String name) {
		_documentBuilderHelper.remove(name);
	}

	*/

	protected void addFieldValue(String name, Object value) {
		FieldBuilder fieldBuilder = new FieldBuilderImpl();

		_documentImpl.addField(
			fieldBuilder.name(
				name
			).value(
				value
			).build());
	}

	protected void addFieldValues(String name, Object[] values) {
		FieldBuilder fieldBuilder = new FieldBuilderImpl();

		_documentImpl.addField(
			fieldBuilder.name(
				name
			).values(
				values
			).build());
	}

	protected Field buildField(String name, String... values) {
		Field field = new Field(name);

		field.setValues(values);

		return field;
	}

	protected void guardType(String name, Function<Mapping, Boolean> function) {
		Optional<Mapping> optional = _fieldRegistry.getMappingOptional(name);

		if (!optional.isPresent()) {
			throw new IllegalArgumentException(
				"Must either add unchecked, or register a mapping for field " +
					name);
		}

		Mapping mapping = optional.get();

		if (!function.apply(mapping)) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Values for field ", name, " must be of type ",
					mapping.getType()));
		}
	}

	protected void guardType(String name, String type) {
		guardType(name, mapping -> isType(mapping, type));
	}

	protected void guardTypeString(String name) {
		guardType(name, this::isTypeString);
	}

	protected boolean isType(Mapping mapping, String type) {
		return Objects.equals(type, mapping.getType());
	}

	protected boolean isTypeString(Mapping mapping) {
		String type = mapping.getType();

		if ("keyword".equals(type) || "text".equals(type)) {
			return true;
		}

		return false;
	}

	private final DocumentBuilderHelper _documentBuilderHelper;
	private final DocumentImpl _documentImpl = new DocumentImpl();
	private final FieldRegistry _fieldRegistry;

}