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

import com.liferay.petra.process.local.LocalProcessExecutor;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.connection.constants.ConnectionConstants;
import com.liferay.portal.search.elasticsearch7.internal.settings.BaseSettingsContributor;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.PathUtil;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.Sidecar;
import com.liferay.portal.search.elasticsearch7.settings.ClientSettingsHelper;
import com.liferay.portal.search.elasticsearch7.settings.SettingsContributor;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.elasticsearch.client.RestHighLevelClient;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class ElasticsearchConnectionFixture
	implements ElasticsearchClientResolver {

	public static Builder builder() {
		return new Builder();
	}

	public ElasticsearchConnection createElasticsearchConnection() {
		ElasticsearchConfigurationWrapper elasticsearchConfigurationWrapper =
			new ElasticsearchConfigurationWrapper() {
				{
					elasticsearchConfiguration =
						ConfigurableUtil.createConfigurable(
							ElasticsearchConfiguration.class,
							_elasticsearchConfigurationProperties);
				}
			};

		Sidecar sidecar = new Sidecar(
			Mockito.mock(ClusterExecutor.class),
			elasticsearchConfigurationWrapper,
			createElasticsearchInstancePaths(), new LocalProcessExecutor(),
			() -> _TMP_PATH.resolve("lib-process-executor"),
			getSettingsContributors());

		ElasticsearchConnectionBuilder elasticsearchConnectionBuilder =
			new ElasticsearchConnectionBuilder();

		elasticsearchConnectionBuilder.active(
			true
		).connectionId(
			ConnectionConstants.SIDECAR_CONNECTION_ID
		).postCloseRunnable(
			sidecar::stop
		).preConnectElasticsearchConnectionConsumer(
			elasticsearchConnection -> {
				deleteTmpDir();

				sidecar.start();

				elasticsearchConnection.setNetworkHostAddresses(
					new String[] {sidecar.getNetworkHostAddress()});
			}
		);

		_elasticsearchConnection = elasticsearchConnectionBuilder.build();

		return _elasticsearchConnection;
	}

	public void createNode() {
		createElasticsearchConnection();

		_elasticsearchConnection.connect();
	}

	public void destroyNode() {
		if (_elasticsearchConnection != null) {
			_elasticsearchConnection.close();
		}

		deleteTmpDir();
	}

	public Map<String, Object> getElasticsearchConfigurationProperties() {
		return _elasticsearchConfigurationProperties;
	}

	public ElasticsearchConnection getElasticsearchConnection() {
		return _elasticsearchConnection;
	}

	@Override
	public RestHighLevelClient getRestHighLevelClient() {
		return _elasticsearchConnection.getRestHighLevelClient();
	}

	@Override
	public RestHighLevelClient getRestHighLevelClient(String connectionId) {
		return getRestHighLevelClient();
	}

	@Override
	public RestHighLevelClient getRestHighLevelClient(
		String connectionId, boolean preferLocalCluster) {

		return getRestHighLevelClient();
	}

	public static class Builder {

		public ElasticsearchConnectionFixture build() {
			ElasticsearchConnectionFixture elasticsearchConnectionFixture =
				new ElasticsearchConnectionFixture();

			elasticsearchConnectionFixture._discoveryTypeZen =
				_discoveryTypeZen;
			elasticsearchConnectionFixture._initialMasterNodes =
				_initialMasterNodes;
			elasticsearchConnectionFixture.
				_elasticsearchConfigurationProperties =
					createElasticsearchConfigurationProperties(
						_elasticsearchConfigurationProperties, _clusterName);
			elasticsearchConnectionFixture._workPath = _TMP_PATH.resolve(
				_clusterName);

			return elasticsearchConnectionFixture;
		}

		public ElasticsearchConnectionFixture.Builder clusterName(
			String clusterName) {

			_clusterName = clusterName;

			return this;
		}

		public Builder discoveryTypeZen(boolean discoveryTypeZen) {
			_discoveryTypeZen = discoveryTypeZen;

			return this;
		}

		public Builder elasticsearchConfigurationProperties(
			Map<String, Object> elasticsearchConfigurationProperties) {

			if (elasticsearchConfigurationProperties == null) {
				elasticsearchConfigurationProperties =
					Collections.<String, Object>emptyMap();
			}

			_elasticsearchConfigurationProperties =
				elasticsearchConfigurationProperties;

			return this;
		}

		public Builder initialMasterNodes(String initialMasterNodes) {
			_initialMasterNodes = initialMasterNodes;

			return this;
		}

		protected static final Map<String, Object>
			createElasticsearchConfigurationProperties(
				Map<String, Object> elasticsearchConfigurationProperties,
				String clusterName) {

			return HashMapBuilder.<String, Object>put(
				"clusterName", clusterName
			).put(
				"configurationPid", ElasticsearchConfiguration.class.getName()
			).put(
				"httpCORSAllowOrigin", "*"
			).put(
				"logExceptionsOnly", false
			).put(
				"sidecarHttpPort", HttpPortRange.AUTO
			).putAll(
				elasticsearchConfigurationProperties
			).build();
		}

		private String _clusterName;
		private Boolean _discoveryTypeZen;
		private Map<String, Object> _elasticsearchConfigurationProperties =
			Collections.<String, Object>emptyMap();
		private String _initialMasterNodes;

	}

	protected ElasticsearchInstancePaths createElasticsearchInstancePaths() {
		ElasticsearchInstancePaths elasticsearchInstancePaths = Mockito.mock(
			ElasticsearchInstancePaths.class);

		Mockito.doReturn(
			_TMP_PATH.resolve("sidecar-elasticsearch")
		).when(
			elasticsearchInstancePaths
		).getHomePath();

		Mockito.doReturn(
			_workPath
		).when(
			elasticsearchInstancePaths
		).getWorkPath();

		return elasticsearchInstancePaths;
	}

	protected void deleteTmpDir() {
		PathUtil.deleteDir(_workPath);
	}

	protected SettingsContributor
		getClusterLoggingThresholdSettingsContributor() {

		return new BaseSettingsContributor(0) {

			@Override
			public void populate(ClientSettingsHelper clientSettingsHelper) {
				clientSettingsHelper.put(
					"cluster.service.slow_task_logging_threshold", "600s");
			}

		};
	}

	protected SettingsContributor getDiscoveryTypeZenContributor() {
		if (!GetterUtil.getBoolean(_discoveryTypeZen)) {
			return null;
		}

		return new SettingsContributor() {

			@Override
			public int compareTo(SettingsContributor o) {
				return 0;
			}

			@Override
			public int getPriority() {
				return 0;
			}

			@Override
			public void populate(ClientSettingsHelper clientSettingsHelper) {
				clientSettingsHelper.put("discovery.type", "zen");
			}

		};
	}

	protected SettingsContributor getDiskThresholdSettingsContributor() {
		return new BaseSettingsContributor(0) {

			@Override
			public void populate(ClientSettingsHelper clientSettingsHelper) {
				clientSettingsHelper.put(
					"cluster.routing.allocation.disk.threshold_enabled",
					"false");
			}

		};
	}

	protected SettingsContributor getInitialMasterNodesSettingsContributor() {
		return new BaseSettingsContributor(0) {

			@Override
			public void populate(ClientSettingsHelper clientSettingsHelper) {
				if (Validator.isBlank(_initialMasterNodes)) {
					return;
				}

				clientSettingsHelper.put(
					"cluster.initial_master_nodes", _initialMasterNodes);
			}

		};
	}

	protected List<SettingsContributor> getSettingsContributors() {
		return Stream.of(
			getClusterLoggingThresholdSettingsContributor(),
			getDiskThresholdSettingsContributor(),
			getDiscoveryTypeZenContributor(),
			getInitialMasterNodesSettingsContributor()
		).filter(
			Objects::nonNull
		).collect(
			Collectors.toList()
		);
	}

	private static final Path _TMP_PATH = Paths.get("tmp");

	private Boolean _discoveryTypeZen;
	private Map<String, Object> _elasticsearchConfigurationProperties =
		Collections.<String, Object>emptyMap();
	private ElasticsearchConnection _elasticsearchConnection;
	private String _initialMasterNodes;
	private Path _workPath;

}