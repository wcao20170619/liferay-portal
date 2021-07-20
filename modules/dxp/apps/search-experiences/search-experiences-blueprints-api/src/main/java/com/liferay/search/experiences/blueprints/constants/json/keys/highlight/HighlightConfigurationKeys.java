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

package com.liferay.search.experiences.blueprints.constants.json.keys.highlight;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author Petteri Karttunen
 */
public enum HighlightConfigurationKeys {

	FIELDS("fields"), FRAGMENT_OFFSET("fragment_offset"),
	FRAGMENT_SIZE("fragment_size"), NUMBER_OF_FRAGMENTS("number_of_fragments"),
	POST_TAGS("post_tags"), PRE_TAGS("pre_tags"),
	REQUIRE_FIELD_MATCH("require_field_match"), TYPE("type");

	public static HighlightConfigurationKeys findByJsonKey(String jsonKey) {
		Stream<HighlightConfigurationKeys> stream = Arrays.stream(
			HighlightConfigurationKeys.values());

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

	private HighlightConfigurationKeys(String jsonKey) {
		_jsonKey = jsonKey;
	}

	private final String _jsonKey;

}