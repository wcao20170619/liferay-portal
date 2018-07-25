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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = LanguageDetector.class)
public class LanguageDetectorImpl implements LanguageDetector {

	@Override
	public String detect(File file) {
		return detect(file, -1, false);
	}

	@Override
	public String detect(File file, int maxTextLength, boolean verbose) {
		String ret = null;

		if (file == null) {
			return ret;
		}

		FileReader fileReader = null;
		BufferedReader bufferedReader = null;

		try {
			fileReader = new FileReader(file);

			LanguageDetect languageDetect = languageDetectFactory.create();

			if (languageDetect != null) {
				_setUserParameter(languageDetect, maxTextLength, verbose);

				bufferedReader = new BufferedReader(fileReader);

				languageDetect.append(bufferedReader);

				ret = languageDetect.detect();
			}
		}
		catch (FileNotFoundException fnfe) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to find the file " + file.getName(), fnfe);
			}
		}

		if (bufferedReader != null) {
			try {
				bufferedReader.close();
			}
			catch (IOException ioe) {
				if (_log.isWarnEnabled()) {
					_log.warn("BufferedReader close exception", ioe);
				}
			}
		}

		if (fileReader != null) {
			try {
				fileReader.close();
			}
			catch (IOException ioe) {
				if (_log.isWarnEnabled()) {
					_log.warn("FileReader close exception", ioe);
				}
			}
		}

		return ret;
	}

	@Override
	public String detect(String text) {
		return detect(text, -1, false);
	}

	@Override
	public String detect(String text, int maxTextLength, boolean verbose) {
		LanguageDetect languageDetect = languageDetectFactory.create();

		if (languageDetect != null) {
			_setUserParameter(languageDetect, maxTextLength, verbose);

			languageDetect.append(text);

			return languageDetect.detect();
		}

		return null;
	}

	@Override
	public List<LocaleProbability> detectLocaleProbabilities(File file) {
		return detectLocaleProbabilities(file, -1, false);
	}

	@Override
	public List<LocaleProbability> detectLocaleProbabilities(
		File file, int maxTextLength, boolean verbose) {

		List<LocaleProbability> ret = null;

		if (file == null) {
			return ret;
		}

		FileReader fileReader = null;
		BufferedReader bufferedReader = null;

		try {
			fileReader = new FileReader(file);

			LanguageDetect languageDetect = languageDetectFactory.create();

			if (languageDetect != null) {
				_setUserParameter(languageDetect, maxTextLength, verbose);

				bufferedReader = new BufferedReader(fileReader);

				languageDetect.append(bufferedReader);

				ret = languageDetect.getLocaleProbabilities();
			}
		}
		catch (FileNotFoundException fnfe) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to find the file " + file.getName(), fnfe);
			}
		}

		if (bufferedReader != null) {
			try {
				bufferedReader.close();
			}
			catch (IOException ioe) {
				if (_log.isWarnEnabled()) {
					_log.warn("BufferedReader close exception", ioe);
				}
			}
		}

		if (fileReader != null) {
			try {
				fileReader.close();
			}
			catch (IOException ioe) {
				if (_log.isWarnEnabled()) {
					_log.warn("FileReader close exception", ioe);
				}
			}
		}

		return ret;
	}

	@Override
	public List<LocaleProbability> detectLocaleProbabilities(String text) {
		return detectLocaleProbabilities(text, -1, false);
	}

	@Override
	public List<LocaleProbability> detectLocaleProbabilities(
		String text, int maxTextLength, boolean verbose) {

		LanguageDetect languageDetect = languageDetectFactory.create();

		if (languageDetect != null) {
			_setUserParameter(languageDetect, maxTextLength, verbose);
			languageDetect.append(text);

			return languageDetect.getLocaleProbabilities();
		}

		return null;
	}

	@Override
	public List<Locale> detectLocales(File file) {
		return detectLocales(file, -1, false);
	}

	@Override
	public List<Locale> detectLocales(
		File file, int maxTextLength, boolean verbose) {

		List<LocaleProbability> localeProbabilityList =
			detectLocaleProbabilities(file, maxTextLength, verbose);

		if (localeProbabilityList != null) {
			final List<Locale> localeList = new ArrayList<>(
				localeProbabilityList.size());

			localeProbabilityList.forEach(
				locProb -> {
					localeList.add(locProb.getLocale());
				});

			return localeList;
		}

		return null;
	}

	@Override
	public List<Locale> detectLocales(String text) {
		return detectLocales(text, -1, false);
	}

	@Override
	public List<Locale> detectLocales(
		String text, int maxTextLength, boolean verbose) {

		List<LocaleProbability> localeProbabilityList =
			detectLocaleProbabilities(text, maxTextLength, verbose);

		if (localeProbabilityList != null) {
			final List<Locale> localeList = new ArrayList<>(
				localeProbabilityList.size());

			localeProbabilityList.forEach(
				locProb -> {
					localeList.add(locProb.getLocale());
				});

			return localeList;
		}

		return null;
	}

	@Reference
	protected LanguageDetectFactory languageDetectFactory;

	private void _setUserParameter(
		LanguageDetect languageDetect, int maxTextLength, boolean verbose) {

		if (maxTextLength > 0) {
			languageDetect.setMaxTextLength(maxTextLength);
		}

		if (verbose) {
			languageDetect.setVerbose();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LanguageDetectorImpl.class);

}