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

package com.liferay.calendar.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.search.CalendarIndexer;
import com.liferay.calendar.service.CalendarLocalServiceUtil;
import com.liferay.calendar.test.util.FieldValuesAssert;
import com.liferay.calendar.util.CalendarResourceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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
public class CalendarIndexerLocalizedContentTest
	extends BaseCalendarIndexerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_group = GroupTestUtil.addGroup();

		_indexer = new CalendarIndexer();
	}

	@SuppressWarnings("serial")
	@Test
	public void testJapaneseName() throws Exception {
		String originalName = "entity name";
		String japaneseName = "新規作成";

		String description = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		addCalendar(
			new LocalizedValuesMap() {
				{
					put(LocaleUtil.US, originalName);
					put(LocaleUtil.JAPAN, japaneseName);
				}
			},
			new LocalizedValuesMap() {
				{
					put(LocaleUtil.US, description);
					put(LocaleUtil.JAPAN, description);
				}
			});

		Map<String, String> nameStrings = new HashMap<String, String>() {
			{
				put("name", originalName);
				put("name_en_US", originalName);
				put("name_ja_JP", japaneseName);
			}
		};

		Map<String, String> descriptionStrings = new HashMap<String, String>() {
			{
				put("description", description);
				put("description_en_US", description);
				put("description_ja_JP", description);
			}
		};

		String word1 = "新規";
		String word2 = "作成";
		String prefix1 = "新";
		String prefix2 = "作";

		Stream<String> searchTerms = Stream.of(word1, word2, prefix1, prefix2);

		searchTerms.forEach(
			searchTerm -> {
				Document document = search(searchTerm, LocaleUtil.JAPAN);

				FieldValuesAssert.assertFieldValues(
					nameStrings, "name", document, searchTerm);

				FieldValuesAssert.assertFieldValues(
					descriptionStrings, "description", document, searchTerm);
			});
	}

	@SuppressWarnings("serial")
	@Test
	public void testJapaneseNameFullWordOnly() throws Exception {
		String full = "新規作成";
		String partial1 = "新大阪";
		String partial2 = "作戦大成功";

		Stream<String> names = Stream.of(full, partial1, partial2);

		String originalName = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		String description = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		names.forEach(
			name -> {
				addCalendar(
					new LocalizedValuesMap() {
						{
							put(LocaleUtil.US, originalName);
							put(LocaleUtil.JAPAN, name);
						}
					},
					new LocalizedValuesMap() {
						{
							put(LocaleUtil.US, description);
							put(LocaleUtil.JAPAN, description);
						}
					});
			});

		Map<String, String> nameStrings = new HashMap<String, String>() {
			{
				put("name", originalName);
				put("name_en_US", originalName);
				put("name_ja_JP", full);
			}
		};

		String word1 = "新規";
		String word2 = "作成";

		Stream<String> searchTerms = Stream.of(word1, word2);

		searchTerms.forEach(
			searchTerm -> {
				Document document = search(searchTerm, LocaleUtil.JAPAN);

				FieldValuesAssert.assertFieldValues(
					nameStrings, "name", document, searchTerm);
			});
	}

	protected Calendar addCalendar(
		LocalizedValuesMap nameMap, LocalizedValuesMap descriptionMap) {

		try {
			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(_group.getGroupId());

			CalendarResource calendarResource =
				CalendarResourceUtil.getGroupCalendarResource(
					_group.getGroupId(), serviceContext);

			return CalendarLocalServiceUtil.addCalendar(
				serviceContext.getUserId(), _group.getGroupId(),
				calendarResource.getCalendarResourceId(), nameMap.getValues(),
				descriptionMap.getValues(), StringPool.UTF8,
				RandomTestUtil.randomInt(0, 255), false, false, false,
				serviceContext);
		}
		catch (PortalException pe) {
			throw new RuntimeException(pe);
		}
	}

	@Override
	protected Group getGroup() {
		return _group;
	}

	@Override
	protected Indexer<?> getIndexer() {
		return _indexer;
	}

	@DeleteAfterTestRun
	private Group _group;

	private Indexer<?> _indexer;

}