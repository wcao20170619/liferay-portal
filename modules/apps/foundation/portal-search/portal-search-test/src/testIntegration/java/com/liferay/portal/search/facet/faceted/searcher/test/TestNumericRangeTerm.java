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
package com.liferay.portal.search.facet.faceted.searcher.test;

import com.liferay.portal.kernel.search.BaseSearcher;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Wade Cao
 */
public class TestNumericRangeTerm
	extends BaseSearcher implements FacetedSearcher {

	public TestNumericRangeTerm() {
	}

	@Override
	protected BooleanQuery createFullQuery(
			BooleanFilter fullQueryBooleanFilter, SearchContext searchContext)
		throws Exception {

		BooleanQuery fullBooleanQuery = super.createFullQuery(
			fullQueryBooleanFilter, searchContext);

		String numericRangeTerm = (String)searchContext.getAttribute(
			"numericRangeTerm");

		if (numericRangeTerm != null) {
			_addNumericRangeTerm(
				fullBooleanQuery, searchContext, numericRangeTerm);
		}

		return fullBooleanQuery;
	}

	private void _addNumericRangeTerm(
			BooleanQuery fullBooleanQuery, SearchContext searchContext,
			String numericRangeTerm)
		throws Exception {

		BooleanQuery searchQuery = new BooleanQueryImpl();

		long startVal = GetterUtil.getLong(
			searchContext.getAttribute("startVal"));
		long endVal = GetterUtil.getLong(searchContext.getAttribute("endVal"));

		searchQuery.addNumericRangeTerm(numericRangeTerm, startVal, endVal);

		fullBooleanQuery.add(searchQuery, BooleanClauseOccur.MUST);
	}

}