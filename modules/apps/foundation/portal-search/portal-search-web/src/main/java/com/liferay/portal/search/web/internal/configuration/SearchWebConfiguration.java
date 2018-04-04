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

package com.liferay.portal.search.web.internal.configuration;

import aQute.bnd.annotation.ProviderType;
import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Adam Brandizzi
 * @author André de Oliveira
 */
@ExtendedObjectClassDefinition(category = "search")
@Meta.OCD(
	id = "com.liferay.portal.search.web.internal.configuration.SearchWebConfiguration",
	localization = "content/Language", name = "search-web-configuration-name"
)
@ProviderType
public interface SearchWebConfiguration {

	@Meta.AD(
		deflt = "true",
		description = "classic-search-portlet-in-front-page-help",
		name = "classic-search-portlet-in-front-page", required = false
	)
	public boolean classicSearchPortletInFrontPage();

	@Meta.AD(
		deflt = "true",
		description = "skip-automatic-creation-of-search-page-in-guest-site-help",
		name = "skip-automatic-creation-of-search-page-in-guest-site",
		required = false
	)
	public boolean skipAutomaticCreationOfSearchPageInGuestSite();

	@Meta.AD(
		deflt = "/search", description = "search-page-friendly-url-help",
		name = "search-page-friendly-url", required = false
	)
	public String searchPageFriendlyURL();

}