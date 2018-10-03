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

package com.liferay.asset.tags.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.search.FileEntryBlueprint;
import com.liferay.document.library.test.util.search.FileEntrySearchFixture;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.facet.tag.AssetTagNamesFacetFactory;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
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
public class AssetTagMultiLanguageSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		WorkflowThreadLocal.setEnabled(false);
		setUpUserSearchFixture();
		setUpFileEntrySearchFixture();
		_group = userSearchFixture.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		userSearchFixture.tearDown();
		fileEntrySearchFixture.tearDown();
	}

	@Test
	public void testChineseTagName() throws Exception {
		String title = "my chinese title";
		String tagName = "你好";

		User user = UserTestUtil.addUser(_group.getGroupId(), LocaleUtil.CHINA);

		_users.add(user);

		addFileEntry(_group, user, title, new String[] {tagName});

		SearchContext searchContext = getSearchContext(tagName);

		Hits hits = search(searchContext);

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchContext.getKeywords(), hits.getDocs(), "assetTagNames",
			Arrays.asList(String.valueOf(tagName)));
	}

	@Test
	public void testEnglishTagName() throws Exception {
		String title = "my english title";
		String tagName = "searchtag";

		User user = UserTestUtil.addUser(_group.getGroupId());

		_users.add(user);

		addFileEntry(_group, user, title, new String[] {tagName});

		SearchContext searchContext = getSearchContext(tagName);

		Hits hits = search(searchContext);

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchContext.getKeywords(), hits.getDocs(), "assetTagNames",
			Arrays.asList(String.valueOf(tagName)));
	}

	@Test
	public void testJapaneseTagName() throws Exception {
		String title = "my japanese title";
		String tagName = "出前東京";

		User user = UserTestUtil.addUser(_group.getGroupId(), LocaleUtil.JAPAN);

		_users.add(user);

		addFileEntry(_group, user, title, new String[] {tagName});

		SearchContext searchContext = getSearchContext(tagName);

		Hits hits = search(searchContext);

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchContext.getKeywords(), hits.getDocs(), "assetTagNames",
			Arrays.asList(String.valueOf(tagName)));
	}

	protected FileEntry addFileEntry(
			Group group, User user, String key, String[] tag)
		throws Exception {

		return fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					groupId = group.getGroupId();
					assetTagNames = tag;
					keyword = key;
					userId = user.getUserId();
				}
			});
	}

	protected FacetedSearcher createFacetedSearcher() {
		return _facetedSearcherManager.createFacetedSearcher();
	}

	protected SearchContext getSearchContext(String keywords) throws Exception {
		return userSearchFixture.getSearchContext(keywords);
	}

	protected Hits search(SearchContext searchContext) throws Exception {
		FacetedSearcher facetedSearcher = createFacetedSearcher();

		return facetedSearcher.search(searchContext);
	}

	protected void setUpFileEntrySearchFixture() {
		fileEntrySearchFixture = new FileEntrySearchFixture(dlAppLocalService);

		fileEntrySearchFixture.setUp();
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture.setUp();

		_assetTags = userSearchFixture.getAssetTags();
		_groups = userSearchFixture.getGroups();
	}

	@Inject
	protected DLAppLocalService dlAppLocalService;

	protected FileEntrySearchFixture fileEntrySearchFixture;
	protected final UserSearchFixture userSearchFixture =
		new UserSearchFixture();

	@Inject
	private static AssetTagNamesFacetFactory _assetTagNamesFacetFactory;

	@Inject
	private static FacetedSearcherManager _facetedSearcherManager;

	@DeleteAfterTestRun
	private List<AssetTag> _assetTags;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}