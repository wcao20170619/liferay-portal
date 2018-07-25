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

import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.SecureDetectorFactory;
import com.cybozu.labs.langdetect.util.LangProfile;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.File;
import java.io.InputStream;

import java.net.URL;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = LanguageDetectFactory.class)
public class LanguageDetectFactoryImpl implements LanguageDetectFactory {

	@Override
	public void clear() {
		DetectorFactory.clear();
	}

	@Override
	public LanguageDetect create() {
		LanguageDetect languageDetect = null;

		try {
			languageDetect = new LanguageDetectImpl(DetectorFactory.create());
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to create Detector", e);
			}
		}

		return languageDetect;
	}

	@Override
	public LanguageDetect create(double alpha) {
		LanguageDetect languageDetect = null;

		try {
			languageDetect = new LanguageDetectImpl(
				DetectorFactory.create(alpha));
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to create Detector with alpha parameter", e);
			}
		}

		return languageDetect;
	}

	@Override
	public List<String> getLangList() {
		return DetectorFactory.getLangList();
	}

	@Override
	public void loadProfile(File profileDirectory) {
		try {
			DetectorFactory.loadProfile(profileDirectory);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to load profile " + profileDirectory.getName(), e);
			}
		}
	}

	@Override
	public void loadProfile(List<String> jsonProfiles) {
		try {
			DetectorFactory.loadProfile(jsonProfiles);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to load json profiles", e);
			}
		}
	}

	@Override
	public void loadProfile(String profileDirectory) {
		try {
			DetectorFactory.loadProfile(profileDirectory);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to load profile " + profileDirectory, e);
			}
		}
	}

	@Override
	public void loadProfileFromClassPath() {
		Map<String, LangProfile> profiles = new HashMap<>();

		try {
			Bundle bundle = FrameworkUtil.getBundle(getClass());

			Enumeration<URL> enm = bundle.findEntries("profiles/", "*", false);

			while ((enm != null) && enm.hasMoreElements()) {
				URL url = enm.nextElement();

				if (url != null) {
					InputStream is = url.openStream();

					try {
						LangProfile profile = JSON.decode(
							is, LangProfile.class);

						profiles.put(url.getPath(), profile);
					}
					finally {
						is.close();
					}
				}
			}

			SecureDetectorFactory.loadProfile(profiles);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to load profile from class path", e);
			}
		}
	}

	@Override
	public void setSeed(long seed) {
		DetectorFactory.setSeed(seed);
	}

	@Activate
	protected void activate() {
		loadProfileFromClassPath();
	}

	@Deactivate
	protected void deActivate() {
		clear();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LanguageDetectFactoryImpl.class);

}