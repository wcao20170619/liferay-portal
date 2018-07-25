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

package com.liferay.portal.search.multilanguage.internal.detect;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.multilanguage.detect.LanguageDetector;
import com.liferay.portal.search.multilanguage.detect.LocaleProbability;

import java.io.File;

import java.util.Date;
import java.util.List;

/**
 * @author Wade Cao
 */
public class LanguageDetectDemo {

	public LanguageDetectDemo(LanguageDetector languageDetector) {
		_languageDetector = languageDetector;
	}

	public String detect(String text, boolean verbose) {
		Date date = new Date();
		String ret = _languageDetector.detect(text, -1, verbose);
		long timeElapsed = _getTimeDifference(date, new Date());

		System.out.println("Time eplapsed: " + timeElapsed);

		return ret;
	}

	public String detectFile(String filePath) {
		return detectFile(filePath, false);
	}

	public String detectFile(String filePath, boolean verbose) {
		try {
			File file = new File(filePath);
			Date date = new Date();
			String ret = _languageDetector.detect(file, -1, verbose);

			long timeElapsed = _getTimeDifference(date, new Date());

			System.out.println("Time eplapsed: " + timeElapsed);

			return ret;
		}
		catch (Exception e) {
			_log.error("File creation error", e);
		}

		return null;
	}

	public List<LocaleProbability> detectFileLocaleProbabilities(
		String filePath) {

		return detectFileLocaleProbabilities(filePath, false);
	}

	public List<LocaleProbability> detectFileLocaleProbabilities(
		String filePath, boolean verbose) {

		try {
			File file = new File(filePath);
			Date date = new Date();

			List<LocaleProbability> ret =
				_languageDetector.detectLocaleProbabilities(file, -1, verbose);

			long timeElapsed = _getTimeDifference(date, new Date());

			System.out.println("Time eplapsed: " + timeElapsed);

			return ret;
		}
		catch (Exception e) {
			_log.error("File creation error", e);
		}

		return null;
	}

	public List<LocaleProbability> detectLocaleProbabilities(
		String text, boolean verbose) {

		Date date = new Date();

		List<LocaleProbability> ret =
			_languageDetector.detectLocaleProbabilities(text, -1, verbose);

		long timeElapsed = _getTimeDifference(date, new Date());

		System.out.println("Time eplapsed: " + timeElapsed);

		return ret;
	}

	private static long _getTimeDifference(Date date1, Date date2) {
		long difference = date2.getTime() - date1.getTime();

		return difference;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LanguageDetectDemo.class);

	private final LanguageDetector _languageDetector;

}