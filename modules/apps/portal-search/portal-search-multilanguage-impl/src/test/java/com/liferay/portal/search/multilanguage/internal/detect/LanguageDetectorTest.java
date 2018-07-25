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
import com.cybozu.labs.langdetect.Language;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.multilanguage.detect.LocaleProbability;

import java.io.File;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Wade Cao
 */
@PrepareForTest(LocaleUtil.class)
@RunWith(PowerMockRunner.class)
public class LanguageDetectorTest extends PowerMockito {

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		_setupLanguageDetectFactory();
		_setupDetector();
		_setupLocaleUtil();
	}

	@Test
	public void testDetectFile() throws Exception {
		LanguageDetectorImpl languageDetector = new LanguageDetectorImpl();

		languageDetector.languageDetectFactory = _languageDetectFactory;

		final File tempFile = tempFolder.newFile("txtFile.txt");

		FileUtils.writeStringToFile(
			tempFile, "hello word", Charset.forName("UTF-8"));

		String ret = languageDetector.detect(tempFile);

		Assert.assertEquals("en", ret);
	}

	@Test
	public void testDetectLocaleProbabilitiesFile() throws Exception {
		LanguageDetectorImpl languageDetector = new LanguageDetectorImpl();

		languageDetector.languageDetectFactory = _languageDetectFactory;

		final File tempFile = tempFolder.newFile("tempFile.txt");

		FileUtils.writeStringToFile(
			tempFile, "This is English angol", Charset.forName("UTF-8"));

		List<LocaleProbability> ret =
			languageDetector.detectLocaleProbabilities(tempFile);

		String[] expectedValues = {"en_US:0.5", "hu_HU:0.6"};

		assertValues(ret, Arrays.asList(expectedValues));
	}

	@Test
	public void testDetectLocaleProbabilitiesText() {
		LanguageDetectorImpl languageDetector = new LanguageDetectorImpl();

		languageDetector.languageDetectFactory = _languageDetectFactory;

		List<LocaleProbability> ret =
			languageDetector.detectLocaleProbabilities("This is English angol");

		String[] expectedValues = {"en_US:0.5", "hu_HU:0.6"};

		assertValues(ret, Arrays.asList(expectedValues));
	}

	@Test
	public void testDetectText() {
		LanguageDetectorImpl languageDetector = new LanguageDetectorImpl();

		languageDetector.languageDetectFactory = _languageDetectFactory;

		String ret = languageDetector.detect("This is English");

		Assert.assertEquals("en", ret);
	}

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	protected void assertValues(
		List<LocaleProbability> localeProbabilities,
		List<String> expectedVaues) {

		List<String> probs = new ArrayList<>(localeProbabilities.size());

		for (LocaleProbability localeProbability : localeProbabilities) {
			probs.add(localeProbability.toString());
		}

		Assert.assertEquals(expectedVaues, probs);
	}

	private void _setupDetector() throws Exception {
		Mockito.when(
			_detector.detect()
		).thenReturn(
			"en"
		);

		ArrayList<Language> probabilities = new ArrayList<>(2);

		probabilities.add(new Language("en", 0.5));
		probabilities.add(new Language("hu", 0.6));

		Mockito.when(
			_detector.getProbabilities()
		).thenReturn(
			probabilities
		);
	}

	private void _setupLanguageDetectFactory() {
		Mockito.when(
			_languageDetectFactory.create()
		).thenReturn(
			new LanguageDetectImpl(_detector)
		);
	}

	private void _setupLocaleUtil() throws Exception {
		mockStatic(LocaleUtil.class);

		when(
			LocaleUtil.fromLanguageId("en")
		).thenReturn(
			LocaleUtil.US
		);

		when(
			LocaleUtil.fromLanguageId("hu")
		).thenReturn(
			LocaleUtil.HUNGARY
		);
	}

	@Mock
	private Detector _detector;

	@Mock
	private LanguageDetectFactory _languageDetectFactory;

}