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

package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.search;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.engine.adapter.search.MultisearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.MultisearchSearchResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.action.search.MultiSearchAction;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, service = MultisearchSearchRequestExecutor.class)
public class MultisearchSearchRequestExecutorImpl
	implements MultisearchSearchRequestExecutor {

	@Override
	public MultisearchSearchResponse execute(
		MultisearchSearchRequest multisearchSearchRequest) {

		Client client = elasticsearchClientResolver.getClient();

		MultiSearchRequestBuilder multiSearchRequestBuilder =
			MultiSearchAction.INSTANCE.newRequestBuilder(client);

		List<SearchSearchRequest> searchSearchRequests =
			multisearchSearchRequest.getSearchSearchRequests();

		List<SearchRequestHolder> searchRequestHolders = new ArrayList<>(
			searchSearchRequests.size());

		searchSearchRequests.forEach(
			searchSearchRequest -> {
				SearchRequestBuilder searchRequestBuilder =
					SearchAction.INSTANCE.newRequestBuilder(client);

				searchSearchRequestAssembler.assemble(
					searchRequestBuilder, searchSearchRequest);

				SearchRequestHolder searchRequestHolder =
					new SearchRequestHolder(
						searchSearchRequest, searchRequestBuilder);

				searchRequestHolders.add(searchRequestHolder);

				multiSearchRequestBuilder.add(searchRequestBuilder);
			});

		MultiSearchResponse multiSearchResponse =
			multiSearchRequestBuilder.get();

		Iterator<MultiSearchResponse.Item> multiSearchResponseItems =
			multiSearchResponse.iterator();

		MultisearchSearchResponse multisearchSearchResponse =
			new MultisearchSearchResponse();

		int counter = 0;

		while (multiSearchResponseItems.hasNext()) {
			MultiSearchResponse.Item multiSearchResponseItem =
				multiSearchResponseItems.next();

			SearchResponse searchResponse =
				multiSearchResponseItem.getResponse();

			SearchSearchResponse searchSearchResponse =
				new SearchSearchResponse();

			SearchRequestHolder searchRequestHolder = searchRequestHolders.get(
				counter);

			SearchSearchRequest searchSearchRequest =
				searchRequestHolder.getSearchSearchRequest();

			searchSearchResponseAssembler.assemble(
				searchRequestHolder.getSearchRequestBuilder(), searchResponse,
				searchSearchRequest, searchSearchResponse);

			if (searchSearchRequest.isIncludeResponseString()) {
				searchSearchResponse.setSearchResponseString(
					searchResponse.toString());
			}

			counter++;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				_getRequestStringExecutionTime(multisearchSearchResponse));
		}

		return multisearchSearchResponse;
	}

	@Reference
	protected ElasticsearchClientResolver elasticsearchClientResolver;

	@Reference
	protected SearchSearchRequestAssembler searchSearchRequestAssembler;

	@Reference
	protected SearchSearchResponseAssembler searchSearchResponseAssembler;

	private static String _getRequestStringExecutionTime(
		MultisearchSearchResponse multisearchSearchResponse) {

		List<String> requestStrings = new ArrayList<>();
		long[] executionTime = {0};

		List<SearchSearchResponse> searchSearchResponses =
			multisearchSearchResponse.getSearchSearchResponses();

		searchSearchResponses.forEach(
			searchSearchResponse -> {
				requestStrings.add(
					searchSearchResponse.getSearchRequestString());
				executionTime[0] += searchSearchResponse.getExecutionTime();
		});

		return StringBundler.concat(
			"The search engine processed ", requestStrings, " in ",
			executionTime[0] + " ms");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MultisearchSearchRequestExecutorImpl.class);

	private class SearchRequestHolder {

		public SearchRequestHolder(
			SearchSearchRequest searchSearchRequest,
			SearchRequestBuilder searchRequestBuilder) {

			_searchSearchRequest = searchSearchRequest;
			_searchRequestBuilder = searchRequestBuilder;
		}

		public SearchRequestBuilder getSearchRequestBuilder() {
			return _searchRequestBuilder;
		}

		public SearchSearchRequest getSearchSearchRequest() {
			return _searchSearchRequest;
		}

		private final SearchRequestBuilder _searchRequestBuilder;
		private final SearchSearchRequest _searchSearchRequest;

	}

}