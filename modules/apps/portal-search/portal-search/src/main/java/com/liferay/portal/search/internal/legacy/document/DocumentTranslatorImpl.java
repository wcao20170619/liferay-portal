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

package com.liferay.portal.search.internal.legacy.document;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.document.Field;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
@Component(immediate = true, service = DocumentTranslator.class)
public class DocumentTranslatorImpl implements DocumentTranslator {

	@Override
	public void translate(
		com.liferay.portal.search.document.Document document,
		com.liferay.portal.kernel.search.Document legacyDocument) {

		Map<String, Field> fields = document.getFields();

		fields.forEach((name, field) -> legacyDocument.add(translate(field)));
	}

	protected com.liferay.portal.kernel.search.Field translate(Field field) {
		com.liferay.portal.kernel.search.Field legacyField =
			new com.liferay.portal.kernel.search.Field(field.getName());

		List<Object> values = field.getValues();

		legacyField.setValues(ArrayUtil.toStringArray(values.toArray()));

		return legacyField;
	}

}