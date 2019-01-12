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

package com.liferay.portal.search.elasticsearch6.internal.field;

import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch6.internal.index.LiferayTypeMappingsConstants;
import com.liferay.portal.search.field.Mapping;
import com.liferay.portal.search.field.MappingsHolder;

import java.io.IOException;

import java.util.Map;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(immediate = true, service = FieldRegistrySynchronizer.class)
public class FieldRegistrySynchronizerImpl
	implements FieldRegistrySynchronizer {

	@Override
	public void sync(String indexName) {
		Client client = _elasticsearchClientResolver.getClient();

		AdminClient adminClient = client.admin();

		IndicesAdminClient indicesAdminClient = adminClient.indices();

		PutMappingRequestBuilder putMappingRequestBuilder =
			indicesAdminClient.preparePutMapping();

		putMappingRequestBuilder.setIndices(
			getIndexName(indexName)
		).setIndicesOptions(
			IndicesOptions.strictExpandOpen()
		).setSource(
			getMappingSource(), XContentType.JSON
		).setType(
			getElasticsearchType(indexName)
		).get();
	}

	protected void addFieldMapping(
		XContentBuilder xContentBuilder, String fieldName, Mapping mapping) {

		try {
			doAddFieldMapping(xContentBuilder, fieldName, mapping);
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	protected void doAddFieldMapping(
			XContentBuilder xContentBuilder, String fieldName, Mapping mapping)
		throws IOException {

		xContentBuilder.startObject(fieldName);

		xContentBuilder.field("type", mapping.getType());

		if (mapping.getAnalyzer() != null) {
			xContentBuilder.field("analyzer", mapping.getAnalyzer());
		}

		if (mapping.isStore()) {
			xContentBuilder.field("store", mapping.isStore());
		}

		if (mapping.getTermVector() != null) {
			xContentBuilder.field("term_vector", mapping.getTermVector());
		}

		if (mapping.getFormat() != null) {
			xContentBuilder.field("format", mapping.getFormat());
		}

		xContentBuilder.endObject();

		// TODO _fields

		/*if (!_fields.isEmpty()) {
			retValue += ",\n \"fields\": {" + _getFieldsMappingSource() + "}";
		}*/
	}

	protected String doGetMappingSource() throws IOException {
		XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();

		xContentBuilder.startObject();

		xContentBuilder.startObject("properties");

		Map<String, Mapping> mappings = _mappingsHolder.getMappings();

		mappings.forEach(
			(fieldName, mapping) -> addFieldMapping(
				xContentBuilder, fieldName, mapping));

		xContentBuilder.endObject();

		xContentBuilder.endObject();

		return Strings.toString(xContentBuilder);
	}

	protected String getElasticsearchType(String indexName) {
		return LiferayTypeMappingsConstants.LIFERAY_DOCUMENT_TYPE; // TODO Get from index? Type name registry?
	}

	protected String getIndexName(String indexName) {
		if (indexName == null) {
			return "_all";											// TODO Index per field registry, type per index
		}

		return indexName;
	}

	protected String getMappingSource() {
		try {
			return doGetMappingSource();
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

		// TODO _fields

	}

	@Reference
	protected void setElasticsearchClientResolver(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	@Reference
	protected void setMappingsHolder(MappingsHolder mappingsHolder) {
		_mappingsHolder = mappingsHolder;
	}

	private ElasticsearchClientResolver _elasticsearchClientResolver;

	private MappingsHolder _mappingsHolder;

}