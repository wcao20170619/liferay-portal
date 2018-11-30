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

package com.liferay.portal.search.test.util.document;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.field.MappingBuilder;
import com.liferay.portal.search.internal.field.LiferayIndexFieldRegistryContributor;
import com.liferay.portal.search.internal.field.MappingBuilderImpl;
import com.liferay.portal.search.internal.legacy.document.DocumentTranslator;
import com.liferay.portal.search.internal.legacy.document.DocumentTranslatorImpl;
import com.liferay.portal.search.spi.field.contributor.helper.FieldRegistryContributorHelper;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.IndexingFixture.IndexingFixtureListenerHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Before;

/**
 * @author Wade Cao
 */
public abstract class BaseDocumentTestCase extends BaseIndexingTestCase {

	@Override
	public void beforeActivate(
		IndexingFixtureListenerHelper indexingFixtureListenerHelper) {

		indexingFixtureListenerHelper.addFieldRegistryContributor(
			new LiferayIndexFieldRegistryContributor());

		indexingFixtureListenerHelper.addFieldRegistryContributor(
			fieldRegistryContributorHelper -> {
				fieldRegistryContributorHelper.register("firstName", "text");
				fieldRegistryContributorHelper.register("lastName", "text");
			});

		indexingFixtureListenerHelper.addFieldRegistryContributor(
			fieldRegistryContributorHelper ->
				new FieldRegistryInitializer(fieldRegistryContributorHelper) {
					{
						register(FIELD_DATE, "date");

						register(FIELD_DOUBLE, "double");
						register(FIELD_FLOAT, "float");
						register(FIELD_INTEGER, "integer");
						register(FIELD_LONG, "long");

						register(FIELD_DOUBLE_ARRAY, "double");
						register(FIELD_FLOAT_ARRAY, "float");
						register(FIELD_INTEGER_ARRAY, "integer");
						register(FIELD_LONG_ARRAY, "long");

						registerPremappedDate(Field.CREATE_DATE);
						registerPremappedDate(Field.MODIFIED_DATE);
					}
				});
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		populateNumberArrays();

		populateNumbers();

		populateDates();

		addDocuments(
			screenName -> document -> populate(document, screenName),
			getScreenNamesStream());
	}

	protected static <K, V> void put(V value, K key, Map<K, V> map) {
		map.put(key, value);
	}

	protected Document createDocument(String screenName) {
		DocumentBuilderFactory documentBuilderFactory =
			getDocumentBuilderFactory();

		DocumentBuilder documentBuilder = documentBuilderFactory.getBuilder();

		String dateString = dates.get(screenName);

		return documentBuilder.addString(
			"firstName", screenName.replaceFirst("user", StringPool.BLANK)
		).addString(
			"lastName", "Smith"
		).addString(
			"screenName", screenName
		).addDate(
			Field.MODIFIED_DATE, dateString
		).addDate(
			Field.CREATE_DATE, dateString
		).addDate(
			FIELD_DATE, dateString.substring(0, 8)
		).addDouble(
			FIELD_DOUBLE, doubles.get(screenName)
		).addFloat(
			FIELD_FLOAT, floats.get(screenName)
		).addInteger(
			FIELD_INTEGER, integers.get(screenName)
		).addLong(
			FIELD_LONG, longs.get(screenName)
		).addDoubles(
			FIELD_DOUBLE_ARRAY, doubleArrays.get(screenName)
		).addFloats(
			FIELD_FLOAT_ARRAY, floatArrays.get(screenName)
		).addIntegers(
			FIELD_INTEGER_ARRAY, integerArrays.get(screenName)
		).addLongs(
			FIELD_LONG_ARRAY, longArrays.get(screenName)
		).build();
	}

	protected Stream<String> getScreenNamesStream() {
		Collection<String> screenNames = doubles.keySet();

		return screenNames.stream();
	}

	protected void populate(
		com.liferay.portal.kernel.search.Document legacyDocument,
		String screenName) {

		DocumentTranslator documentTranslator = new DocumentTranslatorImpl();

		documentTranslator.translate(
			createDocument(screenName), legacyDocument);
	}

	protected void populateDates() throws Exception {
		put("20181001000000", "firstuser", dates);
		put("20181002000000", "seconduser", dates);
		put("20181003000000", "thirduser", dates);
		put("20181004000000", "fourthuser", dates);
		put("20181005000000", "fifthuser", dates);
		put("20181006000000", "sixthuser", dates);
	}

	protected void populateNumberArrays() {
		put(new Double[] {1e-11, 2e-11, 3e-11}, "firstuser", doubleArrays);
		put(new Double[] {1e-11, 2e-11, 4e-11}, "fourthuser", doubleArrays);
		put(new Double[] {1e-11, 2e-11, 5e-11}, "seconduser", doubleArrays);
		put(new Double[] {1e-11, 3e-11, 1e-11}, "fifthuser", doubleArrays);
		put(new Double[] {1e-11, 3e-11, 2e-11}, "thirduser", doubleArrays);
		put(new Double[] {2e-11, 1e-11, 1e-11}, "sixthuser", doubleArrays);

		put(new Float[] {8e-5F, 8e-5F, 8e-5F}, "firstuser", floatArrays);
		put(new Float[] {9e-5F, 8e-5F, 7e-5F}, "seconduser", floatArrays);
		put(new Float[] {9e-5F, 8e-5F, 9e-5F}, "thirduser", floatArrays);
		put(new Float[] {9e-5F, 9e-5F, 7e-5F}, "fourthuser", floatArrays);
		put(new Float[] {9e-5F, 9e-5F, 8e-5F}, "fifthuser", floatArrays);
		put(new Float[] {9e-5F, 9e-5F, 9e-5F}, "sixthuser", floatArrays);

		put(new Integer[] {1, 2, 3}, "firstuser", integerArrays);
		put(new Integer[] {1, 2, 4}, "fourthuser", integerArrays);
		put(new Integer[] {1, 3, 4}, "seconduser", integerArrays);
		put(new Integer[] {1, 4, 4}, "fifthuser", integerArrays);
		put(new Integer[] {2, 1, 1}, "thirduser", integerArrays);
		put(new Integer[] {2, 1, 2}, "sixthuser", integerArrays);

		put(new Long[] {-3L, -2L, -1L}, "firstuser", longArrays);
		put(new Long[] {-3L, -2L, -2L}, "seconduser", longArrays);
		put(new Long[] {-3L, -3L, -1L}, "thirduser", longArrays);
		put(new Long[] {-3L, -3L, -2L}, "fourthuser", longArrays);
		put(new Long[] {-4L, -2L, -1L}, "fifthuser", longArrays);
		put(new Long[] {-4L, -2L, -2L}, "sixthuser", longArrays);
	}

	protected void populateNumbers() {
		put(1e-11, "firstuser", doubles);
		put(2e-11, "fourthuser", doubles);
		put(3e-11, "seconduser", doubles);
		put(4e-11, "fifthuser", doubles);
		put(5e-11, "thirduser", doubles);
		put(6e-11, "sixthuser", doubles);

		put(3e-5F, "sixthuser", floats);
		put(4e-5F, "fifthuser", floats);
		put(5e-5F, "fourthuser", floats);
		put(6e-5F, "thirduser", floats);
		put(7e-5F, "seconduser", floats);
		put(8e-5F, "firstuser", floats);

		put(Integer.MAX_VALUE - 1, "seconduser", integers);
		put(Integer.MAX_VALUE - 2, "thirduser", integers);
		put(Integer.MAX_VALUE - 3, "fourthuser", integers);
		put(Integer.MAX_VALUE - 4, "fifthuser", integers);
		put(Integer.MAX_VALUE - 5, "sixthuser", integers);
		put(Integer.MAX_VALUE, "firstuser", integers);

		put(Long.MIN_VALUE + 1, "seconduser", longs);
		put(Long.MIN_VALUE + 2, "thirduser", longs);
		put(Long.MIN_VALUE + 3, "fourthuser", longs);
		put(Long.MIN_VALUE + 4, "fifthuser", longs);
		put(Long.MIN_VALUE + 5, "sixthuser", longs);
		put(Long.MIN_VALUE, "firstuser", longs);
	}

	protected static final String FIELD_DATE = "cd";

	protected static final String FIELD_DOUBLE = "sd";

	protected static final String FIELD_DOUBLE_ARRAY = "md";

	protected static final String FIELD_FLOAT = "sf";

	protected static final String FIELD_FLOAT_ARRAY = "mf";

	protected static final String FIELD_INTEGER = "si";

	protected static final String FIELD_INTEGER_ARRAY = "mi";

	protected static final String FIELD_LONG = "sl";

	protected static final String FIELD_LONG_ARRAY = "ml";

	protected final Map<String, String> dates = new HashMap<>();
	protected final Map<String, Double[]> doubleArrays = new HashMap<>();
	protected final Map<String, Double> doubles = new HashMap<>();
	protected final Map<String, Float[]> floatArrays = new HashMap<>();
	protected final Map<String, Float> floats = new HashMap<>();
	protected final Map<String, Integer[]> integerArrays = new HashMap<>();
	protected final Map<String, Integer> integers = new HashMap<>();
	protected final Map<String, Long[]> longArrays = new HashMap<>();
	protected final Map<String, Long> longs = new HashMap<>();

	private static class FieldRegistryInitializer {

		public FieldRegistryInitializer(FieldRegistryContributorHelper helper) {
			_fieldRegistryContributorHelper = helper;
		}

		protected MappingBuilder createMappingBuilder() {
			return new MappingBuilderImpl();
		}

		protected void register(String name, String type) {
			MappingBuilder mappingBuilder = createMappingBuilder();

			_fieldRegistryContributorHelper.register(
				name,
				mappingBuilder.store(
					true
				).type(
					type
				).build());
		}

		protected void registerPremappedDate(String name) {
			MappingBuilder mappingBuilder = createMappingBuilder();

			_fieldRegistryContributorHelper.register(
				name,
				mappingBuilder.format(
					"yyyyMMddHHmmss"
				).store(
					true
				).type(
					"date"
				).build());
		}

		private final FieldRegistryContributorHelper
			_fieldRegistryContributorHelper;

	}

}