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
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.blueprints.model.Blueprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class FilterByExactTermMatchTest extends BaseBlueprintsTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testSearchWithFilterByExactTermMatch() throws Exception {
		Group groupA = GroupTestUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, "SiteA", serviceContext);
		Group groupB = GroupTestUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, "SiteB", serviceContext);
		Group groupC = GroupTestUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, "SiteC", serviceContext);

		_groups.add(groupA);
		_groups.add(groupB);
		_groups.add(groupC);

		addJournalArticle(groupA.getGroupId(), "cola coca");
		addJournalArticle(groupB.getGroupId(), "cola pepsi");
		addJournalArticle(groupC.getGroupId(), "cola sprite");

		Blueprint blueprint = addCompanyBlueprint(
			Collections.singletonMap(
				LocaleUtil.US, getClass().getName() + "Blueprint"),
			Collections.singletonMap(LocaleUtil.US, ""),
			getConfigurationString((JSONObject[])null), "");

		assertSearchIgnoreRelevance(
			blueprint, null, "[cola coca, cola pepsi, cola sprite]", "cola",
			null);

		assertSearchIgnoreRelevance(
			blueprint,
			getConfigurationString(
				_getQueryElementJSONObject(
					_getGourpIdJSONArray(
						groupA.getGroupId(), groupB.getGroupId()))),
			"[cola coca, cola pepsi]", "cola",
			_getSelectedElementString(
				_getGorupIdLabelValueJSONArray(
					groupA.getGroupId(), groupB.getGroupId())));
	}

	private JSONArray _getGorupIdLabelValueJSONArray(long... groupIds) {
		JSONArray jsonArray = createJSONArray();

		if (groupIds == null) {
			return jsonArray;
		}

		for (long groupId : groupIds) {
			jsonArray.put(
				JSONUtil.put(
					"label", groupId
				).put(
					"value", groupId
				));
		}

		return jsonArray;
	}

	private JSONArray _getGourpIdJSONArray(long... groupIds) {
		JSONArray jsonArray = createJSONArray();

		if (groupIds == null) {
			return jsonArray;
		}

		for (long groupId : groupIds) {
			jsonArray.put(groupId);
		}

		return jsonArray;
	}

	private JSONObject _getQueryElementJSONObject(JSONArray groupIdJSONArray) {
		return JSONUtil.put(
			"category", "filter"
		).put(
			"clauses",
			createJSONArray().put(
				JSONUtil.put(
					"context", "query"
				).put(
					"occur", "filter"
				).put(
					"query",
					JSONUtil.put(
						"wrapper",
						JSONUtil.put(
							"query",
							JSONUtil.put(
								"terms",
								JSONUtil.put("groupId", groupIdJSONArray))))
				))
		).put(
			"conditions", JSONUtil.put(null, null)
		).put(
			"description",
			JSONUtil.put(
				"en_US",
				"Filter results by one or multiple terms. At least one has " +
					"to match")
		).put(
			"enabled", true
		).put(
			"icon", "filter"
		).put(
			"title", JSONUtil.put("en_US", "Filter by Exact Terms Match")
		);
	}

	private String _getSelectedElementString(
			JSONArray gorupIdLabelValueJSONArray)
		throws Exception {

		JSONObject elementTemplateJSONObject = getElementTemplateJSONObject(
			"/elements/filter-by-exact-terms-match-test.json");

		return JSONUtil.put(
			"query_configuration",
			createJSONArray().put(
				JSONUtil.put(
					"elementTemplateJSON",
					elementTemplateJSONObject.get("elementTemplateJSON")
				).put(
					"uiConfigurationJSON",
					elementTemplateJSONObject.get("uiConfigurationJSON")
				).put(
					"uiConfigurationValues",
					JSONUtil.put(
						"field",
						JSONUtil.put(
							"field", "groupId"
						).put(
							"languageIdPosition", -1
						)
					).put(
						"values", gorupIdLabelValueJSONArray
					)
				))
		).toString();
	}

	@DeleteAfterTestRun
	private List<Group> _groups = new ArrayList<>();

}