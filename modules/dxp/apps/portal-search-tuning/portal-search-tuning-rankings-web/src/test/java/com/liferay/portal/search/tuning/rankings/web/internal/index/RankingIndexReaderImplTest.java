/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.rankings.web.internal.index;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.tuning.rankings.web.internal.index.name.RankingIndexName;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class RankingIndexReaderImplTest extends BaseRankingsIndexTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_rankingIndexReaderImpl = new RankingIndexReaderImpl();

		ReflectionTestUtil.setFieldValue(
			_rankingIndexReaderImpl, "_documentToRankingTranslator",
			_documentToRankingTranslator);

		_rankingIndexReaderImpl.setQueries(queries);
		_rankingIndexReaderImpl.setSearchEngineAdapter(searchEngineAdapter);
	}

	@Test
	public void testFetchByQueryStringOptional() {
		_setupDocumentToRankingTranslator();

		setupQueries();
		setupSearchEngineAdapter(
			setupGetDocumentResponseGetDocument(
				setupDocument(Arrays.asList("queryStrings")),
				setupGetDocumentResponse()));
		setupSearchEngineAdapter(
			setupSearchHits(Arrays.asList("queryStrings")));

		Assert.assertEquals(
			Optional.of(_setupDocumentToRankingTranslator()),
			_rankingIndexReaderImpl.fetchByQueryStringOptional(
				Mockito.mock(RankingIndexName.class), "queryString"));
	}

	@Test
	public void testFetchByQueryStringOptionalBlankQueryString() {
		Assert.assertEquals(
			Optional.empty(),
			_rankingIndexReaderImpl.fetchByQueryStringOptional(
				Mockito.mock(RankingIndexName.class), ""));
	}

	@Test
	public void testFetchOptional() {
		setupSearchEngineAdapter(
			setupGetDocumentResponseGetDocument(
				setupDocument(Arrays.asList("queryStrings")),
				setupGetDocumentResponse()));

		Assert.assertEquals(
			Optional.of(_setupDocumentToRankingTranslator()),
			_rankingIndexReaderImpl.fetchOptional(
				Mockito.mock(RankingIndexName.class), "id"));
	}

	@Test
	public void testIsExistsFalse() {
		setupSearchEngineAdapter(false);

		Assert.assertFalse(
			_rankingIndexReaderImpl.isExists(
				Mockito.mock(RankingIndexName.class)));
	}

	@Test
	public void testIsExistsTrue() {
		setupSearchEngineAdapter(true);

		Assert.assertTrue(
			_rankingIndexReaderImpl.isExists(
				Mockito.mock(RankingIndexName.class)));
	}

	@Override
	protected SearchSearchResponse setupSearchSearchResponse() {
		SearchSearchResponse searchSearchResponse = Mockito.mock(
			SearchSearchResponse.class);

		Mockito.doReturn(
			1L
		).when(
			searchSearchResponse
		).getCount();

		return searchSearchResponse;
	}

	private Ranking _setupDocumentToRankingTranslator() {
		Ranking ranking = Mockito.mock(Ranking.class);

		Mockito.doReturn(
			ranking
		).when(
			_documentToRankingTranslator
		).translate(
			Mockito.anyObject(), Mockito.anyString()
		);

		return ranking;
	}

	@Mock
	private DocumentToRankingTranslator _documentToRankingTranslator;

	private RankingIndexReaderImpl _rankingIndexReaderImpl;

}