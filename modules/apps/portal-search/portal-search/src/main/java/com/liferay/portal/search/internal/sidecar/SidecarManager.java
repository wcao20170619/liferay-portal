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

package com.liferay.portal.search.internal.sidecar;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.configuration.SidecarManagerConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author André de Oliveira
 * @author Wade Cao
 */
@Component(
	configurationPid = "com.liferay.portal.search.configuration.SidecarManagerConfiguration",
	immediate = true, service = {}
)
public class SidecarManager {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_sidecarManagerConfiguration = ConfigurableUtil.createConfigurable(
			SidecarManagerConfiguration.class, properties);

		if (_sidecarManagerConfiguration.enabled() &&
			FileUtil.exists(_getFullPathCommand())) {

			try {
				_activateProcess();
			}
			catch (IOException ioe) {
				_log.error("Unable to start the process on activation", ioe);
			}
		}
		else if (_isProcessAlive()) {
			_process.destroyForcibly();

			_process = null;
		}
	}
	
	 @Deactivate
     protected void deactivate() {
//		if (_isProcessAlive()) {
//			_process.destroyForcibly();			
//		}
     }


	private void _activateProcess() throws IOException {
		_process = new ProcessBuilder().directory(
			_getAppDirFile()
		).command(
			_getCommand()
		).inheritIO(
		).start();

		Runtime runtime = Runtime.getRuntime();

		runtime.addShutdownHook(new Thread(_process::destroy));
	}

	private String _getAppDir() {
		return _sidecarManagerConfiguration.appDir();
	}

	private File _getAppDirFile() {
		return new File(_getRootDir(), _getAppDir());
	}

	private String _getCommand() {
		String command = _getStartupCommand();

		String envVar = _sidecarManagerConfiguration.javaOpts();

		if (!Validator.isBlank(envVar)) {
			command = StringBundler.concat(
				"ES_JAVA_OPTS=\"", envVar, "\" ", command);
		}

		envVar = _sidecarManagerConfiguration.pathConf();

		if (!Validator.isBlank(envVar)) {
			command = StringBundler.concat(
				" ES_PATH_CONF=\"", envVar, "\" ", command);
		}

		return command;
	}

	private String _getFullPathCommand() {
		return StringBundler.concat(
			_getRootDir(), "/", _getAppDir(), "/", _getStartupCommand());
	}

	private String _getRootDir() {
		String rootDir = _sidecarManagerConfiguration.rootDir();

		if (Validator.isBlank(rootDir)) {
			rootDir = System.getProperty("user.home");
		}

		return rootDir;
	}

	private String _getStartupCommand() {
		return _sidecarManagerConfiguration.startupCommand();
	}

	private boolean _isProcessAlive() {
		if ((_process != null) && _process.isAlive()) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(SidecarManager.class);

	private Process _process;
	private volatile SidecarManagerConfiguration _sidecarManagerConfiguration;

}