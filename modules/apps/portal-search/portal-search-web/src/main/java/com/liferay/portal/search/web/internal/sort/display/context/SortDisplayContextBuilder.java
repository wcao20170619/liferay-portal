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

package com.liferay.portal.search.web.internal.sort.display.context;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.search.web.internal.sort.portlet.SortPortletPreferences;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Wade Cao
 */
public class SortDisplayContextBuilder implements Serializable {

	public SortDisplayContextBuilder(
		SortPortletPreferences sortPortletPreferences) {

		_sortPortletPreferences = sortPortletPreferences;
	}

	public SortDisplayContext build() {
		SortDisplayContext sortDisplayContext = new SortDisplayContext(
			_sortPortletPreferences);

		sortDisplayContext.setParameterName(_parameterName);

		if (_selectedFields.size() > 0) {
			sortDisplayContext.setParameterValue(
				_selectedFields.get(_selectedFields.size() - 1));
		}

		sortDisplayContext.setSortTermDisplayContexts(
			buildTermDisplayContexts());

		return sortDisplayContext;
	}

	public void setCurrentURL(String currentURL) {
		_currentURL = currentURL;
	}

	public void setParameterName(String parameterName) {
		_parameterName = parameterName;
	}

	public void setParameterValues(String... parameterValues) {
		if (parameterValues == null) {
			return;
		}

		_selectedFields = Arrays.asList(
			Objects.requireNonNull(parameterValues));
	}

	protected SortTermDisplayContext buildTermDisplayContext(
		String label, String field) {

		SortTermDisplayContext sortTermDisplayContext =
			new SortTermDisplayContext();

		sortTermDisplayContext.setLabel(label);
		sortTermDisplayContext.setField(field);
		sortTermDisplayContext.setFieldURL(_currentURL);
		sortTermDisplayContext.setSelected(_selectedFields.contains(field));

		return sortTermDisplayContext;
	}

	protected List<SortTermDisplayContext> buildTermDisplayContexts() {
		List<SortTermDisplayContext> sortTermDisplayContexts =
			new ArrayList<>();

		JSONArray fieldsJSONArray =
			_sortPortletPreferences.getFieldsJSONArray();

		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject jsonObject = fieldsJSONArray.getJSONObject(i);

			sortTermDisplayContexts.add(
				buildTermDisplayContext(
					jsonObject.getString("label"),
					jsonObject.getString("field")));
		}

		return sortTermDisplayContexts;
	}

	private static final long serialVersionUID = 1L;

	private String _currentURL;
	private String _parameterName;
	private List<String> _selectedFields = Collections.emptyList();
	private final SortPortletPreferences _sortPortletPreferences;

}