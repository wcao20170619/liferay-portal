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

package com.liferay.portal.search.internal.filter.range;

import com.liferay.petra.string.StringPool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Adam Brandizzi
 */
public class RangeTermQueryValueParser {

	public RangeTermQueryValue parse(String value) {
		Matcher matcher = _pattern.matcher(value);

		if (!matcher.matches()) {
			return null;
		}

		RangeTermQueryValue.Builder rangeTermQueryValueBuilder =
			new RangeTermQueryValue.Builder();

		rangeTermQueryValueBuilder.includesLower(isIncludesLower(matcher));
		rangeTermQueryValueBuilder.includesUpper(isIncludesUpper(matcher));
		rangeTermQueryValueBuilder.lowerBound(matcher.group("lowerBound"));
		rangeTermQueryValueBuilder.upperBound(matcher.group("upperBound"));

		return rangeTermQueryValueBuilder.build();
	}

	protected boolean isIncludesLower(Matcher matcher) {
		String lowerBracket = matcher.group("lowerBracket");

		if (lowerBracket.equals(StringPool.OPEN_BRACKET)) {
			return true;
		}

		return false;
	}

	protected boolean isIncludesUpper(Matcher matcher) {
		String upperBracket = matcher.group("upperBracket");

		if (upperBracket.equals(StringPool.CLOSE_BRACKET)) {
			return true;
		}

		return false;
	}

	private static final Pattern _pattern = Pattern.compile(
		"\\s*(?<lowerBracket>[\\]\\[])(?<lowerBound>\\S+)\\s+" +
			"(?<upperBound>\\S+)(?<upperBracket>[\\]\\[])\\s*");

}