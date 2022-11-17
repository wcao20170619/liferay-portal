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

package com.liferay.portal.search.indexer.clauses.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprintBuilder;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class IndexerClausesChangeTrackingTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		JournalArticleSearchFixture journalArticleSearchFixture =
			new JournalArticleSearchFixture(journalArticleLocalService);

		_ctCollection = addCTCollection();
		_group = groupSearchFixture.addGroup(new GroupBlueprint());
		_journalArticleSearchFixture = journalArticleSearchFixture;
		_user = TestPropsValues.getUser();
	}

	@Test
	public void testBaseIndexer() throws Exception {
		Assert.assertTrue(journalArticleIndexer instanceof BaseIndexer);

		JournalArticle journalArticle = addJournalArticle("Gamma Article");

		addJournalArticle("Omega Article");

		Consumer<SearchRequestBuilder> consumer =
			searchRequestBuilder -> searchRequestBuilder.modelIndexerClasses(
				JournalArticle.class
			).queryString(
				"gamma"
			);

		assertSearch("[Gamma Article]", consumer);

		assertSearch(
			"[Gamma Article, Omega Article]", withoutIndexerClauses(),
			consumer);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			updateJournalArticle(journalArticle, "Delta Article");

			assertSearch("[]", consumer);

			assertSearch(
				"[Delta Article, Omega Article]", withoutIndexerClauses(),
				consumer);
		}

		assertSearch("[Gamma Article]", consumer);

		assertSearch(
			getExpected(
				"[Gamma Article, Omega Article]",
				"[Delta Article, Gamma Article, Omega Article]"),
			withoutIndexerClauses(), consumer);
	}

	@Test
	public void testDefaultIndexer() throws Exception {
		Assert.assertEquals(
			"class com.liferay.portal.search.internal.indexer.DefaultIndexer",
			String.valueOf(mbMessageIndexer.getClass()));

		MBMessage mbMessage = addMessage("Gamma Message");

		addMessage("Omega Message");

		Consumer<SearchRequestBuilder> consumer =
			searchRequestBuilder -> searchRequestBuilder.modelIndexerClasses(
				MBMessage.class
			).queryString(
				"gamma"
			);

		assertSearch("[Gamma Message]", consumer);

		assertSearch(
			"[Gamma Message, Omega Message]", withoutIndexerClauses(),
			consumer);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			updateMessage(mbMessage, "Delta Message");

			assertSearch("[]", consumer);

			assertSearch(
				"[Delta Message, Omega Message]", withoutIndexerClauses(),
				consumer);
		}

		assertSearch("[Gamma Message]", consumer);

		assertSearch(
			getExpected(
				"[Gamma Message, Omega Message]",
				"[Delta Message, Gamma Message, Omega Message]"),
			withoutIndexerClauses(), consumer);
	}

	@Test
	public void testFacetedSearcher() throws Exception {
		JournalArticle journalArticle = addJournalArticle("Gamma Article");

		addJournalArticle("Omega Article");

		MBMessage mbMessage = addMessage("Gamma Message");

		addMessage("Omega Message");

		Consumer<SearchRequestBuilder> consumer =
			searchRequestBuilder -> searchRequestBuilder.modelIndexerClasses(
				JournalArticle.class, MBMessage.class
			).queryString(
				"gamma"
			);

		assertSearch("[Gamma Article, Gamma Message]", consumer);

		assertSearch(
			"[Gamma Article, Gamma Message, Omega Article, Omega Message]",
			withoutIndexerClauses(), consumer);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			updateJournalArticle(journalArticle, "Delta Article");
			updateMessage(mbMessage, "Delta Message");

			assertSearch("[]", consumer);

			assertSearch(
				"[Delta Article, Delta Message, Omega Article, Omega Message]",
				withoutIndexerClauses(), consumer);
		}

		assertSearch("[Gamma Article, Gamma Message]", consumer);

		assertSearch(
			getExpected(
				"[Gamma Article, Gamma Message, Omega Article, Omega Message]",
				"[Delta Article, Delta Message, Gamma Article, Gamma " +
					"Message, Omega Article, Omega Message]"),
			withoutIndexerClauses(), consumer);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Rule
	public TestName testName = new TestName();

	protected CTCollection addCTCollection() throws PortalException {
		return ctCollectionLocalService.addCTCollection(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(),
			IndexerClausesChangeTrackingTest.class.getName());
	}

	protected JournalArticle addJournalArticle(String title) {
		return _journalArticleSearchFixture.addArticle(
			JournalArticleBlueprintBuilder.builder(
			).groupId(
				_group.getGroupId()
			).journalArticleContent(
				new JournalArticleContent() {
					{
						put(LocaleUtil.US, RandomTestUtil.randomString());

						setDefaultLocale(LocaleUtil.US);
						setName("content");
					}
				}
			).journalArticleTitle(
				new JournalArticleTitle() {
					{
						put(LocaleUtil.US, title);
					}
				}
			).userId(
				_user.getUserId()
			).build());
	}

	protected MBMessage addMessage(String title) throws Exception {
		return mbMessageLocalService.addMessage(
			null, _user.getUserId(), RandomTestUtil.randomString(),
			_group.getGroupId(), MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			0L, MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, title,
			RandomTestUtil.randomString(), MBMessageConstants.DEFAULT_FORMAT,
			null, false, 0.0, false, _createServiceContext());
	}

	protected void assertSearch(
		String expected, Consumer<SearchRequestBuilder>... consumers) {

		SearchResponse searchResponse = searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).fields(
				StringPool.STAR
			).groupIds(
				_group.getGroupId()
			).withSearchRequestBuilder(
				consumers
			).build());

		String requestString = searchResponse.getRequestString();

		String evidence = "\"ctCollectionId\"";

		if (Objects.equals("Solr", getSearchEngineVendor())) {
			evidence = "ctCollectionId";
		}

		if (!requestString.contains(evidence)) {
			Assert.assertEquals(evidence, requestString);
		}

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse.getRequestString(),
			searchResponse.getDocumentsStream(), _TITLE_EN_US, expected);
	}

	protected String getExpected(
		String elasticResultExcepted, String solrResultExpected) {

		if (Objects.equals("Solr", getSearchEngineVendor())) {
			return solrResultExpected;
		}

		return elasticResultExcepted;
	}

	protected String getSearchEngineVendor() {
		SearchEngine searchEngine = searchEngineHelper.getSearchEngine();

		return searchEngine.getVendor();
	}

	protected void updateJournalArticle(
			JournalArticle journalArticle, String title)
		throws Exception {

		journalArticle.setTitleMap(
			Collections.singletonMap(LocaleUtil.US, title));

		_journalArticleSearchFixture.updateArticle(journalArticle);
	}

	protected void updateMessage(MBMessage mbMessage, String title)
		throws Exception {

		mbMessage.setSubject(title);

		mbMessageLocalService.updateMBMessage(mbMessage);
	}

	protected Consumer<SearchRequestBuilder> withoutIndexerClauses() {
		return searchRequestBuilder -> searchRequestBuilder.withSearchContext(
			searchContext -> searchContext.setAttribute(
				"search.full.query.suppress.indexer.provided.clauses", true));
	}

	@Inject
	protected CTCollectionLocalService ctCollectionLocalService;

	@Inject(filter = "component.name=*.JournalArticleIndexer")
	protected Indexer<JournalArticle> journalArticleIndexer;

	@Inject
	protected JournalArticleLocalService journalArticleLocalService;

	@Inject(
		filter = "indexer.class.name=com.liferay.message.boards.model.MBMessage"
	)
	protected Indexer<MBMessage> mbMessageIndexer;

	@Inject
	protected MBMessageLocalService mbMessageLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	@Inject
	protected Searcher searcher;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	private ServiceContext _createServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());
	}

	private static final String _TITLE_EN_US = StringBundler.concat(
		Field.TITLE, StringPool.UNDERLINE, LocaleUtil.US);

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@DeleteAfterTestRun
	private Group _group;

	private JournalArticleSearchFixture _journalArticleSearchFixture;
	private User _user;

}