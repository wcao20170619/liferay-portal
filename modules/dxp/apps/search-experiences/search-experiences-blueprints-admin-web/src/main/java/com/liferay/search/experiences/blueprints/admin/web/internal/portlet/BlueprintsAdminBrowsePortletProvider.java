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

package com.liferay.search.experiences.blueprints.admin.web.internal.portlet;

import com.liferay.petra.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.BasePortletProvider;
import com.liferay.portal.kernel.portlet.BrowsePortletProvider;
import com.liferay.search.experiences.blueprints.constants.BlueprintsPortletKeys;

import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Kevin Tan
 */
@Component(
	immediate = true,
	property = "model.class.name=com.liferay.search.experiences.blueprints.model.Blueprint",
	service = BrowsePortletProvider.class
)
public class BlueprintsAdminBrowsePortletProvider
	extends BasePortletProvider implements BrowsePortletProvider {

	@Override
	public String getPortletName() {
		return BlueprintsPortletKeys.BLUEPRINTS_ADMIN;
	}

	@Override
	public PortletURL getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		return PortletURLBuilder.create(
			super.getPortletURL(httpServletRequest)
		).setMVCPath(
			"/select_blueprint.jsp"
		).build();
	}

}