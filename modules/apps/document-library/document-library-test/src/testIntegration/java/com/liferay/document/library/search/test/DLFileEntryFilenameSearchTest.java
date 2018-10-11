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
package com.liferay.document.library.search.test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.search.FileEntryBlueprint;
import com.liferay.document.library.test.util.search.FileEntrySearchFixture;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
@Sync
public class DLFileEntryFilenameSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);
	
	@Before
	public void setUp() throws Exception {
		WorkflowThreadLocal.setEnabled(false);

		_fileEntrySearchFixture = new FileEntrySearchFixture(dlAppLocalService);

		_fileEntrySearchFixture.setUp();

		_userSearchFixture = new UserSearchFixture();

		_userSearchFixture.setUp();

		_assetTags = _userSearchFixture.getAssetTags();
		_groups = _userSearchFixture.getGroups();
		_users = _userSearchFixture.getUsers();
	}
	
	@After
	public void tearDown() throws Exception {
		_userSearchFixture.tearDown();
		_fileEntrySearchFixture.tearDown();
	}
	
	@Test
	public void testFilenameWithoutExtension() throws Exception {
		Locale locale = Locale.US;
		String fileName1 = "MyDocument1.jpg";
		String fileName2 = "MyDocument1.png";
		String keyword = "MyDocument1";
		String[] fileNames = {fileName1, fileName2};

		Group group = _userSearchFixture.addGroup(
			new GroupBlueprint() {
				{
					defaultLocale = locale;
				}
			});

		_fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					fileName = fileName1;
					groupId = group.getGroupId();
					title = fileName1;
					userId = getAdminUserId(group);
				}
			});

		_fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					fileName = fileName2;
					groupId = group.getGroupId();
					title = fileName2;
					userId = getAdminUserId(group);
				}
			});
		
		assertDLFileEntryIndexer(keyword, fileNames, locale);
		
	}
	
	@Test
	public void testFilenameWithExtension() throws Exception {
		Locale locale = Locale.US;
		String fileName1 = "Document_1.jpg";
		String fileName2 = "Document_1.png";
		String fileName3 = "Document_2.jpeg";
		String fileName4 = "Document_3.png";
		String keyword = "Document_1.jpg";
		
		String[] fileNames = {fileName1, fileName2};

		Group group = _userSearchFixture.addGroup(
			new GroupBlueprint() {
				{
					defaultLocale = locale;
				}
			});

		_fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					fileName = fileName1;
					groupId = group.getGroupId();
					title = fileName1;
					userId = getAdminUserId(group);
				}
			});

		_fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					fileName = fileName2;
					groupId = group.getGroupId();
					title = fileName2;
					userId = getAdminUserId(group);
				}
			});
		
		_fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					fileName = fileName3;
					groupId = group.getGroupId();
					title = fileName3;
					userId = getAdminUserId(group);
				}
			});

		_fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					fileName = fileName4;
					groupId = group.getGroupId();
					title = fileName4;
				userId = getAdminUserId(group);
				}
			});
		
		assertDLFileEntryIndexer(keyword, fileNames, locale);
		
	}
	
	protected void assertDLFileEntryIndexer(String keyword, String[] fileNames, Locale locale)
			throws Exception {

		Indexer<DLFileEntry> indexer = indexerRegistry.getIndexer(
				DLFileEntry.class);

		SearchContext searchContext = getSearchContext(keyword, locale);

		Hits hits = indexer.search(searchContext);

		assertHits(fileNames, hits, searchContext);
	}
	
	protected void assertHits(
			String[] fileNames, Hits hits, SearchContext searchContext) {

		DocumentsAssert.assertValuesIgnoreRelevance(
				(String)searchContext.getAttribute("queryString"), hits.getDocs(),
				Field.TITLE, Arrays.asList(fileNames));
	}
	
	protected SearchContext getSearchContext(String keyword, Locale locale)
			throws Exception {

		SearchContext searchContext = _userSearchFixture.getSearchContext(
				keyword);

		searchContext.setLocale(locale);

		return searchContext;
	}
	
	protected long getAdminUserId(Group group) {
		try {
			User user = UserTestUtil.getAdminUser(group.getCompanyId());

			return user.getUserId();
		}
		catch (PortalException pe) {
			throw new RuntimeException(pe);
		}
	}

	
	@Inject
	protected static DLAppLocalService dlAppLocalService;
	
	@Inject
	protected static IndexerRegistry indexerRegistry;

	@DeleteAfterTestRun
	private List<AssetTag> _assetTags;

	private FileEntrySearchFixture _fileEntrySearchFixture;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

	private UserSearchFixture _userSearchFixture;
}
