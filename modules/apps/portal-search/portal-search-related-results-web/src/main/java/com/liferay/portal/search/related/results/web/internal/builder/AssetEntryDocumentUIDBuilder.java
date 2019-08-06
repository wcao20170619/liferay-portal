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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Wade Cao
 */
public class AssetEntryDocumentUIDBuilder {

	public String build() {
		if (Validator.isBlank(_assetEntryclassName) ||
			(_assetEntryClassPK == 0)) {

			return StringPool.BLANK;
		}

		return Field.getUID(
			_assetEntryclassName, String.valueOf(_assetEntryClassPK));
	}

	public AssetEntry getAssetEntry() {
		return _assetEntry;
	}

	public long getGroupId() {
		return _groupId;
	}

	public AssetEntryDocumentUIDBuilder setAssetEntry(AssetEntry assetEntry) {
		_assetEntry = assetEntry;

		return this;
	}

	public AssetEntryDocumentUIDBuilder setAssetEntryclassName(
		String assetEntryclassName) {

		_assetEntryclassName = assetEntryclassName;

		return this;
	}

	public AssetEntryDocumentUIDBuilder setAssetEntryClassNamePK() {
		if (_assetEntry != null) {
			_assetEntryclassName = _assetEntry.getClassName();
			_assetEntryClassPK = _assetEntry.getClassPK();
		}

		return this;
	}

	public AssetEntryDocumentUIDBuilder setAssetEntryClassPK(
		long assetEntryClassPK) {

		_assetEntryClassPK = assetEntryClassPK;

		return this;
	}

	public AssetEntryDocumentUIDBuilder setGroupId(long groupId) {
		_groupId = groupId;

		return this;
	}

	private AssetEntry _assetEntry;
	private String _assetEntryclassName;
	private long _assetEntryClassPK;
	private long _groupId;

}