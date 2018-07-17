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

package com.liferay.portal.search.multilanguage.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.highlight.HighlightUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.service.test.ServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
@Sync
public class BlogsEntryIndexerSummaryTest
	extends BaseMultiLanguageSummaryTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ServiceTestUtil.setUser(TestPropsValues.getUser());

		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());
		_indexer = indexerRegistry.getIndexer(BlogsEntry.class);
	}

	@Test
	public void testSummaryHighlighted() throws Exception {
		String originalTitle = "test title";
		String originalContent = "test content";

		addBlogsEntry(getGroup(), getUser(), originalTitle, originalContent);

		String searchTerm = "test";

		List<Document> documents = search(
			searchTerm, LocaleUtil.US, getGroup());

		Document document = getSingleDocument(searchTerm, documents);

		String highlightedTitle = StringBundler.concat(
			HighlightUtil.HIGHLIGHTS[0], "test", HighlightUtil.HIGHLIGHTS[1],
			" title");
		String highlightedContent = StringBundler.concat(
			HighlightUtil.HIGHLIGHTS[0], "test", HighlightUtil.HIGHLIGHTS[1],
			" content");

		assertHighlight(highlightedTitle, highlightedContent, document);

		searchTerm = "test";

		documents = search(searchTerm, LocaleUtil.HUNGARY, getGroup());

		document = getSingleDocument(searchTerm, documents);

		assertHighlight(highlightedTitle, highlightedContent, document);
	}

	protected BlogsEntry addBlogsEntry(
			Group group, User user, String keyword, String content)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
			user.getUserId(), keyword, content, serviceContext);

		_blogsEntries.add(blogsEntry);

		return blogsEntry;
	}

	@Override
	protected Indexer<?> getIndexer() {
		return _indexer;
	}

	@DeleteAfterTestRun
	private final List<BlogsEntry> _blogsEntries = new ArrayList<>();

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	private Indexer<BlogsEntry> _indexer;

}