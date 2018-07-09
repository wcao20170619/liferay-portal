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

package com.liferay.portal.search.multilanguage.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.test.journal.util.JournalArticleBlueprint;
import com.liferay.portal.search.test.journal.util.JournalArticleContent;
import com.liferay.portal.search.test.journal.util.JournalArticleDescription;
import com.liferay.portal.search.test.journal.util.JournalArticleTitle;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
@Sync
public class MultiLanguageSearchTest extends BaseMultiLanguageSearchTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		init();
		addJournalArticlesExpectedResults();
	}

	@Test
	public void testMultiLanguageArticleContent() throws Exception {
		String searchTerm = "content";

		List<Document> documents = _search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(3, documents.size(), 0);
		assertSearch("content", "content_en_US", documents, searchTerm);
		assertSearch("content", "content_nl_NL", documents, searchTerm);

		documents = _search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(3, documents.size(), 0);
		assertSearch("content", "content_en_US", documents, searchTerm);
		assertSearch("content", "content_nl_NL", documents, searchTerm);
	}

	@Test
	public void testMultiLanguageArticleDescription() throws Exception {
		String searchTerm = "description";

		List<Document> documents = _search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(3, documents.size(), 0);
		assertSearch("description", "description_en_US", documents, searchTerm);
		assertSearch("description", "description_nl_NL", documents, searchTerm);

		documents = _search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(3, documents.size(), 0);
		assertSearch("description", "description_en_US", documents, searchTerm);
		assertSearch("description", "description_nl_NL", documents, searchTerm);
	}

	@Test
	public void testMultiLanguageArticleTitle() throws Exception {
		String searchTerm = "english";

		List<Document> documents = _search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(3, documents.size(), 0);
		assertSearch("title", "title", documents, searchTerm);
		assertSearch("title", "localized_title_nl_NL", documents, searchTerm);

		documents = _search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(3, documents.size(), 0);
		assertSearch("title", "title", documents, searchTerm);
		assertSearch("title", "localized_title_nl_NL", documents, searchTerm);
	}

	@Test
	public void testMultiLanguageEmtpySearch() throws Exception {
		String searchTerm = null;

		List<Document> documents = _search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(3, documents.size(), 0);

		documents = _search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(3, documents.size(), 0);

		searchTerm = "no field value";

		documents = _search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(0, documents.size(), 0);
	}

	protected Group addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return group;
	}

	protected JournalArticle addJournalArticle(
			Group group, User user, Locale defaultLocal,
			List<LocaleKeywordWrapper> localeKeywordWrapperTitle,
			List<LocaleKeywordWrapper> localeKeywordWrapperDescription,
			List<LocaleKeywordWrapper> localeKeywordWrapperContent)
		throws Exception {

		return journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					groupId = group.getGroupId();
					journalArticleContent = new JournalArticleContent() {
						{
							defaultLocale = defaultLocal;
							name = "content";
							localeKeywordWrapperContent.forEach(
								wrapper -> {
									put(
										wrapper.getLocale(),
										wrapper.getKeyword());
								});
						}
					};
					journalArticleDescription =
						new JournalArticleDescription() {
							{
								localeKeywordWrapperDescription.forEach(
									wrapper -> {
										put(
											wrapper.getLocale(),
											wrapper.getKeyword());
									});
							}

						};
					journalArticleTitle = new JournalArticleTitle() {
						{
							localeKeywordWrapperTitle.forEach(
								wrapper -> {
									put(
										wrapper.getLocale(),
										wrapper.getKeyword());
								});
						}
					};
					userId = user.getUserId();
				}
			});
	}

	protected void addJournalArticlesExpectedResults() throws Exception {
		Map<String, Map<String, Map<String, String>>>
			articleIdTitleExpectedMap = new HashMap<>();
		Map<String, Map<String, Map<String, String>>>
			articleIdDescriptionExpectedMap = new HashMap<>();
		Map<String, Map<String, Map<String, String>>>
			articleIdContentExpectedMap = new HashMap<>();

		_indexTypeExpectedMap.put("content", articleIdContentExpectedMap);

		_indexTypeExpectedMap.put(
			"description", articleIdDescriptionExpectedMap);
		_indexTypeExpectedMap.put("title", articleIdTitleExpectedMap);

		List<LocaleKeywordWrapper> localeKeywordWrapperContentList =
			Arrays.asList(new LocaleKeywordWrapper(LocaleUtil.US, _content));
		List<LocaleKeywordWrapper> localeKeywordWrapperDescriptionList =
			Arrays.asList(
				new LocaleKeywordWrapper(LocaleUtil.US, _description));
		List<LocaleKeywordWrapper> localeKeywordWrapperTitleList =
			Arrays.asList(new LocaleKeywordWrapper(LocaleUtil.US, _title));

		JournalArticle journalArticle = addJournalArticle(
			_group, _user, LocaleUtil.US, localeKeywordWrapperTitleList,
			localeKeywordWrapperDescriptionList,
			localeKeywordWrapperContentList);

		_addExpectedValueMap_1(
			articleIdTitleExpectedMap, articleIdDescriptionExpectedMap,
			articleIdContentExpectedMap, journalArticle);

		localeKeywordWrapperContentList = Arrays.asList(
			new LocaleKeywordWrapper(LocaleUtil.US, _content),
			new LocaleKeywordWrapper(LocaleUtil.NETHERLANDS, _content_nl));
		localeKeywordWrapperDescriptionList = Arrays.asList(
			new LocaleKeywordWrapper(LocaleUtil.US, _description),
			new LocaleKeywordWrapper(LocaleUtil.NETHERLANDS, _description_nl));
		localeKeywordWrapperTitleList = Arrays.asList(
			new LocaleKeywordWrapper(LocaleUtil.US, _title),
			new LocaleKeywordWrapper(LocaleUtil.NETHERLANDS, _title_nl));

		journalArticle = addJournalArticle(
			_group, _user, LocaleUtil.US, localeKeywordWrapperTitleList,
			localeKeywordWrapperDescriptionList,
			localeKeywordWrapperContentList);

		_addExpectedValueMap_2(
			articleIdTitleExpectedMap, articleIdDescriptionExpectedMap,
			articleIdContentExpectedMap, journalArticle);

		localeKeywordWrapperContentList = Arrays.asList(
			new LocaleKeywordWrapper(LocaleUtil.NETHERLANDS, _content));
		localeKeywordWrapperDescriptionList = Arrays.asList(
			new LocaleKeywordWrapper(LocaleUtil.NETHERLANDS, _description));
		localeKeywordWrapperTitleList = Arrays.asList(
			new LocaleKeywordWrapper(LocaleUtil.NETHERLANDS, _title));

		journalArticle = addJournalArticle(
			_group, _user, LocaleUtil.NETHERLANDS,
			localeKeywordWrapperTitleList, localeKeywordWrapperDescriptionList,
			localeKeywordWrapperContentList);

		_addExpectedValueMap_3(
			articleIdTitleExpectedMap, articleIdDescriptionExpectedMap,
			articleIdContentExpectedMap, journalArticle);
	}

	protected User addUser() throws Exception {
		User user = UserTestUtil.addUser();

		_users.add(user);

		return user;
	}

	protected void assertSearch(
		String indexType, String prefix, List<Document> documents,
		String searchTerm) {

		Map<String, Map<String, Map<String, String>>> articleIdExpectedMap =
			_indexTypeExpectedMap.get(indexType);

		documents.forEach(
			doc -> {
				Map<String, Map<String, String>> expected =
					articleIdExpectedMap.get(doc.get(Field.ARTICLE_ID));

				FieldValuesAssert.assertFieldValues(
					expected.get(prefix), prefix, doc, searchTerm);
			});
	}

	protected void init() throws Exception {
		_title = "english";
		_title_nl = "engels";
		_description = "description";
		_description_nl = "beschrijving";
		_content = "content";
		_content_nl = "inhoud";

		_group = addGroup();
		_user = addUser();
		_indexer = _indexerRegistry.getIndexer(JournalArticle.class);
	}

	protected class LocaleKeywordWrapper {

		public LocaleKeywordWrapper(Locale locale, String keyword) {
			_locale = locale;
			_keyword = keyword;
		}

		public String getKeyword() {
			return _keyword;
		}

		public Locale getLocale() {
			return _locale;
		}

		private final String _keyword;
		private final Locale _locale;

	}

	@SuppressWarnings("serial")
	private void _addExpectedValueMap_1(
		Map<String, Map<String, Map<String, String>>> articleIdTitleExpectedMap,
		Map<String, Map<String, Map<String, String>>>
			articleIdDescriptionExpectedMap,
		Map<String, Map<String, Map<String, String>>>
			articleIdContentExpectedMap,
		JournalArticle journalArticle) {

		Map<String, String> titleStrings = new HashMap<String, String>() {
			{
				put("title_en_US", _title);
			}
		};

		Map<String, String> localizedTitleStrings =
			new HashMap<String, String>() {
				{
					put("localized_title_nl_NL", _title);
					put("localized_title_nl_NL_sortable", _title);
				}
			};

		Map<String, Map<String, String>> titlesMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("title", titleStrings);
					put("localized_title_nl_NL", localizedTitleStrings);
				}
			};

		articleIdTitleExpectedMap.put(journalArticle.getArticleId(), titlesMap);

		Map<String, String> descStrings_US = new HashMap<String, String>() {
			{
				put("description_en_US", _description);
			}
		};

		Map<String, String> descStrings_NL = new HashMap<>();

		Map<String, Map<String, String>> descMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("description_en_US", descStrings_US);
					put("description_nl_NL", descStrings_NL);
				}
			};

		articleIdDescriptionExpectedMap.put(
			journalArticle.getArticleId(), descMap);

		Map<String, String> contentStrings_US = new HashMap<String, String>() {
			{
				put("content_en_US", _content);
			}
		};

		Map<String, String> contentStrings_NL = new HashMap<>();

		Map<String, Map<String, String>> contentsMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("content_en_US", contentStrings_US);
					put("content_nl_NL", contentStrings_NL);
				}
			};

		articleIdContentExpectedMap.put(
			journalArticle.getArticleId(), contentsMap);
	}

	@SuppressWarnings("serial")
	private void _addExpectedValueMap_2(
		Map<String, Map<String, Map<String, String>>> articleIdTitleExpectedMap,
		Map<String, Map<String, Map<String, String>>>
			articleIdDescriptionExpectedMap,
		Map<String, Map<String, Map<String, String>>>
			articleIdContentExpectedMap,
		JournalArticle journalArticle) {

		HashMap<String, String> titleStrings = new HashMap<String, String>() {
			{
				put("title_en_US", _title);
				put("title_nl_NL", _title_nl);
			}
		};

		HashMap<String, String> localizedTitleStrings =
			new HashMap<String, String>() {
				{
					put("localized_title_nl_NL", _title_nl);
					put("localized_title_nl_NL_sortable", _title_nl);
				}
			};

		Map<String, Map<String, String>> titlesMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("title", titleStrings);
					put("localized_title_nl_NL", localizedTitleStrings);
				}
			};

		articleIdTitleExpectedMap.put(journalArticle.getArticleId(), titlesMap);

		Map<String, String> descStrings_US = new HashMap<String, String>() {
			{
				put("description_en_US", _description);
			}
		};

		Map<String, String> descStrings_NL = new HashMap<String, String>() {
			{
				put("description_nl_NL", _description_nl);
			}
		};

		Map<String, Map<String, String>> descMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("description_en_US", descStrings_US);
					put("description_nl_NL", descStrings_NL);
				}
			};

		articleIdDescriptionExpectedMap.put(
			journalArticle.getArticleId(), descMap);

		Map<String, String> contentStrings_US = new HashMap<String, String>() {
			{
				put("content_en_US", _content);
			}
		};

		Map<String, String> contentStrings_NL = new HashMap<String, String>() {
			{
				put("content_nl_NL", _content_nl);
			}
		};

		Map<String, Map<String, String>> contentsMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("content_en_US", contentStrings_US);
					put("content_nl_NL", contentStrings_NL);
				}
			};

		articleIdContentExpectedMap.put(
			journalArticle.getArticleId(), contentsMap);
	}

	@SuppressWarnings("serial")
	private void _addExpectedValueMap_3(
		Map<String, Map<String, Map<String, String>>> articleIdTitleExpectedMap,
		Map<String, Map<String, Map<String, String>>>
			articleIdDescriptionExpectedMap,
		Map<String, Map<String, Map<String, String>>>
			articleIdContentExpectedMap,
		JournalArticle journalArticle) {

		HashMap<String, String> titleStrings = new HashMap<String, String>() {
			{
				put("title_nl_NL", _title);
			}
		};

		HashMap<String, String> localizedTitleStrings =
			new HashMap<String, String>() {
				{
					put("localized_title_nl_NL", _title);
					put("localized_title_nl_NL_sortable", _title);
				}

			};

		Map<String, Map<String, String>> titlesMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("title", titleStrings);
					put("localized_title_nl_NL", localizedTitleStrings);
				}
			};

		articleIdTitleExpectedMap.put(journalArticle.getArticleId(), titlesMap);

		Map<String, String> descStrings_NL = new HashMap<String, String>() {
			{
				put("description_nl_NL", _description);
			}
		};

		Map<String, String> descStrings_US = new HashMap<>();

		Map<String, Map<String, String>> descMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("description_en_US", descStrings_US);
					put("description_nl_NL", descStrings_NL);
				}
			};

		articleIdDescriptionExpectedMap.put(
			journalArticle.getArticleId(), descMap);

		HashMap<String, String> contentStrings_NL =
			new HashMap<String, String>() {

				{
					put("content_nl_NL", _content);
				}

			};

		HashMap<String, String> contentStrings_US = new HashMap<>();

		Map<String, Map<String, String>> contentsMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("content_en_US", contentStrings_US);
					put("content_nl_NL", contentStrings_NL);
				}
			};

		articleIdContentExpectedMap.put(
			journalArticle.getArticleId(), contentsMap);
	}

	private SearchContext _getSearchContext(String searchTerm, Locale locale)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setKeywords(searchTerm);

		searchContext.setLocale(locale);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(StringPool.STAR);

		return searchContext;
	}

	private List<Document> _search(String searchTerm, Locale locale) {
		try {
			SearchContext searchContext = _getSearchContext(searchTerm, locale);

			Hits hits = _indexer.search(searchContext);

			return hits.toList();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Inject
	private static IndexerRegistry _indexerRegistry;

	private String _content;
	private String _content_nl;
	private String _description;
	private String _description_nl;
	private Group _group;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	private Indexer<JournalArticle> _indexer;
	private final Map<String, Map<String, Map<String, Map<String, String>>>>
		_indexTypeExpectedMap = new HashMap<>(3);
	private String _title;
	private String _title_nl;
	private User _user;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}