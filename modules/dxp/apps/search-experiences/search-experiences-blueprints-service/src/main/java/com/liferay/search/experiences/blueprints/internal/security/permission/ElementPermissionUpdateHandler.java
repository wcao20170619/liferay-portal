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

package com.liferay.search.experiences.blueprints.internal.security.permission;

import com.liferay.portal.kernel.security.permission.PermissionUpdateHandler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.search.experiences.blueprints.model.Element;
import com.liferay.search.experiences.blueprints.service.ElementLocalService;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	property = "model.class.name=com.liferay.search.experiences.blueprints.model.Element",
	service = PermissionUpdateHandler.class
)
public class ElementPermissionUpdateHandler implements PermissionUpdateHandler {

	@Override
	public void updatedPermission(String primKey) {
		Element element = _elementLocalService.fetchElement(
			GetterUtil.getLong(primKey));

		if (element == null) {
			return;
		}

		element.setModifiedDate(new Date());

		_elementLocalService.updateElement(element);
	}

	@Reference
	private ElementLocalService _elementLocalService;

}