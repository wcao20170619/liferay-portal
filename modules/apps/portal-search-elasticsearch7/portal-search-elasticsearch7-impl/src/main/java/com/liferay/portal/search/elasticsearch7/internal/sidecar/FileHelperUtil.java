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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.URL;

import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermissions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Wade Cao
 */
public class FileHelperUtil {

	public static void copyDirectory(
			final String from, final String to, final String[] excludedList)
		throws IOException {

		List<Path> excludedPathList = _convertStringToPath(excludedList);

		Path fromPath = Paths.get(from);

		Path toPath = Paths.get(to);

		if (Files.exists(toPath)) {
			PathUtil.deleteDir(toPath);
		}

		final Map<Path, FileTime> fileTimes = new HashMap<>();

		try {
			Files.walkFileTree(
				fromPath,
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult postVisitDirectory(
							Path dir, IOException ioException)
						throws IOException {

						Files.setLastModifiedTime(
							toPath.resolve(fromPath.relativize(dir)),
							fileTimes.remove(dir));

						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult preVisitDirectory(
							Path dir, BasicFileAttributes basicFileAttributes)
						throws IOException {

						if (excludedPathList.contains(dir)) {
							return FileVisitResult.CONTINUE;
						}

						Files.copy(
							dir, toPath.resolve(fromPath.relativize(dir)),
							StandardCopyOption.COPY_ATTRIBUTES,
							StandardCopyOption.REPLACE_EXISTING);

						fileTimes.put(dir, Files.getLastModifiedTime(dir));

						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(
							Path file, BasicFileAttributes basicFileAttributes)
						throws IOException {

						if (excludedPathList.contains(file.getParent())) {
							return FileVisitResult.CONTINUE;
						}

						Path toFile = toPath.resolve(fromPath.relativize(file));

						Files.copy(
							file, toFile, StandardCopyOption.COPY_ATTRIBUTES,
							StandardCopyOption.REPLACE_EXISTING);

						return FileVisitResult.CONTINUE;
					}

				});
		}
		catch (IOException ioException) {
			PathUtil.deleteDir(toPath);

			throw ioException;
		}
	}

	public static boolean createDirectory(String directory) {
		Path directoryPath = Paths.get(directory);

		if (Files.exists(directoryPath)) {
			return true;
		}

		try {
			Files.createDirectory(directoryPath);

			return true;
		}
		catch (IOException ioException) {
			if (_logger.isWarnEnabled()) {
				_logger.warn(
					"Unable to create directory " + directoryPath, ioException);
			}

			return false;
		}
	}

	public static void deleteDirectoryIfExists(String directory) {
		Path directoryPath = Paths.get(directory);

		if (Files.notExists(directoryPath)) {
			return;
		}

		PathUtil.deleteDir(directoryPath);
	}

	public static void deleteIfExists(String fileName) {
		Path filePath = Paths.get(fileName);

		try {
			Files.deleteIfExists(filePath);
		}
		catch (IOException ioException) {
			if (_logger.isWarnEnabled()) {
				_logger.warn("Unable to delete " + filePath, ioException);
			}
		}
	}

	public static boolean downloadArchive(String fileName, String urlString) {
		FileOutputStream fileOutputStream = null;
		File filePath = new File(fileName);
		ReadableByteChannel readableByteChannel = null;
		URL url = null;

		try {
			url = new URL(urlString);

			readableByteChannel = Channels.newChannel(url.openStream());

			fileOutputStream = new FileOutputStream(filePath);

			FileChannel fileChannel = fileOutputStream.getChannel();

			fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		}
		catch (IOException ioException) {
			if (_logger.isWarnEnabled()) {
				_logger.warn(
					"Problem occured while downloading the file=" +
						ioException.getMessage(),
					ioException);
			}

			return false;
		}
		finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}

				if (readableByteChannel != null) {
					readableByteChannel.close();
				}
			}
			catch (IOException ioException) {
				if (_logger.isWarnEnabled()) {
					_logger.warn(
						"Problem occured while closing the object=" +
							ioException.getMessage(),
						ioException);
				}
			}
		}

		return true;
	}

	public static boolean exists(String fileName) {
		Path path = Paths.get(fileName);

		return Files.exists(path);
	}

	public static boolean unarchive(
		String archiveFileName, String destinationDirectory) {

		File archiveFile = new File(archiveFileName);

		File destinationDirectoryFile = new File(destinationDirectory);

		if (!destinationDirectoryFile.exists()) {
			destinationDirectoryFile.mkdirs();
		}

		try (FileInputStream fileInputStream = new FileInputStream(archiveFile);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
				fileInputStream, _CHARS_BUFFER_SIZE);
			GzipCompressorInputStream gzipCompressorInputStream =
				new GzipCompressorInputStream(bufferedInputStream);
			TarArchiveInputStream tarArchiveInputStream =
				new TarArchiveInputStream(gzipCompressorInputStream)) {

			TarArchiveEntry tarArchiveEntry =
				tarArchiveInputStream.getNextTarEntry();

			while (tarArchiveEntry != null) {
				if (tarArchiveInputStream.canReadEntryData(tarArchiveEntry)) {
					if (tarArchiveEntry.isDirectory()) {
						_unarchiveDirectory(
							destinationDirectoryFile, tarArchiveEntry);
					}
					else {
						_unarchiveFile(
							destinationDirectoryFile, tarArchiveEntry,
							tarArchiveInputStream);
					}
				}
				else {
					if (_logger.isWarnEnabled()) {
						_logger.warn(
							"Unable to read " + tarArchiveEntry.getName() +
								" from tarArchiveEntry ");
					}
				}

				tarArchiveEntry = tarArchiveInputStream.getNextTarEntry();
			}
		}
		catch (IOException ioException) {
			if (_logger.isWarnEnabled()) {
				_logger.warn(
					"IO Exception occured in the unarchive operation. " +
						ioException);
			}

			return false;
		}

		return true;
	}

	public static boolean unzip(
		String destinationDirectory, String sourceFile) {

		Path destinationDirectoryPath = Paths.get(destinationDirectory);

		if (!createDirectory(destinationDirectory)) {
			return false;
		}

		Path sourcePath = Paths.get(sourceFile);

		try (ZipInputStream zipInputStream = new ZipInputStream(
				Files.newInputStream(sourcePath))) {

			ZipEntry entry;

			while ((entry = zipInputStream.getNextEntry()) != null) {
				final Path toPath = destinationDirectoryPath.resolve(
					entry.getName());

				if (entry.isDirectory()) {
					Files.createDirectory(toPath);
				}
				else {
					Files.copy(zipInputStream, toPath);
				}

				Files.setPosixFilePermissions(
					toPath, PosixFilePermissions.fromString("rwxrwxrwx"));
			}
		}
		catch (IOException ioException) {
			if (_logger.isWarnEnabled()) {
				_logger.warn("ZipInputStream errors. " + ioException);
			}

			return false;
		}

		return true;
	}

	private static List<Path> _convertStringToPath(String[] pathArray) {
		Stream<String> pathStream = Optional.ofNullable(
			pathArray
		).map(
			Arrays::stream
		).orElseGet(
			Stream::empty
		);

		return pathStream.map(
			Paths::get
		).collect(
			Collectors.toList()
		);
	}

	private static void _unarchiveDirectory(
		File destinationRootDirectory, TarArchiveEntry tarArchiveEntry) {

		File directory = new File(
			destinationRootDirectory, tarArchiveEntry.getName());

		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	private static void _unarchiveFile(
			File destinationRootDir, TarArchiveEntry tarArchiveEntry,
			TarArchiveInputStream tarArchiveInputStream)
		throws IOException {

		File file = new File(destinationRootDir, tarArchiveEntry.getName());

		File parentDirectory = file.getParentFile();

		if (!parentDirectory.exists()) {
			parentDirectory.mkdirs();
		}

		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			IOUtils.copy(tarArchiveInputStream, fileOutputStream);
		}

		Files.setPosixFilePermissions(
			file.toPath(), PosixFilePermissions.fromString("rwxrwxrwx"));
	}

	private static final int _CHARS_BUFFER_SIZE = 8192;

	private static final Logger _logger = LogManager.getLogger(
		FileHelperUtil.class);

}