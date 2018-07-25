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
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import java.io.File;

import java.net.URL;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.FileUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Wade Cao
 */
@PrepareForTest({DetectorFactory.class, FrameworkUtil.class})
@RunWith(PowerMockRunner.class)
public class LanguageDetectFactoryTest extends PowerMockito {

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		_setupDetectorFactory();
		_setupBundle();
	}

	@Test
	public void testActivation() {
		LanguageDetectFactoryImpl languageDetectFactory =
			new LanguageDetectFactoryImpl();

		languageDetectFactory.activate();

		languageDetectFactory.deActivate();
	}

	@Test
	public void testCreate() {
		LanguageDetectFactoryImpl languageDetectFactory =
			new LanguageDetectFactoryImpl();

		LanguageDetect languageDetect = languageDetectFactory.create();

		Assert.assertNotNull(languageDetect);
	}

	@Test
	public void testCreateAlpha() {
		LanguageDetectFactoryImpl languageDetectFactory =
			new LanguageDetectFactoryImpl();

		LanguageDetect languageDetect = languageDetectFactory.create(0.5);

		Assert.assertNotNull(languageDetect);
	}

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private void _setupBundle() throws Exception {
		mockStatic(FrameworkUtil.class);

		when(
			FrameworkUtil.getBundle(Matchers.anyObject())
		).thenReturn(
			_bundle
		);

		final File fileEn = tempFolder.newFile("en");

		FileUtils.writeStringToFile(fileEn, _JSON_EN, Charset.forName("UTF-8"));

		final File fileHu = tempFolder.newFile("hu");

		FileUtils.writeStringToFile(fileHu, _JSON_HU, Charset.forName("UTF-8"));

		List<URL> list = new ArrayList<>(2);

		list.add(fileEn.toURI().toURL());
		list.add(fileHu.toURI().toURL());

		Enumeration<URL> enm = Collections.enumeration(list);

		when(
			_bundle.findEntries(
				Matchers.anyString(), Matchers.anyString(),
				Matchers.anyBoolean())
		).thenReturn(
			enm
		);
	}

	private void _setupDetectorFactory() throws LangDetectException {
		mockStatic(DetectorFactory.class);

		when(
			DetectorFactory.create()
		).thenReturn(
			_detector
		);

		when(
			DetectorFactory.create(Matchers.anyDouble())
		).thenReturn(
			_detector
		);
	}

	private static final String _JSON_EN =
		"{\"freq\":{\"A\":3,\"B\":6,\"C\":3,\"AB\":2,\"BC\":1,\"ABC\":2," +
			"\"BBC\":1,\"CBA\":1},\"n_words\":[12,3,4],\"name\":\"en\"}";

	private static final String _JSON_HU =
		"{\"freq\":{\"A\":6,\"B\":3,\"C\":3,\"AA\":3,\"AB\":2,\"ABC\":1," +
			"\"ABA\":1,\"CAA\":1},\"n_words\":[12,5,3],\"name\":\"hu\"}";

	@Mock
	private Bundle _bundle;

	@Mock
	private Detector _detector;

}