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

package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.document;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch6.internal.document.DefaultElasticsearchDocumentFactory;
import com.liferay.portal.search.elasticsearch6.internal.document.ElasticsearchDocumentFactory;
import com.liferay.portal.search.engine.adapter.document.BulkableDocumentRequestTranslator;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;

import java.io.IOException;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteAction;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexAction;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateAction;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "search.engine.impl=Elasticsearch",
	service = BulkableDocumentRequestTranslator.class
)
public class ElasticsearchBulkableDocumentRequestTranslator
	implements BulkableDocumentRequestTranslator
		<DeleteRequestBuilder, IndexRequestBuilder, UpdateRequestBuilder,
		 BulkRequestBuilder> {

	@Override
	public DeleteRequestBuilder translate(
		DeleteDocumentRequest deleteDocumentRequest,
		BulkRequestBuilder searchEngineAdapterRequest) {

		Client client = elasticsearchClientResolver.getClient();

		DeleteRequestBuilder deleteRequestBuilder =
			DeleteAction.INSTANCE.newRequestBuilder(client);

		deleteRequestBuilder.setId(deleteDocumentRequest.getUid());
		deleteRequestBuilder.setIndex(deleteDocumentRequest.getIndexName());

		if (deleteDocumentRequest.isRefresh()) {
			deleteRequestBuilder.setRefreshPolicy(
				WriteRequest.RefreshPolicy.IMMEDIATE);
		}

		deleteRequestBuilder.setType(deleteDocumentRequest.getType());

		if (searchEngineAdapterRequest != null) {
			searchEngineAdapterRequest.add(deleteRequestBuilder);
		}

		return deleteRequestBuilder;
	}

	@Override
	public IndexRequestBuilder translate(
		IndexDocumentRequest indexDocumentRequest,
		BulkRequestBuilder searchEngineAdapterRequest) {

		try {
			Client client = elasticsearchClientResolver.getClient();

			IndexRequestBuilder indexRequestBuilder =
				IndexAction.INSTANCE.newRequestBuilder(client);

			setIdIndex(indexDocumentRequest, indexRequestBuilder);

			indexRequestBuilder.setIndex(indexDocumentRequest.getIndexName());

			if (indexDocumentRequest.isRefresh()) {
				indexRequestBuilder.setRefreshPolicy(
					WriteRequest.RefreshPolicy.IMMEDIATE);
			}

			indexRequestBuilder.setType(indexDocumentRequest.getType());

			ElasticsearchDocumentFactory elasticsearchDocumentFactory =
				new DefaultElasticsearchDocumentFactory();

			Document document = indexDocumentRequest.getDocument();

			String elasticsearchDocument =
				elasticsearchDocumentFactory.getElasticsearchDocument(document);

			indexRequestBuilder.setSource(
				elasticsearchDocument, XContentType.JSON);

			if (searchEngineAdapterRequest != null) {
				searchEngineAdapterRequest.add(indexRequestBuilder);
			}

			return indexRequestBuilder;
		}
		catch (IOException ioe) {
			throw new SystemException(ioe);
		}
	}

	@Override
	public UpdateRequestBuilder translate(
		UpdateDocumentRequest updateDocumentRequest,
		BulkRequestBuilder searchEngineAdapterRequest) {

		try {
			Client client = elasticsearchClientResolver.getClient();

			UpdateRequestBuilder updateRequestBuilder =
				UpdateAction.INSTANCE.newRequestBuilder(client);

			setIdUpdate(updateDocumentRequest, updateRequestBuilder);

			updateRequestBuilder.setIndex(updateDocumentRequest.getIndexName());

			if (updateDocumentRequest.isRefresh()) {
				updateRequestBuilder.setRefreshPolicy(
					WriteRequest.RefreshPolicy.IMMEDIATE);
			}

			updateRequestBuilder.setType(updateDocumentRequest.getType());

			ElasticsearchDocumentFactory elasticsearchDocumentFactory =
				new DefaultElasticsearchDocumentFactory();

			Document document = updateDocumentRequest.getDocument();

			String elasticsearchDocument =
				elasticsearchDocumentFactory.getElasticsearchDocument(document);

			updateRequestBuilder.setDoc(
				elasticsearchDocument, XContentType.JSON);

			if (searchEngineAdapterRequest != null) {
				searchEngineAdapterRequest.add(updateRequestBuilder);
			}

			return updateRequestBuilder;
		}
		catch (IOException ioe) {
			throw new SystemException(ioe);
		}
	}

	protected void setIdIndex(
		IndexDocumentRequest indexDocumentRequest,
		IndexRequestBuilder indexRequestBuilder) {

		Document document = indexDocumentRequest.getDocument();

		Field field = document.getField(Field.UID);

		if (field != null) {
			String uid = field.getValue();

			if (!Validator.isBlank(uid)) {
				indexRequestBuilder.setId(uid);
			}
		}
	}

	protected void setIdUpdate(
		UpdateDocumentRequest updateDocumentRequest,
		UpdateRequestBuilder updateRequestBuilder) {

		String uid = updateDocumentRequest.getUid();

		if (!Validator.isBlank(uid)) {
			updateRequestBuilder.setId(uid);
		}
	}

	@Reference
	protected ElasticsearchClientResolver elasticsearchClientResolver;

}