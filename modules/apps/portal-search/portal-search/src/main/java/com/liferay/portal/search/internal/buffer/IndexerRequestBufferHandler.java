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

package com.liferay.portal.search.internal.buffer;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.buffer.IndexerRequest;
import com.liferay.portal.search.buffer.IndexerRequestBuffer;
import com.liferay.portal.search.buffer.IndexerRequestBufferOverflowHandler;
import com.liferay.portal.search.configuration.IndexerRegistryConfiguration;

/**
 * @author Bryan Engler
 * @author Michael C. Han
 */
public class IndexerRequestBufferHandler {

	public IndexerRequestBufferHandler(
		IndexerRequestBufferOverflowHandler indexerRequestBufferOverflowHandler,
		IndexerRegistryConfiguration indexerRegistryConfiguration) {

		_indexerRequestBufferOverflowHandler =
			indexerRequestBufferOverflowHandler;
		_indexerRegistryConfiguration = indexerRegistryConfiguration;
	}

	public void bufferRequest(
			IndexerRequest indexerRequest,
			IndexerRequestBuffer indexerRequestBuffer)
		throws Exception {

		if (!BufferOverflowThreadLocal.isOverflowMode()) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Adding indexer request ", indexerRequest,
						" to indexer request buffer ", indexerRequestBuffer));
			}

			int maxBufferSize = _indexerRegistryConfiguration.maxBufferSize();

			indexerRequestBuffer.add(
				indexerRequest, _indexerRequestBufferOverflowHandler,
				maxBufferSize);
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug("Overflow! Execute now: " + indexerRequest);
			}

			indexerRequest.execute();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexerRequestBufferHandler.class);

	private final IndexerRegistryConfiguration _indexerRegistryConfiguration;
	private final IndexerRequestBufferOverflowHandler
		_indexerRequestBufferOverflowHandler;

}