/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.rankings.web.internal.results.builder;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.tuning.rankings.web.internal.index.Ranking;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexReader;
import com.liferay.portal.search.tuning.rankings.web.internal.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.web.internal.searcher.RankingSearchRequestHelper;
import com.liferay.portal.search.web.interpreter.SearchResultInterpreter;

import java.util.Optional;
import java.util.stream.Stream;

import javax.portlet.PortletURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * @author André de Oliveira
 * @author Bryan Engler
 */
public class RankingGetVisibleResultsBuilder {

	public RankingGetVisibleResultsBuilder(
		ComplexQueryPartBuilderFactory complexQueryPartBuilderFactory,
		DLAppLocalService dlAppLocalService,
		FastDateFormatFactory fastDateFormatFactory,
		RankingIndexName rankingIndexName,
		RankingIndexReader rankingIndexReader,
		RankingSearchRequestHelper rankingSearchRequestHelper,
		ResourceActions resourceActions, ResourceRequest resourceRequest,
		ResourceResponse resourceResponse, Queries queries, Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory,
		SearchResultInterpreter searchResultInterpreter) {

		_complexQueryPartBuilderFactory = complexQueryPartBuilderFactory;
		_dlAppLocalService = dlAppLocalService;
		_fastDateFormatFactory = fastDateFormatFactory;
		_rankingIndexName = rankingIndexName;
		_rankingIndexReader = rankingIndexReader;
		_rankingSearchRequestHelper = rankingSearchRequestHelper;
		_resourceActions = resourceActions;
		_resourceRequest = resourceRequest;
		_resourceResponse = resourceResponse;
		_queries = queries;
		_searcher = searcher;
		_searchRequestBuilderFactory = searchRequestBuilderFactory;
		_searchResultInterpreter = searchResultInterpreter;
	}

	public JSONObject build() {
		Optional<Ranking> optional = _rankingIndexReader.fetchOptional(
			_rankingIndexName, _rankingId);

		if (!optional.isPresent()) {
			return JSONUtil.put(
				"documents", JSONFactoryUtil.createJSONArray()
			).put(
				"total", 0
			);
		}

		Ranking ranking = optional.get();

		SearchResponse searchResponse = getSearchResponse(ranking);

		return JSONUtil.put(
			"documents", buildDocuments(ranking, searchResponse)
		).put(
			"total", searchResponse.getTotalHits()
		);
	}

	public RankingGetVisibleResultsBuilder companyId(long companyId) {
		_companyId = companyId;

		return this;
	}

	public RankingGetVisibleResultsBuilder from(int from) {
		_from = from;

		return this;
	}

	public RankingGetVisibleResultsBuilder queryString(String queryString) {
		_queryString = queryString;

		return this;
	}

	public RankingGetVisibleResultsBuilder rankingId(String rankingId) {
		_rankingId = rankingId;

		return this;
	}

	public RankingGetVisibleResultsBuilder size(int size) {
		_size = size;

		return this;
	}

	protected JSONArray buildDocuments(
		Ranking ranking, SearchResponse searchResponse) {

		Stream<Document> documentsStream = searchResponse.getDocumentsStream();

		Stream<JSONObject> jsonObjectStream = documentsStream.map(
			document -> translate(document, ranking));

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		jsonObjectStream.forEach(jsonArray::put);

		return jsonArray;
	}

	protected SearchRequest buildSearchRequest(Ranking ranking) {
		String queryStringOfUrl = _queryString;

		String queryString = queryStringOfUrl;

		if (Validator.isBlank(queryStringOfUrl)) {
			queryString = ranking.getQueryString();
		}

		RankingSearchRequestBuilder rankingSearchRequestBuilder =
			new RankingSearchRequestBuilder(
				_complexQueryPartBuilderFactory, _queries,
				_searchRequestBuilderFactory);

		SearchRequestBuilder searchRequestBuilder =
			rankingSearchRequestBuilder.companyId(
				_companyId
			).from(
				_from
			).queryString(
				queryString
			).size(
				_size
			).build();

		_rankingSearchRequestHelper.contribute(searchRequestBuilder, ranking);

		return searchRequestBuilder.build();
	}

	protected SearchResponse getSearchResponse(Ranking ranking) {
		SearchRequest searchRequest = buildSearchRequest(ranking);

		return _searcher.search(searchRequest);
	}

	protected String getViewURL(Document document) {
		try {
			PortletURL portletURL = _searchResultInterpreter.getAssetURLView(
				document,
				PortalUtil.getLiferayPortletResponse(_resourceResponse),
				_resourceRequest.getWindowState());

			if (portletURL != null) {
				return portletURL.toString();
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get PortletURL", exception);
			}
		}

		return StringPool.BLANK;
	}

	protected JSONObject translate(Document document, Ranking ranking) {
		RankingJSONBuilder rankingJSONBuilder = new RankingJSONBuilder(
			_dlAppLocalService, _fastDateFormatFactory, _resourceActions,
			_resourceRequest);

		return rankingJSONBuilder.document(
			document
		).pinned(
			ranking.isPinned(document.getString(Field.UID))
		).viewURL(
			getViewURL(document)
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RankingGetVisibleResultsBuilder.class);

	private long _companyId;
	private final ComplexQueryPartBuilderFactory
		_complexQueryPartBuilderFactory;
	private final DLAppLocalService _dlAppLocalService;
	private final FastDateFormatFactory _fastDateFormatFactory;
	private int _from;
	private final Queries _queries;
	private String _queryString;
	private String _rankingId;
	private final RankingIndexName _rankingIndexName;
	private final RankingIndexReader _rankingIndexReader;
	private final RankingSearchRequestHelper _rankingSearchRequestHelper;
	private final ResourceActions _resourceActions;
	private final ResourceRequest _resourceRequest;
	private final ResourceResponse _resourceResponse;
	private final Searcher _searcher;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;
	private final SearchResultInterpreter _searchResultInterpreter;
	private int _size;

}