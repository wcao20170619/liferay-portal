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

import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.search.test.util.BaseDocumentTestCase;

import org.junit.Assume;
import org.junit.Test;

/**
 * @author Wade Cao
 */
public abstract class BaseDocumentSortTestCase extends BaseDocumentTestCase {

	@Test
	public void testFirstNameSearchSortedBySingleDouble() throws Exception {
		for (String keywords : KEYWORDS) {
			checkSearchContext(
				"firstName", keywords, SCREEN_NAMES_MIXED, FIELD_DOUBLE,
				Sort.DOUBLE_TYPE);
		}

		for (String keywords : KEYWORDS_ODD) {
			checkSearchContext(
				"firstName", keywords, SCREEN_NAMES_ODD_MIXED, FIELD_DOUBLE,
				Sort.DOUBLE_TYPE);
		}
	}

	@Test
	public void testFirstNameSearchSortedBySingleFloat() throws Exception {
		for (String keywords : KEYWORDS) {
			checkSearchContext(
				"firstName", keywords, SCREEN_NAMES_DESCENDING, FIELD_FLOAT,
				Sort.FLOAT_TYPE);
		}

		for (String keywords : KEYWORDS_ODD) {
			checkSearchContext(
				"firstName", keywords, SCREEN_NAMES_ODD_DESCENDING, FIELD_FLOAT,
				Sort.FLOAT_TYPE);
		}
	}

	@Test
	public void testFirstNameSearchSortedBySingleInteger() throws Exception {
		for (String keywords : KEYWORDS) {
			checkSearchContext(
				"firstName", keywords, SCREEN_NAMES_DESCENDING, FIELD_INTEGER,
				Sort.INT_TYPE);
		}

		for (String keywords : KEYWORDS_ODD) {
			checkSearchContext(
				"firstName", keywords, SCREEN_NAMES_ODD_DESCENDING,
				FIELD_INTEGER, Sort.INT_TYPE);
		}
	}

	@Test
	public void testFirstNameSearchSortedBySingleLong() throws Exception {
		Assume.assumeFalse(isSolrUnitTest());

		for (String keywords : KEYWORDS) {
			checkSearchContext(
				"firstName", keywords, SCREEN_NAMES_ASCENDING, FIELD_LONG,
				Sort.LONG_TYPE);
		}

		for (String keywords : KEYWORDS_ODD) {
			checkSearchContext(
				"firstName", keywords, SCREEN_NAMES_ODD_ASCENDING, FIELD_LONG,
				Sort.LONG_TYPE);
		}
	}

	@Test
	public void testLastNameSearchSortedBySingleDouble() throws Exception {
		checkSearchContext(
			"lastName", "Smith", SCREEN_NAMES_MIXED, FIELD_DOUBLE,
			Sort.DOUBLE_TYPE);
	}

	@Test
	public void testLastNameSearchSortedBySingleFloat() throws Exception {
		checkSearchContext(
			"lastName", "Smith", SCREEN_NAMES_DESCENDING, FIELD_FLOAT,
			Sort.FLOAT_TYPE);
	}

	@Test
	public void testLastNameSearchSortedBySingleInteger() throws Exception {
		checkSearchContext(
			"lastName", "Smith", SCREEN_NAMES_DESCENDING, FIELD_INTEGER,
			Sort.INT_TYPE);
	}

	@Test
	public void testLastNameSearchSortedBySingleLong() throws Exception {
		Assume.assumeFalse(isSolrUnitTest());

		checkSearchContext(
			"lastName", "Smith", SCREEN_NAMES_ASCENDING, FIELD_LONG,
			Sort.LONG_TYPE);
	}

}