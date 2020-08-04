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

import com.liferay.portal.kernel.util.OSDetector;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Wade Cao
 */
public class UncompressUtil {

	public static void unarchive(
			Path tarGzFilePath, Path destinationDirectoryPath)
		throws IOException {

		Files.createDirectories(destinationDirectoryPath);

		try (InputStream inputStream = Files.newInputStream(tarGzFilePath);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
				inputStream, _CHARS_BUFFER_SIZE);
			GzipCompressorInputStream gzipCompressorInputStream =
				new GzipCompressorInputStream(bufferedInputStream);
			TarArchiveInputStream tarArchiveInputStream =
				new TarArchiveInputStream(gzipCompressorInputStream)) {

			TarArchiveEntry tarArchiveEntry = null;

			while ((tarArchiveEntry =
						tarArchiveInputStream.getNextTarEntry()) != null) {

				if (tarArchiveInputStream.canReadEntryData(tarArchiveEntry)) {
					Path path = destinationDirectoryPath.resolve(
						tarArchiveEntry.getName());

					if (tarArchiveEntry.isDirectory()) {
						Files.createDirectory(path);
					}
					else {
						Files.copy(tarArchiveInputStream, path);
					}

					_setFilePermission(path);
				}
				else {
					if (_logger.isWarnEnabled()) {
						_logger.warn(
							"Unable to read " + tarArchiveEntry.getName() +
								" from tar archive entry");
					}
				}
			}
		}
	}

	public static void unzip(Path zipFilePath, Path destinationDirectoryPath)
		throws IOException {

		Files.createDirectories(destinationDirectoryPath);

		try (InputStream inputStream = Files.newInputStream(zipFilePath);
			ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

			ZipEntry zipEntry;

			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				Path path = destinationDirectoryPath.resolve(
					zipEntry.getName());

				if (zipEntry.isDirectory()) {
					Files.createDirectory(path);
				}
				else {
					Files.copy(zipInputStream, path);
				}

				_setFilePermission(path);
			}
		}
	}

	private static void _setFilePermission(Path path) throws IOException {
		if (OSDetector.isWindows()) {
			File file = path.toFile();

			file.setExecutable(true);
			file.setReadable(true);
			file.setWritable(true);
		}
		else {
			Files.setPosixFilePermissions(
				path, PosixFilePermissions.fromString("rwxrwxrwx"));
		}
	}

	private static final int _CHARS_BUFFER_SIZE = 8192;

	private static final Logger _logger = LogManager.getLogger(
		UncompressUtil.class);

}