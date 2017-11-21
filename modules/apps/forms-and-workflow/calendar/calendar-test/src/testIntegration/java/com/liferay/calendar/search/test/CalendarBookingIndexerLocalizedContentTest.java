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
import com.liferay.calendar.search.CalendarBookingIndexer;
import com.liferay.calendar.test.util.FieldValuesAssert;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
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
public class CalendarBookingIndexerLocalizedContentTest
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

		_indexer = new CalendarBookingIndexer();
	}

	@SuppressWarnings("serial")
	@Test
	public void testJapaneseTitle() throws Exception {
		String originalName = "entity name";
		String japaneseName = "新規作成";

		String description = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		addCalendarBooking(
			new LocalizedValuesMap() {
				{
					put(LocaleUtil.US, originalName);
					put(LocaleUtil.JAPAN, japaneseName);
				}
			},
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
			},
			getServiceContext());

		Map<String, String> titleStrings = new HashMap<String, String>() {
			{
				put("title_en_US", originalName);
				put("title_ja_JP", japaneseName);
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
					titleStrings, "title", document, searchTerm);
			});
	}

	@SuppressWarnings("serial")
	@Test
	public void testJapaneseTitleFullWordOnly() throws Exception {
		String full = "新規作成";
		String partial1 = "新大阪";
		String partial2 = "作戦大成功";

		Stream<String> titles = Stream.of(full, partial1, partial2);

		String description = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		titles.forEach(
			title -> {
				try {
					addCalendarBooking(
						new LocalizedValuesMap() {
							{
								put(LocaleUtil.JAPAN, title);
							}
						},
						new LocalizedValuesMap() {
							{
								put(LocaleUtil.US, description);
								put(LocaleUtil.HUNGARY, description);
							}
						},
						new LocalizedValuesMap() {
							{
								put(LocaleUtil.US, description);
								put(LocaleUtil.HUNGARY, description);
							}
						},
						getServiceContext());
				}
				catch (PortalException pe) {
					pe.printStackTrace();
				}
			});

		Map<String, String> titleStrings = new HashMap<String, String>() {
			{
				put("title_ja_JP", "新規作成");
			}
		};

		String word1 = "新規";
		String word2 = "作成";

		Stream<String> searchTerms = Stream.of(word1, word2);

		searchTerms.forEach(
			searchTerm -> {
				Document document = search(searchTerm, LocaleUtil.JAPAN);

				FieldValuesAssert.assertFieldValues(
					titleStrings, "title", document, searchTerm);
			});
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