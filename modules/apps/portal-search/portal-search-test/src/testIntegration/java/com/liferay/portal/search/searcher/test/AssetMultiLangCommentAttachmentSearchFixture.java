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

package com.liferay.portal.search.searcher.test;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;

import java.io.File;
import java.io.InputStream;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author Wade Cao
 */
public class AssetMultiLangCommentAttachmentSearchFixture {

	public AssetMultiLangCommentAttachmentSearchFixture(
		AssetCategoryService assetCategoryService,
		AssetVocabularyLocalService assetVocabularyLocalService,
		BlogsEntryLocalService blogsEntryLocalService, Group group,
		MBMessageLocalService mbMessageLocalService, User user,
		WikiNodeLocalService wikiNodeLocalService,
		WikiPageLocalService wikiPageLocalService) {

		_assetCategoryService = assetCategoryService;
		_assetVocabularyLocalService = assetVocabularyLocalService;
		_blogsEntryLocalService = blogsEntryLocalService;
		_group = group;
		_mbMessageLocalService = mbMessageLocalService;
		_user = user;
		_wikiNodeLocalService = wikiNodeLocalService;
		_wikiPageLocalService = wikiPageLocalService;
	}

	public AssetCategory addAssetCategory(Map<Locale, String> keywordsMap)
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addDefaultVocabulary(
				_group.getGroupId());

		return _assetCategoryService.addCategory(
			_group.getGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, keywordsMap,
			null, assetVocabulary.getVocabularyId(), null,
			_getServiceContext());
	}

	public BlogsEntry addBlogsEntry(String content, String title)
		throws Exception {

		return _blogsEntryLocalService.addEntry(
			_user.getUserId(), title, content, new Date(),
			_getServiceContext());
	}

	public void addBlogsEntryImage(String folderName, String title)
		throws Exception {

		_blogsEntryLocalService.addEntry(
			_user.getUserId(), title, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new Date(), true, true, new String[0], StringPool.BLANK, null,
			_getImageSelector(title, folderName), _getServiceContext());
	}

	public MBMessage addMBMessage(String subject) throws Exception {
		return _mbMessageLocalService.addMessage(
			_user.getUserId(), RandomTestUtil.randomString(),
			_group.getGroupId(), MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			0L, MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, subject,
			RandomTestUtil.randomString(), MBMessageConstants.DEFAULT_FORMAT,
			null, false, 0.0, false, _getServiceContext());
	}

	public MBMessage addMessageAttachment(String fileName, String subject)
		throws Exception {

		return _mbMessageLocalService.addMessage(
			_user.getUserId(), _user.getFullName(), _group.getGroupId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, subject, "Body",
			MBMessageConstants.DEFAULT_FORMAT, fileName, _getAttachment(),
			false, 0, false, _getServiceContext());
	}

	public FileEntry addWikiPageAttachment(String fileName, String title)
		throws Exception {

		ServiceContext serviceContext = _getServiceContext();

		WikiNode wikiNode = _wikiNodeLocalService.addDefaultNode(
			_user.getUserId(), serviceContext);

		WikiPage wikiPage = _wikiPageLocalService.addPage(
			_user.getUserId(), wikiNode.getNodeId(), title, "content",
			"Summary", false, serviceContext);

		String mimeType = MimeTypesUtil.getExtensionContentType("docx");

		return _wikiPageLocalService.addPageAttachment(
			wikiPage.getUserId(), wikiPage.getNodeId(), wikiPage.getTitle(),
			fileName, _getAttachment(), mimeType);
	}

	protected void addComment(
			String className, String comment, Long primaryKeyObj)
		throws Exception {

		CommentManagerUtil.addComment(
			_user.getUserId(), _group.getGroupId(), className, primaryKeyObj,
			comment, new IdentityServiceContextFunction(_getServiceContext()));
	}

	private File _getAttachment() throws Exception {
		byte[] fileBytes = FileUtil.getBytes(
			getClass(), "dependencies/OSX_Test.docx");

		File file = null;

		if (ArrayUtil.isNotEmpty(fileBytes)) {
			file = FileUtil.createTempFile(fileBytes);
		}

		return file;
	}

	private ImageSelector _getImageSelector(String fileName, String folderName)
		throws Exception {

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"com/liferay/portal/search/searcher/test/dependencies/test.jpg");

		FileEntry fileEntry = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), _user.getUserId(), folderName, fileName,
			inputStream, MimeTypesUtil.getContentType(fileName));

		return new ImageSelector(
			FileUtil.getBytes(fileEntry.getContentStream()),
			fileEntry.getTitle(), fileEntry.getMimeType(), StringPool.BLANK);
	}

	private ServiceContext _getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());
	}

	private final AssetCategoryService _assetCategoryService;
	private final AssetVocabularyLocalService _assetVocabularyLocalService;
	private final BlogsEntryLocalService _blogsEntryLocalService;
	private final Group _group;
	private final MBMessageLocalService _mbMessageLocalService;
	private final User _user;
	private final WikiNodeLocalService _wikiNodeLocalService;
	private final WikiPageLocalService _wikiPageLocalService;

}