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

package com.liferay.portal.search.test.util.count;

import com.liferay.portal.search.test.util.BaseDocumentTestCase;

import org.junit.Test;

/**
 * @author Wade Cao
 */
public abstract class BaseDocumentCountTestCase extends BaseDocumentTestCase {

	@Test
	public void testFirstNameSearchResultsCount1() throws Exception {
		checkSearchContext("firstName", buildSearchContext("first"), 1);
		checkSearchContext("firstName", buildSearchContext("second"), 1);
		checkSearchContext("firstName", buildSearchContext("third"), 1);
		checkSearchContext("firstName", buildSearchContext("fourth"), 1);
		checkSearchContext("firstName", buildSearchContext("fifth"), 1);
		checkSearchContext("firstName", buildSearchContext("sixth"), 1);
	}

	@Test
	public void testFirstNameSearchResultsCount2() throws Exception {
		for (String keywords : KEYWORDS) {
			checkSearchContext("firstName", buildSearchContext(keywords), 6);
		}
	}

	@Test
	public void testLastNameSearchResultsCount() throws Exception {
		checkSearchContext("lastName", buildSearchContext("Smith"), 6);
	}

}