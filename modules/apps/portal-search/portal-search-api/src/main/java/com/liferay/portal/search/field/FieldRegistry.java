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

package com.liferay.portal.search.field;

import aQute.bnd.annotation.ProviderType;

/**
 * @author Wade Cao
 */
@ProviderType
public interface FieldRegistry {

	public void addField(String name, String fieldName, MappedField field);

	public boolean isSortableTextField(String name);

	public boolean isTheFieldType(String name, String type);

	public boolean putPropertiesMappingIndex(
		String[] indexNames, String mappingName, String name);

	public void registerFieldAnalyzer(String name, String analyzer);

	public void registerFieldBoost(String name, float boost);

	public void registerFieldFormat(String name, String format);

	public void registerFieldStored(String name, boolean stored);

	public void registerFieldTermVector(String name, String termVector);

	public void registerFieldType(String name, String type);

	public void registerSortableTextField(String name);

}