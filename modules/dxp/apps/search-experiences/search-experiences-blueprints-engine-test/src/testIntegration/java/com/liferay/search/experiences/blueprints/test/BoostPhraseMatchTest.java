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

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.blueprints.model.Blueprint;

import java.util.Collections;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class BoostPhraseMatchTest extends BaseQueryElementsTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testSearchWithBoostPhraseMatch() throws Exception {
		addJournalArticle("This coca looks like a kind of drink", "coca coca");
		addJournalArticle("This looks like a kind of coca drink", "");

		Blueprint blueprint = addCompanyBlueprint(
			Collections.singletonMap(
				LocaleUtil.US, getClass().getName() + "Blueprint"),
			Collections.singletonMap(LocaleUtil.US, ""),
			getConfigurationString(
				getMultiMatchQueryElementJSONObject(
					10, "AUTO", "or", "most_fields")),
			getSelectedElementString(
				getTextMatchOverMultipleFieldUIConfigValuesJSONObject(
					2, 1, "AUTO", 1, "or", "most_fields")));

		assertSearch(
			blueprint, null,
			"[this coca looks like a kind of drink, this looks like a kind " +
				"of coca drink]",
			"coca drink", null);

		String configurationString = getConfigurationString(
			getMultiMatchQueryElementJSONObject(1, null, "or", "most_fields"),
			getAllKeywordsMatchQueryElementJSONObject());

		String selectedElementString = getSelectedElementString(
			getTextMatchOverMultipleFieldJSONObject(
				1, 1, null, 2, "or", "most_fields"),
			getAllKeywordsMatchMultipleFieldJSONObject(100, 1, 2, "phrase"));

		assertSearch(
			blueprint, configurationString,
			"[this looks like a kind of coca drink, this coca looks like a " +
				"kind of drink]",
			"coca drink", selectedElementString);
	}

	protected JSONObject getAllKeywordsMatchMultipleFieldJSONObject(
			int boost, int contentBoost, int localizedTitleBoost,
			String operator)
		throws Exception {

		JSONObject elementTemplateJSONObject = getElementTemplateJSONObject(
			"/elements/boost-all-keywords-match-test.json");

		return JSONUtil.put(
			"elementTemplateJSON",
			elementTemplateJSONObject.get("elementTemplateJSON")
		).put(
			"uiConfigurationJSON",
			elementTemplateJSONObject.get("uiConfigurationJSON")
		).put(
			"uiConfigurationValues",
			getAllKeywordsMatchMultipleFieldUIConfigValuesJSONObject(
				boost, contentBoost, localizedTitleBoost, operator)
		);
	}

	protected JSONObject
		getAllKeywordsMatchMultipleFieldUIConfigValuesJSONObject(
			int boost, int contentBoost, int localizedTitleBoost, String type) {

		JSONArray fieldsJSONArray = createJSONArray();

		return JSONUtil.put(
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
			"type", type
		);
	}

	protected JSONObject getAllKeywordsMatchQueryElementJSONObject() {
		return JSONUtil.put(
			"category", "boost"
		).put(
			"clauses",
			createJSONArray().put(
				JSONUtil.put(
					"context", "query"
				).put(
					"occur", "should"
				).put(
					"query",
					JSONUtil.put(
						"wrapper",
						JSONUtil.put(
							"query",
							JSONUtil.put(
								"multi_match",
								getMultiMatchJSONObject(
									100, null, null, "and", "phrase"))))
				))
		).put(
			"conditions", JSONUtil.put(null, null)
		).put(
			"description",
			JSONUtil.put(
				"en_US",
				"Boost contents matching all the words in the search phrase")
		).put(
			"enabled", true
		).put(
			"icon", "thumbs-up"
		).put(
			"title", JSONUtil.put("en_US", "Boost All Keywords Match")
		);
	}

}