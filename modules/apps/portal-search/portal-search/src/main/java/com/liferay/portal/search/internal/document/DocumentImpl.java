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

import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.Field;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Wade Cao
 */
public class DocumentImpl implements Document {

	public DocumentImpl() {
		_fields = new LinkedHashMap<>();
	}

	public DocumentImpl(DocumentImpl documentImpl) {
		_fields = new LinkedHashMap<>(documentImpl._fields);
	}

	@Override
	public Map<String, Field> getFields() {
		return _fields;
	}

	protected void addField(Field field) {
		_fields.put(field.getName(), field);
	}

	/*private Field doGetField(String name, boolean createIfNew) {
		Field field = null; // TODO _fields.get(name);

		if ((field == null) && createIfNew) {
			field = new Field(name);

			// TODO _fields.put(name, field);

		}

		return field;
	}

	private Field getField(String name) {
		return doGetField(name, false);
	}

	private String get(String name) {
		Field field = getField(name);

		if (field == null) {
			return StringPool.BLANK;
		}

		return field.getValue();
	}

	private String get(String name, String defaultName) {
		Field field = getField(name);

		if (field == null) {
			return get(defaultName);
		}

		return field.getValue();
	}

	private String getUID() {
		Field field = getField(Field.UID);

		if (field == null) {
			throw new RuntimeException("UID is not set");
		}

		return field.getValue();
	}

	private String get(Locale locale, String name) {
		if (locale == null) {
			return get(name);
		}

		String localizedName = Field.getLocalizedName(locale, name);

		Field field = getField(localizedName);

		if (field == null) {
			field = getField(name);
		}

		if (field == null) {
			return StringPool.BLANK;
		}

		return field.getValue();
	}

	private String get(Locale locale, String name, String defaultName) {
		if (locale == null) {
			return get(name, defaultName);
		}

		String localizedName = Field.getLocalizedName(locale, name);

		Field field = getField(localizedName);

		if (field == null) {
			localizedName = Field.getLocalizedName(locale, defaultName);

			field = getField(localizedName);
		}

		if (field == null) {
			return StringPool.BLANK;
		}

		return field.getValue();
	}

	private String[] getValues(String name) {
		Field field = getField(name);

		if (field == null) {
			return new String[] {StringPool.BLANK};
		}

		return field.getValues();
	}

	private boolean hasField(String name) {
		if (_fields.containsKey(name)) {
			return true;
		}

		return false;
	}

	private Date getDate(String name) throws ParseException {
		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			_INDEX_DATE_FORMAT_PATTERN);

		return dateFormat.parse(get(name));
	}

	private Stream<Field> getFieldsStream() {
		Collection<Field> fields = _fields.values();

		return fields.stream();
	}

	private String getPortletId() {
		String uid = getUID();

		int pos = uid.indexOf(_UID_PORTLET);

		return uid.substring(0, pos);
	}

	private static final String _INDEX_DATE_FORMAT_PATTERN = PropsUtil.get(
		PropsKeys.INDEX_DATE_FORMAT_PATTERN);

	private static final String _UID_PORTLET = "_PORTLET_";*/

	private final Map<String, Field> _fields;

}