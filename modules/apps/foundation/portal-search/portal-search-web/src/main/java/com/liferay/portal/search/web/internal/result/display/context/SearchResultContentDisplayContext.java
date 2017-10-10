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

package com.liferay.portal.search.web.internal.result.display.context;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.language.LanguageUtil;

import java.io.Serializable;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Wade Cao
 */
public class SearchResultContentDisplayContext implements Serializable {

	public AssetEntry getAssetEntry() {
		return _assetEntry;
	}

	public AssetRenderer<?> getAssetRenderer() {
		return _assetRenderer;
	}

	public AssetRendererFactory<?> getAssetRendererFactory() {
		return _assetRendererFactory;
	}

	public Map<String, Object> getData() {
		return _data;
	}

	public String getEditPortletURL() {
		return _editPortletURL;
	}

	public String getMessage() {
		return _message;
	}

	public String getTitle() {
		return _title;
	}

	public boolean hasEditPermission() {
		return _hasEditPermission;
	}

	public boolean isTemporarilyUnavailable() {
		return _temporarilyUnavailable;
	}

	public boolean isVisible() {
		return _visible;
	}

	public void setAssetEntry(AssetEntry assetEntry) {
		_assetEntry = assetEntry;
	}

	public void setAssetRenderer(AssetRenderer<?> assetRenderer) {
		_assetRenderer = assetRenderer;
	}

	public void setAssetRendererFactory(
		AssetRendererFactory<?> assetRendererFactory) {

		_assetRendererFactory = assetRendererFactory;
	}

	public void setData(Map<String, Object> data) {
		_data = data;
	}

	public void setDataId(String displayNamespace) {
		if (_data != null) {
			_data.put("id", displayNamespace + "editAsset");
		}
	}

	public void setDataTitle(HttpServletRequest request, String titleEscaped) {
		if (_data != null) {
			_data.put(
				"title",
				LanguageUtil.format(request, "edit-x", titleEscaped, false));
		}
	}

	public void setEditPortletURL(String editPortletURL) {
		_editPortletURL = editPortletURL;
	}

	public void setHasEditPermission(boolean hasEditPermission) {
		_hasEditPermission = hasEditPermission;
	}

	public void setMessage(HttpServletRequest request, String titleEscaped) {
		_message = LanguageUtil.format(
			request, "edit-x-x", new Object[] {"hide-accessible", titleEscaped},
			false);
	}

	public void setTemporarilyUnavailable(boolean temporarilyUnavailable) {
		_temporarilyUnavailable = temporarilyUnavailable;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setVisible(boolean visible) {
		_visible = visible;
	}

	private static final long serialVersionUID = 1L;

	private AssetEntry _assetEntry;
	private AssetRenderer<?> _assetRenderer;
	private AssetRendererFactory<?> _assetRendererFactory;
	private Map<String, Object> _data;
	private String _editPortletURL;
	private boolean _hasEditPermission;
	private String _message;
	private boolean _temporarilyUnavailable;
	private String _title;
	private boolean _visible;

}