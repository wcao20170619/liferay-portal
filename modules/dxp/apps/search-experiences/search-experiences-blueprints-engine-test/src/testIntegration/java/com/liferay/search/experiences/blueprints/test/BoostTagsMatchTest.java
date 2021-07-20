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
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
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
public class BoostTagsMatchTest extends BaseBlueprintsTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testKeywoardMatchWithAssetTagName() throws Exception {
		serviceContext.setAssetTagNames(new String[0]);

		addJournalArticle("coca cola", "");

		JournalArticle journalArticle = addJournalArticle("pepsi cola", "");

		Blueprint blueprint = addCompanyBlueprint(
			Collections.singletonMap(
				LocaleUtil.US, getClass().getName() + "Blueprint"),
			Collections.singletonMap(LocaleUtil.US, ""),
			getConfigurationString((JSONObject[])null), "");

		assertSearch(blueprint, null, "[coca cola, pepsi cola]", "cola", null);

		AssetTag assetTag = _addTag("cola");

		serviceContext.setAssetTagNames(new String[] {assetTag.getName()});

		updateJournalArticle(
			journalArticle, journalArticle.getTitle(),
			journalArticle.getContent());

		blueprint = addCompanyBlueprint(
			Collections.singletonMap(
				LocaleUtil.US, getClass().getName() + "Blueprint"),
			Collections.singletonMap(LocaleUtil.US, ""),
			getConfigurationString(_getQueryElementJSONObject(100)),
			_getSelectedElementString(100));

		assertSearch(blueprint, null, "[pepsi cola, coca cola]", "cola", null);
	}

	private AssetTag _addTag(String assetTagName) throws Exception {
		return AssetTagLocalServiceUtil.addTag(
			user.getUserId(), group.getGroupId(), assetTagName, serviceContext);
	}

	private JSONObject _getQueryElementJSONObject(int boost) {
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
								"term",
								JSONUtil.put(
									"assetTagNames.raw",
									JSONUtil.put(
										"boost", boost
									).put(
										"value", "${keywords}"
									)))))
				))
		).put(
			"conditions", JSONUtil.put(null, null)
		).put(
			"description",
			JSONUtil.put(
				"en_US",
				"Boost contents having an exact keyword match to a tag")
		).put(
			"enabled", true
		).put(
			"icon", "thumbs-up"
		).put(
			"title", JSONUtil.put("en_US", "Boost Tags Match")
		);
	}

	private String _getSelectedElementString(int boost) throws Exception {
		JSONObject elementTemplateJSONObject = getElementTemplateJSONObject(
			"/elements/boost-tags-match-test.json");

		return JSONUtil.put(
			"query_configuration",
			createJSONArray().put(
				JSONUtil.put(
					"elementOutput",
					JSONUtil.put(
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
											"term",
											JSONUtil.put(
												"assetTagNames.raw",
												JSONUtil.put(
													"boost", boost
												).put(
													"value", "${keywords}"
												)))))
							))
					).put(
						"conditions", createJSONArray()
					).put(
						"description",
						JSONUtil.put(
							"en_US",
							"Boost contents having an exact keyword match to " +
								"a tag")
					).put(
						"enabled", true
					).put(
						"icon", "thumbs-up"
					).put(
						"title", JSONUtil.put("en_US", "Boost Tags Match")
					)
				).put(
					"elementTemplateJSON",
					elementTemplateJSONObject.get("elementTemplateJSON")
				).put(
					"uiConfigurationJSON",
					elementTemplateJSONObject.get("uiConfigurationJSON")
				).put(
					"uiConfigurationValues", JSONUtil.put("boost", boost)
				))
		).toString();
	}

}