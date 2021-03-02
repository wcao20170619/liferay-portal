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

package com.liferay.portal.search.tuning.blueprints.admin.web.internal.display.context;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.tuning.blueprints.admin.web.internal.constants.BlueprintsAdminMVCCommandNames;
import com.liferay.portal.search.tuning.blueprints.comparator.BlueprintModifiedDateComparator;
import com.liferay.portal.search.tuning.blueprints.comparator.BlueprintTitleComparator;
import com.liferay.portal.search.tuning.blueprints.model.Blueprint;
import com.liferay.portal.search.tuning.blueprints.service.BlueprintServiceUtil;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.PortletException;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Petteri Karttunen
 * @author Kevin Tan
 */
public class SelectBlueprintDisplayContext {

	public SelectBlueprintDisplayContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, int blueprintType) {

		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_httpServletRequest = _liferayPortletRequest.getHttpServletRequest();

		_blueprintType = blueprintType;
	}

	public String getEventName() {
		if (_eventName != null) {
			return _eventName;
		}

		_eventName = ParamUtil.getString(
			_httpServletRequest, "eventName",
			_liferayPortletResponse.getNamespace() + "selectBlueprint");

		return _eventName;
	}

	public SearchContainer<Blueprint> getSearchContainer()
		throws PortalException, PortletException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletURL portletURL = _liferayPortletResponse.createRenderURL();

		portletURL.setParameter("eventName", getEventName());
		portletURL.setParameter(
			"mvcRenderCommandName",
			BlueprintsAdminMVCCommandNames.SELECT_BLUEPRINT);

		SearchContainer<Blueprint> entriesSearchContainer =
			new SearchContainer<>(
				_liferayPortletRequest,
				PortletURLUtil.clone(portletURL, _liferayPortletResponse), null,
				"no-entries-were-found");

		String orderByCol = ParamUtil.getString(
			_httpServletRequest, "orderByCol", "title");
		String orderByType = ParamUtil.getString(
			_httpServletRequest, "orderByType", "asc");

		entriesSearchContainer.setOrderByCol(orderByCol);
		entriesSearchContainer.setOrderByComparator(
			_getOrderByComparator(
				themeDisplay.getLocale(), orderByCol, orderByType));
		entriesSearchContainer.setOrderByType(orderByType);

		_populateResults(entriesSearchContainer);

		return entriesSearchContainer;
	}

	private static OrderByComparator<Blueprint> _getOrderByComparator(
		Locale locale, String orderByCol, String orderByType) {

		boolean orderByAsc = true;

		if (orderByType.equals("desc")) {
			orderByAsc = false;
		}

		OrderByComparator<Blueprint> orderByComparator = null;

		if (orderByCol.equals("modified-date")) {
			orderByComparator = new BlueprintModifiedDateComparator(orderByAsc);
		}
		else {
			orderByComparator = new BlueprintTitleComparator(
				orderByAsc, locale);
		}

		return orderByComparator;
	}

	private void _populateResults(SearchContainer<Blueprint> searchContainer)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<Blueprint> entriesResults = null;

		long groupId = themeDisplay.getCompanyGroupId();

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (Validator.isNull(keywords)) {
			searchContainer.setTotal(
				BlueprintServiceUtil.getGroupBlueprintsCount(
					groupId, WorkflowConstants.STATUS_ANY, _blueprintType));

			entriesResults = BlueprintServiceUtil.getGroupBlueprints(
				groupId, WorkflowConstants.STATUS_ANY, _blueprintType,
				searchContainer.getStart(), searchContainer.getEnd(),
				searchContainer.getOrderByComparator());
		}
		else {
			Indexer<Blueprint> indexer = IndexerRegistryUtil.getIndexer(
				Blueprint.class);

			SearchContext searchContext = SearchContextFactory.getInstance(
				_httpServletRequest);

			searchContext.setAttribute(
				Field.STATUS, WorkflowConstants.STATUS_ANY);
			searchContext.setAttribute(Field.TYPE, _blueprintType);
			searchContext.setEnd(searchContainer.getEnd());
			searchContext.setGroupIds(new long[] {groupId});
			searchContext.setKeywords(keywords);
			searchContext.setStart(searchContainer.getStart());

			String orderByCol = ParamUtil.getString(
				_httpServletRequest, "orderByCol", "title");
			String orderByType = ParamUtil.getString(
				_httpServletRequest, "orderByType", "asc");

			Sort sort = null;

			boolean orderByAsc = false;

			if (Objects.equals(orderByType, "asc")) {
				orderByAsc = true;
			}

			if (Objects.equals(orderByCol, "modified-date")) {
				sort = new Sort(
					Field.MODIFIED_DATE, Sort.LONG_TYPE, !orderByAsc);
			}
			else {
				sort = new Sort(orderByCol, !orderByAsc);
			}

			searchContext.setSorts(sort);

			Hits hits = indexer.search(searchContext);

			searchContainer.setTotal(hits.getLength());

			List<SearchResult> searchResults =
				SearchResultUtil.getSearchResults(
					hits, LocaleUtil.getDefault());

			Stream<SearchResult> stream = searchResults.stream();

			entriesResults = stream.map(
				this::_toBlueprintOptional
			).filter(
				Optional::isPresent
			).map(
				Optional::get
			).collect(
				Collectors.toList()
			);
		}

		searchContainer.setResults(entriesResults);
	}

	private Optional<Blueprint> _toBlueprintOptional(
		SearchResult searchResult) {

		try {
			return Optional.of(
				BlueprintServiceUtil.getBlueprint(searchResult.getClassPK()));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Search index is stale and contains a Blueprint entry " +
						searchResult.getClassPK());
			}

			return Optional.empty();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SelectBlueprintDisplayContext.class);

	private final int _blueprintType;
	private String _eventName;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;

}