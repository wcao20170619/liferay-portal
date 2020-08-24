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

package com.liferay.portal.search.tuning.blueprints.searchrequest.contributor.internal.searcher;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.tuning.blueprints.engine.context.SearchRequestContext;
import com.liferay.portal.search.tuning.blueprints.engine.searchrequest.SearchRequestData;
import com.liferay.portal.search.tuning.blueprints.engine.util.SearchClientHelper;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = BlueprintsSearchRequestLookup.class)
public class BlueprintsSearchRequestLookup {

	public SearchRequest lookup(
			int searchConfigurationId, SearchContext searchContext)
		throws Exception {

		SearchRequestContext searchRequestContext =
			_searchClientHelper.getSearchRequestContext(
				searchContext, searchConfigurationId);

		SearchRequestData searchRequestData =
			_searchClientHelper.getSearchRequestData(searchRequestContext);

		return _searchRequestBuilderFactory.builder(
		).companyId(
			searchRequestContext.getCompanyId()
		).query(
			searchRequestData.getQuery()
		).indexes(
			searchRequestContext.getIndexNames()
		).build();
	}

	@Reference
	private SearchClientHelper _searchClientHelper;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}
