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

package com.liferay.portal.search.related.results.web.internal.display.context;

import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.related.results.web.internal.configuration.SearchRelatedResultsPortletInstanceConfiguration;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Kevin Tan
 */
public class SearchRelatedResultsDisplayContext {

	public SearchRelatedResultsDisplayContext(
			HttpServletRequest httpServletRequest)
		throws ConfigurationException {

		_httpServletRequest = httpServletRequest;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		_searchRelatedResultsPortletInstanceConfiguration =
			portletDisplay.getPortletInstanceConfiguration(
				SearchRelatedResultsPortletInstanceConfiguration.class);
	}

	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId != 0) {
			return _displayStyleGroupId;
		}

		_displayStyleGroupId =
			_searchRelatedResultsPortletInstanceConfiguration.
				displayStyleGroupId();

		if (_displayStyleGroupId <= 0) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_displayStyleGroupId = themeDisplay.getScopeGroupId();
		}

		return _displayStyleGroupId;
	}

	public List<Document> getDocuments() {
		return _documents;
	}

	public List<SearchRelatedResultsDocumentDisplayContext>
		getSearchRelatedResultsDocumentDisplayContexts() {

		return _searchRelatedResultsDocumentDisplayContexts;
	}

	public SearchRelatedResultsPortletInstanceConfiguration
		getSearchRelatedResultsPortletInstanceConfiguration() {

		return _searchRelatedResultsPortletInstanceConfiguration;
	}

	public int getTotalHits() {
		return _totalHits;
	}

	public void setDocuments(List<Document> documents) {
		_documents = documents;
	}

	public void setSearchRelatedResultsDocumentDisplayContexts(
		List<SearchRelatedResultsDocumentDisplayContext>
			searchRelatedResultsDocumentDisplayContexts) {

		_searchRelatedResultsDocumentDisplayContexts =
			searchRelatedResultsDocumentDisplayContexts;
	}

	public void setTotalHits(int totalHits) {
		_totalHits = totalHits;
	}

	private long _displayStyleGroupId;
	private List<Document> _documents;
	private final HttpServletRequest _httpServletRequest;
	private List<SearchRelatedResultsDocumentDisplayContext>
		_searchRelatedResultsDocumentDisplayContexts;
	private final SearchRelatedResultsPortletInstanceConfiguration
		_searchRelatedResultsPortletInstanceConfiguration;
	private int _totalHits;

}