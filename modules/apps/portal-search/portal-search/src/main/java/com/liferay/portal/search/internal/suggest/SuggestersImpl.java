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
package com.liferay.portal.search.internal.suggest;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.search.suggest.CompletionSuggester;
import com.liferay.portal.kernel.search.suggest.PhraseSuggester;
import com.liferay.portal.kernel.search.suggest.TermSuggester;
import com.liferay.portal.search.suggest.Suggesters;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = Suggesters.class)
public class SuggestersImpl implements Suggesters {
	
	@Override
	public TermSuggester termSuggester(String name, String field) {
		return new TermSuggester(name, field);
	}
	
	public CompletionSuggester completionSuggester(
			String name, String field, String value) {
		return new CompletionSuggester(name, field, value);
	}
	
	public PhraseSuggester phraseSuggester(String name, String field) {
		return new PhraseSuggester(name, field);
	}

}
