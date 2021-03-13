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

package com.liferay.portal.search.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 * 
 * From test results, once moving roleId array to top/bottom
 * in the boolean prefilter from associated within entryClassName,
 * We get one more result of document(the modelIndexerClassNames 
 * has 3 different class model indexers for the testing) which is 
 * not expected document returned. The resource permission looks 
 * like not be impacted by moving the roleId array location.
 * and all scores are identical except an extra document sneaked in. 
 * 
 * After moving the rolId to top or bottom. The json for pre-filter
 * looks like the following:
 * {
 *	"bool":{
 *	 "must":[
 *		 ......
 *		 {
 *          "term":{
 *              "entryClassName":{
 *                  "value":"com.liferay.knowledge.base.model.KBArticle"
 *               }
 *          }
 *       },                                 
 *       {
 *          "bool":{
 *              "should":[
 *                  {
 *                     "terms":{
 *                         "roleId":[
 *                              "20110"
 *                          ]
 *                      }
 *                  }
 *               ]
 *           }
 *       }
 *     ]
 *   }
 * }
 * 
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class RoleIdEntryClassNameRelationshipTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_groups.add(_group);
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());

		//adding the asset entities
		addAssetEntries();
	}
	
	// The following test has a query without any role id in the prefilter
	@Test
	public void testEntryClassNameWithoutRoleId() throws Exception {
		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).emptySearchEnabled(
				true
			).excludeContributors(
				"com.liferay.portal.search.tuning.blueprints"
			).locale(
				LocaleUtil.US
			).modelIndexerClassNames(
				JournalArticle.class.getName(), DLFileEntry.class.getName(),
				User.class.getName()
			).queryString(
				"beta"
			);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		//expected 3 results returned (no-indexable/no-viewable excluded)
		_assertSearch(
			Arrays.asList("alpha beta", "beta charlie", "delta beta"),
			"localized_title_en_US", searchResponse);

		float maxScore1 = _getMaxScore(searchResponse);
		Float[] scoreArray1 = _getScoreArray(searchResponse);

		//try to take roleId out from associated with each entryClassName
		//because of no roleId within the entryClassName, no rolId added to
		//the top/bottom, just repeating the request and checkout score values
		searchRequestBuilder = searchRequestBuilder.withSearchContext(
			searchContext -> searchContext.setAttribute(
				"testMoveupRolesTermsFilter", true));

		searchResponse = _searcher.search(searchRequestBuilder.build());

		//The result is matched with the previous one
		_assertSearch(
			Arrays.asList("alpha beta", "beta charlie", "delta beta"),
			"localized_title_en_US", searchResponse);

		float maxScore2 = _getMaxScore(searchResponse);
		Float[] scoreArray2 = _getScoreArray(searchResponse);

		//All the score values are identical with the previous query
		Assert.assertEquals(maxScore1, maxScore2, 0.1);
		Assert.assertArrayEquals(scoreArray1, scoreArray2);
	}

	/* When using default user as a user to query, only one role in the array
	 * embedded in prefilter.
	 */
	@Test
	public void testRoleIdWidthinEntryClassNameByDefaultUser()
		throws Exception {

		//get default userId from db
		long userId = UserLocalServiceUtil.getDefaultUserId(
			_group.getCompanyId());

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).emptySearchEnabled(
				true
			).excludeContributors(
				"com.liferay.portal.search.tuning.blueprints"
			).modelIndexerClassNames(
				JournalArticle.class.getName(), DLFileEntry.class.getName(),
				User.class.getName()
			).queryString(
				"beta"
			).withSearchContext(
				searchContext -> searchContext.setUserId(userId)
			);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		//expected 3 results returned (no-indexable/no-viewable excluded)
		_assertSearch(
			Arrays.asList("alpha beta", "beta charlie", "delta beta"),
			"localized_title_en_US", searchResponse);

		float maxScore1 = _getMaxScore(searchResponse);
		Float[] scoreArray1 = _getScoreArray(searchResponse);
		Document[] documents1 = _getDocs(searchResponse);

		//move the rolId to the bottom of the prefilter and along with
		//all the entryClassNames
		searchRequestBuilder = searchRequestBuilder.withSearchContext(
			searchContext -> searchContext.setAttribute(
				"testMoveupRolesTermsFilter", true));

		searchResponse = _searcher.search(searchRequestBuilder.build());

		float maxScore2 = _getMaxScore(searchResponse);

		Document[] documents2 = _getDocs(searchResponse);

		//****The result is NOT matched with one more document****//
		//****which is also not one of no-indexable/no-viewable ****//
		Assert.assertNotEquals(documents1.length, documents2.length);

		//The score values are matched with the previous query on 
		//corresponding document except the extra one
		Assert.assertEquals(maxScore1, maxScore2, 0.1);

		_assertScoreValues(searchResponse, scoreArray1);
	}

	//when use guest user as a user to query,  multiple roles in the array
	//embedded in prefilter
	//it is equivalent to keyword search before login
	@Test
	public void testRoleIdWidthinEntryClassNameByGuestUser() throws Exception {
		Group guestGroup = GroupLocalServiceUtil.getGroup(
			_group.getCompanyId(), GroupConstants.GUEST);

		User guestUser = UserTestUtil.addGroupUser(guestGroup, "Guest");

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				guestGroup.getCompanyId()
			).emptySearchEnabled(
				true
			).excludeContributors(
				"com.liferay.portal.search.tuning.blueprints"
			).modelIndexerClassNames(
				JournalArticle.class.getName(), DLFileEntry.class.getName(),
				User.class.getName()
			).queryString(
				"beta"
			).withSearchContext(
				searchContext -> searchContext.setUserId(guestUser.getUserId())
			);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		//expected 3 results returned (no-indexable/no-viewable excluded)
		_assertSearch(
			Arrays.asList("alpha beta", "beta charlie", "delta beta"),
			"localized_title_en_US", searchResponse);

		float maxScore1 = _getMaxScore(searchResponse);
		Float[] scoreArray1 = _getScoreArray(searchResponse);
		Document[] documents1 = _getDocs(searchResponse);

		//move the rolId to the bottom of the prefilter and along with
		//all the entryClassNames. there are 3 roleIds in the array
		searchRequestBuilder = searchRequestBuilder.withSearchContext(
			searchContext -> searchContext.setAttribute(
				"testMoveupRolesTermsFilter", true));

		searchResponse = _searcher.search(searchRequestBuilder.build());

		float maxScore2 = _getMaxScore(searchResponse);

		Document[] documents2 = _getDocs(searchResponse);

		//****The result is NOT matched with one more document****//
		//****which is also not one of no-indexable/no-viewable ****//
		Assert.assertNotEquals(documents1.length, documents2.length);

		//The score values are matched with the previous query on 
		//corresponding document except the extra one
		Assert.assertEquals(maxScore1, maxScore2, 0.1);

		_assertScoreValues(searchResponse, scoreArray1);
	}

	/* The following function is adding 5 Asset Entities within 
	 * 2 different groups and one entity is not Indexable,
	 * another entity is not viewable. With 5 entities, one of 
	 * them is FileEntry type and rest of them are JournalArticle
	 * type. 
	 */
	protected void addAssetEntries() throws Exception {
		Group aGroup = GroupTestUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, "aSite", _serviceContext);

		_groups.add(aGroup);

		User aUser = UserTestUtil.addGroupUser(aGroup, "Site Member");

		Group userPersonalGroup = GroupLocalServiceUtil.getGroup(
			_group.getCompanyId(), GroupConstants.USER_PERSONAL_SITE);

		Group aUserPersonalGroup = GroupTestUtil.addGroup(
			userPersonalGroup.getGroupId());

		_groups.add(aUserPersonalGroup);

		_addDLFileEntry(aGroup, "delta beta", aUser);

		_addJournalArticle(aGroup, "alpha beta");
		_addJournalArticle(aUserPersonalGroup, "beta charlie");

		JournalArticle journalArticle = _addJournalArticle(
			aUserPersonalGroup, "edison beta");

		journalArticle.setIndexable(false);

		JournalTestUtil.updateArticle(journalArticle);
		
		journalArticle = _addJournalArticle(aGroup, "foxtrot beta"); 
		
		journalArticle.setStatus(0);
		
		JournalTestUtil.updateArticle(journalArticle);
	}

	private FileEntry _addDLFileEntry(Group group, String title, User user)
		throws Exception {

		return DLAppTestUtil.addFileEntryWithWorkflow(
			user.getUserId(), group.getGroupId(), 0, StringPool.BLANK, title,
			true, _serviceContext);
	}

	private JournalArticle _addJournalArticle(Group group, String title)
		throws Exception {

		return JournalTestUtil.addArticle(
			group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.US, title
			).build(),
			null,
			HashMapBuilder.put(
				LocaleUtil.US, ""
			).build(),
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);
	}

	private void _assertScoreValues(
		SearchResponse searchResponse, Float[] scoreArray1) {

		float score = _getDocsScore("alpha beta", searchResponse);

		Assert.assertEquals(scoreArray1[0], score, 0.1);

		score = _getDocsScore("beta charlie", searchResponse);

		Assert.assertEquals(scoreArray1[1], score, 0.1);

		score = _getDocsScore("delta beta", searchResponse);

		Assert.assertEquals(scoreArray1[2], score, 0.1);
	}

	private void _assertSearch(
		List<String> expected, String fieldName,
		SearchResponse searchResponse) {

		Hits hits = searchResponse.withHitsGet(Function.identity());

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse.getRequestString(), hits.getDocs(), fieldName,
			expected);
	}

	private Document[] _getDocs(SearchResponse searchResponse) {
		Hits hits = searchResponse.withHitsGet(Function.identity());

		return hits.getDocs();
	}

	private float _getDocsScore(
		String fieldValue, SearchResponse searchResponse) {

		Document[] documents = _getDocs(searchResponse);
		Float[] scoreArray = _getScoreArray(searchResponse);

		float score = -1F;

		for (int i = 0; i < documents.length; i++) {
			Document document = documents[i];

			Field field = document.getField("localized_title_en_US");

			if (StringUtil.equals(fieldValue, field.getValue())) {
				score = scoreArray[i];

				break;
			}
		}

		return score;
	}

	private float _getMaxScore(SearchResponse searchResponse) {
		SearchHits searchHits = searchResponse.getSearchHits();

		return searchHits.getMaxScore();
	}

	private Float[] _getScoreArray(SearchResponse searchResponse) {
		SearchHits searchHits = searchResponse.getSearchHits();

		List<SearchHit> searchHitList = searchHits.getSearchHits();

		return searchHitList.stream(
		).map(
			searchHit -> searchHit.getScore()
		).collect(
			Collectors.toList()
		).toArray(
			new Float[0]
		);
	}

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups = new ArrayList<>();

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	private ServiceContext _serviceContext;

}