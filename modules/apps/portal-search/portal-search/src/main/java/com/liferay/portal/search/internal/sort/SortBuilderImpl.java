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

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.sort.SortBuilder;

/**
 * @author Wade Cao
 */
public class SortBuilderImpl implements SortBuilder {

	public SortBuilderImpl(
		FieldRegistry fieldRegistry, SortTranslator sortTranslator) {

		_fieldRegistry = fieldRegistry;
		_sortTranslator = sortTranslator;
	}

	@Override
	public com.liferay.portal.kernel.search.Sort build() {
		String sortableName = getSortableName(_field);

		return _sortTranslator.translate(new SortImpl(sortableName, _reverse));
	}

	@Override
	public Sort buildSearchSort() {
		String sortableName = getSortableName(_field);

		return new SortImpl(sortableName, _reverse);
	}

	@Override
	public SortBuilder setField(String field) {
		_field = field;

		return this;
	}

	@Override
	public SortBuilder setReverse(boolean reverse) {
		_reverse = reverse;

		return this;
	}

	protected String getSortableName(String name) {
		if (_fieldRegistry.isSortableTextField(name)) {
			return Field.getSortableFieldName(name);
		}

		return name;
	}

	private String _field;
	private final FieldRegistry _fieldRegistry;
	private boolean _reverse;
	private final SortTranslator _sortTranslator;

}