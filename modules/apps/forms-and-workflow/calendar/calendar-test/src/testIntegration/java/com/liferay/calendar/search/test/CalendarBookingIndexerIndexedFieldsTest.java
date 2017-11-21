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
import com.liferay.calendar.constants.CalendarActionKeys;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.search.CalendarBookingIndexer;
import com.liferay.calendar.test.util.FieldValuesAssert;
import com.liferay.portal.kernel.exception.PortalException;
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
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
public class CalendarBookingIndexerIndexedFieldsTest
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

	@Test
	public void testIndexedFields() throws Exception {
		String originalTitle = "entity title";
		String translatedTitle = "entitas neve";

		DateFormat df = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmm");

		String description = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		CalendarBooking calendarBooking = addCalendarBooking(
			new LocalizedValuesMap() {
				{
					put(LocaleUtil.US, originalTitle);
					put(LocaleUtil.HUNGARY, translatedTitle);
				}
			},
			new LocalizedValuesMap() {
				{
					put(LocaleUtil.US, originalTitle);
					put(LocaleUtil.HUNGARY, translatedTitle);
				}
			},
			new LocalizedValuesMap() {
				{
					put(LocaleUtil.US, description);
					put(LocaleUtil.HUNGARY, description);
				}
			},
			getServiceContext());

		String searchTerm = "nev";

		ParamForFormatSource paramForFormatSource = new ParamForFormatSource(
			originalTitle, translatedTitle, calendarBooking, df);

		Map<String, String> mapStrings = _getFieldsString(paramForFormatSource);

		Document document = search(searchTerm, LocaleUtil.HUNGARY);

		setRoleId(mapStrings, document);

		_processDatePrecision(df, document);

		FieldValuesAssert.assertFieldValues(mapStrings, document, searchTerm);
	}

	@Override
	protected Group getGroup() {
		return _group;
	}

	@Override
	protected Indexer<?> getIndexer() {
		return _indexer;
	}

	protected void setLocalized(Map<String, String> mapStrings, String title) {
		mapStrings.put("localized_title_ca_ES_sortable", title);
		mapStrings.put("localized_title_iw_IL_sortable", title);
		mapStrings.put("localized_title_nl_NL", title);
		mapStrings.put("localized_title_pt_BR", title);
		mapStrings.put("localized_title_zh_CN", title);
		mapStrings.put("localized_title_zh_CN_sortable", title);

		mapStrings.put("localized_title_ca_ES", title);
		mapStrings.put("localized_title_fr_FR", title);
		mapStrings.put("localized_title_ja_JP", title);
		mapStrings.put("localized_title_nl_NL_sortable", title);

		mapStrings.put("localized_title_de_DE_sortable", title);
		mapStrings.put("localized_title_en_US_sortable", title);
		mapStrings.put("localized_title_fi_FI_sortable", title);
		mapStrings.put("localized_title_fr_FR_sortable", title);
		mapStrings.put("localized_title_iw_IL", title);
		mapStrings.put("localized_title_pt_BR_sortable", title);

		mapStrings.put("localized_title", title);
		mapStrings.put("localized_title_en_US", title);
		mapStrings.put("localized_title_es_ES", title);
		mapStrings.put("localized_title_es_ES_sortable", title);
		mapStrings.put("localized_title_fi_FI", title);
		mapStrings.put("localized_title_ja_JP_sortable", title);

		mapStrings.put("localized_title_de_DE", title);
	}

	@SuppressWarnings("serial")
	private Map<String, String> _getFieldsString(
			ParamForFormatSource paramForFormatSource)
		throws ParseException, PortalException {

		CalendarBooking calendarBooking = paramForFormatSource.calendarBooking;

		CalendarResource calendarResource =
			calendarBooking.getCalendarResource();

		Calendar calendar = calendarResource.getDefaultCalendar();

		String originalTitle = paramForFormatSource.originalTitle;
		String translatedTitle = paramForFormatSource.translatedTitle;
		DateFormat df = paramForFormatSource.df;

		Map<String, String> mapStrings = new HashMap<String, String>() {
			{
				put(Field.TITLE + "_en_US", originalTitle);
				put(Field.TITLE + "_hu_HU", translatedTitle);
				put("localized_title_hu_HU_sortable", translatedTitle);
				put("localized_title_hu_HU", translatedTitle);
				put(
					Field.ENTRY_CLASS_PK,
					String.valueOf(calendarBooking.getCalendarBookingId()));
				put(Field.PUBLISH_DATE, "19700101000000");
				put("publishDate_sortable", "0");
				put(
					Field.CLASS_NAME_ID,
					String.valueOf(PortalUtil.getClassNameId(Calendar.class)));
				put(
					Field.CLASS_PK,
					String.valueOf(calendarBooking.getCalendarId()));
				put(
					"calendarBookingId",
					String.valueOf(calendarBooking.getCalendarBookingId()));
				put(
					Field.COMPANY_ID,
					String.valueOf(calendarResource.getCompanyId()));
				put(Field.STATUS, "0");
				put(Field.RELATED_ENTRY, "true");
				put(Field.STAGING_GROUP, "false");
				put(Field.USER_ID, String.valueOf(calendar.getUserId()));
				put(
					Field.USER_NAME,
					StringUtil.toLowerCase(calendar.getUserName()));
				put("visible", "true");
				put(Field.DEFAULT_LANGUAGE_ID, calendar.getDefaultLanguageId());
				put(Field.PRIORITY, "0.0");
				put("viewActionId", CalendarActionKeys.VIEW_BOOKING_DETAILS);
				put(
					Field.ENTRY_CLASS_NAME,
					calendarBooking.getModelClassName());
				put(
					"startTime",
					String.valueOf(calendarBooking.getStartTime()));
				put(
					"startTime_sortable",
					String.valueOf(calendarBooking.getStartTime()));
				put("endTime", String.valueOf(calendarBooking.getEndTime()));
				put(
					"endTime_sortable",
					String.valueOf(calendarBooking.getEndTime()));

				put(Field.EXPIRATION_DATE, "99950812133000");
				put("expirationDate_sortable", "9223372036854775807");

				String createDate = df.format(calendar.getCreateDate());
				String modifiedDate = df.format(calendar.getModifiedDate());

				put(Field.CREATE_DATE, createDate + "00");
				put(
					"createDate_sortable",
					String.valueOf(df.parse(createDate).getTime()));
				put(Field.MODIFIED_DATE, modifiedDate + "00");
				put(
					"modified_sortable",
					String.valueOf(df.parse(modifiedDate).getTime()));
				put(
					Field.GROUP_ID,
					String.valueOf(calendarResource.getGroupId()));
				put(
					Field.SCOPE_GROUP_ID,
					String.valueOf(calendarResource.getGroupId()));
			}
		};

		String modelClassName = calendarBooking.getModelClassName();
		long calendarBookingId = calendarBooking.getCalendarBookingId();

		serUID(mapStrings, modelClassName, calendarBookingId);

		setGroupRoleId(mapStrings);
		setLocalized(mapStrings, originalTitle);

		return mapStrings;
	}

	private void _processDatePrecision(DateFormat df, Document document)
		throws ParseException {

		Date createDate = document.getDate(Field.CREATE_DATE);
		Date modifiedDate = document.getDate(Field.MODIFIED_DATE);

		String cDate = df.format(createDate);
		String mDate = df.format(modifiedDate);

		document.addDate(Field.CREATE_DATE, df.parse(cDate));
		document.addDate(Field.MODIFIED_DATE, df.parse(mDate));
		document.addKeyword(
			Field.CREATE_DATE + "_sortable", df.parse(cDate).getTime());
		document.addKeyword(
			Field.MODIFIED_DATE + "_sortable", df.parse(mDate).getTime());
	}

	@DeleteAfterTestRun
	private Group _group;

	private Indexer<?> _indexer;

	private class ParamForFormatSource {

		public ParamForFormatSource(
			String originalTitle, String translatedTitle,
			CalendarBooking calendarBooking, DateFormat df) {

			this.originalTitle = originalTitle;
			this.translatedTitle = translatedTitle;
			this.calendarBooking = calendarBooking;
			this.df = df;
		}

		protected CalendarBooking calendarBooking;
		protected DateFormat df;
		protected String originalTitle;
		protected String translatedTitle;

	}

}