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

package com.liferay.portal.search.web.internal.morelikethis;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

/**
 * @author Wade Cao
 */
public class DocumentUIDBuilder {

	public String build() {
		if (Validator.isBlank(_currentURL)) {
			return StringPool.BLANK;
		}

		String currentURL = HttpUtil.decodeURL(_currentURL);

		while (currentURL.indexOf(CharPool.QUESTION) > -1) {
			currentURL = HttpUtil.getQueryString(currentURL);
		}

		if (Validator.isBlank(currentURL)) {
			
			return StringPool.BLANK;
		}

		Map<String, String[]> parameters = HttpUtil.parameterMapFromString(
			currentURL);

		String uid = _getParameterValue(Field.UID, parameters);

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

			if (!Validator.isBlank(entryClassName) && (entryClassPK != 0)) {
				uid = Field.getUID(
					entryClassName, String.valueOf(entryClassPK));
			}
		}

		return uid;
	}

	public DocumentUIDBuilder currentURL(String currentURL) {
		_currentURL = currentURL;

		return this;
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
		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchAssetEntry(
			assetEntryId);

		if (assetEntry == null) {
			return null;
		}

		String className = assetEntry.getClassName();
		long classPK = assetEntry.getClassPK();

		if (Validator.isBlank(className) || (classPK == 0)) {
			return null;
		}

		return Field.getUID(className, String.valueOf(classPK));
	}

	private String _currentURL;

}