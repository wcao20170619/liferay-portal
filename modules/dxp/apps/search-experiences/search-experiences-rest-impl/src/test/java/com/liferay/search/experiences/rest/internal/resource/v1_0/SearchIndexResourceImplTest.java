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

package com.liferay.search.experiences.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.index.IndexInformation;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.search.experiences.rest.dto.v1_0.SearchIndex;

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
public class SearchIndexResourceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_searchIndexResourceImpl = new SearchIndexResourceImpl();

		_searchIndexResourceImpl.setContextCompany(_contextCompany);

		ReflectionTestUtil.setFieldValue(
			_searchIndexResourceImpl, "_indexInformation", _indexInformation);
	}

	@Test
	public void testGetSearchIndexes() {
		_setUpContextCompany(12345);
		_setUpIndexInformation(
			"prod-12345",
			new String[] {
				"liferay-12345-search-tuning-xyz", "prod-0", "prod-12345",
				"prod-12345-search-tuning-rankings",
				"prod-12345-search-tuning-synonyms",
				"prod-54321-search-tuning-xyz"
			});

		List<SearchIndex> searchIndexes =
			_searchIndexResourceImpl.getSearchIndexes();

		Assert.assertEquals(searchIndexes.toString(), 2, searchIndexes.size());

		SearchIndex searchIndex = searchIndexes.get(0);

		Assert.assertEquals(
			"prod-12345-search-tuning-rankings", searchIndex.getFullName());
		Assert.assertEquals(
			"12345-search-tuning-rankings", searchIndex.getName());

		searchIndex = searchIndexes.get(1);

		Assert.assertEquals(
			"prod-12345-search-tuning-synonyms", searchIndex.getFullName());
		Assert.assertEquals(
			"12345-search-tuning-synonyms", searchIndex.getName());
	}

	private void _setUpContextCompany(long companyId) {
		Mockito.doReturn(
			companyId
		).when(
			_contextCompany
		).getCompanyId();
	}

	private void _setUpIndexInformation(
		String companyIndexName, String[] indexNames) {

		Mockito.doReturn(
			companyIndexName
		).when(
			_indexInformation
		).getCompanyIndexName(
			Mockito.anyLong()
		);

		Mockito.doReturn(
			indexNames
		).when(
			_indexInformation
		).getIndexNames();
	}

	private final Company _contextCompany = Mockito.mock(Company.class);
	private final IndexInformation _indexInformation = Mockito.mock(
		IndexInformation.class);
	private SearchIndexResourceImpl _searchIndexResourceImpl;

}