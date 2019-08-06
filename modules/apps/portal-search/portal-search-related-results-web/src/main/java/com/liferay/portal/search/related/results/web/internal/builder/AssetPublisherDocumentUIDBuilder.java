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
import com.liferay.petra.string.StringPool;

/**
 * @author Wade Cao
 */
public class AssetPublisherDocumentUIDBuilder
	extends AssetEntryDocumentUIDBuilder {

	public String build() {
		long classPK = 0;
		String className = StringPool.BLANK;
		long groupId = getGroupId();

		if ((_id > 0) && (groupId > 0)) {
			AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchAssetEntry(
				_id);

			if (assetEntry == null) {
				return null;
			}

			className = assetEntry.getClassName();

			if (_JOURNALARTICLE_NAME.equals(className)) {
				AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

				JournalArticle journalArticle =
					(JournalArticle)assetRenderer.getAssetObject();

				if (journalArticle != null) {
					classPK = journalArticle.getId();
				}
			}
			else {
				classPK = assetEntry.getClassPK();
			}
		}

		setAssetEntryclassName(className);
		setAssetEntryClassPK(classPK);

		return super.build();
	}

	public AssetPublisherDocumentUIDBuilder setGroupId(long groupId) {
		super.setGroupId(groupId);

		return this;
	}

	public AssetPublisherDocumentUIDBuilder setId(long id) {
		_id = id;

		return this;
	}

	private static final String _JOURNALARTICLE_NAME =
		JournalArticle.class.getName();

	private long _id;

}