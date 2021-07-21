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
package com.liferay.search.experiences.blueprints.search.spi.model.index.contributor.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.blueprints.model.Blueprint;
import com.liferay.search.experiences.blueprints.service.BlueprintService;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class BlueprintModelDocumentContributorTest {
	
	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);
	
	@Before
	public void setUp() throws Exception {
		Assert.assertEquals(
			_MODEL_INDEXER_CLASS.getName(), indexer.getClassName());
		
		_group = GroupTestUtil.addGroup();
		
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());
		_serviceContext.setAddGroupPermissions(true);
		_serviceContext.setAddGuestPermissions(true);
		
		_blueprints = new ArrayList<Blueprint>();
	}
	
	@Test
	public void testContributor() throws Exception {
		
		//String fieldName = "localized_title_en_US";
		String fieldName = "title_en_US";
		
		String searchTerm = RandomTestUtil.randomString();
		//String searchTerm = "blablabla";

		_blueprints.add(_blueprintService.addCompanyBlueprint(
				Collections.singletonMap(
				LocaleUtil.US, searchTerm), 
				Collections.singletonMap(LocaleUtil.US, ""),
				JSONUtil.put(null, null).toString(), "",
				_serviceContext));
		
		SearchSearchResponse searchSearchResponse = 
			_search(_queries.match(fieldName, searchTerm));
		
		 SearchHits searchHits = searchSearchResponse.getSearchHits();

         List<SearchHit> searchHitsList = searchHits.getSearchHits();

         Hits hits = searchSearchResponse.getHits();

         List<String> expectedValues = Arrays.asList(searchTerm);
         
         DocumentsAssert.assertValuesIgnoreRelevance(
                 "Retrieved hits ->", hits.getDocs(), fieldName,
                 expectedValues);

         Assert.assertEquals(
                 "Retrieved hits", expectedValues.size(),
                 searchHitsList.size());

         Assert.assertEquals(
                 "Total hits", expectedValues.size(),
                 searchHits.getTotalHits());
	}
		
	private SearchSearchResponse _search(Query query) {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		String indexName = _indexNameBuilder.getIndexName(_group.getCompanyId());
		
		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setIndexNames(indexName);
		searchSearchRequest.setPreferLocalCluster(false);
		searchSearchRequest.setQuery(query);
		searchSearchRequest.setSize(100);
		searchSearchRequest.setStart(0);

		return _searchEngineAdapter.execute(searchSearchRequest);
	}
	
	@Inject(filter = "indexer.class.name=com.liferay.search.experiences.blueprints.model.Blueprint")
	protected Indexer<Blueprint> indexer;

	private static final Class<?> _MODEL_INDEXER_CLASS = Blueprint.class;
	
	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;
	
	@Inject
	private BlueprintService _blueprintService;

	@Inject
	private Queries _queries;
	
	@Inject
	private SearchEngineAdapter _searchEngineAdapter;
	
	@Inject
	private IndexNameBuilder _indexNameBuilder;
	
	@DeleteAfterTestRun
	private List<Blueprint> _blueprints;
}
