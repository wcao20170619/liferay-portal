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

package com.liferay.asset.categories.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalArticleBlueprint;
import com.liferay.journal.test.util.JournalArticleContent;
import com.liferay.journal.test.util.JournalArticleTitle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.facet.category.CategoryFacetFactory;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
@Sync
public class CategoryMultiLanguageSearchTest
	extends BaseCategorySearcherTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_assetCategoryList = new ArrayList<>();
		_assetVocabularyList = new ArrayList<>();
		_updateJournalArticleList = new ArrayList<>();

		_group = userSearchFixture.addGroup();

		_user = UserTestUtil.addUser(_group.getGroupId());
	}

	@Test
	public void testChineseCategories() throws Exception {
		String category_title = "你好";
		String web_content_title = "whatever";

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.addDefaultVocabulary(
				_group.getGroupId());

		_assetVocabularyList.add(assetVocabulary);
		AssetCategory assetCategory = addCategory(
			assetVocabulary, category_title, LocaleUtil.CHINA.getLanguage());

		_assetCategoryList.add(assetCategory);
		long categoryId = assetCategory.getCategoryId();

		JournalArticle journalArticle = addJournalArticle(
			_group, web_content_title, web_content_title, LocaleUtil.CHINA,
			LocaleUtil.CHINA);
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId());

		serviceContext.setAssetCategoryIds(new long[] {categoryId});

		JournalArticle updateArticle = updateJournalArticle(
			journalArticle, serviceContext);

		_updateJournalArticleList.add(updateArticle);

		SearchContext searchContext = getSearchContext(category_title);

		searchContext.setCategoryIds(new long[] {categoryId});
		searchContext.setGroupIds(new long[] {_group.getGroupId()});
		searchContext.setLocale(LocaleUtil.CHINA);

		Facet facet = categoryFacetFactory.newInstance(searchContext);

		searchContext.addFacet(facet);

		Hits hits = search(searchContext);

		Assert.assertTrue(
			searchContext.getKeywords() + "->" + hits.getDocs().toString(),
			hits.getDocs().length == 0);
	}

	@Test
	public void testEnglishCategories() throws Exception {
		String category_title = "testEngine";
		String web_content_title = "testContent";

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.addDefaultVocabulary(
				_group.getGroupId());

		_assetVocabularyList.add(assetVocabulary);
		AssetCategory assetCategory = addCategory(
			assetVocabulary, category_title, LocaleUtil.US.getLanguage());

		_assetCategoryList.add(assetCategory);
		long categoryId = assetCategory.getCategoryId();

		JournalArticle journalArticle = addJournalArticle(
			_group, web_content_title, web_content_title, LocaleUtil.US,
			LocaleUtil.US);
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId());

		serviceContext.setAssetCategoryIds(new long[] {categoryId});

		JournalArticle updateArticle = updateJournalArticle(
			journalArticle, serviceContext);

		_updateJournalArticleList.add(updateArticle);

		SearchContext searchContext = getSearchContext(category_title);

		searchContext.setCategoryIds(new long[] {categoryId});
		searchContext.setGroupIds(new long[] {_group.getGroupId()});
		searchContext.setLocale(LocaleUtil.US);

		Facet facet = categoryFacetFactory.newInstance(searchContext);

		searchContext.addFacet(facet);

		Hits hits = search(searchContext);

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchContext.getKeywords(), hits.getDocs(), facet.getFieldName(),
			Arrays.asList(String.valueOf(categoryId)));
	}

	@Test
	public void testJapaneseCategories() throws Exception {
		String vocabulary_title = "ボキャブラリ";
		String category1_title = "東京";
		String category2_title = "京都";
		String web_content1_title_summary = "豊島区";
		String web_content2_title_summary = "下京区";

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.addDefaultVocabulary(
				_group.getGroupId());

		assetVocabulary.setTitle(vocabulary_title);

		assetVocabularyLocalService.updateAssetVocabulary(assetVocabulary);
		_assetVocabularyList.add(assetVocabulary);

		AssetCategory assetCategory_1 = addCategory(
			assetVocabulary, category1_title, LocaleUtil.JAPAN.getLanguage());

		_assetCategoryList.add(assetCategory_1);
		long categoryId_1 = assetCategory_1.getCategoryId();

		AssetCategory assetCategory_2 = addCategory(
			assetVocabulary, category2_title, LocaleUtil.JAPAN.getLanguage());
		_assetCategoryList.add(assetCategory_1);

		long categoryId_2 = assetCategory_2.getCategoryId();

		long[] categoryIds = {categoryId_1, categoryId_2};

		JournalArticle journalArticle_1 = addJournalArticle(
			_group, web_content1_title_summary, web_content1_title_summary,
			LocaleUtil.JAPAN, LocaleUtil.JAPAN);
		ServiceContext serviceContext_1 =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId());

		serviceContext_1.setAssetCategoryIds(new long[] {categoryId_1});

		JournalArticle updateArticle_1 = updateJournalArticle(
			journalArticle_1, serviceContext_1);

		_updateJournalArticleList.add(updateArticle_1);

		JournalArticle journalArticle_2 = addJournalArticle(
			_group, web_content2_title_summary, web_content2_title_summary,
			LocaleUtil.JAPAN, LocaleUtil.JAPAN);
		ServiceContext serviceContext_2 =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId());

		serviceContext_2.setAssetCategoryIds(new long[] {categoryId_2});

		JournalArticle updateArticle_2 = updateJournalArticle(
			journalArticle_2, serviceContext_2);

		_updateJournalArticleList.add(updateArticle_2);

		SearchContext searchContext = getSearchContext(category1_title);

		searchContext.setCategoryIds(categoryIds);
		searchContext.setGroupIds(new long[] {_group.getGroupId()});
		searchContext.setLocale(LocaleUtil.JAPAN);

		Facet facet = categoryFacetFactory.newInstance(searchContext);

		searchContext.addFacet(facet);

		Hits hits = search(searchContext);

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchContext.getKeywords(), hits.getDocs(), facet.getFieldName(),
			Arrays.asList(String.valueOf(categoryId_2)));
	}

	protected AssetCategory addCategory(
			AssetVocabulary assetVocabulary, String title, String languageId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId());

		serviceContext.setLanguageId(languageId);

		AssetCategory assetCategory = assetCategoryLocalService.addCategory(
			_user.getUserId(), _group.getGroupId(), title,
			assetVocabulary.getVocabularyId(), serviceContext);

		return assetCategory;
	}

	protected JournalArticle addJournalArticle(
			Group group, String title, String content, Locale defLocale,
			Locale targetLocale)
		throws Exception {

		return journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					groupId = group.getGroupId();
					journalArticleContent = new JournalArticleContent() {
						{
							defaultLocale = defLocale;
							name = "content";
							put(targetLocale, content);
						}
					};
					journalArticleTitle = new JournalArticleTitle() {
						{
							put(targetLocale, title);
						}
					};
				}
			});
	}

	@Inject
	protected AssetCategoryLocalService assetCategoryLocalService;

	@Inject
	protected AssetVocabularyLocalService assetVocabularyLocalService;

	@Inject
	protected CategoryFacetFactory categoryFacetFactory;

	@DeleteAfterTestRun
	private List<AssetCategory> _assetCategoryList;

	@DeleteAfterTestRun
	private List<AssetVocabulary> _assetVocabularyList;

	private Group _group;

	@DeleteAfterTestRun
	private List<JournalArticle> _updateJournalArticleList;

	@DeleteAfterTestRun
	private User _user;

}