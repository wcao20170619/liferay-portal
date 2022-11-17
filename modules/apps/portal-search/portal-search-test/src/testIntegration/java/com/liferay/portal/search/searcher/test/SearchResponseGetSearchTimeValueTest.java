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

package com.liferay.portal.search.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class SearchResponseGetSearchTimeValueTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_companyId = TestPropsValues.getCompanyId();

		_userSearchFixture = new UserSearchFixture();

		_userSearchFixture.setUp();

		_users = _userSearchFixture.getUsers();

		_addGroupAndUser();
		_addUsers();
	}

	@After
	public void tearDown() throws Exception {
		_userSearchFixture.tearDown();
	}

	@Test
	public void testLowLevelSearchResponseGetSearchTimeValue()
		throws Exception {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).groupIds(
				_group.getGroupId()
			).queryString(
				"omega"
			);

		SearchEngine searchEngine = searchEngineHelper.getSearchEngine();

		if (Objects.equals("Elasticsearch", searchEngine.getVendor())) {
			searchRequestBuilder.withSearchContext(
				searchContext -> searchContext.getQueryConfig(
				).setSelectedIndexNames(
					_indexNameBuilder.getIndexName(_companyId)
				));
		}

		assertSearchResponseTook(searchRequestBuilder);
	}

	@Test
	public void testMultiIndexerSearchResponseGetSearchTimeValue()
		throws Exception {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).groupIds(
				_group.getGroupId()
			).queryString(
				"omega"
			);

		assertSearchResponseTook(searchRequestBuilder);
	}

	@Test
	public void testSingleIndexerSearchResponseGetSearchTimeValue()
		throws Exception {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).groupIds(
				_group.getGroupId()
			).queryString(
				"omega"
			).modelIndexerClasses(
				User.class
			);

		assertSearchResponseTook(searchRequestBuilder);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Rule
	public TestName testName = new TestName();

	protected void assertSearchResponseTook(
		SearchRequestBuilder searchRequestBuilder) {

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		SearchEngine searchEngine = searchEngineHelper.getSearchEngine();

		if (Objects.equals("Elasticsearch", searchEngine.getVendor())) {
			Assert.assertNotNull(searchResponse.getSearchTimeValue());
		}
		else {
			Assert.assertNull(searchResponse.getSearchTimeValue());
		}
	}

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	private void _addGroupAndUser() throws Exception {
		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		_group = groupSearchFixture.addGroup(new GroupBlueprint());

		_groups = groupSearchFixture.getGroups();

		_user = TestPropsValues.getUser();

		PermissionThreadLocal.setPermissionChecker(
			_permissionCheckerFactory.create(_user));
	}

	private void _addUsers() throws Exception {
		String[] assetTagNames = {};

		_userSearchFixture.addUser(
			"alpha", "alpha", "omega", LocaleUtil.US, _group, assetTagNames);
		_userSearchFixture.addUser(
			"omega", "alpha", "omega", LocaleUtil.US, _group, assetTagNames);
		_userSearchFixture.addUser(
			"phi", "alpha", "omega", LocaleUtil.US, _group, assetTagNames);
		_userSearchFixture.addUser(
			"sigma", "zeta", "omega", LocaleUtil.US, _group, assetTagNames);
	}

	private long _companyId;

	@Inject
	private ComplexQueryPartBuilderFactory _complexQueryPartBuilderFactory;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@Inject
	private IndexNameBuilder _indexNameBuilder;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private Queries _queries;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

	private UserSearchFixture _userSearchFixture;

}