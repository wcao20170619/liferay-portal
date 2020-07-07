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

package com.liferay.portal.search.sort.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
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
public class AssetSearchResultDefaultSortingTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		WorkflowThreadLocal.setEnabled(false);

		_defaultLocale = LocaleThreadLocal.getDefaultLocale();

		_userSearchFixture = new UserSearchFixture();

		_userSearchFixture.setUp();

		_users = _userSearchFixture.getUsers();

		_addGroupAndUser();

		_assetSearchFixture = new AssetSearchFixture(
			_blogsEntryLocalService, _dlAppLocalService, _group,
			journalArticleLocalService, mbMessageLocalService, _user,
			wikiNodeLocalService, wikiPageLocalService);

		_blogsEntries = _assetSearchFixture.getBlogsEntries();
		_journalArticles = _assetSearchFixture.getJournalArticles();
		_wikiPages = _assetSearchFixture.getWikiPages();
	}

	@After
	public void tearDown() throws Exception {
		LocaleThreadLocal.setDefaultLocale(_defaultLocale);
		_assetSearchFixture.tearDown();
	}

	@Test
	public void testEnglishTitle() throws Exception {
		_testLocaleKeywordSearch(LocaleUtil.US, "title");
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Inject
	protected JournalArticleLocalService journalArticleLocalService;

	@Inject
	protected MBMessageLocalService mbMessageLocalService;

	@Inject
	protected Searcher searcher;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected UserLocalService userLocalService;

	@Inject
	protected WikiNodeLocalService wikiNodeLocalService;

	@Inject
	protected WikiPageLocalService wikiPageLocalService;

	private void _addGroupAndUser() throws Exception {
		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		_group = groupSearchFixture.addGroup(new GroupBlueprint());

		_groups = groupSearchFixture.getGroups();

		_user = TestPropsValues.getUser();

		PermissionThreadLocal.setPermissionChecker(
			_permissionCheckerFactory.create(_user));
	}

	private User _addUser(String userName) throws Exception {
		String[] assetTagNames = {};

		return _userSearchFixture.addUser(userName, _group, assetTagNames);
	}

	private void _assertSearch(
			Class<?>[] classes, Locale locale, List<?> expectedEntryClassNames,
			String searchTerm)
		throws Exception {

		SearchResponse searchResponse = searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).fields(
				StringPool.STAR
			).groupIds(
				_group.getGroupId()
			).locale(
				locale
			).modelIndexerClasses(
				classes
			).queryString(
				searchTerm
			).build());

		_soresPrintout(searchResponse);

		Stream<Document> documentsStream = searchResponse.getDocumentsStream();

		List<String> acturalEntryClassNames = documentsStream.map(
			document -> document.getString(Field.ENTRY_CLASS_NAME)
		).collect(
			Collectors.toList()
		);

		Assert.assertEquals(
			searchResponse.getRequestString(), expectedEntryClassNames,
			acturalEntryClassNames);
	}

	private List<String> _getExpectedEntryClassNamesByOrder() {
		List<String> expectedEntryClassNames = new ArrayList<>();

		expectedEntryClassNames.add(BlogsEntry.class.getCanonicalName());
		expectedEntryClassNames.add(WikiPage.class.getCanonicalName());
		expectedEntryClassNames.add(MBMessage.class.getCanonicalName());
		expectedEntryClassNames.add(JournalArticle.class.getCanonicalName());
		expectedEntryClassNames.add(DLFileEntry.class.getCanonicalName());
		expectedEntryClassNames.add(User.class.getCanonicalName());

		return expectedEntryClassNames;
	}

	private void _setTestLocale(Locale locale) throws Exception {
		_assetSearchFixture.updateDisplaySettings(locale);

		LocaleThreadLocal.setDefaultLocale(locale);
	}

	private void _soresPrintout(SearchResponse searchResponse) {
		SearchHits searchHits = searchResponse.getSearchHits();

		List<SearchHit> searchHitList = searchHits.getSearchHits();

		System.out.println("Max score:" + searchHits.getMaxScore());

		for (SearchHit searchHit : searchHitList) {
			System.out.println(
				StringBundler.concat(
					"id:", searchHit.getId(), " score:", searchHit.getScore()));
		}
	}

	private void _testLocaleKeywordSearch(Locale locale, String keywords)
		throws Exception {

		_setTestLocale(locale);

		_assetSearchFixture.addBlogsEntry(keywords);
		_assetSearchFixture.addFileEntry(keywords);
		_assetSearchFixture.addJournalArticle(locale, keywords);
		_assetSearchFixture.addMessage(keywords);
		_assetSearchFixture.addWikiPage(keywords);

		_addUser(keywords);

		Class<?>[] classes = new Class<?>[] {
			BlogsEntry.class, JournalArticle.class, User.class, MBMessage.class,
			WikiPage.class, DLFileEntry.class
		};

		_assertSearch(
			classes, locale, _getExpectedEntryClassNamesByOrder(), keywords);

		_assetSearchFixture.reindex(classes);

		_assertSearch(
			classes, locale, _getExpectedEntryClassNamesByOrder(), keywords);
	}

	private AssetSearchFixture _assetSearchFixture;

	@DeleteAfterTestRun
	private List<BlogsEntry> _blogsEntries;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	private Locale _defaultLocale;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

	private UserSearchFixture _userSearchFixture;

	@DeleteAfterTestRun
	private List<WikiPage> _wikiPages;

}