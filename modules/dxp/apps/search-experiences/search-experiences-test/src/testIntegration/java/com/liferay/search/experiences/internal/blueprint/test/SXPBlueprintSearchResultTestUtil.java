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

package com.liferay.search.experiences.internal.blueprint.test;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.search.experiences.model.SXPElement;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.ElementDefinition;
import com.liferay.search.experiences.rest.dto.v1_0.ElementInstance;
import com.liferay.search.experiences.rest.dto.v1_0.Field;
import com.liferay.search.experiences.rest.dto.v1_0.FieldSet;
import com.liferay.search.experiences.rest.dto.v1_0.UiConfiguration;
import com.liferay.search.experiences.rest.dto.v1_0.util.ElementDefinitionUtil;
import com.liferay.search.experiences.rest.dto.v1_0.util.SXPElementUtil;
import com.liferay.search.experiences.rest.dto.v1_0.util.UnpackUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Wade Cao
 */
public class SXPBlueprintSearchResultTestUtil {

	public static String queryConfigurationJSON = StringBundler.concat(
		"{\"generalConfiguration\":{\"searchableAssetTypes\":[",
		"\"com.liferay.journal.model.JournalArticle\"]},",
		"\"queryConfiguration\":{\"applyIndexerClauses\":true}}");

	public static String getElementInstancesJSON(
		Object[] configurationValuesArray, String[] sxpElementNames,
		List<SXPElement> sxpElements) {

		ElementInstance[] elementInstances =
			new ElementInstance[sxpElementNames.length];

		for (int i = 0; i < sxpElementNames.length; i++) {
			String sxpElementName = sxpElementNames[i];

			Stream<SXPElement> stream = sxpElements.stream();

			SXPElement sxpElement = stream.filter(
				x -> x.getTitle(
					LocaleUtil.US
				).equalsIgnoreCase(
					sxpElementName
				)
			).findFirst(
			).get();

			ElementDefinition elementDefinition =
				ElementDefinitionUtil.toElementDefinition(
					sxpElement.getElementDefinitionJSON());

			String configurationJSON = String.valueOf(
				elementDefinition.getConfiguration());

			Map<String, Object> configurationValues = null;

			if ((configurationValuesArray != null) &&
				(configurationValuesArray[i] != null)) {

				configurationValues =
					(Map<String, Object>)configurationValuesArray[i];

				for (Map.Entry<String, Object> entry :
						configurationValues.entrySet()) {

					Object configurationValue = UnpackUtil.unpack(
						entry.getValue());

					Map<String, String> values = HashMapBuilder.put(
						entry.getKey(), configurationValue.toString()
					).build();

					if (configurationValue instanceof String) {
						configurationJSON = StringUtil.replace(
							configurationJSON, "${configuration.", "}", values);
						configurationJSON = StringUtil.replace(
							configurationJSON, "${", "}", values);
					}
					else {
						configurationJSON = StringUtil.replace(
							configurationJSON, "\"${configuration.", "}\"",
							values);
					}
				}
			}

			ElementInstance elementInstance = ElementInstance.unsafeToDTO("{}");

			elementInstance.setConfigurationEntry(
				Configuration.toDTO(configurationJSON));

			elementInstance.setSxpElement(
				SXPElementUtil.toSXPElement(
					"{\"elementDefinition\":" + elementDefinition.toString() +
						"}"));

			Map<String, Object> uiConfigurationValues = new HashMap<>();

			UiConfiguration uiConfiguration =
				elementDefinition.getUiConfiguration();

			if ((uiConfiguration != null) &&
				(uiConfiguration.getFieldSets() != null) &&
				(configurationValues != null)) {

				FieldSet[] fieldSets = uiConfiguration.getFieldSets();

				if (ArrayUtil.isEmpty(fieldSets)) {
					return null;
				}

				for (FieldSet fieldSet : fieldSets) {
					Field[] fields = fieldSet.getFields();

					if (fields == null) {
						continue;
					}

					for (Field field : fields) {
						String fieldName = field.getName();

						Object value = configurationValues.get(fieldName);

						if (value == null) {
							continue;
						}

						uiConfigurationValues.put(
							fieldName, UnpackUtil.unpack(value));
					}
				}
			}

			elementInstance.setUiConfigurationValues(uiConfigurationValues);

			elementInstances[i] = elementInstance;
		}

		return Arrays.toString(elementInstances);
	}

	public static JSONObject getMatchQueryJSONObject(int boost, String query) {
		return JSONUtil.put(
			"match",
			JSONUtil.put(
				"content_en_US",
				JSONUtil.put(
					"boost", boost
				).put(
					"query", query
				)));
	}

	public static JSONObject getMultiMatchQueryJSONObject(
		int boost, String[] fields, String fuzziness, String operator,
		String query, String type) {

		JSONObject jsonObject = JSONUtil.put(
			"boost", boost
		).put(
			"fields", fields
		).put(
			"query", query
		).put(
			"type", type
		);

		if (fuzziness != null) {
			jsonObject.put("fuzziness", fuzziness);
		}

		if (operator != null) {
			jsonObject.put("operator", operator);
		}

		return JSONUtil.put("multi_match", jsonObject);
	}

}