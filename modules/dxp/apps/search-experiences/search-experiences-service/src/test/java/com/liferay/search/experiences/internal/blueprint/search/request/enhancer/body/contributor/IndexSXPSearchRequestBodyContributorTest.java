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

package com.liferay.search.experiences.internal.blueprint.search.request.enhancer.body.contributor;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.index.IndexInformation;
import com.liferay.portal.search.internal.searcher.SearchRequestBuilderFactoryImpl;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.search.experiences.internal.blueprint.search.request.body.contributor.IndexSXPSearchRequestBodyContributor;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.IndexConfiguration;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class IndexSXPSearchRequestBodyContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_indexSXPSearchRequestBodyContributor =
			new IndexSXPSearchRequestBodyContributor(_indexInformation);

		SearchRequestBuilderFactory searchRequestBuilderFactory =
			new SearchRequestBuilderFactoryImpl();

		_searchRequestBuilder = searchRequestBuilderFactory.builder();
	}

	@Test
	public void testContribute() {
		Configuration configuration = new Configuration();

		_indexSXPSearchRequestBodyContributor.contribute(
			configuration, _searchRequestBuilder, null);

		SearchRequest searchRequest = _searchRequestBuilder.build();

		Assert.assertTrue(ListUtil.isEmpty(searchRequest.getIndexes()));

		_setUpIndexInformation("prod-12345");

		IndexConfiguration indexConfiguration = new IndexConfiguration();

		indexConfiguration.setIndexName("12345-search-tuning-rankings");

		configuration.setIndexConfiguration(indexConfiguration);

		_indexSXPSearchRequestBodyContributor.contribute(
			configuration, _searchRequestBuilder, null);

		searchRequest = _searchRequestBuilder.build();

		List<String> indexNames = searchRequest.getIndexes();

		Assert.assertEquals(
			"prod-12345-search-tuning-rankings", indexNames.get(0));

		_setUpIndexInformation("prod-54321");

		indexConfiguration.setIndexName("54321-search-tuning-rankings");

		configuration.setIndexConfiguration(indexConfiguration);

		_indexSXPSearchRequestBodyContributor.contribute(
			configuration, _searchRequestBuilder, null);

		searchRequest = _searchRequestBuilder.build();

		indexNames = searchRequest.getIndexes();

		Assert.assertEquals(
			"prod-12345-search-tuning-rankings", indexNames.get(0));
		Assert.assertEquals(
			"prod-54321-search-tuning-rankings", indexNames.get(1));
	}

	private void _setUpIndexInformation(String companyIndexName) {
		Mockito.doReturn(
			companyIndexName
		).when(
			_indexInformation
		).getCompanyIndexName(
			Mockito.anyLong()
		);
	}

	private final IndexInformation _indexInformation = Mockito.mock(
		IndexInformation.class);
	private IndexSXPSearchRequestBodyContributor
		_indexSXPSearchRequestBodyContributor;
	private SearchRequestBuilder _searchRequestBuilder;

}