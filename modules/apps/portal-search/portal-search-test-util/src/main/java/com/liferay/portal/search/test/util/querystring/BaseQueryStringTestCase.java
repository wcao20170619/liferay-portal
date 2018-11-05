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

package com.liferay.portal.search.test.util.querystring;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.search.generic.StringQuery;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelpers;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Wade Cao
 */
public abstract class BaseQueryStringTestCase extends BaseIndexingTestCase {

	@Test
	public void testNotNullQueryStringWithNoDocument() throws Exception {
		addDocuments("alpha bravo", "alpha charlie", "charlie delta");

		assertSearch("bogus AND NOT field");
	}

	protected void addDocuments(String... values) throws Exception {
		addDocuments(
			value -> DocumentCreationHelpers.singleText(_FIELD_NAME, value),
			Arrays.asList(values));
	}

	protected void assertSearch(String queryString) throws Exception {
		assertSearch(
			indexingTestHelper -> {
				indexingTestHelper.setFilter(
					new TermFilter(
						Field.ENTRY_CLASS_NAME, getEntryClassName()));
				indexingTestHelper.setQuery(new StringQuery(queryString));

				indexingTestHelper.search();

				indexingTestHelper.assertResultCount(0);

				String qString = indexingTestHelper.getQueryString();

				Assert.assertNotNull(qString);

				Assert.assertTrue(qString.indexOf(queryString) >= 0);
			});
	}

	private static final String _FIELD_NAME = "title";

}