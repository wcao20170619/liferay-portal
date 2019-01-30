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

package com.liferay.journal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprint;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleDescription;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.highlight.HighlightUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.test.util.SearchContextTestUtil;
import com.liferay.portal.search.test.util.SummaryFixture;
import com.liferay.portal.service.test.ServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class JournalArticleMultiLanguageSearchSummaryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		ServiceTestUtil.setUser(TestPropsValues.getUser());

		_indexer = indexerRegistry.getIndexer(JournalArticle.class);

		_journalArticleSearchFixture = new JournalArticleSearchFixture(
			journalArticleLocalService);

		_journalArticleSearchFixture.setUp();

		_journalArticles = _journalArticleSearchFixture.getJournalArticles();

		_userSearchFixture = new UserSearchFixture();

		_userSearchFixture.setUp();

		_groups = _userSearchFixture.getGroups();
		_users = _userSearchFixture.getUsers();

		_group = _userSearchFixture.addGroup();

		_user = _userSearchFixture.addUser(
			RandomTestUtil.randomString(), _group);

		_summaryFixture = new SummaryFixture<>(
			JournalArticle.class, _group, null, _user);
	}

	@After
	public void tearDown() {
		_journalArticleSearchFixture.tearDown();

		_userSearchFixture.tearDown();
	}

	@Test
	public void testBrContentUntranslatedHighlightedTranslatedPlain()
		throws Exception {

		String title = "All About Clocks";

		addArticleUntranslated(title, "Clocks are great for telling time");

		String brContent = "Times de futebol são populares";
		String brDescription = "Sobre times de futebol";
		String brTitle = "Tudo Sobre Futebol";
		String usContent = "Soccer teams are popular";
		String usDescription = "On soccer teams";
		String usTitle = "All About Soccer";

		addArticleTranslated(
			usTitle, usContent, usDescription, brTitle, brContent,
			brDescription);

		List<Document> documents = search("time", LocaleUtil.BRAZIL);

		Document document1 = getDocumentByUSTitle(documents, title);

		_summaryFixture.assertSummary(
			title,
			StringBundler.concat(
				"Clocks are great for telling ",
				HighlightUtil.HIGHLIGHT_TAG_OPEN, "time",
				HighlightUtil.HIGHLIGHT_TAG_CLOSE),
			LocaleUtil.BRAZIL, document1);

		Document document2 = getDocumentByUSTitle(documents, usTitle);

		_summaryFixture.assertSummary(
			brTitle,
			StringBundler.concat(
				"Sobre ", HighlightUtil.HIGHLIGHT_TAG_OPEN,
				HighlightUtil.HIGHLIGHT_TAG_OPEN, "times",
				HighlightUtil.HIGHLIGHT_TAG_CLOSE,
				HighlightUtil.HIGHLIGHT_TAG_CLOSE, " de futebol"),
			LocaleUtil.BRAZIL, document2);

		Assert.assertEquals(documents.toString(), 2, documents.size());
	}

	@Test
	public void testBrDescriptionUntranslatedHighlightedTwiceTranslatedPlain()
		throws Exception {

		String title = "All About Clocks";

		addArticleUntranslated(
			title, "Clocks are great for telling time", "On clocks and time");

		String brContent = "Times de futebol são populares";
		String brDescription = "Sobre times de futebol";
		String brTitle = "Tudo Sobre Futebol";
		String usContent = "Soccer teams are popular";
		String usDescription = "On soccer teams";
		String usTitle = "All About Soccer";

		addArticleTranslated(
			usTitle, usContent, usDescription, brTitle, brContent,
			brDescription);

		List<Document> documents = search("time", LocaleUtil.BRAZIL);

		Document document1 = getDocumentByUSTitle(documents, title);

		_summaryFixture.assertSummary(
			title,
			StringBundler.concat(
				"On clocks and ", HighlightUtil.HIGHLIGHT_TAG_OPEN,
				HighlightUtil.HIGHLIGHT_TAG_OPEN, "time",
				HighlightUtil.HIGHLIGHT_TAG_CLOSE,
				HighlightUtil.HIGHLIGHT_TAG_CLOSE),
			LocaleUtil.BRAZIL, document1);

		Document document2 = getDocumentByUSTitle(documents, usTitle);

		_summaryFixture.assertSummary(
			brTitle,
			StringBundler.concat(
				"Sobre ", HighlightUtil.HIGHLIGHT_TAG_OPEN,
				HighlightUtil.HIGHLIGHT_TAG_OPEN, "times",
				HighlightUtil.HIGHLIGHT_TAG_CLOSE,
				HighlightUtil.HIGHLIGHT_TAG_CLOSE, " de futebol"),
			LocaleUtil.BRAZIL, document2);

		Assert.assertEquals(documents.toString(), 2, documents.size());
	}

	@Test
	public void testUsContentUntranslatedHighlightedTranslatedPlain()
		throws Exception {

		String title = "All About Clocks";

		addArticleUntranslated(title, "Clocks are great for telling time");

		String brContent = "Times de futebol são populares";
		String brTitle = "Tudo Sobre Futebol";
		String usContent = "Soccer teams are popular";
		String usTitle = "All About Soccer";

		addArticleTranslated(usTitle, usContent, brTitle, brContent);

		List<Document> documents = search("time", LocaleUtil.US);

		Document document1 = getDocumentByUSTitle(documents, title);

		_summaryFixture.assertSummary(
			title,
			StringBundler.concat(
				"Clocks are great for telling ",
				HighlightUtil.HIGHLIGHT_TAG_OPEN, "time",
				HighlightUtil.HIGHLIGHT_TAG_CLOSE),
			LocaleUtil.US, document1);

		Document document2 = getDocumentByUSTitle(documents, usTitle);

		_summaryFixture.assertSummary(
			usTitle, usContent, LocaleUtil.US, document2);

		Assert.assertEquals(documents.toString(), 2, documents.size());
	}

	@Test
	public void testUsDescriptionUntranslatedHighlightedTwiceTranslatedPlain()
		throws Exception {

		String content = "Clocks are great for telling time";
		String description = "On clocks and time";
		String title = "All About Clocks";

		addArticleUntranslated(title, content, description);

		String brContent = "Times de futebol são populares";
		String brDescription = "Sobre times de futebol";
		String brTitle = "Tudo Sobre Futebol";
		String usContent = "Soccer teams are popular";
		String usDescription = "On soccer teams";
		String usTitle = "All About Soccer";

		addArticleTranslated(
			usTitle, usContent, usDescription, brTitle, brContent,
			brDescription);

		List<Document> documents = search("time", LocaleUtil.US);

		Document document1 = getDocumentByUSTitle(documents, title);

		_summaryFixture.assertSummary(
			title,
			StringBundler.concat(
				"On clocks and ", HighlightUtil.HIGHLIGHT_TAG_OPEN,
				HighlightUtil.HIGHLIGHT_TAG_OPEN, "time",
				HighlightUtil.HIGHLIGHT_TAG_CLOSE,
				HighlightUtil.HIGHLIGHT_TAG_CLOSE),
			LocaleUtil.US, document1);

		Document document2 = getDocumentByUSTitle(documents, usTitle);

		_summaryFixture.assertSummary(
			usTitle, usDescription, LocaleUtil.US, document2);

		Assert.assertEquals(documents.toString(), 2, documents.size());
	}

	protected void addArticleTranslated(
		String usTitle, String usContent, String brTitle, String brContent) {

		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(LocaleUtil.BRAZIL, brContent);
								put(LocaleUtil.US, usContent);

								setDefaultLocale(LocaleUtil.US);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(LocaleUtil.BRAZIL, brTitle);
								put(LocaleUtil.US, usTitle);
							}
						});
				}
			});
	}

	protected void addArticleTranslated(
		String usTitle, String usContent, String usDescription, String brTitle,
		String brContent, String brDescription) {

		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(LocaleUtil.BRAZIL, brContent);
								put(LocaleUtil.US, usContent);

								setDefaultLocale(LocaleUtil.US);
								setName("content");
							}
						});
					setJournalArticleDescription(
						new JournalArticleDescription() {
							{
								put(LocaleUtil.BRAZIL, brDescription);
								put(LocaleUtil.US, usDescription);
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(LocaleUtil.BRAZIL, brTitle);
								put(LocaleUtil.US, usTitle);
							}
						});
				}
			});
	}

	protected void addArticleUntranslated(String title, String content) {
		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(LocaleUtil.US, content);

								setDefaultLocale(LocaleUtil.US);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(LocaleUtil.US, title);
							}
						});
				}
			});
	}

	protected void addArticleUntranslated(
		String title, String content, String description) {

		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(LocaleUtil.US, content);

								setDefaultLocale(LocaleUtil.US);
								setName("content");
							}
						});
					setJournalArticleDescription(
						new JournalArticleDescription() {
							{
								put(LocaleUtil.US, description);
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(LocaleUtil.US, title);
							}
						});
				}
			});
	}

	protected Document getDocumentByUSTitle(
		List<Document> documents, String title) {

		Stream<Document> stream = documents.stream();

		Optional<Document> documentOptional = stream.filter(
			document -> title.equals(document.get(LocaleUtil.US, "title"))
		).findAny();

		Assert.assertTrue(title, documentOptional.isPresent());

		return documentOptional.get();
	}

	protected List<Document> search(String searchTerm, Locale locale)
		throws PortalException {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_user.getUserId(), new long[] {_group.getGroupId()}, searchTerm,
			locale, true);

		try {
			Hits hits = _indexer.search(searchContext);

			return hits.toList();
		}
		catch (SearchException se) {
			throw new RuntimeException(se);
		}
	}

	@Inject
	protected IndexerRegistry indexerRegistry;

	@Inject
	protected JournalArticleLocalService journalArticleLocalService;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private Indexer<JournalArticle> _indexer;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	private JournalArticleSearchFixture _journalArticleSearchFixture;
	private SummaryFixture<JournalArticle> _summaryFixture;
	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

	private UserSearchFixture _userSearchFixture;

}