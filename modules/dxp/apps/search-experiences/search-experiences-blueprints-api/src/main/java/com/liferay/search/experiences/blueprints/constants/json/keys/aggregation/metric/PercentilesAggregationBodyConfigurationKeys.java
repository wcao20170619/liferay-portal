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

package com.liferay.search.experiences.blueprints.constants.json.keys.aggregation.metric;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author Petteri Karttunen
 */
public enum PercentilesAggregationBodyConfigurationKeys {

	FIELD("field"), HDR("hdr"), KEYED("keyed"), MISSING("missing"),
	PERCENTS("percents"), SCRIPT("script"), TDIGEST("tdigest");

	public static PercentilesAggregationBodyConfigurationKeys findByJsonKey(
		String jsonKey) {

		Stream<PercentilesAggregationBodyConfigurationKeys> stream =
			Arrays.stream(PercentilesAggregationBodyConfigurationKeys.values());

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

	private PercentilesAggregationBodyConfigurationKeys(String jsonKey) {
		_jsonKey = jsonKey;
	}

	private final String _jsonKey;

}