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

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.field.FieldRegistry;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = DocumentTranslator.class)
public class DocumentTranslatorImpl implements DocumentTranslator {

	@Override
	public Document translate(
		com.liferay.portal.search.document.Document searchDocument,
		Document document, FieldRegistry fieldRegistry) {

		Map<String, Field> documentFields = document.getFields();

		documentFields.putAll(searchDocument.getFields());

		return document;
	}

}