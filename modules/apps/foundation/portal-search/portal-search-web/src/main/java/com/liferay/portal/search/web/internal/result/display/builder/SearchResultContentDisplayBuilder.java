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

package com.liferay.portal.search.web.internal.result.display.builder;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultContentDisplayContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Wade Cao
 */
public class SearchResultContentDisplayBuilder {

	public SearchResultContentDisplayContext build() throws Exception {
		try {
			return buildResultContext();
		}
		catch (Exception e) {
			_log.error(e, e);

			return buildTemporarilyUnavailable();
		}
	}

	public void setAssetEntryId(long assetEntryId) {
		_assetEntryId = assetEntryId;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public void setPermissionChecker(PermissionChecker permissionChecker) {
		_permissionChecker = permissionChecker;
	}

	public void setRenderRequest(RenderRequest renderRequest) {
		_renderRequest = renderRequest;
	}

	public void setRenderResponse(RenderResponse renderResponse) {
		_renderResponse = renderResponse;
	}

	public void setType(String type) {
		_type = type;
	}

	protected SearchResultContentDisplayContext buildResultContext()
		throws Exception {

		SearchResultContentDisplayContext searchResultContentDisplayContext =
			new SearchResultContentDisplayContext();

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByType(
				_type);

		if (assetRendererFactory != null) {
			searchResultContentDisplayContext.setAssetRendererFactory(
				assetRendererFactory);

			AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
				_assetEntryId);

			searchResultContentDisplayContext.setAssetEntry(assetEntry);

			AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

			searchResultContentDisplayContext.setAssetRenderer(assetRenderer);

			boolean hasViewPermission = assetRenderer.hasViewPermission(
				_permissionChecker);

			searchResultContentDisplayContext.setVisible(
				(assetEntry != null) && assetEntry.isVisible() &&
				hasViewPermission);

			String title = assetRenderer.getTitle(_locale);

			searchResultContentDisplayContext.setTitle(title);

			boolean hasEditPermission = assetRenderer.hasEditPermission(
				_permissionChecker);

			searchResultContentDisplayContext.setHasEditPermission(
				hasEditPermission);

			if (hasEditPermission) {
				PortletURL redirectURL = _renderResponse.createRenderURL();

				redirectURL.setParameter(
					"mvcPath", "/edit_content_redirect.jsp");

				LiferayPortletRequest liferayPortletRequest =
					PortalUtil.getLiferayPortletRequest(_renderRequest);

				LiferayPortletResponse liferayPortletResponse =
					PortalUtil.getLiferayPortletResponse(_renderResponse);

				PortletURL editPortletURL = assetRenderer.getURLEdit(
					liferayPortletRequest, liferayPortletResponse,
					LiferayWindowState.POP_UP, redirectURL);

				searchResultContentDisplayContext.setEditPortletURL(
					editPortletURL.toString());

				Map<String, Object> data = new HashMap<>();

				data.put("destroyOnHide", true);

				searchResultContentDisplayContext.setData(data);
			}
		}

		return searchResultContentDisplayContext;
	}

	protected SearchResultContentDisplayContext buildTemporarilyUnavailable() {
		SearchResultContentDisplayContext searchResultContentDisplayContext =
			new SearchResultContentDisplayContext();

		searchResultContentDisplayContext.setTemporarilyUnavailable(true);

		return searchResultContentDisplayContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SearchResultContentDisplayBuilder.class);

	private long _assetEntryId;
	private Locale _locale;
	private PermissionChecker _permissionChecker;
	private RenderRequest _renderRequest;
	private RenderResponse _renderResponse;
	private String _type;

}