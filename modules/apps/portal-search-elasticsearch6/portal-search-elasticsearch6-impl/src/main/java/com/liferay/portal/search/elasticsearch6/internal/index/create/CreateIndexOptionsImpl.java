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

import java.util.ArrayList;
import java.util.stream.Stream;

import org.elasticsearch.client.AdminClient;

/**
 * @author André de Oliveira
 */
public class CreateIndexOptionsImpl implements CreateIndexOptions {

	public CreateIndexOptionsImpl() {
		contributors = new ArrayList<>();
	}

	public CreateIndexOptionsImpl(
		CreateIndexOptionsImpl createIndexOptionsImpl) {

		adminClient = createIndexOptionsImpl.adminClient;
		contributors = new ArrayList<>(createIndexOptionsImpl.contributors);
		indexName = createIndexOptionsImpl.indexName;
	}

	@Override
	public AdminClient getAdminClient() {
		return adminClient;
	}

	@Override
	public Stream<CreateIndexContributor> getContributorsStream() {
		return contributors.stream();
	}

	@Override
	public String getIndexName() {
		return indexName;
	}

	protected AdminClient adminClient;
	protected final ArrayList<CreateIndexContributor> contributors;
	protected String indexName;

}