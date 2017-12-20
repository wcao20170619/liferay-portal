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

package com.liferay.apio.architect.wiring.osgi.internal.manager.resource;

import static com.liferay.apio.architect.wiring.osgi.internal.manager.resource.ResourceClass.ITEM_IDENTIFIER_CLASS;
import static com.liferay.apio.architect.wiring.osgi.internal.manager.resource.ResourceClass.MODEL_CLASS;
import static com.liferay.apio.architect.wiring.osgi.internal.manager.util.ManagerUtil.createServiceTracker;
import static com.liferay.apio.architect.wiring.osgi.internal.manager.util.ManagerUtil.getTypeParamOrFail;

import com.liferay.apio.architect.representor.Representable;
import com.liferay.apio.architect.resource.ItemResource;
import com.liferay.apio.architect.router.ItemRouter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Allow developers to register its resources as a {@link ItemResource} instead
 * of implement an register each of the enclosing interfaces separately.
 *
 * @author Alejandro Hernández
 * @review
 */
@Component(immediate = true)
public class ItemResourceManager {

	@Activate
	protected void activate(BundleContext bundleContext) {
		String[] classes =
			{ItemRouter.class.getName(), Representable.class.getName()};

		_serviceTracker = createServiceTracker(
			bundleContext, ItemResource.class, classes,
			(properties, service) -> {
				Class<?> modelClass = getTypeParamOrFail(
					service, ItemResource.class, 0);

				properties.put(MODEL_CLASS.getName(), modelClass);

				Class<?> identifierClass = getTypeParamOrFail(
					service, ItemResource.class, 1);

				properties.put(
					ITEM_IDENTIFIER_CLASS.getName(), identifierClass);
			});

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private ServiceTracker<ItemResource, ServiceRegistration<?>>
		_serviceTracker;

}