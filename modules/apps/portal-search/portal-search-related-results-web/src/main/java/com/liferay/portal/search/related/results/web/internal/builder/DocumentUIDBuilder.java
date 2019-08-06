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

package com.liferay.portal.search.related.results.web.internal.builder;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Wade Cao
 */
public class DocumentUIDBuilder {

	public String build() {
		if (Validator.isBlank(_currentURL)) {
			return StringPool.BLANK;
		}

		String currentURL = HttpUtil.decodeURL(_currentURL);

		String urlQueryString = currentURL;

		while (urlQueryString.indexOf(CharPool.QUESTION) > -1) {
			urlQueryString = HttpUtil.getQueryString(urlQueryString);
		}

		if (Validator.isBlank(urlQueryString)) {
			return StringPool.BLANK;
		}

		Map<String, String[]> parameters = HttpUtil.parameterMapFromString(
			urlQueryString);

		String uid = _getParameterValue(Field.UID, parameters);

		if (!Validator.isBlank(uid)) {
			return uid;
		}

		uid = getUIDFromURL(currentURL);

		if (!Validator.isBlank(uid)) {
			return uid;
		}

		long assetEntryId = GetterUtil.getLong(
			_getParameterValue("assetEntryId", parameters));

		if (assetEntryId != 0) {
			uid = _getUIDFromAssetEntryId(assetEntryId);
		}

		if (Validator.isBlank(uid)) {
			String entryClassName = _getParameterValue(
				Field.ENTRY_CLASS_NAME, parameters);
			long entryClassPK = GetterUtil.getLong(
				_getParameterValue(Field.ENTRY_CLASS_PK, parameters));

			uid = new AssetEntryDocumentUIDBuilder().setAssetEntryclassName(
				entryClassName
			).setAssetEntryClassPK(
				entryClassPK
			).build();
		}

		return uid;
	}

	public DocumentUIDBuilder currentURL(String currentURL) {
		_currentURL = currentURL;

		return this;
	}

	public DocumentUIDBuilder groupId(long groupId) {
		_groupId = groupId;

		return this;
	}

	protected String[] getModelURLParameters(String currentURL) {
		String url = HttpUtil.getPath(currentURL);

		String[] path = StringUtil.split(url, CharPool.QUESTION);

		if (path.length == 0) {
			return null;
		}

		String[] subpath = _pattern.split(path[0]);

		if (subpath.length < 2) {
			return null;
		}

		String[] modelURLParameters = StringUtil.split(
			subpath[subpath.length - 1], CharPool.FORWARD_SLASH);

		if (modelURLParameters.length < 2) {
			return null;
		}

		return modelURLParameters;
	}

	protected String getUIDFromURL(String currentURL) {
		String uid = StringPool.BLANK;

		String[] modelURLParameters = getModelURLParameters(currentURL);

		if (modelURLParameters == null) {
			return uid;
		}

		if (_BLOGS.equals(modelURLParameters[0])) {
			uid = new BlogsEntryDocumentUIDBuilder().setGroupId(
				_groupId
			).setUrlTitle(
				modelURLParameters[1]
			).build();
		}
		else if (_JOURNAL.equals(modelURLParameters[0])) {
			uid = new JournalArticleDocumentUIDBuilder().setGroupId(
				_groupId
			).setUrlTitle(
				modelURLParameters[1]
			).build();
		}
		else if (_WIKI.equals(modelURLParameters[0]) &&
				 (modelURLParameters.length > 1)) {

			uid = new WikiPageDocumentUIDBuilder().setGroupId(
				_groupId
			).setName(
				modelURLParameters[1]
			).build();
		}
		else if (_MESSAGE_BOARDS.equals(modelURLParameters[0]) &&
				 (modelURLParameters.length > 2) &&
				 Validator.isNumber(modelURLParameters[2])) {

			uid = new MBMessageDocumentUIDBuilder().setGroupId(
				_groupId
			).setId(
				Long.valueOf(modelURLParameters[2])
			).setMBType(
				modelURLParameters[1]
			).build();
		}
		else if (_DOCUMENT_LIBRARY.equals(modelURLParameters[0]) &&
				 (modelURLParameters.length > 3) &&
				 Validator.isNumber(modelURLParameters[3])) {

			uid = new DLFileEntryDocumentUIDBuilder().setGroupId(
				_groupId
			).setId(
				Long.valueOf(modelURLParameters[3])
			).build();
		}

		return uid;
	}

	private String _getParameterValue(
		String parameterName, Map<String, String[]> parameters) {

		String[] parameterValues = parameters.get(parameterName);

		if ((parameterValues != null) && (parameterValues.length > 0)) {
			return parameterValues[0];
		}

		String[] namespaces = parameters.get("p_p_id");

		if ((namespaces != null) && (namespaces.length > 0)) {
			parameterValues = parameters.get(
				StringBundler.concat("_", namespaces[0], "_", parameterName));

			if ((parameterValues != null) && (parameterValues.length > 0)) {
				return parameterValues[0];
			}
		}

		return null;
	}

	private String _getUIDFromAssetEntryId(long assetEntryId) {
		String uid = StringPool.BLANK;

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchAssetEntry(
			assetEntryId);

		if (assetEntry == null) {
			return uid;
		}

		String className = assetEntry.getClassName();

		if (_JOURNALARTICLE_NAME.equals(className)) {
			uid = new JournalArticleDocumentUIDBuilder().setAssetEntry(
				assetEntry
			).build();
		}
		else {
			uid = new AssetEntryDocumentUIDBuilder().setAssetEntryclassName(
				assetEntry.getClassName()
			).setAssetEntryClassPK(
				assetEntry.getClassPK()
			).build();
		}

		return uid;
	}

	private static final String _BLOGS = "blogs";

	private static final String _DOCUMENT_LIBRARY = "document_library";

	private static final String _JOURNAL = "journal";

	private static final String _JOURNALARTICLE_NAME =
		"com.liferay.journal.model.JournalArticle";

	private static final String _MESSAGE_BOARDS = "message_boards";

	private static final String _WIKI = "wiki";

	private static final Pattern _pattern = Pattern.compile("\\/-\\/");

	private String _currentURL;
	private long _groupId;

}