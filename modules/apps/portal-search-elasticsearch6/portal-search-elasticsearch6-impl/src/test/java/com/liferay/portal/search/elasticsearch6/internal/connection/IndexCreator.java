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

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.search.elasticsearch6.internal.index.create.CreateIndexContributor;
import com.liferay.portal.search.elasticsearch6.internal.index.create.CreateIndexOptions;
import com.liferay.portal.search.elasticsearch6.internal.index.create.CreateIndexOptionsBuilder;
import com.liferay.portal.search.elasticsearch6.internal.index.create.CreateIndexRequest;
import com.liferay.portal.search.elasticsearch6.internal.index.create.CreateIndexRequestFactory;
import com.liferay.portal.search.elasticsearch6.internal.index.create.CreateIndexRequestFactoryImpl;
import com.liferay.portal.search.elasticsearch6.internal.index.create.DummyCreateIndexContributor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;

/**
 * @author André de Oliveira
 */
public class IndexCreator {

	public void addCreateIndexContributor(
		CreateIndexContributor createIndexContributor) {

		createIndexContributors.add(createIndexContributor);
	}

	public Index createIndex(IndexName indexName) {
		if (_liferayMappingsAddedToIndex) {
			addCreateIndexContributor(
				new LiferayCreateIndexContributor(elasticsearchClientResolver));
		}

		CreateIndexRequestFactory createIndexRequestFactory =
			new CreateIndexRequestFactoryImpl();

		CreateIndexOptionsBuilder createIndexOptionsBuilder =
			createIndexRequestFactory.createOptionsBuilder();

		CreateIndexRequest createIndexRequest =
			createIndexRequestFactory.create(
				createIndexOptionsBuilder.addContributor(
					new DeleteBeforeCreateIndexContributor()
				).addContributors(
					createIndexContributors
				).adminClient(
					getAdminClient()
				).indexName(
					indexName.getName()
				).build());

		createIndexRequest.createIndex();

		return new Index(indexName);
	}

	public Collection<CreateIndexContributor> getCreateIndexContributors() {
		return Collections.unmodifiableCollection(createIndexContributors);
	}

	public void setLiferayMappingsAddedToIndex(
		boolean liferayMappingsAddedToIndex) {

		_liferayMappingsAddedToIndex = liferayMappingsAddedToIndex;
	}

	protected AdminClient getAdminClient() {
		Client client = elasticsearchClientResolver.getClient();

		return client.admin();
	}

	protected final List<CreateIndexContributor> createIndexContributors =
		new ArrayList<>();
	protected ElasticsearchClientResolver elasticsearchClientResolver;
	protected JSONFactory jsonFactory;

	private boolean _liferayMappingsAddedToIndex;

	private class DeleteBeforeCreateIndexContributor
		extends DummyCreateIndexContributor {

		@Override
		public void beforeCreateIndex(CreateIndexOptions createIndexOptions) {
			AdminClient adminClient = getAdminClient();

			IndicesAdminClient indicesAdminClient = adminClient.indices();

			DeleteIndexRequestBuilder deleteIndexRequestBuilder =
				indicesAdminClient.prepareDelete(
					createIndexOptions.getIndexName());

			deleteIndexRequestBuilder.setIndicesOptions(
				IndicesOptions.lenientExpandOpen());

			deleteIndexRequestBuilder.get();
		}

	}

	protected void setElasticsearchClientResolver(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	protected void setIndexCreationHelper(
		IndexCreationHelper indexCreationHelper) {

		_indexCreationHelper = indexCreationHelper;
	}

	protected void setLiferayMappingsAddedToIndex(
		boolean liferayMappingsAddedToIndex) {

		_liferayMappingsAddedToIndex = liferayMappingsAddedToIndex;
	}

	private ElasticsearchClientResolver _elasticsearchClientResolver;
	private IndexCreationHelper _indexCreationHelper;
	private boolean _liferayMappingsAddedToIndex;

}