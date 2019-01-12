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
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author André de Oliveira
 */
public class JSONUtil {

	public static JSONArray mergeStable(
		JSONArray jsonArray1, JSONArray jsonArray2, JSONFactory jsonFactory) {

		LinkedHashMap<String, JSONObject> linkedHashMap = new LinkedHashMap<>();

		putAll(linkedHashMap, jsonArray1);

		putAll(linkedHashMap, jsonArray2);

		JSONArray jsonArray3 = jsonFactory.createJSONArray();

		linkedHashMap.forEach((key, value) -> jsonArray3.put(value));

		return jsonArray3;
	}

	protected static void putAll(
		Map<String, JSONObject> map, JSONArray jsonArray) {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONArray namesJSONArray = jsonObject.names();

			String name = (String)namesJSONArray.get(0);

			map.put(name, jsonObject);
		}
	}

}