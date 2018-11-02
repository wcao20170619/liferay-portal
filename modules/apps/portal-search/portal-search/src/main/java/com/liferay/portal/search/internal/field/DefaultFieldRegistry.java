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

package com.liferay.portal.search.internal.field;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.search.field.FieldRegistry;

/**
 * @author Wade Cao
 */
@Component(
       immediate = true, service = FieldRegistry.class
)

public class DefaultFieldRegistry implements FieldRegistry {

	@Override
	public void registerFieldType(String name, String type) {
		_fieldTypeMap.put(name, type);
	}
	
	@Override
	public String getSortableName(String name) {
		String type = _fieldTypeMap.get(name);
		
		if (type != null && type.equals("text")) {
			return name.concat(StringPool.UNDERLINE).concat(Field.SORTABLE_FIELD_SUFFIX);
		}
		
		return name;	
	}
	
	@Override
	public boolean isTextField(String name) {
		return _fieldTypeMap.get(name) != null && 
				_fieldTypeMap.get(name).equals("text");
	}
	
	@Activate
	protected void activate() {

		_registerFieldTypes();
		
		Set<String> defaultSortableTextFields =
			SetUtil.fromArray(
				PropsUtil.getArray(PropsKeys.INDEX_SORTABLE_TEXT_FIELDS));
		
		defaultSortableTextFields.forEach(
			defaultSortableTextField->{
				registerFieldType(defaultSortableTextField, "text");
			});
	}
	
	private void _registerFieldTypes() {
//		registerFieldType("ancestorOrganizationIds", "keyword");
		registerFieldType("articleId", "text");
//		registerFieldType("assetCategoryId", "keyword");
//		registerFieldType("assetCategoryIds", "keyword");
		registerFieldType("assetCategoryTitle", "text"); //
		registerFieldType("assetCategoryTitles", "text");//
		registerFieldType("assetCount", "text"); //
//		registerFieldType("assetTagId", "keyword"); 
//		registerFieldType("assetTagIds", "keyword"); 
		registerFieldType("assetTagNames", "text"); 
//		registerFieldType("assetVocabularyIds", "keyword"); 
//		registerFieldType("assetVocabularyId", "keyword"); 
		registerFieldType("calendarId", "text"); //
		registerFieldType("categoryId", "text");//
		registerFieldType("classNameId", "text"); //
		registerFieldType("classPK", "text"); //
//		registerFieldType("classTypeId", "keyword");
//		registerFieldType("companyId", "keyword");
//		registerFieldType("configurationModelAttributeName", "keyword");	
//		registerFieldType("configurationModelFactoryPid", "keyword");
//		registerFieldType("configurationModelId", "keyword");
		registerFieldType("content", "text");
		registerFieldType("country", "text");//
//		registerFieldType("createDate", "date");
//		registerFieldType("dataRepositoryId", "keyword");
		registerFieldType("ddmContent", "text");
//		registerFieldType("ddmStructureKey", "keyword");
//		registerFieldType("ddmTemplateKey", "keyword");
//		registerFieldType("defaultLanguageId", "keyword");
		registerFieldType("description", "text");		
//		registerFieldType("discussion", "keyword");
//		registerFieldType("displayDate", "date");
//		registerFieldType("emailAddress", "keyword");		
//		registerFieldType("endTime", "date");
//		registerFieldType("entryClassName", "keyword");
//		registerFieldType("entryClassPK", "keyword");
//		registerFieldType("expirationDate", "date");
//		registerFieldType("extension", "keyword");		
//		registerFieldType("fileEntryTypeId", "keyword");
//		registerFieldType("folderId", "keyword");
		registerFieldType("geoLocation", "geo_point");		
//		registerFieldType("groupId", "keyword");
//		registerFieldType("groupIds", "keyword");
//		registerFieldType("groupRoleId", "keyword");
//		registerFieldType("head", "keyword");
//		registerFieldType("hidden", "keyword");		
//		registerFieldType("id", "keyword");
//		registerFieldType("languageId", "keyword");
//		registerFieldType("layoutUuid", "keyword");		
//		registerFieldType("leftOrganizationId", "keyword");
//		registerFieldType("mimeType", "keyword");
//		registerFieldType("modified", "date");
//		registerFieldType("nodeId", "keyword");
//		registerFieldType("organizationId", "keyword");		
//		registerFieldType("organizationIds", "keyword");
//		registerFieldType("parentCategoryId", "keyword");
//		registerFieldType("parentCategoryIds", "keyword");		
//		registerFieldType("parentOrganizationId", "keyword");
//		registerFieldType("path", "keyword");
//		registerFieldType("priority", "double");
//		registerFieldType("properties", "keyword");
//		registerFieldType("publishDate", "date");		
//		registerFieldType("readCount", "keyword");
//		registerFieldType("recordSetId", "keyword");
//		registerFieldType("removedByUser", "keyword");		
//		registerFieldType("removedDate", "date");
//		registerFieldType("rightOrganizationId", "keyword");
//		registerFieldType("roleId", "keyword");
//		registerFieldType("roleIds", "keyword");
//		registerFieldType("rootEntryClassName", "keyword");		
//		registerFieldType("rootEntryClassPK", "keyword");
//		registerFieldType("scopeGroupId", "keyword");
//		registerFieldType("screenName", "keyword");		
//		registerFieldType("size", "keyword");
//		registerFieldType("status", "keyword");
		registerFieldType("subtitle", "text");
//		registerFieldType("teamIds", "keyword");
//		registerFieldType("threadId", "keyword");		
		registerFieldType("title", "text");
//		registerFieldType("treePath", "keyword");
//		registerFieldType("type", "keyword");		
//		registerFieldType("uid", "keyword");
//		registerFieldType("userGroupId", "keyword");
//		registerFieldType("userGroupIds", "keyword");
//		registerFieldType("userId", "keyword");
//		registerFieldType("userName", "keyword");		
//		registerFieldType("version", "keyword");
//		registerFieldType("visible", "keyword");
	}
	
	private Map<String, String> _fieldTypeMap = new HashMap<String, String>();
	
}
