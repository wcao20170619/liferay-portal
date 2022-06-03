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

package com.liferay.client.extension.type.internal;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.type.CETThemeFavicon;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

import java.util.Properties;

import javax.portlet.PortletRequest;

/**
 * @author Iván Zaera Avellón
 */
public class CETThemeFaviconImpl
	extends BaseCETImpl implements CETThemeFavicon {

	public CETThemeFaviconImpl(ClientExtensionEntry clientExtensionEntry) {
		super(clientExtensionEntry);
	}

	public CETThemeFaviconImpl(PortletRequest portletRequest) {

		// TODO Remove themeFavicon* prefix

		this(
			UnicodePropertiesBuilder.create(
				false
			).put(
				"url", ParamUtil.getString(portletRequest, "themeFaviconURL")
			).buildString());
	}

	public CETThemeFaviconImpl(String typeSettings) {
		super(typeSettings);
	}

	public CETThemeFaviconImpl(
		String baseURL, long companyId, String description,
		String externalReferenceCode, String name, Properties properties,
		String sourceCodeURL, String typeSettings) {

		super(
			baseURL, companyId, description, externalReferenceCode, name,
			properties, sourceCodeURL, typeSettings);
	}

	@Override
	public String getType() {
		return ClientExtensionEntryConstants.TYPE_THEME_FAVICON;
	}

	@Override
	public String getURL() {
		return getString("url");
	}

}