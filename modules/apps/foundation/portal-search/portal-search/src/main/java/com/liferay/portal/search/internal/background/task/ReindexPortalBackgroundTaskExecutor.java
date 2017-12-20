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

import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.background.task.ReindexBackgroundTaskConstants;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSenderUtil;
import com.liferay.portal.search.internal.SearchEngineInitializer;

/**
 * @author Andrew Betts
 */
public class ReindexPortalBackgroundTaskExecutor
	extends ReindexBackgroundTaskExecutor {

	public ReindexPortalBackgroundTaskExecutor(
		PortalExecutorManager portalExecutorManager,
		IndexWriterHelper indexWriterHelper) {

		_portalExecutorManager = portalExecutorManager;
		_indexWriterHelper = indexWriterHelper;
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return new ReindexPortalBackgroundTaskExecutor(
			_portalExecutorManager, _indexWriterHelper);
	}

	@Override
	protected void reindex(String className, long[] companyIds)
		throws Exception {

		for (long companyId : companyIds) {
			ReindexStatusMessageSenderUtil.sendStatusMessage(
				ReindexBackgroundTaskConstants.PORTAL_START, companyId,
				companyIds);

			try {
				SearchEngineInitializer searchEngineInitializer =
					new SearchEngineInitializer(
						companyId, _portalExecutorManager, _indexWriterHelper);

				searchEngineInitializer.reindex();
			}
			catch (Exception e) {
				_log.error(e, e);
			}
			finally {
				ReindexStatusMessageSenderUtil.sendStatusMessage(
					ReindexBackgroundTaskConstants.PORTAL_END, companyId,
					companyIds);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReindexPortalBackgroundTaskExecutor.class);

	private final IndexWriterHelper _indexWriterHelper;
	private final PortalExecutorManager _portalExecutorManager;

}