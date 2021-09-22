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

package com.liferay.portal.search.tuning.rankings.web.internal.index.creation.activator;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.uuid.PortalUUID;
import com.liferay.portal.search.tuning.rankings.web.internal.background.task.RankingIndexCreationBackgroundTaskExecutor;
import com.liferay.portal.search.tuning.rankings.web.internal.index.importer.SingleIndexToMultipleIndexImporter;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Wade Cao
 */
public class RankingIndexCreationBundleActivatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		_rankingIndexCreationBundleActivator =
			new RankingIndexCreationBundleActivator();

		ReflectionTestUtil.setFieldValue(
			_rankingIndexCreationBundleActivator, "_backgroundTaskManager",
			_backgroundTaskManager);
		ReflectionTestUtil.setFieldValue(
			_rankingIndexCreationBundleActivator, "_portalUUID", _portalUUID);
		ReflectionTestUtil.setFieldValue(
			_rankingIndexCreationBundleActivator,
			"_rankingIndexRenameBackgroundTaskExecutor",
			_rankingIndexRenameBackgroundTaskExecutor);
		ReflectionTestUtil.setFieldValue(
			_rankingIndexCreationBundleActivator,
			"_singleIndexToMultipleIndexImporter",
			_singleIndexToMultipleIndexImporter);
	}

	@Test
	public void testActivatorSingleIndexToMultipleIndexImporterFalse()
		throws Exception {

		_setUpSingleIndexToMultipleIndexImporter(false);

		_rankingIndexCreationBundleActivator.activate();

		Mockito.verify(
			_singleIndexToMultipleIndexImporter, Mockito.times(1)
		).needImport();
		Mockito.verify(
			_backgroundTaskManager, Mockito.times(0)
		).addBackgroundTask(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(),
			Mockito.anyString(), Mockito.anyMap(), Mockito.anyObject()
		);
	}

	@Test
	public void testActivatorSingleIndexToMultipleIndexImporterTrue()
		throws Exception {

		_setUpSingleIndexToMultipleIndexImporter(true);

		_rankingIndexCreationBundleActivator.activate();

		Mockito.verify(
			_singleIndexToMultipleIndexImporter, Mockito.times(1)
		).needImport();
		Mockito.verify(
			_backgroundTaskManager, Mockito.times(1)
		).addBackgroundTask(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(),
			Mockito.anyString(), Mockito.anyMap(), Mockito.anyObject()
		);
	}

	private void _setUpSingleIndexToMultipleIndexImporter(boolean exist) {
		Mockito.doReturn(
			exist
		).when(
			_singleIndexToMultipleIndexImporter
		).needImport();
	}

	@Mock
	private BackgroundTaskManager _backgroundTaskManager;

	@Mock
	private PortalUUID _portalUUID;

	private RankingIndexCreationBundleActivator
		_rankingIndexCreationBundleActivator;

	@Mock
	private RankingIndexCreationBackgroundTaskExecutor
		_rankingIndexRenameBackgroundTaskExecutor;

	@Mock
	private SingleIndexToMultipleIndexImporter
		_singleIndexToMultipleIndexImporter;

}