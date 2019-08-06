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
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBCategoryServiceUtil;
import com.liferay.message.boards.service.MBMessageServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Wade Cao
 */
public class MBMessageDocumentUIDBuilder extends AssetEntryDocumentUIDBuilder {

	public String build() {
		long classPK = 0;

		String className = StringPool.BLANK;
		long groupId = getGroupId();

		if ((_id > 0) && (groupId > 0) && !Validator.isBlank(_mbType)) {
			try {
				if (MESSAGE.equals(_mbType)) {
					MBMessage mbMessage = MBMessageServiceUtil.getMessage(_id);

					if (mbMessage != null) {
						classPK = mbMessage.getMessageId();

						AssetEntry assetEntry =
							AssetEntryLocalServiceUtil.fetchEntry(
								groupId, mbMessage.getUuid());

						if (assetEntry != null) {
							className = assetEntry.getClassName();
						}
					}
				}
				else if (CATEGORY.equals(_mbType)) {
					MBCategory mbCategory = MBCategoryServiceUtil.getCategory(
						_id);

					if (mbCategory != null) {
						className = MBCategory.class.getName();
						classPK = mbCategory.getCategoryId();
					}
				}
			}
			catch (PortalException pe) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"MBMessage/MBCategory fetch entry error " + _id, pe);
				}
			}
		}

		if ((classPK <= 0) || Validator.isBlank(className)) {
			setAssetEntryClassNamePK();
		}
		else {
			setAssetEntryclassName(className);
			setAssetEntryClassPK(classPK);
		}

		return super.build();
	}

	public MBMessageDocumentUIDBuilder setAssetEntry(AssetEntry assetEntry) {
		super.setAssetEntry(assetEntry);

		return this;
	}

	public MBMessageDocumentUIDBuilder setGroupId(long groupId) {
		super.setGroupId(groupId);

		return this;
	}

	public MBMessageDocumentUIDBuilder setId(long id) {
		_id = id;

		return this;
	}

	public MBMessageDocumentUIDBuilder setMBType(String mbType) {
		_mbType = mbType;

		return this;
	}

	protected static final String CATEGORY = "category";

	protected static final String MESSAGE = "message";

	private static final Log _log = LogFactoryUtil.getLog(
		MBMessageDocumentUIDBuilder.class);

	private long _id;
	private String _mbType;

}