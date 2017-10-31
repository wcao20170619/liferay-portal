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

package com.liferay.portal.search.internal.engine;

import com.liferay.portal.kernel.search.BaseSearchEngine;
import com.liferay.portal.kernel.search.SearchEngine;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 */
@Component(
	immediate = true,
	property = {
		"search.engine.id=GENERIC_ENGINE", "search.engine.impl=Defaultsearch"
	},
	service = {DefaultSearchEngine.class, SearchEngine.class}
)
public class DefaultSearchEngine extends BaseSearchEngine {
}