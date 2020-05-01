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

package com.liferay.portal.search.elasticsearch7.internal.index;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;

import java.util.Collections;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexResponse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author André de Oliveira
 */
public class CompanyIdIndexNameBuilderTest {

	@Before
	public void setUp() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture(
			CompanyIdIndexNameBuilderTest.class.getSimpleName());

		_elasticsearchFixture.setUp();
	}

	@After
	public void tearDown() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	@Test
	public void testActivate() throws Exception {
		CompanyIdIndexNameBuilder companyIdIndexNameBuilder =
			new CompanyIdIndexNameBuilder();

		companyIdIndexNameBuilder.activate(
			Collections.singletonMap("indexNamePrefix", (Object)"UPPERCASE"));

		Assert.assertEquals(
			"uppercase0", companyIdIndexNameBuilder.getIndexName(0));
	}

	@Test
	public void testIndexNamePrefixBlank() throws Exception {
		assertIndexNamePrefix(StringPool.BLANK, StringPool.BLANK);
	}

	@Test(expected = ElasticsearchStatusException.class)
	public void testIndexNamePrefixInvalidIndexName() throws Exception {
		createIndices(StringPool.STAR, 0);
	}

	@Test
	public void testIndexNamePrefixNull() throws Exception {
		assertIndexNamePrefix(null, StringPool.BLANK);
	}

	@Test
	public void testIndexNamePrefixTrim() throws Exception {
		String string = RandomTestUtil.randomString();

		assertIndexNamePrefix(
			StringPool.TAB + string + StringPool.SPACE,
			StringUtil.toLowerCase(string));
	}

	@Test
	public void testIndexNamePrefixUppercase() throws Exception {
		assertIndexNamePrefix("UPPERCASE", "uppercase");
	}

	protected void assertIndexNamePrefix(
			String indexNamePrefix, String expectedIndexNamePrefix)
		throws Exception {

		long companyId = RandomTestUtil.randomLong();

		createIndices(indexNamePrefix, companyId);

		String expectedIndexName = expectedIndexNamePrefix + companyId;

		GetIndexResponse getIndexResponse = _elasticsearchFixture.getIndex(
			expectedIndexName);

		Assert.assertArrayEquals(
			new String[] {expectedIndexName}, getIndexResponse.getIndices());
	}

	protected void createIndices(String indexNamePrefix, long companyId)
		throws Exception {

		final CompanyIdIndexNameBuilder companyIdIndexNameBuilder =
			new CompanyIdIndexNameBuilder();

		companyIdIndexNameBuilder.setIndexNamePrefix(indexNamePrefix);

		CompanyIndexFactory companyIndexFactory = new CompanyIndexFactory() {
			{
				setIndexNameBuilder(companyIdIndexNameBuilder);
				setJsonFactory(new JSONFactoryImpl());
			}
		};

		RestHighLevelClient restHighLevelClient =
			_elasticsearchFixture.getRestHighLevelClient();

		companyIndexFactory.createIndices(
			restHighLevelClient.indices(), companyId);
	}

	private ElasticsearchFixture _elasticsearchFixture;

}