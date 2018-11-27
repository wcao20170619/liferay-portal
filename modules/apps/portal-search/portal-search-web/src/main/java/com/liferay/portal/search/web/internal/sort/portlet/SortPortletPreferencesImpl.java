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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.util.PortletPreferencesHelper;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author Wade Cao
 */
public class SortPortletPreferencesImpl implements SortPortletPreferences {

	public SortPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferences) {

		_portletPreferencesHelper = new PortletPreferencesHelper(
			portletPreferences);
	}

	@Override
	public JSONArray getFieldsJSONArray() {
		String fieldsString = getFieldsString();

		if (Validator.isBlank(fieldsString)) {
			return getDefaultFieldsJSONArray();
		}

		try {
			return JSONFactoryUtil.createJSONArray(fieldsString);
		}
		catch (JSONException jsone) {
			_log.error(
				"Unable to create a JSON array from: " + fieldsString, jsone);

			return getDefaultFieldsJSONArray();
		}
	}

	@Override
	public String getFieldsString() {
		return _portletPreferencesHelper.getString(
			SortPortletPreferences.PREFERENCE_KEY_FIELDS, StringPool.BLANK);
	}

	@Override
	public String getParameterName() {
		return "sort";
	}

	@Override
	public String getTypeParameterName() {
		return "type";
	}

	protected JSONArray getDefaultFieldsJSONArray() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < _LABELS.length; i++) {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

			jsonObject.put("field", _FIELDS[i]);
			jsonObject.put("label", _LABELS[i]);
			jsonObject.put("type", _TYPES[i]);

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	private static final String[] _FIELDS =
		{"", "modified-", "modified+", "createDate-", "createDate+", "title"};

	private static final String[] _LABELS = {
		"Relevance", "Modified", "Modified (oldest first)", "Created",
		"Created (oldest first)", "Title"
	};

	private static final String[] _TYPES = {
		String.valueOf(Sort.SCORE_TYPE), String.valueOf(Sort.LONG_TYPE),
		String.valueOf(Sort.LONG_TYPE), String.valueOf(Sort.LONG_TYPE),
		String.valueOf(Sort.LONG_TYPE), String.valueOf(Sort.STRING_TYPE)
	};

	private static final Log _log = LogFactoryUtil.getLog(
		SortPortletPreferencesImpl.class);

	private final PortletPreferencesHelper _portletPreferencesHelper;

}