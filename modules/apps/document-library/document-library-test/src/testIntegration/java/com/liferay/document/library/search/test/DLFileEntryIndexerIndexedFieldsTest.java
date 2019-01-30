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
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.test.util.search.FileEntryBlueprint;
import com.liferay.document.library.test.util.search.FileEntrySearchFixture;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 * @author Eric Yan
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

	public FileEntry addFileEntry(String fileName) throws IOException {
		Class<?> clazz = getClass();

		try (InputStream inputStream = clazz.getResourceAsStream(
				"dependencies/" + fileName)) {

			return fileEntrySearchFixture.addFileEntry(
				new FileEntryBlueprint() {
					{
						setFileName(fileName);
						setGroupId(dlFixture.getGroupId());
						setInputStream(inputStream);
						setTitle(fileName);
						setUserId(dlFixture.getUserId());
					}
				});
		}
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		fileEntrySearchFixture = new FileEntrySearchFixture(dlAppLocalService);

		fileEntrySearchFixture.setUp();

		setGroup(dlFixture.addGroup());
		setIndexerClass(DLFileEntry.class);
		setUser(dlFixture.addUser());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		fileEntrySearchFixture.tearDown();
	}

	@Test
	public void testIndexedFields() throws Exception {
		dlFixture.updateDisplaySettings(LocaleUtil.JAPAN);

		String fileName_jp = "content_search.txt";

		String searchTerm = "新規";

		FileEntry fileEntry = addFileEntry(fileName_jp);

		Document document = dlSearchFixture.searchOnlyOne(
			searchTerm, LocaleUtil.JAPAN);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> map = new HashMap<>();

		populateExpectedFieldValues(fileEntry, map);

		FieldValuesAssert.assertFieldValues(map, document, searchTerm);
	}

	protected String getContents(FileEntry fileEntry) throws Exception {
		String contents = FileUtil.extractText(
			fileEntry.getContentStream(), fileEntry.getTitle());

		return contents.trim();
	}

	protected long getDDMStructureId(FileEntry fileEntry) throws Exception {
		long ddmStructureId = 0;

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

		List<DLFileEntryMetadata> dlFileEntryMetadatas =
			dlFileEntryMetadataLocalService.getFileVersionFileEntryMetadatas(
				dlFileVersion.getFileVersionId());

		for (DLFileEntryMetadata dlFileEntryMetadata : dlFileEntryMetadatas) {
			if (dlFileEntryMetadata != null) {
				ddmStructureId = dlFileEntryMetadata.getDDMStructureId();
			}
		}

		return ddmStructureId;
	}

	protected void populateDates(FileEntry fileEntry, Map<String, String> map) {
		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, fileEntry.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, fileEntry.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.PUBLISH_DATE, fileEntry.getCreateDate(), map);

		indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	protected void populateExpectedFieldValues(
			FileEntry fileEntry, Map<String, String> map)
		throws Exception {

		map.put(Field.CLASS_NAME_ID, "0");
		map.put(Field.CLASS_PK, "0");
		map.put(Field.COMPANY_ID, String.valueOf(fileEntry.getCompanyId()));
		map.put(Field.ENTRY_CLASS_NAME, DLFileEntry.class.getName());
		map.put(
			Field.ENTRY_CLASS_PK, String.valueOf(fileEntry.getFileEntryId()));
		map.put(Field.FOLDER_ID, String.valueOf(fileEntry.getFolderId()));
		map.put(Field.GROUP_ID, String.valueOf(fileEntry.getGroupId()));
		map.put(
			Field.PROPERTIES, FileUtil.stripExtension(fileEntry.getTitle()));
		map.put(Field.SCOPE_GROUP_ID, String.valueOf(fileEntry.getGroupId()));
		map.put(Field.STAGING_GROUP, "false");
		map.put(Field.STATUS, "0");
		map.put(Field.TITLE, fileEntry.getTitle());
		map.put(Field.TITLE + "_sortable", fileEntry.getTitle());
		map.put(Field.TREE_PATH, "");
		map.put(Field.USER_ID, String.valueOf(fileEntry.getUserId()));
		map.put(
			Field.USER_NAME, StringUtil.toLowerCase(fileEntry.getUserName()));

		map.put("classTypeId", "0");
		map.put("content_ja_JP", getContents(fileEntry));
		map.put(
			"dataRepositoryId", String.valueOf(fileEntry.getRepositoryId()));
		map.put("ddmContent", "text/plain; charset=UTF-8 UTF-8");
		map.put("extension", fileEntry.getExtension());
		map.put("fileEntryTypeId", "0");
		map.put("hidden", "false");
		map.put("mimeType", fileEntry.getMimeType().replaceAll("/", "_"));
		map.put("path", fileEntry.getTitle());
		map.put("readCount", String.valueOf(fileEntry.getReadCount()));
		map.put("size", String.valueOf(fileEntry.getSize()));
		map.put("size_sortable", String.valueOf(fileEntry.getSize()));
		map.put("visible", "true");

		populateDates(fileEntry, map);
		populateHttpHeaders(fileEntry, map);
		populateLocalizedTitles(fileEntry, map);

		indexedFieldsFixture.populatePriority("0.0", map);
		indexedFieldsFixture.populateRoleIdFields(
			fileEntry.getCompanyId(), DLFileEntry.class.getName(),
			fileEntry.getPrimaryKey(), fileEntry.getGroupId(), null, map);
		indexedFieldsFixture.populateUID(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId(), map);
	}

	protected void populateHttpHeader(
		String fieldName, String value, String ddmStructureId,
		String languageId, Map<String, String> map) {

		String contentEncodingFieldName = StringBundler.concat(
			"ddm__text__", ddmStructureId, "__HttpHeaders_", fieldName, "_",
			languageId);

		map.put(contentEncodingFieldName, value);
		map.put(
			contentEncodingFieldName + "_String_sortable",
			StringUtil.toLowerCase(value));
	}

	protected void populateHttpHeaders(
			FileEntry fileEntry, Map<String, String> map)
		throws Exception {

		String ddmStructureId = String.valueOf(getDDMStructureId(fileEntry));

		String languageId = LocaleUtil.toLanguageId(LocaleUtil.getDefault());

		populateHttpHeader(
			"CONTENT_ENCODING", "UTF-8", ddmStructureId, languageId, map);
		populateHttpHeader(
			"CONTENT_TYPE", "text/plain; charset=UTF-8", ddmStructureId,
			languageId, map);
	}

	protected void populateLocalizedTitles(
		FileEntry fileEntry, Map<String, String> map) {

		String title = fileEntry.getTitle();

		map.put("localized_title", title);

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);

			String key = "localized_title_" + languageId;

			map.put(key, title);
			map.put(key.concat("_sortable"), title);
		}
	}

	protected FileEntrySearchFixture fileEntrySearchFixture;

}