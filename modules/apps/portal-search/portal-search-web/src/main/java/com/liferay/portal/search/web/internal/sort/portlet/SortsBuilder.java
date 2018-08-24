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

package com.liferay.portal.search.web.internal.sort.portlet;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Sort;

/**
 * @author Wade Cao
 */
public class SortsBuilder {

	public SortsBuilder(SortPortletPreferences sortPortletPreferences) {
		_sortPortletPreferences = sortPortletPreferences;
	}

	public Sort[] build() {
		Sort[] sorts = null;

		if (_fieldValues == null) {
			return sorts;
		}

		sorts = new Sort[_fieldValues.length];

		for (int i = 0; i < _fieldValues.length; i++) {
			String field = _fieldValues[i];
			boolean reverse = false;

			if (field.equals("")) {
				sorts[i] = new Sort(null, Sort.SCORE_TYPE, reverse);
				continue;
			}

			if (_fieldValues[i].endsWith("+")) {
				field = field.substring(0, field.lastIndexOf("+"));
			}
			else if (_fieldValues[i].endsWith("-")) {
				field = field.substring(0, field.lastIndexOf("-"));
				reverse = true;
			}

			int type = getTypeValueByFieldValue(_fieldValues[i]);

			sorts[i] = new Sort(field, type, reverse);
		}

		return sorts;
	}

	public void setFieldValues(String[] fieldValues) {
		_fieldValues = fieldValues;
	}

	protected int getTypeValueByFieldValue(String fieldValue) {
		int ret = 3;
		JSONArray fieldsJSONArray =
			_sortPortletPreferences.getFieldsJSONArray();

		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject jsonObject = fieldsJSONArray.getJSONObject(i);

			String fieldVal = jsonObject.getString("field");

			if (fieldValue.equals(fieldVal)) {
				ret = jsonObject.getInt("type");

				break;
			}
		}

		return ret;
	}

	private String[] _fieldValues;
	private final SortPortletPreferences _sortPortletPreferences;

}