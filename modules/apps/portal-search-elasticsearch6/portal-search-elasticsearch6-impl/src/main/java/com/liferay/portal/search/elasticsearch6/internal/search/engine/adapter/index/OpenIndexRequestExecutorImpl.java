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

package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.index;

import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.search.engine.adapter.index.OpenIndexRequest;
import com.liferay.portal.search.engine.adapter.index.OpenIndexResponse;

import org.elasticsearch.action.admin.indices.open.OpenIndexAction;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequestBuilder;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.common.unit.TimeValue;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = OpenIndexRequestExecutor.class)
public class OpenIndexRequestExecutorImpl implements OpenIndexRequestExecutor {

	@Override
	public OpenIndexResponse execute(OpenIndexRequest openIndexRequest) {
		OpenIndexRequestBuilder openIndexRequestBuilder =
			createOpenIndexRequestBuilder(openIndexRequest);

		AcknowledgedResponse acknowledgedResponse =
			openIndexRequestBuilder.get();

		OpenIndexResponse openIndexResponse = new OpenIndexResponse(
			acknowledgedResponse.isAcknowledged());

		return openIndexResponse;
	}

	protected OpenIndexRequestBuilder createOpenIndexRequestBuilder(
		OpenIndexRequest openIndexRequest) {

		OpenIndexRequestBuilder openIndexRequestBuilder =
			OpenIndexAction.INSTANCE.newRequestBuilder(
				_elasticsearchClientResolver.getClient());

		openIndexRequestBuilder.setIndices(openIndexRequest.getIndexNames());

		IndicesOptions indicesOptions = openIndexRequest.getIndicesOptions();

		if (indicesOptions != null) {
			openIndexRequestBuilder.setIndicesOptions(
				_indicesOptionsTranslator.translate(indicesOptions));
		}

		if (openIndexRequest.getTimeout() > 0) {
			TimeValue timeValue = TimeValue.timeValueMillis(
				openIndexRequest.getTimeout());

			openIndexRequestBuilder.setMasterNodeTimeout(timeValue);
			openIndexRequestBuilder.setTimeout(timeValue);
		}

		if (openIndexRequest.getWaitForActiveShards() > 0) {
			openIndexRequestBuilder.setWaitForActiveShards(
				openIndexRequest.getWaitForActiveShards());
		}

		return openIndexRequestBuilder;
	}

	@Reference(unbind = "-")
	protected void setElasticsearchClientResolver(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	@Reference(unbind = "-")
	protected void setIndicesOptionsTranslator(
		IndicesOptionsTranslator indicesOptionsTranslator) {

		_indicesOptionsTranslator = indicesOptionsTranslator;
	}

	private ElasticsearchClientResolver _elasticsearchClientResolver;
	private IndicesOptionsTranslator _indicesOptionsTranslator;

}