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

package com.liferay.portal.search.highlight;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.search.query.Query;

import java.util.List;

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
@ProviderType
public interface Highlight {

	public List<FieldConfig> getFieldConfigs();

	public Boolean getForceSource();

	public String getFragmenter();

	public Integer getFragmentSize();

	public String getHighlighterType();

	public Boolean getHighlightFilter();

	public Query getHighlightQuery();

	public Integer getNumOfFragments();

	public List<String> getPostTags();

	public List<String> getPreTags();

	public Boolean getRequireFieldMatch();

	@ProviderType
	public interface Builder {

		public Highlight build();

		public void fieldConfig(FieldConfig fieldConfig);

		public void fieldConfigs(List<FieldConfig> fieldConfigs);

		public void forceSource(Boolean forceSource);

		public void fragmenter(String fragmenter);

		public void fragmentSize(Integer fragmentSize);

		public void highlighterType(String highlighterType);

		public void highlightFilter(Boolean highlightFilter);

		public void highlightQuery(Query highlightQuery);

		public void numOfFragments(Integer numOfFragments);

		public void postTags(String... postTags);

		public void preTags(String... preTags);

		public void requireFieldMatch(Boolean requireFieldMatch);

	}

}