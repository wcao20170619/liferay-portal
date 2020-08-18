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

package com.liferay.portal.search.tuning.blueprints.constants.json.keys;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author Petteri Karttunen
 */
public enum AggregationConfigurationKeys {

	AGGREGATIONS("aggregations"), BODY("body"), COLLECT_MODE("collect_mode"),
	ENABLED("enabled"), EXCLUDE("exclude"), EXECUTION_HINT("execution_hint"),
	FACET("facet"), FIELD("field"), ID("id"), INCLUDE("include"), LANG("lang"),
	MIN_DOC_COUNT("min_doc_count"), MISSING("multi_value"), NAME("name"),
	OPTIONS("options"), ORDER("order"), PARAMS("params"), SCRIPT("script"),
	SHARD_MIN_DOC_COUNT("shard_min_doc_count"), SHARD_SIZE("shard_size"),
	SHOW_TERM_DOC_COUNT_ERROR("show_term_doc_count_error"), SIZE("size"),
	SOURCE("source"), TYPE("type");

	public static final AggregationConfigurationKeys findByJsonKey(
		String jsonKey) {

		Stream<AggregationConfigurationKeys>
			aggregationConfigurationKeysStream = Arrays.stream(
				AggregationConfigurationKeys.values());

		return aggregationConfigurationKeysStream.filter(
			value -> value._jsonKey.equals(jsonKey)
		).findFirst(
		).orElse(
			null
		);
	}

	public String getJsonKey() {
		return _jsonKey;
	}

	private AggregationConfigurationKeys(String jsonKey) {
		_jsonKey = jsonKey;
	}

	private final String _jsonKey;

}