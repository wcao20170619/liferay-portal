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
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Wade Cao
 */
public class DLFileEntryDocumentUIDBuilder
	extends AssetEntryDocumentUIDBuilder {

	public String build() {
		long classPK = 0;

		String className = StringPool.BLANK;
		long groupId = getGroupId();

		if ((_id > 0) && (groupId > 0)) {
			DLFileEntry dlFileEntry = getDLFileEntryById(_id);

			if (dlFileEntry != null) {
				classPK = dlFileEntry.getFileEntryId();

				AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
					groupId, dlFileEntry.getUuid());

				if (assetEntry != null) {
					className = assetEntry.getClassName();
				}
			}
			else {
				DLFolder dlFolder = getDLFolderById(_id);

				if (dlFolder != null) {
					classPK = dlFolder.getFolderId();

					AssetEntry assetEntry =
						AssetEntryLocalServiceUtil.fetchEntry(
							groupId, dlFolder.getUuid());

					if (assetEntry != null) {
						className = assetEntry.getClassName();
					}
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

	public DLFileEntryDocumentUIDBuilder setAssetEntry(AssetEntry assetEntry) {
		super.setAssetEntry(assetEntry);

		return this;
	}

	public DLFileEntryDocumentUIDBuilder setGroupId(long groupId) {
		super.setGroupId(groupId);

		return this;
	}

	public DLFileEntryDocumentUIDBuilder setId(long id) {
		_id = id;

		return this;
	}

	protected DLFileEntry getDLFileEntryById(long id) {
		DLFileEntry dlFileEntry = null;

		try {
			dlFileEntry = DLFileEntryServiceUtil.getFileEntry(id);
		}
		catch (PortalException pe) {
			if (_log.isWarnEnabled()) {
				_log.warn("DLFileEntry fetch entry error " + id, pe);
			}
		}

		return dlFileEntry;
	}

	protected DLFolder getDLFolderById(long id) {
		DLFolder dlFolder = null;

		try {
			dlFolder = DLFolderServiceUtil.getFolder(id);
		}
		catch (PortalException pe) {
			if (_log.isWarnEnabled()) {
				_log.warn("DLFolder fetch entry error " + id, pe);
			}
		}

		return dlFolder;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryDocumentUIDBuilder.class);

	private long _id;

}