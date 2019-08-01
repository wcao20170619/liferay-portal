package com.liferay.portal.search.web.internal.search.morelikethis.display.context;

import java.util.List;

import java.util.stream.Stream;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.summary.SummaryBuilderFactory;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactory;
import com.liferay.portal.search.web.internal.display.context.SearchResultPreferences;
import com.liferay.portal.search.web.internal.result.display.builder.AssetRendererFactoryLookup;
import com.liferay.portal.search.web.internal.result.display.builder.SearchResultSummaryDisplayBuilder;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext;
import com.liferay.portal.search.web.search.result.SearchResultImageContributor;

public class SearchMoreLikeThisResultsBuilder {
	
	public JSONObject build(SearchHits searchHits) {

		JSONObject resultsObject = JSONFactoryUtil.createJSONObject();

		resultsObject.put(
			"items", createItemsArray(searchHits));

		resultsObject.put("meta", createMetaObject(searchHits));

		resultsObject.put("paging", createPagingObject(searchHits));

		return resultsObject;
	}
	
	protected JSONArray createItemsArray(SearchHits searchHits) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		List<SearchHit> searchHitList = searchHits.getSearchHits();

		searchHitList.forEach(searchHit->{
			Document document = searchHit.getDocument();
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
			
			SearchResultSummaryDisplayContext searchResultSummaryDisplayContext = 
				getSearchResultSummaryDisplayContext(document);
			
			jsonObject.put(
				"title", searchResultSummaryDisplayContext.getTitle());
			
			jsonObject.put(
				"date",
				searchResultSummaryDisplayContext.getCreationDateString());

			jsonObject.put(
				"description", searchResultSummaryDisplayContext.getContent());
			
			jsonObject.put(
				"type", searchResultSummaryDisplayContext.getClassName().toLowerCase());
				
			jsonObject.put("link", searchResultSummaryDisplayContext.getViewURL());
			
			jsonObject.put("metadata", "");
			
			jsonObject.put("redirect", "");
			
			jsonArray.put(jsonObject);
		
		});
		
		return jsonArray;
	}
	
	protected JSONObject createMetaObject(SearchHits searchHits) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		
		jsonObject.put("originalQueryTerms", "");
	
		jsonObject.put("queryTerms", "");

		jsonObject.put(
			"executionTime", String.format("%.3f", searchHits.getSearchTime()));

		jsonObject.put("querySuggestions", "");

		jsonObject.put("start", "");

		jsonObject.put("totalPages", "");

		jsonObject.put("totalHits", searchHits.getTotalHits());

		return jsonObject;
	}

	protected SearchResultSummaryDisplayContext getSearchResultSummaryDisplayContext(
			Document document) {
		
		SearchResultSummaryDisplayBuilder searchResultSummaryDisplayBuilder =
				new SearchResultSummaryDisplayBuilder();

		searchResultSummaryDisplayBuilder.setAssetEntryLocalService(
			_assetEntryLocalService
		).setAssetRendererFactoryLookup(
			_assetRendererFactoryLookup
		).setCurrentURL(
			_portal.getCurrentURL(_renderRequest)
		).setDocument(
			document
		).setDocumentBuilderFactory(
			null
		).setFastDateFormatFactory(
			_fastDateFormatFactory
		).setHighlightEnabled(
			_highlightEnabled
		).setImageRequested(
			true
		).setIndexerRegistry(
			_indexerRegistry
		).setLanguage(
			_language
		).setLocale(
			_themeDisplay.getLocale()
		).setMoreLikeThisEnabled(
			_moreLikeThisEnabled	
		).setPortletURLFactory(
			_portletURLFactory
		).setRenderRequest(
			_renderRequest
		).setRenderResponse(
			_renderResponse
		).setRequest(
			getHttpServletRequest(_renderRequest)
			).setResourceActions(
				_resourceActions
			).setSearchResultImageContributorsStream(
				_searchResultImageContributorsStream
			).setSearchResultPreferences(
				_searchResultPreferences
			).setSummaryBuilderFactory(
				_summaryBuilderFactory
			).setThemeDisplay(
				_themeDisplay
			);
	
		SearchResultSummaryDisplayContext searchResultSummaryDisplayContext;
		try {
			searchResultSummaryDisplayContext = searchResultSummaryDisplayBuilder.build();
		} catch (Exception e) {
			return null;
		}
		
		return searchResultSummaryDisplayContext;
	}
	
	protected JSONObject createPagingObject(SearchHits searchHits) {

		JSONObject pagingObject = JSONFactoryUtil.createJSONObject();

		long totalHits = searchHits.getTotalHits();

		if (totalHits == 0) {
			return pagingObject;
		}

		int pagesToShow = 10;
		int pageSize = 10; //getPageSize();
		int start = 0; //getStart(searchHits);
		int pageCount = 10; //getPageCount(searchHits);

		int currentPage = ((int) Math.floor((start + 1) / pageSize)) + 1;
		pagingObject.put("currentPage", currentPage);

		int loopStart = 1;
		
		int loopEnd = pagesToShow;

		if (currentPage > pagesToShow) {
			loopStart = currentPage - (pagesToShow / 2);
			loopEnd = currentPage + (pagesToShow / 2);
		}

		if (loopEnd > pageCount) {
			loopEnd = pageCount;
		}

		int prevStart = -1;

		if (currentPage > pagesToShow) {
			prevStart = (loopStart - 2) * pageSize;
			pagingObject.put("prevStart", prevStart);
		}

		int nextStart = -1;

		if (pageCount > loopEnd) {
			nextStart = loopEnd * pageSize;
			pagingObject.put("nextStart", nextStart);
		}

		// Create paging set.

		JSONArray pageArray = JSONFactoryUtil.createJSONArray();

		for (int i = loopStart; i <= loopEnd; i++) {

			JSONObject pageObject = JSONFactoryUtil.createJSONObject();

			pageObject.put("number", i);
			pageObject.put("start", (i - 1) * pageSize);

			if (i == currentPage) {
				pageObject.put("selected", true);
			}
			pageArray.put(pageObject);
		}

		pagingObject.put("pages", pageArray);

		return pagingObject;
	}
	
	protected HttpServletRequest getHttpServletRequest(
			RenderRequest renderRequest) {

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(renderRequest);

		return liferayPortletRequest.getHttpServletRequest();
	}
		
//	private boolean _abridged;
	private AssetEntryLocalService _assetEntryLocalService;
	private AssetRendererFactoryLookup _assetRendererFactoryLookup;
//	private String _currentURL;
//	private Document _document;
//	private DocumentBuilderFactory _documentBuilderFactory;
	private FastDateFormatFactory _fastDateFormatFactory;
	
	public void assetEntryLocalService(
			AssetEntryLocalService assetEntryLocalService) {
		_assetEntryLocalService = assetEntryLocalService;
	}

	public void assetRendererFactoryLookup(
			AssetRendererFactoryLookup assetRendererFactoryLookup) {
		_assetRendererFactoryLookup = assetRendererFactoryLookup;
	}

	public void fastDateFormatFactory(
			FastDateFormatFactory fastDateFormatFactory) {
		_fastDateFormatFactory = fastDateFormatFactory;
	}

	public void highlightEnabled(boolean highlightEnabled) {
		_highlightEnabled = highlightEnabled;
	}

	public void indexerRegistry(IndexerRegistry indexerRegistry) {
		_indexerRegistry = indexerRegistry;
	}

	public void language(Language language) {
		_language = language;
	}

	public void moreLikeThisEnabled(boolean moreLikeThisEnabled) {
		_moreLikeThisEnabled = moreLikeThisEnabled;
	}

	public void portletURLFactory(PortletURLFactory portletURLFactory) {
		_portletURLFactory = portletURLFactory;
	}

	public void renderRequest(RenderRequest renderRequest) {
		_renderRequest = renderRequest;
	}

	public void renderResponse(RenderResponse renderResponse) {
		_renderResponse = renderResponse;
	}

	public void resourceActions(ResourceActions resourceActions) {
		_resourceActions = resourceActions;
	}

	public void searchResultImageContributorsStream(
			Stream<SearchResultImageContributor> searchResultImageContributorsStream) {
		_searchResultImageContributorsStream = searchResultImageContributorsStream;
	}

	public void searchResultPreferences(
			SearchResultPreferences searchResultPreferences) {
		this._searchResultPreferences = searchResultPreferences;
	}

	public void summaryBuilderFactory(
			SummaryBuilderFactory summaryBuilderFactory) {
		_summaryBuilderFactory = summaryBuilderFactory;
	}

	public void themeDisplay(ThemeDisplay themeDisplay) {
		_themeDisplay = themeDisplay;
	}

	public void portal(Portal portal) {
		_portal = portal;
	}

	private boolean _highlightEnabled;
//	private HttpServletRequest _httpServletRequest;
//	private boolean _imageRequested;
	private IndexerRegistry _indexerRegistry;
	private Language _language;
//	private com.liferay.portal.kernel.search.Document _legacyDocument;
//	private Locale _locale;
	private boolean _moreLikeThisEnabled;
	private PortletURLFactory _portletURLFactory;
	private RenderRequest _renderRequest;
	private RenderResponse _renderResponse;
	private ResourceActions _resourceActions;
	private Stream<SearchResultImageContributor>
		_searchResultImageContributorsStream = Stream.empty();
	private SearchResultPreferences _searchResultPreferences;
//	private SearchResultViewURLSupplier _searchResultViewURLSupplier;
	private SummaryBuilderFactory _summaryBuilderFactory;
	private ThemeDisplay _themeDisplay;
	private Portal _portal;

}
