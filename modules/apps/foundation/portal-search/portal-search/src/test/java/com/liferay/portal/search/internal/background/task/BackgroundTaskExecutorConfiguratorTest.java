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

package com.liferay.portal.search.internal.background.task;

import java.util.Dictionary;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Wade Cao
 */
public class BackgroundTaskExecutorConfiguratorTest {

	@Before
	public void setUp() throws Exception
	{

		_bundleContext = Mockito.mock(BundleContext.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testActivateAndDeactivate() throws Exception {
		Mockito.when(
			_bundleContext.registerService(
				Mockito.any(Class.class), Mockito.any(Object.class),
				Mockito.any(Dictionary.class))
		).thenReturn(
			Mockito.mock(ServiceRegistration.class)
		);

		BackgroundTaskExecutorConfigurator configurator =
			new BackgroundTaskExecutorConfigurator();

		configurator.activate(_bundleContext);

		configurator.deactivate();
	}

	@Test(expected = Exception.class)
	public void testActivateAndDeactivateException() throws Exception {
		BackgroundTaskExecutorConfigurator configurator =
			new BackgroundTaskExecutorConfigurator();

		configurator.activate(_bundleContext);
		//without mock registerService returning it will cause unregister NPE
		configurator.deactivate();
	}

	private BundleContext _bundleContext;

}