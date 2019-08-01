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

package com.liferay.portal.search.web.internal.search.morelikethis.display.context;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.query.MoreLikeThisQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.web.internal.search.morelikethis.portlet.SearchMoreLikeThisPortletPreferences;
import com.liferay.portal.search.web.internal.search.morelikethis.portlet.SearchMoreLikeThisPortletPreferencesImpl;
import com.liferay.portal.search.web.internal.util.SearchStringUtil;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.search.request.SearchSettings;

/**
 * @author Wade Cao
 */
public class SearchMoreLikeThisPortletDisplayBuilder {
	
	public SearchMoreLikeThisPortletDisplayBuilder(
		ComplexQueryPartBuilderFactory complexQueryPartBuilderFactory,
		HttpServletRequest httpServletRequest, 
		IndexNameBuilder indexNameBuilder,
		Language language,
		PortletSharedSearchResponse portletSharedSearchResponse, 
		Queries queries, RenderRequest renderRequest,
		RenderResponse renderResponse, 
		SearchEngineAdapter searchEngineAdapter) {
//		Searcher searcher,
//		SearchRequestBuilderFactory searchRequestBuilderFactory) {
		
		_complexQueryPartBuilderFactory = complexQueryPartBuilderFactory;
		_httpServletRequest = httpServletRequest;
		_indexNameBuilder = indexNameBuilder;
		_language = language;
		_portletSharedSearchResponse = portletSharedSearchResponse;
		_queries = queries;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	//	_searcher = searcher;
		_searchEngineAdapter = searchEngineAdapter;
	//	_searchRequestBuilderFactory = searchRequestBuilderFactory;
	}
	
	public SearchMoreLikeThisPortletDisplayContext build() {
		SearchMoreLikeThisPortletDisplayContext searchMoreLikeThisPortletDisplayContext =
			new SearchMoreLikeThisPortletDisplayContext();
		
		SearchContainer<SearchMoreLikeThisEntryDisplayContext> searchContainer =
			search();
		
		searchMoreLikeThisPortletDisplayContext.setSearchContainer(
			searchContainer);
		
		return searchMoreLikeThisPortletDisplayContext;
	}
	
	protected SearchContainer<SearchMoreLikeThisEntryDisplayContext> search() {
		
		SearchContainer<SearchMoreLikeThisEntryDisplayContext> searchContainer = getSearchContainer();
		
		String uid = getUID();
		
		if (Validator.isBlank(uid)) {
			return searchContainer;
		}
		
//		SearchContext searchContext = getSearchContext();
		
		Optional<String> keywordsOptional =
			_portletSharedSearchResponse.getKeywordsOptional();
		
		ThemeDisplay themeDisplay = _portletSharedSearchResponse.getThemeDisplay(_renderRequest);
		
		String indexName = _indexNameBuilder.getIndexName(
			themeDisplay.getCompanyId());
		
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();
			
		searchSearchRequest.setIndexNames(indexName);
		searchSearchRequest.setQuery(getMoreLikeThisQuery(
			uid, keywordsOptional));
		
		searchSearchRequest.setFetchSource(true);
		
		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);		
		SearchHits searchHits = searchSearchResponse.getSearchHits();
		
//		SearchRequestBuilder searchRequestBuilder =
//			_searchRequestBuilderFactory.builder(
//				searchContext
//			).addComplexQueryPart(_complexQueryPartBuilderFactory.builder(			
//				).query(
//					getMoreLikeThisQuery(
//						uid, keywordsOptional)
//				).build());
//		
//			
//		SearchResponse searchResponse = _searcher.search(
//				searchRequestBuilder.build());
//		
//		SearchHits searchHits = searchResponse.getSearchHits();
		
		searchContainer.setResults(
			getSearchMoreLikeThisEntryDisplayContexts(searchHits.getSearchHits()));

		return searchContainer;
	}
	
	protected List<SearchMoreLikeThisEntryDisplayContext> 
		getSearchMoreLikeThisEntryDisplayContexts(
			List<SearchHit> searchHits) {
		
		return searchHits.stream(
			).map(
				this::buildDisplayContext
			).collect(
				Collectors.toList()
			);
	}
	
	protected SearchMoreLikeThisEntryDisplayContext buildDisplayContext(
			SearchHit searchHit) {

		String id = searchHit.getId();

		Document document = searchHit.getDocument();
		
		SearchMoreLikeThisEntryDisplayContext 
			searchMoreLikeThisEntryDisplayContext =
			 new SearchMoreLikeThisEntryDisplayContext();
		
		searchMoreLikeThisEntryDisplayContext.setId(id);		
		searchMoreLikeThisEntryDisplayContext.setDocument(document);
		
		return searchMoreLikeThisEntryDisplayContext;
		
	}

	protected String getUID() {
		Optional<String> uidOptional =
				_portletSharedSearchResponse.getParameter(Field.UID, _renderRequest);

		return uidOptional.orElse(StringPool.BLANK);

	}

	protected SearchContainer<SearchMoreLikeThisEntryDisplayContext> getSearchContainer() {
		
		String emptyResultMessage = "";
		
		PortletURL portletURL = _renderResponse.createRenderURL();

			SearchContainer<SearchMoreLikeThisEntryDisplayContext> searchContainer =
				new SearchContainer<>(
					_renderRequest, portletURL, null,
					emptyResultMessage);
		return searchContainer;
	}
	
	protected SearchContext getSearchContext() {
		  SearchSettings searchSettings =
                _portletSharedSearchResponse.getSearchSettings();

          return searchSettings.getSearchContext();
	}

	protected MoreLikeThisQuery getMoreLikeThisQuery(
			String uid, Optional<String> keywords) {
		
		SearchMoreLikeThisPortletPreferences searchMoreLikeThisPortletPreferences =
			new SearchMoreLikeThisPortletPreferencesImpl(
				_portletSharedSearchResponse.getPortletPreferences(_renderRequest));

		String fields = searchMoreLikeThisPortletPreferences.getFields();

		Optional<String> fieldOptional = SearchStringUtil.maybe(fields);

		MoreLikeThisQuery moreLikeThis = _queries.moreLikeThis(
				SearchStringUtil.splitAndUnquote(fieldOptional), 
				SearchStringUtil.splitAndUnquote(keywords));
		
		moreLikeThis.setMinTermFrequency(1);

		String indexName = searchMoreLikeThisPortletPreferences.getIndexName();
		String docType = searchMoreLikeThisPortletPreferences.getDocType();

		moreLikeThis.addDocumentIdentifier(_queries.documentIdentifier(indexName, docType, uid));

		return moreLikeThis;	
	}

	private final ComplexQueryPartBuilderFactory _complexQueryPartBuilderFactory;
	private final HttpServletRequest _httpServletRequest;
	private final IndexNameBuilder _indexNameBuilder;
	private final Language _language;
	private final PortletSharedSearchResponse _portletSharedSearchResponse;
	private final Queries _queries;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	//private final Searcher _searcher;
	private final SearchEngineAdapter _searchEngineAdapter;
	//private final SearchRequestBuilderFactory _searchRequestBuilderFactory;
}
