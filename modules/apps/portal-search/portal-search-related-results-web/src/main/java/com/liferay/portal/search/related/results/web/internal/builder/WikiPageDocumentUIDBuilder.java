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

		if (!Validator.isBlank(_nodeName) && (groupId > 0)) {
			try {
				WikiNode wikiNode = WikiNodeLocalServiceUtil.fetchNode(
					groupId, _nodeName);

				if (wikiNode != null) {
					WikiPage wikiPage = WikiPageServiceUtil.getPage(
						wikiNode.getNodeId(), _title, 1.0);

					if (wikiPage != null) {
						AssetEntry assetEntry =
							AssetEntryLocalServiceUtil.fetchEntry(
								groupId, wikiPage.getUuid());

						if (assetEntry != null) {
							classPK = assetEntry.getClassPK();
							className = assetEntry.getClassName();
						}
					}
				}
			}
			catch (PortalException pe) {
				if (_log.isWarnEnabled()) {
					_log.warn("WikiPage fetch entry error" + _nodeName, pe);
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

	public WikiPageDocumentUIDBuilder setNodeName(String nodeName) {
		_nodeName = nodeName;

		return this;
	}

	public WikiPageDocumentUIDBuilder setTitle(String title) {
		_title = title;

		return this;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiPageDocumentUIDBuilder.class);

	private String _nodeName;
	private String _title;

}