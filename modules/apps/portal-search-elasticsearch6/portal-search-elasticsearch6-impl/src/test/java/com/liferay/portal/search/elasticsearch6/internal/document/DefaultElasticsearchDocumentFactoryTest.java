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

package com.liferay.portal.search.elasticsearch6.internal.document;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.field.Mapping;
import com.liferay.portal.search.internal.document.DocumentBuilderImpl;
import com.liferay.portal.search.internal.field.MappingBuilderImpl;
import com.liferay.portal.search.internal.legacy.document.DocumentBuilderFactoryImpl;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;

import java.io.IOException;

import java.util.Optional;

import org.elasticsearch.common.Strings;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class DefaultElasticsearchDocumentFactoryTest {

	@Before
	public void setUp() throws Exception {
		_documentFixture.setUp();

		FieldRegistry fieldRegistry = createFieldRegistry();

		ElasticsearchDocumentFactory elasticsearchDocumentFactory =
			createElasticsearchDocumentFactory(fieldRegistry);

		_elasticsearchDocumentFactory = elasticsearchDocumentFactory;

		_fieldRegistry = fieldRegistry;
	}

	@After
	public void tearDown() throws Exception {
		_documentFixture.tearDown();
	}

	@Test
	public void testNull() throws Exception {
		assertDocument(null, "{\"field\":null}");
	}

	@Test
	public void testNullLegacy() throws Exception {
		assertDocumentLegacy(null, "{}");
	}

	@Test
	public void testSpaces() throws Exception {
		assertDocument(StringPool.SPACE, "{\"field\":\" \"}");

		assertDocument(StringPool.THREE_SPACES, "{\"field\":\"   \"}");
	}

	@Test
	public void testSpacesLegacy() throws Exception {
		assertDocumentLegacy(StringPool.SPACE, "{\"field\":\"\"}");

		assertDocumentLegacy(StringPool.THREE_SPACES, "{\"field\":\"\"}");
	}

	@Test
	public void testStringBlank() throws Exception {
		assertDocumentSameAsLegacy(StringPool.BLANK, "{\"field\":\"\"}");
	}

	@Test
	public void testStringNull() throws Exception {
		assertDocumentSameAsLegacy(StringPool.NULL, "{\"field\":\"null\"}");
	}

	protected static ElasticsearchDocumentFactory
		createElasticsearchDocumentFactory(FieldRegistry fieldRegistry) {

		return new DefaultElasticsearchDocumentFactory() {
			{
				setDocumentBuilderFactory(
					new DocumentBuilderFactoryImpl() {
						{
							setFieldRegistry(fieldRegistry);
						}
					});
			}
		};
	}

	protected static FieldRegistry createFieldRegistry() {
		FieldRegistry fieldRegistry = Mockito.mock(FieldRegistry.class);

		Mapping mapping = new MappingBuilderImpl().type("keyword").build();

		Mockito.doReturn(
			Optional.of(mapping)
		).when(
			fieldRegistry
		).getMappingOptional(
			_FIELD
		);

		return fieldRegistry;
	}

	protected void assertDocument(String value, String json)
		throws IOException {

		DocumentBuilder documentBuilder = new DocumentBuilderImpl(
			_fieldRegistry);

		documentBuilder.addStrings(_FIELD, new String[] {value});

		Assert.assertEquals(
			json,
			Strings.toString(
				_elasticsearchDocumentFactory.getElasticsearchDocument(
					documentBuilder.build())));
	}

	protected void assertDocumentLegacy(String value, String json)
		throws Exception {

		Document document = new DocumentImpl();

		document.addText(_FIELD, new String[] {value});

		Assert.assertEquals(
			json,
			_elasticsearchDocumentFactory.getElasticsearchDocument(document));
	}

	protected void assertDocumentSameAsLegacy(String value, String json)
		throws Exception {

		assertDocument(value, json);
		assertDocumentLegacy(value, json);
	}

	private static final String _FIELD = "field";

	private final DocumentFixture _documentFixture = new DocumentFixture();
	private ElasticsearchDocumentFactory _elasticsearchDocumentFactory;
	private FieldRegistry _fieldRegistry;

}