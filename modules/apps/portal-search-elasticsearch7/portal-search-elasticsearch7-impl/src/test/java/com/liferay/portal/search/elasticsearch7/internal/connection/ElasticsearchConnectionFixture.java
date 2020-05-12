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
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch7.internal.cluster.ClusterSettingsContext;
import com.liferay.portal.search.elasticsearch7.internal.cluster.UnicastSettingsContributor;
import com.liferay.portal.search.elasticsearch7.internal.settings.BaseSettingsContributor;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.LPS104115;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.Sidecar;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.SidecarPaths;
import com.liferay.portal.search.elasticsearch7.settings.ClientSettingsHelper;
import com.liferay.portal.util.FileImpl;

import java.io.File;
import java.io.IOException;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import org.elasticsearch.client.RestHighLevelClient;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;

/**
 * @author André de Oliveira
 */
public class ElasticsearchConnectionFixture
	implements ElasticsearchClientResolver {

	public static ElasticsearchConnectionFixtureBuilder builder() {
		return new ElasticsearchConnectionFixtureBuilder();
	}

	public void createNode() {
		deleteTmpDir();

		_elasticsearchConnection = openElasticsearchConnection();
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

	public static class ElasticsearchConnectionFixtureBuilder {

		public ElasticsearchConnectionFixture build() {
			ElasticsearchConnectionFixture elasticsearchConnectionFixture =
				new ElasticsearchConnectionFixture();

			elasticsearchConnectionFixture._clusterSettingsContext =
				_clusterSettingsContext;
			elasticsearchConnectionFixture.
				_elasticsearchConfigurationProperties =
					createElasticsearchConfigurationProperties(
						_elasticsearchConfigurationProperties);
			elasticsearchConnectionFixture._tmpDirName =
				"tmp/" + _tmpSubdirName;

			return elasticsearchConnectionFixture;
		}

		public
			ElasticsearchConnectionFixture.ElasticsearchConnectionFixtureBuilder
				clusterSettingsContext(
					ClusterSettingsContext clusterSettingsContext) {

			_clusterSettingsContext = clusterSettingsContext;

			return this;
		}

		public
			ElasticsearchConnectionFixture.ElasticsearchConnectionFixtureBuilder
				elasticsearchConfigurationProperties(
					Map<String, Object> elasticsearchConfigurationProperties) {

			if (elasticsearchConfigurationProperties == null) {
				elasticsearchConfigurationProperties =
					Collections.<String, Object>emptyMap();
			}

			_elasticsearchConfigurationProperties =
				elasticsearchConfigurationProperties;

			return this;
		}

		public
			ElasticsearchConnectionFixture.ElasticsearchConnectionFixtureBuilder
				tmpSubdirName(String tmpSubdirName) {

			_tmpSubdirName = tmpSubdirName;

			return this;
		}

		protected final Map<String, Object>
			createElasticsearchConfigurationProperties(
				Map<String, Object> elasticsearchConfigurationProperties) {

			return HashMapBuilder.<String, Object>put(
				"configurationPid", ElasticsearchConfiguration.class.getName()
			).put(
				"httpCORSAllowOrigin", "*"
			).put(
				"logExceptionsOnly", false
			).putAll(
				elasticsearchConfigurationProperties
			).build();
		}

		private ClusterSettingsContext _clusterSettingsContext;
		private Map<String, Object> _elasticsearchConfigurationProperties =
			Collections.<String, Object>emptyMap();
		private String _tmpSubdirName;

	}

	protected void addClusterLoggingThresholdContributor(
		EmbeddedElasticsearchConnection embeddedElasticsearchConnection) {

		embeddedElasticsearchConnection.addSettingsContributor(
			new BaseSettingsContributor(0) {

				@Override
				public void populate(
					ClientSettingsHelper clientSettingsHelper) {

					clientSettingsHelper.put(
						"cluster.service.slow_task_logging_threshold", "600s");
				}

			});
	}

	protected void addDiskThresholdSettingsContributor(
		EmbeddedElasticsearchConnection embeddedElasticsearchConnection) {

		embeddedElasticsearchConnection.addSettingsContributor(
			new BaseSettingsContributor(0) {

				@Override
				public void populate(
					ClientSettingsHelper clientSettingsHelper) {

					clientSettingsHelper.put(
						"cluster.routing.allocation.disk.threshold_enabled",
						"false");
				}

			});
	}

	protected void addUnicastSettingsContributor(
		EmbeddedElasticsearchConnection embeddedElasticsearchConnection) {

		if (_clusterSettingsContext == null) {
			return;
		}

		UnicastSettingsContributor unicastSettingsContributor =
			new UnicastSettingsContributor() {
				{
					setClusterSettingsContext(_clusterSettingsContext);

					activate(_elasticsearchConfigurationProperties);
				}
			};

		embeddedElasticsearchConnection.addSettingsContributor(
			unicastSettingsContributor);
	}

	protected ElasticsearchConnection createElasticsearchConnection() {
		if (LPS104115.SIDECAR_NOT_EMBEDDED) {
			return createSidecarElasticsearchConnection();
		}

		return createEmbeddedElasticsearchConnection();
	}

	protected ElasticsearchConnection createEmbeddedElasticsearchConnection() {
		EmbeddedElasticsearchConnection embeddedElasticsearchConnection =
			new EmbeddedElasticsearchConnection();

		addClusterLoggingThresholdContributor(embeddedElasticsearchConnection);
		addDiskThresholdSettingsContributor(embeddedElasticsearchConnection);
		addUnicastSettingsContributor(embeddedElasticsearchConnection);

		ClusterSettingsContext clusterSettingsContext = _clusterSettingsContext;

		if (clusterSettingsContext == null) {
			clusterSettingsContext = Mockito.mock(ClusterSettingsContext.class);
		}

		embeddedElasticsearchConnection.clusterSettingsContext =
			clusterSettingsContext;

		embeddedElasticsearchConnection.props = createProps();

		ReflectionTestUtil.setFieldValue(
			embeddedElasticsearchConnection, "_file", new FileImpl());

		BundleContext bundleContext = Mockito.mock(BundleContext.class);

		Mockito.when(
			bundleContext.getDataFile(
				EmbeddedElasticsearchConnection.JNA_TMP_DIR)
		).thenReturn(
			new File(
				SystemProperties.get(SystemProperties.TMP_DIR) + "/" +
					EmbeddedElasticsearchConnection.JNA_TMP_DIR)
		);

		embeddedElasticsearchConnection.activate(
			bundleContext, _elasticsearchConfigurationProperties);

		return embeddedElasticsearchConnection;
	}

	protected Props createProps() {
		Props props = Mockito.mock(Props.class);

		Mockito.when(
			props.get(PropsKeys.LIFERAY_HOME)
		).thenReturn(
			_tmpDirName
		);

		return props;
	}

	protected SidecarElasticsearchConnection
		createSidecarElasticsearchConnection() {

		ElasticsearchConfiguration elasticsearchConfiguration =
			ConfigurableUtil.createConfigurable(
				ElasticsearchConfiguration.class,
				HashMapBuilder.putAll(
					_elasticsearchConfigurationProperties
				).put(
					"sidecarHome", "classes/sidecar-elasticsearch"
				).build());

		return new SidecarElasticsearchConnection(
			new Sidecar(
				elasticsearchConfiguration, new LocalProcessExecutor(),
				createSidecarPaths()));
	}

	protected SidecarPaths createSidecarPaths() {
		SidecarPaths sidecarPaths = Mockito.mock(SidecarPaths.class);

		Mockito.doReturn(
			_tmpDirName
		).when(
			sidecarPaths
		).getWork();

		Mockito.doReturn(
			"classes/sidecar-elasticsearch/ext"
		).when(
			sidecarPaths
		).getLib();

		return sidecarPaths;
	}

	protected void deleteTmpDir() {
		try {
			FileUtils.deleteDirectory(new File(_tmpDirName));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected ElasticsearchConnection openElasticsearchConnection() {
		ElasticsearchConnection elasticsearchConnection =
			createElasticsearchConnection();

		elasticsearchConnection.connect();

		return elasticsearchConnection;
	}

	private ClusterSettingsContext _clusterSettingsContext;
	private Map<String, Object> _elasticsearchConfigurationProperties =
		Collections.<String, Object>emptyMap();
	private ElasticsearchConnection _elasticsearchConnection;
	private String _tmpDirName;

}