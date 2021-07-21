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

package com.liferay.blueprint.model.document.demo;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.search.experiences.blueprints.model.Blueprint;
import com.liferay.search.experiences.blueprints.service.BlueprintLocalService;
import com.liferay.search.experiences.blueprints.service.BlueprintService;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = BlueprintModelDocumentDemoDataCreator.class)
public class BlueprintModelDocumentDemoDataCreator {
	
	
	public Blueprint createBlueprint(
		String title, String description, long groupId, long userId) throws Exception {
		
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(groupId);
		
		return _blueprintLocalService.addBlueprint(
				userId, groupId,
				Collections.singletonMap(
				LocaleUtil.US, title), 
				Collections.singletonMap(LocaleUtil.US, description),
				JSONUtil.put(null, null).toString(), "",
				serviceContext);
	}
	
	
	public void delete(Blueprint blueprint) throws Exception {
		_blueprintLocalService.deleteBlueprint(blueprint.getBlueprintId());
	}
	
	@Reference
	private BlueprintLocalService _blueprintLocalService;

}
