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
import com.liferay.calendar.test.util.FieldValuesAssert;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.text.DateFormat;

import java.util.HashMap;
import java.util.Map;

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
public class CalendarIndexerIndexedFieldsTest
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

	@Test
	public void testIndexedFields() throws Exception {

		//calendar name
		String originalName = "entity title";
		String translatedName = "entitas neve";

		//calendar description
		String originalDescription = "calendar description";
		String translatedDescription = "descripción del calendario";

		//add Calendar
		Calendar calendar = addCalendar(
			new LocalizedValuesMap() {
				{
					put(LocaleUtil.US, originalName);
					put(LocaleUtil.HUNGARY, translatedName);
				}
			},
			new LocalizedValuesMap() {
				{
					put(LocaleUtil.US, originalDescription);
					put(LocaleUtil.HUNGARY, translatedDescription);
				}
			},
			getServiceContext());

		ParamForFormatSource paramForFormatSource = new ParamForFormatSource(
			originalName, translatedName, originalDescription,
			translatedDescription, calendar);

		Map<String, String> mapStrings = _getFieldsString(paramForFormatSource);

		String searchTerm = "nev";

		Document document = search(searchTerm, LocaleUtil.HUNGARY);

		setRoleId(mapStrings, document);

		FieldValuesAssert.assertFieldValues(mapStrings, document, searchTerm);
	}

	@Test
	public void testIndexedFieldsMissingWhenDescriptionIsEmpty()
		throws Exception {

		//calendar name
		String originalName = "entity title";
		String translatedName = "título da entidade";

		//add Calendar
		Calendar calendar = addCalendar(
			new LocalizedValuesMap() {
				{
					put(LocaleUtil.US, originalName);
					put(LocaleUtil.BRAZIL, translatedName);
				}
			},
			new LocalizedValuesMap() {
			},
			getServiceContext());

		long calendarId = calendar.getCalendarId();

		String searchTerm = String.valueOf(calendarId);

		Document document = search(searchTerm, LocaleUtil.BRAZIL);

		Assert.assertNull(document);
	}

	@Override
	protected Group getGroup() {
		return _group;
	}

	@Override
	protected Indexer<?> getIndexer() {
		return _indexer;
	}

	@SuppressWarnings("serial")
	private Map<String, String> _getFieldsString(
			ParamForFormatSource paramForFormatSource)
		throws Exception {

		String originalName = paramForFormatSource.originalName;
		String translatedName = paramForFormatSource.translatedName;
		String originalDescription = paramForFormatSource.originalDescription;
		String translatedDescription =
			paramForFormatSource.translatedDescription;
		Calendar calendar = paramForFormatSource.calendar;

		DateFormat df = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmmss");

		CalendarResource clendarResource = calendar.getCalendarResource();

		Map<String, String> mapStrings = new HashMap<String, String>() {
			{
				put(Field.NAME, originalName);
				put(Field.NAME + "_en_US", originalName);
				put(Field.NAME + "_hu_HU", translatedName);
				put(Field.DESCRIPTION, originalDescription);
				put(Field.DESCRIPTION + "_en_US", originalDescription);
				put(Field.DESCRIPTION + "_hu_HU", translatedDescription);
				put(
					Field.ENTRY_CLASS_PK,
					String.valueOf(calendar.getCalendarId()));
				put("calendarId", String.valueOf(calendar.getCalendarId()));
				put(Field.COMPANY_ID, String.valueOf(calendar.getCompanyId()));
				put(Field.STAGING_GROUP, "false");
				put(Field.USER_ID, String.valueOf(calendar.getUserId()));
				put(
					Field.USER_NAME,
					StringUtil.toLowerCase(calendar.getUserName()));
				put(
					"resourceName",
					StringUtil.toLowerCase(
						clendarResource.getName(LocaleUtil.US, true)));
				put(
					"resourceName_en_US",
					StringUtil.toLowerCase(
						clendarResource.getName(
							calendar.getDefaultLanguageId())));
				put(Field.DEFAULT_LANGUAGE_ID, calendar.getDefaultLanguageId());
				put(Field.ENTRY_CLASS_NAME, calendar.getModelClassName());
				put(Field.CREATE_DATE, df.format(calendar.getCreateDate()));
				put(
					"createDate_sortable",
					String.valueOf(calendar.getCreateDate().getTime()));
				put(Field.MODIFIED_DATE, df.format(calendar.getModifiedDate()));
				put(
					"modified_sortable",
					String.valueOf(calendar.getModifiedDate().getTime()));
				put(Field.GROUP_ID, String.valueOf(calendar.getGroupId()));
				put(
					Field.SCOPE_GROUP_ID,
					String.valueOf(calendar.getGroupId()));
			}
		};

		String modelClassName = calendar.getModelClassName();
		long calendarId = calendar.getCalendarId();

		serUID(mapStrings, modelClassName, calendarId);

		setGroupRoleId(mapStrings);

		return mapStrings;
	}

	@DeleteAfterTestRun
	private Group _group;

	private Indexer<?> _indexer;

	private class ParamForFormatSource {

		public ParamForFormatSource(
			String originalName, String translatedName,
			String originalDescription, String translatedDescription,
			Calendar calendar) {

			this.originalName = originalName;
			this.translatedName = translatedName;
			this.originalDescription = originalDescription;
			this.translatedDescription = translatedDescription;
			this.calendar = calendar;
		}

		protected Calendar calendar;
		protected String originalDescription;
		protected String originalName;
		protected String translatedDescription;
		protected String translatedName;

	}

}