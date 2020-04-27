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
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.search.test.util.SearchTestRule;
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
 */
@RunWith(Arquillian.class)
@Sync
public class BookmarksEntryIndexerIndexedFieldsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_group = userSearchFixture.addGroup();

		_groups = userSearchFixture.getGroups();

		_user = userSearchFixture.addUser(
			RandomTestUtil.randomString(), _group);

		_users = userSearchFixture.getUsers();

		bookmarksFixture = new BookmarksFixture(_group, _user);

		_bookmarksEntries = bookmarksFixture.getBookmarksEntries();

		_bookmarksFolders = bookmarksFixture.getBookmarksFolders();

		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	@Test
	public void testIndexedFields() throws Exception {
		BookmarksEntry bookmarksEntry = bookmarksFixture.createBookmarksEntry();

		String searchTerm = bookmarksEntry.getUserName();

		assertFieldValues(_expectedFieldValues(bookmarksEntry), searchTerm);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertFieldValues(Map<String, ?> map, String searchTerm) {
		FieldValuesAssert.assertFieldValues(
			map, name -> !name.equals("score"),
			searcher.search(
				searchRequestBuilderFactory.builder(
				).companyId(
					_group.getCompanyId()
				).fields(
					StringPool.STAR
				).groupIds(
					_group.getGroupId()
				).modelIndexerClasses(
					MODEL_INDEXER_CLASS
				).queryString(
					searchTerm
				).build()));
	}

	protected static final Class<?> MODEL_INDEXER_CLASS = BookmarksEntry.class;

	protected BookmarksFixture bookmarksFixture;
	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	@Inject
	protected Searcher searcher;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _expectedFieldValues(
			BookmarksEntry bookmarksEntry)
		throws Exception {

		Map<String, String> map = HashMapBuilder.put(
			Field.COMPANY_ID, String.valueOf(bookmarksEntry.getCompanyId())
		).put(
			Field.DESCRIPTION, bookmarksEntry.getDescription()
		).put(
			Field.ENTRY_CLASS_NAME, BookmarksEntry.class.getName()
		).put(
			Field.ENTRY_CLASS_PK, String.valueOf(bookmarksEntry.getEntryId())
		).put(
			Field.FOLDER_ID, String.valueOf(bookmarksEntry.getFolderId())
		).put(
			Field.GROUP_ID, String.valueOf(bookmarksEntry.getGroupId())
		).put(
			Field.SCOPE_GROUP_ID, String.valueOf(bookmarksEntry.getGroupId())
		).put(
			Field.STAGING_GROUP, String.valueOf(_group.isStagingGroup())
		).put(
			Field.STATUS, String.valueOf(bookmarksEntry.getStatus())
		).put(
			Field.TITLE, bookmarksEntry.getName()
		).put(
			Field.URL, bookmarksEntry.getUrl()
		).put(
			Field.USER_ID, String.valueOf(bookmarksEntry.getUserId())
		).put(
			Field.USER_NAME, StringUtil.lowerCase(bookmarksEntry.getUserName())
		).put(
			"title_sortable", StringUtil.lowerCase(bookmarksEntry.getName())
		).put(
			"visible", "true"
		).build();

		bookmarksFixture.populateLocalizedTitles(bookmarksEntry.getName(), map);
		bookmarksFixture.populateTreePath(bookmarksEntry.getTreePath(), map);

		indexedFieldsFixture.populatePriority("0.0", map);
		indexedFieldsFixture.populateUID(
			BookmarksEntry.class.getName(), bookmarksEntry.getEntryId(), map);
		indexedFieldsFixture.populateViewCount(
			BookmarksEntry.class, bookmarksEntry.getEntryId(), map);

		_populateDates(bookmarksEntry, map);
		_populateRoles(bookmarksEntry, map);

		return map;
	}

	private void _populateDates(
		BookmarksEntry bookmarksEntry, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, bookmarksEntry.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, bookmarksEntry.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.PUBLISH_DATE, bookmarksEntry.getCreateDate(), map);
		indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	private void _populateRoles(
			BookmarksEntry bookmarksEntry, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			bookmarksEntry.getCompanyId(), BookmarksEntry.class.getName(),
			bookmarksEntry.getEntryId(), bookmarksEntry.getGroupId(), null,
			map);
	}

	@DeleteAfterTestRun
	private List<BookmarksEntry> _bookmarksEntries;

	@DeleteAfterTestRun
	private List<BookmarksFolder> _bookmarksFolders;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

}