/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.blueprints.query.index.web.internal.display.context;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.search.tuning.blueprints.query.index.web.internal.constants.QueryStringStatus;
import com.liferay.portal.search.tuning.blueprints.query.index.web.internal.index.QueryString;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Petteri Karttunen
 */
public class ViewBlacklistedEntriesManagementToolbarDisplayContext
	extends ViewEntriesManagementToolbarDisplayContext {

	public ViewBlacklistedEntriesManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<QueryString> searchContainer, String displayStyle) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer, displayStyle, QueryStringStatus.BLACKLISTED);
	}

	/*
	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName",
					QueryIndexMVCCommandNames.EDIT_QUERY_STRING, "redirect",
					currentURLObj.toString());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest,
						"add-blacklisted-query-string"));
			}
		).build();
	}

	*/
}