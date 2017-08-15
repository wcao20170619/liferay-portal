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

import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSenderUtil;
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
@PrepareForTest(
	{ReindexStatusMessageSenderUtil.class, IndexerRegistryUtil.class}
)
@RunWith(PowerMockRunner.class)
public class ReindexSingleIndexerBackgroundTaskExecutorTest {

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		MockitoAnnotations.initMocks(this);

		PowerMockito.mockStatic(IndexerRegistryUtil.class);
		PowerMockito.when(
			IndexerRegistryUtil.getIndexer(Mockito.anyString())
		).thenReturn(
			Mockito.mock(Indexer.class)
		);
	}

	@Test
	public void testClone() throws Exception {
		ReindexSingleIndexerBackgroundTaskExecutor
			reindexSingleIndexerBackgroundTaskExecutor =
				new ReindexSingleIndexerBackgroundTaskExecutor(
					_indexWriterHelper);

		BackgroundTaskExecutor backgroundTaskExecutor =
			reindexSingleIndexerBackgroundTaskExecutor.clone();

		Assert.assertNotNull(backgroundTaskExecutor);
	}

	@Test
	public void testGenerateLockKey() throws Exception {
		BackgroundTask backgroundTask = Mockito.mock(BackgroundTask.class);

		ReindexSingleIndexerBackgroundTaskExecutor
			reindexSingleIndexerBackgroundTaskExecutor =
				new ReindexSingleIndexerBackgroundTaskExecutor(
					_indexWriterHelper);

		String lockKey =
			reindexSingleIndexerBackgroundTaskExecutor.generateLockKey(
				backgroundTask);

		Assert.assertEquals("null#5", lockKey);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testReindex() throws Exception {
		PowerMockito.spy(ReindexStatusMessageSenderUtil.class);

		PowerMockito.doNothing().when(
			ReindexStatusMessageSenderUtil.class, "sendStatusMessage",
			Mockito.anyString(), Mockito.anyLong(), Mockito.any(long[].class));

		Mockito.when(
			_indexWriterHelper.isIndexReadOnly()
		).thenReturn(
			true
		);

		ReindexSingleIndexerBackgroundTaskExecutor
			reindexSingleIndexerBackgroundTaskExecutor =
				new ReindexSingleIndexerBackgroundTaskExecutor(
					_indexWriterHelper);

		String className = "myPath.to.myClassName";
		long[] companyIds = {12345, 23456};

		reindexSingleIndexerBackgroundTaskExecutor.reindex(
			className, companyIds);
	}

	@Mock
	private IndexWriterHelper _indexWriterHelper;

}