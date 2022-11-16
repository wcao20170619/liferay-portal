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

package com.liferay.portal.search.solr8.internal.search.engine.adapter.document;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.document.BulkableDocumentRequestTranslator;
import com.liferay.portal.search.engine.adapter.document.GetDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetDocumentResponse;
import com.liferay.portal.search.solr8.internal.connection.SolrClientManager;
import com.liferay.portal.search.solr8.internal.document.DocumentFieldsTranslator;

import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(immediate = true, service = GetDocumentRequestExecutor.class)
public class GetDocumentRequestExecutorImpl
	implements GetDocumentRequestExecutor {

	@Override
	public GetDocumentResponse execute(GetDocumentRequest getDocumentRequest) {
		QueryRequest queryRequest =
			_bulkableDocumentRequestTranslator.translate(getDocumentRequest);

		try {
			QueryResponse queryResponse = queryRequest.process(
				_solrClientManager.getSolrClient(),
				getDocumentRequest.getIndexName());

			SolrDocumentList solrDocumentList = queryResponse.getResults();

			if (solrDocumentList.isEmpty()) {
				return new GetDocumentResponse(false);
			}

			SolrDocument solrDocument = solrDocumentList.get(0);

			GetDocumentResponse getDocumentResponse = new GetDocumentResponse(
				true);

			getDocumentResponse.setSource(
				String.valueOf(solrDocument.getFieldValuesMap()));

			DocumentBuilder documentBuilder = _documentBuilderFactory.builder();

			DocumentFieldsTranslator.translate(documentBuilder, solrDocument);

			getDocumentResponse.setDocument(documentBuilder.build());

			getDocumentResponse.setVersion(
				GetterUtil.getLong(solrDocument.getFieldValue(_VERSION_FIELD)));

			return getDocumentResponse;
		}
		catch (Exception exception) {
			if (exception instanceof SolrException) {
				SolrException solrException = (SolrException)exception;

				throw solrException;
			}

			throw new RuntimeException(exception);
		}
	}

	private static final String _VERSION_FIELD = "_version_";

	@Reference(target = "(search.engine.impl=Solr)")
	private BulkableDocumentRequestTranslator
		_bulkableDocumentRequestTranslator;

	@Reference
	private DocumentBuilderFactory _documentBuilderFactory;

	@Reference
	private SolrClientManager _solrClientManager;

}