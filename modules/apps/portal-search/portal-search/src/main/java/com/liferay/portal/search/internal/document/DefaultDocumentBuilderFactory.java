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

package com.liferay.portal.search.internal.document;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.field.FieldType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = DocumentBuilderFactory.class)
public class DefaultDocumentBuilderFactory implements DocumentBuilderFactory {

	@Override
	public DocumentBuilder getBuilder() {
		return new DocumentBuilderImpl(fieldRegistry, documentTranslator);
	}

	@Override
	public void processSortFields(SearchContext searchContext) {
		Sort[] sorts = searchContext.getSorts();

		if (sorts == null) {
			return;
		}

		for (Sort sort : sorts) {
			sort.setFieldName(getSortableName(sort.getFieldName()));
		}
	}

	protected String getSortableName(String name) {
		if (fieldRegistry.isTheFieldType(name, FieldType.STRING_ANALYZED) ||
			fieldRegistry.isTheFieldType(name, FieldType.DATE) ||
			fieldRegistry.isTheFieldType(name, FieldType.NUMERIC)) {

			return Field.getSortableFieldName(name);
		}

		return name;
	}

	@Reference
	protected DocumentTranslator documentTranslator;

	@Reference
	protected FieldRegistry fieldRegistry;

}