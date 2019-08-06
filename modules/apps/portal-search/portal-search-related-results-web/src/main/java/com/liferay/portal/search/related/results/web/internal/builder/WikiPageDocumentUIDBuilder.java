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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;
import com.liferay.wiki.service.WikiPageServiceUtil;

/**
 * @author Wade Cao
 */
public class WikiPageDocumentUIDBuilder extends AssetEntryDocumentUIDBuilder {

	public String build() {
		long classPK = 0;

		String className = StringPool.BLANK;
		long groupId = getGroupId();

		if (!Validator.isBlank(_name) && (groupId > 0)) {
			try {
				WikiNode wikiNode = WikiNodeLocalServiceUtil.fetchNode(
					groupId, _name);

				if (wikiNode != null) {
					WikiPage wikiPage = WikiPageServiceUtil.getPage(
						groupId, wikiNode.getNodeId(), "FrontPage");

					if (wikiPage != null) {
						classPK = wikiPage.getResourcePrimKey();

						AssetEntry assetEntry =
							AssetEntryLocalServiceUtil.fetchEntry(
								groupId, wikiPage.getUuid());

						if (assetEntry != null) {
							className = assetEntry.getClassName();
						}
					}
				}
			}
			catch (PortalException pe) {
				if (_log.isWarnEnabled()) {
					_log.warn("WikiPage fetch entry error" + _name, pe);
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

	public WikiPageDocumentUIDBuilder setAssetEntry(AssetEntry assetEntry) {
		super.setAssetEntry(assetEntry);

		return this;
	}

	public WikiPageDocumentUIDBuilder setGroupId(long groupId) {
		super.setGroupId(groupId);

		return this;
	}

	public WikiPageDocumentUIDBuilder setName(String name) {
		_name = name;

		return this;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiPageDocumentUIDBuilder.class);

	private String _name;

}