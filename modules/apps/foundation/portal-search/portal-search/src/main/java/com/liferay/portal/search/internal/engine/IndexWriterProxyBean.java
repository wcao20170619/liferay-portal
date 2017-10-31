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

package com.liferay.portal.search.internal.engine;

import com.liferay.portal.kernel.messaging.proxy.BaseMultiDestinationProxyBean;
import com.liferay.portal.kernel.messaging.proxy.ProxyRequest;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineHelperUtil;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;

/**
 * @author Bruno Farache
 * @author Tina Tian
 */
@Component(
	immediate = true, property = {"search.engine.impl=Defaultsearch"},
	service = IndexWriter.class
)
public class IndexWriterProxyBean
	extends BaseMultiDestinationProxyBean implements IndexWriter {

	@Override
	public void addDocument(SearchContext searchContext, Document document) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addDocuments(
		SearchContext searchContext, Collection<Document> documents) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void clearQuerySuggestionDictionaryIndexes(
		SearchContext searchContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void clearSpellCheckerDictionaryIndexes(
		SearchContext searchContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void commit(SearchContext searchContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteDocument(SearchContext searchContext, String uid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteDocuments(
		SearchContext searchContext, Collection<String> uids) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteEntityDocuments(
		SearchContext searchContext, String className) {

		throw new UnsupportedOperationException();
	}

	@Override
	public String getDestinationName(ProxyRequest proxyRequest) {
		Object[] arguments = proxyRequest.getArguments();

		SearchContext searchContext = (SearchContext)arguments[0];

		String searchEngineId = searchContext.getSearchEngineId();

		return SearchEngineHelperUtil.getSearchWriterDestinationName(
			searchEngineId);
	}

	@Override
	public void indexKeyword(
		SearchContext searchContext, float weight, String keywordType) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void indexQuerySuggestionDictionaries(SearchContext searchContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void indexQuerySuggestionDictionary(SearchContext searchContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void indexSpellCheckerDictionaries(SearchContext searchContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void indexSpellCheckerDictionary(SearchContext searchContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void partiallyUpdateDocument(
		SearchContext searchContext, Document document) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void partiallyUpdateDocuments(
		SearchContext searchContext, Collection<Document> documents) {
	}

	@Override
	public void updateDocument(SearchContext searchContext, Document document) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateDocuments(
		SearchContext searchContext, Collection<Document> documents) {

		throw new UnsupportedOperationException();
	}

}