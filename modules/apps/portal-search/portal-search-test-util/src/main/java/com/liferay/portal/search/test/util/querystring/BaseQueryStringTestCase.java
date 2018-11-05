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
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public abstract class BaseQueryStringTestCase extends BaseIndexingTestCase {

	@Test
	public void testPresentAfterSearch() throws Exception {
		assertSearch(
			indexingTestHelper -> {
				indexingTestHelper.search();

				assertQueryStringPresent(indexingTestHelper);
			});
	}

	@Test
	public void testPresentAfterSearchCount() throws Exception {
		assertSearch(
			indexingTestHelper -> {
				indexingTestHelper.searchCount();

				assertQueryStringPresent(indexingTestHelper);
			});
	}

	protected void assertQueryStringPresent(
		IndexingTestHelper indexingTestHelper) {

		Assert.assertThat(
			indexingTestHelper.getQueryString(),
			CoreMatchers.containsString(Field.ENTRY_CLASS_NAME));
	}

}