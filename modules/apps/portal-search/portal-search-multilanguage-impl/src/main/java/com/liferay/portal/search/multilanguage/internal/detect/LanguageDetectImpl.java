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

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.multilanguage.detect.LocaleProbability;

import java.io.IOException;
import java.io.Reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Wade Cao
 */
public class LanguageDetectImpl implements LanguageDetect {

	public LanguageDetectImpl(Detector detector) {
		_detector = detector;
	}

	@Override
	public void append(Reader reader) {
		try {
			_detector.append(reader);
		}
		catch (IOException ioe) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to append reader", ioe);
			}
		}
	}

	@Override
	public void append(String text) {
		_detector.append(text);
	}

	@Override
	public String detect() {
		String ret = UNKNOWN_LANG;

		try {
			ret = _detector.detect();
		}
		catch (LangDetectException lde) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to detect text", lde);
			}
		}

		return ret;
	}

	@Override
	public List<LocaleProbability> getLocaleProbabilities() {
		List<LocaleProbability> retList = new ArrayList<>();

		try {
			List<Language> languages = _detector.getProbabilities();

			languages.forEach(
				language -> {
					LocaleProbability localeProbability =
						new LocaleProbabilityImpl(
							LocaleUtil.fromLanguageId(language.lang, false),
							language.prob);

					retList.add(localeProbability);
				});
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get probabilities", e);
			}
		}

		return retList;
	}

	@Override
	public void setAlpha(double alpha) {
		_detector.setAlpha(alpha);
	}

	@Override
	public void setMaxTextLength(int maxTextLength) {
		_detector.setMaxTextLength(maxTextLength);
	}

	@Override
	public void setPriorMap(HashMap<String, Double> priorMap) {
		try {
			_detector.setPriorMap(priorMap);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to set priority map", e);
			}
		}
	}

	@Override
	public void setVerbose() {
		_detector.setVerbose();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LanguageDetectImpl.class);

	private final Detector _detector;

}