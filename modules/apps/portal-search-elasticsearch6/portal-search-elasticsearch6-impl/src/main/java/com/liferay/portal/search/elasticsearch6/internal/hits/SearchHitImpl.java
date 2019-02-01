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

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.highlight.HighlightField;
import com.liferay.portal.search.hits.SearchHit;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Michael C. Han
 */
@ProviderType
public class SearchHitImpl implements SearchHit {

	public SearchHitImpl() {
	}

	public SearchHitImpl(SearchHitImpl searchHitImpl) {
		_document = searchHitImpl._document;
		_explanation = searchHitImpl._explanation;

		_highlightFields.putAll(searchHitImpl.getHighlightFields());

		_id = searchHitImpl._id;
		_matchedQueries = searchHitImpl._matchedQueries;
		_score = searchHitImpl._score;
		_sourceMap.putAll(searchHitImpl._sourceMap);
		_version = searchHitImpl._version;
	}

	@Override
	public Document getDocument() {
		return _document;
	}

	@Override
	public String getExplanation() {
		return _explanation;
	}

	@Override
	public Map<String, HighlightField> getHighlightFields() {
		return Collections.unmodifiableMap(_highlightFields);
	}

	@Override
	public String getId() {
		return _id;
	}

	@Override
	public String[] getMatchedQueries() {
		return _matchedQueries;
	}

	@Override
	public float getScore() {
		return _score;
	}

	@Override
	public Map<String, Object> getSourceMap() {
		return Collections.unmodifiableMap(_sourceMap);
	}

	@Override
	public long getVersion() {
		return _version;
	}

	public static class Builder implements SearchHit.Builder {

		@Override
		public Builder addHighlightField(HighlightField highlightField) {
			_searchHitImpl._highlightFields.put(
				highlightField.getName(), highlightField);

			return this;
		}

		@Override
		public Builder addSource(String name, Object value) {
			_searchHitImpl._sourceMap.put(name, value);

			return this;
		}

		@Override
		public SearchHit build() {
			return new SearchHitImpl(_searchHitImpl);
		}

		@Override
		public Builder document(Document document) {
			_searchHitImpl._document = document;

			return this;
		}

		@Override
		public Builder explanation(String explanation) {
			_searchHitImpl._explanation = explanation;

			return this;
		}

		@Override
		public Builder id(String id) {
			_searchHitImpl._id = id;

			return this;
		}

		@Override
		public Builder matchedQueries(String[] matchedQueries) {
			_searchHitImpl._matchedQueries = matchedQueries;

			return this;
		}

		@Override
		public Builder score(float score) {
			_searchHitImpl._score = score;

			return this;
		}

		@Override
		public Builder version(long version) {
			_searchHitImpl._version = version;

			return this;
		}

		private final SearchHitImpl _searchHitImpl = new SearchHitImpl();

	}

	private Document _document;
	private String _explanation;
	private final Map<String, HighlightField> _highlightFields =
		new LinkedHashMap<>();
	private String _id;
	private String[] _matchedQueries;
	private float _score;
	private final Map<String, Object> _sourceMap = new LinkedHashMap<>();
	private long _version;

}