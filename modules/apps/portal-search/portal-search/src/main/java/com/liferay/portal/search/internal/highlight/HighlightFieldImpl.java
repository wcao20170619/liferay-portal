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

import com.liferay.portal.search.highlight.HighlightField;
import com.liferay.portal.search.highlight.HighlightFieldBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Michael C. Han
 */
public class HighlightFieldImpl implements HighlightField {

	public HighlightFieldImpl() {
	}

	public HighlightFieldImpl(HighlightFieldImpl highlightFieldImpl) {
		_fragments.addAll(highlightFieldImpl._fragments);
		_name = highlightFieldImpl._name;
	}

	@Override
	public List<String> getFragments() {
		return Collections.unmodifiableList(_fragments);
	}

	@Override
	public String getName() {
		return _name;
	}

	public static final class HighlightFieldBuilderImpl
		implements HighlightFieldBuilder {

		@Override
		public HighlightField build() {
			return new HighlightFieldImpl(_highlightFieldImpl);
		}

		@Override
		public HighlightFieldBuilder fragment(String fragment) {
			_highlightFieldImpl.addFragment(fragment);

			return this;
		}

		@Override
		public HighlightFieldBuilder fragments(Stream<String> fragmentStream) {
			_highlightFieldImpl.addFragments(fragmentStream);

			return this;
		}

		@Override
		public HighlightFieldBuilder name(String name) {
			_highlightFieldImpl._name = name;

			return this;
		}

		private final HighlightFieldImpl _highlightFieldImpl =
			new HighlightFieldImpl();

	}

	protected void addFragment(String fragment) {
		_fragments.add(fragment);
	}

	protected void addFragments(Stream<String> fragmentStream) {
		fragmentStream.forEach(_fragments::add);
	}

	private List<String> _fragments = new ArrayList<>();
	private String _name;

}