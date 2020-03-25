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
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.test.util.HitsAssert;
import com.liferay.portal.search.test.util.SearchContextTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;

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
public class BookmarksFolderIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_setUpUserSearchFixture();

		_setUpBookmarksFolderFixture();
	}

	@Test
	public void testReindex() throws Exception {
		BookmarksFolder bookmarksFolder =
			_bookmarksFixture.createBookmarksFolder();

		String searchTerm = bookmarksFolder.getUserName();

		Document document = _searchOnlyOne(searchTerm);

		_deleteDocument(document);

		_searchNoHits(searchTerm);

		_reindex(bookmarksFolder.getCompanyId());

		_searchOnlyOne(searchTerm);
	}

	private void _deleteDocument(Document document) throws Exception {
		_indexWriterHelper.deleteDocument(
			_indexer.getSearchEngineId(), TestPropsValues.getCompanyId(),
			document.getUID(), true);
	}

	private void _reindex(long companyId) throws Exception {
		_indexer.reindex(new String[] {String.valueOf(companyId)});
	}

	private void _searchNoHits(String keywords) {
		try {
			SearchContext searchContext =
				SearchContextTestUtil.getSearchContext(
					_user.getUserId(), null, keywords, null, null);

			Hits hits = _indexer.search(searchContext);

			HitsAssert.assertNoHits(hits);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
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

	private void _setUpBookmarksFolderFixture() throws Exception {
		_bookmarksFixture = new BookmarksFixture(_group, _user);

		_bookmarksFolders = _bookmarksFixture.getBookmarksFolders();
	}

	@SuppressWarnings("deprecation")
	private void _setUpUserSearchFixture() throws Exception {
		_userSearchFixture = new UserSearchFixture();

		_userSearchFixture.setUp();

		_group = _userSearchFixture.addGroup();

		_groups = _userSearchFixture.getGroups();

		_user = _userSearchFixture.addUser(
			RandomTestUtil.randomString(), _group);

		_users = _userSearchFixture.getUsers();
	}

	private BookmarksFixture _bookmarksFixture;

	@DeleteAfterTestRun
	private List<BookmarksFolder> _bookmarksFolders;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@Inject(
		filter = "indexer.class.name=com.liferay.bookmarks.model.BookmarksFolder"
	)
	private Indexer<BookmarksFolder> _indexer;

	@Inject
	private IndexWriterHelper _indexWriterHelper;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

	private UserSearchFixture _userSearchFixture;

}