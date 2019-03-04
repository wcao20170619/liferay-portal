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

package com.liferay.portal.search.test.util.field;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.field.FieldRegistryManager;
import com.liferay.portal.search.field.MappingBuilder;
import com.liferay.portal.search.internal.field.LiferayIndexFieldRegistryContributor;
import com.liferay.portal.search.internal.field.MappingBuilderImpl;
import com.liferay.portal.search.internal.legacy.document.DocumentTranslator;
import com.liferay.portal.search.internal.legacy.document.DocumentTranslatorImpl;
import com.liferay.portal.search.spi.field.contributor.helper.FieldRegistryContributorHelper;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.IndexingFixture.IndexingFixtureListenerHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Wade Cao
 */
public abstract class BaseDocumentFieldRegistryTestCase
	extends BaseIndexingTestCase {

	@Override
	public void beforeActivate(
		IndexingFixtureListenerHelper indexingFixtureListenerHelper) {

		indexingFixtureListenerHelper.addFieldRegistryContributor(
			new LiferayIndexFieldRegistryContributor());

		indexingFixtureListenerHelper.addFieldRegistryContributor(
			fieldRegistryContributorHelper ->
				new FieldRegistryInitializer(fieldRegistryContributorHelper) {
					{
						register(FIELD_STRING, "text");
						register(FIELD_LONG, "long");
					}
				});
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		populateData();

		addDocuments(
			screenName -> document -> populate(document, screenName, ""),
			getScreenNamesStream());
	}

	@Test
	public void testSearchLong() throws Exception {
		assertValues(
			FIELD_LONG,
			Arrays.asList(
				String.valueOf(Long.MIN_VALUE + 1),
				String.valueOf(Long.MIN_VALUE + 2)),
			"screenName", Arrays.asList("seconduser", "thirduser"));
	}

	@Test
	public void testSearchString() throws Exception {
		assertValues(
			FIELD_STRING, Arrays.asList("string" + 1, "string" + 2),
			"screenName", Arrays.asList("firstuser", "seconduser"));
	}

	protected static <K, V> void put(V value, K key, Map<K, V> map) {
		map.put(key, value);
	}

	protected void assertValues(
			String field, List<String> keywords, String assertField,
			List<String> expectedValues)
		throws Exception {

		assertSearch(
			indexingTestHelper -> {
				indexingTestHelper.setQuery(getQuery(field, keywords));

				indexingTestHelper.search();

				indexingTestHelper.assertValuesIgnoreRelevance(
					assertField, expectedValues);
			});
	}

	protected Document createDocument(String screenName, String indexName) {
		DocumentBuilderFactory documentBuilderFactory =
			getDocumentBuilderFactory();

		DocumentBuilder documentBuilder = documentBuilderFactory.getBuilder(
			indexName);

		return documentBuilder.addString(
			"screenName", screenName
		).addLong(
			FIELD_LONG, longs.get(screenName)
		).addString(
			FIELD_STRING, strings.get(screenName)
		).build();
	}

	protected Query getQuery(String field, List<String> keywords) {
		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		keywords.forEach(
			keyword -> {
				booleanQueryImpl.add(
					new MatchQuery(field, keyword), BooleanClauseOccur.SHOULD);
			});

		return booleanQueryImpl;
	}

	protected Stream<String> getScreenNamesStream() {
		Collection<String> screenNames = strings.keySet();

		return screenNames.stream();
	}

	protected void populate(
		com.liferay.portal.kernel.search.Document legacyDocument,
		String screenName, String indexName) {

		DocumentTranslator documentTranslator = new DocumentTranslatorImpl();

		documentTranslator.translate(
			createDocument(screenName, indexName), legacyDocument);
	}

	protected void populateData() {
		put("string" + 1, "firstuser", strings);
		put("string" + 2, "seconduser", strings);
		put("string" + 3, "thirduser", strings);
		put("string" + 4, "fourthuser", strings);
		put("string" + 5, "fifthuser", strings);
		put("string" + 6, "sixthuser", strings);

		put(Long.MIN_VALUE + 1, "seconduser", longs);
		put(Long.MIN_VALUE + 2, "thirduser", longs);
		put(Long.MIN_VALUE + 3, "fourthuser", longs);
		put(Long.MIN_VALUE + 4, "fifthuser", longs);
		put(Long.MIN_VALUE + 5, "sixthuser", longs);
		put(Long.MIN_VALUE, "firstuser", longs);
	}

	protected void registerFieldRegistry(String indexName) {
		FieldRegistryManager fieldRegistryManager = getFieldRegistryManager();

		fieldRegistryManager.register(indexName);
	}

	protected static final String FIELD_LONG = "sl";

	protected static final String FIELD_STRING = "ss";

	protected final Map<String, Long> longs = new HashMap<>();
	protected final Map<String, String> strings = new HashMap<>();

	protected static class FieldRegistryInitializer {

		public FieldRegistryInitializer(FieldRegistryContributorHelper helper) {
			_fieldRegistryContributorHelper = helper;
		}

		protected MappingBuilder createMappingBuilder() {
			return new MappingBuilderImpl();
		}

		protected void register(String name, String type) {
			MappingBuilder mappingBuilder = createMappingBuilder();

			_fieldRegistryContributorHelper.register(
				name,
				mappingBuilder.store(
					true
				).type(
					type
				).build());
		}

		private final FieldRegistryContributorHelper
			_fieldRegistryContributorHelper;

	}

}