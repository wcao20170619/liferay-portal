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

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.search.FileEntryBlueprint;
import com.liferay.document.library.test.util.search.FileEntrySearchFixture;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprint;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.search.test.blogs.util.BlogsEntrySearchFixture;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Wade Cao
 */
public class AssetSearchFixture {

	public AssetSearchFixture(
		BlogsEntryLocalService blogsEntryLocalService,
		DLAppLocalService dlAppLocalService, Group group,
		JournalArticleLocalService journalArticleLocalService,
		MBMessageLocalService mbMessageLocalService, User user,
		WikiNodeLocalService wikiNodeLocalService,
		WikiPageLocalService wikiPageLocalService) {

		_blogsEntrySearchFixture = new BlogsEntrySearchFixture(
			blogsEntryLocalService);

		_fileEntrySearchFixture = new FileEntrySearchFixture(dlAppLocalService);

		_group = group;

		_journalArticleSearchFixture = new JournalArticleSearchFixture(
			journalArticleLocalService);

		_mbMessageLocalService = mbMessageLocalService;
		_user = user;
		_wikiNodeLocalService = wikiNodeLocalService;
		_wikiPageLocalService = wikiPageLocalService;
	}

	public BlogsEntry addBlogsEntry(String title) throws Exception {
		return _blogsEntrySearchFixture.addBlogsEntry(_group, _user, title);
	}

	public FileEntry addFileEntry(String keywords) throws Exception {
		return _fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setTitle(keywords);
					setUserId(_user.getUserId());
				}
			});
	}

	public JournalArticle addJournalArticle(Locale locale, String keyword) {
		return _journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(locale, RandomTestUtil.randomString());

								setDefaultLocale(locale);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(locale, keyword);
							}
						});
					setUserId(_user.getUserId());
				}
			});
	}

	public MBMessage addMessage(String title) throws Exception {
		return _mbMessageLocalService.addMessage(
			_user.getUserId(), RandomTestUtil.randomString(),
			_group.getGroupId(), MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			0L, MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, title,
			RandomTestUtil.randomString(), MBMessageConstants.DEFAULT_FORMAT,
			null, false, 0.0, false, _createServiceContext());
	}

	public WikiPage addWikiPage(String title) throws Exception {
		ServiceContext serviceContext = _createServiceContext();

		WikiNode wikiNode = _wikiNodeLocalService.addDefaultNode(
			_user.getUserId(), serviceContext);

		WikiPage wikiPage = _wikiPageLocalService.addPage(
			_user.getUserId(), wikiNode.getNodeId(), title, "content",
			"Summary", false, serviceContext);

		_wikiPages.add(wikiPage);

		return wikiPage;
	}

	public List<BlogsEntry> getBlogsEntries() {
		return _blogsEntrySearchFixture.getBlogsEntries();
	}

	public List<JournalArticle> getJournalArticles() {
		return _journalArticleSearchFixture.getJournalArticles();
	}

	public List<WikiPage> getWikiPages() {
		return _wikiPages;
	}

	public void reindex(Class<?>[] classes) throws Exception {
		IndexerFixture<?> indexerFixture = null;

		for (Class<?> clazz : classes) {
			indexerFixture = new IndexerFixture<>(clazz);

			indexerFixture.reindex(_group.getCompanyId());
		}
	}

	public void setUp() {
		_blogsEntrySearchFixture.setUp();
		_fileEntrySearchFixture.setUp();
		_journalArticleSearchFixture.setUp();
	}

	public void tearDown() throws Exception {
		_blogsEntrySearchFixture.tearDown();
		_fileEntrySearchFixture.tearDown();
		_journalArticleSearchFixture.tearDown();
	}

	public void updateDisplaySettings(Locale locale) throws Exception {
		Group group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), null, locale);

		_group.setModelAttributes(group.getModelAttributes());
	}

	private ServiceContext _createServiceContext() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAddGroupPermissions(false);

		return serviceContext;
	}

	private final BlogsEntrySearchFixture _blogsEntrySearchFixture;
	private final FileEntrySearchFixture _fileEntrySearchFixture;
	private final Group _group;
	private final JournalArticleSearchFixture _journalArticleSearchFixture;
	private final MBMessageLocalService _mbMessageLocalService;
	private final User _user;
	private final WikiNodeLocalService _wikiNodeLocalService;
	private final WikiPageLocalService _wikiPageLocalService;
	private final List<WikiPage> _wikiPages = new ArrayList<>();

}