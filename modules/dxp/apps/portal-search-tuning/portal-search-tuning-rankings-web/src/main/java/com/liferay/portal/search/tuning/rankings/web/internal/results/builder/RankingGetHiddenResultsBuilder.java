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
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.GetDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetDocumentResponse;
import com.liferay.portal.search.query.IdsQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.tuning.rankings.web.internal.index.Ranking;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexReader;
import com.liferay.portal.search.tuning.rankings.web.internal.index.name.RankingIndexName;
import com.liferay.portal.search.web.interpreter.SearchResultInterpreter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.portlet.PortletURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * @author André de Oliveira
 * @author Bryan Engler
 */
public class RankingGetHiddenResultsBuilder {

	public RankingGetHiddenResultsBuilder(
		DLAppLocalService dlAppLocalService,
		FastDateFormatFactory fastDateFormatFactory, Queries queries,
		RankingIndexName rankingIndexName,
		RankingIndexReader rankingIndexReader, ResourceActions resourceActions,
		ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		SearchEngineAdapter searchEngineAdapter,
		SearchResultInterpreter searchResultInterpreter) {

		_dlAppLocalService = dlAppLocalService;
		_fastDateFormatFactory = fastDateFormatFactory;
		_queries = queries;
		_rankingIndexName = rankingIndexName;
		_rankingIndexReader = rankingIndexReader;
		_resourceActions = resourceActions;
		_resourceRequest = resourceRequest;
		_resourceResponse = resourceResponse;
		_searchEngineAdapter = searchEngineAdapter;
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

		List<String> ids = ranking.getBlockIds();

		List<String> paginatedIds = paginateIds(ids);

		return JSONUtil.put(
			"documents", buildDocuments(paginatedIds, ranking)
		).put(
			"total", ids.size()
		);
	}

	public RankingGetHiddenResultsBuilder from(int from) {
		_from = from;

		return this;
	}

	public RankingGetHiddenResultsBuilder rankingId(String rankingId) {
		_rankingId = rankingId;

		return this;
	}

	public RankingGetHiddenResultsBuilder size(int size) {
		_size = size;

		return this;
	}

	protected JSONArray buildDocuments(List<String> ids, Ranking ranking) {
		Stream<String> stringStream = ids.stream();

		Stream<JSONObject> jsonObjectStream = stringStream.map(
			id -> getDocument(ranking.getIndex(), id, LIFERAY_DOCUMENT_TYPE)
		).filter(
			document -> document != null
		).map(
			this::translate
		);

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		jsonObjectStream.forEach(jsonArray::put);

		return jsonArray;
	}

	protected Document getDocument(String index, String id, String type) {
		GetDocumentRequest getDocumentRequest = new GetDocumentRequest(
			index, id);

		getDocumentRequest.setFetchSource(true);
		getDocumentRequest.setFetchSourceInclude("*");
		getDocumentRequest.setType(type);

		GetDocumentResponse getDocumentResponse = _searchEngineAdapter.execute(
			getDocumentRequest);

		if (!getDocumentResponse.isExists()) {
			return null;
		}

		return getDocumentResponse.getDocument();
	}

	protected Query getIdsQuery(List<String> ids) {
		IdsQuery idsQuery = _queries.ids();

		idsQuery.addIds(ArrayUtil.toStringArray(ids));

		return idsQuery;
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

	protected List<String> paginateIds(List<String> ids) {
		int end = _from + _size;

		return ListUtil.subList(ids, _from, end);
	}

	protected JSONObject translate(Document document) {
		RankingJSONBuilder rankingJSONBuilder = new RankingJSONBuilder(
			_dlAppLocalService, _fastDateFormatFactory, _resourceActions,
			_resourceRequest);

		return rankingJSONBuilder.document(
			document
		).hidden(
			true
		).viewURL(
			getViewURL(document)
		).build();
	}

	protected static final String LIFERAY_DOCUMENT_TYPE = "LiferayDocumentType";

	private static final Log _log = LogFactoryUtil.getLog(
		RankingGetHiddenResultsBuilder.class);

	private final DLAppLocalService _dlAppLocalService;
	private final FastDateFormatFactory _fastDateFormatFactory;
	private int _from;
	private final Queries _queries;
	private String _rankingId;
	private final RankingIndexName _rankingIndexName;
	private final RankingIndexReader _rankingIndexReader;
	private final ResourceActions _resourceActions;
	private final ResourceRequest _resourceRequest;
	private final ResourceResponse _resourceResponse;
	private final SearchEngineAdapter _searchEngineAdapter;
	private final SearchResultInterpreter _searchResultInterpreter;
	private int _size;

}