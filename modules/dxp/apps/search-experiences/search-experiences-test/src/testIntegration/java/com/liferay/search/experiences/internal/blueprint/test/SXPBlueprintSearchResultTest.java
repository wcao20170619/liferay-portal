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
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.expando.util.test.ExpandoTestUtil;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.service.SXPBlueprintLocalServiceUtil;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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

	@BeforeClass
	public static void setUpClass() throws Exception {
		WorkflowThreadLocal.setEnabled(false);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		WorkflowThreadLocal.setEnabled(true);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());

		_user = TestPropsValues.getUser();

		_sxpBlueprint = SXPBlueprintLocalServiceUtil.addSXPBlueprint(
			_user.getUserId(), "{}",
			Collections.singletonMap(LocaleUtil.US, ""), null,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			_serviceContext);
	}

	@Test
	public void testBoostContents() throws Exception {
		_setUp(
			new String[] {"cola cola", ""},
			new String[] {"coca cola", "pepsi cola"},
			() -> {
				_addAssetCatetory("Important", _user);
				_addGroupAAndGroupB();
			});

		_test(
			() -> _assertSearch("[pepsi cola, coca cola]", "cola"),
			"inCategory", new String[] {"${configuration.asset_category_ids}"},
			new String[] {String.valueOf(_assetCategory.getCategoryId())});
		_test(
			() -> _assertSearchIgnoreRelevance(
				"[coca cola, pepsi cola]", "cola"),
			null, null, null);

		_user = UserTestUtil.addUser(_groupB.getGroupId());

		_serviceContext.setUserId(_user.getUserId());

		_test(
			() -> _assertSearch("[pepsi cola, coca cola]", "cola"), "onMySites",
			null, null);
	}

	@Test
	public void testBoostFreshness() throws Exception {
		_setUp(
			new String[] {"cola cola", ""},
			new String[] {"coca cola", "pepsi cola"},
			() -> _addJournalArticleSleep = 3);

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[coca cola, pepsi cola]", "cola"),
			null, null, null);
		_test(
			() -> _assertSearch("[pepsi cola, coca cola]", "cola"),
			"withFunctionScore", new String[] {"${time.current_date}"},
			new String[] {
				DateUtil.getCurrentDate("yyyyMMddHHmmss", LocaleUtil.US)
			});
	}

	@Test
	public void testBoostProximity() throws Exception {
		_setUp(
			new String[] {"location", "location"},
			new String[] {"Branch SF", "Branch LA"},
			new double[] {64.01, 24.03}, new double[] {-117.42, -107.44},
			() -> _addExpandoColumn(_group.getCompanyId(), "location"));

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[branch la, branch sf]", "branch"),
			null, null, null);
		_test(
			() -> {
				try (ConfigurationTemporarySwapper
						configurationTemporarySwapper =
							_getConfigurationTemporarySwapper(
								"2345", "true", "64.225.32.7")) {

					_assertSearch("[branch sf, branch la]", "branch");
				}
				catch (Exception exception) {
					Assert.fail();
				}
			},
			"withFunctionScore",
			new String[] {"${configuration.lat}", "${configuration.lon}"},
			new String[] {"64.01", "-117.42"});
		_test(
			() -> {
				try (ConfigurationTemporarySwapper
						configurationTemporarySwapper =
							_getConfigurationTemporarySwapper(
								"2345", "true", "34.94.32.240")) {

					_assertSearch("[branch la, branch sf]", "branch");
				}
				catch (Exception exception) {
					Assert.fail();
				}
			},
			"withFunctionScore",
			new String[] {"${configuration.lat}", "${configuration.lon}"},
			new String[] {"24.03", "-107.44"});
	}

	@Test
	public void testConditionContains() throws Exception {
		_setUp(
			new String[] {"alpha alpha", ""},
			new String[] {"beta alpha", "charlie alpha"},
			() -> {
				_user = _addGroupUser(_group, "employee");

				_addAssetCatetory("Promoted", _user);
			});

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[beta alpha, charlie alpha]", "alpha"),
			null, null, null);
		_test(
			() -> _assertSearch("[charlie alpha, beta alpha]", "alpha"),
			"withAssetCategory",
			new String[] {
				"${configuration.asset_category_id}",
				"${configuration.keywords}"
			},
			new String[] {
				String.valueOf(_assetCategory.getCategoryId()), "alpha"
			});

		JournalArticle journalArticle = _journalArticles.get(1);

		_test(
			() -> _assertSearch("[charlie alpha, beta alpha]", "alpha"),
			"withContains", new String[] {"${articleId}"},
			new String[] {journalArticle.getArticleId()});
		_test(
			() -> _assertSearch("[beta alpha, charlie alpha]", "alpha"),
			"withNotContains", new String[] {"${articleId}"},
			new String[] {journalArticle.getArticleId()});

		SegmentsEntry segmentsEntry = _addSegmentsEntry(_user);

		_test(
			() -> _assertSearch("[charlie alpha, beta alpha]", "alpha"),
			"withSegmentsEntry",
			new String[] {
				"${configuration.asset_category_id}",
				"${configuration.user_segment_ids}"
			},
			new String[] {
				String.valueOf(_assetCategory.getCategoryId()),
				String.valueOf(segmentsEntry.getSegmentsEntryId())
			});
	}

	@Test
	public void testConditionRange() throws Exception {
		_setUp(
			new String[] {"cola cola", ""},
			new String[] {"Coca Cola", "Pepsi Cola"},
			() -> _addAssetCatetory(
				"Promoted", _addGroupUser(_group, "Custmers")));

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[coca cola, pepsi cola]", "cola"),
			null, null, null);
		_test(
			() -> _assertSearch("[pepsi cola, coca cola]", "cola"),
			"withPeriodOfTime",
			new String[] {
				"${configuration.asset_category_id}",
				"${configuration.start_date}", "${configuration.end_date}"
			},
			new String[] {
				String.valueOf(_assetCategory.getCategoryId()),
				DateUtil.getDate(
					new Date(System.currentTimeMillis()), "yyyyMMdd",
					LocaleUtil.US),
				DateUtil.getDate(_getNextDay(), "yyyyMMdd", LocaleUtil.US)
			});

		_setUp(
			new String[] {"policies policies", ""},
			new String[] {
				"Company policies for All Employees Recruits",
				"Company Policies for New Recruits"
			},
			() -> _addAssetCatetory(
				"For New Recruits", _addGroupUser(_group, "Employee")));

		_test(
			() -> _assertSearch(
				"[company policies for new recruits, company policies for " +
					"all employees recruits]",
				"policies"),
			"withNewUserAccount",
			new String[] {"${configuration.asset_category_id}"},
			new String[] {String.valueOf(_assetCategory.getCategoryId())});
	}

	@Test
	public void testHideSearch() throws Exception {
		_setUp(
			new String[] {"", ""}, new String[] {"do not hide me", "hide me"},
			() -> {
				try {
					_assetTag = AssetTestUtil.addTag(
						_group.getGroupId(), "hide");

					_journalFolder = JournalFolderServiceUtil.addFolder(
						_group.getGroupId(), 0, RandomTestUtil.randomString(),
						StringPool.BLANK, _serviceContext);
				}
				catch (Exception exception) {
					_log.error(
						"Add asset tag and/or jounal folder error", exception);
				}
			});

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[do not hide me, hide me]", "hide me"),
			null, null, null);
		_test(
			() -> _assertSearch("[do not hide me]", "hide me"), "taggedContent",
			null, null);
		_test(
			() -> _assertSearch("[do not hide me]", "hide me"),
			"byExactTermMatch", new String[] {"${configuration.value}"},
			new String[] {String.valueOf(_journalFolder.getFolderId())});

		_assetTag = null;
		_journalFolder = null;

		_setUp(
			new String[] {
				"Los Angeles", "Orange County", "Los Angeles", "Los Angeles"
			},
			new String[] {
				"Cafe Rio", "Cloud Cafe", "Denny's", "Starbucks Cafe"
			});

		_test(
			() -> _assertSearchIgnoreRelevance("[cloud cafe]", "cafe"),
			"pasteESQueryMustNot",
			new String[] {"${configuration.query}", "${configuration.occur}"},
			new String[] {"los angeles", "must_not"});
		_test(
			() -> _assertSearchIgnoreRelevance(
				"[cafe rio, starbucks cafe]", "cafe"),
			"pasteESQueryMustNot",
			new String[] {"${configuration.query}", "${configuration.occur}"},
			new String[] {"orange county", "must_not"});
	}

	@Test
	public void testKeywoardMatch() throws Exception {
		_setUp(
			new String[] {"", ""}, new String[] {"coca cola", "pepsi cola"},
			() -> {
				try {
					_assetTag = AssetTagLocalServiceUtil.addTag(
						_user.getUserId(), _group.getGroupId(), "cola",
						_serviceContext);
				}
				catch (Exception exception) {
					_log.error("Add asset tag error", exception);
				}
			});

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[coca cola, pepsi cola]", "cola"),
			null, null, null);
		_test(
			() -> _assertSearch("[pepsi cola, coca cola]", "cola"),
			"withAssetTagName", null, null);
	}

	@Test
	public void testLimitSearch() throws Exception {
		_setUp(
			new String[] {"", "", ""},
			new String[] {"cola coca", "cola pepsi", "cola sprite"},
			() -> _addGroupAAndGroupB());

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[cola coca, cola pepsi, cola sprite]", "cola"),
			null, null, null);

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[cola coca, cola pepsi]", "cola"),
			"withFilterByExactTermMatch",
			new String[] {"${configuration.value1}", "${configuration.value2}"},
			new String[] {
				String.valueOf(_groupA.getGroupId()),
				String.valueOf(_groupB.getGroupId())
			});

		_user = UserTestUtil.addUser(_groupA.getGroupId());

		_serviceContext.setUserId(_user.getUserId());

		_test(
			() -> _assertSearchIgnoreRelevance("[cola coca]", "cola"),
			"withInAGroup", null, null);
		_test(
			() -> _assertSearchIgnoreRelevance(
				"[cola coca, cola pepsi]", "cola"),
			"withTheseSites",
			new String[] {"${configuration.value1}", "${configuration.value2}"},
			new String[] {
				String.valueOf(_groupA.getGroupId()),
				String.valueOf(_groupB.getGroupId())
			});
	}

	@Test
	public void testMatch() throws Exception {
		_setUp(
			new String[] {
				"Los Angeles", "Orange County", "Los Angeles", "Los Angeles"
			},
			new String[] {
				"Cafe Rio", "Cloud Cafe", "Denny's", "Starbucks Cafe"
			});

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[cafe rio, cloud cafe, starbucks cafe]", "cafe"),
			null, null, null);
		_test(
			() -> _assertSearchIgnoreRelevance("[cloud cafe]", "cafe"),
			"fromScratch", new String[] {"${configuration.query}"},
			new String[] {"orange county"});
		_test(
			() -> _assertSearchIgnoreRelevance(
				"[cafe rio, starbucks cafe]", "cafe"),
			"fromScratch", new String[] {"${configuration.query}"},
			new String[] {"los angeles"});
	}

	@Test
	public void testPhraseMatch() throws Exception {
		_setUp(
			new String[] {"coca coca", ""},
			new String[] {
				"This coca looks like a kind of drink",
				"This looks like a kind of coca drink"
			});

		_test(
			() -> _assertSearch(
				"[this looks like a kind of coca drink, this coca looks like " +
					"a kind of drink]",
				"coca drink"),
			"withMultiAllKeywordsMatch", null, null);
		_test(
			() -> _assertSearch(
				"[this coca looks like a kind of drink, this looks like a " +
					"kind of coca drink]",
				"coca drink"),
			"withMultiMatch", null, null);
	}

	@Test
	public void testSearch() throws Exception {
		_setUp(
			new String[] {"Los Angeles", "Orange County"},
			new String[] {"Cafe Rio", "Cloud Cafe"});

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[cafe rio, cloud cafe]", "cafe"),
			null, null, null);
		_test(
			() -> _assertSearch("[cafe rio, cloud cafe]", "cafe"),
			"withDefaultBehavior", null, null);

		_setUp(new String[] {"", ""}, new String[] {"Coca Cola", "Pepsi Cola"});

		_test(
			() -> _assertSearch("[coca cola]", "cola +coca"),
			"withLuceneSyntax", null, null);
		_test(
			() -> _assertSearch("[pepsi cola]", "cola -coca"),
			"withLuceneSyntax", null, null);
	}

	@Test
	public void testTextMatchOverMultipleFields_bestFields() throws Exception {
		_setUp(
			new String[] {
				"carbonated cola", "carbonated cola cola",
				"non-carbonated cola", "carbonated cola cola"
			},
			new String[] {
				"drink carbonated coca", "drink carbonated pepsi cola",
				"fruit punch", "sprite"
			});
		_test(
			() -> _assertSearch(
				"[drink carbonated coca, drink carbonated pepsi cola, " +
					"sprite, fruit punch]",
				"coca cola"),
			"withOperator",
			new String[] {
				"${configuration.fuzziness}", "${configuration.operator}"
			},
			new String[] {"AUTO", "and"});

		_setUp(
			new String[] {"ipsum sit", "ipsum sit sit", "non-lorem ipsum sit"},
			new String[] {"lorem ipsum dolor", "lorem ipsum sit", "nunquis"});
		_test(
			() -> _assertSearch(
				"[lorem ipsum sit, lorem ipsum dolor, nunquis]",
				"ipsum sit sit"),
			"withOperator",
			new String[] {
				"${configuration.fuzziness}", "${configuration.operator}"
			},
			new String[] {"0", "or"});
	}

	@Test
	public void testTextMatchOverMultipleFields_boolPrefix() throws Exception {
		_setUp(
			new String[] {
				"ipsum sit sit", "ipsum sit", "ipsum sit sit",
				"non-lorem ipsum sit"
			},
			new String[] {
				"lorem ipsum sit", "lorem ipsum dolor", "amet", "nunquis"
			});

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[lorem ipsum dolor, lorem ipsum sit, nunquis]", "lorem dol"),
			"withOperator", new String[] {"${configuration.operator}"},
			new String[] {"or"});

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[lorem ipsum dolor]", "lorem dol"),
			"withOperator", new String[] {"${configuration.operator}"},
			new String[] {"and"});
	}

	@Test
	public void testTextMatchOverMultipleFields_crossFields() throws Exception {
		_setUp(
			new String[] {"foxtrot, golf", "hotel golf", "alpha", "beta"},
			new String[] {
				"alpha beta", "alpha edison", "beta charlie", "edison india"
			});

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[alpha beta, alpha edison, beta charlie]", "alpha golf"),
			"withOperator", new String[] {"${configuration.operator}"},
			new String[] {"or"});
		_test(
			() -> _assertSearchIgnoreRelevance(
				"[alpha beta, alpha edison]", "alpha golf"),
			"withOperator", new String[] {"${configuration.operator}"},
			new String[] {"and"});
	}

	@Test
	public void testTextMatchOverMultipleFields_mostFields() throws Exception {
		_setUp(
			new String[] {
				"ipsum sit sit", "ipsum sit", "ipsum sit sit",
				"non-lorem ipsum sit"
			},
			new String[] {
				"amet", "lorem ipsum dolor", "lorem ipsum sit", "nunquis"
			});

		_test(
			() -> _assertSearch(
				"[lorem ipsum sit, lorem ipsum dolor, amet, nunquis]",
				"ipsum sit sit"),
			"withOperator", new String[] {"${configuration.operator}"},
			new String[] {"or"});
		_test(
			() -> _assertSearch("[lorem ipsum sit, nunquis]", "sit lorem"),
			"withOperator", new String[] {"${configuration.operator}"},
			new String[] {"and"});
	}

	@Test
	public void testTextMatchOverMultipleFields_phase() throws Exception {
		_setUp(
			new String[] {
				"do not listen to birds", "listen listen to birds",
				"listen to birds", "listen listen to birds"
			},
			new String[] {
				"listen something", "listen to birds", "listen to planes",
				"silence"
			});

		_test(
			() -> _assertSearch("[listen to birds, silence]", "listen listen"),
			"withKeywords", null, null);
	}

	@Test
	public void testTextMatchOverMultipleFields_phasePrefix() throws Exception {
		_setUp(
			new String[] {
				"simple things are beautiful sometimes",
				"simple things are beautiful", "simple things are not good",
				"simple things are bad"
			},
			new String[] {
				"clouds", "watch birds on the sky", "watch planes on the sky",
				"watch trains"
			});

		_test(
			() -> _assertSearch(
				"[watch birds on the sky, clouds]", "simple things are beau"),
			"withKeywords", null, null);
	}

	private void _addAssetCatetory(String title, User user) throws Exception {
		if (_assetVocabulary == null) {
			_assetVocabulary =
				AssetVocabularyLocalServiceUtil.addDefaultVocabulary(
					_group.getGroupId());
		}

		_assetCategory = AssetCategoryLocalServiceUtil.addCategory(
			user.getUserId(), _group.getGroupId(), title,
			_assetVocabulary.getVocabularyId(), _serviceContext);
	}

	private void _addExpandoColumn(long companyId, String... columns) {
		ExpandoTable expandoTable = ExpandoTableLocalServiceUtil.fetchTable(
			companyId,
			ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class),
			"CUSTOM_FIELDS");

		try {
			if (expandoTable == null) {
				expandoTable = ExpandoTableLocalServiceUtil.addTable(
					companyId,
					ClassNameLocalServiceUtil.getClassNameId(
						JournalArticle.class),
					"CUSTOM_FIELDS");

				_expandoTables.add(expandoTable);
			}

			for (String column : columns) {
				ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
					expandoTable, column, ExpandoColumnConstants.GEOLOCATION);

				_expandoColumns.add(expandoColumn);

				UnicodeProperties unicodeProperties =
					expandoColumn.getTypeSettingsProperties();

				unicodeProperties.setProperty(
					ExpandoColumnConstants.INDEX_TYPE,
					String.valueOf(ExpandoColumnConstants.GEOLOCATION));

				expandoColumn.setTypeSettingsProperties(unicodeProperties);

				ExpandoColumnLocalServiceUtil.updateExpandoColumn(
					expandoColumn);
			}
		}
		catch (Exception exception) {
			_log.error("Add expando column error", exception);
		}
	}

	private void _addGroupAAndGroupB() {
		try {
			_groupA = GroupTestUtil.addGroup(
				GroupConstants.DEFAULT_PARENT_GROUP_ID,
				RandomTestUtil.randomString(), _serviceContext);
			_groupB = GroupTestUtil.addGroup(
				GroupConstants.DEFAULT_PARENT_GROUP_ID,
				RandomTestUtil.randomString(), _serviceContext);
		}
		catch (Exception exception) {
			_log.error("Add groupA and groupB error", exception);
		}
	}

	private User _addGroupUser(Group group, String roleName) {
		try {
			Role role = RoleTestUtil.addRole(
				roleName, RoleConstants.TYPE_REGULAR);

			return UserTestUtil.addGroupUser(group, role.getName());
		}
		catch (Exception exception) {
			_log.error("Add group user error", exception);
		}

		return null;
	}

	private SegmentsEntry _addSegmentsEntry(User user) throws Exception {
		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria, String.format("(firstName eq '%s')", user.getFirstName()),
			Criteria.Conjunction.AND);

		return SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria),
			User.class.getName());
	}

	private void _assertSearch(String expected, String keywords) {
		try {
			SearchResponse searchResponse = _getSearchResponse(keywords);

			DocumentsAssert.assertValues(
				searchResponse.getRequestString(),
				searchResponse.getDocumentsStream(), "localized_title_en_US",
				expected);
		}
		catch (Exception exception) {
			Assert.fail();
		}
	}

	private void _assertSearchIgnoreRelevance(
		String expected, String keywords) {

		try {
			SearchResponse searchResponse = _getSearchResponse(keywords);

			DocumentsAssert.assertValuesIgnoreRelevance(
				searchResponse.getRequestString(),
				searchResponse.getDocumentsStream(), "localized_title_en_US",
				expected);
		}
		catch (Exception exception) {
			Assert.fail();
		}
	}

	private ConfigurationTemporarySwapper _getConfigurationTemporarySwapper(
			String apiKey, String enabled, String apiURL)
		throws Exception {

		return new ConfigurationTemporarySwapper(
			"com.liferay.search.experiences.internal.configuration." +
				"IpstackConfiguration",
			_toDictionary(
				HashMapBuilder.put(
					"apiKey", apiKey
				).put(
					"apiURL", apiURL
				).put(
					"enabled", enabled
				).build()));
	}

	private Date _getNextDay() {
		Calendar cal = CalendarFactoryUtil.getCalendar();

		cal.add(Calendar.DAY_OF_YEAR, 1);

		return cal.getTime();
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
					"scope_group_id", _group.getGroupId());
				_searchContext.setAttribute(
					"search.experiences.blueprint.id",
					_sxpBlueprint.getSXPBlueprintId());
				_searchContext.setTimeZone(_user.getTimeZone());
				_searchContext.setUserId(_user.getUserId());
			}
		).build();

		return _searcher.search(searchRequest);
	}

	private void _setUp(
			String[] journalArticleContents, String[] journalArticleTitles)
		throws Exception {

		_setUp(
			journalArticleContents, journalArticleTitles,
			() -> {
			});
	}

	private void _setUp(
			String[] expandoColumns, String[] journalArticleTitles,
			double[] latitudes, double[] longitudes,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		unsafeRunnable.run();

		for (int i = 0; i < journalArticleTitles.length; i++) {
			_serviceContext.setExpandoBridgeAttributes(
				Collections.singletonMap(
					expandoColumns[i],
					JSONUtil.put(
						"latitude", latitudes[i]
					).put(
						"longitude", longitudes[i]
					).toString()));

			_setUp(new String[] {""}, new String[] {journalArticleTitles[i]});
		}
	}

	private void _setUp(
			String[] journalArticleContents, String[] journalArticleTitles,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		unsafeRunnable.run();

		Group group = _group;

		if (_groupA != null) {
			group = _groupA;
		}

		_journalArticles.add(
			JournalTestUtil.addArticle(
				group.getGroupId(), 0,
				PortalUtil.getClassNameId(JournalArticle.class),
				HashMapBuilder.put(
					LocaleUtil.US, journalArticleTitles[0]
				).build(),
				null,
				HashMapBuilder.put(
					LocaleUtil.US, journalArticleContents[0]
				).build(),
				LocaleUtil.getSiteDefault(), false, true, _serviceContext));

		if (journalArticleTitles.length < 2) {
			return;
		}

		if (_groupB != null) {
			group = _groupB;
		}

		if (_assetCategory != null) {
			_serviceContext.setAssetCategoryIds(
				new long[] {_assetCategory.getCategoryId()});
		}

		if (_assetTag != null) {
			_serviceContext.setAssetTagNames(
				new String[] {_assetTag.getName()});
		}

		TimeUnit.SECONDS.sleep(_addJournalArticleSleep);

		if (_journalFolder != null) {
			_journalArticles.add(
				JournalTestUtil.addArticle(
					group.getGroupId(), _journalFolder.getFolderId(),
					PortalUtil.getClassNameId(JournalArticle.class),
					HashMapBuilder.put(
						LocaleUtil.US, journalArticleTitles[1]
					).build(),
					null,
					HashMapBuilder.put(
						LocaleUtil.US, journalArticleContents[1]
					).build(),
					LocaleUtil.getSiteDefault(), false, true, _serviceContext));
		}
		else {
			_journalArticles.add(
				JournalTestUtil.addArticle(
					group.getGroupId(), 0,
					PortalUtil.getClassNameId(JournalArticle.class),
					HashMapBuilder.put(
						LocaleUtil.US, journalArticleTitles[1]
					).build(),
					null,
					HashMapBuilder.put(
						LocaleUtil.US, journalArticleContents[1]
					).build(),
					LocaleUtil.getSiteDefault(), false, true, _serviceContext));
		}

		for (int i = 2;
			 (journalArticleTitles.length > 2) &&
			 (i < journalArticleTitles.length); i++) {

			_journalArticles.add(
				JournalTestUtil.addArticle(
					_group.getGroupId(), 0,
					PortalUtil.getClassNameId(JournalArticle.class),
					HashMapBuilder.put(
						LocaleUtil.US, journalArticleTitles[i]
					).build(),
					null,
					HashMapBuilder.put(
						LocaleUtil.US, journalArticleContents[i]
					).build(),
					LocaleUtil.getSiteDefault(), false, true, _serviceContext));
		}
	}

	private void _test(
			Runnable runnable, String resourceName, String[] configurationNames,
			String[] configurationValues)
		throws Exception {

		String json = "{}";

		if (!Validator.isBlank(resourceName)) {
			Thread currentThread = Thread.currentThread();

			Class<?> clazz = getClass();

			StackTraceElement[] stackTraceElements =
				currentThread.getStackTrace();

			json = StringUtil.read(
				clazz.getResourceAsStream(
					StringBundler.concat(
						"dependencies/", clazz.getSimpleName(),
						StringPool.PERIOD,
						stackTraceElements[2].getMethodName(), "_",
						resourceName, ".json")));
		}

		if (configurationNames != null) {
			for (int i = 0; i < configurationNames.length; i++) {
				json = StringUtil.replace(
					json, configurationNames[i], configurationValues[i]);
			}
		}

		SXPBlueprintLocalServiceUtil.updateSXPBlueprint(
			_sxpBlueprint.getUserId(), _sxpBlueprint.getSXPBlueprintId(), json,
			_sxpBlueprint.getDescriptionMap(),
			_sxpBlueprint.getElementInstancesJSON(),
			_sxpBlueprint.getTitleMap(), _serviceContext);
		runnable.run();
	}

	private Dictionary<String, Object> _toDictionary(Map<String, String> map) {
		return new HashMapDictionary<>(new HashMap<String, Object>(map));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SXPBlueprintSearchResultTest.class);

	private int _addJournalArticleSleep;
	private AssetCategory _assetCategory;
	private AssetTag _assetTag;
	private AssetVocabulary _assetVocabulary;

	@DeleteAfterTestRun
	private final List<ExpandoColumn> _expandoColumns = new ArrayList<>();

	@DeleteAfterTestRun
	private final List<ExpandoTable> _expandoTables = new ArrayList<>();

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Group _groupA;

	@DeleteAfterTestRun
	private Group _groupB;

	private final List<JournalArticle> _journalArticles = new ArrayList<>();
	private JournalFolder _journalFolder;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private SXPBlueprint _sxpBlueprint;

	private User _user;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _userSegmentsCriteriaContributor;

}