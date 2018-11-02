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

//import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;


//import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.field.FieldRegistry;

/**
 * @author Wade Cao
 */
@Component(
	immediate = true, service = DocumentTranslator.class
) 
public class DocumentTranslatorImpl implements DocumentTranslator {

	@Override
	public Document translate(
			com.liferay.portal.search.document.Document searchDocument,
			Document document,
			FieldRegistry fieldRegistry) {
		
		Map<String, Field> documentFields = document.getFields();
		
//		Map<String, Field> fields = _processFields(
//				documentFields, fieldRegistry);
		
//		documentFields.clear();
//		documentFields.putAll(fields);
		documentFields.putAll(searchDocument.getFields());
		
		return document;
	}
	
//	private Map<String, Field> _processFields(
//		Map<String, Field> fields, 
//		FieldRegistry fieldRegistry) {
//
//	Map<String, Field> filedsMap = new HashMap<>();
//	Map<String, Field> fieldsSortable = new HashMap<>();
//	
//	fields.forEach((key,value)->{
//		if (key.endsWith(Field.SORTABLE_FIELD_SUFFIX)) {
//			
//			int postFixIndex = key.lastIndexOf(
//				StringPool.UNDERLINE + Field.SORTABLE_FIELD_SUFFIX);
//			
//			String fieldName = key.substring(0, postFixIndex);
//		
//			String regName = fieldRegistry.getSortableName(fieldName);
//			
//			value.setName(regName);
//			
//			fieldsSortable.put(regName, value);
//		} else {
//			filedsMap.put(key, value);
//		}
//	});
//	
//	fieldsSortable.forEach((key,value)->{
//		if (filedsMap.get(key) == null) {
//			filedsMap.put(key, value);
//		}
//    });
//	return filedsMap;
//  }

}
