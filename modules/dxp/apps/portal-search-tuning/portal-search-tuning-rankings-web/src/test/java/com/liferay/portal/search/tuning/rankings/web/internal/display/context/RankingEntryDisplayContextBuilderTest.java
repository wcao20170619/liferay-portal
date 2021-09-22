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

package com.liferay.portal.search.tuning.rankings.web.internal.display.context;

import com.liferay.portal.search.tuning.rankings.web.internal.index.Ranking;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Wade Cao
 */
public class RankingEntryDisplayContextBuilderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		_rankingEntryDisplayContextBuilder =
			new RankingEntryDisplayContextBuilder(_ranking);
	}

	@Test
	public void testBuild() throws Exception {
		Mockito.doReturn(
			Arrays.asList("aliases")
		).when(
			_ranking
		).getAliases();

		Mockito.doReturn(
			Arrays.asList("blockIds")
		).when(
			_ranking
		).getHiddenDocumentIds();

		Mockito.doReturn(
			false
		).when(
			_ranking
		).isInactive();

		Mockito.doReturn(
			"indexName"
		).when(
			_ranking
		).getIndexName();

		Mockito.doReturn(
			"rankingDocumentId"
		).when(
			_ranking
		).getRankingDocumentId();

		Mockito.doReturn(
			"nameForDisplay"
		).when(
			_ranking
		).getNameForDisplay();

		Mockito.doReturn(
			Arrays.asList(new Ranking.Pin[] {Mockito.mock(Ranking.Pin.class)})
		).when(
			_ranking
		).getPins();

		Mockito.doReturn(
			"name"
		).when(
			_ranking
		).getName();

		RankingEntryDisplayContext rankingEntryDisplayContext =
			_rankingEntryDisplayContextBuilder.build();

		Assert.assertEquals("aliases", rankingEntryDisplayContext.getAliases());
		Assert.assertEquals(
			"1", rankingEntryDisplayContext.getHiddenResultsCount());
		Assert.assertEquals("indexName", rankingEntryDisplayContext.getIndex());
		Assert.assertEquals(
			"nameForDisplay", rankingEntryDisplayContext.getKeywords());
		Assert.assertEquals(
			"1", rankingEntryDisplayContext.getPinnedResultsCount());
		Assert.assertEquals(
			"rankingDocumentId", rankingEntryDisplayContext.getUid());

		Assert.assertFalse(rankingEntryDisplayContext.getInactive());
	}

	@Mock
	private Ranking _ranking;

	private RankingEntryDisplayContextBuilder
		_rankingEntryDisplayContextBuilder;

}