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

	public TestCluster(int size, Object object, int startingHttpPort, int startingTransportTcpPort) {
		
		String prefix = getPrefix(object);

		_elasticsearchFixtures = new ElasticsearchConnectionFixture[size];

		_prefix = prefix;
		
		_startingHttpPort = startingHttpPort;
		
		_startingTransportTcpPort = startingTransportTcpPort;
	}

	public ElasticsearchConnectionFixture createNode(int index) {
		ElasticsearchConnectionFixture elasticsearchConnectionFixture =
			ElasticsearchConnectionFixture.builder(
			).clusterName(
				_prefix + "-" + index
			).clusterSettingsContext(
				new TestClusterSettingsContext()
			).elasticsearchConfigurationProperties(
				createElasticsearchConfigurationProperties(
					index, _prefix, _elasticsearchFixtures.length)
			).build();

		elasticsearchConnectionFixture.createNode();

		_elasticsearchFixtures[index] = elasticsearchConnectionFixture;

		return elasticsearchConnectionFixture;
	}

	public void createNodes() {
		for (int i = 0; i < _elasticsearchFixtures.length; i++) {
			createNode(i);
		}
	}

	public void destroyNode(int index) {
		if (_elasticsearchFixtures[index] != null) {
			_elasticsearchFixtures[index].destroyNode();

			_elasticsearchFixtures[index] = null;
		}
	}

	public void destroyNodes() {
		for (int i = 0; i < _elasticsearchFixtures.length; i++) {
			destroyNode(i);
		}
	}

	public ElasticsearchConnectionFixture getNode(int index) {
		return _elasticsearchFixtures[index];
	}

	public void setUp() {
		createNodes();
	}

	public void tearDown() {
		destroyNodes();
	}

	protected HashMap<String, Object>
		createElasticsearchConfigurationProperties(
			int index, String prefix, int size) {

		String transportRange = getPortRange(_startingTransportTcpPort, size);

		return HashMapBuilder.<String, Object>put(
				"clusterName", prefix + "-Cluster"
			).put(
				"embeddedHttpPort", String.valueOf(_startingHttpPort + index)
			).put(
				"nodeName", getNodeName(index) 
			).put(
				"transportTcpPort", transportRange
			).build();
	}

	protected String getNodeName(int index) {
		return "node" + "-" + index;
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
	private final int _startingTransportTcpPort;
	private final int _startingHttpPort;

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