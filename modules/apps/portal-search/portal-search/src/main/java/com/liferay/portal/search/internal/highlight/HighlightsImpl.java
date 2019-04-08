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
import com.liferay.portal.search.highlight.FieldConfigBuilder;
import com.liferay.portal.search.highlight.Highlight;
import com.liferay.portal.search.highlight.HighlightBuilder;
import com.liferay.portal.search.highlight.Highlights;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = Highlights.class)
public class HighlightsImpl implements Highlights {
	
	@Override
	public FieldConfigBuilder getFieldConfigBuilder() {
		return new FieldConfigImpl.FieldConfigBuilderImpl();
	}
	
	@Override
	public HighlightBuilder getHighlightBuilder() {
		return new HighlightImpl.HighlightBuilderImpl();
	}

	@Override
	public FieldConfig fieldConfig(String field) {
		FieldConfigBuilder builder =
			new FieldConfigImpl.FieldConfigBuilderImpl();

		builder.field(field);

		return builder.build();
	}

	@Override
	public Highlight highlight(FieldConfig fieldConfig) {
		HighlightBuilder builder = new HighlightImpl.HighlightBuilderImpl();

		builder.fieldConfig(fieldConfig);

		return builder.build();
	}

}