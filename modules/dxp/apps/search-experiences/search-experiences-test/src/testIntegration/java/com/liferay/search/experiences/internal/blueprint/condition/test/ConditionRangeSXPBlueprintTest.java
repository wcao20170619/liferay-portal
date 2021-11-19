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
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.internal.blueprint.test.BaseSXPBlueprintsTestCase;
import com.liferay.search.experiences.internal.blueprint.util.SXPBlueprintTestUtil;

import java.util.Calendar;
import java.util.Date;

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
public class ConditionRangeSXPBlueprintTest extends BaseSXPBlueprintsTestCase {

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

	@Ignore
	@Test
	public void testSearchAPeriodOfTimeWithConditionRange() throws Exception {
		addJournalArticle(group.getGroupId(), "Coca Cola", "cola cola");

		AssetCategory assetCategory = addAssetCategory(
			"Promoted", group,
			SXPBlueprintTestUtil.addGroupUser(group, "Custmers"));

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		addJournalArticle(group.getGroupId(), "Pepsi Cola", "");

		updateSXPBlueprint(
			_getConfigurationJSONString(
				assetCategory, new Date(System.currentTimeMillis()),
				_getNextDay()));

		assertSearch("[pepsi cola, coca cola]", "cola");
	}

	@Ignore
	@Test
	public void testSearchNewUserAccountWithConditionRange() throws Exception {
		updateSXPBlueprint(
			StringUtil.replace(
				getConfigurationJSONString(
					getClass(), testName.getMethodName()),
				"${configuration.asset_category_id}",
				String.valueOf(
					_addAssetCategoryJournalArticles().getCategoryId())));

		assertSearch(
			"[company policies for new recruits, company policies for all " +
				"employees recruits]",
			"policies");
	}

	@Test
	public void testSearchNewUserAccountWithoutRangeCondition()
		throws Exception {

		_addAssetCategoryJournalArticles();

		updateSXPBlueprint(getEmptyConfigurationJSONString());

		assertSearchIgnoreRelevance(
			"[company policies for all employees recruits, company policies " +
				"for new recruits]",
			"policies");
	}

	private AssetCategory _addAssetCategoryJournalArticles() throws Exception {
		AssetCategory assetCategory = addAssetCategory(
			"For New Recruits", group,
			SXPBlueprintTestUtil.addGroupUser(group, "Employee"));

		addJournalArticle(
			group.getGroupId(), "Company policies for All Employees Recruits",
			"policies policies");

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		addJournalArticle(
			group.getGroupId(), "Company Policies for New Recruits", "");

		return assetCategory;
	}

	private String _getConfigurationJSONString(
		AssetCategory assetCategory, Date startDate, Date endDate) {

		String configurationJSON = StringUtil.replace(
			getConfigurationJSONString(getClass(), testName.getMethodName()),
			"${configuration.asset_category_id}",
			String.valueOf(assetCategory.getCategoryId()));

		configurationJSON = StringUtil.replace(
			configurationJSON, "${configuration.start_date}",
			DateUtil.getDate(startDate, "yyyyMMdd", LocaleUtil.US));

		return StringUtil.replace(
			configurationJSON, "${configuration.end_date}",
			DateUtil.getDate(endDate, "yyyyMMdd", LocaleUtil.US));
	}

	private Date _getNextDay() {
		Calendar cal = CalendarFactoryUtil.getCalendar();

		cal.add(Calendar.DAY_OF_YEAR, 1);

		return cal.getTime();
	}

}