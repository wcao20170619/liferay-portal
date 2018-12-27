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
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.PrefixFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.search.generic.WildcardQueryImpl;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.internal.field.LiferayIndexFieldRegistryContributor;
import com.liferay.portal.search.internal.legacy.document.DocumentTranslator;
import com.liferay.portal.search.internal.legacy.document.DocumentTranslatorImpl;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelper;
import com.liferay.portal.search.test.util.indexing.IndexingFixture.IndexingFixtureListenerHelper;
import com.liferay.portal.search.test.util.indexing.QueryContributor;
import com.liferay.portal.search.test.util.indexing.QueryContributors;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Wade Cao
 */
public abstract class BaseODataSemanticsFilterTestCase
	extends BaseIndexingTestCase {

	@Override
	public void beforeActivate(
		IndexingFixtureListenerHelper indexingFixtureListenerHelper) {

		indexingFixtureListenerHelper.addFieldRegistryContributor(
			fieldRegistryContributorHelper -> {
				fieldRegistryContributorHelper.register(
					_FIELD_CONCATENATED, "keyword");
				fieldRegistryContributorHelper.register(
					_FIELD_KEYWORD, "keyword");
				fieldRegistryContributorHelper.register(_FIELD_TEXT, "text");
			});

		indexingFixtureListenerHelper.addFieldRegistryContributor(
			new LiferayIndexFieldRegistryContributor());
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		indexDocuments();
	}

	@Test
	public void testEx1AnyEq() throws Exception {
		assertSearch(
			QueryContributors.must(new MatchQuery(_FIELD_TEXT, "taga")),
			Arrays.asList("cContent", "aContent", "bContent"));

		assertSearch(
			new TermsFilter(_FIELD_KEYWORD) {
				{
					addValue("taga");
				}
			},
			Arrays.asList("bContent", "aContent", "cContent"));
	}

	@Test
	public void testEx2AnyEqMultipleWords() throws Exception {
		assertSearch(
			QueryContributors.must(new MatchQuery(_FIELD_TEXT, "car accident")),
			Arrays.asList("aContent"));
	}

	@Test
	public void testEx3AnyNe() throws Exception {
		assertSearch(
			QueryContributors.mustNot(new MatchQuery(_FIELD_TEXT, "taga")),
			Arrays.asList("dContent"));
	}

	@Test
	public void testEx4AnyContains() throws Exception {
		assertSearch(
			QueryContributors.must(
				new WildcardQueryImpl(_FIELD_KEYWORD, "*tag*")),
			Arrays.asList("bContent", "aContent", "dContent", "cContent"));

		assertSearch(
			QueryContributors.must(new WildcardQueryImpl(_FIELD_TEXT, "*tag*")),
			Arrays.asList("bContent", "aContent", "dContent", "cContent"));

		assertSearch(
			new PrefixFilter(_FIELD_KEYWORD, "tag"),
			Arrays.asList("bContent", "aContent", "dContent", "cContent"));

		assertSearch(
			new PrefixFilter(_FIELD_TEXT, "tag"),
			Arrays.asList("bContent", "aContent", "dContent", "cContent"));
	}

	@Test
	public void testEx5AllEq() throws Exception {
		assertSearch(
			QueryContributors.must(
				new BooleanQueryImpl() {
					{
						add(
							new MatchQuery(_FIELD_TEXT, "sports"),
							BooleanClauseOccur.MUST);
						add(
							new MatchQuery(_FIELD_TEXT, "tagb"),
							BooleanClauseOccur.MUST);
						add(
							new MatchQuery(_FIELD_TEXT, "tennis"),
							BooleanClauseOccur.MUST);
					}
				}),
			Arrays.asList("dContent"));

		assertSearch(
			QueryContributors.must(
				new BooleanQueryImpl() {
					{
						add(
							new MatchQuery(_FIELD_TEXT, "sports"),
							BooleanClauseOccur.MUST);
						add(
							new MatchQuery(_FIELD_TEXT, "tennis"),
							BooleanClauseOccur.MUST);
					}
				}),
			Arrays.asList("dContent"));

		assertSearch(
			QueryContributors.must(
				new TermQueryImpl(
					_FIELD_CONCATENATED, getExact("sports", "tagb", "tennis"))),
			Arrays.asList("dContent"));

		assertSearch(
			QueryContributors.must(
				new TermQueryImpl(
					_FIELD_CONCATENATED, getExact("sports", "tennis"))),
			Collections.emptyList());
	}

	protected void assertSearch(Filter filter, List<String> expected)
		throws Exception {

		assertSearch(
			indexingTestHelper -> {
				indexingTestHelper.setFilter(filter);

				indexingTestHelper.search();

				indexingTestHelper.assertValues(Field.TITLE, expected);
			});
	}

	protected void assertSearch(
			QueryContributor queryContributor, List<String> expected)
		throws Exception {

		assertSearch(
			indexingTestHelper -> {
				indexingTestHelper.setQueryContributor(queryContributor);

				indexingTestHelper.search();

				indexingTestHelper.assertValues(Field.TITLE, expected);
			});
	}

	protected Stream<Element> getElementsStream() {
		return Stream.of(
			new Element() {
				{
					description = "<p>bleble</p>";
					labels = new String[] {
						"taga", "tagb", "sports", "hobbies", "meat"
					};
					title = "bContent";
				}
			},
			new Element() {
				{
					labels = new String[] {"taga", "tagc", "car accident"};
					title = "aContent";
				}
			},
			new Element() {
				{
					labels = new String[] {"tagb", "sports", "tennis"};
					title = "dContent";
				}
			},
			new Element() {
				{
					description = "<p>blibli</p>";
					labels = new String[] {"taga", "basketball", "football"};
					title = "cContent";
				}
			});
	}

	protected String getExact(String... values) {
		return Stream.of(
			values
		).map(
			StringUtil::toLowerCase
		).sorted(
		).collect(
			Collectors.toList()
		).toString();
	}

	protected void indexDocuments() {
		Stream<Element> stream = getElementsStream();

		stream.map(
			(Function<Element, DocumentCreationHelper>)
				element -> document -> populate(document, element)
		).forEach(
			this::addDocument
		);
	}

	protected void populate(
		com.liferay.portal.kernel.search.Document legacyDocument,
		Element element) {

		DocumentTranslator documentTranslator = new DocumentTranslatorImpl();

		DocumentBuilder documentBuilder =
			getDocumentBuilderFactory().getBuilder();

		Document document = documentBuilder.addString(
			_FIELD_CONCATENATED, getExact(element.labels)
		).addStrings(
			_FIELD_KEYWORD, element.labels
		).addStrings(
			_FIELD_TEXT, element.labels
		).addString(
			Field.DESCRIPTION, element.description
		).addString(
			Field.TITLE, element.title
		).build();

		documentTranslator.translate(document, legacyDocument);
	}

	private static final String _FIELD_CONCATENATED = "labels_concatenated";

	private static final String _FIELD_KEYWORD = "labels_keyword";

	private static final String _FIELD_TEXT = "labels_text";

	private static class Element {

		protected String description = "";
		protected String[] labels = {};
		protected String title = "";

	}

}