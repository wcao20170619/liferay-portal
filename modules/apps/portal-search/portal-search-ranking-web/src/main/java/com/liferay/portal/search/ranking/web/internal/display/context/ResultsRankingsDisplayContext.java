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

package com.liferay.portal.search.ranking.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.SafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.ranking.web.internal.request.SearchRankingRequest;
import com.liferay.portal.search.ranking.web.internal.request.SearchRankingResponse;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Kevin Tan
 */
public class ResultsRankingsDisplayContext {

	public ResultsRankingsDisplayContext(
		HttpServletRequest request, RenderRequest renderRequest,
		RenderResponse renderResponse, Language language, Searcher searcher, 
		SearchRequestBuilderFactory searchRequestBuilderFactory) {

		_request = request;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_searcher = searcher;
		_searchRequestBuilderFactory = searchRequestBuilderFactory;
		
		_themeDisplay = (ThemeDisplay)_request.getAttribute(
			WebKeys.THEME_DISPLAY);
	
		String keywords = _getKeywords();

		if (keywords == null) {
			_hits = null;
			_keywords = null;
			_searchContainer = null;
			_searchContext = null;

			return;
		}
		
		String emptyResultMessage = language.format(
			request, "no-results-were-found-that-matched-the-keywords-x",
			"<strong>" + HtmlUtil.getHtml().escape(keywords) + "</strong>", false);
	
		SearchContainer<Document> searchContainer = new SearchContainer<>(
			_renderRequest, _getPortletURL(), null, emptyResultMessage);

		SearchContext searchContext = SearchContextFactory.getInstance(request);

		searchContext.setGroupIds(null);

		Map<String, Serializable> attributes = searchContext.getAttributes();

		attributes.remove("groupId", "0");
		
		searchContext.setKeywords(_keywords);

		//searchContext.setEntryClassNames();
		
		SearchRankingRequest searchRequestImpl = new SearchRankingRequest(
				searchContext, getSearchContainer(),
				searcher, searchRequestBuilderFactory);

		

			SearchRankingResponse searchRankingResponse = searchRequestImpl.search();

			SearchResponse searchResponse = searchRankingResponse.getSearchResponse();

			_hits = searchResponse.withHitsGet(Function.identity());
			_searchContainer = searchContainer;
			_searchContext = searchContext;
	}
	

	public List<DropdownItem> getActionDropdownItems() {
		return new DropdownItemList() {
			{
				add(
					SafeConsumer.ignore(
						dropdownItem -> {
							dropdownItem.putData(
								"action", "deleteResultRankingsEntries");
							dropdownItem.setIcon("times");
							dropdownItem.setLabel(
								LanguageUtil.get(_request, "delete"));
							dropdownItem.setQuickAction(true);
						}));
			}
		};
	}

	public String getClearResultsURL() {
		PortletURL clearResultsURL = _getPortletURL();

		clearResultsURL.setParameter("keywords", StringPool.BLANK);

		return clearResultsURL.toString();
	}

	public CreationMenu getCreationMenu() {
		return new CreationMenu() {
			{
				addPrimaryDropdownItem(
					dropdownItem -> {
						dropdownItem.setHref(
							_renderResponse.createRenderURL(),
							"mvcRenderCommandName", "editResultsRankingsEntry",
							"redirect", PortalUtil.getCurrentURL(_request));
						dropdownItem.setLabel(
							LanguageUtil.get(_request, "new-ranking"));
					});
			}
		};
	}

	public String getDisplayStyle() {
		if (_displayStyle != null) {
			return _displayStyle;
		}

		_displayStyle = ParamUtil.getString(
			_renderRequest, "displayStyle", "list");

		return _displayStyle;
	}

	public List<DropdownItem> getFilterItemsDropdownItems() {
		return new DropdownItemList() {
			{
				addGroup(
					dropdownGroupItem -> {
						dropdownGroupItem.setDropdownItems(
							_getFilterNavigationDropdownItems());
						dropdownGroupItem.setLabel(
							LanguageUtil.get(_request, "filter-by-navigation"));
					});
				addGroup(
					dropdownGroupItem -> {
						dropdownGroupItem.setDropdownItems(
							_getOrderByDropdownItems());
						dropdownGroupItem.setLabel(
							LanguageUtil.get(_request, "order-by"));
					});
			}
		};
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = ParamUtil.getString(_request, "orderByType", "asc");

		return _orderByType;
	}

	public String getSearchActionURL() {
		PortletURL portletURL = _getPortletURL();

		return portletURL.toString();
	}

	public SearchContainer<Document> getSearchContainer() throws PortalException {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<Document> searchContainer = new SearchContainer(
			_renderRequest, _getPortletURL(), null, "there-are-no-entries");
		
		searchContainer.setId("resultRankingsEntries");
		searchContainer.setOrderByCol(_getOrderByCol());
		// searchContainer.setOrderByComparator(_getOrderByComparator());
		searchContainer.setOrderByType(getOrderByType());
		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	public String getSortingURL() {
		PortletURL sortingURL = _getPortletURL();

		sortingURL.setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc");

		return sortingURL.toString();
	}

	public int getTotalItems() throws PortalException {
		SearchContainer<?> searchContainer = getSearchContainer();

		return searchContainer.getTotal();
	}

	public boolean isDisabledManagementBar() throws PortalException {
		if (_hasResults()) {
			return false;
		}

		if (_isSearch()) {
			return false;
		}

		return true;
	}

	public Boolean isShowCreationMenu() {
		return true;
	}

	private List<DropdownItem> _getFilterNavigationDropdownItems() {
		return new DropdownItemList() {
			{
				add(
					dropdownItem -> {
						dropdownItem.setActive(true);
						dropdownItem.setHref(_renderResponse.createRenderURL());
						dropdownItem.setLabel(
							LanguageUtil.get(_request, "all"));
					});
			}
		};
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_request, "keywords");

		return _keywords;
	}

	private String _getOrderByCol() {
		if (_orderByCol != null) {
			return _orderByCol;
		}

		_orderByCol = ParamUtil.getString(
			_renderRequest, "orderByCol", "modified-date");

		return _orderByCol;
	}

	private List<DropdownItem> _getOrderByDropdownItems() {
		return new DropdownItemList() {
			{
				add(
					SafeConsumer.ignore(
						dropdownItem -> {
							dropdownItem.setActive(
								Objects.equals(
									_getOrderByCol(), "modified-date"));
							dropdownItem.setHref(
								_getPortletURL(), "orderByCol",
								"modified-date");
							dropdownItem.setLabel(
								LanguageUtil.get(_request, "modified-date"));
						}));
				add(
					SafeConsumer.ignore(
						dropdownItem -> {
							dropdownItem.setActive(
								Objects.equals(_getOrderByCol(), "name"));
							dropdownItem.setHref(
								_getPortletURL(), "orderByCol", "name");
							dropdownItem.setLabel(
								LanguageUtil.get(_request, "name"));
						}));
			}
		};
	}

	private PortletURL _getPortletURL() {
		PortletURL portletURL = _renderResponse.createRenderURL();

		portletURL.setParameter("mvcPath", "/view.jsp");

		String keywords = _getKeywords();

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		portletURL.setParameter("displayStyle", getDisplayStyle());
		portletURL.setParameter("orderByCol", _getOrderByCol());
		portletURL.setParameter("orderByType", getOrderByType());

		return portletURL;
	}

	private boolean _hasResults() throws PortalException {
		if (getTotalItems() > 0) {
			return true;
		}

		return false;
	}

	private boolean _isSearch() {
		if (Validator.isNotNull(_getKeywords())) {
			return true;
		}

		return false;
	}

	private String _displayStyle;
	private final Hits _hits;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final HttpServletRequest _request;
	private final Searcher _searcher;
	private SearchContainer<Document> _searchContainer;
	private final SearchContext _searchContext;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;
	private final ThemeDisplay _themeDisplay;

}