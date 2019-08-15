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

package com.liferay.portal.search.web.internal.util;

import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.internal.morelikethis.filter.portlet.MoreLikeThisFilterPortletPreferences;
import com.liferay.portal.search.web.internal.morelikethis.filter.portlet.MoreLikeThisFilterPortletPreferencesImpl;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.RenderRequest;

/**
 * @author Wade Cao
 */
public class RecommenderHelper {

	public RecommenderHelper(
		PortletSharedSearchResponse portletSharedSearchResponse) {

		_portletSharedSearchResponse = portletSharedSearchResponse;
	}

	public List<Document> getMoreLikeThisSearchDocuments(
		RenderRequest renderRequest) {

		SearchResponse searchResponse = getMoreLikeThisSearchResponse(
			renderRequest);

		if (searchResponse == null) {
			return null;
		}

		Stream<Document> stream = searchResponse.getDocumentsStream();

		return stream.collect(Collectors.toList());
	}

	public SearchResponse getMoreLikeThisSearchResponse(
		RenderRequest renderRequest) {

		MoreLikeThisFilterPortletPreferences
			searchMoreLikeThisPortletPreferences =
				new MoreLikeThisFilterPortletPreferencesImpl(
					_portletSharedSearchResponse.getPortletPreferences(
						renderRequest));

		return _portletSharedSearchResponse.getFederatedSearchResponse(
			searchMoreLikeThisPortletPreferences.
				getFederatedSearchKeyOptional());
	}

	private final PortletSharedSearchResponse _portletSharedSearchResponse;

}