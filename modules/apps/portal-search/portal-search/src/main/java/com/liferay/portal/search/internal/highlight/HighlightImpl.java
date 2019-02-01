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

package com.liferay.portal.search.internal.highlight;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.search.highlight.FieldConfig;
import com.liferay.portal.search.highlight.Highlight;
import com.liferay.portal.search.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
@ProviderType
public class HighlightImpl implements Highlight {

	@Override
	public void addFieldConfig(FieldConfig fieldConfig) {
		_fieldConfigs.add(fieldConfig);
	}

	@Override
	public void addFields(String... fields) {
		Stream.of(
			fields
		).forEach(
			field -> addFieldConfig(new FieldConfigImpl(field))
		);
	}

	@Override
	public void addPostTags(String... postTags) {
		Collections.addAll(_postTags, postTags);
	}

	@Override
	public void addPreTags(String... preTags) {
		Collections.addAll(_preTags, preTags);
	}

	@Override
	public List<FieldConfig> getFieldConfigs() {
		return Collections.unmodifiableList(_fieldConfigs);
	}

	@Override
	public Boolean getForceSource() {
		return _forceSource;
	}

	@Override
	public String getFragmenter() {
		return _fragmenter;
	}

	@Override
	public Integer getFragmentSize() {
		return _fragmentSize;
	}

	@Override
	public String getHighlighterType() {
		return _highlighterType;
	}

	@Override
	public Boolean getHighlightFilter() {
		return _highlightFilter;
	}

	@Override
	public Query getHighlightQuery() {
		return _highlightQuery;
	}

	@Override
	public Integer getNumOfFragments() {
		return _numOfFragments;
	}

	@Override
	public List<String> getPostTags() {
		return Collections.unmodifiableList(_postTags);
	}

	@Override
	public List<String> getPreTags() {
		return Collections.unmodifiableList(_preTags);
	}

	@Override
	public Boolean getRequireFieldMatch() {
		return _requireFieldMatch;
	}

	@Override
	public void setForceSource(Boolean forceSource) {
		_forceSource = forceSource;
	}

	@Override
	public void setFragmenter(String fragmenter) {
		_fragmenter = fragmenter;
	}

	@Override
	public void setFragmentSize(Integer fragmentSize) {
		_fragmentSize = fragmentSize;
	}

	@Override
	public void setHighlighterType(String highlighterType) {
		_highlighterType = highlighterType;
	}

	@Override
	public void setHighlightFilter(Boolean highlightFilter) {
		_highlightFilter = highlightFilter;
	}

	@Override
	public void setHighlightQuery(Query highlightQuery) {
		_highlightQuery = highlightQuery;
	}

	@Override
	public void setNumOfFragments(Integer numOfFragments) {
		_numOfFragments = numOfFragments;
	}

	@Override
	public void setRequireFieldMatch(Boolean requireFieldMatch) {
		_requireFieldMatch = requireFieldMatch;
	}

	private final List<FieldConfig> _fieldConfigs = new ArrayList<>();
	private Boolean _forceSource;
	private String _fragmenter;
	private Integer _fragmentSize;
	private String _highlighterType;
	private Boolean _highlightFilter;
	private Query _highlightQuery;
	private Integer _numOfFragments;
	private final List<String> _postTags = new ArrayList<>();
	private final List<String> _preTags = new ArrayList<>();
	private Boolean _requireFieldMatch;

}