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

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.search.experiences.blueprints.model.Blueprint;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = PortalInstanceLifecycleListener.class)
public class BlueprintModelDocumentContributorDemo
	extends BasePortalInstanceLifecycleListener {
	
	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		
		Group group = _groupLocalService.getGroup(
			company.getCompanyId(), "Guest");
		
		User user = company.getDefaultUser();
		
		_blueprints.add(
			_blueprintModelDocumentDemoDataCreator.createBlueprint(
				"alpha", "beta", group.getGroupId(), user.getUserId()));
		
		_blueprints.add(
			_blueprintModelDocumentDemoDataCreator.createBlueprint(
				"charlie", "delta", group.getGroupId(), user.getUserId()));
		
	}
	
	@Deactivate
	protected void deactivate() throws Exception {
		for (Blueprint blueprint : _blueprints) {
			_blueprintModelDocumentDemoDataCreator.delete(blueprint);
		}
	}
	
	@Reference
	private BlueprintModelDocumentDemoDataCreator 
		_blueprintModelDocumentDemoDataCreator;
	
	@Reference
	private GroupLocalService _groupLocalService;
	
	
	private List<Blueprint> _blueprints = new ArrayList<Blueprint>();

}
