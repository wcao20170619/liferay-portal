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

package com.liferay.portal.search.elasticsearch7.internal.background.task;

import com.liferay.portal.search.elasticsearch7.internal.ElasticsearchSearchEngineFixture;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionFixture;
import com.liferay.portal.search.elasticsearch7.internal.index.FieldMappingAssert;
import com.liferay.portal.search.elasticsearch7.internal.index.LiferayTypeMappingsConstants;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.LPS104115;
import com.liferay.portal.search.test.util.background.task.BaseReindexSingleIndexerBackgroundTaskExecutorTestCase;

import org.elasticsearch.client.RestHighLevelClient;

import org.junit.Assume;
import org.junit.Before;

/**
 * @author Adam Brandizzi
 */
public class ReindexSingleIndexerBackgroundTaskExecutorTest
	extends BaseReindexSingleIndexerBackgroundTaskExecutorTestCase {

	public ReindexSingleIndexerBackgroundTaskExecutorTest() {
		ElasticsearchConnectionFixture elasticsearchConnectionFixture =
			ElasticsearchConnectionFixture.builder(
			).tmpSubdirName(
				ReindexSingleIndexerBackgroundTaskExecutorTest.class.
					getSimpleName()
			).build();

		ElasticsearchSearchEngineFixture elasticsearchSearchEngineFixture =
			new ElasticsearchSearchEngineFixture(
				elasticsearchConnectionFixture);

		_elasticsearchConnectionFixture = elasticsearchConnectionFixture;

		_elasticsearchSearchEngineFixture = elasticsearchSearchEngineFixture;
	}

	@Before
	public void setUp() throws Exception {
		Assume.assumeTrue(LPS104115.LPS113038);

		super.setUp();
	}

	@Override
	protected void assertFieldType(String fieldName, String fieldType)
		throws Exception {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchConnectionFixture.getRestHighLevelClient();

		FieldMappingAssert.assertType(
			fieldType, fieldName,
			LiferayTypeMappingsConstants.LIFERAY_DOCUMENT_TYPE, getIndexName(),
			restHighLevelClient.indices());
	}

	@Override
	protected ElasticsearchSearchEngineFixture getSearchEngineFixture() {
		return _elasticsearchSearchEngineFixture;
	}

	private final ElasticsearchConnectionFixture
		_elasticsearchConnectionFixture;
	private final ElasticsearchSearchEngineFixture
		_elasticsearchSearchEngineFixture;

}