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
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
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

		_assetCategory = null;
		_assetCreatedTimeInterval = 0;
		_assetTag = null;
		_assetVocabulary = null;
		_group = GroupTestUtil.addGroup();
		_journalArticles.clear();

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
	public void testBoostContents() throws Exception {
		_addGroups();
		_addCatetory("Important", _group, _user);
		_addJournalArticles(
			new String[] {"coca cola", "pepsi cola"},
			new String[] {"cola cola", ""});

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[coca cola, pepsi cola]", "cola"),
			null, null, null);
		_test(
			() -> _assertSearch("[pepsi cola, coca cola]", "cola"),
			"inCategory", new String[] {"${configuration.asset_category_ids}"},
			new String[] {String.valueOf(_assetCategory.getCategoryId())});

		Group group = _groups.get(1);

		_user = UserTestUtil.addUser(group.getGroupId());

		_serviceContext.setUserId(_user.getUserId());

		_test(
			() -> _assertSearch("[pepsi cola, coca cola]", "cola"), "onMySites",
			null, null);
	}

	@Test
	public void testBoostFreshness() throws Exception {
		_assetCreatedTimeInterval = 3;

		_addJournalArticles(
			new String[] {"coca cola", "pepsi cola"},
			new String[] {"cola cola", ""});
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
		_addExpandoColumn(_group.getCompanyId(), "location");

		_addJournalArticle("Branch SF", "location", 64.01, -117.42);
		_addJournalArticle("Branch LA", "location", 24.03, -107.44);

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
		_user = _addGroupUser(_group, "employee");

		_addCatetory("Promoted", _group, _user);

		_addJournalArticles(
			new String[] {"Coca Cola", "Pepsi Cola"},
			new String[] {"cola cola", ""});

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[coca cola, pepsi cola]", "cola"),
			null, null, null);
		_test(
			() -> _assertSearch("[pepsi cola, coca cola]", "cola"),
			"withAssetCategory",
			new String[] {
				"${configuration.asset_category_id}",
				"${configuration.keywords}"
			},
			new String[] {
				String.valueOf(_assetCategory.getCategoryId()), "cola"
			});

		JournalArticle journalArticle = _journalArticles.get(1);

		_test(
			() -> _assertSearch("[pepsi cola, coca cola]", "cola"),
			"withContains", new String[] {"${articleId}"},
			new String[] {journalArticle.getArticleId()});
		_test(
			() -> _assertSearch("[coca cola, pepsi cola]", "cola"),
			"withNotContains", new String[] {"${articleId}"},
			new String[] {journalArticle.getArticleId()});

		SegmentsEntry segmentsEntry = _addSegmentsEntry(_user);

		_test(
			() -> _assertSearch("[pepsi cola, coca cola]", "cola"),
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
		_addCatetory("Promoted", _group, _addGroupUser(_group, "Custmers"));
		_addJournalArticles(
			new String[] {"Coca Cola", "Pepsi Cola"},
			new String[] {"cola cola", ""});

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

		_addCatetory(
			"For New Recruits", _group, _addGroupUser(_group, "Employee"));
		_addJournalArticles(
			new String[] {
				"Company policies for All Employees Recruits",
				"Company Policies for New Recruits"
			},
			new String[] {"policies policies", ""});

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
		_assetTag = AssetTestUtil.addTag(_group.getGroupId(), "hide");

		_journalFolder = JournalFolderServiceUtil.addFolder(
			_group.getGroupId(), 0, RandomTestUtil.randomString(),
			StringPool.BLANK, _serviceContext);

		_addJournalArticles(
			new String[] {"do not hide me", "hide me"}, new String[] {"", ""});

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
		_addJournalArticles(
			new String[] {
				"Cafe Rio", "Cloud Cafe", "Denny's", "Starbucks Cafe"
			},
			new String[] {
				"Los Angeles", "Orange County", "Los Angeles", "Los Angeles"
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
		_assetTag = AssetTagLocalServiceUtil.addTag(
			_user.getUserId(), _group.getGroupId(), "cola", _serviceContext);

		_addJournalArticles(
			new String[] {"coca cola", "pepsi cola"}, new String[] {"", ""});

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
		_addGroups();
		_addJournalArticles(
			new String[] {"cola coca", "cola pepsi", "cola sprite"},
			new String[] {"", "", ""});

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[cola coca, cola pepsi, cola sprite]", "cola"),
			null, null, null);

		Group groupA = _groups.get(0);
		Group groupB = _groups.get(1);

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[cola coca, cola pepsi]", "cola"),
			"withFilterByExactTermMatch",
			new String[] {"${configuration.value1}", "${configuration.value2}"},
			new String[] {
				String.valueOf(groupA.getGroupId()),
				String.valueOf(groupB.getGroupId())
			});

		_user = UserTestUtil.addUser(groupA.getGroupId());

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
				String.valueOf(groupA.getGroupId()),
				String.valueOf(groupB.getGroupId())
			});
	}

	@Test
	public void testMatch() throws Exception {
		_addJournalArticles(
			new String[] {
				"Cafe Rio", "Cloud Cafe", "Denny's", "Starbucks Cafe"
			},
			new String[] {
				"Los Angeles", "Orange County", "Los Angeles", "Los Angeles"
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
		_addJournalArticles(
			new String[] {
				"This coca looks like a kind of drink",
				"This looks like a kind of coca drink"
			},
			new String[] {"coca coca", ""});

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
		_addJournalArticles(
			new String[] {"Cafe Rio", "Cloud Cafe"},
			new String[] {"Los Angeles", "Orange County"});

		_test(
			() -> _assertSearchIgnoreRelevance(
				"[cafe rio, cloud cafe]", "cafe"),
			null, null, null);
		_test(
			() -> _assertSearch("[cafe rio, cloud cafe]", "cafe"),
			"withDefaultBehavior", null, null);

		_addJournalArticles(
			new String[] {"Coca Cola", "Pepsi Cola"}, new String[] {"", ""});

		_test(
			() -> _assertSearch("[coca cola]", "cola +coca"),
			"withLuceneSyntax", null, null);
		_test(
			() -> _assertSearch("[pepsi cola]", "cola -coca"),
			"withLuceneSyntax", null, null);
	}

	@Test
	public void testTextMatchOverMultipleFields_bestFields() throws Exception {
		_addJournalArticles(
			new String[] {
				"drink carbonated coca", "drink carbonated pepsi cola",
				"fruit punch", "sprite"
			},
			new String[] {
				"carbonated cola", "carbonated cola cola",
				"non-carbonated cola", "carbonated cola cola"
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

		_addJournalArticles(
			new String[] {"lorem ipsum dolor", "lorem ipsum sit", "nunquis"},
			new String[] {"ipsum sit", "ipsum sit sit", "non-lorem ipsum sit"});
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
		_addJournalArticles(
			new String[] {
				"lorem ipsum sit", "lorem ipsum dolor", "amet", "nunquis"
			},
			new String[] {
				"ipsum sit sit", "ipsum sit", "ipsum sit sit",
				"non-lorem ipsum sit"
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
		_addJournalArticles(
			new String[] {
				"alpha beta", "alpha edison", "beta charlie", "edison india"
			},
			new String[] {"foxtrot, golf", "hotel golf", "alpha", "beta"});

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
		_addJournalArticles(
			new String[] {
				"amet", "lorem ipsum dolor", "lorem ipsum sit", "nunquis"
			},
			new String[] {
				"ipsum sit sit", "ipsum sit", "ipsum sit sit",
				"non-lorem ipsum sit"
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
		_addJournalArticles(
			new String[] {
				"listen something", "listen to birds", "listen to planes",
				"silence"
			},
			new String[] {
				"do not listen to birds", "listen listen to birds",
				"listen to birds", "listen listen to birds"
			});
		_test(
			() -> _assertSearch("[listen to birds, silence]", "listen listen"),
			"withKeywords", null, null);
	}

	@Test
	public void testTextMatchOverMultipleFields_phasePrefix() throws Exception {
		_addJournalArticles(
			new String[] {
				"clouds", "watch birds on the sky", "watch planes on the sky",
				"watch trains"
			},
			new String[] {
				"simple things are beautiful sometimes",
				"simple things are beautiful", "simple things are not good",
				"simple things are bad"
			});
		_test(
			() -> _assertSearch(
				"[watch birds on the sky, clouds]", "simple things are beau"),
			"withKeywords", null, null);
	}

	private void _addCatetory(String categoryTitle, Group group, User user)
		throws Exception {

		if (_assetVocabulary == null) {
			_assetVocabulary =
				AssetVocabularyLocalServiceUtil.addDefaultVocabulary(
					group.getGroupId());
		}

		_assetCategory = AssetCategoryLocalServiceUtil.addCategory(
			user.getUserId(), group.getGroupId(), categoryTitle,
			_assetVocabulary.getVocabularyId(), _serviceContext);
	}

	private void _addExpandoColumn(long companyId, String... columns)
		throws Exception {

		ExpandoTable expandoTable = ExpandoTableLocalServiceUtil.fetchTable(
			companyId,
			ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class),
			"CUSTOM_FIELDS");

		if (expandoTable == null) {
			expandoTable = ExpandoTableLocalServiceUtil.addTable(
				companyId,
				ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class),
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

			ExpandoColumnLocalServiceUtil.updateExpandoColumn(expandoColumn);
		}
	}

	private void _addGroups() throws Exception {
		Group groupA = GroupTestUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, "SiteA", _serviceContext);
		Group groupB = GroupTestUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, "SiteB", _serviceContext);

		_groups.add(groupA);
		_groups.add(groupB);
	}

	private User _addGroupUser(Group group, String roleName) throws Exception {
		Role role = RoleTestUtil.addRole(roleName, RoleConstants.TYPE_REGULAR);

		return UserTestUtil.addGroupUser(group, role.getName());
	}

	private void _addJournalArticle(
			String title, String expandoColumn, double latitude,
			double longitude)
		throws Exception {

		_serviceContext.setExpandoBridgeAttributes(
			Collections.singletonMap(
				expandoColumn,
				StringBundler.concat(
					"{\"latitude\":", latitude, ",\"longitude\":", longitude,
					"}")));

		_addJournalArticles(new String[] {title}, new String[] {""});
	}

	private void _addJournalArticles(String[] titles, String[] contents)
		throws Exception {

		Group group = _group;

		if (_groups.size() > 0) {
			group = _groups.get(0);
		}

		_journalArticles.add(
			JournalTestUtil.addArticle(
				group.getGroupId(), 0,
				PortalUtil.getClassNameId(JournalArticle.class),
				HashMapBuilder.put(
					LocaleUtil.US, titles[0]
				).build(),
				null,
				HashMapBuilder.put(
					LocaleUtil.US, contents[0]
				).build(),
				LocaleUtil.getSiteDefault(), false, true, _serviceContext));

		if (titles.length < 2) {
			return;
		}

		if (_groups.size() > 1) {
			group = _groups.get(1);
		}

		if (_assetCategory != null) {
			_serviceContext.setAssetCategoryIds(
				new long[] {_assetCategory.getCategoryId()});
		}

		if (_assetTag != null) {
			_serviceContext.setAssetTagNames(
				new String[] {_assetTag.getName()});
		}

		TimeUnit.SECONDS.sleep(_assetCreatedTimeInterval);

		if (_journalFolder != null) {
			_journalArticles.add(
				JournalTestUtil.addArticle(
					group.getGroupId(), _journalFolder.getFolderId(),
					PortalUtil.getClassNameId(JournalArticle.class),
					HashMapBuilder.put(
						LocaleUtil.US, titles[1]
					).build(),
					null,
					HashMapBuilder.put(
						LocaleUtil.US, contents[1]
					).build(),
					LocaleUtil.getSiteDefault(), false, true, _serviceContext));
		}
		else {
			_journalArticles.add(
				JournalTestUtil.addArticle(
					group.getGroupId(), 0,
					PortalUtil.getClassNameId(JournalArticle.class),
					HashMapBuilder.put(
						LocaleUtil.US, titles[1]
					).build(),
					null,
					HashMapBuilder.put(
						LocaleUtil.US, contents[1]
					).build(),
					LocaleUtil.getSiteDefault(), false, true, _serviceContext));
		}

		for (int i = 2; (titles.length > 2) && (i < titles.length); i++) {
			_journalArticles.add(
				JournalTestUtil.addArticle(
					_group.getGroupId(), 0,
					PortalUtil.getClassNameId(JournalArticle.class),
					HashMapBuilder.put(
						LocaleUtil.US, titles[i]
					).build(),
					null,
					HashMapBuilder.put(
						LocaleUtil.US, contents[i]
					).build(),
					LocaleUtil.getSiteDefault(), false, true, _serviceContext));
		}
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
			String apiKey, String enabled, String ip)
		throws Exception {

		return new ConfigurationTemporarySwapper(
			"com.liferay.search.experiences.internal.configuration." +
				"IpstackConfiguration",
			_toDictionary(
				HashMapBuilder.put(
					"apiKey", apiKey
				).put(
					"apiURL", ip
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

	private AssetCategory _assetCategory;
	private int _assetCreatedTimeInterval;
	private AssetTag _assetTag;
	private AssetVocabulary _assetVocabulary;

	@DeleteAfterTestRun
	private final List<ExpandoColumn> _expandoColumns = new ArrayList<>();

	@DeleteAfterTestRun
	private final List<ExpandoTable> _expandoTables = new ArrayList<>();

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

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