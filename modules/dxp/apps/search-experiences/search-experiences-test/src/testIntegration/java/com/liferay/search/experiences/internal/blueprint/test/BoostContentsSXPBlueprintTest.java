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

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.internal.blueprint.util.SXPBlueprintTestUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class BoostContentsSXPBlueprintTest extends BaseSXPBlueprintsTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();

		setUpSXPBlueprint(getClass());
	}

	@Test
	public void testSearchBoostContentOnMySites() throws Exception {
		_addGroupJournalArticles();

		Group groupB = _groups.get(1);

		user = UserTestUtil.addUser(groupB.getGroupId());

		serviceContext.setUserId(user.getUserId());

		updateSXPBlueprint(
			getConfigurationJSONString(getClass(), testName.getMethodName()));

		assertSearch("[pepsi cola, coca cola]", "cola");
	}

	@Test
	public void testSearchBoostContentsInCategory() throws Exception {
		AssetCategory assetCategory = _addAssetCategoryJournalArticles();

		updateSXPBlueprint(
			StringUtil.replace(
				getConfigurationJSONString(
					getClass(), testName.getMethodName()),
				"${configuration.asset_category_ids}",
				String.valueOf(assetCategory.getCategoryId())));

		assertSearch("[pepsi cola, coca cola]", "cola");
	}

	@Test
	public void testSearchWithoutBoostContentOnMySites() throws Exception {
		_addGroupJournalArticles();

		updateSXPBlueprint(getEmptyConfigurationJSONString());

		assertSearchIgnoreRelevance("[coca cola, pepsi cola]", "cola");
	}

	@Test
	public void testSearchWithoutBoostContentsInCategory() throws Exception {
		_addAssetCategoryJournalArticles();

		updateSXPBlueprint(getEmptyConfigurationJSONString());

		assertSearchIgnoreRelevance("[coca cola, pepsi cola]", "cola");
	}

	private AssetCategory _addAssetCategoryJournalArticles() throws Exception {
		AssetCategory assetCategory = addAssetCategory(
			"Important", group,
			SXPBlueprintTestUtil.addGroupUser(group, "Custmers"));

		addJournalArticle(group.getGroupId(), "Coca Cola", "cola cola");

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		addJournalArticle(group.getGroupId(), "Pepsi Cola", "");

		return assetCategory;
	}

	private void _addGroupJournalArticles() throws Exception {
		Group groupA = addGroup("SiteA");
		Group groupB = addGroup("SiteB");

		addJournalArticle(groupA.getGroupId(), "coca cola", "cola cola");
		addJournalArticle(groupB.getGroupId(), "pepsi cola", "");

		_groups.add(groupA);
		_groups.add(groupB);
	}

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

}