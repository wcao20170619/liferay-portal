/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.search.indexer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseIndexerPostProcessor;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.internal.util.UserSearchFixture;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class DocumentIndexerPostProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_userSearchFixture.setUp();

		_group = _userSearchFixture.addGroup();

		_indexer = IndexerRegistryUtil.getIndexer(User.class);
	}

	@After
	public void tearDown() throws Exception {
		_indexer.unregisterIndexerPostProcessor(_indexerPostProcessor);
	}

	@Test
	public void testIndexerPostProcessorNotRegister() throws Exception {
		populateTestFieldValues();
		populateUserIndexer();

		for (String keywords : _KEYWORDS) {
			SearchContext searchContext = buildSearchContext(keywords);

			Hits results = _indexer.search(
				searchContext, getSelectedFieldNames());

			checkSearchHitsNoMatch(results);
		}
	}

	@Test
	public void testIndexerPostProcessorRegister() throws Exception {
		populateTestFieldValues();

		_indexerPostProcessor = getIndexerPostProcessor();

		_indexer.registerIndexerPostProcessor(_indexerPostProcessor);

		populateUserIndexer();

		for (String keywords : _KEYWORDS) {
			SearchContext searchContext = buildSearchContext(keywords);

			Hits results = _indexer.search(
				searchContext, getSelectedFieldNames());

			checkSearchHitsMatch(results);
		}
	}

	@Test
	public void testIndexerPostProcessorUnRegister() throws Exception {
		populateTestFieldValues();
		populateUserIndexer();
		_indexer.unregisterIndexerPostProcessor(_indexerPostProcessor);

		for (String keywords : _KEYWORDS) {
			SearchContext searchContext = buildSearchContext(keywords);

			Hits results = _indexer.search(
				searchContext, getSelectedFieldNames());

			checkSearchHitsNoMatch(results);
		}
	}

	protected SearchContext buildSearchContext(String keywords)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext();

		searchContext.setAttribute(Field.STATUS, WorkflowConstants.STATUS_ANY);
		searchContext.setGroupIds(new long[0]);
		searchContext.setKeywords(keywords);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(getSelectedFieldNames());

		return searchContext;
	}

	protected void checkSearchHitsMatch(Hits hits) throws Exception {
		for (Document document : hits.getDocs()) {
			String screenName = document.get("screenName");

			Assert.assertEquals(
				document.get(_TEST_FIELD), _strings.get(screenName));
		}
	}

	protected void checkSearchHitsNoMatch(Hits hits) throws Exception {
		for (Document document : hits.getDocs()) {
			String screenName = document.get("screenName");

			Assert.assertNotEquals(
				document.get(_TEST_FIELD), _strings.get(screenName));
		}
	}

	protected IndexerPostProcessor getIndexerPostProcessor() {
		IndexerPostProcessor indexerPostProcessor =
			new BaseIndexerPostProcessor() {

				@Override
				public void postProcessDocument(Document document, Object obj)
					throws Exception {

					String screenName = document.get("screenName");

					document.addText(_TEST_FIELD, _strings.get(screenName));
				}

			};

		return indexerPostProcessor;
	}

	protected String[] getSelectedFieldNames() {
		return new String[] {"screenName", _TEST_FIELD};
	}

	protected void populateTestFieldValues() {
		_strings.put("fifthuser", "testValue5");
		_strings.put("firstuser", "testValue1");
		_strings.put("fourthuser", "testValue4");
		_strings.put("seconduser", "testValue2");
		_strings.put("sixthuser", "testValue6");
		_strings.put("thirduser", "testValue3");
	}

	protected void populateUserIndexer() throws Exception {
		for (String screenName : _strings.keySet()) {
			User user = _userSearchFixture.addUser(
				screenName, _group, RandomTestUtil.randomString());

			_users.add(user);

			_indexer.reindex(user);
		}
	}

	private static final String[] _KEYWORDS =
		{"first", "second", "third", "fourth", "fifth", "sixth"};

	private static final String _TEST_FIELD = "testField";

	@DeleteAfterTestRun
	private Group _group;

	private Indexer<User> _indexer;
	private IndexerPostProcessor _indexerPostProcessor;
	private final Map<String, String> _strings = new HashMap<>();

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

	private final UserSearchFixture _userSearchFixture =
		new UserSearchFixture();

}