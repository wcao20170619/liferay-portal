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
import com.liferay.bookmarks.model.BookmarksFolderConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.Collections;
import java.util.List;

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
public class BookmarksEntryIndexerReindexTest {

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
	}

	@Test
	public void testReindex() throws Exception {
		BookmarksEntry bookmarksEntry = bookmarksFixture.createBookmarksEntry();

		String searchTerm = StringUtil.toLowerCase(
			bookmarksEntry.getUserName());

		assertFieldValue(Field.USER_NAME, searchTerm, searchTerm);

		deleteDocument(
			bookmarksEntry.getCompanyId(), uidFactory.getUID(bookmarksEntry));

		assertNoHits(searchTerm);

		reindexAllIndexerModels();

		assertFieldValue(Field.USER_NAME, searchTerm, searchTerm);
	}

	@Test
	public void testReindexEntriesInTheRootFolder() throws Exception {
		long folderId = BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		testReindexEntriesInFolder(folderId);
	}

	@Test
	public void testReindexEntriesInTheSameFolder() throws Exception {
		BookmarksFolder bookmarksFolder =
			bookmarksFixture.createBookmarksFolder();

		testReindexEntriesInFolder(bookmarksFolder.getFolderId());
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertFieldValue(
		String fieldName, String fieldValue, String searchTerm) {

		FieldValuesAssert.assertFieldValue(
			fieldName, fieldValue, search(searchTerm));
	}

	protected void assertNoHits(String searchTerm) {
		FieldValuesAssert.assertFieldValues(
			Collections.emptyMap(), search(searchTerm));
	}

	protected void deleteDocument(long companyId, String uid) throws Exception {
		indexWriterHelper.deleteDocument(
			indexer.getSearchEngineId(), companyId, uid, true);
	}

	protected void reindexAllIndexerModels() throws Exception {
		indexer.reindex(new String[] {String.valueOf(_group.getCompanyId())});
	}

	protected SearchResponse search(String searchTerm) {
		return searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).fields(
				StringPool.STAR
			).groupIds(
				_group.getGroupId()
			).modelIndexerClasses(
				MODEL_INDEXER_CLASS
			).ownerUserId(
				_user.getUserId()
			).queryString(
				searchTerm
			).build());
	}

	protected void searchAndAssertLength(String searchTerm, int expected) {
		SearchResponse searchResponse = search(searchTerm);

		List<Document> documents = searchResponse.getDocuments71();

		DocumentsAssert.assertCount(
			searchResponse.getRequestString(),
			documents.toArray(new Document[0]), Field.USER_NAME, expected);
	}

	protected void testReindexEntriesInFolder(long folderId) throws Exception {
		BookmarksEntry bookmarksEntry = bookmarksFixture.createBookmarksEntry(
			folderId);

		bookmarksFixture.createBookmarksEntry(folderId);

		String searchTerm = _user.getFullName();

		searchAndAssertLength(searchTerm, 2);

		deleteDocument(
			bookmarksEntry.getCompanyId(), uidFactory.getUID(bookmarksEntry));

		reindexAllIndexerModels();

		searchAndAssertLength(searchTerm, 2);
	}

	protected static final Class<?> MODEL_INDEXER_CLASS = BookmarksEntry.class;

	protected BookmarksFixture bookmarksFixture;

	@Inject(
		filter = "indexer.class.name=com.liferay.bookmarks.model.BookmarksEntry"
	)
	protected Indexer<BookmarksEntry> indexer;

	@Inject
	protected IndexWriterHelper indexWriterHelper;

	@Inject
	protected Searcher searcher;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected UIDFactory uidFactory;

	protected UserSearchFixture userSearchFixture;

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