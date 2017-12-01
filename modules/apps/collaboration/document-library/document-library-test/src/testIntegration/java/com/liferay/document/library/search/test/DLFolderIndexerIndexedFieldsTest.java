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
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import com.liferay.portlet.documentlibrary.util.DLFolderIndexer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
public class DLFolderIndexerIndexedFieldsTest extends BaseDLIndexerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_group = GroupTestUtil.addGroup();

		_indexer = new DLFolderIndexer();
	}

	@Test
	public void testIndexedFields() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		BaseModel<?> parentBaseModel = getParentBaseModel(
			_group, serviceContext);

		String keyword = RandomTestUtil.randomString();

		BaseModel<?> baseModel = addBaseModel(
			parentBaseModel, true, keyword, serviceContext);

		Document document = search("folder", LocaleUtil.US, 1);

		Map<String, String> mapStrings = _getFieldsString(keyword, baseModel);

		serUID(
			mapStrings, ((DLFolder)baseModel).getModelClassName(),
			((DLFolder)baseModel).getFolderId());

		FieldValuesAssert.assertFieldValues(mapStrings, document, "folder");
	}

	protected BaseModel<?> addBaseModel(
			BaseModel<?> parentBaseModel, boolean approved, String keywords,
			ServiceContext serviceContext)
		throws Exception {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(true);

			BaseModel<?> baseModel = addBaseModelWithWorkflow(
				parentBaseModel, approved, keywords, serviceContext);

			return baseModel;
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	protected BaseModel<?> addBaseModelWithWorkflow(
			BaseModel<?> parentBaseModel, boolean approved, String keywords,
			ServiceContext serviceContext)
		throws Exception {

		DLFolder parentDLFolder = (DLFolder)parentBaseModel;

		long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		if (parentDLFolder != null) {
			folderId = parentDLFolder.getFolderId();
		}

		Folder folder = DLAppServiceUtil.addFolder(
			serviceContext.getScopeGroupId(), folderId, keywords,
			RandomTestUtil.randomString(), serviceContext);

		return (DLFolder)folder.getModel();
	}

	@Override
	protected Group getGroup() {
		return _group;
	}

	@Override
	protected Indexer<?> getIndexer() {
		return _indexer;
	}

	protected BaseModel<?> getParentBaseModel(
			Group group, ServiceContext serviceContext)
		throws Exception {

		Folder folder = DLAppServiceUtil.addFolder(
			serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(_FOLDER_NAME_MAX_LENGTH),
			RandomTestUtil.randomString(), serviceContext);

		return (DLFolder)folder.getModel();
	}

	@Override
	protected SearchContext getSearchContext(String searchTerm, Locale locale)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			getGroup().getGroupId());

		return searchContext;
	}

	private Map<String, String> _getFieldsString(
		String keyword, BaseModel<?> baseModel) {

		DLFolder dlFolder = (DLFolder)baseModel;

		@SuppressWarnings("serial")
		Map<String, String> mapStrings = new HashMap<String, String>() {
			{
				put(
					Field.ENTRY_CLASS_PK,
					String.valueOf(dlFolder.getFolderId()));
				put(Field.COMPANY_ID, String.valueOf(dlFolder.getCompanyId()));
				put(Field.ENTRY_CLASS_NAME, dlFolder.getModelClassName());
				put(Field.DESCRIPTION, dlFolder.getDescription());
				put(Field.TITLE, keyword);
			}

		};

		return mapStrings;
	}

	private static final int _FOLDER_NAME_MAX_LENGTH = 100;

	@DeleteAfterTestRun
	private Group _group;

	private Indexer<?> _indexer;

}