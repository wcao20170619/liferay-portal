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

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.InputStream;

import java.net.URL;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;

import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import org.hamcrest.CoreMatchers;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.MockitoAnnotations;

/**
 * @author André de Oliveira
 */
public class EmbeddedElasticsearchConnectionHttpTest {

	@BeforeClass
	public static void setUpClass() {
		String clusterName = RandomTestUtil.randomString();

		Map<String, Object> properties = HashMapBuilder.<String, Object>put(
			"clusterName", clusterName
		).put(
			"networkHost", "_site_"
		).build();

		ElasticsearchConnectionFixture elasticsearchConnectionFixture =
			ElasticsearchConnectionFixture.builder(
			).clusterName(
				EmbeddedElasticsearchConnectionHttpTest.class.getSimpleName()
			).elasticsearchConfigurationProperties(
				properties
			).build();

		elasticsearchConnectionFixture.createNode();

		_clusterName = clusterName;
		_elasticsearchConnectionFixture = elasticsearchConnectionFixture;
	}

	@AfterClass
	public static void tearDownClass() {
		_elasticsearchConnectionFixture.destroyNode();
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testHttpLocallyAvailableRegardlessOfNetworkHost()
		throws Exception {

		String status = toString(new URL("http://localhost:" + getHttpPort()));

		Assert.assertThat(
			status,
			CoreMatchers.containsString(
				"\"cluster_name\" : \"" + _clusterName));
	}

	protected int getHttpPort() {
		RestHighLevelClient restHighLevelClient =
			_elasticsearchConnectionFixture.getRestHighLevelClient();

		RestClient restClient = restHighLevelClient.getLowLevelClient();

		List<Node> nodes = restClient.getNodes();

		Node node = nodes.get(0);

		HttpHost httpHost = node.getHost();

		return httpHost.getPort();
	}

	protected String toString(URL url) throws Exception {
		try (InputStream inputStream = url.openStream()) {
			return StringUtil.read(inputStream);
		}
	}

	private static String _clusterName;
	private static ElasticsearchConnectionFixture
		_elasticsearchConnectionFixture;

}