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

import com.liferay.portal.kernel.search.suggest.CompletionSuggester;
import com.liferay.portal.kernel.search.suggest.PhraseSuggester;
import com.liferay.portal.kernel.search.suggest.TermSuggester;

/**
 * @author Wade Cao
 */
public interface Suggesters {
	
	public TermSuggester termSuggester(String name, String field);
	
	public CompletionSuggester completionSuggester(
			String name, String field, String value); 
	
	public PhraseSuggester phraseSuggester(String name, String field);

}
