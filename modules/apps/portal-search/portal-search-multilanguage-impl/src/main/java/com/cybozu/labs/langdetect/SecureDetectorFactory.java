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

package com.cybozu.labs.langdetect;

import com.cybozu.labs.langdetect.util.LangProfile;

import java.util.Map;

/**
 * @author Wade Cao
 */
public class SecureDetectorFactory {

	public static void loadProfile(Map<String, LangProfile> profiles)
		throws LangDetectException {

		int langsize = profiles.size();
		int index = 0;

		for (Map.Entry<String, LangProfile> entry : profiles.entrySet()) {
			LangProfile profile = entry.getValue();

			DetectorFactory.addProfile(profile, index++, langsize);
		}
	}

}