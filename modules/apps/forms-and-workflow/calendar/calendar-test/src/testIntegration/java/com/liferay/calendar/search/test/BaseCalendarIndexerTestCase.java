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

import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.CalendarBookingConstants;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.service.CalendarBookingLocalServiceUtil;
import com.liferay.calendar.service.CalendarLocalServiceUtil;
import com.liferay.calendar.util.CalendarResourceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.service.test.ServiceTestUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Wade Cao
 */
public abstract class BaseCalendarIndexerTestCase {

	public void setUp() throws Exception {
		ServiceTestUtil.setUser(TestPropsValues.getUser());

		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());
	}

	protected Calendar addCalendar(
		LocalizedValuesMap nameMap, LocalizedValuesMap descriptionMap,
		ServiceContext serviceContext) {

		try {
			CalendarResource calendarResource =
				CalendarResourceUtil.getGroupCalendarResource(
					getGroup().getGroupId(), serviceContext);

			return CalendarLocalServiceUtil.addCalendar(
				serviceContext.getUserId(), getGroup().getGroupId(),
				calendarResource.getCalendarResourceId(), nameMap.getValues(),
				descriptionMap.getValues(), StringPool.UTF8,
				RandomTestUtil.randomInt(0, 255), false, false, false,
				serviceContext);
		}
		catch (PortalException pe) {
			throw new RuntimeException(pe);
		}
	}

	protected CalendarBooking addCalendarBooking(
		LocalizedValuesMap titleMap, LocalizedValuesMap nameMap,
		LocalizedValuesMap descripMap, ServiceContext serviceContext) {

		try {
			Calendar calendar = addCalendar(
				nameMap, descripMap, serviceContext);

			long startTime = DateUtil.newTime() + RandomTestUtil.randomInt();

			long endTime = startTime + Time.HOUR;

			HashMap<Locale, String> hashMap = new HashMap<>();

			CalendarBooking calendarBooking =
				CalendarBookingLocalServiceUtil.addCalendarBooking(
					serviceContext.getUserId(), calendar.getCalendarId(),
					new long[0],
					CalendarBookingConstants.PARENT_CALENDAR_BOOKING_ID_DEFAULT,
					0, titleMap.getValues(), hashMap, null, startTime, endTime,
					false, null, 0, "email", 0, "email", serviceContext);

			return calendarBooking;
		}
		catch (PortalException pe) {
			throw new RuntimeException(pe);
		}
	}

	protected abstract Group getGroup();

	protected abstract Indexer<?> getIndexer();

	protected SearchContext getSearchContext(String searchTerm, Locale locale)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			getGroup().getGroupId());

		searchContext.setKeywords(searchTerm);

		if (locale != null) {
			searchContext.setLocale(locale);
		}

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(StringPool.STAR);

		return searchContext;
	}

	protected ServiceContext getServiceContext() throws PortalException {
		return ServiceContextTestUtil.getServiceContext(
			getGroup().getGroupId());
	}

	protected Document getSingleDocument(String searchTerm, Hits hits) {
		List<Document> documents = hits.toList();

		if (documents.size() == 1) {
			return documents.get(0);
		}
		else if (documents.isEmpty()) {
			return null;
		}

		throw new AssertionError(searchTerm + "->" + documents);
	}

	protected Document search(String searchTerm, Locale locale) {
		try {
			SearchContext searchContext = getSearchContext(searchTerm, locale);

			Hits hits = getIndexer().search(searchContext);

			return getSingleDocument(searchTerm, hits);
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void serUID(
		Map<String, String> mapStrings, String modelClassName,
		long calendarId) {

		String uid = modelClassName + "_PORTLET_" + calendarId;

		mapStrings.put(Field.UID, uid);
	}

	protected void setGroupRoleId(Map<String, String> mapStrings)
		throws PortalException {

		Role role = RoleLocalServiceUtil.getDefaultGroupRole(
			getGroup().getGroupId());

		String groupRoleId =
			getGroup().getGroupId() + StringPool.DASH + role.getRoleId();

		mapStrings.put(Field.GROUP_ROLE_ID, groupRoleId);
	}

	protected void setRoleId(
		Map<String, String> mapStrings, Document document) {

		Role role = RoleLocalServiceUtil.fetchRole(
			getGroup().getCompanyId(), "Owner");

		String roleId = String.valueOf(role.getRoleId());

		if (!roleId.equals(document.get(Field.ROLE_ID))) {
			role = RoleLocalServiceUtil.fetchRole(
				getGroup().getCompanyId(), "Guest");

			roleId = String.valueOf(role.getRoleId());
		}

		mapStrings.put(Field.ROLE_ID, roleId);
	}

}