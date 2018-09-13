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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactory;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.internal.SortFactoryImpl;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;

/**
 * @author Wade Cao
 */
public abstract class BaseDocumentTestCase extends BaseIndexingTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		populateNumbers();

		for (String screenName : _doubles.keySet()) {
			addDocument(screenName);
		}
	}

	protected static final boolean isSolrUnitTest() {
		return Boolean.valueOf(
			System.getProperty(
				"com.liferay.portal.search.solr.test.unit.started"));
	}

	protected void addDocument(String screenName) throws Exception {
		String firstName = screenName.replaceFirst("user", StringPool.BLANK);

		addDocument(
			document -> {
				document.addNumber(
					FIELD_DOUBLE_ARRAY, _doubleArrays.get(screenName));
				document.addNumber(
					FIELD_FLOAT_ARRAY, _floatArrays.get(screenName));
				document.addNumber(
					FIELD_INTEGER_ARRAY, _integerArrays.get(screenName));
				document.addNumber(
					FIELD_LONG_ARRAY, _longArrays.get(screenName));
				document.addNumber(FIELD_DOUBLE, _doubles.get(screenName));
				document.addNumber(FIELD_FLOAT, _floats.get(screenName));
				document.addNumber(FIELD_INTEGER, _integers.get(screenName));
				document.addNumber(FIELD_LONG, _longs.get(screenName));
				document.addText("screenName", screenName);
				document.addKeyword("firstName", firstName);
				document.addKeyword("lastName", "Smith");
			});
	}

	protected void assertCount(
			SearchContext searchContext, Query query, long expectedHitsLength)
		throws Exception {

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS,
			() -> {
				Hits hits = search(searchContext, query);

				Assert.assertEquals(
					hits.toString(), expectedHitsLength, hits.getLength());

				return null;
			});
	}

	protected void assertMapping(SearchContext searchContext, Query query)
		throws Exception {

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS,
			() -> {
				Hits hits = search(searchContext, query);

				for (Document document : hits.getDocs()) {
					String screenName = document.get("screenName");

					Assert.assertEquals(
						Double.valueOf(document.get(FIELD_DOUBLE)),
						_doubles.get(screenName), 0);

					Assert.assertEquals(
						Long.valueOf(document.get(FIELD_LONG)),
						_longs.get(screenName), 0);

					Assert.assertEquals(
						Float.valueOf(document.get(FIELD_FLOAT)),
						_floats.get(screenName), 0);

					Assert.assertEquals(
						Integer.valueOf(document.get(FIELD_INTEGER)),
						_integers.get(screenName), 0);

					Assert.assertArrayEquals(
						getDoubleArray(document),
						_doubleArrays.get(screenName));

					Assert.assertArrayEquals(
						getLongArray(document), _longArrays.get(screenName));

					Assert.assertArrayEquals(
						getFloatArray(document), _floatArrays.get(screenName));

					Assert.assertArrayEquals(
						getIntegerArray(document),
						_integerArrays.get(screenName));
				}

				return null;
			});
	}

	protected void assertSort(
			Sort sort, Query query, SearchContext searchContext,
			String... screenNames)
		throws Exception {

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS,
			() -> {
				Hits hits = search(searchContext, query);

				List<String> searchResultValues = new ArrayList<>(
					screenNames.length);
				List<String> screenNamesList = new ArrayList<>(
					screenNames.length);

				if (hits.getLength() > 0) {
					for (int i = 0; i < screenNames.length; i++) {
						Document document = hits.doc(i);

						searchResultValues.add(
							document.get(sort.getFieldName()));

						screenNamesList.add(document.get("screenName"));
					}

					Assert.assertEquals(
						StringUtil.merge(searchResultValues),
						StringUtil.merge(screenNames),
						StringUtil.merge(screenNamesList));
				}

				return null;
			});
	}

	protected SearchContext buildSearchContext(String keywords) {
		SearchContext searchContext = createSearchContext();

		searchContext.setAttribute(Field.STATUS, WorkflowConstants.STATUS_ANY);

		searchContext.setKeywords(keywords);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(getSelectedFieldNames());

		return searchContext;
	}

	protected void checkSearchContext(
			String keyworkField, SearchContext searchContext)
		throws Exception {

		Query query = getDefaultQuery(
			keyworkField, searchContext.getKeywords());

		assertMapping(searchContext, query);
	}

	protected void checkSearchContext(
			String keyworkField, SearchContext searchContext,
			long expectedHitsLength)
		throws Exception {

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(getSelectedFieldNames());

		Query query = getDefaultQuery(
			keyworkField, searchContext.getKeywords());

		query.setQueryConfig(queryConfig);

		assertCount(searchContext, query, expectedHitsLength);
	}

	protected void checkSearchContext(
			String keyworkField, SearchContext searchContext, Sort sort,
			String[] screenNames)
		throws Exception {

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(getSelectedFieldNames());

		searchContext.setSorts(sort);

		Query query = getDefaultQuery(
			keyworkField, searchContext.getKeywords());

		query.setQueryConfig(queryConfig);

		assertSort(sort, query, searchContext, screenNames);
	}

	protected void checkSearchContext(
			String keywordField, String keywords, String[] ascendingScreenNames,
			String field, int type)
		throws Exception {

		SearchContext searchContext = buildSearchContext(keywords);

		SortFactory sortFactory = new SortFactoryImpl();

		checkSearchContext(
			keywordField, searchContext, sortFactory.create(field, type, false),
			ascendingScreenNames);

		String[] descendingScreenNames = Arrays.copyOf(
			ascendingScreenNames, ascendingScreenNames.length);

		ArrayUtil.reverse(descendingScreenNames);

		checkSearchContext(
			keywordField, searchContext, sortFactory.create(field, type, true),
			descendingScreenNames);
	}

	protected Query getDefaultQuery(String field, String keywords) {
		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new TermQueryImpl(Field.COMPANY_ID, String.valueOf(COMPANY_ID)),
			BooleanClauseOccur.MUST);
		booleanQueryImpl.add(
			new MatchQuery(field, keywords), BooleanClauseOccur.MUST);

		return booleanQueryImpl;
	}

	protected Double[] getDoubleArray(Document document) {
		List<Double> list = new ArrayList<>();

		for (String value : document.getValues(FIELD_DOUBLE_ARRAY)) {
			list.add(Double.valueOf(value));
		}

		return list.toArray(new Double[list.size()]);
	}

	protected Float[] getFloatArray(Document document) {
		List<Float> list = new ArrayList<>();

		for (String value : document.getValues(FIELD_FLOAT_ARRAY)) {
			list.add(Float.valueOf(value));
		}

		return list.toArray(new Float[list.size()]);
	}

	protected Integer[] getIntegerArray(Document document) {
		List<Integer> list = new ArrayList<>();

		for (String value : document.getValues(FIELD_INTEGER_ARRAY)) {
			list.add(Integer.valueOf(value));
		}

		return list.toArray(new Integer[list.size()]);
	}

	protected Long[] getLongArray(Document document) {
		List<Long> list = new ArrayList<>();

		for (String value : document.getValues(FIELD_LONG_ARRAY)) {
			list.add(Long.valueOf(value));
		}

		return list.toArray(new Long[list.size()]);
	}

	protected String[] getSelectedFieldNames() {
		return new String[] {
			FIELD_DOUBLE, FIELD_DOUBLE_ARRAY, FIELD_FLOAT, FIELD_FLOAT_ARRAY,
			FIELD_INTEGER, FIELD_INTEGER_ARRAY, FIELD_LONG, FIELD_LONG_ARRAY,
			"screenName", "firstName", "lastName"
		};
	}

	protected void populateNumberArrays(
		String screenName, Double[] doubleArray, Float[] floatArray,
		Integer[] integerArray, Long[] longArray) {

		_doubleArrays.put(screenName, doubleArray);
		_floatArrays.put(screenName, floatArray);
		_integerArrays.put(screenName, integerArray);
		_longArrays.put(screenName, longArray);
	}

	protected void populateNumbers() {
		populateNumbers(
			"firstuser", 1e-11, 8e-5F, Integer.MAX_VALUE, Long.MIN_VALUE);
		populateNumberArrays(
			"firstuser", new Double[] {1e-11, 2e-11, 3e-11},
			new Float[] {8e-5F, 8e-5F, 8e-5F}, new Integer[] {1, 2, 3},
			new Long[] {-3L, -2L, -1L});

		populateNumbers(
			"seconduser", 3e-11, 7e-5F, Integer.MAX_VALUE - 1,
			Long.MIN_VALUE + 1L);
		populateNumberArrays(
			"seconduser", new Double[] {1e-11, 2e-11, 5e-11},
			new Float[] {9e-5F, 8e-5F, 7e-5F}, new Integer[] {1, 3, 4},
			new Long[] {-3L, -2L, -2L});

		populateNumbers(
			"thirduser", 5e-11, 6e-5F, Integer.MAX_VALUE - 2,
			Long.MIN_VALUE + 2L);
		populateNumberArrays(
			"thirduser", new Double[] {1e-11, 3e-11, 2e-11},
			new Float[] {9e-5F, 8e-5F, 9e-5F}, new Integer[] {2, 1, 1},
			new Long[] {-3L, -3L, -1L});

		populateNumbers(
			"fourthuser", 2e-11, 5e-5F, Integer.MAX_VALUE - 3,
			Long.MIN_VALUE + 3L);
		populateNumberArrays(
			"fourthuser", new Double[] {1e-11, 2e-11, 4e-11},
			new Float[] {9e-5F, 9e-5F, 7e-5F}, new Integer[] {1, 2, 4},
			new Long[] {-3L, -3L, -2L});

		populateNumbers(
			"fifthuser", 4e-11, 4e-5F, Integer.MAX_VALUE - 4,
			Long.MIN_VALUE + 4L);
		populateNumberArrays(
			"fifthuser", new Double[] {1e-11, 3e-11, 1e-11},
			new Float[] {9e-5F, 9e-5F, 8e-5F}, new Integer[] {1, 4, 4},
			new Long[] {-4L, -2L, -1L});

		populateNumbers(
			"sixthuser", 6e-11, 3e-5F, Integer.MAX_VALUE - 5,
			Long.MIN_VALUE + 5L);
		populateNumberArrays(
			"sixthuser", new Double[] {2e-11, 1e-11, 1e-11},
			new Float[] {9e-5F, 9e-5F, 9e-5F}, new Integer[] {2, 1, 2},
			new Long[] {-4L, -2L, -2L});
	}

	protected void populateNumbers(
		String screenName, Double numberDouble, Float floatNumber,
		Integer numberInteger, Long longNumber) {

		_doubles.put(screenName, numberDouble);
		_floats.put(screenName, floatNumber);
		_integers.put(screenName, numberInteger);
		_longs.put(screenName, longNumber);
	}

	protected static final String FIELD_DOUBLE = "sd";

	protected static final String FIELD_DOUBLE_ARRAY = "md";

	protected static final String FIELD_FLOAT = "sf";

	protected static final String FIELD_FLOAT_ARRAY = "mf";

	protected static final String FIELD_INTEGER = "si";

	protected static final String FIELD_INTEGER_ARRAY = "mi";

	protected static final String FIELD_LONG = "sl";

	protected static final String FIELD_LONG_ARRAY = "ml";

	protected static final String[] KEYWORDS = {
		"sixth second first fourth fifth third",
		"second first fourth fifth third sixth",
		"first fourth fifth third sixth second",
		"fourth fifth third sixth second first",
		"fifth third sixth second first fourth",
		"third sixth second first fourth fifth"
	};

	protected static final String[] KEYWORDS_ODD = {
		"first fifth third", "fifth third first", "third first fifth",
		"first third fifth", "fifth first third"
	};

	protected static final String[] SCREEN_NAMES_ASCENDING = {
		"firstuser", "seconduser", "thirduser", "fourthuser", "fifthuser",
		"sixthuser"
	};

	protected static final String[] SCREEN_NAMES_DESCENDING = {
		"sixthuser", "fifthuser", "fourthuser", "thirduser", "seconduser",
		"firstuser"
	};

	protected static final String[] SCREEN_NAMES_MIXED = {
		"firstuser", "fourthuser", "seconduser", "fifthuser", "thirduser",
		"sixthuser"
	};

	protected static final String[] SCREEN_NAMES_ODD_ASCENDING =
		{"firstuser", "thirduser", "fifthuser"};

	protected static final String[] SCREEN_NAMES_ODD_DESCENDING =
		{"fifthuser", "thirduser", "firstuser"};

	protected static final String[] SCREEN_NAMES_ODD_MIXED =
		{"firstuser", "fifthuser", "thirduser"};

	private final Map<String, Double[]> _doubleArrays = new HashMap<>();
	private final Map<String, Double> _doubles = new HashMap<>();
	private final Map<String, Float[]> _floatArrays = new HashMap<>();
	private final Map<String, Float> _floats = new HashMap<>();
	private final Map<String, Integer[]> _integerArrays = new HashMap<>();
	private final Map<String, Integer> _integers = new HashMap<>();
	private final Map<String, Long[]> _longArrays = new HashMap<>();
	private final Map<String, Long> _longs = new HashMap<>();

}