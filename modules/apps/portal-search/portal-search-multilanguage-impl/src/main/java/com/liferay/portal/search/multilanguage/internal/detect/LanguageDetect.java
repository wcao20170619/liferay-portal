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

import com.liferay.portal.search.multilanguage.detect.LocaleProbability;

import java.io.Reader;

import java.util.HashMap;
import java.util.List;

/**
 * @author Wade Cao
 */
public interface LanguageDetect {

	public static final String UNKNOWN_LANG = "unknown";

	public void append(Reader reader);

	public void append(String text);

	public String detect();

	public List<LocaleProbability> getLocaleProbabilities();

	public void setAlpha(double alpha);

	public void setMaxTextLength(int maxTextLength);

	public void setPriorMap(HashMap<String, Double> priorMap);

	public void setVerbose();

}