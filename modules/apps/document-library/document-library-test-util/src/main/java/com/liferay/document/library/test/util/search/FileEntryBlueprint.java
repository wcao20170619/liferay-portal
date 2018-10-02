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

package com.liferay.document.library.test.util.search;

import com.liferay.portal.kernel.test.util.TestPropsValues;

/**
 * @author Wade Cao
 */
public class FileEntryBlueprint {

	public String[] getAssetTagNames() {
		return assetTagNames;
	}

	public long getGroupId() {
		return groupId;
	}

	public String getKeyword() {
		return keyword;
	}

	public long getUserId() throws Exception {
		if (userId > 0) {
			return userId;
		}

		return TestPropsValues.getUserId();
	}

	protected String[] assetTagNames;
	protected long groupId;
	protected String keyword;
	protected long userId;

}