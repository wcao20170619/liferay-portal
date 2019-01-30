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
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.search.FileEntryBlueprint;
import com.liferay.document.library.test.util.search.FileEntrySearchFixture;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprint;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleDescription;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.ReaderInputStream;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.io.InputStream;
import java.io.StringReader;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 * @author Adam Brandizzi
 */
@RunWith(Arquillian.class)
@Sync
public class MultiLanguageSearchFieldsSharedAcrossIndexersTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		WorkflowThreadLocal.setEnabled(false);

		_fileEntrySearchFixture = new FileEntrySearchFixture(dlAppLocalService);

		_fileEntrySearchFixture.setUp();

		_fileEntries = _fileEntrySearchFixture.getFileEntries();

		_journalArticleSearchFixture = new JournalArticleSearchFixture(
			journalArticleLocalService);

		_journalArticleSearchFixture.setUp();

		_journalArticles = _journalArticleSearchFixture.getJournalArticles();

		_userSearchFixture = new UserSearchFixture();

		_userSearchFixture.setUp();

		_groups = _userSearchFixture.getGroups();
		_users = _userSearchFixture.getUsers();

		addGroupAndUser();
	}

	@After
	public void tearDown() throws Exception {
		_journalArticleSearchFixture.tearDown();

		_userSearchFixture.tearDown();
	}

	@Test
	public void testContent() throws Exception {
		addArticlesWithEnglishWordsInUsAndNlTranslations();

		addFileEntryWithEnglishWords();

		assertSearchMatchesAllAssets(LocaleUtil.US, US_CONTENT);
	}

	@Test
	public void testContentWithAlternateDisplayLanguage() throws Exception {
		addArticlesWithEnglishWordsInUsAndNlTranslations();

		addFileEntryWithEnglishWords();

		assertSearchMatchesAllAssets(LocaleUtil.NETHERLANDS, US_CONTENT);
	}

	@Test
	public void testTitle() throws Exception {
		addArticlesWithEnglishWordsInUsAndNlTranslations();

		addFileEntryWithEnglishWords();

		assertSearchMatchesAllAssets(LocaleUtil.US, US_TITLE);
	}

	@Test
	public void testTitleWithAlternateDisplayLanguage() throws Exception {
		addArticlesWithEnglishWordsInUsAndNlTranslations();

		addFileEntryWithEnglishWords();

		assertSearchMatchesAllAssets(LocaleUtil.NETHERLANDS, US_TITLE);
	}

	protected void addArticlesWithEnglishWordsInUsAndNlTranslations() {
		addJournalArticle(
			new JournalArticleContent() {
				{
					put(LocaleUtil.NETHERLANDS, NL_CONTENT);
					put(LocaleUtil.US, US_CONTENT);

					setDefaultLocale(LocaleUtil.US);
					setName("content");
				}
			},
			new JournalArticleDescription() {
				{
					put(LocaleUtil.NETHERLANDS, NL_DESCRIPTION);
					put(LocaleUtil.US, US_DESCRIPTION);
				}
			},
			new JournalArticleTitle() {
				{
					put(LocaleUtil.NETHERLANDS, NL_TITLE);
					put(LocaleUtil.US, US_TITLE);
				}
			});

		addJournalArticle(
			new JournalArticleContent() {
				{
					put(LocaleUtil.US, US_CONTENT);

					setDefaultLocale(LocaleUtil.US);
					setName("content");
				}
			},
			new JournalArticleDescription() {
				{
					put(LocaleUtil.US, US_DESCRIPTION);
				}
			},
			new JournalArticleTitle() {
				{
					put(LocaleUtil.US, US_TITLE);
				}
			});

		addJournalArticle(
			new JournalArticleContent() {
				{
					put(LocaleUtil.NETHERLANDS, US_CONTENT);

					setDefaultLocale(LocaleUtil.NETHERLANDS);
					setName("content");
				}
			},
			new JournalArticleDescription() {
				{
					put(LocaleUtil.NETHERLANDS, US_DESCRIPTION);
				}
			},
			new JournalArticleTitle() {
				{
					put(LocaleUtil.NETHERLANDS, US_TITLE);
				}
			});
	}

	protected FileEntry addFileEntryWithEnglishWords() throws Exception {
		try (InputStream inputStream = new ReaderInputStream(
				new StringReader(US_CONTENT))) {

			return _fileEntrySearchFixture.addFileEntry(
				new FileEntryBlueprint() {
					{
						setGroupId(_group.getGroupId());
						setInputStream(inputStream);
						setTitle(US_TITLE);
						setUserId(_user.getUserId());
					}
				});
		}
	}

	protected void addGroupAndUser() throws Exception {
		Group group = _userSearchFixture.addGroup();

		_group = group;
		_user = _userSearchFixture.addUser(
			RandomTestUtil.randomString(), group);
	}

	protected JournalArticle addJournalArticle(
		JournalArticleContent journalArticleContentParam,
		JournalArticleDescription journalArticleDescriptionParam,
		JournalArticleTitle journalArticleTitleParam) {

		return _journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setJournalArticleContent(journalArticleContentParam);
					setJournalArticleDescription(
						journalArticleDescriptionParam);
					setJournalArticleTitle(journalArticleTitleParam);
					setUserId(_user.getUserId());
				}
			});
	}

	protected void assertSearchMatchesAllAssets(
		Locale locale, String searchTerm) {

		SearchContext searchContext = getSearchContext(locale, searchTerm);

		Hits hits = search(searchContext);

		List<String> keys = Stream.concat(
			_fileEntries.stream().map(FileEntry::getPrimaryKey),
			_journalArticles.stream().map(JournalArticle::getResourcePrimKey)
		).map(
			String::valueOf
		).collect(
			Collectors.toList()
		);

		DocumentsAssert.assertValuesIgnoreRelevance(
			(String)searchContext.getAttribute("queryString"), hits.getDocs(),
			Field.ENTRY_CLASS_PK, keys);
	}

	protected SearchContext getSearchContext() {
		SearchContext searchContext = new SearchContext();

		try {
			searchContext.setCompanyId(TestPropsValues.getCompanyId());
			searchContext.setGroupIds(new long[] {_group.getGroupId()});
			searchContext.setUserId(TestPropsValues.getUserId());
		}
		catch (PortalException pe) {
			throw new RuntimeException(pe);
		}

		return searchContext;
	}

	protected SearchContext getSearchContext(Locale locale) {
		SearchContext searchContext = getSearchContext();

		searchContext.setEntryClassNames(
			new String[] {
				DLFileEntry.class.getName(), JournalArticle.class.getName()
			});

		searchContext.setLocale(locale);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setLocale(locale);
		queryConfig.setSelectedFieldNames(StringPool.STAR);

		return searchContext;
	}

	protected SearchContext getSearchContext(Locale locale, String searchTerm) {
		SearchContext searchContext = getSearchContext(locale);

		searchContext.setKeywords(searchTerm);

		return searchContext;
	}

	protected Hits search(SearchContext searchContext) {
		FacetedSearcher facetedSearcher =
			facetedSearcherManager.createFacetedSearcher();

		try {
			return facetedSearcher.search(searchContext);
		}
		catch (SearchException se) {
			throw new RuntimeException(se);
		}
	}

	protected static final String NL_CONTENT = "inhoud";

	protected static final String NL_DESCRIPTION = "beschrijving";

	protected static final String NL_TITLE = "engels";

	protected static final String US_CONTENT = "content";

	protected static final String US_DESCRIPTION = "description";

	protected static final String US_TITLE = "english";

	@Inject
	protected static FacetedSearcherManager facetedSearcherManager;

	@Inject
	protected DLAppLocalService dlAppLocalService;

	@Inject
	protected JournalArticleLocalService journalArticleLocalService;

	private List<FileEntry> _fileEntries;
	private FileEntrySearchFixture _fileEntrySearchFixture;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	private JournalArticleSearchFixture _journalArticleSearchFixture;
	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

	private UserSearchFixture _userSearchFixture;

}