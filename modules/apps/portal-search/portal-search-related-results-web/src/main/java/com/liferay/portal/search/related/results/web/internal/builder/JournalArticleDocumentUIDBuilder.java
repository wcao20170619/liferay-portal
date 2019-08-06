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
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Wade Cao
 */
public class JournalArticleDocumentUIDBuilder
	extends AssetEntryDocumentUIDBuilder {

	public String build() {
		long classPK = 0;
		String className = StringPool.BLANK;
		long groupId = getGroupId();

		if (!Validator.isBlank(_urlTitle) && (groupId > 0)) {
			try {
				JournalArticle journalArticle =
					JournalArticleLocalServiceUtil.fetchArticleByUrlTitle(
						groupId, _urlTitle);

				classPK = journalArticle.getId();

				JournalArticleResource journalArticleResource =
					journalArticle.getArticleResource();

				AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
					groupId, journalArticleResource.getUuid());

				if (assetEntry != null) {
					className = assetEntry.getClassName();
				}
			}
			catch (PortalException pe) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"JournalArticle fetch entry error" + _urlTitle, pe);
				}
			}
		}

		if ((classPK <= 0) || Validator.isBlank(className)) {
			AssetEntry assetEntry = getAssetEntry();

			if (assetEntry != null) {
				className = assetEntry.getClassName();
				AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

				JournalArticle journalArticle =
					(JournalArticle)assetRenderer.getAssetObject();

				if (journalArticle != null) {
					classPK = journalArticle.getId();
				}
			}
		}

		setAssetEntryclassName(className);
		setAssetEntryClassPK(classPK);

		return super.build();
	}

	public JournalArticleDocumentUIDBuilder setAssetEntry(
		AssetEntry assetEntry) {

		super.setAssetEntry(assetEntry);

		return this;
	}

	public JournalArticleDocumentUIDBuilder setGroupId(long groupId) {
		super.setGroupId(groupId);

		return this;
	}

	public JournalArticleDocumentUIDBuilder setUrlTitle(String urlTitle) {
		_urlTitle = urlTitle;

		return this;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleDocumentUIDBuilder.class);

	private String _urlTitle;

}