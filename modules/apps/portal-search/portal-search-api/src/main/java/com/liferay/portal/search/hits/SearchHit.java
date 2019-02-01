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

package com.liferay.portal.search.hits;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.highlight.HighlightField;

import java.util.Map;

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
@ProviderType
public interface SearchHit {

	public Document getDocument();

	public String getExplanation();

	public Map<String, HighlightField> getHighlightFields();

	public String getId();

	public String[] getMatchedQueries();

	public float getScore();

	public Map<String, Object> getSourceMap();

	public long getVersion();

	@ProviderType
	public interface Builder {

		public Builder addHighlightField(HighlightField highlightField);

		public Builder addSource(String name, Object value);

		public SearchHit build();

		public Builder document(Document document);

		public Builder explanation(String explanation);

		public Builder id(String id);

		public Builder matchedQueries(String[] matchedQueries);

		public Builder score(float score);

		public Builder version(long version);

	}

}