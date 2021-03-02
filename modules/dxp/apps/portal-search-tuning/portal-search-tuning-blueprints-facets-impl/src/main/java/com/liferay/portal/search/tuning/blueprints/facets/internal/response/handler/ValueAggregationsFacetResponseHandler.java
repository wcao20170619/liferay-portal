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

package com.liferay.portal.search.tuning.blueprints.facets.internal.response.handler;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.tuning.blueprints.attributes.BlueprintsAttributes;
import com.liferay.portal.search.tuning.blueprints.facets.constants.FacetConfigurationKeys;
import com.liferay.portal.search.tuning.blueprints.facets.constants.FacetJSONResponseKeys;
import com.liferay.portal.search.tuning.blueprints.facets.spi.response.FacetResponseHandler;
import com.liferay.portal.search.tuning.blueprints.message.Message;
import com.liferay.portal.search.tuning.blueprints.message.Messages;
import com.liferay.portal.search.tuning.blueprints.message.Severity;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;

/**
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, property = "name=value_aggregations",
	service = FacetResponseHandler.class
)
public class ValueAggregationsFacetResponseHandler
	extends BaseFacetResponseHandler implements FacetResponseHandler {

	@Override
	public Optional<JSONObject> getResultOptional(
		AggregationResult aggregationResult,
		BlueprintsAttributes blueprintsAttributes,
		ResourceBundle resourceBundle, Messages messages,
		JSONObject configurationJsonObject) {

		TermsAggregationResult termsAggregationResult =
			(TermsAggregationResult)aggregationResult;

		JSONArray jsonArray = null;

		try {
			JSONObject handlerParametersJsonObject =
				configurationJsonObject.getJSONObject(
					FacetConfigurationKeys.HANDLER_PARAMETERS.getJsonKey());

			JSONArray aggregationsJsonArray =
				handlerParametersJsonObject.getJSONArray("aggregations");

			Map<String, Integer> termsMap = new HashMap<>();

			for (Bucket bucket : termsAggregationResult.getBuckets()) {
				if (Validator.isBlank(bucket.getKey())) {
					continue;
				}

				boolean mappingFound = false;

				for (int i = 0; i < aggregationsJsonArray.length(); i++) {
					JSONObject aggregationJsonObject =
						aggregationsJsonArray.getJSONObject(i);

					String key = aggregationJsonObject.getString("key");
					JSONArray valuesJsonArray =
						aggregationJsonObject.getJSONArray("values");

					for (int j = 0; j < valuesJsonArray.length(); j++) {
						String val = valuesJsonArray.getString(j);

						if (StringUtil.equals(val, bucket.getKey())) {
							if (termsMap.get(key) != null) {
								int newValue =
									termsMap.get(key) +
										(int)bucket.getDocCount();

								termsMap.put(key, newValue);
							}
							else {
								termsMap.put(key, (int)bucket.getDocCount());
							}

							mappingFound = true;
						}
					}
				}

				if (!mappingFound) {
					termsMap.put(bucket.getKey(), (int)bucket.getDocCount());
				}
			}

			Map<String, Integer> termMapOrdered = _sort(termsMap);

			long frequencyThreshold = configurationJsonObject.getLong(
				FacetConfigurationKeys.FREQUENCY_THRESHOLD.getJsonKey(), 1);

			jsonArray = _getTermsJSONArray(
				termMapOrdered, frequencyThreshold, resourceBundle);
		}
		catch (Exception exception) {
			messages.addMessage(
				new Message(
					Severity.ERROR, "core", "core.error.unknown-exception",
					exception.getMessage(), exception, configurationJsonObject,
					null, null));

			_log.error(exception.getMessage(), exception);
		}

		return createResultObject(
			jsonArray, configurationJsonObject, resourceBundle);
	}

	private JSONArray _getTermsJSONArray(
		Map<String, Integer> termsMap, long frequencyThreshold,
		ResourceBundle resourceBundle) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (Map.Entry<String, Integer> entry : termsMap.entrySet()) {
			long frequency = entry.getValue();

			if (frequency < frequencyThreshold) {
				continue;
			}

			String value = entry.getKey();

			jsonArray.put(
				JSONUtil.put(
					FacetJSONResponseKeys.FREQUENCY, frequency
				).put(
					FacetJSONResponseKeys.NAME, value
				).put(
					FacetJSONResponseKeys.TEXT,
					getText(value, frequency, resourceBundle)
				).put(
					FacetJSONResponseKeys.VALUE, value
				));
		}

		return jsonArray;
	}

	private Map<String, Integer> _sort(Map<String, Integer> termsMap)
		throws Exception {

		Set<Map.Entry<String, Integer>> entrySet = termsMap.entrySet();

		Stream<Map.Entry<String, Integer>> stream = entrySet.stream();

		return stream.sorted(
			Map.Entry.comparingByValue(Comparator.reverseOrder())
		).collect(
			Collectors.toMap(
				Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new)
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ValueAggregationsFacetResponseHandler.class);

}