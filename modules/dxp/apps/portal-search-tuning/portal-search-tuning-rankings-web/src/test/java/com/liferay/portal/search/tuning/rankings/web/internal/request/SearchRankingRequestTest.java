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

package com.liferay.portal.search.tuning.rankings.web.internal.request;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.MatchAllQuery;
import com.liferay.portal.search.query.MatchQuery;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.tuning.rankings.web.internal.BaseRankingsWebTestCase;
import com.liferay.portal.search.tuning.rankings.web.internal.index.name.RankingIndexName;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import javax.servlet.http.HttpServletRequest;

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
public class SearchRankingRequestTest extends BaseRankingsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_setUpQuery();

		setUpPropsUtil();

		HttpServletRequest httpServletRequest =
			setUpPortalGetHttpServletRequest();

		setUpHttpServletRequestGetAttribute(
			httpServletRequest, WebKeys.THEME_DISPLAY,
			Mockito.mock(ThemeDisplay.class));

		_searchRankingRequest = new SearchRankingRequest(
			httpServletRequest, queries, _rankingIndexName, _sorts,
			Mockito.mock(SearchContainer.class), searchEngineAdapter);
	}

	@Test
	public void testSearch() throws Exception {
		SearchHits searchHits = setUpSearchEngineAdapter(
			Mockito.mock(SearchHits.class));

		SearchRankingResponse searchRankingResponse =
			_searchRankingRequest.search();

		Assert.assertEquals(searchHits, searchRankingResponse.getSearchHits());
	}

	@Test
	public void testSearchWithBlankKeyword() throws Exception {
		SearchHits searchHits = setUpSearchEngineAdapter(
			Mockito.mock(SearchHits.class));

		SearchRankingResponse searchRankingResponse =
			_searchRankingRequest.search();

		Assert.assertEquals(searchHits, searchRankingResponse.getSearchHits());
	}

	private void _setUpQuery() {
		Mockito.doReturn(
			Mockito.mock(MatchQuery.class)
		).when(
			queries
		).match(
			Mockito.anyString(), Mockito.anyString()
		);

		Mockito.doReturn(
			Mockito.mock(MatchAllQuery.class)
		).when(
			queries
		).matchAll();
	}

	@Mock
	private RankingIndexName _rankingIndexName;

	private SearchRankingRequest _searchRankingRequest;

	@Mock
	private Sorts _sorts;

}