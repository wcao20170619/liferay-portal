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
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import com.liferay.portlet.documentlibrary.util.DLFileEntryIndexer;

import java.io.File;
import java.io.InputStream;

import java.text.DateFormat;

import java.util.HashMap;
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
public class DLFileEntryIndexerIndexedFieldsTest extends BaseDLIndexerTestCase {

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

		_indexer = new DLFileEntryIndexer();
	}

	@Test
	public void testIndexedFields() throws Exception {
		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), null, LocaleUtil.JAPAN);

		String fileName_jp = "content_search.txt";

		String searchTerm = "新規";

		FileEntry fileEntry = addFileEntry(fileName_jp);

		Document document = search(searchTerm, LocaleUtil.JAPAN, 0);

		Map<String, String> mapStrings = _getFieldsString(fileEntry);

		setRoleId(mapStrings, document);

		FieldValuesAssert.assertFieldValues(mapStrings, document, searchTerm);
	}

	protected FileEntry addFileEntry(String fileName) throws Exception {
		return addFileEntry(fileName, _group.getGroupId());
	}

	protected FileEntry addFileEntry(String fileName, long groupId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		File file = null;
		FileEntry fileEntry = null;

		try (InputStream inputStream =
				DLFileEntrySearchTest.class.getResourceAsStream(
					"dependencies/" + fileName)) {

			String mimeType = MimeTypesUtil.getContentType(file, fileName);

			file = FileUtil.createTempFile(inputStream);

			fileEntry = DLAppLocalServiceUtil.addFileEntry(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName, mimeType,
				fileName, StringPool.BLANK, StringPool.BLANK, file,
				serviceContext);
		}
		finally {
			FileUtil.delete(file);
		}

		return fileEntry;
	}

	@Override
	protected Group getGroup() {
		return _group;
	}

	@Override
	protected Indexer<?> getIndexer() {
		return _indexer;
	}

	private Map<String, String> _getFieldsString(FileEntry fileEntry)
		throws PortalException {

		DateFormat df = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmmss");

		String title = fileEntry.getTitle();

		String modelClassName = DLFileEntry.class.getName();

		long ddmStructureId = getDDMStructureId(fileEntry);

		@SuppressWarnings("serial")
		Map<String, String> mapStrings = new HashMap<String, String>() {
			{
				String extension = fileEntry.getExtension();

				put(
					Field.ENTRY_CLASS_PK,
					String.valueOf(fileEntry.getFileEntryId()));

				put("extension", extension);

				put("publishDate_sortable", "0");

				put("hidden", "false");

				put(
					"dataRepositoryId",
					String.valueOf(fileEntry.getRepositoryId()));

				put(
					"ddm__text__" + ddmStructureId +
						"__HttpHeaders_CONTENT_TYPE_en_US",
					"text/plain; charset=UTF-8");

				put(Field.GROUP_ID, String.valueOf(_group.getGroupId()));

				put("publishDate", "19700101000000");

				put("mimeType", fileEntry.getMimeType().replaceAll("/", "_"));

				put(Field.CLASS_NAME_ID, "0");

				put(
					"createDate_sortable",
					String.valueOf(fileEntry.getCreateDate().getTime()));

				put("readCount", String.valueOf(fileEntry.getReadCount()));

				put(Field.TITLE, title);

				put("path", title);

				put(Field.CLASS_PK, "0");

				put("localized_title_en_US_sortable", title);

				put(Field.SCOPE_GROUP_ID, String.valueOf(_group.getGroupId()));

				put(Field.TITLE + "_sortable", title);

				put(
					Field.MODIFIED_DATE,
					df.format(fileEntry.getModifiedDate()));

				put(
					"modified_sortable",
					String.valueOf(fileEntry.getModifiedDate().getTime()));

				put(Field.EXPIRATION_DATE + "_sortable", "9223372036854775807");

				put(Field.CREATE_DATE, df.format(fileEntry.getCreateDate()));

				put(Field.EXPIRATION_DATE, "99950812133000");

				put("visible", "true");

				put(Field.ENTRY_CLASS_NAME, modelClassName);

				put(
					"ddm__text__" + ddmStructureId +
						"__HttpHeaders_CONTENT_ENCODING_en_US",
					"UTF-8");

				put(
					"ddm__text__" + ddmStructureId +
						"__HttpHeaders_CONTENT_ENCODING_en_US_String_sortable",
					"utf-8");

				put("fileEntryTypeId", "0");

				put(Field.PRIORITY, "0.0");

				put(
					Field.USER_NAME,
					StringUtil.toLowerCase(fileEntry.getUserName()));

				put(Field.USER_ID, String.valueOf(fileEntry.getUserId()));

				put("classTypeId", "0");

				put(Field.FOLDER_ID, String.valueOf(fileEntry.getFolderId()));

				put("localized_title", title);

				put(Field.STAGING_GROUP, "false");

				put(Field.COMPANY_ID, String.valueOf(fileEntry.getCompanyId()));

				put("size", String.valueOf(fileEntry.getSize()));

				put("localized_title_en_US", title);

				put(
					"ddm__text__" + ddmStructureId +
						"__HttpHeaders_CONTENT_TYPE_en_US_String_sortable",
					"text/plain; charset=utf-8");

				put("ddmContent", "text/plain; charset=UTF-8 UTF-8");

				int extensionIdx = title.indexOf(extension) - 1;

				String props = title.substring(0, extensionIdx);

				put(Field.PROPERTIES, props);

				put(Field.TREE_PATH, "");

				put(Field.STATUS, "0");
			}
		};

		String contents = FileUtil.extractText(
			fileEntry.getContentStream(), title);

		mapStrings.put("content_ja_JP", contents.trim());

		serUID(mapStrings, modelClassName, fileEntry.getFileEntryId());

		setGroupRoleId(mapStrings);

		return mapStrings;
	}

	@DeleteAfterTestRun
	private Group _group;

	private Indexer<?> _indexer;

}