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

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.index.GetMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetMappingIndexResponse;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.compress.CompressedXContent;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dylan Rebelak
 */
@Component(immediate = true, service = GetMappingIndexRequestExecutor.class)
public class GetMappingIndexRequestExecutorImpl
	implements GetMappingIndexRequestExecutor {

	@Override
	public GetMappingIndexResponse execute(
		GetMappingIndexRequest getMappingIndexRequest) {

		GetMappingsRequest getMappingsRequest = createGetMappingsRequest(
			getMappingIndexRequest);

		GetMappingsResponse getMappingsResponse = getGetMappingsResponse(
			getMappingsRequest, getMappingIndexRequest);

		Map<String, MappingMetaData> mappings = getMappingsResponse.mappings();

		Map<String, String> indexMappings = new HashMap<>();

		for (String indexName : getMappingIndexRequest.getIndexNames()) {
			MappingMetaData mappingMetaData = mappings.get(indexName);

			CompressedXContent mappingContent = mappingMetaData.source();

			indexMappings.put(indexName, mappingContent.toString());
		}

		return new GetMappingIndexResponse(indexMappings);
	}

	protected GetMappingsRequest createGetMappingsRequest(
		GetMappingIndexRequest getMappingIndexRequest) {

		GetMappingsRequest getMappingsRequest = new GetMappingsRequest();

		getMappingsRequest.indices(getMappingIndexRequest.getIndexNames());

		return getMappingsRequest;
	}

	protected GetMappingsResponse getGetMappingsResponse(
		GetMappingsRequest getMappingsRequest,
		GetMappingIndexRequest getMappingIndexRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				getMappingIndexRequest.getConnectionId(), true);

		IndicesClient indicesClient = restHighLevelClient.indices();

		try {
			return indicesClient.getMapping(
				getMappingsRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Reference(unbind = "-")
	protected void setElasticsearchClientResolver(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	private ElasticsearchClientResolver _elasticsearchClientResolver;

}