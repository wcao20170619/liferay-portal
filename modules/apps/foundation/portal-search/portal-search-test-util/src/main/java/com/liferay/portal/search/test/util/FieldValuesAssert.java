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

package com.liferay.portal.search.test.util;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;

/**
 * @author André de Oliveira
 */
public class FieldValuesAssert {

	public static void assertEquals(
		String message, Map<?, ?> expectedMap, Map<?, ?> actualMap) {

		Assert.assertEquals(
			message, _toString(expectedMap), _toString(actualMap));
	}

	public static void assertFieldValues(
		Map<String, String> expected, Document document, String message) {

		assertEquals(message, expected, _getFieldValues(null, document));
	}

	public static void assertFieldValues(
		Map<String, String> expected, String prefix, Document document,
		String message) {

		AssertUtils.assertEquals(
			message, expected, _getFieldValues(prefix, document));
	}

	private static Map<String, String> _getFieldValues(
		String prefix, Document document) {

		Map<String, Field> fieldsMap = document.getFields();

		Set<Entry<String, Field>> entrySet = fieldsMap.entrySet();

		Stream<Entry<String, Field>> entries = entrySet.stream();

		Stream<Entry<String, Field>> prefixedEntries = entries.filter(
			entry -> {
				String name = entry.getKey();

				if (prefix == null) {
					return name != null && !name.equals("");
				}

				return name.startsWith(prefix);
			});

		return prefixedEntries.collect(
			Collectors.toMap(
				Map.Entry::getKey,
				entry -> {
					Field field = entry.getValue();

					return field.getValue();
				}));
	}

	private static String _toString(Map<?, ?> map) {
		List<String> list = new ArrayList<>(map.size());

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			list.add(entry.toString());
		}

		Collections.sort(list);

		return list.toString();
	}

}