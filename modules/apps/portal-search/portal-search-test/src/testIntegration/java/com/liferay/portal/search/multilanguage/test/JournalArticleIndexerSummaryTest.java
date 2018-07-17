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
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolderConstants;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.highlight.HighlightUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.test.journal.util.JournalArticleContent;
import com.liferay.portal.search.test.journal.util.JournalArticleTitle;
import com.liferay.portal.service.test.ServiceTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
public class JournalArticleIndexerSummaryTest
	extends BaseMultiLanguageSummaryTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		super.setUp();

		ServiceTestUtil.setUser(TestPropsValues.getUser());

		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());
		_indexer = indexerRegistry.getIndexer(JournalArticle.class);
	}

	@Test
	public void testMaxContentLength() throws Exception {
		String originalTitle = "test title";
		String originalContent = "test content";
		String searchTerm = "test";

		Summary summary = getSummary(
			originalTitle, originalContent, searchTerm);

		String highlightedContent = StringBundler.concat(
			HighlightUtil.HIGHLIGHT_TAG_OPEN, searchTerm,
			HighlightUtil.HIGHLIGHT_TAG_CLOSE, " content");

		Assert.assertEquals(200, summary.getMaxContentLength(), 0);

		testMaxContentLength(
			summary.getContent(), summary.getMaxContentLength(),
			highlightedContent);

		searchTerm = "more";

		originalTitle = searchTerm + " test title";

		originalContent =
			" test content test content test content test content test " +
				"content test content test content test content test content ";

		originalContent +=
			"test content test content test content test content test " +
				"content test content test content test content test content ";

		originalContent +=
			"test content test content test content test content test content";

		summary = getSummary(
			originalTitle, searchTerm + originalContent, searchTerm);

		highlightedContent = StringBundler.concat(
			HighlightUtil.HIGHLIGHT_TAG_OPEN, searchTerm,
			HighlightUtil.HIGHLIGHT_TAG_CLOSE, originalContent);

		Assert.assertEquals(225, summary.getMaxContentLength(), 0);

		testMaxContentLength(
			summary.getContent(), summary.getMaxContentLength(),
			highlightedContent);
	}

	@Test
	public void testSummaryHighlighted() throws Exception {
		String originalTitle = "test title";
		String translatedTitle = "teszt neve";

		String originalContent = "test content";
		String translatedContent = "teszt tartalom";

		_journalArticles.add(
			addJournalArticle(
				new JournalArticleTitle() {
					{
						put(LocaleUtil.US, originalTitle);
						put(LocaleUtil.HUNGARY, translatedTitle);
					}
				},
				new JournalArticleContent() {
					{
						name = "content";
						defaultLocale = LocaleUtil.US;

						put(LocaleUtil.US, originalContent);
						put(LocaleUtil.HUNGARY, translatedContent);
					}
				}));

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

		searchTerm = "teszt";

		documents = search(searchTerm, LocaleUtil.HUNGARY, getGroup());

		document = getSingleDocument(searchTerm, documents);

		assertHighlight(originalTitle, originalContent, document);
	}

	protected JournalArticle addJournalArticle(
			JournalArticleTitle journalArticleTitle,
			JournalArticleContent journalArticleContent)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(getGroup().getGroupId());

		long userId = serviceContext.getUserId();

		long folderId = JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		Map<Locale, String> titleMap = journalArticleTitle.getValues();
		Map<Locale, String> descriptionMap = null;
		String contentString = journalArticleContent.getContentString();
		String ddmStructureKey = "BASIC-WEB-CONTENT";
		String ddmTemplateKey = "BASIC-WEB-CONTENT";

		return JournalArticleLocalServiceUtil.addArticle(
			userId, getGroup().getGroupId(), folderId, titleMap, descriptionMap,
			contentString, ddmStructureKey, ddmTemplateKey, serviceContext);
	}

	protected Indexer<?> getIndexer() {
		return _indexer;
	}

	protected Summary getSummary(
			String originalTitle, String originalContent, String searchTerm)
		throws Exception, SearchException {

		_journalArticles.add(
			addJournalArticle(
				new JournalArticleTitle() {
					{
						put(LocaleUtil.US, originalTitle);
					}
				},
				new JournalArticleContent() {
					{
						name = "content";
						defaultLocale = LocaleUtil.US;
						put(LocaleUtil.US, originalContent);
					}
				}));

		List<Document> documents = search(
			searchTerm, LocaleUtil.US, getGroup());

		Document document = getSingleDocument(searchTerm, documents);

		return getIndexer().getSummary(
			document, null, createPortletRequest(), createPortletResponse());
	}

	private Indexer<JournalArticle> _indexer;

	@DeleteAfterTestRun
	private final List<JournalArticle> _journalArticles = new ArrayList<>();

}