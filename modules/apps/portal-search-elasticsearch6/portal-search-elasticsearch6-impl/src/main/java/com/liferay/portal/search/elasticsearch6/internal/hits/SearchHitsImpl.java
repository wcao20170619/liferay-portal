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

package com.liferay.portal.search.elasticsearch6.internal.hits;

import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;

import java.util.ArrayList;
import java.util.Collections;
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
		return Collections.unmodifiableList(_searchHits);
	}

	@Override
	public long getSearchTime() {
		return _searchTime;
	}

	@Override
	public long getTotalHits() {
		return _totalHits;
	}

	public static class Builder implements SearchHits.Builder {

		@Override
		public Builder addSearchHit(SearchHit searchHit) {
			_searchHitsImpl._searchHits.add(searchHit);

			return this;
		}

		@Override
		public SearchHits build() {
			return new SearchHitsImpl(_searchHitsImpl);
		}

		@Override
		public Builder maxScore(float maxScore) {
			_searchHitsImpl._maxScore = maxScore;

			return this;
		}

		@Override
		public Builder searchTime(long searchTime) {
			_searchHitsImpl._searchTime = searchTime;

			return this;
		}

		@Override
		public Builder totalHits(long totalHits) {
			_searchHitsImpl._totalHits = totalHits;

			return this;
		}

		private final SearchHitsImpl _searchHitsImpl = new SearchHitsImpl();

	}

	protected SearchHitsImpl() {
	}

	protected SearchHitsImpl(SearchHitsImpl searchHitsImpl) {
		_maxScore = searchHitsImpl._maxScore;

		_searchHits.addAll(searchHitsImpl._searchHits);

		_searchTime = searchHitsImpl._searchTime;
		_totalHits = searchHitsImpl._totalHits;
	}

	private float _maxScore;
	private final List<SearchHit> _searchHits = new ArrayList<>();
	private long _searchTime;
	private long _totalHits;

}