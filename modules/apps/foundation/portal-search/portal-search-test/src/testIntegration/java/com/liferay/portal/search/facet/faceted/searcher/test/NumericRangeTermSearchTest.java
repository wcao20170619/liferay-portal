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

package com.liferay.portal.search.facet.faceted.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.text.Format;

import java.util.Calendar;
import java.util.Date;

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
public class NumericRangeTermSearchTest extends BaseFacetedSearcherTestCase {

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

		String indexDateFormatPattern = PropsUtil.get(
			PropsKeys.INDEX_DATE_FORMAT_PATTERN);

		_dateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
			indexDateFormatPattern);
	}

	@Test
	public void testSearchByRange() throws Exception {
		final Group group1 = userSearchFixture.addGroup();

		String keyword = RandomTestUtil.randomString();

		//user1 in group1
		addUser(group1, keyword + " " + RandomTestUtil.randomString());

		final Group group2 = userSearchFixture.addGroup();

		//user2 in group2
		addUser(group2, keyword + " " + RandomTestUtil.randomString());
		//user3 in group2
		addUser(group2, keyword + " " + RandomTestUtil.randomString());

		SearchContext searchContext = getSearchContext(keyword);

		searchContext.setGroupIds(
			new long[] {group1.getGroupId(), group2.getGroupId()});

		//no numeric query
		Hits hits = search(searchContext);

		Assert.assertEquals(hits.toString(), 3, hits.getLength());

		//test date
		searchContext.setAttribute(
			"endVal", _dateFormat.format(getDateFromToday(1)));
		searchContext.setAttribute("numericRangeTerm", "modified");
		searchContext.setAttribute(
			"startVal", _dateFormat.format(getDateFromToday(-3)));

		hits = search(searchContext);

		Assert.assertEquals(hits.toString(), 3, hits.getLength());

		searchContext.setAttribute(
			"endVal", _dateFormat.format(getDateFromToday(100)));
		searchContext.setAttribute(
			"startVal", _dateFormat.format(getDateFromToday(99)));

		hits = search(searchContext);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());

		searchContext.setAttribute(
			"endVal", _dateFormat.format(getDateFromToday(2)));
		searchContext.setAttribute(
			"startVal", _dateFormat.format(getDateFromToday(2)));

		hits = search(searchContext);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());

		//test groupId
		searchContext.setAttribute("endVal", Long.MAX_VALUE);
		searchContext.setAttribute("numericRangeTerm", Field.GROUP_ID);
		searchContext.setAttribute("startVal", Long.MAX_VALUE);

		hits = search(searchContext);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());

		searchContext.setAttribute("endVal", Long.MAX_VALUE);
		searchContext.setAttribute("startVal", Long.MIN_VALUE);

		hits = search(searchContext);

		Assert.assertEquals(hits.toString(), 3, hits.getLength());

		//test userId
		searchContext.setAttribute("endVal", Long.MAX_VALUE);
		searchContext.setAttribute("numericRangeTerm", Field.USER_ID);
		searchContext.setAttribute("startVal", Long.MAX_VALUE);

		hits = search(searchContext);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());

		searchContext.setAttribute("endVal", Long.MAX_VALUE);
		searchContext.setAttribute("startVal", Long.MIN_VALUE);

		hits = search(searchContext);

		Assert.assertEquals(hits.toString(), 3, hits.getLength());
	}

	protected static Date getDateFromToday(int dayOffset) {
		Date currentDate = new Date();

		if (dayOffset == 0) {
			return currentDate;
		}

		Calendar cal = Calendar.getInstance();

		cal.setTime(currentDate);
		cal.add(Calendar.DATE, dayOffset);

		return cal.getTime();
	}

	protected Hits search(SearchContext searchContext) throws Exception {
		TestNumericRangeTerm testNumbericRangeTerm = new TestNumericRangeTerm();

		return testNumbericRangeTerm.search(searchContext);
	}

	private Format _dateFormat;

}