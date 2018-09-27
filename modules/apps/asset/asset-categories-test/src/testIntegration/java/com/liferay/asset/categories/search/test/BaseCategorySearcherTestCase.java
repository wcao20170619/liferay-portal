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

package com.liferay.asset.categories.search.test;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalArticleSearchFixture;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;

import org.junit.After;
import org.junit.Before;

/**
 * @author Wade Cao
 */
public abstract class BaseCategorySearcherTestCase {

	@Before
	public void setUp() throws Exception {
		WorkflowThreadLocal.setEnabled(false);

		setUpJournalArticleSearchFixture();
		setUpUserSearchFixture();
	}

	@After
	public void tearDown() throws Exception {
		journalArticleSearchFixture.tearDown();
		userSearchFixture.tearDown();
	}

	protected SearchContext getSearchContext(String keywords) throws Exception {
		return userSearchFixture.getSearchContext(keywords);
	}

	protected Hits search(SearchContext searchContext) throws Exception {
		FacetedSearcher facetedSearcher =
			_facetedSearcherManager.createFacetedSearcher();

		return facetedSearcher.search(searchContext);
	}

	protected void setUpJournalArticleSearchFixture() throws Exception {
		journalArticleSearchFixture.setUp();

		_journalArticles = journalArticleSearchFixture.getJournalArticles();
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();
		_users = userSearchFixture.getUsers();
	}

	protected JournalArticle updateJournalArticle(
			JournalArticle journalArticle, ServiceContext serviceContext)
		throws Exception {

		return JournalTestUtil.updateArticle(
			journalArticle, journalArticle.getTitleMap(),
			journalArticle.getContent(), true, true, serviceContext);
	}

	protected final JournalArticleSearchFixture journalArticleSearchFixture =
		new JournalArticleSearchFixture();
	protected final UserSearchFixture userSearchFixture =
		new UserSearchFixture();

	@Inject
	private static FacetedSearcherManager _facetedSearcherManager;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	@DeleteAfterTestRun
	private List<User> _users;

}