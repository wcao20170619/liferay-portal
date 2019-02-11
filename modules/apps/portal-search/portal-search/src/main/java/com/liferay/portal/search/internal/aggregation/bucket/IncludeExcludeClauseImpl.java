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

package com.liferay.portal.search.internal.aggregation.bucket;

import com.liferay.portal.search.aggregation.bucket.IncludeExcludeClause;

/**
 * @author Michael C. Han
 */
public class IncludeExcludeClauseImpl implements IncludeExcludeClause {

	public IncludeExcludeClauseImpl(String includeRegex, String excludeRegex) {
		_includeRegex = includeRegex;
		_excludeRegex = excludeRegex;
	}

	public IncludeExcludeClauseImpl(
		String[] includedValues, String[] excludedValues) {

		_includedValues = includedValues;
		_excludedValues = excludedValues;
	}

	@Override
	public String[] getExcludedValues() {
		return _excludedValues;
	}

	@Override
	public String getExcludeRegex() {
		return _excludeRegex;
	}

	@Override
	public String[] getIncludedValues() {
		return _includedValues;
	}

	@Override
	public String getIncludeRegex() {
		return _includeRegex;
	}

	private String[] _excludedValues;
	private String _excludeRegex;
	private String[] _includedValues;
	private String _includeRegex;

}