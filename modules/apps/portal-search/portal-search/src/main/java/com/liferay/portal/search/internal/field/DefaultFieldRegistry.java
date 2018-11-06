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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.search.field.FieldRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = FieldRegistry.class)
public class DefaultFieldRegistry implements FieldRegistry {

	@Override
	public String getSortableName(String name) {
		String type = _fieldTypeMap.get(name);

		if ((type != null) && type.equals("text")) {
			return name.concat(
				StringPool.UNDERLINE).concat(Field.SORTABLE_FIELD_SUFFIX);
		}

		return name;
	}

	@Override
	public boolean isTextField(String name) {
		String value = _fieldTypeMap.get(name);

		if ((value != null) && value.equals("text")) {
			return true;
		}

		return false;
	}

	@Override
	public void registerFieldType(String name, String type) {
		_fieldTypeMap.put(name, type);
	}

	@Activate
	protected void activate() {
		_registerFieldTypes();

		Set<String> defaultSortableTextFields = SetUtil.fromArray(
			PropsUtil.getArray(PropsKeys.INDEX_SORTABLE_TEXT_FIELDS));

		defaultSortableTextFields.forEach(
			defaultSortableTextField -> {
				registerFieldType(defaultSortableTextField, "text");
			});
	}

	private void _registerFieldTypes() {
		registerFieldType("articleId", "text");
		registerFieldType("assetCategoryTitle", "text");
		registerFieldType("assetCategoryTitles", "text");
		registerFieldType("assetCount", "text");
		registerFieldType("assetTagNames", "text");
		registerFieldType("calendarId", "text");
		registerFieldType("categoryId", "text");
		registerFieldType("classNameId", "text");
		registerFieldType("classPK", "text");
		registerFieldType("content", "text");
		registerFieldType("country", "text");
		registerFieldType("ddmContent", "text");
		registerFieldType("description", "text");
		registerFieldType("geoLocation", "geo_point");
		registerFieldType("subtitle", "text");
		registerFieldType("title", "text");
	}

	private final Map<String, String> _fieldTypeMap = new HashMap<>();

}