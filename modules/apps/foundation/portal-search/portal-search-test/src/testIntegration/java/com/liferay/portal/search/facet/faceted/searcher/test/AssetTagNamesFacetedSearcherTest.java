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

package com.liferay.portal.search.facet.faceted.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.facet.tag.AssetTagNamesFacetFactory;
import com.liferay.portal.search.test.journal.util.JournalArticleBuilder;
import com.liferay.portal.search.test.journal.util.JournalArticleContent;
import com.liferay.portal.search.test.journal.util.JournalArticleTitle;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrew Betts
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
@Sync
public class AssetTagNamesFacetedSearcherTest
	extends BaseFacetedSearcherTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testAggregation() throws Exception {
		String tag = RandomTestUtil.randomString();

		Group group = userSearchFixture.addGroup();

		addUser(group, tag);

		addJournalArticle(tag, group);

		SearchContext searchContext = getSearchContext(tag);

		Facet facet = _assetTagNamesFacetFactory.newInstance(searchContext);

		searchContext.addFacet(facet);

		Hits hits = search(searchContext);

		assertEntryClassNames(
			_entryClassNames, hits, tag, Field.ENTRY_CLASS_NAME);

		String tagToLowerCase = StringUtil.toLowerCase(tag);

		List<String> tagList = Arrays.asList(tagToLowerCase);

		assertFrequencies(facet.getFieldName(), searchContext, toMap(tagList));
	}

	@Test
	public void testSearchByFacet() throws Exception {
		String tag = RandomTestUtil.randomString();

		addUser(tag);

		SearchContext searchContext = getSearchContext(tag);

		Facet facet = _assetTagNamesFacetFactory.newInstance(searchContext);

		searchContext.addFacet(facet);

		search(searchContext);

		String tagToLowerCase = StringUtil.toLowerCase(tag);

		Map<String, Integer> frequencies = Collections.singletonMap(
			tagToLowerCase, 1);

		assertFrequencies(facet.getFieldName(), searchContext, frequencies);
	}

	@Test
	public void testSearchQuoted() throws Exception {
		String[] assetTagNames = {"Enterprise", "Open Source", "For   Life"};

		User user = addUser(assetTagNames);

		Map<String, String> expected = userSearchFixture.toMap(
			user, assetTagNames);

		assertTags("\"Enterprise\"", expected);
		assertTags("\"Open\"", expected);
		assertTags("\"Source\"", expected);
		assertTags("\"Open Source\"", expected);
		assertTags("\"For   Life\"", expected);
	}

	@Test
	public void testSelection() throws Exception {
		String tag = RandomTestUtil.randomString();

		Group group = userSearchFixture.addGroup();

		addUser(group, tag);

		addJournalArticle(tag, group);

		SearchContext searchContext = getSearchContext(tag);

		Facet facet = _assetTagNamesFacetFactory.newInstance(searchContext);

		facet.select(tag);

		searchContext.addFacet(facet);

		Hits hits = search(searchContext);

		List<String> entryClassNameList = Arrays.asList(User.class.getName());

		assertEntryClassNames(
			entryClassNameList, hits, tag, Field.ENTRY_CLASS_NAME);

		String tagToLowerCase = StringUtil.toLowerCase(tag);

		Map<String, Integer> frequencies = Collections.singletonMap(
			tagToLowerCase, 1);

		assertFrequencies(facet.getFieldName(), searchContext, frequencies);
	}

	protected void addJournalArticle(String title, Group group)
		throws Exception {

		JournalArticleBuilder journalArticleBuilder =
			new JournalArticleBuilder();

		journalArticleBuilder.setContent(
			new JournalArticleContent() {
				{
					name = "content";
					defaultLocale = LocaleUtil.US;

					put(LocaleUtil.US, RandomTestUtil.randomString());
				}
			});
		journalArticleBuilder.setGroupId(group.getGroupId());
		journalArticleBuilder.setTitle(
			new JournalArticleTitle() {
				{
					put(LocaleUtil.US, title);
				}
			});

		journalArticleSearchFixture.addArticle(journalArticleBuilder);
	}

	protected User addUser(String... assetTagNames) throws Exception {
		Group group = userSearchFixture.addGroup();

		return addUser(group, assetTagNames);
	}

	protected void assertEntryClassNames(
		List<String> entryclassnames, Hits hits, String keyword,
		String fieldName) {

		DocumentsAssert.assertValuesIgnoreRelevance(
			keyword, hits.getDocs(), fieldName, entryclassnames);
	}

	protected void assertTags(String keywords, Map<String, String> expected)
		throws Exception {

		SearchContext searchContext = getSearchContext(keywords);

		Hits hits = search(searchContext);

		assertTags(keywords, hits, expected);
	}

	protected Map<String, Integer> toMap(Collection<String> strings) {
		return strings.stream().collect(Collectors.toMap(s -> s, s -> 1));
	}

	@Inject
	private static AssetTagNamesFacetFactory _assetTagNamesFacetFactory;

	private static final List<String> _entryClassNames = Arrays.asList(
		JournalArticle.class.getName(), User.class.getName());

}