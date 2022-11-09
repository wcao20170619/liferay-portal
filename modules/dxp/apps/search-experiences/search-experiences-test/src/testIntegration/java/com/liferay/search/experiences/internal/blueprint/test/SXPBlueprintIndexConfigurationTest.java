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

package com.liferay.search.experiences.internal.blueprint.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.index.IndexInformation;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexName;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.blueprint.search.request.enhancer.SXPBlueprintSearchRequestEnhancer;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.service.SXPBlueprintLocalService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.apache.logging.log4j.core.util.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class SXPBlueprintIndexConfigurationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());

		_themeDisplay = new ThemeDisplay();

		_themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_themeDisplay.setLayout(layout);
		_themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());

		_user = TestPropsValues.getUser();
	}

	@After
	public void tearDown() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter(
			"rowIds", _getSynonymSetDocumentIds());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID,
			"com_liferay_portal_search_tuning_synonyms_web_internal_portlet_" +
				"SynonymsPortlet");
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		ReflectionTestUtil.invoke(
			_mvcActionCommandDeleteSynonymSets, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());
	}

	@Test
	public void testSXPBlueprintIndexSearch() throws Exception {
		SXPBlueprint sxpBlueprint = _addSXPBlueprint(_user.getUserId(), "");

		String title1 = RandomTestUtil.randomString();
		String title2 = RandomTestUtil.randomString();

		JournalTestUtil.addArticle(_group.getGroupId(), title1, "");
		JournalTestUtil.addArticle(_group.getGroupId(), title2, "");

		_assertSearch("[" + title1 + "]", title1, null, sxpBlueprint);

		addSynonymSet(title1 + "," + title2);

		_assertSearch(
			StringBundler.concat("[", title1, ", ", title2, "]"), title1, null,
			sxpBlueprint);

		sxpBlueprint = _addSXPBlueprint(
			_user.getUserId(), _getSynomySetIndexName());

		_assertSearch(null, title1, null, sxpBlueprint);

		_assertSearch(
			null, title1,
			_indexInformation.getCompanyIndexName(
				TestPropsValues.getCompanyId()),
			sxpBlueprint);
	}

	protected static void addSynonymSet(String synonymSet) throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());
		mockLiferayPortletActionRequest.addParameter("synonymSet", synonymSet);

		ReflectionTestUtil.invoke(
			_mvcActionCommandEditSynonymSets, "updateSynonymSet",
			new Class<?>[] {ActionRequest.class},
			mockLiferayPortletActionRequest);
	}

	private SXPBlueprint _addSXPBlueprint(long userId, String indexName)
		throws Exception {

		SXPBlueprint sxpBlueprint = _sxpBlueprintLocalService.addSXPBlueprint(
			RandomTestUtil.randomString(), userId,
			_createConfigurationJSONObject(false, indexName),
			Collections.singletonMap(LocaleUtil.US, ""), null, "",
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			ServiceContextTestUtil.getServiceContext());

		_sxpBlueprints.add(sxpBlueprint);

		return sxpBlueprint;
	}

	private void _assertSearch(
			String expected, String keywords, String lowLevelOptionIndexName,
			SXPBlueprint sxpBlueprint)
		throws Exception {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).queryString(
				keywords
			).withSearchContext(
				_searchContext -> {
					_searchContext.setAttribute(
						"search.experiences.blueprint.id",
						String.valueOf(sxpBlueprint.getSXPBlueprintId()));
					_searchContext.setAttribute(
						"search.experiences.scope.group.id",
						_group.getGroupId());
					_searchContext.setTimeZone(_user.getTimeZone());
					_searchContext.setUserId(_serviceContext.getUserId());
				}
			);

		SearchResponse searchResponse = null;

		if (lowLevelOptionIndexName != null) {
			searchRequestBuilder = searchRequestBuilder.addIndex(
				lowLevelOptionIndexName);

			searchResponse = _searcher.search(searchRequestBuilder.build());

			Assert.isNonEmpty(searchResponse.getDocumentsStream());

			return;
		}

		searchResponse = _searcher.search(searchRequestBuilder.build());

		if (expected == null) {
			Assert.isEmpty(searchResponse.getDocumentsStream());

			return;
		}

		DocumentsAssert.assertValues(
			searchResponse.getRequestString(),
			searchResponse.getDocumentsStream(), "title_en_US", expected);
	}

	private String _createConfigurationJSONObject(
		boolean external, String indexName) {

		return JSONUtil.put(
			"generalConfiguration",
			JSONUtil.put(
				"searchableAssetTypes",
				JSONUtil.put("com.liferay.journal.model.JournalArticle"))
		).put(
			"indexConfiguration",
			JSONUtil.put(
				"external", external
			).put(
				"indexName", indexName
			)
		).put(
			"queryConfiguration", JSONUtil.put("applyIndexerClauses", true)
		).toString();
	}

	private String _getSynomySetIndexName() throws Exception {
		String companyIndexName = _indexInformation.getCompanyIndexName(
			TestPropsValues.getCompanyId());

		SynonymSetIndexName synonymSetIndexName =
			_synonymSetIndexNameBuilder.getSynonymSetIndexName(
				TestPropsValues.getCompanyId());

		String fullIndexName = synonymSetIndexName.getIndexName();

		return fullIndexName.substring(companyIndexName.length() + 1);
	}

	private String[] _getSynonymSetDocumentIds() throws Exception {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setFetchSource(true);

		SynonymSetIndexName synonymSetIndexName =
			_synonymSetIndexNameBuilder.getSynonymSetIndexName(
				TestPropsValues.getCompanyId());

		searchSearchRequest.setIndexNames(synonymSetIndexName.getIndexName());

		searchSearchRequest.setPreferLocalCluster(false);
		searchSearchRequest.setQuery(_queries.matchAll());
		searchSearchRequest.setSize(20);
		searchSearchRequest.setSorts(
			Arrays.asList(_sorts.field("synonyms.keyword", SortOrder.ASC)));
		searchSearchRequest.setStart(0);

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		List<SearchHit> searchHitList = searchHits.getSearchHits();

		List<String> synonymSetDocumentIds = new ArrayList<>(
			searchHitList.size());

		for (SearchHit searchHit : searchHitList) {
			synonymSetDocumentIds.add(searchHit.getId());
		}

		return synonymSetDocumentIds.toArray(new String[0]);
	}

	@Inject(
		filter = "mvc.command.name=/synonyms/delete_synonym_sets",
		type = MVCActionCommand.class
	)
	private static MVCActionCommand _mvcActionCommandDeleteSynonymSets;

	@Inject(
		filter = "mvc.command.name=/synonyms/edit_synonym_sets",
		type = MVCActionCommand.class
	)
	private static MVCActionCommand _mvcActionCommandEditSynonymSets;

	@Inject
	private static SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private IndexInformation _indexInformation;

	@Inject
	private Queries _queries;

	@Inject
	private SearchEngineAdapter _searchEngineAdapter;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	private ServiceContext _serviceContext;

	@Inject
	private Sorts _sorts;

	@Inject
	private SXPBlueprintLocalService _sxpBlueprintLocalService;

	@DeleteAfterTestRun
	private List<SXPBlueprint> _sxpBlueprints = new ArrayList<>();

	@Inject
	private SXPBlueprintSearchRequestEnhancer
		_sxpBlueprintSearchRequestEnhancer;

	private ThemeDisplay _themeDisplay;
	private User _user;

}