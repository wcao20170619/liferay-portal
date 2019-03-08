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

import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.highlight.HighlightField;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHitBuilder;

import java.util.Map;

/**
 * @author Wade Cao
 */
public class SearchHitBuilderImpl implements SearchHitBuilder {

	@Override
	public SearchHitBuilder addHighlightField(HighlightField highlightField) {
		Map<String, HighlightField> highlightFields =
			_searchHitImpl.getHighlightFields();

		highlightFields.put(highlightField.getName(), highlightField);

		return this;
	}

	@Override
	public SearchHitBuilder addSource(String name, Object value) {
		Map<String, Object> sourceMap = _searchHitImpl.getSourceMap();

		sourceMap.put(name, value);

		return this;
	}

	@Override
	public SearchHit build() {
		return new SearchHitImpl(_searchHitImpl);
	}

	@Override
	public SearchHitBuilder document(Document document) {
		_searchHitImpl.setDocument(document);

		return this;
	}

	@Override
	public SearchHitBuilder explanation(String explanation) {
		_searchHitImpl.setExplanation(explanation);

		return this;
	}

	@Override
	public SearchHitBuilder id(String id) {
		_searchHitImpl.setId(id);

		return this;
	}

	@Override
	public SearchHitBuilder matchedQueries(String[] matchedQueries) {
		_searchHitImpl.setMatchedQueries(matchedQueries);

		return this;
	}

	@Override
	public SearchHitBuilder score(float score) {
		_searchHitImpl.setScore(score);

		return this;
	}

	@Override
	public SearchHitBuilder version(long version) {
		_searchHitImpl.setVersion(version);

		return this;
	}

	private final SearchHitImpl _searchHitImpl = new SearchHitImpl();

}