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

package com.liferay.portal.search.elasticsearch7.internal.connection;

import com.liferay.portal.search.elasticsearch7.configuration.CCRClusterType;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.RestHighLevelClient;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = CCRElasticsearchConnectionManager.class)
public class CCRElasticsearchConnectionManager {

	public void activate(CCRClusterType ccrClusterType) {
		validate(ccrClusterType);
	}

	public void connect() {
		CCRRemoteElasticsearchConnection remoteElasticsearchConnection =
			(CCRRemoteElasticsearchConnection)getElasticsearchConnection(
				CCRClusterType.REMOTE);

		if (!remoteElasticsearchConnection.isConnected()) {
			remoteElasticsearchConnection.connect();
		}

		CCRLocalElasticsearchConnection localElasticsearchConnection =
			(CCRLocalElasticsearchConnection)getElasticsearchConnection(
				CCRClusterType.LOCAL);

		if (!localElasticsearchConnection.isConnected()) {
			localElasticsearchConnection.connect();
		}
	}

	public ElasticsearchConnection getElasticsearchConnection(
		CCRClusterType clusterType) {

		return _ccrElasticsearchConnections.get(clusterType);
	}

	public RestHighLevelClient getLocalRestHighLevelClient() {
		ElasticsearchConnection elasticsearchConnection =
			getElasticsearchConnection(CCRClusterType.LOCAL);

		if (elasticsearchConnection == null) {
			throw new ElasticsearchConnectionNotInitializedException();
		}

		return elasticsearchConnection.getRestHighLevelClient();
	}

	public RestHighLevelClient getRemoteRestHighLevelClient() {
		ElasticsearchConnection elasticsearchConnection =
			getElasticsearchConnection(CCRClusterType.REMOTE);

		if (elasticsearchConnection == null) {
			throw new ElasticsearchConnectionNotInitializedException();
		}

		return elasticsearchConnection.getRestHighLevelClient();
	}

	@Reference(
		cardinality = ReferenceCardinality.MANDATORY,
		target = "(ccr.cluster.type=LOCAL)",
		unbind = "unsetLocalElasticsearchConnection"
	)
	public void setCCRLocalElasticsearchConnection(
		ElasticsearchConnection elasticsearchConnection) {

		_ccrElasticsearchConnections.put(
			CCRClusterType.LOCAL, elasticsearchConnection);
	}

	@Reference(
		cardinality = ReferenceCardinality.MANDATORY,
		target = "(ccr.cluster.type=REMOTE)",
		unbind = "unsetRemoteElasticsearchConnection"
	)
	public void setCCRRemoteElasticsearchConnection(
		ElasticsearchConnection elasticsearchConnection) {

		_ccrElasticsearchConnections.put(
			CCRClusterType.REMOTE, elasticsearchConnection);
	}

	public void unsetLocalElasticsearchConnection(
		ElasticsearchConnection elasticsearchConnection) {

		_ccrElasticsearchConnections.remove(CCRClusterType.LOCAL);

		elasticsearchConnection.close();
	}

	public void unsetRemoteElasticsearchConnection(
		ElasticsearchConnection elasticsearchConnection) {

		_ccrElasticsearchConnections.remove(CCRClusterType.REMOTE);

		elasticsearchConnection.close();
	}

	@Activate
	protected void activate() {
		activate(CCRClusterType.REMOTE);
		activate(CCRClusterType.LOCAL);

		//	connect();
	}

	protected void validate(CCRClusterType ccrClusterType) {
		if (!_ccrElasticsearchConnections.containsKey(ccrClusterType)) {
			throw new MissingCCRClusterTypeException(ccrClusterType);
		}
	}

	private final Map<CCRClusterType, ElasticsearchConnection>
		_ccrElasticsearchConnections = new HashMap<>();

}