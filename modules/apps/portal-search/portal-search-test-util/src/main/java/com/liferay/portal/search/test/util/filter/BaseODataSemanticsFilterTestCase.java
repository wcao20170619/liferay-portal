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

package com.liferay.portal.search.test.util.filter;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.kernel.search.generic.WildcardQueryImpl;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.field.FieldType;
import com.liferay.portal.search.field.MappedField;
import com.liferay.portal.search.internal.document.DefaultDocumentBuilderFactory;
import com.liferay.portal.search.internal.document.DocumentTranslatorImpl;
import com.liferay.portal.search.internal.field.DefaultFieldRegistry;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.QueryContributor;
import com.liferay.portal.search.test.util.indexing.QueryContributors;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Wade Cao
 */
public abstract class BaseODataSemanticsFilterTestCase
	extends BaseIndexingTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_fieldRegistry = createFieldRegistry();

		populateData();

		addDocuments(
			titleName -> document -> populate(document, titleName),
			getTitleNamesStream());
	}

	@Test
	public void testFieldPasswords1() throws Exception {
		QueryContributor queryContributor = QueryContributors.mustMatch(
			PASSWORDS, "taga");

		assertFieldPasswords(
			new String[] {"cContent", "aContent", "bContent"},
			queryContributor);

		assertFieldPasswordsFilter(
			new String[] {"taga"},
			new String[] {"cContent", "bContent", "aContent"});
	}

	@Test
	public void testFieldPasswords2() throws Exception {
		QueryContributor queryContributor = QueryContributors.mustMatch(
			PASSWORDS, "car accident");

		assertFieldPasswords(new String[] {"aContent"}, queryContributor);
	}

	@Test
	public void testFieldPasswords3() throws Exception {
		QueryContributor queryContributor = QueryContributors.mustNotMatch(
			PASSWORDS, "taga");

		assertFieldPasswords(new String[] {"dContent"}, queryContributor);
	}

	@Test
	public void testFieldPasswords4() throws Exception {
		WildcardQuery query = new WildcardQueryImpl(
			PASSWORDS + ".raw", "*tag*");

		QueryContributor queryContributor = QueryContributors.must(query);

		assertFieldPasswords(
			new String[] {"dContent", "cContent", "bContent", "aContent"},
			queryContributor);
	}

	@Test
	public void testFieldPasswords5() throws Exception {
		BooleanQuery booleanQuery = new BooleanQueryImpl();

		booleanQuery.add(
			new MatchQuery(PASSWORDS, "tagb"), BooleanClauseOccur.MUST);

		booleanQuery.add(
			new MatchQuery(PASSWORDS, "sports"), BooleanClauseOccur.MUST);

		booleanQuery.add(
			new MatchQuery(PASSWORDS, "tennis"), BooleanClauseOccur.MUST);

		QueryContributor queryContributor = QueryContributors.must(
			booleanQuery);

		assertFieldPasswords(new String[] {"dContent"}, queryContributor);
	}

	protected void assertFieldPasswords(
			String[] resultTitles, QueryContributor queryContributor)
		throws Exception {

		assertSearch(
			indexingTestHelper -> {
				indexingTestHelper.setQueryContributor(queryContributor);

				indexingTestHelper.search();

				indexingTestHelper.assertValues(
					Field.TITLE, Arrays.asList(resultTitles));
			});
	}

	protected void assertFieldPasswordsFilter(
			String[] filterValues, String[] resultTitles)
		throws Exception {

		assertSearch(
			indexingTestHelper -> {
				indexingTestHelper.setFilter(
					new TermsFilter(PASSWORDS + ".raw") {
						{
							addValues(filterValues);
						}
					});
				indexingTestHelper.search();
				indexingTestHelper.assertValues(
					Field.TITLE, Arrays.asList(resultTitles));
			});
	}

	protected FieldRegistry createFieldRegistry() {
		FieldRegistry fieldRegistry = new DefaultFieldRegistry() {
			{
				searchEngineAdapter = getSearchEngineAdapter();
			}
		};

		MappedField field = new MappedField(FieldType.getFieldType("keyword"));

		fieldRegistry.registerFieldType(AVAILABLE_LANGUAGEES, "text");
		fieldRegistry.registerFieldTermVector(
			AVAILABLE_LANGUAGEES, "with_positions_offsets");
		fieldRegistry.addField(AVAILABLE_LANGUAGEES, "raw", field);

		fieldRegistry.registerFieldType(PASSWORDS, "text");
		fieldRegistry.registerFieldStored(PASSWORDS, true);
		fieldRegistry.registerFieldTermVector(
			PASSWORDS, "with_positions_offsets");
		fieldRegistry.addField(PASSWORDS, "raw", field);

		fieldRegistry.putPropertiesMappingIndex(
			new String[] {String.valueOf(COMPANY_ID)}, "LiferayDocumentType",
			AVAILABLE_LANGUAGEES);

		fieldRegistry.putPropertiesMappingIndex(
			new String[] {String.valueOf(COMPANY_ID)}, "LiferayDocumentType",
			PASSWORDS);

		return fieldRegistry;
	}

	protected DocumentBuilder getDocumentBuilder() {
		DocumentBuilderFactory documentBuilderFactory =
			new DefaultDocumentBuilderFactory() {
				{
					documentTranslator = new DocumentTranslatorImpl();
					fieldRegistry = _fieldRegistry;
				}
			};

		return documentBuilderFactory.getBuilder();
	}

	protected Stream<String> getTitleNamesStream() {
		Collection<String> titles = titleLangMap.keySet();

		return titles.stream();
	}

	protected void populate(Document document, String titleName) {
		DocumentBuilder documentBuilder = getDocumentBuilder();

		documentBuilder.add(Field.TITLE, titleName);
		documentBuilder.add(Field.DESCRIPTION, titleDescripMap.get(titleName));
		documentBuilder.add(AVAILABLE_LANGUAGEES, titleLangMap.get(titleName));
		documentBuilder.add(PASSWORDS, titleKeywordMap.get(titleName));

		document = documentBuilder.build(document);
	}

	protected void populateData() {
		String[] titles = {"bContent", "aContent", "dContent", "cContent"};

		titleDescripMap.put(titles[0], "<p>bleble</p>");
		titleLangMap.put(titles[0], new String[] {"en-US"});
		titleKeywordMap.put(
			titles[0],
			new String[] {"taga", "tagb", "sports", "hobbies", "meat"});

		titleDescripMap.put(titles[1], "");
		titleLangMap.put(titles[1], new String[] {"en-US"});
		titleKeywordMap.put(
			titles[1], new String[] {"taga", "tagc", "car accident"});

		titleDescripMap.put(titles[2], "");
		titleLangMap.put(titles[2], new String[] {"es-ES"});
		titleKeywordMap.put(
			titles[2], new String[] {"tagb", "sports", "tennis"});

		titleDescripMap.put(titles[3], "<p>blibli</p>");
		titleLangMap.put(titles[3], new String[] {"en-US"});
		titleKeywordMap.put(
			titles[3], new String[] {"taga", "basketball", "football"});
	}

	protected static final String AVAILABLE_LANGUAGEES = "availableLanguages";

	protected static final String PASSWORDS = "passwords";

	protected final Map<String, String> titleDescripMap = new HashMap<>();
	protected final Map<String, String[]> titleKeywordMap = new HashMap<>();
	protected final Map<String, String[]> titleLangMap = new HashMap<>();

	private FieldRegistry _fieldRegistry;

}