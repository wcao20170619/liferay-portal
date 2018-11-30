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

package com.liferay.portal.search.test.util.sort;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.internal.sort.DefaultSortBuilderFactory;
import com.liferay.portal.search.internal.sort.SortTranslator;
import com.liferay.portal.search.sort.SortBuilder;
import com.liferay.portal.search.sort.SortBuilderFactory;
import com.liferay.portal.search.test.util.document.BaseDocumentTestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Wade Cao
 */
public abstract class BaseDocumentSortTestCase extends BaseDocumentTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		FieldRegistry fieldRegistry1 = getFieldRegistry();

		sortBuilderFactory = new DefaultSortBuilderFactory() {
			{
				fieldRegistry = fieldRegistry1;
				sortTranslator = new SortTranslator();
			}
		};
	}

	@Test
	public void testDate() throws Exception {
		assertSort(FIELD_DATE, dates);
	}

	@Test
	public void testDatePremappedFieldCreate() throws Exception {
		assertSort(Field.CREATE_DATE, dates);
	}

	@Test
	public void testDatePremappedFieldModified() throws Exception {
		assertSort(Field.MODIFIED_DATE, dates);
	}

	@Test
	public void testDouble() throws Exception {
		assertSort(FIELD_DOUBLE, doubles);
	}

	@Test
	public void testFloat() throws Exception {
		assertSort(FIELD_FLOAT, floats);
	}

	@Test
	public void testInteger() throws Exception {
		assertSort(FIELD_INTEGER, integers);
	}

	@Test
	public void testLong() throws Exception {
		assertSort(FIELD_LONG, longs);
	}

	protected static <K, V extends Comparable<? super V>> List<K>
		getKeysSortedByValue(Map<K, V> map) {

		Set<Map.Entry<K, V>> set = map.entrySet();

		Stream<Map.Entry<K, V>> stream = set.stream();

		return stream.sorted(
			Map.Entry.comparingByValue()
		).map(
			Map.Entry::getKey
		).collect(
			Collectors.toList()
		);
	}

	protected static <V> Map<String, V> getOdds(Map<String, V> map) {
		Set<Map.Entry<String, V>> entrySet = map.entrySet();

		String[] odds = {"fifthuser", "firstuser", "thirduser"};

		Stream<Map.Entry<String, V>> stream = entrySet.stream();

		return stream.filter(
			entry -> ArrayUtil.contains(odds, entry.getKey())
		).collect(
			Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
		);
	}

	protected <V extends Comparable<? super V>> void assertSort(
			String fieldName, Map<String, V> map)
		throws Exception {

		String[] searches = {
			"sixth second first fourth fifth third",
			"second first fourth fifth third sixth",
			"first fourth fifth third sixth second",
			"fourth fifth third sixth second first",
			"fifth third sixth second first fourth",
			"third sixth second first fourth fifth"
		};

		for (String search : searches) {
			_assertSort(search, fieldName, map);
		}

		String[] oddSearches = {
			"first fifth third", "fifth third first", "third first fifth",
			"first third fifth", "fifth first third"
		};

		for (String search : oddSearches) {
			_assertSort(search, fieldName, getOdds(map));
		}
	}

	protected Query getQuery(String keywords) {
		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new MatchQuery("firstName", keywords), BooleanClauseOccur.SHOULD);
		booleanQueryImpl.add(
			new MatchQuery("lastName", keywords), BooleanClauseOccur.SHOULD);

		return booleanQueryImpl;
	}

	protected SortBuilderFactory sortBuilderFactory;

	private void _assertSort(
			String keywords, Sort sort, List<String> screenNames)
		throws Exception {

		assertSearch(
			indexingTestHelper -> {
				indexingTestHelper.define(
					searchContext -> searchContext.setSorts(sort));

				indexingTestHelper.setQuery(getQuery(keywords));

				indexingTestHelper.search();

				indexingTestHelper.verify(
					hits -> {
						List<String> searchResultValues = new ArrayList<>(
							screenNames.size());
						List<String> screenNamesList = new ArrayList<>(
							screenNames.size());

						for (int i = 0; i < hits.getLength(); i++) {
							Document document = hits.doc(i);

							searchResultValues.add(
								document.get(sort.getFieldName()));

							screenNamesList.add(document.get("screenName"));
						}

						Assert.assertEquals(
							StringUtil.merge(searchResultValues),
							StringUtil.merge(screenNames),
							StringUtil.merge(screenNamesList));
					});
			});
	}

	private <V extends Comparable<? super V>> void _assertSort(
			String keywords, String field, Map<String, V> map)
		throws Exception {

		SortBuilder sortBuilder = sortBuilderFactory.getBuilder();

		sortBuilder.setField(field);

		List<String> screenNames = getKeysSortedByValue(map);

		List<String> ascendingScreenNames = new ArrayList<>(screenNames);

		_assertSort(
			keywords, sortBuilder.setReverse(false).build(),
			ascendingScreenNames);

		List<String> descendingScreenNames = new ArrayList<>(screenNames);

		Collections.reverse(descendingScreenNames);

		_assertSort(
			keywords, sortBuilder.setReverse(true).build(),
			descendingScreenNames);
	}

}