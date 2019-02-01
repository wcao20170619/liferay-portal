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

package com.liferay.portal.search.elasticsearch6.internal.highlight;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.elasticsearch6.internal.query.QueryToQueryBuilderTranslator;
import com.liferay.portal.search.highlight.FieldConfig;
import com.liferay.portal.search.highlight.Highlight;

import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

/**
 * @author Michael C. Han
 */
public class HighlightTranslator {

	public HighlightBuilder translate(
		Highlight highlight, QueryToQueryBuilderTranslator queryTranslator) {

		HighlightBuilder highlightBuilder = new HighlightBuilder();

		List<FieldConfig> fieldConfigs = highlight.getFieldConfigs();

		fieldConfigs.forEach(
			fieldConfig -> highlightBuilder.field(translate(fieldConfig)));

		highlightBuilder.forceSource(highlight.getForceSource());
		highlightBuilder.fragmenter(highlight.getFragmenter());
		highlightBuilder.fragmentSize(highlight.getFragmentSize());
		highlightBuilder.highlighterType(highlight.getHighlighterType());
		highlightBuilder.highlightFilter(highlight.getHighlightFilter());

		if (highlight.getHighlightQuery() != null) {
			QueryBuilder highlightQueryBuilder = queryTranslator.translate(
				highlight.getHighlightQuery());

			highlightBuilder.highlightQuery(highlightQueryBuilder);
		}

		highlightBuilder.highlighterType(highlight.getHighlighterType());
		highlightBuilder.numOfFragments(highlight.getNumOfFragments());

		List<String> preTags = highlight.getPreTags();

		if (ListUtil.isNotEmpty(preTags)) {
			highlightBuilder.preTags(
				preTags.toArray(new String[preTags.size()]));
		}

		List<String> postTags = highlight.getPostTags();

		if (ListUtil.isNotEmpty(postTags)) {
			highlightBuilder.postTags(
				postTags.toArray(new String[postTags.size()]));
		}

		highlightBuilder.requireFieldMatch(highlight.getRequireFieldMatch());

		return highlightBuilder;
	}

	protected HighlightBuilder.Field translate(FieldConfig fieldConfig) {
		HighlightBuilder.Field field = new HighlightBuilder.Field(
			fieldConfig.getField());

		if (fieldConfig.getFragmentOffset() != null) {
			field.fragmentOffset(fieldConfig.getFragmentOffset());
		}

		field.fragmentSize(fieldConfig.getFragmentSize());
		field.numOfFragments(fieldConfig.getNumFragments());

		return field;
	}

}