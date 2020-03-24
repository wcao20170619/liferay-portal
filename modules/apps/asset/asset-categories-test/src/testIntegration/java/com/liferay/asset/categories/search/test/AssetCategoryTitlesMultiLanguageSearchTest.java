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

package com.liferay.asset.categories.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprint;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.facet.category.CategoryFacetFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.SearchRetryFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.After;
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
public class AssetCategoryTitlesMultiLanguageSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	@SuppressWarnings("deprecation")
	public void setUp() throws Exception {
		_journalArticleSearchFixture = new JournalArticleSearchFixture(
			_journalArticleLocalService);

		_journalArticleSearchFixture.setUp();

		_journalArticles = _journalArticleSearchFixture.getJournalArticles();

		_userSearchFixture = new UserSearchFixture();

		_userSearchFixture.setUp();

		_groups = _userSearchFixture.getGroups();
		_users = _userSearchFixture.getUsers();

		_assetCategories = new ArrayList<>();
		_assetVocabularies = new ArrayList<>();
		_user = _userSearchFixture.addUser(
			RandomTestUtil.randomString(), _userSearchFixture.addGroup());
	}

	@After
	public void tearDown() throws Exception {
		_journalArticleSearchFixture.tearDown();
		_userSearchFixture.tearDown();
	}

	@Test
	public void testChineseCategories() throws Exception {
		String categoryTitleString = "你好";
		String journalArticleContentString = "English";
		String journalArticleTitleString = "English";

		Locale locale = LocaleUtil.CHINA;

		Group group = _addGroup(locale);

		AssetCategory assetCategory = _addCategory(
			group, _addVocabulary(group), categoryTitleString, locale);

		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setAssetCategoryIds(
						new long[] {assetCategory.getCategoryId()});
					setGroupId(group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(locale, journalArticleContentString);

								setDefaultLocale(locale);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(locale, journalArticleTitleString);
							}
						});
					setUserId(_user.getUserId());
				}
			});

		_assertSearch(categoryTitleString, assetCategory, locale, group);
	}

	@Test
	public void testEnglishCategories() throws Exception {
		String categoryTitleString = "testCategory";
		String journalArticleContentString = "testContent";
		String journalArticleTitleString = "testTitle";

		Locale locale = LocaleUtil.US;

		Group group = _addGroup(locale);

		AssetCategory assetCategory = _addCategory(
			group, _addVocabulary(group), categoryTitleString, locale);

		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setAssetCategoryIds(
						new long[] {assetCategory.getCategoryId()});
					setGroupId(group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(locale, journalArticleContentString);

								setDefaultLocale(locale);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(locale, journalArticleTitleString);
							}
						});
					setUserId(_user.getUserId());
				}
			});

		_assertSearch(categoryTitleString, assetCategory, locale, group);
	}

	@Test
	public void testJapaneseCategories() throws Exception {
		String categoryTitleString1 = "東京";
		String categoryTitleString2 = "京都";
		String journalArticleContentString1 = "豊島区";
		String journalArticleContentString2 = "伏見区";
		String journalArticleTitleString1 = "豊島区";
		String journalArticleTitleString2 = "伏見区";
		Locale locale = LocaleUtil.JAPAN;
		String vocabularyTitleString = "ボキャブラリ";

		Group group = _addGroup(locale);

		AssetVocabulary assetVocabulary = _addVocabulary(
			group, vocabularyTitleString);

		AssetCategory assetCategory1 = _addCategory(
			group, assetVocabulary, categoryTitleString1, locale);

		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setAssetCategoryIds(
						new long[] {assetCategory1.getCategoryId()});
					setGroupId(group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(locale, journalArticleContentString1);

								setDefaultLocale(locale);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(locale, journalArticleTitleString1);
							}
						});
					setUserId(_user.getUserId());
				}
			});

		AssetCategory assetCategory2 = _addCategory(
			group, assetVocabulary, categoryTitleString2, locale);

		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setAssetCategoryIds(
						new long[] {assetCategory2.getCategoryId()});
					setGroupId(group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(locale, journalArticleContentString2);

								setDefaultLocale(locale);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(locale, journalArticleTitleString2);
							}
						});
					setUserId(_user.getUserId());
				}
			});

		_assertSearch(categoryTitleString1, assetCategory1, locale, group);
		_assertSearch(categoryTitleString2, assetCategory2, locale, group);
	}

	private AssetCategory _addCategory(
			Group group, AssetVocabulary assetVocabulary, String title,
			Locale locale)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), _user.getUserId());

		Map<Locale, String> titleMap = HashMapBuilder.put(
			locale, title
		).build();

		Locale previousLocale = LocaleThreadLocal.getSiteDefaultLocale();

		LocaleThreadLocal.setSiteDefaultLocale(locale);

		try {
			AssetCategory assetCategory =
				_assetCategoryLocalService.addCategory(
					_user.getUserId(), group.getGroupId(),
					AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, titleMap,
					new HashMap<>(), assetVocabulary.getVocabularyId(),
					new String[0], serviceContext);

			_assetCategories.add(assetCategory);

			return assetCategory;
		}
		finally {
			LocaleThreadLocal.setSiteDefaultLocale(previousLocale);
		}
	}

	@SuppressWarnings("deprecation")
	private Group _addGroup(Locale locale) throws Exception {
		return _userSearchFixture.addGroup(
			new GroupBlueprint() {
				{
					setDefaultLocale(locale);
				}
			});
	}

	private AssetVocabulary _addVocabulary(Group group) throws Exception {
		return _addVocabulary(group, RandomTestUtil.randomString());
	}

	private AssetVocabulary _addVocabulary(Group group, String title)
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addDefaultVocabulary(
				group.getGroupId());

		assetVocabulary.setTitle(title);

		assetVocabulary = _assetVocabularyLocalService.updateAssetVocabulary(
			assetVocabulary);

		_assetVocabularies.add(assetVocabulary);

		return assetVocabulary;
	}

	private void _assertFacetedSearcher(
			AssetCategory assetCategory, String categoryTitleString,
			Group group, Locale locale)
		throws Exception {

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).fields(
				Field.ASSET_CATEGORY_IDS
			).queryString(
				categoryTitleString
			).withSearchContext(
				searchContext -> {
					searchContext.setCompanyId(group.getCompanyId());
					searchContext.setGroupIds(new long[] {group.getGroupId()});
					searchContext.setLocale(locale);
				}
			).build());

		Stream<Document> stream = searchResponse.getDocumentsStream();

		_searchRetryFixture.assertSearch(
			() -> _assertHits(
				assetCategory, searchResponse.getRequestString(), stream));
	}

	private void _assertHits(
		AssetCategory assetCategory, String queryString,
		Stream<Document> stream) {

		List<String> queryStringList = Arrays.asList(
			String.valueOf(assetCategory.getCategoryId()));

		Stream<String> queryStringStream = queryStringList.stream();

		DocumentsAssert.assertValuesIgnoreRelevance(
			queryString, stream, Field.ASSET_CATEGORY_IDS, queryStringStream);
	}

	private void _assertJournalArticleIndexer(
			AssetCategory assetCategory, String categoryTitleString,
			Group group, Locale locale)
		throws Exception {

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).fields(
				Field.ASSET_CATEGORY_IDS
			).modelIndexerClasses(
				JournalArticle.class
			).queryString(
				categoryTitleString
			).withSearchContext(
				searchContext -> {
					searchContext.setCompanyId(group.getCompanyId());
					searchContext.setGroupIds(new long[] {group.getGroupId()});
					searchContext.setLocale(locale);
				}
			).build());

		Stream<Document> stream = searchResponse.getDocumentsStream();

		_searchRetryFixture.assertSearch(
			() -> _assertHits(
				assetCategory, searchResponse.getRequestString(), stream));
	}

	private void _assertSearch(
			String categoryTitleString, AssetCategory assetCategory,
			Locale locale, Group group)
		throws Exception {

		_assertJournalArticleIndexer(
			assetCategory, categoryTitleString, group, locale);
		_assertFacetedSearcher(
			assetCategory, categoryTitleString, group, locale);
	}

	@Inject
	private static AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private static AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private static CategoryFacetFactory _categoryFacetFactory;

	@Inject
	private static JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private static Searcher _searcher;

	@Inject
	private static SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@DeleteAfterTestRun
	private List<AssetCategory> _assetCategories;

	@DeleteAfterTestRun
	private List<AssetVocabulary> _assetVocabularies;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	private JournalArticleSearchFixture _journalArticleSearchFixture;
	private final SearchRetryFixture _searchRetryFixture =
		new SearchRetryFixture.Builder().build();
	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

	private UserSearchFixture _userSearchFixture;

}