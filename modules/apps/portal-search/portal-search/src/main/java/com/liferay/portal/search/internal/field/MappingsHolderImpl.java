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

import com.liferay.portal.search.field.Mapping;
import com.liferay.portal.search.field.MappingsHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

/**
 * @author André de Oliveira
 */
@Component(immediate = true, service = MappingsHolder.class)
public class MappingsHolderImpl implements MappingsHolder {

	@Override
	public Optional<Mapping> getMappingOptional(String fieldName) {
		return Optional.ofNullable(_fieldTypeMap.get(fieldName));
	}

	@Override
	public Map<String, Mapping> getMappings() {
		return Collections.unmodifiableMap(_fieldTypeMap);
	}

	@Override
	public void putMapping(String fieldName, Mapping mapping) {
		_fieldTypeMap.put(fieldName, mapping);
	}

	private final Map<String, Mapping> _fieldTypeMap = new HashMap<>();

}