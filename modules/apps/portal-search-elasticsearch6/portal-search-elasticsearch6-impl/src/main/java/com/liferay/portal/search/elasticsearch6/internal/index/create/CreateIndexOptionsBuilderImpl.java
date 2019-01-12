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

import java.util.Collection;

import org.elasticsearch.client.AdminClient;

/**
 * @author André de Oliveira
 */
public class CreateIndexOptionsBuilderImpl
	implements CreateIndexOptionsBuilder {

	@Override
	public CreateIndexOptionsBuilder addContributor(
		CreateIndexContributor createIndexContributor) {

		if (createIndexContributor != null) {
			_createIndexOptionsImpl.contributors.add(createIndexContributor);
		}

		return this;
	}

	@Override
	public CreateIndexOptionsBuilder addContributors(
		Collection<CreateIndexContributor> createIndexContributors) {

		if (createIndexContributors != null) {
			_createIndexOptionsImpl.contributors.addAll(
				createIndexContributors);
		}

		return this;
	}

	@Override
	public CreateIndexOptionsBuilder adminClient(AdminClient adminClient) {
		_createIndexOptionsImpl.adminClient = adminClient;

		return this;
	}

	@Override
	public CreateIndexOptions build() {
		return new CreateIndexOptionsImpl(_createIndexOptionsImpl);
	}

	@Override
	public CreateIndexOptionsBuilder indexName(String indexName) {
		_createIndexOptionsImpl.indexName = indexName;

		return this;
	}

	private final CreateIndexOptionsImpl _createIndexOptionsImpl =
		new CreateIndexOptionsImpl();

}