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

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.executor.PortalExecutorManager;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSender;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.RegistryUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Wade Cao
 */
@PrepareForTest(IndexWriterHelperUtil.class)
@RunWith(PowerMockRunner.class)
public class ReindexPortalBackgroundTaskExecutorTest {

	@Before
	@SuppressWarnings("deprecation")
	public void setUp() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		MockitoAnnotations.initMocks(this);

		_reindexStatusMessageSender = Mockito.spy(
			new ReindexStatusMessageSenderImpl());

		PowerMockito.mockStatic(IndexWriterHelperUtil.class);
		PowerMockito.when(
			IndexWriterHelperUtil.isIndexReadOnly()
		).thenReturn(
			true
		);
	}

	@Test
	public void testClone() throws Exception {
		ReindexPortalBackgroundTaskExecutor
			reindexPortalBackgroundTaskExecutor =
				new ReindexPortalBackgroundTaskExecutor(
					_portalExecutorManager, _reindexStatusMessageSender);

		BackgroundTaskExecutor backgroundTaskExecutor =
			reindexPortalBackgroundTaskExecutor.clone();

		Assert.assertNotNull(backgroundTaskExecutor);
	}

	@Test
	public void testReindex() throws Exception {
		Mockito.doNothing(
		).when(
			_reindexStatusMessageSender
		).sendStatusMessage(
			Mockito.anyString(), Mockito.anyLong(), Mockito.any(long[].class)
		);

		ReindexPortalBackgroundTaskExecutor
			reindexPortalBackgroundTaskExecutor =
				new ReindexPortalBackgroundTaskExecutor(
					_portalExecutorManager, _reindexStatusMessageSender);

		String className = "myPath.to.myClassName";
		long[] companyIds = {12345, 23456};

		reindexPortalBackgroundTaskExecutor.reindex(className, companyIds);
	}

	@Test(expected = Exception.class)
	public void testReindexException() throws Exception {
		Mockito.doThrow(
			new Exception()
		).when(
			_reindexStatusMessageSender
		).sendStatusMessage(
			Mockito.anyString(), Mockito.anyLong(), Mockito.any(long[].class)
		);

		ReindexPortalBackgroundTaskExecutor
			reindexPortalBackgroundTaskExecutor =
				new ReindexPortalBackgroundTaskExecutor(
					_portalExecutorManager, _reindexStatusMessageSender);

		String className = "myPath.to.myClassName";
		long[] companyIds = {12345, 23456};

		reindexPortalBackgroundTaskExecutor.reindex(className, companyIds);
	}

	@Mock
	private PortalExecutorManager _portalExecutorManager;

	private ReindexStatusMessageSender _reindexStatusMessageSender;

}