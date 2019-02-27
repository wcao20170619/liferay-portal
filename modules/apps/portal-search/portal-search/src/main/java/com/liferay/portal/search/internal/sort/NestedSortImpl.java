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

package com.liferay.portal.search.internal.sort;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.sort.NestedSort;

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
@ProviderType
public class NestedSortImpl implements NestedSort {

	public NestedSortImpl(String path) {
		_path = path;
	}

	@Override
	public Query getFilterQuery() {
		return _filterQuery;
	}

	@Override
	public Integer getMaxChildren() {
		return _maxChildren;
	}

	@Override
	public NestedSort getNestedSort() {
		return _nestedSort;
	}

	@Override
	public String getPath() {
		return _path;
	}

	@Override
	public void setFilterQuery(Query filterQuery) {
		_filterQuery = filterQuery;
	}

	@Override
	public void setMaxChildren(int maxChildren) {
		_maxChildren = maxChildren;
	}

	@Override
	public void setNestedSort(NestedSort nestedSort) {
		_nestedSort = nestedSort;
	}

	private Query _filterQuery;
	private Integer _maxChildren;
	private NestedSort _nestedSort;
	private final String _path;

}