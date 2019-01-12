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

package com.liferay.portal.search.elasticsearch6.internal.index.create;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.common.settings.Settings;

/**
 * @author André de Oliveira
 */
public class DummyCreateIndexContributor implements CreateIndexContributor {

	@Override
	public void afterCreateIndex(CreateIndexOptions createIndexOptions) {
	}

	@Override
	public void beforeCreateIndex(CreateIndexOptions createIndexOptions) {
	}

	@Override
	public void contributeRequest(
		CreateIndexRequestBuilder createIndexRequestBuilder) {
	}

	@Override
	public void contributeSettings(Settings.Builder builder) {
	}

}