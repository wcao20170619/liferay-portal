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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.elasticsearch6.internal.util.LogUtil;

import java.util.stream.Stream;

import org.elasticsearch.ResourceAlreadyExistsException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;

/**
 * @author André de Oliveira
 */
public class CreateIndexRequestImpl implements CreateIndexRequest {

	public CreateIndexRequestImpl(CreateIndexOptions createIndexOptions) {
		_createIndexOptions = createIndexOptions;
	}

	@Override
	public void createIndex() {
		notifyListenersBeforeCreateIndex();

		createIndex(
			_createIndexOptions.getAdminClient(),
			_createIndexOptions.getIndexName());

		notifyListenersAfterCreateIndex();
	}

	protected CreateIndexRequestBuilder contributeRequest(
		CreateIndexRequestBuilder createIndexRequestBuilder) {

		Stream<CreateIndexContributor> stream =
			_createIndexOptions.getContributorsStream();

		stream.forEach(
			createIndexContributor ->
				createIndexContributor.contributeRequest(
					createIndexRequestBuilder));

		return createIndexRequestBuilder;
	}

	protected Settings.Builder contributeSettings(Settings.Builder builder) {
		Stream<CreateIndexContributor> stream =
			_createIndexOptions.getContributorsStream();

		stream.forEach(
			createIndexContributor -> createIndexContributor.contributeSettings(
				builder));

		return builder;
	}

	protected void createIndex(AdminClient adminClient, String indexName) {
		IndicesAdminClient indicesAdminClient = adminClient.indices();

		CreateIndexRequestBuilder createIndexRequestBuilder = contributeRequest(
			indicesAdminClient.prepareCreate(indexName));

		createIndexRequestBuilder.setSettings(
			contributeSettings(Settings.builder()));

		try {
			CreateIndexResponse createIndexResponse =
				createIndexRequestBuilder.get();

			LogUtil.logActionResponse(_log, createIndexResponse);
		}
		catch (ResourceAlreadyExistsException raee) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping index creation because it already exists: " +
						indexName,
					raee);
			}
		}
	}

	protected void notifyListenersAfterCreateIndex() {
		Stream<CreateIndexContributor> stream =
			_createIndexOptions.getContributorsStream();

		stream.forEach(
			createIndexContributor -> createIndexContributor.afterCreateIndex(
				_createIndexOptions));
	}

	protected void notifyListenersBeforeCreateIndex() {
		Stream<CreateIndexContributor> stream =
			_createIndexOptions.getContributorsStream();

		stream.forEach(
			createIndexContributor -> createIndexContributor.beforeCreateIndex(
				_createIndexOptions));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CreateIndexRequestImpl.class);

	private final CreateIndexOptions _createIndexOptions;

}