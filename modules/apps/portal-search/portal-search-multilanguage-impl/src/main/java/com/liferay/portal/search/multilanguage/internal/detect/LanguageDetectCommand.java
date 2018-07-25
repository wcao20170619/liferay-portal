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

import com.liferay.portal.search.multilanguage.detect.LanguageDetector;
import com.liferay.portal.search.multilanguage.detect.LocaleProbability;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(
	immediate = true,
	property = {
		"osgi.command.function=detect", "osgi.command.function=detectFile",
		"osgi.command.function=detectFileLocProb",
		"osgi.command.function=detectLocProb", "osgi.command.scope=multilang"
	},
	service = LanguageDetectCommand.class
)
public class LanguageDetectCommand {

	public void detect(String text) {
		detect(text, false);
	}

	public void detect(String text, boolean verbose) {
		LanguageDetectDemo languageDetectDemo = new LanguageDetectDemo(
			_languageDetector);

		String lang = languageDetectDemo.detect(text, verbose);

		System.out.println("The language is " + lang);
	}

	public void detectFile(String filePath) {
		detectFile(filePath, false);
	}

	public void detectFile(String filePath, boolean verbose) {
		LanguageDetectDemo languageDetectDemo = new LanguageDetectDemo(
			_languageDetector);

		String lang = languageDetectDemo.detectFile(filePath, verbose);

		System.out.println("The language is " + lang);
	}

	public void detectFileLocProb(String filePath) {
		detectFileLocProb(filePath, false);
	}

	public void detectFileLocProb(String filePath, boolean verbose) {
		LanguageDetectDemo languageDetectDemo = new LanguageDetectDemo(
			_languageDetector);

		List<LocaleProbability> lpList =
			languageDetectDemo.detectFileLocaleProbabilities(filePath, verbose);

		_printList(lpList);
	}

	public void detectLocProb(String text) {
		detectLocProb(text, false);
	}

	public void detectLocProb(String text, boolean verbose) {
		LanguageDetectDemo languageDetectDemo = new LanguageDetectDemo(
			_languageDetector);

		List<LocaleProbability> lpList =
			languageDetectDemo.detectLocaleProbabilities(text, verbose);

		_printList(lpList);
	}

	private void _printList(List<LocaleProbability> lpList) {
		int index = 0;

		for (LocaleProbability lp : lpList) {
			Locale locale = lp.getLocale();
			System.out.println("====================");
			System.out.println("#" + index++);
			System.out.println("language is " + locale.getDisplayLanguage());
			System.out.println("The probability is " + lp.getProbability());
		}
	}

	@Reference
	private LanguageDetector _languageDetector;

}