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

package com.liferay.portal.search.suggest;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.suggest.SpellCheckIndexWriter;

/**
 * @author Michael C. Han
 */
public interface BaseSpellCheckIndexWriter extends SpellCheckIndexWriter {

	@Override
	public void indexKeyword(
			SearchContext searchContext, float weight, String keywordType)
		throws SearchException;

	@Override
	public void indexQuerySuggestionDictionaries(SearchContext searchContext)
		throws SearchException;

	@Override
	public void indexQuerySuggestionDictionary(SearchContext searchContext)
		throws SearchException;

	@Override
	public void indexSpellCheckerDictionaries(SearchContext searchContext)
		throws SearchException;

	@Override
	public void indexSpellCheckerDictionary(SearchContext searchContext)
		throws SearchException;

	public void setQuerySuggestionMaxNGramLength(
		int querySuggestionMaxNGramLength);

}