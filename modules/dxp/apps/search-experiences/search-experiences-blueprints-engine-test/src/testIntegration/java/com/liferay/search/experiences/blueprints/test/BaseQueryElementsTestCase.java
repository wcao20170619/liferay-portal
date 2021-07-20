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

package com.liferay.search.experiences.blueprints.test;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

/**
 * @author Wade Cao
 */
public abstract class BaseQueryElementsTestCase extends BaseBlueprintsTestCase {

	@Override
	protected JSONObject getFrameworkConfiguration() {
		JSONArray fieldsJSONArray = createJSONArray();

		return JSONUtil.put(
			"apply_indexer_clauses", true
		).put(
			"searchable_asset_types",
			fieldsJSONArray.put(
				"com.liferay.wiki.model.WikiPage"
			).put(
				"com.liferay.journal.model.JournalArticle"
			)
		);
	}

	protected JSONObject getHideHiddenContentsElementJSONObject()
		throws Exception {

		JSONObject hideHiddenContentsJSONObject =
			getHideHiddenContentsJSONObject();

		return hideHiddenContentsJSONObject.getJSONObject(
			"elementTemplateJSON");
	}

	protected JSONObject getHideHiddenContentsJSONObject() throws Exception {
		JSONObject elementTemplateJSONObject = getElementTemplateJSONObject(
			"/elements/hide-hidden-contents-test.json");

		return JSONUtil.put(
			"elementTemplateJSON",
			elementTemplateJSONObject.get("elementTemplateJSON")
		).put(
			"uiConfigurationJSON",
			elementTemplateJSONObject.get("uiConfigurationJSON")
		).put(
			"uiConfigurationValues", JSONUtil.put(null, null)
		);
	}

	protected JSONObject getMatchQueryElementJSONObject(
		int boost, String occur, String queryValue) {

		return JSONUtil.put(
			"category", "custom"
		).put(
			"clauses",
			createJSONArray().put(
				JSONUtil.put(
					"context", "query"
				).put(
					"occur", occur
				).put(
					"query",
					JSONUtil.put(
						"wrapper",
						JSONUtil.put(
							"query",
							JSONUtil.put(
								"match",
								JSONUtil.put(
									"content_en_US",
									JSONUtil.put(
										"boost", boost
									).put(
										"query", queryValue
									)))))
				))
		).put(
			"conditions", JSONUtil.put(null, null)
		).put(
			"description",
			JSONUtil.put(
				"en_US",
				"Paste any Elasticsearch query body in the element as is")
		).put(
			"enabled", true
		).put(
			"icon", "custom-field"
		).put(
			"title", JSONUtil.put("en_US", "Paste any Elasticsearch query")
		);
	}

	protected JSONObject getMultiMatchJSONObject(
		int boost, String fuzziness, Integer minimumShouldMatch,
		String operator, String type) {

		JSONArray fieldsJSONArray = createJSONArray();

		JSONObject jsonObject = JSONUtil.put(
			"boost", boost
		).put(
			"fields",
			fieldsJSONArray.put(
				"localized_title${context.language_id}^2"
			).put(
				"content${context.language_id}^1"
			)
		).put(
			"operator", operator
		).put(
			"query", "${keywords}"
		).put(
			"type", type
		);

		if (minimumShouldMatch != null) {
			jsonObject.put("minimum_should_match", minimumShouldMatch);
		}

		if (fuzziness != null) {
			jsonObject.put("fuzziness", fuzziness);
		}

		return jsonObject;
	}

	protected JSONObject getMultiMatchQueryElementJSONObject(
		int boost, String fuzziness, Integer minimumShouldMatch,
		String operator, String type) {

		return JSONUtil.put(
			"category", "match"
		).put(
			"clauses",
			createJSONArray().put(
				JSONUtil.put(
					"context", "query"
				).put(
					"occur", "must"
				).put(
					"query",
					JSONUtil.put(
						"wrapper",
						JSONUtil.put(
							"query",
							JSONUtil.put(
								"multi_match",
								getMultiMatchJSONObject(
									boost, fuzziness, minimumShouldMatch,
									operator, type))))
				))
		).put(
			"conditions", JSONUtil.put(null, null)
		).put(
			"description",
			JSONUtil.put(
				"en_US", "Search for a text match over multiple text fields")
		).put(
			"enabled", true
		).put(
			"icon", "picture"
		).put(
			"title", JSONUtil.put("en_US", "Text Match Over Multiple Fields")
		);
	}

	protected JSONObject getMultiMatchQueryElementJSONObject(
		int boost, String fuzziness, String operator) {

		return getMultiMatchQueryElementJSONObject(
			boost, fuzziness, operator, "best_fields");
	}

	protected JSONObject getMultiMatchQueryElementJSONObject(
		int boost, String fuzziness, String operator, String type) {

		return getMultiMatchQueryElementJSONObject(
			boost, fuzziness, null, operator, type);
	}

	protected JSONObject getPasteESQueryJSONObject(
			int boost, String occur, String... queryValues)
		throws Exception {

		JSONObject elementTemplateJSONObject = getElementTemplateJSONObject(
			"/elements/paste-an-elasticsearch-query-test.json");

		return JSONUtil.put(
			"elementTemplateJSON",
			elementTemplateJSONObject.get("elementTemplateJSON")
		).put(
			"uiConfigurationJSON",
			elementTemplateJSONObject.get("uiConfigurationJSON")
		).put(
			"uiConfigurationValues",
			getPasteESQueryUIConfigValuesJSONObject(boost, occur, queryValues)
		);
	}

	protected JSONObject getPasteESQueryUIConfigValuesJSONObject(
		int boost, String occur, String... queryValues) {

		return JSONUtil.put(
			"occur", occur
		).put(
			"query",
			JSONUtil.put(
				"match",
				JSONUtil.put(
					"content_en_US",
					JSONUtil.put(
						"boost", boost
					).put(
						"query", queryValues[0]
					)))
		);
	}

	protected String getSelectedElementString(JSONObject... jsonObjects)
		throws Exception {

		JSONArray jsonArray = createJSONArray();

		for (JSONObject jsonObject : jsonObjects) {
			jsonArray.put(jsonObject);
		}

		return JSONUtil.put(
			"query_configuration", jsonArray
		).toString();
	}

	protected JSONObject getTextMatchOverMultipleFieldJSONObject(
			int boost, int contentBoost, String fuzziness,
			int localizedTitleBoost, String operator, String type)
		throws Exception {

		JSONObject elementTemplateJSONObject = getElementTemplateJSONObject(
			"/elements/text-match-over-multiple-fields-test.json");

		return JSONUtil.put(
			"elementTemplateJSON",
			elementTemplateJSONObject.get("elementTemplateJSON")
		).put(
			"uiConfigurationJSON",
			elementTemplateJSONObject.get("uiConfigurationJSON")
		).put(
			"uiConfigurationValues",
			getTextMatchOverMultipleFieldUIConfigValuesJSONObject(
				boost, contentBoost, fuzziness, localizedTitleBoost, operator,
				type)
		);
	}

	protected JSONObject getTextMatchOverMultipleFieldUIConfigValuesJSONObject(
		int boost, int contentBoost, String fuzziness, int localizedTitleBoost,
		String operator) {

		return getTextMatchOverMultipleFieldUIConfigValuesJSONObject(
			boost, contentBoost, fuzziness, localizedTitleBoost, operator,
			"best_fields");
	}

	protected JSONObject getTextMatchOverMultipleFieldUIConfigValuesJSONObject(
		int boost, int contentBoost, String fuzziness, int localizedTitleBoost,
		String operator, String type) {

		JSONArray fieldsJSONArray = createJSONArray();

		JSONObject jsonObject = JSONUtil.put(
			"boost", boost
		).put(
			"fields",
			fieldsJSONArray.put(
				JSONUtil.put(
					"boost", localizedTitleBoost
				).put(
					"field", "localized_title"
				).put(
					"locale", "${context.language_id}"
				)
			).put(
				JSONUtil.put(
					"boost", contentBoost
				).put(
					"field", "content"
				).put(
					"locale", "${context.language_id}"
				)
			)
		).put(
			"minimum_should_match", 1
		).put(
			"operator", operator
		).put(
			"slop", "null"
		).put(
			"type", type
		);

		if (fuzziness != null) {
			jsonObject.put("fuzziness", fuzziness);
		}

		return jsonObject;
	}

}