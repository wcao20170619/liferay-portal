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

package com.liferay.portal.search.multilanguage.detect;

import aQute.bnd.annotation.ProviderType;

import java.io.File;

import java.util.List;
import java.util.Locale;

/**
 * @author Wade Cao
 */
@ProviderType
public interface LanguageDetector {

	public String detect(File file);

	public String detect(File file, int maxTextLength, boolean verbose);

	public String detect(String text);

	public String detect(String text, int maxTextLength, boolean verbose);

	public List<LocaleProbability> detectLocaleProbabilities(File file);

	public List<LocaleProbability> detectLocaleProbabilities(
		File file, int maxTextLength, boolean verbose);

	public List<LocaleProbability> detectLocaleProbabilities(String text);

	public List<LocaleProbability> detectLocaleProbabilities(
		String text, int maxTextLength, boolean verbose);

	public List<Locale> detectLocales(File file);

	public List<Locale> detectLocales(
		File file, int maxTextLength, boolean verbose);

	public List<Locale> detectLocales(String text);

	public List<Locale> detectLocales(
		String text, int maxTextLength, boolean verbose);

}