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

package com.liferay.search.experiences.blueprints.facets.constants;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author Petteri Karttunen
 */
public enum FacetConfigurationKeys {

	AGGREGATION_NAME("aggregation_name"), AGGREGATION_TYPE("aggregation_type"),
	ENABLED("enabled"), EXCLUDE_VALUES("exclude_values"), FIELD("field"),
	FILTER_MODE("filter_mode"), INCLUDE_VALUES("include_values"),
	LABEL("label"), MIN_DOC_COUNT("min_doc_count"), MULTI_VALUE("multi_value"),
	MULTI_VALUE_OPERATOR("multi_value_operator"),
	PARAMETER_NAME("parameter_name"), PARAMETERS("parameters"),
	SHARD_SIZE("shard_size"), SIZE("size");

	public static FacetConfigurationKeys findByJsonKey(String jsonKey) {
		Stream<FacetConfigurationKeys> stream = Arrays.stream(
			FacetConfigurationKeys.values());

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

	private FacetConfigurationKeys(String jsonKey) {
		_jsonKey = jsonKey;
	}

	private final String _jsonKey;

}