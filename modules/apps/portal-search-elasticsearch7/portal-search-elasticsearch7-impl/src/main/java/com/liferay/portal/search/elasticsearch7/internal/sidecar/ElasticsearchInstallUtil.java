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

import com.liferay.petra.string.StringBundler;

import java.io.IOException;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Wade Cao
 */
public class ElasticsearchInstallUtil {

	public static boolean downloadAndInstall(
		Path elasticsearchDeployDirectory) {

		return downloadAndInstall(elasticsearchDeployDirectory.toString());
	}

	public static boolean downloadAndInstall(
		String elasticsearchDeployDirectory) {

		if (!_elasticsearchDownloadAndInstall(elasticsearchDeployDirectory)) {
			return false;
		}

		for (String elasticsearchPluginURL : _ELASTICSEARCH_PLUGIN_URLS) {
			_pluginDownloadAndInstall(
				elasticsearchDeployDirectory, elasticsearchPluginURL);
		}

		return true;
	}

	public static boolean isElasticsearchInstalled(
		String elasticsearchDeployDirectory) {

		return FileHelperUtil.exists(elasticsearchDeployDirectory);
	}

	private static boolean _elasticsearchDownloadAndInstall(
		String elasticsearchDeployDirectory) {

		try {
			if (!FileHelperUtil.downloadArchive(
					_ELASTICSEARCH_FILE_NAME, _ELASTICSEARCH) ||
				!_installElasticsearch(
					_ELASTICSEARCH_FILE_NAME, elasticsearchDeployDirectory)) {

				return false;
			}
		}
		finally {
			FileHelperUtil.deleteIfExists(_ELASTICSEARCH_FILE_NAME);
			FileHelperUtil.deleteDirectoryIfExists(
				_TMP_PATH + "/elasticsearch-" + _ELASTICSEARCH_VERSION);
		}

		return true;
	}

	private static boolean _installElasticsearch(
		String elasticsearchDistributionFile,
		String elasticsearchDeployDirectory) {

		if (!FileHelperUtil.unarchive(
				elasticsearchDistributionFile, _TMP_PATH) ||
			!FileHelperUtil.exists(_LIB_SOURCE) ||
			!FileHelperUtil.exists(_MODULES_SOURCE) ||
			!FileHelperUtil.createDirectory(elasticsearchDeployDirectory)) {

			return false;
		}

		try {
			FileHelperUtil.copyDirectory(
				_LIB_SOURCE, elasticsearchDeployDirectory + "/lib", null);
			FileHelperUtil.copyDirectory(
				_MODULES_SOURCE, elasticsearchDeployDirectory + "/modules",
				_MODULES_SOURCE_EXCLUDE);
		}
		catch (IOException ioException) {
			if (_logger.isWarnEnabled()) {
				_logger.warn("Unable to copy files ", ioException);
			}

			return false;
		}

		return true;
	}

	private static boolean _installPlugin(
		String elasticsearchDeployDirectory, String pluginDistributionFileName,
		String pluginTmpDir) {

		String pluginDeployDirectory =
			elasticsearchDeployDirectory + "/plugins";

		String pluginDirName = pluginDistributionFileName.substring(
			0,
			pluginDistributionFileName.lastIndexOf(
				"-" + _ELASTICSEARCH_VERSION));

		String pluginTmpFileDirPath = pluginTmpDir + pluginDirName;

		String sourceFileName = _TMP_PATH + "/" + pluginDistributionFileName;

		if (!FileHelperUtil.unzip(pluginTmpFileDirPath, sourceFileName) ||
			!FileHelperUtil.exists(pluginTmpFileDirPath) ||
			!FileHelperUtil.createDirectory(pluginDeployDirectory)) {

			return false;
		}

		try {
			FileHelperUtil.copyDirectory(
				pluginTmpFileDirPath,
				pluginDeployDirectory + "/" + pluginDirName, null);
		}
		catch (IOException ioException) {
			if (_logger.isWarnEnabled()) {
				_logger.warn("Unable to copy files ", ioException);
			}

			return false;
		}

		return true;
	}

	private static void _pluginDownloadAndInstall(
		String elasticsearchDeployDirectory, String elasticsearchPluginURL) {

		String pluginTmpDir = _TMP_PATH + "/plugins/";

		if (!FileHelperUtil.createDirectory(pluginTmpDir)) {
			return;
		}

		String pluginDistributionFileName = elasticsearchPluginURL.substring(
			elasticsearchPluginURL.lastIndexOf("/") + 1);

		String pluginDistributionFilePath =
			_TMP_PATH + "/" + pluginDistributionFileName;

		try {
			if (FileHelperUtil.downloadArchive(
					pluginDistributionFilePath, elasticsearchPluginURL)) {

				_installPlugin(
					elasticsearchDeployDirectory, pluginDistributionFileName,
					pluginTmpDir);
			}
		}
		finally {
			FileHelperUtil.deleteIfExists(pluginDistributionFilePath);
			FileHelperUtil.deleteDirectoryIfExists(pluginTmpDir);
		}
	}

	private static final String _DOWNLOAD_URL =
		"https://artifacts.elastic.co/downloads/";

	private static final String _ELASTICSEARCH = StringBundler.concat(
		_DOWNLOAD_URL, "elasticsearch/elasticsearch-oss-",
		ElasticsearchInstallUtil._ELASTICSEARCH_VERSION,
		"-no-jdk-linux-x86_64.tar.gz");

	private static final String _ELASTICSEARCH_FILE_NAME =
		ElasticsearchInstallUtil._TMP_PATH + "/" +
			_ELASTICSEARCH.substring(_ELASTICSEARCH.lastIndexOf('/') + 1);

	private static final String[] _ELASTICSEARCH_PLUGIN_URLS = {
		StringBundler.concat(
			_DOWNLOAD_URL, "elasticsearch-plugins/analysis-icu/analysis-icu-",
			ElasticsearchInstallUtil._ELASTICSEARCH_VERSION, ".zip"),
		StringBundler.concat(
			_DOWNLOAD_URL,
			"elasticsearch-plugins/analysis-kuromoji/analysis-kuromoji-",
			ElasticsearchInstallUtil._ELASTICSEARCH_VERSION, ".zip"),
		StringBundler.concat(
			_DOWNLOAD_URL,
			"elasticsearch-plugins/analysis-smartcn/analysis-smartcn-",
			ElasticsearchInstallUtil._ELASTICSEARCH_VERSION, ".zip"),
		StringBundler.concat(
			_DOWNLOAD_URL,
			"elasticsearch-plugins/analysis-stempel/analysis-stempel-",
			ElasticsearchInstallUtil._ELASTICSEARCH_VERSION, ".zip")
	};

	private static final String _ELASTICSEARCH_ROOT =
		ElasticsearchInstallUtil._TMP_PATH + "/elasticsearch-" +
			ElasticsearchInstallUtil._ELASTICSEARCH_VERSION;

	private static final String _ELASTICSEARCH_VERSION = "7.3.0";

	private static final String _LIB_SOURCE = _ELASTICSEARCH_ROOT + "/lib";

	private static final String _MODULES_SOURCE =
		_ELASTICSEARCH_ROOT + "/modules";

	private static final String[] _MODULES_SOURCE_EXCLUDE = {
		_MODULES_SOURCE + "/ingest-geoip"
	};

	//private static final String _TMP_PATH = "/tmp";
	//	private static final String _TMP_PATH = SystemProperties.get(
	//		SystemProperties.TMP_DIR);

	private static final String _TMP_PATH  = System.getProperty("java.io.tmpdir");

	private static final Logger _logger = LogManager.getLogger(
		ElasticsearchInstallUtil.class);

}