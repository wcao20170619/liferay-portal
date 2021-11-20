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
package com.liferay.search.experiences.internal.blueprint.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.search.experiences.service.SXPBlueprintLocalServiceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class SXPBlueprintSearchResultTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		WorkflowThreadLocal.setEnabled(false);

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());

		_user = TestPropsValues.getUser();
		
		_sxpBlueprint = SXPBlueprintLocalServiceUtil.addSXPBlueprint(
			_user.getUserId(), "{}",
			Collections.singletonMap(LocaleUtil.US, ""), null,
			Collections.singletonMap(
				LocaleUtil.US, getClass().getName() + "-Blueprint"),
			_serviceContext);
	}

	@Test
	public void testConditionContains() throws Exception {
	}

	@Test
	public void testBoostContents() throws Exception {
		AssetCategory assetCategory = _addAssetCategoryGroupJournalArticles(
			new String[] {"coca cola", "pepsi cola"}, new String[] {"cola cola", ""});
		
		_test(
			() -> {
				try {
					_assertSearch("[pepsi cola, coca cola]", "cola");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			},
			"InCategory",
			new String[] {"${configuration.asset_category_ids}"}, 
			new String[] {String.valueOf(assetCategory.getCategoryId())});
		_test(
			() -> {
				try {
					_user = UserTestUtil.addUser(_groups.get(1).getGroupId());

					_serviceContext.setUserId(_user.getUserId());

					_assertSearch("[pepsi cola, coca cola]", "cola");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			},
			"OnMySites", null, null);
	}

	@Test
	public void testBoostFreshness() throws Exception {
	}

	@Test
	public void testPhraseMatch() throws Exception {

		// Set up

//		Integer x = 5;
//
//		_test(
//			() -> {
//				Assert.assertEquals(new Integer(5), x);
//			},
//			"withKeyword");
//		_test(
//			() -> {
//				Assert.assertTrue(true);
//			},
//			"withMultiMatch");
	}

	private void _test(
		Runnable runnable, String resourceName, 
		String[] configurationNames, String[] configurationValues) throws Exception {

		// Add SXP blueprint

		Class<?> clazz = getClass();

		Thread currentThread = Thread.currentThread();

		StackTraceElement[] stackTraceElements = currentThread.getStackTrace();

		String json = StringUtil.read(
			clazz.getResourceAsStream(
				StringBundler.concat(
					"dependencies/", clazz.getSimpleName(), StringPool.PERIOD,
					stackTraceElements[2].getMethodName(), "_", resourceName,
					".json")));

		if (configurationNames != null) {
			for (int i = 0; i < configurationNames.length; i++) {
				json = StringUtil.replace(
					json, configurationNames[i], configurationValues[i]);
			}
		}
		SXPBlueprintLocalServiceUtil.updateSXPBlueprint(
			_sxpBlueprint.getUserId(), _sxpBlueprint.getSXPBlueprintId(),
			json, _sxpBlueprint.getDescriptionMap(),
			_sxpBlueprint.getElementInstancesJSON(),
			_sxpBlueprint.getTitleMap(), _serviceContext);
		runnable.run();

		// Delete SXP blueprint

	}
	
	private SearchResponse _getSearchResponse(String keywords)
		throws Exception {

		SearchRequest searchRequest = _searchRequestBuilderFactory.builder(
		).companyId(
			TestPropsValues.getCompanyId()
		).queryString(
			keywords
		).withSearchContext(
			_searchContext -> {
				_searchContext.setAttribute(
					"search.experiences.blueprint.id",
					_sxpBlueprint.getSXPBlueprintId());
				_searchContext.setTimeZone(_user.getTimeZone());
				_searchContext.setUserId(_user.getUserId());
				}
		).build();

		return _searcher.search(searchRequest);
	}
	
	private void _assertSearch(String expected, String keywords)
		throws Exception {

		SearchResponse searchResponse = _getSearchResponse(keywords);

		DocumentsAssert.assertValues(
			searchResponse.getRequestString(),
			searchResponse.getDocumentsStream(), "localized_title_en_US", expected);
	}

	private void _assertSearchIgnoreRelevance(String expected, String keywords)
		throws Exception {

		SearchResponse searchResponse = _getSearchResponse(keywords);

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse.getRequestString(),
			searchResponse.getDocumentsStream(), "localized_title_en_US", expected);
	}
	
	private AssetCategory _addAssetCategoryGroupJournalArticles(String[] title, String[] content) throws Exception {
		Group groupA = GroupTestUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, "SiteA", _serviceContext);
		Group groupB = GroupTestUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, "SiteB", _serviceContext);

		 JournalTestUtil.addArticle(
			groupA.getGroupId(), 0, PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.US, title[0]
			).build(),
			null,
			HashMapBuilder.put(
				LocaleUtil.US, content[0]
			).build(),
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);
		 
		 AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.addDefaultVocabulary(
				_group.getGroupId());
		 
		 AssetCategory assetCategory = AssetCategoryLocalServiceUtil.addCategory(
			_user.getUserId(), _group.getGroupId(), "Important",
			assetVocabulary.getVocabularyId(), _serviceContext);
		 
		_serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		 JournalTestUtil.addArticle(
			groupB.getGroupId(), 0, PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.US, title[1]
			).build(),
			null,
			HashMapBuilder.put(
				LocaleUtil.US, content[1]
			).build(),
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);
		 
		_groups.add(groupA);
		_groups.add(groupB);
		
		return assetCategory;
	}
	
	
	
	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;
	private User _user;
	
	@DeleteAfterTestRun
	private SXPBlueprint _sxpBlueprint;
	
	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;
	
	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();


}
