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
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.util.StringBundler;

import java.util.List;

import org.junit.Assert;

/**
 * @author André de Oliveira
 */
public class HitsAssert {

	public static void assertNoHits(Hits hits) {
		Assert.assertEquals(hits.toString(), 0, hits.getLength());
	}

	public static Document assertOnlyOne(Hits hits) {
		List<Document> documents = hits.toList();

		Assert.assertEquals(hits.toString(), 1, documents.size());

		return documents.get(0);
	}

	public static Document assertOnlyOne(Hits hits, String message) {
		List<Document> documents = hits.toList();

		Assert.assertEquals(
			_getMessage(hits.toString(), message), 1, documents.size());

		return documents.get(0);
	}

	private static String _getMessage(String hitsToString, String message) {
		return StringBundler.concat(message, "->", hitsToString);
	}

}