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

/**
 * @author Wade Cao
 */
@ProviderType
public interface SearchHitBuilder {

	public SearchHitBuilder addHighlightField(HighlightField highlightField);

	public SearchHitBuilder addSource(String name, Object value);

	public SearchHit build();

	public SearchHitBuilder document(Document document);

	public SearchHitBuilder explanation(String explanation);

	public SearchHitBuilder id(String id);

	public SearchHitBuilder matchedQueries(String[] matchedQueries);

	public SearchHitBuilder score(float score);

	public SearchHitBuilder version(long version);

}