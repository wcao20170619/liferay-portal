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

package com.liferay.portal.search.internal.ranking;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.ranking.CustomRankingsIndexer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(immediate = true, service = CustomRankingsIndexer.class)
public class CustomRankingsIndexerImpl implements CustomRankingsIndexer {

	@Override
	public void addCustomRanking(
		String index, String keywords, String[] pinnedDocuments,
		String[] hiddenDocuments) {

		Document document = _createCustomRankingDocument(
			index, keywords, pinnedDocuments, hiddenDocuments);

		_addDocument(document);
	}

	@Override
	public void deleteCustomRanking(String index, String keywords) {
		Document document = _createCustomRankingDocument(
			index, keywords, null, null);

		_deleteDocument(document);
	}

	@Override
	public String getCustomRanking(String index, String keywords) {
		Document document = _createCustomRankingDocument(
			index, keywords, null, null);

		return _getDocument(document);
	}

	@Override
	public void updateCustomRanking(
		String index, String keywords, String[] pinnedDocuments,
		String[] hiddenDocuments) {

		Document document = _createCustomRankingDocument(
			index, keywords, pinnedDocuments, hiddenDocuments);

		_updateDocument(document);
	}

	@Activate
	protected void activate() throws Exception {
		createIndex();
	}

	protected void createIndex() {
		IndicesExistsIndexRequest indicesExistsIndexRequest =
			new IndicesExistsIndexRequest(getIndexName());

		IndicesExistsIndexResponse indicesExistsIndexResponse =
			_searchEngineAdapter.execute(indicesExistsIndexRequest);

		if (indicesExistsIndexResponse.isExists()) {
			return;
		}

		try {
			CreateIndexRequest createIndexRequest = new CreateIndexRequest(
				getIndexName());

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				StringUtil.read(getClass(), "/META-INF/search/mappings.json"));

			createIndexRequest.setSource(
				JSONUtil.put(
					"mappings",
					JSONUtil.put(getIndexType(), jsonObject.get(getIndexType()))
				).toString());

			_searchEngineAdapter.execute(createIndexRequest);
		}
		catch (Exception e) {
			System.out.println("unable to create index");
		}
	}

	protected String getIndexName() {
		return "custom-rankings";
	}

	protected String getIndexType() {
		return "CustomRankingsType";
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	protected void setModuleServiceLifecycle(
		ModuleServiceLifecycle moduleServiceLifecycle) {
	}

	@Reference(unbind = "-")
	protected void setSearchEngineAdapter(
		SearchEngineAdapter searchEngineAdapter) {

		_searchEngineAdapter = searchEngineAdapter;
	}

	private void _addDocument(Document document) {
		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			getIndexName(), document);

		indexDocumentRequest.setType(getIndexType());

		_searchEngineAdapter.execute(indexDocumentRequest);
	}

	private Document _createCustomRankingDocument(
		String index, String keywords, String[] pinnedDocuments,
		String[] hiddenDocuments) {

		Document document = new DocumentImpl();

		document.addKeyword(
			Field.UID,
			index + StringPool.UNDERLINE +
				StringUtil.replace(
					keywords, CharPool.SPACE, CharPool.UNDERLINE));
		document.addKeyword("hidden_documents", hiddenDocuments);
		document.addKeyword("index", index);
		document.addKeyword("keywords", keywords);
		document.addKeyword("pinned_documents", pinnedDocuments);

		return document;
	}

	private void _deleteDocument(Document document) {
		DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest(
			getIndexName(), document.getUID());

		deleteDocumentRequest.setType(getIndexType());

		_searchEngineAdapter.execute(deleteDocumentRequest);
	}

	private String _getDocument(Document document) {
		GetDocumentRequest getDocumentRequest = new GetDocumentRequest(
			getIndexName(), document.getUID());

		getDocumentRequest.setType(getIndexType());

		GetDocumentResponse getDocumentResponse = _searchEngineAdapter.execute(
			getDocumentRequest);

		if (getDocumentResponse.isExists()) {
			return getDocumentResponse.getSource();
		}

		return null;
	}

	private void _updateDocument(Document document) {
		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(
			getIndexName(), document.getUID(), document);

		updateDocumentRequest.setType(getIndexType());

		_searchEngineAdapter.execute(updateDocumentRequest);
	}

	private SearchEngineAdapter _searchEngineAdapter;

}