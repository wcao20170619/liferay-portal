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

package com.liferay.bookmarks.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.HitsAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.search.test.util.SearchContextTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 * @author Lucas Marques
 */
@RunWith(Arquillian.class)
public class BookmarksFolderIndexerIndexedFieldsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		setUpUserSearchFixture();

		setUpBookmarksFolderFixture();

		setUpIndexedFieldsFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		BookmarksFolder bookmarksFolder =
			bookmarksFixture.createBookmarksFolder();

		String searchTerm = bookmarksFolder.getUserName();

		Document document = _searchOnlyOne(searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		FieldValuesAssert.assertFieldValues(
			_expectedFieldValues(bookmarksFolder), document, searchTerm);
	}

	protected void setUpBookmarksFolderFixture() throws Exception {
		bookmarksFixture = new BookmarksFixture(_group, _user);

		_bookmarksFolders = bookmarksFixture.getBookmarksFolders();
	}

	protected void setUpIndexedFieldsFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	@SuppressWarnings("deprecation")
	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_group = userSearchFixture.addGroup();

		_groups = userSearchFixture.getGroups();

		_user = userSearchFixture.addUser(
			RandomTestUtil.randomString(), _group);

		_users = userSearchFixture.getUsers();
	}

	protected BookmarksFixture bookmarksFixture;
	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected IndexerRegistry indexerRegistry;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _expectedFieldValues(
			BookmarksFolder bookmarksFolder)
		throws Exception {

		Map<String, String> map = HashMapBuilder.put(
			Field.COMPANY_ID, String.valueOf(bookmarksFolder.getCompanyId())
		).put(
			Field.DESCRIPTION, bookmarksFolder.getDescription()
		).put(
			Field.ENTRY_CLASS_NAME, BookmarksFolder.class.getName()
		).put(
			Field.ENTRY_CLASS_PK, String.valueOf(bookmarksFolder.getFolderId())
		).put(
			Field.FOLDER_ID, String.valueOf(bookmarksFolder.getParentFolderId())
		).put(
			Field.GROUP_ID, String.valueOf(bookmarksFolder.getGroupId())
		).put(
			Field.SCOPE_GROUP_ID, String.valueOf(bookmarksFolder.getGroupId())
		).put(
			Field.STAGING_GROUP, String.valueOf(_group.isStagingGroup())
		).put(
			Field.STATUS, String.valueOf(bookmarksFolder.getStatus())
		).put(
			Field.TITLE, bookmarksFolder.getName()
		).put(
			Field.USER_ID, String.valueOf(bookmarksFolder.getUserId())
		).put(
			Field.USER_NAME, StringUtil.lowerCase(bookmarksFolder.getUserName())
		).put(
			"title_sortable", StringUtil.lowerCase(bookmarksFolder.getName())
		).put(
			"visible", "true"
		).build();

		bookmarksFixture.populateLocalizedTitles(
			bookmarksFolder.getName(), map);
		bookmarksFixture.populateTreePath(bookmarksFolder.getTreePath(), map);

		indexedFieldsFixture.populatePriority("0.0", map);
		indexedFieldsFixture.populateUID(
			BookmarksFolder.class.getName(), bookmarksFolder.getFolderId(),
			map);
		indexedFieldsFixture.populateViewCount(
			BookmarksFolder.class, bookmarksFolder.getFolderId(), map);

		_populateDates(bookmarksFolder, map);
		_populateRoles(bookmarksFolder, map);

		return map;
	}

	private void _populateDates(
		BookmarksFolder bookmarksFolder, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, bookmarksFolder.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, bookmarksFolder.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.PUBLISH_DATE, bookmarksFolder.getCreateDate(), map);
		indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	private void _populateRoles(
			BookmarksFolder bookmarksFolder, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			bookmarksFolder.getCompanyId(), BookmarksFolder.class.getName(),
			bookmarksFolder.getFolderId(), bookmarksFolder.getGroupId(), null,
			map);
	}

	private Document _searchOnlyOne(String keywords) {
		try {
			SearchContext searchContext =
				SearchContextTestUtil.getSearchContext(
					_user.getUserId(), null, keywords, null, null);

			Hits hits = _indexer.search(searchContext);

			return HitsAssert.assertOnlyOne(hits);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	@DeleteAfterTestRun
	private List<BookmarksFolder> _bookmarksFolders;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@Inject(
		filter = "indexer.class.name=com.liferay.bookmarks.model.BookmarksFolder"
	)
	private Indexer<BookmarksFolder> _indexer;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

}