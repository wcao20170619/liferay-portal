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

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.search.FileEntryBlueprint;
import com.liferay.document.library.test.util.search.FileEntrySearchFixture;
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
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.Arrays;
import java.util.List;

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
public class DLFileEntryFileNameSearchTest {

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
	public void testExtensionAloneMatchesPathAndExtensionFields()
		throws Exception {

		addFileEntriesWithTitleSameAsFileName("One.jpg", "Two.JPG");

		assertSearch("jpg", Arrays.asList("One.jpg", "Two.JPG"));
	}

	@Test
	public void testExtensionDoesNotSplitFromPlainBaseName() throws Exception {
		addFileEntriesWithTitleSameAsFileName(
			"Document_1.jpg", "Document_2.jpg", "Memorandum.jpg");

		assertSearch("Letter.jpg", Arrays.asList());
		assertSearch("Memorandum.jpg", Arrays.asList("Memorandum.jpg"));
	}

	@Test
	public void testExtensionSplitsFromUnusualBaseNameMatchingTitleField()
		throws Exception {

		addFileEntriesWithTitleSameAsFileName(
			"Document_1.png", "Document_2345.png");

		assertSearch(
			"Memorandum_99.png",
			Arrays.asList("Document_1.png", "Document_2345.png"));
	}

	@Test
	public void testLPS73013() throws Exception {
		addFileEntriesWithTitleSameAsFileName(
			"myfile.txt", "MyFile.txt", "MYFILE.txt");

		assertSearch(
			"myfile", Arrays.asList("myfile.txt", "MyFile.txt", "MYFILE.txt"));
		assertSearch(
			"my", Arrays.asList("myfile.txt", "MyFile.txt", "MYFILE.txt"));
	}

	@Test
	public void testLPS82588() throws Exception {
		addFileEntriesWithTitleSameAsFileName(
			"Document_1.jpg", "Document_1.png", "Document_2.jpeg",
			"Document_3.png");

		assertSearch(
			"Document_1", Arrays.asList("Document_1.jpg", "Document_1.png"));
		assertSearch("asd.jpg", Arrays.asList());
		assertSearch(
			"Document_1.jpg",
			Arrays.asList("Document_1.jpg", "Document_1.png"));
	}

	@Test
	public void testUnusualBaseNameSplitsExtensionMatchingTitleFieldAsPhraseExact()
		throws Exception {

		addFileEntriesWithTitleSameAsFileName("Document_1.jpg");

		assertSearch("Document_1.jpg", Arrays.asList("Document_1.jpg"));
	}

	@Test
	public void testUnusualBaseNameSplitsExtensionMatchingTitleFieldAsPhrasePrefix()
		throws Exception {

		addFileEntriesWithTitleSameAsFileName("Document_1.docx");

		assertSearch("Document_1.doc", Arrays.asList("Document_1.docx"));
	}

	@Test
	public void testUnusualBaseNameSplitsExtensionMismatchingTitleField()
		throws Exception {

		addFileEntriesWithTitleSameAsFileName("Document_1234.jpg");

		assertSearch("Document_1.PNG", Arrays.asList());
	}

	protected void addFileEntriesWithTitleSameAsFileName(String... fileNames)
		throws Exception {

		Group group = _userSearchFixture.addGroup();

		for (String fileName1 : fileNames) {
			_fileEntrySearchFixture.addFileEntry(
				new FileEntryBlueprint() {
					{
						fileName = fileName1;
						groupId = group.getGroupId();
						title = fileName1;
						userId = getAdminUserId(group);
					}
				});
		}
	}

	protected void assertSearch(String keyword, List<String> titles)
		throws Exception {

		Indexer<DLFileEntry> indexer = indexerRegistry.getIndexer(
			DLFileEntry.class);

		SearchContext searchContext = _userSearchFixture.getSearchContext(
			keyword);

		Hits hits = indexer.search(searchContext);

		DocumentsAssert.assertValuesIgnoreRelevance(
			(String)searchContext.getAttribute("queryString"), hits.getDocs(),
			Field.TITLE, titles);
	}

	protected long getAdminUserId(Group group) throws Exception {
		User user = UserTestUtil.getAdminUser(group.getCompanyId());

		return user.getUserId();
	}

	protected SearchContext getSearchContext(String keyword) throws Exception {
		return _userSearchFixture.getSearchContext(keyword);
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