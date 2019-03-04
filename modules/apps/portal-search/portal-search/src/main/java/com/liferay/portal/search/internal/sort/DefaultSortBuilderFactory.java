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

import com.liferay.portal.search.field.FieldRegistryManager;
import com.liferay.portal.search.sort.SortBuilder;
import com.liferay.portal.search.sort.SortBuilderFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = SortBuilderFactory.class)
public class DefaultSortBuilderFactory implements SortBuilderFactory {

	@Override
	public SortBuilder getBuilder() {
		return new SortBuilderImpl(
			_fieldRegistryManager.getFieldRegistry(), _sortTranslator);
	}

	@Override
	public SortBuilder getBuilder(String indexName) {
		return new SortBuilderImpl(
			_fieldRegistryManager.getFieldRegistry(indexName), _sortTranslator);
	}

	@Reference(unbind = "-")
	protected void setFieldRegistryManager(
		FieldRegistryManager fieldRegistryManager) {

		_fieldRegistryManager = fieldRegistryManager;
	}

	@Reference(unbind = "-")
	protected void setSortTranslator(SortTranslator sortTranslator) {
		_sortTranslator = sortTranslator;
	}

	private FieldRegistryManager _fieldRegistryManager;
	private SortTranslator _sortTranslator;

}