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
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class DefaultBehaviorLuceneSyntaxSXPBlueprintTest
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
		addJournalArticle(group.getGroupId(), "Cafe Rio", "Los Angeles");
		addJournalArticle(group.getGroupId(), "Cloud Cafe", "Orange County");

		updateSXPBlueprint(getEmptyConfigurationJSONString());

		assertSearchIgnoreRelevance("[cafe rio, cloud cafe]", "cafe");
	}

	@Test
	public void testSearchWithDefaultBlueprint() throws Exception {
		addJournalArticle(group.getGroupId(), "Cafe Rio", "Los Angeles");
		addJournalArticle(group.getGroupId(), "Cloud Cafe", "Orange County");

		updateSXPBlueprint(
			getConfigurationJSONString(getClass(), testName.getMethodName()));

		assertSearch("[cafe rio, cloud cafe]", "cafe");
	}

	@Test
	public void testSearchWithLuceneSyntax() throws Exception {
		addJournalArticle(group.getGroupId(), "Coca Cola", "");
		addJournalArticle(group.getGroupId(), "Pepsi Cola", "");

		updateSXPBlueprint(
			getConfigurationJSONString(getClass(), testName.getMethodName()));

		assertSearch("[coca cola]", "cola +coca");
		assertSearch("[pepsi cola]", "cola -coca");
	}

	@Test
	public void testSearchWithoutLuceneSyntax() throws Exception {
		addJournalArticle(group.getGroupId(), "Coca Cola", "");
		addJournalArticle(group.getGroupId(), "Pepsi Cola", "");

		updateSXPBlueprint(getEmptyConfigurationJSONString());

		assertSearchIgnoreRelevance("[coca cola, pepsi cola]", "cola +coca");
	}

}