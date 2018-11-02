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

import java.util.Collection;
import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.sort.SortBuilder;
import com.liferay.portal.search.sort.SortBuilderFactory;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = SortBuilderFactory.class)
public class DefaultSortBuilderFactory implements SortBuilderFactory {

	@Override
	public SortBuilder getBuilder() {
		return new SortBuilderImpl(_fieldRegistry, _sortTranslator);
	}
	
	@Reference
	private SortTranslator _sortTranslator;
	
	@Reference
	private FieldRegistry _fieldRegistry;

}
