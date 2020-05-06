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
import com.liferay.portal.search.elasticsearch7.internal.sidecar.LPS104115;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * @author André de Oliveira
 */
public class Cluster1InstanceTest {

	@Before
	public void setUp() throws Exception {
		Assume.assumeTrue(LPS104115.LPS113038);

		_testCluster.setUp();
	}

	@After
	public void tearDown() throws Exception {
		_testCluster.tearDown();
	}

	@Test
	public void test1PrimaryShardByDefault() throws Exception {
		ElasticsearchConnectionFixture elasticsearchConnectionFixture =
			_testCluster.getNode(0);

		createIndex(elasticsearchConnectionFixture);

		ClusterAssert.assert1PrimaryShardOnly(elasticsearchConnectionFixture);
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

	private final TestCluster _testCluster = new TestCluster(1, this);

}