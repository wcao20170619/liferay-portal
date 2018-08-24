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
import com.liferay.portal.search.web.internal.sort.portlet.SortPortletPreferences;

import java.io.Serializable;

import java.util.List;

/**
 * @author Wade Cao
 */
public class SortDisplayContext implements Serializable {

	public SortDisplayContext(SortPortletPreferences sortPortletPreferences) {
		_portletPreferences = sortPortletPreferences;
	}

	public String getParameterName() {
		return _parameterName;
	}

	public String getParameterValue() {
		return _parameterValue;
	}

	public List<SortTermDisplayContext> getSortTermDisplayContexts() {
		return _sortTermDisplayContexts;
	}

	public boolean isRenderNothing() {
		if (_portletPreferences == null) {
			return true;
		}

		JSONArray jsonArray = _portletPreferences.getFieldsJSONArray();

		if (jsonArray == null) {
			return true;
		}

		if (jsonArray.length() <= 0) {
			return true;
		}

		return false;
	}

	public void setParameterName(String parameterName) {
		_parameterName = parameterName;
	}

	public void setParameterValue(String parameterValue) {
		_parameterValue = parameterValue;
	}

	public void setSortTermDisplayContexts(
		List<SortTermDisplayContext> sortTermDisplayContexts) {

		_sortTermDisplayContexts = sortTermDisplayContexts;
	}

	private static final long serialVersionUID = 1L;

	private String _parameterName;
	private String _parameterValue;
	private final SortPortletPreferences _portletPreferences;
	private List<SortTermDisplayContext> _sortTermDisplayContexts;

}