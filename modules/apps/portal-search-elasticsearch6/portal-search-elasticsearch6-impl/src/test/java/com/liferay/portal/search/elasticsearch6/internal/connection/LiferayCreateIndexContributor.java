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

package com.liferay.portal.search.elasticsearch6.internal.connection;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.search.elasticsearch6.internal.index.LiferayDocumentTypeFactory;
import com.liferay.portal.search.elasticsearch6.internal.index.create.CreateIndexOptions;
import com.liferay.portal.search.elasticsearch6.internal.index.create.DummyCreateIndexContributor;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;

/**
 * @author André de Oliveira
 */
public class LiferayCreateIndexContributor extends DummyCreateIndexContributor {

	public LiferayCreateIndexContributor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	@Override
	public void afterCreateIndex(CreateIndexOptions createIndexOptions) {
		LiferayDocumentTypeFactory liferayDocumentTypeFactory =
			getLiferayDocumentTypeFactory();

		liferayDocumentTypeFactory.createOptionalDefaultTypeMappings(
			createIndexOptions.getIndexName());
	}

	@Override
	public void contributeRequest(
		CreateIndexRequestBuilder createIndexRequestBuilder) {

		LiferayDocumentTypeFactory liferayDocumentTypeFactory =
			getLiferayDocumentTypeFactory();

		liferayDocumentTypeFactory.createRequiredDefaultTypeMappings(
			createIndexRequestBuilder);
	}

	@Override
	public void contributeSettings(Settings.Builder builder) {
		LiferayDocumentTypeFactory liferayDocumentTypeFactory =
			getLiferayDocumentTypeFactory();

		liferayDocumentTypeFactory.createRequiredDefaultAnalyzers(builder);

		builder.put("index.number_of_replicas", 0);
		builder.put("index.number_of_shards", 1);
	}

	protected LiferayDocumentTypeFactory getLiferayDocumentTypeFactory() {
		Client client = _elasticsearchClientResolver.getClient();

		AdminClient adminClient = client.admin();

		return new LiferayDocumentTypeFactory(
			adminClient.indices(), new JSONFactoryImpl());
	}

	private final ElasticsearchClientResolver _elasticsearchClientResolver;

}