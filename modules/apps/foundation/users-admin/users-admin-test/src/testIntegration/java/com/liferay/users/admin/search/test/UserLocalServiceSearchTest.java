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

package com.liferay.users.admin.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

import java.util.ArrayList;
import java.util.List;

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
@Sync
public class UserLocalServiceSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Registry registry = RegistryUtil.getRegistry();

		_userLocalService = registry.getService(UserLocalService.class);

		IndexerRegistry indexerRegistry = registry.getService(
			IndexerRegistry.class);

		_indexer = indexerRegistry.getIndexer(User.class);
		//force to use indexer, not userFinder
		_indexer.setIndexerEnabled(true);
	}

	@Test
	public void testSearchCountWithKeyword() throws Exception {
		_users = new ArrayList<>(_NUMOFUSER);

		//before adding users
		int count_exist = _userLocalService.searchCount(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, null);

		for (int i = 0; i < _NUMOFUSER; i++) {
			_users.add(UserTestUtil.addUser());
		}

		//for screening email address
		int count = _userLocalService.searchCount(
			TestPropsValues.getCompanyId(), _users.get(0).getEmailAddress(),
			WorkflowConstants.STATUS_APPROVED, null);

		Assert.assertEquals(1, count);

		//all user for companyId
		count = _userLocalService.searchCount(
			TestPropsValues.getCompanyId(), null,
			WorkflowConstants.STATUS_APPROVED, null);

		Assert.assertEquals(count_exist + _NUMOFUSER, count);
	}

	@Test
	public void testSearchCountWithName() throws Exception {
		_users = new ArrayList<>(_NUMOFUSER);

		for (int i = 0; i < _NUMOFUSER; i++) {
			_users.add(UserTestUtil.addUser());
		}

		// set firstname/lastname to be null since there is a bug for the search

		int cnt = _userLocalService.searchCount(
			_users.get(0).getCompanyId(), null, null, null,
			_users.get(0).getScreenName(), _users.get(0).getEmailAddress(),
			_users.get(0).getStatus(), null, false);

		Assert.assertEquals(1, cnt);
	}

	private static final int _NUMOFUSER = 6;

	private Indexer<User> _indexer;
	private UserLocalService _userLocalService;

	@DeleteAfterTestRun
	private List<User> _users;

}