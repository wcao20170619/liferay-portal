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

import com.liferay.portal.search.highlight.FieldConfig;
import com.liferay.portal.search.highlight.Highlight;
import com.liferay.portal.search.highlight.HighlightField;
import com.liferay.portal.search.highlight.Highlights;
import com.liferay.portal.search.query.Query;

import java.util.List;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = Highlights.class)
public class HighlightsImpl implements Highlights {

	@Override
	public FieldConfig fieldConfig(String field) {
		FieldConfig.Builder builder = getFieldConfigBuilder();

		builder.field(field);

		return builder.build();
	}

	public FieldConfig.Builder getFieldConfigBuilder() {
		FieldConfig.Builder builder = new FieldConfig.Builder() {

			@Override
			public FieldConfig build() {
				return new FieldConfigImpl(_fieldConfigImpl);
			}

			@Override
			public void field(String field) {
				_fieldConfigImpl.setField(field);
			}

			@Override
			public void fragmentOffset(Integer fragmentOffset) {
				_fieldConfigImpl.setFragmentOffset(fragmentOffset);
			}

			@Override
			public void fragmentSize(Integer fragmentSize) {
				_fieldConfigImpl.setFragmentSize(fragmentSize);
			}

			@Override
			public void numFragments(Integer numFragments) {
				_fieldConfigImpl.setNumFragments(numFragments);
			}

			private final FieldConfigImpl _fieldConfigImpl =
				new FieldConfigImpl();

		};

		return builder;
	}

	public Highlight.Builder getHighlightBuilder() {
		Highlight.Builder builder = new Highlight.Builder() {

			@Override
			public Highlight build() {
				return new HighlightImpl(_highlightImpl);
			}

			@Override
			public void fieldConfig(FieldConfig fieldConfig) {
				_highlightImpl.addFieldConfig(fieldConfig);
			}

			@Override
			public void fieldConfigs(List<FieldConfig> fieldConfigs) {
				_highlightImpl.addFieldConfigs(fieldConfigs);
			}

			@Override
			public void forceSource(Boolean forceSource) {
				_highlightImpl.setForceSource(forceSource);
			}

			@Override
			public void fragmenter(String fragmenter) {
				_highlightImpl.setFragmenter(fragmenter);
			}

			@Override
			public void fragmentSize(Integer fragmentSize) {
				_highlightImpl.setFragmentSize(fragmentSize);
			}

			@Override
			public void highlighterType(String highlighterType) {
				_highlightImpl.setHighlighterType(highlighterType);
			}

			@Override
			public void highlightFilter(Boolean highlightFilter) {
				_highlightImpl.setHighlightFilter(highlightFilter);
			}

			@Override
			public void highlightQuery(Query highlightQuery) {
				_highlightImpl.setHighlightQuery(highlightQuery);
			}

			@Override
			public void numOfFragments(Integer numOfFragments) {
				_highlightImpl.setNumOfFragments(numOfFragments);
			}

			@Override
			public void postTags(String... postTags) {
				_highlightImpl.addPostTags(postTags);
			}

			@Override
			public void preTags(String... preTags) {
				_highlightImpl.addPreTags(preTags);
			}

			@Override
			public void requireFieldMatch(Boolean requireFieldMatch) {
				_highlightImpl.setRequireFieldMatch(requireFieldMatch);
			}

			private final HighlightImpl _highlightImpl = new HighlightImpl();

		};

		return builder;
	}

	@Override
	public HighlightField.Builder getHighlightFieldBuilder() {
		HighlightField.Builder builder = new HighlightField.Builder() {

			@Override
			public HighlightField build() {
				return new HighlightFieldImpl(_highlightFieldImpl);
			}

			@Override
			public void fragment(String fragment) {
				_highlightFieldImpl.addFragment(fragment);
			}

			@Override
			public void fragments(Stream<String> fragmentStream) {
				_highlightFieldImpl.addFragments(fragmentStream);
			}

			@Override
			public void name(String name) {
				_highlightFieldImpl.setName(name);
			}

			private final HighlightFieldImpl _highlightFieldImpl =
				new HighlightFieldImpl();

		};

		return builder;
	}

	@Override
	public Highlight highlight(FieldConfig fieldConfig) {
		Highlight.Builder builder = getHighlightBuilder();

		builder.fieldConfig(fieldConfig);

		return builder.build();
	}

	@Override
	public HighlightField highlightField(String field) {
		HighlightField.Builder builder = getHighlightFieldBuilder();

		builder.name(field);

		return builder.build();
	}

}