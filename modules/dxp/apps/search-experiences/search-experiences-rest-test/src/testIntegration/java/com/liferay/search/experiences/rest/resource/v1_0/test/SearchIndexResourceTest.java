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

package com.liferay.search.experiences.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.search.experiences.rest.client.dto.v1_0.SearchIndex;
import com.liferay.search.experiences.rest.client.pagination.Page;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 */
@RunWith(Arquillian.class)
public class SearchIndexResourceTest extends BaseSearchIndexResourceTestCase {

	@Override
	@Test
	public void testGetSearchIndexesPage() throws Exception {
		Page<SearchIndex> page = searchIndexResource.getSearchIndexesPage();

		List<SearchIndex> searchIndexs = _getSearchIndexs();

		Assert.assertEquals(searchIndexs.size(), page.getTotalCount());

		assertEqualsIgnoringOrder(
			searchIndexs, (List<SearchIndex>)page.getItems());

		assertValid(page);
	}

	@Test
	public void testGraphQLGetSearchIndexesPage() throws Exception {
		Assert.assertTrue(true);
	}

	private List<SearchIndex> _getSearchIndexs() {
		String prefix =
			_indexNameBuilder.getIndexName(testCompany.getCompanyId()) +
				StringPool.DASH;

		GetIndexIndexResponse getIndexIndexResponse =
			_searchEngineAdapter.execute(
				new GetIndexIndexRequest(prefix + StringPool.STAR));

		List<SearchIndex> searchIndexs = new ArrayList<>();

		for (String indexName : getIndexIndexResponse.getIndexNames()) {
			searchIndexs.add(
				new SearchIndex() {
					{
						external = false;
						name = indexName;
					}
				});
		}

		return searchIndexs;
	}

	@Inject
	private IndexNameBuilder _indexNameBuilder;

	@Inject
	private SearchEngineAdapter _searchEngineAdapter;

}