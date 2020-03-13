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

package com.liferay.portal.search.elasticsearch6.internal.highlight;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.highlight.HighlightUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.analysis.FieldQueryBuilder;
import com.liferay.portal.search.elasticsearch6.internal.HighlighterWithAnalyzersFixture;
import com.liferay.portal.search.elasticsearch6.internal.HighlighterWithAnalyzersFixtureFactory;
import com.liferay.portal.search.internal.analysis.SimpleKeywordTokenizer;
import com.liferay.portal.search.internal.analysis.TitleFieldQueryBuilder;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelpers;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class HighlighterWithAnalyzersTest extends BaseIndexingTestCase {

	@Before
	public void setUp() throws Exception {
		super.setUp();

		LocalizationUtil localizationUtil = new LocalizationUtil();

		localizationUtil.setLocalization(_createLocalization());
	}

	@Test
	public void testHighlightingEnglish() throws Exception {
		addDocuments(
			value -> DocumentCreationHelpers.singleText(_TEST_EN_US, value),
			Arrays.asList("articles", "article1", "articlez", "journal"));

		FieldQueryBuilder fieldQueryBuilder = _createFieldQueryBuilder();

		Query query = fieldQueryBuilder.build(_TEST_EN_US, "article");

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			Arrays.asList("articles", "journal"), _TEST_EN_US, query,
			_TEST_EN_US);

		String snippetFieldName = "snippet_".concat(_TEST_EN_US);

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights("[H]articles[/H]", "[H]journal[/H]"), _TEST_EN_US,
			query, snippetFieldName);
	}

	@Test
	public void testHighlightingKuromoji() throws Exception {
		addDocuments(
			value -> DocumentCreationHelpers.singleText(_TEST_JA_JP, value),
			Arrays.asList("articles", "article1", "articlez"));

		FieldQueryBuilder fieldQueryBuilder = _createFieldQueryBuilder();

		Query query = fieldQueryBuilder.build(_TEST_JA_JP, "article");

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			Arrays.asList("article1", "articles", "articlez"), _TEST_JA_JP,
			query, _TEST_JA_JP);

		String snippetFieldName = "snippet_".concat(_TEST_JA_JP);

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights(
				"[H]article[/H]1", "[H]articles[/H]", "[H]articlez[/H]"),
			_TEST_JA_JP, query, snippetFieldName);
	}

	@Test
	public void testHighlightingSpanish() throws Exception {
		addDocuments(
			value -> DocumentCreationHelpers.singleText(_TEST_ES_ES, value),
			Arrays.asList("articles", "article1", "articlez"));

		FieldQueryBuilder fieldQueryBuilder = _createFieldQueryBuilder();

		Query query = fieldQueryBuilder.build(_TEST_ES_ES, "article");

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			Arrays.asList("article1", "articles", "articlez"), _TEST_ES_ES,
			query, _TEST_ES_ES);

		String snippetFieldName = "snippet_".concat(_TEST_ES_ES);

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights(
				"[H]article1[/H]", "[H]articles[/H]", "[H]articlez[/H]"),
			_TEST_ES_ES, query, snippetFieldName);
	}

	@Test
	public void testHighlightingSynonymNoStopword() throws Exception {
		addDocuments(
			value -> DocumentCreationHelpers.singleText(
				_TEST_NO_STOPWORD_EN_US, value),
			Arrays.asList("thing with words", "for you"));

		FieldQueryBuilder fieldQueryBuilder = _createFieldQueryBuilder();

		Query query = fieldQueryBuilder.build(_TEST_NO_STOPWORD_EN_US, "thing");

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			Arrays.asList("thing with words"), _TEST_NO_STOPWORD_EN_US, query,
			_TEST_NO_STOPWORD_EN_US);

		String snippetFieldName = "snippet_".concat(_TEST_NO_STOPWORD_EN_US);

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights("[H]thing[/H] with words"),
			_TEST_NO_STOPWORD_EN_US, query, snippetFieldName);

		query = fieldQueryBuilder.build(
			_TEST_NO_STOPWORD_EN_US, "\"thing with words\"");

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			Arrays.asList("thing with words"), _TEST_NO_STOPWORD_EN_US, query,
			_TEST_NO_STOPWORD_EN_US);
		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights("[H]thing[/H] [H]with[/H] [H]words[/H]"),
			_TEST_NO_STOPWORD_EN_US, query, snippetFieldName);

		query = fieldQueryBuilder.build(_TEST_NO_STOPWORD_EN_US, "phrase");

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights(""), _TEST_NO_STOPWORD_EN_US, query,
			_TEST_NO_STOPWORD_EN_US);
		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights(""), _TEST_NO_STOPWORD_EN_US, query,
			snippetFieldName);

		query = fieldQueryBuilder.build(_TEST_NO_STOPWORD_EN_US, "\"for you\"");

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights("for you"), _TEST_NO_STOPWORD_EN_US, query,
			_TEST_NO_STOPWORD_EN_US);
		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights("[H]for[/H] [H]you[/H]"), _TEST_NO_STOPWORD_EN_US,
			query, snippetFieldName);

		query = fieldQueryBuilder.build(_TEST_NO_STOPWORD_EN_US, "letter");

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights(""), _TEST_NO_STOPWORD_EN_US, query,
			_TEST_NO_STOPWORD_EN_US);
		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights(""), _TEST_NO_STOPWORD_EN_US, query,
			snippetFieldName);
	}

	@Test
	public void testHighlightingSynonyms() throws Exception {
		addDocuments(
			value -> DocumentCreationHelpers.singleText(
				_TEST_SEARCH_SYNONYMS, value),
			Arrays.asList(
				"article", "articles", "article1", "articlez", "journal"));

		FieldQueryBuilder fieldQueryBuilder = _createFieldQueryBuilder();

		Query query = fieldQueryBuilder.build(_TEST_SEARCH_SYNONYMS, "article");

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			Arrays.asList("article", "articles", "journal"),
			_TEST_SEARCH_SYNONYMS, query, _TEST_SEARCH_SYNONYMS);

		String snippetFieldName = "snippet_".concat(_TEST_SEARCH_SYNONYMS);

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights(
				"[H]article[/H]", "[H]articles[/H]", "[H]journal[/H]"),
			_TEST_SEARCH_SYNONYMS, query, snippetFieldName);
	}

	@Test
	public void testHighlightingSynonymStopword() throws Exception {
		addDocuments(
			value -> DocumentCreationHelpers.singleText(
				_TEST_STOPWORD_EN_US, value),
			Arrays.asList("thing with words", "for you"));

		FieldQueryBuilder fieldQueryBuilder = _createFieldQueryBuilder();

		Query query = fieldQueryBuilder.build(_TEST_STOPWORD_EN_US, "thing");

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			Arrays.asList("thing with words"), _TEST_STOPWORD_EN_US, query,
			_TEST_STOPWORD_EN_US);

		String snippetFieldName = "snippet_".concat(_TEST_STOPWORD_EN_US);

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights("[H]thing[/H] with words"), _TEST_STOPWORD_EN_US,
			query, snippetFieldName);

		query = fieldQueryBuilder.build(
			_TEST_STOPWORD_EN_US, "thing with words");

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			Arrays.asList(""), _TEST_STOPWORD_EN_US, query,
			_TEST_STOPWORD_EN_US);
		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights(""), _TEST_STOPWORD_EN_US, query,
			snippetFieldName);

		query = fieldQueryBuilder.build(_TEST_STOPWORD_EN_US, "phrase");

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights(""), _TEST_STOPWORD_EN_US, query,
			_TEST_STOPWORD_EN_US);
		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights(""), _TEST_STOPWORD_EN_US, query,
			snippetFieldName);

		query = fieldQueryBuilder.build(_TEST_STOPWORD_EN_US, "\"for you\"");

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights(""), _TEST_STOPWORD_EN_US, query,
			_TEST_STOPWORD_EN_US);
		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights(""), _TEST_STOPWORD_EN_US, query,
			snippetFieldName);

		query = fieldQueryBuilder.build(_TEST_STOPWORD_EN_US, "letter");

		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights(""), _TEST_STOPWORD_EN_US, query,
			_TEST_STOPWORD_EN_US);
		_assertSearch(
			queryConfig -> queryConfig.setHighlightFragmentSize(20),
			_toFullHighlights(""), _TEST_STOPWORD_EN_US, query,
			snippetFieldName);
	}

	@Override
	protected IndexingFixture createIndexingFixture() throws Exception {
		HighlighterWithAnalyzersFixture highlighterWithAnalyzersFixture =
			HighlighterWithAnalyzersFixtureFactory.getInstance();

		highlighterWithAnalyzersFixture.setIndexName(_INDEX_NAME);
		highlighterWithAnalyzersFixture.setTestClass(getClass());

		return highlighterWithAnalyzersFixture;
	}

	private void _assertSearch(
			Consumer<QueryConfig> consumer, List<String> expectedValues,
			String fieldName, Query query, String snippetFieldName)
		throws Exception {

		IdempotentRetryAssert.retryAssert(
			5, TimeUnit.SECONDS,
			() -> _doAssertSearch(
				consumer, expectedValues, fieldName, query, snippetFieldName));
	}

	private FieldQueryBuilder _createFieldQueryBuilder() {
		return new TitleFieldQueryBuilder() {
			{
				keywordTokenizer = new SimpleKeywordTokenizer();
			}
		};
	}

	private Localization _createLocalization() {
		Localization localization = Mockito.mock(Localization.class);

		Mockito.doReturn(
			StringPool.BLANK
		).when(
			localization
		).getLocalizedName(
			Mockito.anyString(), Mockito.anyString()
		);

		return localization;
	}

	private Void _doAssertSearch(
			Consumer<QueryConfig> consumer, List<String> expectedValues,
			String fieldName, Query query, String snippetFieldName)
		throws Exception {

		SearchContext searchContext = super.createSearchContext();

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.addHighlightFieldNames(fieldName);
		queryConfig.addSelectedFieldNames(fieldName);
		queryConfig.setHighlightEnabled(true);
		queryConfig.setHighlightRequireFieldMatch(true);

		consumer.accept(queryConfig);

		query.setQueryConfig(queryConfig);

		Hits hits = search(searchContext, query);

		DocumentsAssert.assertValuesIgnoreRelevance(
			(String)searchContext.getAttribute("queryString"), hits.getDocs(),
			snippetFieldName, expectedValues);

		return null;
	}

	private String _toFullHighlight(String s) {
		return StringUtil.replace(
			s, new String[] {"[H]", "[/H]"},
			new String[] {
				HighlightUtil.HIGHLIGHT_TAG_OPEN,
				HighlightUtil.HIGHLIGHT_TAG_CLOSE
			});
	}

	private List<String> _toFullHighlights(String... strings) {
		return Stream.of(
			strings
		).map(
			this::_toFullHighlight
		).collect(
			Collectors.toList()
		);
	}

	private static final String _INDEX_NAME = "highlighter-test-analyzers";

	private static final String _TEST_EN_US = "test_en_US";

	private static final String _TEST_ES_ES = "test_es_ES";

	private static final String _TEST_JA_JP = "test_ja_JP";

	private static final String _TEST_NO_STOPWORD_EN_US =
		"test_no_stopword_en_US";

	private static final String _TEST_SEARCH_SYNONYMS = "test_search_synonyms";

	private static final String _TEST_STOPWORD_EN_US = "test_stopword_en_US";

}