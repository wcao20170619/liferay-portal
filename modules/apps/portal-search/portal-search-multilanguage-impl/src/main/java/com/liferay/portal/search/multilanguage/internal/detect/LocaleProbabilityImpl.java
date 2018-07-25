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

import java.util.Locale;

/**
 * @author Wade Cao
 */
public class LocaleProbabilityImpl implements LocaleProbability {

	public LocaleProbabilityImpl(Locale locale, double probability) {
		_locale = locale;
		_probability = probability;
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	public double getProbability() {
		return _probability;
	}

	@Override
	public String toString() {
		if (_locale == null) {
			return "";
		}

		return _locale.toString() + ":" + _probability;
	}

	private final Locale _locale;
	private final double _probability;

}