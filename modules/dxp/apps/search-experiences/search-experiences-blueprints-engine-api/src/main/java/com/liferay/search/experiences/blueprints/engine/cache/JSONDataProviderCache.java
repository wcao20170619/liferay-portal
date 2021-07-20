/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.search.experiences.blueprints.engine.cache;

import com.liferay.portal.kernel.json.JSONObject;

/**
 * @author Petteri Karttunen
 */
public interface JSONDataProviderCache {

	public void clearCache();

	public JSONObject getJSONObject(String cacheKey);

	public void put(String cacheKey, JSONObject jsonObject);

	public void put(String cacheKey, JSONObject jsonObject, int timeToLive);

	public void remove(String cacheKey);

}