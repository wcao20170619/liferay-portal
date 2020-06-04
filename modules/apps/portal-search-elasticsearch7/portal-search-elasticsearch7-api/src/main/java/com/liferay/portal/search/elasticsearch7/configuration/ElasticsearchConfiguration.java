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

package com.liferay.portal.search.elasticsearch7.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Michael C. Han
 */
@ExtendedObjectClassDefinition(category = "search")
@Meta.OCD(
	id = "com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration",
	localization = "content/Language",
	name = "elasticsearch7-configuration-name"
)
@ProviderType
public interface ElasticsearchConfiguration {

	@Meta.AD(
		deflt = "SIDECAR", description = "operation-mode-help",
		name = "operation-mode", optionLabels = {"remote", "sidecar"},
		optionValues = {"REMOTE", "SIDECAR"}, required = false
	)
	public String operationMode();

	@Meta.AD(
		description = "remote-cluster-connection-id-help",
		name = "remote-cluster-connection-id", required = false
	)
	public String remoteClusterConnectionId();

	@Meta.AD(
		deflt = "liferay-", description = "index-name-prefix-help",
		name = "index-name-prefix", required = false
	)
	public String indexNamePrefix();

	@Meta.AD(
		deflt = "", description = "number-of-index-replicas-help",
		name = "number-of-index-replicas", required = false
	)
	public String indexNumberOfReplicas();

	@Meta.AD(
		deflt = "", description = "number-of-index-shards-help",
		name = "number-of-index-shards", required = false
	)
	public String indexNumberOfShards();

	@Meta.AD(
		description = "additional-index-configurations-help",
		name = "additional-index-configurations", required = false
	)
	public String additionalIndexConfigurations();

	@Meta.AD(
		description = "additional-type-mappings-help",
		name = "additional-type-mappings", required = false
	)
	public String additionalTypeMappings();

	@Meta.AD(
		description = "override-type-mappings-help",
		name = "override-type-mappings", required = false
	)
	public String overrideTypeMappings();

	@Meta.AD(
		deflt = "true", description = "log-exceptions-only-help",
		name = "log-exceptions-only", required = false
	)
	public boolean logExceptionsOnly();

	@Meta.AD(
		deflt = "ERROR", description = "rest-client-logger-level-help",
		name = "rest-client-logger-level", required = false
	)
	public RESTClientLoggerLevel restClientLoggerLevel();

	@Meta.AD(
		deflt = "LiferayElasticsearchCluster",
		description = "cluster-name-help", name = "cluster-name",
		required = false
	)
	public String clusterName();

	@Meta.AD(
		deflt = "false", description = "bootstrap-mlockall-help",
		name = "bootstrap-mlockall", required = false
	)
	public boolean bootstrapMlockAll();

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *			 #sidecarHttpPort()}
	 */
	@Deprecated
	@Meta.AD(
		deflt = "9201", description = "embedded-http-port-help",
		name = "embedded-http-port", required = false
	)
	public int embeddedHttpPort();

	@Meta.AD(
		deflt = "9200-9300", description = "sidecar-http-port-help",
		name = "sidecar-http-port", required = false
	)
	public String sidecarHttpPort();

	@Meta.AD(
		deflt = "9300-9400",
		description = "discovery-zen-ping-unicast-hosts-port-help",
		name = "discovery-zen-ping-unicast-hosts-port", required = false
	)
	public String discoveryZenPingUnicastHostsPort();

	@Meta.AD(
		deflt = "", description = "network-host-help", name = "network-host",
		required = false
	)
	public String networkHost();

	@Meta.AD(
		deflt = "", description = "network-bind-host-help",
		name = "network-bind-host", required = false
	)
	public String networkBindHost();

	@Meta.AD(
		deflt = "", description = "network-publish-host-help",
		name = "network-publish-host", required = false
	)
	public String networkPublishHost();

	@Meta.AD(
		deflt = "", description = "transport-tcp-port-help",
		name = "transport-tcp-port", required = false
	)
	public String transportTcpPort();

	@Meta.AD(
		description = "additional-configurations-help",
		name = "additional-configurations", required = false
	)
	public String additionalConfigurations();

	@Meta.AD(
		deflt = "true", description = "http-cors-enabled-help",
		name = "http-cors-enabled", required = false
	)
	public boolean httpCORSEnabled();

	@Meta.AD(
		deflt = "/https?:\\/\\/localhost(:[0-9]+)?/",
		description = "http-cors-allow-origin-help",
		name = "http-cors-allow-origin", required = false
	)
	public String httpCORSAllowOrigin();

	@Meta.AD(
		description = "http-cors-configurations-help",
		name = "http-cors-configurations", required = false
	)
	public String httpCORSConfigurations();

	@Meta.AD(
		deflt = "false", description = "sidecar-debug-help",
		name = "sidecar-debug", required = false
	)
	public boolean sidecarDebug();

	@Meta.AD(
		deflt = "-agentlib:jdwp\\=transport\\=dt_socket\\,address\\=8001\\,server\\=y\\,suspend\\=y\\,quiet\\=y",
		description = "sidecar-debug-settings-help",
		name = "sidecar-debug-settings", required = false
	)
	public String sidecarDebugSettings();

	@Meta.AD(
		deflt = "10000", description = "sidecar-heartbeat-interval-help",
		name = "sidecar-heartbeat-interval", required = false
	)
	public long sidecarHeartbeatInterval();

	@Meta.AD(
		deflt = "elasticsearch7", description = "sidecar-home-help",
		name = "sidecar-home", required = false
	)
	public String sidecarHome();

	@Meta.AD(
		deflt = "-Xms1g|-Xmx1g|-XX:+AlwaysPreTouch",
		description = "sidecar-jvm-options-help", name = "sidecar-jvm-options",
		required = false
	)
	public String[] sidecarJVMOptions();

	@Meta.AD(
		deflt = "10000", description = "sidecar-shutdown-timeout-help",
		name = "sidecar-shutdown-timeout", required = false
	)
	public long sidecarShutdownTimeout();

}