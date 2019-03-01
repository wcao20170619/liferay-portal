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

package com.liferay.portal.search.internal.hits;

import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.hits.SearchHitsBuilder;

import java.util.List;

/**
 * @author Wade Cao
 */
public class SearchHitsBuilderImpl implements SearchHitsBuilder {

	@Override
	public SearchHitsBuilder addSearchHit(SearchHit searchHit) {
		List<SearchHit> searchHits = _searchHitsImpl.getSearchHits();

		searchHits.add(searchHit);

		return this;
	}

	@Override
	public SearchHits build() {
		return new SearchHitsImpl(_searchHitsImpl);
	}

	@Override
	public SearchHitsBuilder maxScore(float maxScore) {
		_searchHitsImpl.setMaxScore(maxScore);

		return this;
	}

	@Override
	public SearchHitsBuilder searchTime(long searchTime) {
		_searchHitsImpl.setSearchTime(searchTime);

		return this;
	}

	@Override
	public SearchHitsBuilder totalHits(long totalHits) {
		_searchHitsImpl.setTotalHits(totalHits);

		return this;
	}

	private final SearchHitsImpl _searchHitsImpl = new SearchHitsImpl();

}