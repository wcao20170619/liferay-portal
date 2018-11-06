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
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.search.document.Document;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author Wade Cao
 */
public class DocumentImpl implements Document {

	public DocumentImpl(Map<String, Field> fields) {
		_fields = fields;
	}

	@Override
	public String get(Locale locale, String name) {
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

	@Override
	public String get(Locale locale, String name, String defaultName) {
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

	@Override
	public String get(String name) {
		Field field = getField(name);

		if (field == null) {
			return StringPool.BLANK;
		}

		return field.getValue();
	}

	@Override
	public String get(String name, String defaultName) {
		Field field = getField(name);

		if (field == null) {
			return get(defaultName);
		}

		return field.getValue();
	}

	@Override
	public Date getDate(String name) throws ParseException {
		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			_INDEX_DATE_FORMAT_PATTERN);

		return dateFormat.parse(get(name));
	}

	@Override
	public Field getField(String name) {
		return doGetField(name, false);
	}

	@Override
	public Map<String, Field> getFields() {
		return _fields;
	}

	@Override
	public String getPortletId() {
		String uid = getUID();

		int pos = uid.indexOf(_UID_PORTLET);

		return uid.substring(0, pos);
	}

	@Override
	public String getUID() {
		Field field = getField(Field.UID);

		if (field == null) {
			throw new RuntimeException("UID is not set");
		}

		return field.getValue();
	}

	@Override
	public String[] getValues(String name) {
		Field field = getField(name);

		if (field == null) {
			return new String[] {StringPool.BLANK};
		}

		return field.getValues();
	}

	@Override
	public boolean hasField(String name) {
		if (_fields.containsKey(name)) {
			return true;
		}

		return false;
	}

	protected Field doGetField(String name, boolean createIfNew) {
		Field field = _fields.get(name);

		if ((field == null) && createIfNew) {
			field = new Field(name);

			_fields.put(name, field);
		}

		return field;
	}

	private static final String _INDEX_DATE_FORMAT_PATTERN = PropsUtil.get(
		PropsKeys.INDEX_DATE_FORMAT_PATTERN);

	private static final String _UID_PORTLET = "_PORTLET_";

	private final Map<String, Field> _fields;

}