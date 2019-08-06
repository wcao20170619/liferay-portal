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
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Wade Cao
 */
public class BlogsEntryDocumentUIDBuilder extends AssetEntryDocumentUIDBuilder {

	public String build() {
		long classPK = 0;
		String className = StringPool.BLANK;

		long groupId = getGroupId();

		if (!Validator.isBlank(_urlTitle) && (groupId > 0)) {
			try {
				BlogsEntry blogsEntry = BlogsEntryServiceUtil.getEntry(
					groupId, _urlTitle);

				if (blogsEntry != null) {
					classPK = blogsEntry.getEntryId();

					AssetEntry assetEntry =
						AssetEntryLocalServiceUtil.fetchEntry(
							groupId, blogsEntry.getUuid());

					if (assetEntry != null) {
						className = assetEntry.getClassName();
					}
				}
			}
			catch (PortalException pe) {
				if (_log.isWarnEnabled()) {
					_log.warn("BlogsEntry fetch entry error" + _urlTitle, pe);
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

	public BlogsEntryDocumentUIDBuilder setAssetEntry(AssetEntry assetEntry) {
		super.setAssetEntry(assetEntry);

		return this;
	}

	public BlogsEntryDocumentUIDBuilder setGroupId(long groupId) {
		super.setGroupId(groupId);

		return this;
	}

	public BlogsEntryDocumentUIDBuilder setUrlTitle(String urlTitle) {
		_urlTitle = urlTitle;

		return this;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BlogsEntryDocumentUIDBuilder.class);

	private String _urlTitle;

}