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

package com.liferay.portal.search.elasticsearch7.internal.cluster;

import com.liferay.portal.search.elasticsearch7.internal.connection.ClusterHealthResponseUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch7.internal.connection.HealthExpectations;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;

import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.cluster.health.ClusterHealthStatus;

import org.junit.Assert;

/**
 * @author André de Oliveira
 */
public class ClusterAssert {

	public static void assert1PrimaryShardAnd2Nodes(
			ElasticsearchClientResolver elasticsearchClientResolver)
		throws Exception {

		assertHealth(
			elasticsearchClientResolver,
			new HealthExpectations() {
				{
					setActivePrimaryShards(1);
					setActiveShards(1);
					setNumberOfDataNodes(2);
					setNumberOfNodes(2);
					setStatus(ClusterHealthStatus.GREEN);
					setUnassignedShards(0);
				}
			});
	}

	public static void assert1PrimaryShardOnly(
			ElasticsearchClientResolver elasticsearchClientResolver)
		throws Exception {

		assertHealth(
			elasticsearchClientResolver,
			new HealthExpectations() {
				{
					setActivePrimaryShards(1);
					setActiveShards(1);
					setNumberOfDataNodes(1);
					setNumberOfNodes(1);
					setStatus(ClusterHealthStatus.GREEN);
					setUnassignedShards(0);
				}
			});
	}

	public static void assert1ReplicaShard(
			ElasticsearchClientResolver elasticsearchClientResolver)
		throws Exception {

		assertHealth(
			elasticsearchClientResolver,
			new HealthExpectations() {
				{
					setActivePrimaryShards(1);
					setActiveShards(2);
					setNumberOfDataNodes(2);
					setNumberOfNodes(2);
					setStatus(ClusterHealthStatus.GREEN);
					setUnassignedShards(0);
				}
			});
	}

	public static void assert2PrimaryShards1ReplicaAnd2Nodes(
			ElasticsearchClientResolver elasticsearchClientResolver)
		throws Exception {

		assertHealth(
			elasticsearchClientResolver,
			new HealthExpectations() {
				{
					setActivePrimaryShards(2);
					setActiveShards(4);
					setNumberOfDataNodes(2);
					setNumberOfNodes(2);
					setStatus(ClusterHealthStatus.GREEN);
					setUnassignedShards(0);
				}
			});
	}

	public static void assert2PrimaryShardsAnd2Nodes(
			ElasticsearchClientResolver elasticsearchClientResolver)
		throws Exception {

		assertHealth(
			elasticsearchClientResolver,
			new HealthExpectations() {
				{
					setActivePrimaryShards(2);
					setActiveShards(2);
					setNumberOfDataNodes(2);
					setNumberOfNodes(2);
					setStatus(ClusterHealthStatus.GREEN);
					setUnassignedShards(0);
				}
			});
	}

	public static void assert2ReplicaShards(
			ElasticsearchClientResolver elasticsearchClientResolver)
		throws Exception {

		assertHealth(
			elasticsearchClientResolver,
			new HealthExpectations() {
				{
					setActivePrimaryShards(1);
					setActiveShards(3);
					setNumberOfDataNodes(3);
					setNumberOfNodes(3);
					setStatus(ClusterHealthStatus.GREEN);
					setUnassignedShards(0);
				}
			});
	}

	public static void assertHealth(
			final ElasticsearchClientResolver elasticsearchClientResolver,
			final HealthExpectations healthExpectations)
		throws Exception {

		IdempotentRetryAssert.retryAssert(
			25, TimeUnit.SECONDS,
			() -> {
				_assertHealth(
					ClusterHealthResponseUtil.getClusterHealthResponse(
						elasticsearchClientResolver, healthExpectations),
					healthExpectations);

				return null;
			});
	}

	private static void _assertHealth(
		ClusterHealthResponse clusterHealthResponse,
		HealthExpectations expectedHealthExpectations) {

		HealthExpectations actualHealthExpectations = new HealthExpectations(
			clusterHealthResponse);

		Assert.assertEquals(
			expectedHealthExpectations.toString(),
			actualHealthExpectations.toString());
	}

}