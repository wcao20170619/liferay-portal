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

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionFixture;
import com.liferay.portal.search.elasticsearch7.internal.connection.Index;
import com.liferay.portal.search.elasticsearch7.internal.connection.IndexCreator;
import com.liferay.portal.search.elasticsearch7.internal.connection.IndexName;

import org.elasticsearch.client.RestHighLevelClient;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * @author André de Oliveira
 */
public class ClusterUnicastTest {

	@Before
	public void setUp() throws Exception {
		Assume.assumeTrue(ClusterAssert.isClusterTestingEnabled());

		_testCluster.setUp();
	}

	@After
	public void tearDown() throws Exception {
		_testCluster.tearDown();
	}

	@Test
	public void testSplitBrainPreventedEvenIfMasterLeaves() throws Exception {
		ElasticsearchConnectionFixture elasticsearchConnectionFixture0 =
			_testCluster.getNode(0);

		Index index0 = createIndex(elasticsearchConnectionFixture0);

		ElasticsearchConnectionFixture elasticsearchConnectionFixture1 =
			_testCluster.getNode(1);

		Index index1 = createIndex(elasticsearchConnectionFixture1);

		ElasticsearchConnectionFixture elasticsearchConnectionFixture2 =
			_testCluster.getNode(2);

		Index index2 = createIndex(elasticsearchConnectionFixture2);

		//updateNumberOfReplicas(2, index0, elasticsearchConnectionFixture0);

		ClusterAssert.assert2ReplicaShards(elasticsearchConnectionFixture0);
		ClusterAssert.assert2ReplicaShards(elasticsearchConnectionFixture1);
		ClusterAssert.assert2ReplicaShards(elasticsearchConnectionFixture2);

		_testCluster.destroyNode(0);

		ClusterAssert.assert1ReplicaAnd1UnassignedShard(
			elasticsearchConnectionFixture1);
		ClusterAssert.assert1ReplicaAnd1UnassignedShard(
			elasticsearchConnectionFixture2);

		//updateNumberOfReplicas(1, index1, elasticsearchConnectionFixture1);

		ClusterAssert.assert1ReplicaShard(elasticsearchConnectionFixture1);
		ClusterAssert.assert1ReplicaShard(elasticsearchConnectionFixture2);

		_testCluster.destroyNode(1);

		ClusterAssert.assert1PrimaryAnd1UnassignedShard(
			elasticsearchConnectionFixture2);

		updateNumberOfReplicas(0, index2, elasticsearchConnectionFixture2);

		ClusterAssert.assert1PrimaryShardOnly(elasticsearchConnectionFixture2);
	}

	@Rule
	public TestName testName = new TestName();

	protected Index createIndex(
		ElasticsearchConnectionFixture elasticsearchConnectionFixture) {

		IndexCreator indexCreator = new IndexCreator() {
			{
				setElasticsearchClientResolver(elasticsearchConnectionFixture);
			}
		};

		return indexCreator.createIndex(
			new IndexName(testName.getMethodName()));
	}

	protected void updateNumberOfReplicas(
		int numberOfReplicas, Index index,
		ElasticsearchConnectionFixture elasticsearchConnectionFixture) {

		RestHighLevelClient restHighLevelClient =
			elasticsearchConnectionFixture.getRestHighLevelClient();

		ReplicasManager replicasManager = new ReplicasManagerImpl(
			restHighLevelClient.indices());

		replicasManager.updateNumberOfReplicas(
			numberOfReplicas, index.getName());
	}

	private final TestCluster _testCluster = new TestCluster(3, this, 9206, 9306);

}