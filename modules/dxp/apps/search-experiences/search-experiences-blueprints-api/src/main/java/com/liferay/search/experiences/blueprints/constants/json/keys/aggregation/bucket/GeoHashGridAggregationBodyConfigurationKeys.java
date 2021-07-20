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

package com.liferay.search.experiences.blueprints.constants.json.keys.aggregation.bucket;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author Petteri Karttunen
 */
public enum GeoHashGridAggregationBodyConfigurationKeys {

	FIELD("field"), MISSING("missing"), PRECISION("precision"),
	SCRIPT("script"), SHARD_SIZE("shard_size"), SIZE("size");

	public static GeoHashGridAggregationBodyConfigurationKeys findByJsonKey(
		String jsonKey) {

		Stream<GeoHashGridAggregationBodyConfigurationKeys> stream =
			Arrays.stream(GeoHashGridAggregationBodyConfigurationKeys.values());

		return stream.filter(
			value -> value._jsonKey.equals(jsonKey)
		).findFirst(
		).orElse(
			null
		);
	}

	public String getJsonKey() {
		return _jsonKey;
	}

	private GeoHashGridAggregationBodyConfigurationKeys(String jsonKey) {
		_jsonKey = jsonKey;
	}

	private final String _jsonKey;

}