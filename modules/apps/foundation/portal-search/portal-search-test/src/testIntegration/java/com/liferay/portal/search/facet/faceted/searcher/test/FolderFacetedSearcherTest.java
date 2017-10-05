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

package com.liferay.portal.search.facet.faceted.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.facet.folder.FolderFacetFactory;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.documentlibrary.util.test.DLAppTestUtil;

import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
@Sync
public class FolderFacetedSearcherTest extends BaseFacetedSearcherTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		deleteAfterTestRun(_folders);
	}

	@Test
	public void testAggregation() throws Exception {
		String keyword = RandomTestUtil.randomString();

		addDLModel(keyword);

		SearchContext searchContext = getSearchContext(keyword);

		Facet facet = folderFacetFactory.newInstance(searchContext);

		facet.setFieldName(Field.ENTRY_CLASS_NAME);

		searchContext.addFacet(facet);

		Hits hits = search(searchContext);

		//should get total 3 hits
		Assert.assertEquals(hits.toString(), 3, hits.getLength());

		assertEntryClassNames(_entryClassNames, hits, keyword, facet);
		//frequency should be 2 for treePath. DLFolder and DLFileEntry
		assertFrequencies(
			facet.getFieldName(), searchContext, toMap(_entryClassNames, 1));
	}

	@Test
	public void testFolderIdSelection() throws Exception {
		String keyword = RandomTestUtil.randomString();

		addDLModel(keyword);

		SearchContext searchContext = getSearchContext(keyword);

		Facet facet = folderFacetFactory.newInstance(searchContext);

		facet.setFieldName(Field.FOLDER_ID);

		facet.select(getDLFolderId(_folders));

		searchContext.addFacet(facet);

		Hits hits = search(searchContext);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());

		assertEntryClassNames(
			Arrays.asList(DLFileEntry.class.getName()), hits, keyword, facet);

		long[] dlFolderId = ArrayUtil.append(getDLFolderId(_folders), 0);

		List<String> dlFolderIdList = Arrays.asList(
			ArrayUtil.toStringArray(dlFolderId));

		assertFrequencies(
			facet.getFieldName(), searchContext, toMap(dlFolderIdList, 1));
	}

	@Test
	public void testTreePathSelection() throws Exception {
		String keyword = RandomTestUtil.randomString();

		addDLModel(keyword);

		SearchContext searchContext = getSearchContext(keyword);

		Facet facet = folderFacetFactory.newInstance(searchContext);

		facet.setFieldName(Field.TREE_PATH);

		facet.select(getDLFolderId(_folders));

		searchContext.addFacet(facet);

		Hits hits = search(searchContext);

		Assert.assertEquals(hits.toString(), 2, hits.getLength());

		List<String> entryClassNames = Arrays.asList(
			DLFolder.class.getName(), DLFileEntry.class.getName());

		assertEntryClassNames(entryClassNames, hits, keyword, facet);

		long[] dlFolderId = getDLFolderId(_folders);

		List<String> dlFolderIdList = Arrays.asList(
			ArrayUtil.append(ArrayUtil.toStringArray(dlFolderId), ""));

		assertFrequencies(
			facet.getFieldName(), searchContext, toMap(dlFolderIdList, 2));
	}

	protected DLFolder addDLFolderAndDLFileEntry(
			Group group, User user, String keywords,
			ServiceContext serviceContext)
		throws Exception {

		DLFolder folder = DLFolderLocalServiceUtil.addFolder(
			user.getUserId(), group.getGroupId(), group.getGroupId(), false,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, keywords, keywords,
			false, serviceContext);

		DLFileEntryLocalServiceUtil.addFileEntry(
			user.getUserId(), group.getGroupId(), group.getGroupId(),
			folder.getFolderId(), keywords, null, RandomTestUtil.randomString(),
			keywords, StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new ByteArrayInputStream(_CONTENT.getBytes()), 0,
			serviceContext);

		return folder;
	}

	protected void addDLModel(String keyword)
		throws Exception, PortalException {

		Group group = userSearchFixture.addGroup();

		User user = addUser(group, keyword);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		DLFolder folder = addDLFolderAndDLFileEntry(
			group, user, keyword, serviceContext);

		DLAppTestUtil.addFileEntryWithWorkflow(
			user.getUserId(), group.getGroupId(), folder.getFolderId(),
			StringPool.BLANK, keyword, true, serviceContext);

		_folders.add(folder);

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(false);

		PermissionThreadLocal.setPermissionChecker(
			permissionCheckerFactory.create(user));
	}

	protected void assertEntryClassNames(
		List<String> entryclassnames, Hits hits, String keyword, Facet facet) {

		DocumentsAssert.assertValuesIgnoreRelevance(
			keyword, hits.getDocs(), Field.ENTRY_CLASS_NAME, entryclassnames);
	}

	protected void deleteAfterTestRun(List<DLFolder> folders) throws Exception {
		for (DLFolder folder : folders) {
			DLFolderLocalServiceUtil.deleteAllByRepository(
				folder.getRepositoryId());
		}
	}

	protected long[] getDLFolderId(List<DLFolder> folders) {
		long[] ret = null;

		if (folders != null) {
			ret = new long[folders.size()];
			int i = 0;

			for (DLFolder folder : folders) {
				ret[i++] = folder.getFolderId();
			}
		}

		return ret;
	}

	protected Map<String, Integer> toMap(
		Collection<String> strings, int value) {

		return strings.stream().collect(Collectors.toMap(s -> s, s -> value));
	}

	@Inject
	protected FolderFacetFactory folderFacetFactory;

	@Inject
	protected PermissionCheckerFactory permissionCheckerFactory;

	private static final String _CONTENT =
		"Content: Enterprise. Open Source. For Life.";

	private static final List<String> _entryClassNames = Arrays.asList(
		DLFolder.class.getName(), DLFileEntry.class.getName(),
		User.class.getName());

	private final List<DLFolder> _folders = new ArrayList<>();

}