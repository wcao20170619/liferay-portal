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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael C. Han
 */
public class SearchHitsImpl implements SearchHits {

	@Override
	public float getMaxScore() {
		return _maxScore;
	}

	@Override
	public List<SearchHit> getSearchHits() {
		return _searchHits;
	}

	@Override
	public long getSearchTime() {
		return _searchTime;
	}

	@Override
	public long getTotalHits() {
		return _totalHits;
	}

	protected SearchHitsImpl() {
	}

	protected SearchHitsImpl(SearchHitsImpl searchHitsImpl) {
		_maxScore = searchHitsImpl._maxScore;

		_searchHits.addAll(searchHitsImpl._searchHits);

		_searchTime = searchHitsImpl._searchTime;
		_totalHits = searchHitsImpl._totalHits;
	}

	protected void setMaxScore(float maxScore) {
		_maxScore = maxScore;
	}

	protected void setSearchTime(long searchTime) {
		_searchTime = searchTime;
	}

	protected void setTotalHits(long totalHits) {
		_totalHits = totalHits;
	}

	private static final long serialVersionUID = 1L;

	private float _maxScore;
	private final List<SearchHit> _searchHits = new ArrayList<>();
	private long _searchTime;
	private long _totalHits;

}