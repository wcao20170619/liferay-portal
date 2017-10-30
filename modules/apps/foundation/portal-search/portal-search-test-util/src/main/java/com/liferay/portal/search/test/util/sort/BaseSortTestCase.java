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

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelper;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelpers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Wade Cao
 */
public abstract class BaseSortTestCase extends BaseIndexingTestCase {

	public void testAddNumberSortable() throws Exception {
		for (int priority : _priorities) {
			addDocument(
				DocumentCreationHelpers.singleNumberSortable(
						_PRIORITY_FIELD, priority));
		}

		Arrays.sort(_priorities);

		String[] expected = ArrayUtil.toStringArray(_priorities);

		assertPriorityOrder(Arrays.asList(expected), _PRIORITY_SORTABLE_FIELD);
	}
	
	public void testAddNumber() throws Exception {
		for (int priority : _priorities) {
			addDocument(
				singleNumberSortable(
						_PRIORITY_FIELD, priority));
		}

		//no sorting, return the same orders
		String[] expected = ArrayUtil.toStringArray(_priorities);

		assertPriorityOrder(Arrays.asList(expected), _PRIORITY_FIELD);
	}

	protected void assertPriorityOrder(List<String> expectedValues, String sortField)
		throws Exception {

		SearchContext searchContext = createSearchContext();

		searchContext.setSorts(
			new Sort(sortField, Sort.INT_TYPE, false));

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.addSelectedFieldNames(_PRIORITY_FIELD);

		Query q = getDefaultQuery();

		q.setQueryConfig(queryConfig);

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS,
			() -> {
				Hits hits = search(searchContext, q);

				DocumentsAssert.assertValues(
					"", hits.getDocs(), _PRIORITY_FIELD, expectedValues);

				return null;
			});
	}

	private static final String _PRIORITY_FIELD = Field.PRIORITY;

	private static final String _PRIORITY_SORTABLE_FIELD =
		Field.PRIORITY + "_Number";
	
	private static final int[] _priorities = {10, 40, 5};
	
	public static DocumentCreationHelper singleNumberSortable(
			final String field, final int value) {

		return new DocumentCreationHelper() {

			@Override
			public void populate(Document document) {
				document.addNumber(field, value);
			}

		};
	}
	

}