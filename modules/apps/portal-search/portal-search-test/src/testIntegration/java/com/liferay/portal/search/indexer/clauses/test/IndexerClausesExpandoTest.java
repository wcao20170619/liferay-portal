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

package com.liferay.portal.search.indexer.clauses.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.expando.util.test.ExpandoTestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author André de Oliveira
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class IndexerClausesExpandoTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testBaseIndexer() throws Exception {
		_setUp(
			HashMapBuilder.<Class<?>, String[]>put(
				JournalArticle.class,
				new String[] {"Gamma Article", "Omega Article"}
			).build());

		_test(
			new Class<?>[] {JournalArticle.class}, "gamma",
			() -> {
				Assert.assertTrue(
					_journalArticleIndexer instanceof BaseIndexer);

				_assertSearch("[Gamma Article]", _consumer);
				_assertSearch(
					"[Gamma Article, Omega Article]", _withoutIndexerClauses(),
					_consumer);
			});
	}

	@Test
	public void testDefaultIndexer() throws Exception {
		_setUp(
			HashMapBuilder.<Class<?>, String[]>put(
				BlogsEntry.class, new String[] {"Gamma Blog", "Omega Blog"}
			).build());

		_test(
			new Class<?>[] {BlogsEntry.class}, "gamma",
			() -> {
				Assert.assertEquals(
					"class com.liferay.portal.search.internal.indexer." +
						"DefaultIndexer",
					String.valueOf(_blogsEntryIndexer.getClass()));

				_assertSearch("[Gamma Blog]", _consumer);
				_assertSearch(
					"[Gamma Blog, Omega Blog]", _withoutIndexerClauses(),
					_consumer);
			});
	}

	@Test
	public void testFacetedSearcher() throws Exception {
		_setUp(
			HashMapBuilder.<Class<?>, String[]>put(
				BlogsEntry.class, new String[] {"Gamma Blog", "Omega Blog"}
			).put(
				JournalArticle.class,
				new String[] {"Gamma Article", "Omega Article"}
			).put(
				MBMessage.class, new String[] {"Gamma Message", "Omega Message"}
			).build());

		_test(
			new Class<?>[] {BlogsEntry.class, JournalArticle.class}, "gamma",
			() -> {
				_assertSearch("[Gamma Article, Gamma Blog]", _consumer);
				_assertSearch(
					"[Gamma Article, Gamma Blog, Omega Article, Omega Blog]",
					_withoutIndexerClauses(), _consumer);
			});
	}

	private void _assertSearch(
		String expected, Consumer<SearchRequestBuilder>... consumers) {

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).fields(
				StringPool.STAR
			).groupIds(
				_group.getGroupId()
			).withSearchRequestBuilder(
				consumers
			).build());

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse.getRequestString(),
			searchResponse.getDocumentsStream(),
			"expando__keyword__custom_fields__" + _EXPANDO_COLUMN, expected);
	}

	private void _setUp(Map<Class<?>, String[]> map) throws Exception {
		for (Map.Entry<Class<?>, String[]> entry : map.entrySet()) {
			Class<?> clazz = entry.getKey();

			ExpandoTable expandoTable = ExpandoTableLocalServiceUtil.fetchTable(
				_group.getCompanyId(),
				ClassNameLocalServiceUtil.getClassNameId(clazz),
				"CUSTOM_FIELDS");

			if (expandoTable == null) {
				expandoTable = ExpandoTableLocalServiceUtil.addTable(
					_group.getCompanyId(),
					ClassNameLocalServiceUtil.getClassNameId(clazz),
					"CUSTOM_FIELDS");

				_expandoTables.add(expandoTable);
			}

			ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
				expandoTable, _EXPANDO_COLUMN, ExpandoColumnConstants.STRING);

			_expandoColumns.add(expandoColumn);

			UnicodeProperties unicodeProperties =
				expandoColumn.getTypeSettingsProperties();

			unicodeProperties.setProperty(
				ExpandoColumnConstants.INDEX_TYPE,
				String.valueOf(ExpandoColumnConstants.INDEX_TYPE_KEYWORD));

			expandoColumn.setTypeSettingsProperties(unicodeProperties);

			ExpandoColumnLocalServiceUtil.updateExpandoColumn(expandoColumn);

			String[] expandoValues = entry.getValue();

			for (String expandoValue : expandoValues) {
				ServiceContext serviceContext =
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId());

				serviceContext.setExpandoBridgeAttributes(
					Collections.singletonMap(_EXPANDO_COLUMN, expandoValue));

				if (clazz.equals(BlogsEntry.class)) {
					_blogsEntries.add(
						BlogsEntryLocalServiceUtil.addEntry(
							TestPropsValues.getUserId(),
							RandomTestUtil.randomString(),
							RandomTestUtil.randomString(), serviceContext));
				}
				else if (clazz.equals(JournalArticle.class)) {
					JournalArticleLocalServiceUtil.addArticle(
						null, TestPropsValues.getUserId(), _group.getGroupId(),
						JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
						HashMapBuilder.put(
							LocaleUtil.US, RandomTestUtil.randomString()
						).build(),
						HashMapBuilder.put(
							LocaleUtil.US, RandomTestUtil.randomString()
						).build(),
						DDMStructureTestUtil.getSampleStructuredContent(
							"content", Collections.emptyList(),
							LocaleUtil.toLanguageId(LocaleUtil.US)),
						"BASIC-WEB-CONTENT", "BASIC-WEB-CONTENT",
						serviceContext);
				}
				else if (clazz.equals(MBMessage.class)) {
					MBMessageLocalServiceUtil.addMessage(
						null, TestPropsValues.getUserId(),
						RandomTestUtil.randomString(), _group.getGroupId(),
						MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, 0L,
						MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID,
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString(),
						MBMessageConstants.DEFAULT_FORMAT, null, false, 0.0,
						false, serviceContext);
				}
			}
		}
	}

	private void _test(
			Class<?>[] classes, String queryString,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		_consumer =
			searchRequestBuilder -> searchRequestBuilder.modelIndexerClasses(
				classes
			).queryString(
				queryString
			);

		unsafeRunnable.run();
	}

	private Consumer<SearchRequestBuilder> _withoutIndexerClauses() {
		return searchRequestBuilder -> searchRequestBuilder.withSearchContext(
			searchContext -> searchContext.setAttribute(
				"search.full.query.suppress.indexer.provided.clauses", true));
	}

	private static final String _EXPANDO_COLUMN = "expandoColumn";

	@DeleteAfterTestRun
	private List<BlogsEntry> _blogsEntries = new ArrayList<>();

	@Inject(filter = "indexer.class.name=com.liferay.blogs.model.BlogsEntry")
	private Indexer<BlogsEntry> _blogsEntryIndexer;

	private Consumer<SearchRequestBuilder> _consumer;

	@DeleteAfterTestRun
	private List<ExpandoColumn> _expandoColumns = new ArrayList<>();

	@DeleteAfterTestRun
	private List<ExpandoTable> _expandoTables = new ArrayList<>();

	@DeleteAfterTestRun
	private Group _group;

	@Inject(filter = "component.name=*.JournalArticleIndexer")
	private Indexer<JournalArticle> _journalArticleIndexer;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}