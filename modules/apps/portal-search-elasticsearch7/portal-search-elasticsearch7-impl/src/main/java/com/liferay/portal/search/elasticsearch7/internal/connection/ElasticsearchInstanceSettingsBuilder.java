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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch7.internal.settings.SettingsBuilder;
import com.liferay.portal.search.elasticsearch7.internal.util.ResourceUtil;
import com.liferay.portal.search.elasticsearch7.settings.ClientSettingsHelper;
import com.liferay.portal.search.elasticsearch7.settings.SettingsContributor;

import java.net.InetAddress;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collection;
import java.util.function.Supplier;

import org.elasticsearch.common.settings.Settings;

/**
 * @author André de Oliveira
 */
public class ElasticsearchInstanceSettingsBuilder {

	public static ElasticsearchInstanceSettingsBuilder builder() {
		return new ElasticsearchInstanceSettingsBuilder();
	}

	public ElasticsearchInstanceSettingsBuilder() {
	}

	public Settings build() {
		load();

		Settings.Builder builder = _settingsBuilder.getBuilder();

		return builder.build();
	}

	public ElasticsearchInstanceSettingsBuilder clusterInitialMasterNodes(
		String clusterInitialMasterNodes) {

		_clusterInitialMasterNodes = clusterInitialMasterNodes;

		return this;
	}

	public ElasticsearchInstanceSettingsBuilder clusterName(
		String clusterName) {

		_clusterName = clusterName;

		return this;
	}

	public ElasticsearchInstanceSettingsBuilder discoverySeedHosts(
		String discoverySeedHosts) {

		_discoverySeedHosts = discoverySeedHosts;

		return this;
	}

	public ElasticsearchInstanceSettingsBuilder elasticsearchConfiguration(
		ElasticsearchConfiguration elasticsearchConfiguration) {

		_elasticsearchConfiguration = elasticsearchConfiguration;

		return this;
	}

	public ElasticsearchInstanceSettingsBuilder elasticsearchInstancePaths(
		ElasticsearchInstancePaths elasticsearchInstancePaths) {

		_elasticsearchInstancePaths = elasticsearchInstancePaths;

		return this;
	}

	public ElasticsearchInstanceSettingsBuilder httpPort(String httpPort) {
		_httpPort = httpPort;

		return this;
	}

	public ElasticsearchInstanceSettingsBuilder localBindInetAddressSupplier(
		Supplier<InetAddress> localBindInetAddressSupplier) {

		_localBindInetAddressSupplier = localBindInetAddressSupplier;

		return this;
	}

	public ElasticsearchInstanceSettingsBuilder networkHost(
		String networkHost) {

		_networkHost = networkHost;

		return this;
	}

	public ElasticsearchInstanceSettingsBuilder nodeName(String nodeName) {
		_nodeName = nodeName;

		return this;
	}

	public ElasticsearchInstanceSettingsBuilder settingsContributors(
		Collection<SettingsContributor> settingsContributors) {

		_settingsContributors = settingsContributors;

		return this;
	}

	public interface LocalBindInetAddressSupplier
		extends Supplier<InetAddress> {
	}

	protected void configureClustering() {
		put("cluster.name", _clusterName);
		put("cluster.routing.allocation.disk.threshold_enabled", false);

		if (Validator.isBlank(_clusterInitialMasterNodes)) {
			put("discovery.type", "single-node");
		}
	}

	protected void configureHttp() {
		put("http.port", _httpPort);

		put("http.cors.enabled", _elasticsearchConfiguration.httpCORSEnabled());

		if (!_elasticsearchConfiguration.httpCORSEnabled()) {
			return;
		}

		put(
			"http.cors.allow-origin",
			_elasticsearchConfiguration.httpCORSAllowOrigin());

		_settingsBuilder.loadFromSource(
			_elasticsearchConfiguration.httpCORSConfigurations());
	}

	protected void configureNetworking() {
		String networkBindHost = _elasticsearchConfiguration.networkBindHost();
		String networkHost = _elasticsearchConfiguration.networkHost();
		String networkPublishHost =
			_elasticsearchConfiguration.networkPublishHost();

		if (Validator.isNotNull(networkBindHost)) {
			put("network.bind.host", networkBindHost);
		}

		if (!Validator.isBlank(_networkHost)) {
			put("network.host", _networkHost);
		}
		else {
			if (Validator.isNull(networkBindHost) &&
				Validator.isNull(networkHost) &&
				Validator.isNull(networkPublishHost)) {

				InetAddress inetAddress = _localBindInetAddressSupplier.get();

				if (inetAddress != null) {
					networkHost = inetAddress.getHostAddress();
				}
			}

			if (Validator.isNotNull(networkHost)) {
				put("network.host", networkHost);
			}
		}

		if (Validator.isNotNull(networkPublishHost)) {
			put("network.publish_host", networkPublishHost);
		}

		String transportTcpPort =
			_elasticsearchConfiguration.transportTcpPort();

		if (Validator.isNotNull(transportTcpPort)) {
			put("transport.tcp.port", transportTcpPort);
		}

		put("transport.type", "netty4");
	}

	protected void configurePaths() {
		String work = _elasticsearchInstancePaths.getWork();

		put("path.data", work.concat("/data/elasticsearch7/indices"));

		put("path.home", toAbsolutePathString(getPathHome()));

		put("path.logs", work.concat("/logs"));
		put("path.repo", work.concat("/data/elasticsearch7/repo"));
	}

	protected void configureTestMode() {
		if (!PortalRunMode.isTestMode()) {
			return;
		}

		put("monitor.jvm.gc.enabled", StringPool.FALSE);
	}

	protected String getPathHome() {
		String home = _elasticsearchInstancePaths.getHome();

		if (!Validator.isBlank(home)) {
			return home;
		}

		String work = _elasticsearchInstancePaths.getWork();

		return work.concat("/data/elasticsearch7");
	}

	protected void load() {
		loadDefaultConfigurations();

		loadSidecarConfigurations();

		loadAdditionalConfigurations();

		loadSettingsContributors();
	}

	protected void loadAdditionalConfigurations() {
		_settingsBuilder.loadFromSource(
			_elasticsearchConfiguration.additionalConfigurations());
	}

	protected void loadDefaultConfigurations() {
		String defaultConfigurations = ResourceUtil.getResourceAsString(
			getClass(), "/META-INF/elasticsearch-optional-defaults.yml");

		_settingsBuilder.loadFromSource(defaultConfigurations);

		put("action.auto_create_index", false);
		put(
			"bootstrap.memory_lock",
			_elasticsearchConfiguration.bootstrapMlockAll());

		configureClustering();

		configureHttp();

		configureNetworking();

		put("node.data", true);
		put("node.ingest", true);
		put("node.master", true);
		put("node.name", _nodeName);

		configurePaths();

		configureTestMode();
	}

	protected void loadSettingsContributors() {
		ClientSettingsHelper clientSettingsHelper = new ClientSettingsHelper() {

			@Override
			public void put(String setting, String value) {
				_settingsBuilder.put(setting, value);
			}

			@Override
			public void putArray(String setting, String... values) {
				_settingsBuilder.putList(setting, values);
			}

		};

		for (SettingsContributor settingsContributor : _settingsContributors) {
			settingsContributor.populate(clientSettingsHelper);
		}
	}

	protected void loadSidecarConfigurations() {
		put("bootstrap.system_call_filter", false);
		put("node.store.allow_mmap", false);

		if (!Validator.isBlank(_clusterInitialMasterNodes)) {
			put("cluster.initial_master_nodes", _clusterInitialMasterNodes);
		}

		if (!Validator.isBlank(_discoverySeedHosts)) {
			put("discovery.seed_hosts", _discoverySeedHosts);
		}
	}

	protected void put(String key, boolean value) {
		_settingsBuilder.put(key, value);
	}

	protected void put(String key, String value) {
		_settingsBuilder.put(key, value);
	}

	protected String toAbsolutePathString(String pathString) {
		Path path = Paths.get(pathString);

		return String.valueOf(path.toAbsolutePath());
	}

	private String _clusterInitialMasterNodes;
	private String _clusterName;
	private String _discoverySeedHosts;
	private ElasticsearchConfiguration _elasticsearchConfiguration;
	private ElasticsearchInstancePaths _elasticsearchInstancePaths;
	private String _httpPort;
	private Supplier<InetAddress> _localBindInetAddressSupplier;
	private String _networkHost;
	private String _nodeName;
	private final SettingsBuilder _settingsBuilder = new SettingsBuilder(
		Settings.builder());
	private Collection<SettingsContributor> _settingsContributors;

}