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

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexResponse;
import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.field.FieldType;
import com.liferay.portal.search.field.MappedField;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = FieldRegistry.class)
public class DefaultFieldRegistry implements FieldRegistry {

	public void addField(String name, String fieldName, MappedField field) {
		MappedField mappedFieldType = _createMappedFieldIfNotExist(name);

		mappedFieldType.addField(fieldName, field);
	}

	@Override
	public boolean isSortableTextField(String name) {
		return _sortableTextFields.contains(name);
	}

	@Override
	public boolean isTheFieldType(String name, String dataType) {
		MappedField mappedFieldType = _fieldTypeMap.get(name);

		if (mappedFieldType == null) {
			return false;
		}

		return mappedFieldType.isFieldTypeMatched(dataType);
	}

	public boolean putPropertiesMappingIndex(
		String[] indexNames, String mappingName, String name) {

		boolean retValue = false;

		MappedField mappedField = _fieldTypeMap.get(name);

		if (mappedField != null) {
			String mappingSource =
				"{\"properties\": {" + mappedField.getMappingSource(name) +
					"} }";

			PutMappingIndexRequest putMappingIndexRequest =
				new PutMappingIndexRequest(
					indexNames, mappingName, mappingSource);

			PutMappingIndexResponse putMappingIndexResponse =
				searchEngineAdapter.execute(putMappingIndexRequest);

			retValue = putMappingIndexResponse.isAcknowledged();
		}

		return retValue;
	}

	@Override
	public void registerFieldAnalyzer(String name, String analyzer) {
		MappedField mappedFieldType = _createMappedFieldIfNotExist(name);

		mappedFieldType.setAnalyzer(analyzer);
	}

	@Override
	public void registerFieldBoost(String name, float boost) {
		MappedField mappedFieldType = _createMappedFieldIfNotExist(name);

		mappedFieldType.setBoost(boost);
	}

	@Override
	public void registerFieldFormat(String name, String format) {
		MappedField mappedFieldType = _createMappedFieldIfNotExist(name);

		mappedFieldType.setFormat(format);
	}

	@Override
	public void registerFieldStored(String name, boolean stored) {
		MappedField mappedFieldType = _createMappedFieldIfNotExist(name);

		mappedFieldType.setStored(stored);
	}

	@Override
	public void registerFieldTermVector(String name, String termVector) {
		MappedField mappedFieldType = _createMappedFieldIfNotExist(name);

		mappedFieldType.setTermVector(termVector);
	}

	@Override
	public void registerFieldType(String name, String type) {
		FieldType fieldDataType = FieldType.getFieldType(type);

		if (fieldDataType == null) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"mapper [", name, "] cannot be changed from type [", type,
					"]"));
		}

		MappedField mappedFieldType = new MappedField(fieldDataType);

		_fieldTypeMap.put(name, mappedFieldType);
	}

	@Override
	public void registerSortableTextField(String name) {
		_sortableTextFields.add(name);
	}

	@Activate
	protected void activate() {
		_registerFieldTypes();

		Set<String> defaultSortableTextFields = SetUtil.fromArray(
			PropsUtil.getArray(PropsKeys.INDEX_SORTABLE_TEXT_FIELDS));

		defaultSortableTextFields.forEach(
			defaultSortableTextField -> {
				registerSortableTextField(defaultSortableTextField);
				registerFieldType(defaultSortableTextField, "text");
			});
	}

	@Reference
	protected SearchEngineAdapter searchEngineAdapter;

	private MappedField _createMappedFieldIfNotExist(String name) {
		MappedField mappedFieldType = _fieldTypeMap.get(name);

		if (mappedFieldType == null) {
			mappedFieldType = new MappedField();

			_fieldTypeMap.put(name, mappedFieldType);
		}

		return mappedFieldType;
	}

	private void _registerFieldTypes() {
		registerFieldType("articleId", "text");
		registerFieldType("assetCategoryTitle", "text");
		registerFieldType("assetCategoryTitles", "text");
		registerFieldType("assetCount", "text");
		registerFieldType("assetTagNames", "text");
		registerFieldType("calendarId", "text");
		registerFieldType("categoryId", "text");
		registerFieldType("classNameId", "text");
		registerFieldType("classPK", "text");
		registerFieldType("content", "text");
		registerFieldType("country", "text");
		registerFieldType("ddmContent", "text");
		registerFieldType("description", "text");
		registerFieldType("geoLocation", "geo_point");
		registerFieldType("subtitle", "text");
		registerFieldType("title", "text");
	}

	private final Map<String, MappedField> _fieldTypeMap = new HashMap<>();
	private final Set<String> _sortableTextFields = new HashSet<>();

}