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

import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wade Cao
 */
public enum SortTypePreference {

	CUSTOM_TYPE("custom-type", Sort.CUSTOM_TYPE),
	DOC_TYPE("doc-type", Sort.DOC_TYPE),
	DOUBLE_TYPE("double-type", Sort.DOUBLE_TYPE),
	FLOAT_TYPE("float-type", Sort.FLOAT_TYPE),
	GEO_DISTANCE_TYPE("geo-distance-type", Sort.GEO_DISTANCE_TYPE),
	INT_TYPE("int-type", Sort.INT_TYPE), LONG_TYPE("long-type", Sort.LONG_TYPE),
	SCORE_TYPE("score-type", Sort.SCORE_TYPE),
	STRING_TYPE("string-type", Sort.STRING_TYPE);

	public static SortTypePreference[] getSortTypePreferences() {
		return values();
	}

	public String getPreferenceString() {
		return _preferenceString;
	}

	public int getSortType() {
		return _sortType;
	}

	public SortTypePreference getSortTypePreference(String preferenceString) {
		if (Validator.isNull(preferenceString)) {
			return SortTypePreference.STRING_TYPE;
		}

		SortTypePreference sortTypePreference = _sortTypePreferences.get(
			preferenceString);

		if (sortTypePreference == null) {
			throw new IllegalArgumentException(
				"The string " + preferenceString +
					" does not correspond to a valid sort type preference");
		}

		return sortTypePreference;
	}

	private SortTypePreference(String preferenceString, int sortType) {
		_preferenceString = preferenceString;
		_sortType = sortType;
	}

	private static final Map<String, SortTypePreference> _sortTypePreferences =
		new HashMap<>();

	static {
		for (SortTypePreference sortTypePreference : values()) {
			_sortTypePreferences.put(
				sortTypePreference._preferenceString, sortTypePreference);
		}
	}

	private final String _preferenceString;
	private final int _sortType;

}