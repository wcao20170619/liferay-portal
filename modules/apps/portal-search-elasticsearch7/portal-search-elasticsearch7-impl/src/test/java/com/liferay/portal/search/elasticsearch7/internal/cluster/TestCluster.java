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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionFixture;

import java.net.InetAddress;
import java.net.NetworkInterface;

import java.util.HashMap;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class TestCluster {

	public TestCluster(int size, Object object) {
		String prefix = getPrefix(object);

		_elasticsearchFixtures = new ElasticsearchConnectionFixture[size];

		_prefix = prefix;
	}

	public ElasticsearchConnectionFixture createNode(int index) {
		ElasticsearchConnectionFixture elasticsearchConnectionFixture =
			ElasticsearchConnectionFixture.builder(
			).clusterSettingsContext(
				new TestClusterSettingsContext()
			).elasticsearchConfigurationProperties(
				createElasticsearchConfigurationProperties(
					index, _prefix, _elasticsearchFixtures.length)
			).tmpSubdirName(
				_prefix + "-" + index
			).build();

		elasticsearchConnectionFixture.createNode();

		_elasticsearchFixtures[index] = elasticsearchConnectionFixture;

		return elasticsearchConnectionFixture;
	}

	public void createNodes() throws Exception {
		for (int i = 0; i < _elasticsearchFixtures.length; i++) {
			createNode(i);
		}
	}

	public void destroyNode(int index) throws Exception {
		if (_elasticsearchFixtures[index] != null) {
			_elasticsearchFixtures[index].destroyNode();

			_elasticsearchFixtures[index] = null;
		}
	}

	public void destroyNodes() throws Exception {
		for (int i = 0; i < _elasticsearchFixtures.length; i++) {
			destroyNode(i);
		}
	}

	public ElasticsearchConnectionFixture getNode(int index) {
		return _elasticsearchFixtures[index];
	}

	public void setUp() throws Exception {
		createNodes();
	}

	public void tearDown() throws Exception {
		destroyNodes();
	}

	protected HashMap<String, Object>
		createElasticsearchConfigurationProperties(
			int index, String prefix, int size) {

		String transportRange = getPortRange(9310, size);

		return HashMapBuilder.<String, Object>put(
			"clusterName", prefix + "-Cluster"
		).put(
			"discoveryZenPingUnicastHostsPort", transportRange
		).put(
			"embeddedHttpPort", String.valueOf(9201 + index)
		).put(
			"transportTcpPort", transportRange
		).build();
	}

	protected String getPortRange(int startingPort, int size) {
		if (size > 1) {
			return String.valueOf(startingPort) + StringPool.MINUS +
				String.valueOf(startingPort + size - 1);
		}

		return String.valueOf(startingPort);
	}

	protected String getPrefix(Object object) {
		Class<?> clazz = object.getClass();

		return clazz.getSimpleName();
	}

	private final ElasticsearchConnectionFixture[] _elasticsearchFixtures;
	private final String _prefix;

	private static class TestClusterSettingsContext
		implements ClusterSettingsContext {

		@Override
		public String[] getHosts() {
			return new String[] {"127.0.0.1"};
		}

		@Override
		public InetAddress getLocalBindInetAddress() {
			return InetAddress.getLoopbackAddress();
		}

		@Override
		public NetworkInterface getLocalBindNetworkInterface() {
			return Mockito.mock(NetworkInterface.class);
		}

		@Override
		public boolean isClusterEnabled() {
			return true;
		}

	}

}