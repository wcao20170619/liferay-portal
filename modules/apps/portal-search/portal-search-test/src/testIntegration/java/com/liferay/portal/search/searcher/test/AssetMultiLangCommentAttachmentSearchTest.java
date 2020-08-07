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

package com.liferay.portal.search.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;

import java.util.Locale;
import java.util.function.Function;

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
public class AssetMultiLangCommentAttachmentSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	public Hits searchBaseModelsCount(
			String attributeKey, String attributeValue, Class<?>[] classes,
			String[] fields, boolean includeAttachments,
			boolean includeDiscussions, Locale locale, String searchTerm)
		throws Exception {

		SearchResponse searchResponse = searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				fields
			).groupIds(
				_group.getGroupId()
			).locale(
				locale
			).modelIndexerClasses(
				classes
			).queryString(
				searchTerm
			).withSearchContext(
				searchContext -> {
					searchContext.setAttribute(attributeKey, attributeValue);
					searchContext.setIncludeAttachments(includeAttachments);
					searchContext.setIncludeDiscussions(includeDiscussions);
				}
			).build());

		return searchResponse.withHitsGet(Function.identity());
	}

	@Before
	public void setUp() throws Exception {
		_addGroupAndUser();

		_multiLangAssetCommentAttachmentSearchFixture =
			new AssetMultiLangCommentAttachmentSearchFixture(
				assetCategoryService, assetVocabularyLocalService,
				blogsEntryLocalService, _group, mbMessageLocalService, _user,
				wikiNodeLocalService, wikiPageLocalService);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAssetCategoryLocalizedSearch() throws Exception {
		assertBaseModelsCount(
			null, null, new Class<?>[] {AssetCategory.class}, 0,
			new String[] {StringPool.STAR}, false, false,
			LocaleUtil.getDefault(), StringPool.BLANK);

		_multiLangAssetCommentAttachmentSearchFixture.addAssetCategory(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "entity title"
			).put(
				LocaleUtil.HUNGARY, "entitas neve"
			).build());

		assertBaseModelsCount(
			null, null, new Class<?>[] {AssetCategory.class}, 1,
			new String[] {StringPool.STAR}, false, false,
			LocaleUtil.getDefault(), "title");

		assertBaseModelsCount(
			Field.TITLE, "nev", new Class<?>[] {AssetCategory.class}, 1,
			new String[] {StringPool.STAR}, false, false, LocaleUtil.HUNGARY,
			"nev");
	}

	@Test
	public void testBlogsEntryAttachementFileName() throws Exception {
		String title = "Image";

		_multiLangAssetCommentAttachmentSearchFixture.addBlogsEntryImage(
			BlogsEntry.class.getName(), title);

		assertBaseModelsCount(
			null, null, new Class<?>[] {BlogsEntry.class}, 2,
			new String[] {StringPool.STAR}, true, false,
			LocaleUtil.getDefault(), title);
	}

	@Test
	public void testBlogsEntryComment() throws Exception {
		_searchBlogsEntryComment(false);
	}

	@Test
	public void testBlogsEntryCommentByKeywords() throws Exception {
		_searchBlogsEntryComment(true);
	}

	@Test
	public void testMBMessageAttachementFileName() throws Exception {
		String subject = "Subject";

		_multiLangAssetCommentAttachmentSearchFixture.addMessageAttachment(
			subject, subject);

		assertBaseModelsCount(
			null, null, new Class<?>[] {MBMessage.class}, 2,
			new String[] {StringPool.STAR}, true, false,
			LocaleUtil.getDefault(), subject);
	}

	@Test
	public void testMBMessageComment() throws Exception {
		_searchMBMessageComment(false);
	}

	@Test
	public void testMBMessageCommentsByKeywords() throws Exception {
		_searchMBMessageComment(true);
	}

	@Test
	public void testWikiPageAttachementFileName() throws Exception {
		String title = "Attachement";

		_multiLangAssetCommentAttachmentSearchFixture.addWikiPageAttachment(
			title, title);

		assertBaseModelsCount(
			null, null, new Class<?>[] {WikiPage.class}, 2,
			new String[] {StringPool.STAR}, true, false,
			LocaleUtil.getDefault(), title);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertBaseModelsCount(
			String attributeKey, String attributeValue,
			final Class<?>[] classes, final int expectedCount, String[] fields,
			boolean includeAttachments, boolean includeDiscussions,
			Locale locale, String searchTerm)
		throws Exception {

		Hits hits = searchBaseModelsCount(
			attributeKey, attributeValue, classes, fields, includeAttachments,
			includeDiscussions, locale, searchTerm);

		Assert.assertEquals(hits.toString(), expectedCount, hits.getLength());
	}

	@Inject
	protected AssetCategoryService assetCategoryService;

	@Inject
	protected AssetVocabularyLocalService assetVocabularyLocalService;

	@Inject
	protected BlogsEntryLocalService blogsEntryLocalService;

	@Inject
	protected MBMessageLocalService mbMessageLocalService;

	@Inject
	protected Searcher searcher;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected WikiNodeLocalService wikiNodeLocalService;

	@Inject
	protected WikiPageLocalService wikiPageLocalService;

	private void _addGroupAndUser() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = TestPropsValues.getUser();

		PermissionThreadLocal.setPermissionChecker(
			_permissionCheckerFactory.create(_user));
	}

	private void _searchBlogsEntryComment(boolean searchByKeywords)
		throws Exception {

		String comment = "comment";

		BlogsEntry blogsEntry =
			_multiLangAssetCommentAttachmentSearchFixture.addBlogsEntry(
				comment, comment);

		assertBaseModelsCount(
			null, null, new Class<?>[0], 1, new String[] {StringPool.STAR},
			false, true, LocaleUtil.getDefault(), comment);

		_multiLangAssetCommentAttachmentSearchFixture.addComment(
			BlogsEntry.class.getName(), comment,
			(Long)blogsEntry.getPrimaryKeyObj());

		if (!searchByKeywords) {
			comment = StringPool.BLANK;
		}

		assertBaseModelsCount(
			null, null, new Class<?>[0], searchByKeywords ? 1 : 2,
			new String[] {StringPool.STAR}, false, true,
			LocaleUtil.getDefault(), comment);
	}

	private void _searchMBMessageComment(boolean searchByKeywords)
		throws Exception {

		String comment = "comment";

		MBMessage mbMessage =
			_multiLangAssetCommentAttachmentSearchFixture.addMBMessage(comment);

		assertBaseModelsCount(
			null, null, new Class<?>[] {MBMessage.class}, 1,
			new String[] {StringPool.STAR}, false, false,
			LocaleUtil.getDefault(), comment);

		_multiLangAssetCommentAttachmentSearchFixture.addComment(
			MBMessage.class.getName(), comment,
			(Long)mbMessage.getPrimaryKeyObj());

		if (!searchByKeywords) {
			comment = StringPool.BLANK;
		}

		assertBaseModelsCount(
			null, null, new Class<?>[0], searchByKeywords ? 1 : 2,
			new String[] {StringPool.STAR}, false, true,
			LocaleUtil.getDefault(), comment);
	}

	@DeleteAfterTestRun
	private Group _group;

	private AssetMultiLangCommentAttachmentSearchFixture
		_multiLangAssetCommentAttachmentSearchFixture;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	private User _user;

}