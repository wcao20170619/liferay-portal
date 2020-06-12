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

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.concurrent.FutureListener;
import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.process.ClassPathUtil;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.process.ProcessLog;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch7.internal.cluster.ClusterSettingsContext;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchInstancePaths;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchInstanceSettingsBuilder;
import com.liferay.portal.search.elasticsearch7.internal.util.ResourceUtil;
import com.liferay.portal.search.elasticsearch7.settings.SettingsContributor;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.common.settings.Settings;
import org.objectweb.asm.Opcodes;

/**
 * @author Tina Tian
 */
public class Sidecar {

	public Sidecar(
		ClusterSettingsContext clusterSettingsContext,
		ElasticsearchConfiguration elasticsearchConfiguration,
		ElasticsearchInstancePaths elasticsearchInstancePaths, String httpPort,
		ProcessExecutor processExecutor,
		ProcessExecutorPaths processExecutorPaths,
		Collection<SettingsContributor> settingsContributors) {

		_clusterSettingsContext = clusterSettingsContext;
		_dataHomePath = elasticsearchInstancePaths.getDataPath();
		_elasticsearchConfiguration = elasticsearchConfiguration;
		_elasticsearchInstancePaths = elasticsearchInstancePaths;
		_httpPort = httpPort;
		_indicesPath = elasticsearchInstancePaths.getIndicesPath();
		_processExecutor = processExecutor;
		_processExecutorPaths = processExecutorPaths;
		_settingsContributors = settingsContributors;
		_sidecarHomePath = elasticsearchInstancePaths.getHomePath();
	}

	public String getNetworkHostAddress() {
		if (_address == null) {
			try {
				_address = _addressNoticeableFuture.get();

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Sidecar Elasticsearch ", getNodeName(), " is at ",
							_address));
				}
			}
			catch (Exception exception) {
				_processChannel.write(new StopSidecarProcessCallable());

				throw new IllegalStateException(
					"Unable to get sidecar Elasticsearch network host address",
					exception);
			}
		}

		return _address;
	}

	public void start() {
		if (_log.isInfoEnabled()) {
			_log.info("Starting sidecar Elasticsearch");
		}

		String sidecarLibClassPath = _createClasspath(
			_sidecarHomePath.resolve("lib"), path -> true);

		try {
			_processChannel = _processExecutor.execute(
				_createProcessConfig(sidecarLibClassPath),
				new SidecarMainProcessCallable(
					_elasticsearchConfiguration.sidecarHeartbeatInterval(),
					_getModifiedClasses(sidecarLibClassPath)));
		}
		catch (ProcessException processException) {
			throw new RuntimeException(
				"Unable to start sidecar Elasticsearch process",
				processException);
		}

		NoticeableFuture<Serializable> noticeableFuture =
			_processChannel.getProcessNoticeableFuture();

		_restartFutureListener = new RestartFutureListener();

		noticeableFuture.addFutureListener(_restartFutureListener);

		_addressNoticeableFuture = _processChannel.write(
			new StartSidecarProcessCallable(_getSidecarArguments()));
	}

	public void stop() {
		PathUtil.deleteDir(_sidecarTempDirPath);

		if (_processChannel == null) {
			return;
		}

		NoticeableFuture<Serializable> noticeableFuture =
			_processChannel.getProcessNoticeableFuture();

		noticeableFuture.removeFutureListener(_restartFutureListener);

		_processChannel.write(new StopSidecarProcessCallable());

		try {
			noticeableFuture.get(
				_elasticsearchConfiguration.sidecarShutdownTimeout(),
				TimeUnit.MILLISECONDS);
		}
		catch (Exception exception) {
			if (!noticeableFuture.isDone()) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Forcibly shutdown sidecar Elasticsearch process ",
							"because it did not shut down in ",
							_elasticsearchConfiguration.
								sidecarShutdownTimeout(),
							" ms"));
				}

				noticeableFuture.cancel(true);
			}
		}

		_processChannel = null;
	}

	protected static boolean fileNameContains(Path path, String s) {
		String name = String.valueOf(path.getFileName());

		if (name.contains(s)) {
			return true;
		}

		return false;
	}

	protected void consumeProcessLog(ProcessLog processLog) {
		if (ProcessLog.Level.DEBUG == processLog.getLevel()) {
			if (_log.isDebugEnabled()) {
				_log.debug(processLog.getMessage(), processLog.getThrowable());
			}
		}
		else if (ProcessLog.Level.INFO == processLog.getLevel()) {
			if (_log.isInfoEnabled()) {
				_log.info(processLog.getMessage(), processLog.getThrowable());
			}
		}
		else if (ProcessLog.Level.WARN == processLog.getLevel()) {
			if (_log.isWarnEnabled()) {
				_log.warn(processLog.getMessage(), processLog.getThrowable());
			}
		}
		else {
			_log.error(processLog.getMessage(), processLog.getThrowable());
		}
	}
	
//	protected String getBootstrapClassPath() {
//		return _createClasspath(
//			_processExecutorPaths.getLibPath(),
//			path -> fileNameContains(path, "petra"));
//	}

	protected String getBootstrapClassPath() {
		String classpathForPetra =  _createClasspath(
			_processExecutorPaths.getLibPath(),
			path -> fileNameContains(path, "petra"));
		
		if ((_log.isWarnEnabled()) && 
			Validator.isBlank(classpathForPetra)) {
			_log.warn("The petra lib classpath is missing.");
		}
			
		return classpathForPetra;
	}

	protected URL getBundleURL() {
		ProtectionDomain protectionDomain = Sidecar.class.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		return codeSource.getLocation();
	}

	protected String getClusterName() {
		return _elasticsearchConfiguration.clusterName();
	}

	protected Path getDataHomePath() {
		return _dataHomePath;
	}

	protected HashMap<String, String> getEnvironment() {
		return HashMapBuilder.putAll(
			System.getenv()
		).put(
			"HOSTNAME", "localhost"
		).build();
	}

	protected String getHttpPort() {
		if (!Validator.isBlank(_httpPort)) {
			return _httpPort;
		}

		if (!Validator.isBlank(_elasticsearchConfiguration.sidecarHttpPort())) {
			return _elasticsearchConfiguration.sidecarHttpPort();
		}

		int embeddedHttpPort = _elasticsearchConfiguration.embeddedHttpPort();

		return embeddedHttpPort + StringPool.DASH + embeddedHttpPort;
	}

	protected String getLogProperties() {
		return StringPool.BLANK;
	}

	protected String getNodeName() {
		return "liferay";
	}

	protected Path getPathData() {
		return _indicesPath;
	}

	protected URL getSecurityPolicyURL(URL bundleURL) {
		URLClassLoader urlClassLoader = new URLClassLoader(
			new URL[] {bundleURL});

		return urlClassLoader.findResource("META-INF/sidecar.policy");
	}

	protected Settings getSettings() {
		return ElasticsearchInstanceSettingsBuilder.builder(
		).clusterName(
			getClusterName()
		).elasticsearchConfiguration(
			_elasticsearchConfiguration
		).elasticsearchInstancePaths(
			_elasticsearchInstancePaths
		).httpPort(
			getHttpPort()
		).clusterInitialMasterNodes(
			getNodeName()
		).localBindInetAddressSupplier(
			_clusterSettingsContext::getLocalBindInetAddress
		).nodeName(
			getNodeName()
		).settingsContributors(
			_settingsContributors
		).build();
	}

	private String _createClasspath(
		Path dirPath, DirectoryStream.Filter<Path> filter) {

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				dirPath, filter)) {

			StringBundler sb = new StringBundler();

			directoryStream.forEach(
				path -> {
					sb.append(path);
					sb.append(File.pathSeparator);
				});

			if (sb.index() > 0) {
				sb.setIndex(sb.index() - 1);
			}

			return sb.toString();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to iterate " + dirPath, ioException);
		}
	}

	private ProcessConfig _createProcessConfig(String sidecarLibClassPath) {
		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		URL bundleURL = getBundleURL();

		return builder.setArguments(
			_getJVMArguments(bundleURL)
		).setBootstrapClassPath(
			getBootstrapClassPath()
		).setEnvironment(
			getEnvironment()
		).setProcessLogConsumer(
			this::consumeProcessLog
		).setReactClassLoader(
			Sidecar.class.getClassLoader()
		).setRuntimeClassPath(
			StringBundler.concat(
				sidecarLibClassPath, File.pathSeparator, bundleURL.getPath(),
				File.pathSeparator, getBootstrapClassPath())
		).build();
	}

	private List<String> _getJVMArguments(URL bundleURL) {
		List<String> arguments = new ArrayList<>();

		for (String jvmOption :
				_elasticsearchConfiguration.sidecarJVMOptions()) {

			arguments.add(jvmOption);
		}

		if (_elasticsearchConfiguration.sidecarDebug()) {
			arguments.add(_elasticsearchConfiguration.sidecarDebugSettings());
		}

		try {
			_sidecarTempDirPath = Files.createTempDirectory("sidecar");
		}
		catch (IOException ioException) {
			throw new IllegalStateException(
				"Unable to create temp folder", ioException);
		}

		Path configFolder = _sidecarTempDirPath.resolve("config");

		try {
			Files.createDirectories(configFolder);

			Files.write(
				configFolder.resolve("log4j2.properties"),
				Arrays.asList(
					"logger.bootstrapchecks.name=org.elasticsearch.bootstrap." +
						"BootstrapChecks",
					"logger.bootstrapchecks.level=error",
					"logger.deprecation.name=org.elasticsearch.deprecation",
					"logger.deprecation.level=error", getLogProperties(),
					ResourceUtil.getResourceAsString(
						Sidecar.class, "/log4j2.properties")));
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to copy log4j2.properties to " + configFolder,
				ioException);
		}

		arguments.add("-Des.path.conf=" + configFolder);
		arguments.add("-Des.networkaddress.cache.ttl=60");
		arguments.add("-Des.networkaddress.cache.negative.ttl=10");
		arguments.add("-Dlog4j.shutdownHookEnabled=false");
		arguments.add("-Dlog4j2.disable.jmx=true");
		arguments.add("-Dio.netty.allocator.type=unpooled");
		arguments.add("-Dio.netty.allocator.numDirectArenas=0");
		arguments.add("-Dio.netty.noUnsafe=true");
		arguments.add("-Dio.netty.noKeySetOptimization=true");
		arguments.add("-Dio.netty.recycler.maxCapacityPerThread=0");
		arguments.add("-Dfile.encoding=UTF-8");
		arguments.add("-Djava.io.tmpdir=" + _sidecarTempDirPath);

		arguments.add(
			"-Djava.security.policy=" +
				String.valueOf(getSecurityPolicyURL(bundleURL)));

		arguments.add("-Djna.nosys=true");

		return arguments;
	}

	private Map<String, byte[]> _getModifiedClasses(
		String sidecarLibClassPath) {

		Map<String, byte[]> modifiedClasses = new HashMap<>();

		try {
			ClassLoader classLoader = new URLClassLoader(
				ClassPathUtil.getClassPathURLs(sidecarLibClassPath), null);

			modifiedClasses.put(
				"org.elasticsearch.bootstrap.Natives",
				ClassModificationUtil.getModifiedClassBytes(
					"org.elasticsearch.bootstrap.Natives",
					"definitelyRunningAsRoot",
					methodVisitor -> {
						methodVisitor.visitCode();
						methodVisitor.visitInsn(Opcodes.ICONST_0);
						methodVisitor.visitInsn(Opcodes.IRETURN);
					},
					classLoader));

			modifiedClasses.put(
				"org.elasticsearch.common.settings.KeyStoreWrapper",
				ClassModificationUtil.getModifiedClassBytes(
					"org.elasticsearch.common.settings.KeyStoreWrapper", "save",
					methodVisitor -> {
						methodVisitor.visitCode();
						methodVisitor.visitInsn(Opcodes.RETURN);
					},
					classLoader));
		}
		catch (Exception exception) {
			_log.error("Unable to modify classes", exception);
		}

		return modifiedClasses;
	}

	private String[] _getSidecarArguments() {
		Settings settings = getSettings();

		StringBundler sb = new StringBundler(2 * settings.size() + 1);

		sb.append("Sidecar Elasticsearch properties : {");

		List<String> arguments = new ArrayList<>();

		for (String key : settings.keySet()) {
			arguments.add("-E");

			String value = settings.get(key);

			if (Validator.isNotNull(value)) {
				String keyValue = StringBundler.concat(
					key, StringPool.EQUAL, value);

				arguments.add(keyValue);

				sb.append(keyValue);

				sb.append(StringPool.COMMA);
			}
		}

		sb.setStringAt(StringPool.CLOSE_CURLY_BRACE, sb.index() - 1);

		if (_log.isInfoEnabled()) {
			_log.info(sb.toString());
		}

		return arguments.toArray(new String[0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(Sidecar.class);

	private String _address;
	private NoticeableFuture<String> _addressNoticeableFuture;
	private final ClusterSettingsContext _clusterSettingsContext;
	private final Path _dataHomePath;
	private final ElasticsearchConfiguration _elasticsearchConfiguration;
	private final ElasticsearchInstancePaths _elasticsearchInstancePaths;
	private final String _httpPort;
	private final Path _indicesPath;
	private ProcessChannel<Serializable> _processChannel;
	private final ProcessExecutor _processExecutor;
	private final ProcessExecutorPaths _processExecutorPaths;
	private FutureListener<Serializable> _restartFutureListener;
	private final Collection<SettingsContributor> _settingsContributors;
	private final Path _sidecarHomePath;
	private Path _sidecarTempDirPath;

	private class RestartFutureListener
		implements FutureListener<Serializable> {

		@Override
		public void complete(Future<Serializable> future) {
			try {
				future.get();
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Sidecar Elasticsearch process is aborted", exception);
				}
			}

			SidecarComponentUtil.disableSidecarElasticsearchConnectionManager();

			if (_log.isInfoEnabled()) {
				_log.info("Restarting sidecar Elasticsearch process");
			}

			SidecarComponentUtil.enableSidecarElasticsearchConnectionManager();
		}

	}

}