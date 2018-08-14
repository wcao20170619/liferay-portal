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

package com.liferay.portal.search.web.internal.search.request;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.search.SearchRequestExecutor;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.web.search.request.FederatedSearcher;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(immediate = true, service = FederatedSearcher.class)
public class FederatedSearcherImplTwo implements FederatedSearcher {

	@Override
	public Hits getHits(SearchContext searchContext) {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames(new String[]{"elastic_federated"});

		String keywords = searchContext.getKeywords();

		if (Validator.isBlank(keywords)) {
			keywords = "federated";
		}

		Query federatedTitleQuery = new MatchQuery(
			"federatedTitle", keywords);

		Query federatedContentQuery = new MatchQuery(
			"federatedContent", keywords);

		BooleanQuery booleanQuery = new BooleanQueryImpl();
		Query hitsQuery;

		try {
			booleanQuery.add(federatedTitleQuery, BooleanClauseOccur.SHOULD);
			booleanQuery.add(federatedContentQuery, BooleanClauseOccur.SHOULD);

			searchSearchRequest.setQuery(booleanQuery);
			hitsQuery = booleanQuery;
		}
		catch (ParseException pe) {
			searchSearchRequest.setQuery(federatedTitleQuery);
			hitsQuery = federatedTitleQuery;
		}

		searchSearchRequest.setSize(10);

		SearchSearchResponse searchSearchResponse =
			searchSearchRequest.accept(searchRequestExecutor);

		Hits hits = searchSearchResponse.getHits();

		hits.setQuery(hitsQuery);

		return hits;
	}

	@Override
	public String getSource() {
		return "elastic_federated";
	}

	@Reference
	protected SearchRequestExecutor searchRequestExecutor;
}