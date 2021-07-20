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

package com.liferay.search.experiences.predict.keyword.index.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.search.experiences.predict.keyword.index.web.internal.constants.KeywordIndexPortletKeys;
import com.liferay.search.experiences.web.application.list.constants.SearchExperiencesPanelCategoryKeys;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	property = {
		"panel.app.order:Integer=400",
		"panel.category.key=" + SearchExperiencesPanelCategoryKeys.CONTROL_PANEL_SEARCH_EXPERIENCES
	},
	service = PanelApp.class
)
public class KeywordIndexPanelApp extends BasePanelApp {

	@Override
	public String getPortletId() {
		return KeywordIndexPortletKeys.KEYWORD_INDEX_ADMIN;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (Objects.equals(searchEngineInformation.getVendorString(), "Solr")) {
			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Override
	@Reference(
		target = "(javax.portlet.name=" + KeywordIndexPortletKeys.KEYWORD_INDEX_ADMIN + ")",
		unbind = "-"
	)
	public void setPortlet(Portlet portlet) {
		super.setPortlet(portlet);
	}

	@Reference
	protected SearchEngineInformation searchEngineInformation;

}