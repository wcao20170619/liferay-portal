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

package com.liferay.source.formatter;

import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.source.formatter.checks.util.JSPSourceUtil;
import com.liferay.source.formatter.checkstyle.util.AlloyMVCCheckstyleLogger;
import com.liferay.source.formatter.checkstyle.util.AlloyMVCCheckstyleUtil;
import com.liferay.source.formatter.checkstyle.util.CheckstyleUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import com.puppycrawl.tools.checkstyle.api.Configuration;

import java.io.File;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Hugo Huijser
 */
public class JSPSourceProcessor extends BaseSourceProcessor {

	@Override
	protected List<String> doGetFileNames() throws Exception {
		String[] excludes = {"**/null.jsp", "**/tools/**"};

		List<String> fileNames = getFileNames(excludes, getIncludes());

		if (fileNames.isEmpty() ||
			(!sourceFormatterArgs.isFormatCurrentBranch() &&
			 !sourceFormatterArgs.isFormatLatestAuthor() &&
			 !sourceFormatterArgs.isFormatLocalChanges())) {

			return fileNames;
		}

		List<String> allJSPFileNames = getFileNames(
			excludes, getIncludes(), true);

		return JSPSourceUtil.addIncludedAndReferencedFileNames(
			fileNames, new HashSet<String>(),
			JSPSourceUtil.getContentsMap(allJSPFileNames));
	}

	@Override
	protected String[] doGetIncludes() {
		return _INCLUDES;
	}

	@Override
	protected File format(
			File file, String fileName, String absolutePath, String content)
		throws Exception {

		// When executing 'format-source-current-branch',
		// 'format-source-latest-author', or 'format-source-local-changes', we
		// add included and referenced file names in order to detect unused
		// imports or variable names. As a result, file names that are excluded
		// via source-formatter.properties#source.formatter.excludes are also
		// added to the list. Here we make sure we do not format files that
		// should be excluded.

		if (sourceFormatterArgs.isFormatCurrentBranch() ||
			sourceFormatterArgs.isFormatLatestAuthor() ||
			sourceFormatterArgs.isFormatLocalChanges()) {

			List<String> fileNames = SourceFormatterUtil.filterFileNames(
				Arrays.asList(fileName), new String[0], new String[] {"*.*"},
				getSourceFormatterExcludes(), false);

			if (fileNames.isEmpty()) {
				return file;
			}
		}

		file = super.format(file, fileName, absolutePath, content);

		_processCheckstyle(absolutePath, content);

		return file;
	}

	@Override
	protected void postFormat() throws Exception {
		_processCheckstyle();

		for (SourceFormatterMessage sourceFormatterMessage :
				_sourceFormatterMessages) {

			String fileName = sourceFormatterMessage.getFileName();

			processMessage(fileName, sourceFormatterMessage);

			printError(fileName, sourceFormatterMessage.toString());
		}
	}

	private void _processCheckstyle() throws Exception {
		if (_ungeneratedFiles.isEmpty()) {
			return;
		}

		if (_configuration == null) {
			_checkstyleLogger = new AlloyMVCCheckstyleLogger(
				new UnsyncByteArrayOutputStream(), true,
				sourceFormatterArgs.getBaseDirName());
			_configuration = CheckstyleUtil.getConfiguration(
				"checkstyle-alloy-mvc.xml", getPropertiesMap(),
				sourceFormatterArgs.getMaxLineLength(),
				sourceFormatterArgs.isShowDebugInformation());
		}

		_sourceFormatterMessages.addAll(
			processCheckstyle(
				_configuration, _checkstyleLogger,
				_ungeneratedFiles.toArray(new File[_ungeneratedFiles.size()])));

		for (File ungeneratedFile : _ungeneratedFiles) {
			Files.deleteIfExists(ungeneratedFile.toPath());
		}

		_ungeneratedFiles.clear();
	}

	private synchronized void _processCheckstyle(
			String absolutePath, String content)
		throws Exception {

		File file = AlloyMVCCheckstyleUtil.getJavaFile(absolutePath, content);

		if (file != null) {
			_ungeneratedFiles.add(file);

			if (_ungeneratedFiles.size() == CheckstyleUtil.BATCH_SIZE) {
				_processCheckstyle();
			}
		}
	}

	private static final String[] _INCLUDES =
		{"**/*.jsp", "**/*.jspf", "**/*.tag", "**/*.tpl", "**/*.vm"};

	private AlloyMVCCheckstyleLogger _checkstyleLogger;
	private Configuration _configuration;
	private final Set<SourceFormatterMessage> _sourceFormatterMessages =
		new TreeSet<>();
	private final List<File> _ungeneratedFiles = new ArrayList<>();

}