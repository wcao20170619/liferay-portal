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

import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch7.internal.cluster.ClusterSettingsContext;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.ClusterableSidecar;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.ProcessExecutorPathsImpl;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.Sidecar;
import com.liferay.portal.search.elasticsearch7.settings.SettingsContributor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Tina Tian
 */
@Component(
	configurationPid = "com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration",
	enabled = true, immediate = true, service = {}
)
public class SidecarElasticsearchConnectionManager {

	@Activate
	protected void activate(ComponentContext componentContext) {
		BundleContext bundleContext = componentContext.getBundleContext();

		ElasticsearchConfiguration elasticsearchConfiguration =
			ConfigurableUtil.createConfigurable(
				ElasticsearchConfiguration.class,
				componentContext.getProperties());

		ElasticsearchConnection elasticsearchConnection;

		if (elasticsearchConfiguration.operationMode() ==
				com.liferay.portal.search.elasticsearch7.configuration.
					OperationMode.EMBEDDED) {

			if (_log.isWarnEnabled()) {
				StringBundler sb = new StringBundler(8);

				sb.append("Liferay is configured to use Elasticsearch engine ");
				sb.append("running in a child process of current process ");
				sb.append("named as sidecar. Do NOT use sidecar in ");
				sb.append("production. Sidecar is useful for development and ");
				sb.append("demonstration purposes. Refer to the ");
				sb.append("documentation for details on the limitations of ");
				sb.append("sidecar. Remote Elasticsearch connections can be ");
				sb.append("configured in the Control Panel.");

				_log.warn(sb.toString());
			}

			ElasticsearchInstancePaths elasticsearchInstancePaths =
				new SidecarElasticsearchInstancePaths(
					_props, elasticsearchConfiguration);

			if (_clusterExecutor.isEnabled()) {
				ClusterableSidecar clusterableSidecar = new ClusterableSidecar(
					_clusterExecutor, _clusterMasterExecutor,
					_clusterSettingsContext, elasticsearchConfiguration,
					elasticsearchInstancePaths, _jsonFactory, _processExecutor,
					new ProcessExecutorPathsImpl(_props),
					_settingsContributors);

				_clusterableSidecarsOSGiServiceserviceRegistration =
					bundleContext.registerService(
						new String[] {
							ClusterableSidecar.class.getName(),
							IdentifiableOSGiService.class.getName()
						},
						clusterableSidecar, null);

				elasticsearchConnection = new SidecarElasticsearchConnection(
					clusterableSidecar);
			}
			else {
				elasticsearchConnection = new SidecarElasticsearchConnection(
					new Sidecar(
						_clusterSettingsContext, elasticsearchConfiguration,
						elasticsearchInstancePaths, null, _processExecutor,
						new ProcessExecutorPathsImpl(_props),
						_settingsContributors));
			}
		}
		else {
			elasticsearchConnection = ProxyFactory.newDummyInstance(
				ElasticsearchConnection.class);
		}

		_serviceRegistration = bundleContext.registerService(
			ElasticsearchConnection.class, elasticsearchConnection,
			MapUtil.singletonDictionary(
				"operation.mode", String.valueOf(OperationMode.EMBEDDED)));
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(operation.mode=EMBEDDED)"
	)
	protected void addSettingsContributor(
		SettingsContributor settingsContributor) {

		_settingsContributors.add(settingsContributor);
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();

		if (_clusterableSidecarsOSGiServiceserviceRegistration != null) {
			_clusterableSidecarsOSGiServiceserviceRegistration.unregister();
		}
	}

	protected void removeSettingsContributor(
		SettingsContributor settingsContributor) {

		_settingsContributors.remove(settingsContributor);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SidecarElasticsearchConnectionManager.class);

	private ServiceRegistration<?>
		_clusterableSidecarsOSGiServiceserviceRegistration;

	@Reference
	private ClusterExecutor _clusterExecutor;

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	@Reference
	private ClusterSettingsContext _clusterSettingsContext;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ProcessExecutor _processExecutor;

	@Reference
	private Props _props;

	private ServiceRegistration<ElasticsearchConnection> _serviceRegistration;
	private final Set<SettingsContributor> _settingsContributors =
		new ConcurrentSkipListSet<>();

	private static class SidecarElasticsearchInstancePaths
		implements ElasticsearchInstancePaths {

		@Override
		public String getHome() {
			return _home;
		}

		@Override
		public String getWork() {
			return _work;
		}

		protected static Path resolveHomePath(
			String work,
			ElasticsearchConfiguration elasticsearchConfiguration) {

			Path workPath = Paths.get(work);

			workPath = workPath.toAbsolutePath();

			Path sidecarHomePath = workPath.resolve(
				elasticsearchConfiguration.sidecarHome());

			if (!Files.isDirectory(sidecarHomePath)) {
				sidecarHomePath = Paths.get(
					elasticsearchConfiguration.sidecarHome());

				if (!Files.isDirectory(sidecarHomePath)) {
					throw new IllegalArgumentException(
						"Sidecar Elasticsearch home " +
							elasticsearchConfiguration.sidecarHome() +
								" does not exist");
				}
			}

			return sidecarHomePath;
		}

		private SidecarElasticsearchInstancePaths(
			Props props,
			ElasticsearchConfiguration elasticsearchConfiguration) {

			String work = props.get(PropsKeys.LIFERAY_HOME);

			_home = String.valueOf(
				resolveHomePath(work, elasticsearchConfiguration));
			_work = work;
		}

		private final String _home;
		private final String _work;

	}

}