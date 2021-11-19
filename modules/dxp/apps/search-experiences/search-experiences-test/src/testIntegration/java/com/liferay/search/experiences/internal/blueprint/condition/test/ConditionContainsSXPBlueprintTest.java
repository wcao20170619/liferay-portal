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

package com.liferay.search.experiences.internal.blueprint.condition.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.internal.blueprint.test.BaseSXPBlueprintsTestCase;
import com.liferay.search.experiences.internal.blueprint.util.SXPBlueprintTestUtil;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.test.util.SegmentsTestUtil;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class ConditionContainsSXPBlueprintTest
	extends BaseSXPBlueprintsTestCase {

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
	public void testSearch() throws Exception {
		addJournalArticle(group.getGroupId(), "Coca Cola", "cola cola");
		addJournalArticle(group.getGroupId(), "Pepsi Cola", "");

		updateSXPBlueprint(getEmptyConfigurationJSONString());

		assertSearchIgnoreRelevance("[coca cola, pepsi cola]", "cola");
	}

	@Ignore
	@Test
	public void testSearchWithAssetCategory() throws Exception {
		addJournalArticle(group.getGroupId(), "Coca Cola", "cola cola");

		AssetCategory assetCategory = addAssetCategory(
			"Promoted", group,
			SXPBlueprintTestUtil.addGroupUser(group, "employee"));

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		addJournalArticle(group.getGroupId(), "Pepsi Cola", "");

		updateSXPBlueprint(_getConfigurationJSONString(assetCategory, "cola"));

		assertSearch("[pepsi cola, coca cola]", "cola");
	}

	@Ignore
	@Test
	public void testSearchWithConditionContains() throws Exception {
		addJournalArticle(group.getGroupId(), "Coca Cola", "cola cola");

		JournalArticle journalArticle = addJournalArticle(
			group.getGroupId(), "Pepsi Cola", "");

		updateSXPBlueprint(
			StringUtil.replace(
				getConfigurationJSONString(
					getClass(), testName.getMethodName()),
				"${articleId}", journalArticle.getArticleId()));

		assertSearch("[pepsi cola, coca cola]", "cola");
	}

	@Ignore
	@Test
	public void testSearchWithConditionNotContains() throws Exception {
		addJournalArticle(group.getGroupId(), "Coca Cola", "cola cola");

		JournalArticle journalArticle = addJournalArticle(
			group.getGroupId(), "Pepsi Cola", "");

		updateSXPBlueprint(
			StringUtil.replace(
				getConfigurationJSONString(
					getClass(), testName.getMethodName()),
				"${articleId}", journalArticle.getArticleId()));

		assertSearch("[coca cola, pepsi cola]", "cola");
	}

	@Ignore
	@Test
	public void testSearchWithSegmentsEntry() throws Exception {
		user = UserTestUtil.addUser(group.getGroupId());

		AssetCategory assetCategory = addAssetCategory("Promoted", group, user);

		SegmentsEntry segmentsEntry = _addSegmentsEntry(user);

		addJournalArticle(group.getGroupId(), "Coca Cola", "cola cola");

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		addJournalArticle(group.getGroupId(), "Pepsi Cola", "");

		updateSXPBlueprint(
			_getConfigurationJSONString(assetCategory, segmentsEntry));

		assertSearch("[pepsi cola, coca cola]", "cola");
	}

	private SegmentsEntry _addSegmentsEntry(User user) throws Exception {
		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria, String.format("(firstName eq '%s')", user.getFirstName()),
			Criteria.Conjunction.AND);

		return SegmentsTestUtil.addSegmentsEntry(
			group.getGroupId(), CriteriaSerializer.serialize(criteria),
			User.class.getName());
	}

	private String _getConfigurationJSONString(
		AssetCategory assetCategory, SegmentsEntry segmentsEntry) {

		String configurationJSON = StringUtil.replace(
			getConfigurationJSONString(getClass(), testName.getMethodName()),
			"${configuration.asset_category_id}",
			String.valueOf(assetCategory.getCategoryId()));

		return StringUtil.replace(
			configurationJSON, "${configuration.user_segment_ids}",
			String.valueOf(segmentsEntry.getSegmentsEntryId()));
	}

	private String _getConfigurationJSONString(
		AssetCategory assetCategory, String keywords) {

		String configurationJSON = StringUtil.replace(
			getConfigurationJSONString(getClass(), testName.getMethodName()),
			"${configuration.asset_category_id}",
			String.valueOf(assetCategory.getCategoryId()));

		return StringUtil.replace(
			configurationJSON, "${configuration.keywords}", keywords);
	}

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _userSegmentsCriteriaContributor;

}