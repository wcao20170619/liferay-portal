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
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.internal.blueprint.util.SXPBlueprintTestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class SXPBlueprintBoostProximityTest extends BaseSXPBlueprintsTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_addExpandoColumn();

		serviceContext.setExpandoBridgeAttributes(
			Collections.singletonMap(
				_EXPANDO_COLUMN_GEOLOCATION,
				_getGeolocationValue(64.01, -117.42)));

		addJournalArticle(group.getGroupId(), "Branch SF", "");

		serviceContext.setExpandoBridgeAttributes(
			Collections.singletonMap(
				_EXPANDO_COLUMN_GEOLOCATION,
				_getGeolocationValue(24.03, -107.44)));

		addJournalArticle(group.getGroupId(), "Branch LA", "");

		setUpSXPBlueprint(getClass());
	}

	@Ignore
	@Test
	public void testExpandoKeywordCustomFieldsLocationGeolocation()
		throws Exception {

		updateSXPBlueprint(_getConfigurationJSONString("64.01", "-117.42"));

		assertSearch("[branch sf, branch la]", "branch");

		updateSXPBlueprint(_getConfigurationJSONString("24.03", "-107.44"));

		assertSearch("[branch la, branch sf]", "branch");
	}

	@Test
	public void testSearch() throws Exception {
		updateSXPBlueprint(getEmptyConfigurationJSONString());

		assertSearchIgnoreRelevance("[branch la, branch sf]", "branch");
	}

	private void _addExpandoColumn() throws Exception {
		ExpandoTable expandoTable = SXPBlueprintTestUtil.addExpandoTable(
			group.getCompanyId(), _expandoTables);

		_expandoColumns.addAll(
			SXPBlueprintTestUtil.addExpandoColumn(
				expandoTable, group.getCompanyId(),
				ExpandoColumnConstants.GEOLOCATION,
				_EXPANDO_COLUMN_GEOLOCATION));
	}

	private String _getConfigurationJSONString(String lat, String lon) {
		String configurationJSONString = StringUtil.replace(
			getConfigurationJSONString(getClass()), "${configuration.lat}",
			lat);

		return StringUtil.replace(
			configurationJSONString, "${configuration.lon}", lon);
	}

	private String _getGeolocationValue(double latitude, double longitude) {
		return StringBundler.concat(
			"{\"latitude\":", latitude, ",\"longitude\":", longitude, "}");
	}

	private static final String _EXPANDO_COLUMN_GEOLOCATION = "location";

	@DeleteAfterTestRun
	private final List<ExpandoColumn> _expandoColumns = new ArrayList<>();

	@DeleteAfterTestRun
	private final List<ExpandoTable> _expandoTables = new ArrayList<>();

}