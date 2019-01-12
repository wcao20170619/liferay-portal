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

package com.liferay.portal.search.elasticsearch6.internal.index.mapping;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.function.Function;

/**
 * @author André de Oliveira
 */
public class DynamicTemplatesMerger {

	public DynamicTemplatesMerger(
		JSONFactory jsonFactory, Function<String, String> getMappingsFunction) {

		_jsonFactory = jsonFactory;
		_getMappingsFunction = getMappingsFunction;
	}

	public String mergeDynamicTemplates(String source, String typeName) {
		JSONObject sourceJSONObject = createJSONObject(source);

		JSONObject sourceTypeJSONObject = sourceJSONObject;

		if (sourceJSONObject.has(typeName)) {
			sourceTypeJSONObject = sourceJSONObject.getJSONObject(typeName);
		}

		JSONArray sourceTypeTemplatesJSONArray =
			sourceTypeJSONObject.getJSONArray("dynamic_templates");

		if (sourceTypeTemplatesJSONArray == null) {
			return sourceJSONObject.toString();
		}

		String mappings = _getMappingsFunction.apply(typeName);

		JSONObject mappingsJSONObject = createJSONObject(mappings);

		JSONObject typeJSONObject = mappingsJSONObject.getJSONObject(typeName);

		JSONArray typeTemplatesJSONArray = typeJSONObject.getJSONArray(
			"dynamic_templates");

		sourceTypeJSONObject.put(
			"dynamic_templates",
			JSONUtil.mergeStable(
				typeTemplatesJSONArray, sourceTypeTemplatesJSONArray,
				_jsonFactory));

		return sourceJSONObject.toString();
	}

	protected JSONObject createJSONObject(String mappings) {
		try {
			return _jsonFactory.createJSONObject(mappings);
		}
		catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}
	}

	private final Function<String, String> _getMappingsFunction;
	private final JSONFactory _jsonFactory;

}