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

package com.liferay.portal.search.elasticsearch.internal.sort;

import com.liferay.portal.search.elasticsearch.internal.ElasticsearchIndexingFixture;
import com.liferay.portal.search.elasticsearch.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.search.test.util.sort.BaseSortTestCase;

import org.junit.Test;

/**
 * @author Wade Cao
 */
public class SortTest extends BaseSortTestCase {

	@Test
	public void testElastictestAddNumberSortable() throws Exception {
		super.testAddNumberSortable();
	}
	
	@Test
	public void testElastictestAddNumber() throws Exception {
		super.testAddNumber();
	}

	@Override
	protected IndexingFixture createIndexingFixture() throws Exception {
		return new ElasticsearchIndexingFixture(
			new ElasticsearchFixture(SortTest.class.getSimpleName()),
			BaseIndexingTestCase.COMPANY_ID);
	}

}