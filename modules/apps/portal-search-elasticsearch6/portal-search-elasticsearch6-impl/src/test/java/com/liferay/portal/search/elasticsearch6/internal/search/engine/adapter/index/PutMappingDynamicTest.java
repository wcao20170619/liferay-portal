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

package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.index;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch6.internal.connection.Index;
import com.liferay.portal.search.elasticsearch6.internal.connection.IndexCreator;
import com.liferay.portal.search.elasticsearch6.internal.connection.IndexName;
import com.liferay.portal.search.elasticsearch6.internal.document.DefaultElasticsearchDocumentFactory;
import com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.ElasticsearchSearchEngineAdapterImpl;
import com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.document.DocumentRequestExecutorFixture;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.DocumentRequestExecutor;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.internal.legacy.document.DocumentBuilderFactoryImpl;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.mapper.StrictDynamicMappingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;

/**
 * @author André de Oliveira
 */
public class PutMappingDynamicTest {

	@Before
	public void setUp() throws Exception {
		_documentFixture.setUp();

		_elasticsearchFixture = new ElasticsearchFixture(getClass());

		_elasticsearchFixture.setUp();

		_index = createIndex(_elasticsearchFixture);

		_searchEngineAdapter = createSearchEngineAdapter(_elasticsearchFixture);
	}

	@After
	public void tearDown() throws Exception {
		_documentFixture.tearDown();

		_elasticsearchFixture.tearDown();
	}

	@Test
	public void testStrict() throws Exception {
		IndicesAdminClient indicesAdminClient =
			_elasticsearchFixture.getIndicesAdminClient();

		PutMappingRequestBuilder putMappingRequestBuilder =
			indicesAdminClient.preparePutMapping(_index.getName());

		String type = RandomTestUtil.randomString();

		putMappingRequestBuilder.setSource(
			"{\"dynamic\":\"strict\"}", XContentType.JSON
		).setType(
			type
		);

		putMappingRequestBuilder.get();

		Document document = new DocumentImpl();

		String fieldName = RandomTestUtil.randomString();

		document.addKeyword(fieldName, fieldName);
		document.addUID(fieldName, fieldName);

		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			_index.getName(), document);

		indexDocumentRequest.setType(type);

		expectedException.expect(StrictDynamicMappingException.class);
		expectedException.expectMessage(
			"mapping set to strict, dynamic introduction of [");

		_searchEngineAdapter.execute(indexDocumentRequest);
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Rule
	public TestName testName = new TestName();

	protected static DocumentRequestExecutor createDocumentRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		DocumentRequestExecutorFixture documentRequestExecutorFixture =
			new DocumentRequestExecutorFixture() {
				{
					setElasticsearchClientResolver(elasticsearchClientResolver);
					setElasticsearchDocumentFactory(
						new DefaultElasticsearchDocumentFactory() {
							{
								setDocumentBuilderFactory(
									new DocumentBuilderFactoryImpl());
							}
						});
				}
			};

		documentRequestExecutorFixture.setUp();

		return documentRequestExecutorFixture.getDocumentRequestExecutor();
	}

	protected static SearchEngineAdapter createSearchEngineAdapter(
		ElasticsearchFixture elasticsearchFixture) {

		return new ElasticsearchSearchEngineAdapterImpl() {
			{
				setDocumentRequestExecutor(
					createDocumentRequestExecutor(elasticsearchFixture));
			}
		};
	}

	protected Index createIndex(ElasticsearchFixture elasticsearchFixture) {
		IndexCreator indexCreator = new IndexCreator() {
			{
				setElasticsearchClientResolver(elasticsearchFixture);
			}
		};

		return indexCreator.createIndex(
			new IndexName(testName.getMethodName()));
	}

	private final DocumentFixture _documentFixture = new DocumentFixture();
	private ElasticsearchFixture _elasticsearchFixture;
	private Index _index;
	private SearchEngineAdapter _searchEngineAdapter;

}