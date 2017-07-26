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

package com.liferay.portal.kernel.search.generic;

import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.query.FieldQueryFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.RegistryUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Wade Cao
 */
@PrepareOnlyThisForTest(FieldQueryFactoryUtil.class)
@RunWith(PowerMockRunner.class)
public class BooleanQueryImplTest extends PowerMockito {

	@Test
	public void testaddExactTerm() {
		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		//test null
		Query query = booleanQueryImpl.addExactTerm(null, (String)null);

		Assert.assertNotNull(query);

		Assert.assertTrue(booleanQueryImpl.hasClauses());

		Assert.assertEquals(1, getClauseSize(booleanQueryImpl.clauses()));

		String expected = buildQueryToString("TermQueryImpl", null, null);

		Assert.assertEquals(expected, query.toString());

		//test boolean
		query = booleanQueryImpl.addExactTerm("field1", true);

		Assert.assertNotNull(query);

		Assert.assertEquals(2, getClauseSize(booleanQueryImpl.clauses()));

		expected = buildQueryToString("TermQueryImpl", "field1", "true");

		Assert.assertEquals(expected, query.toString());

		//test double
		query = booleanQueryImpl.addExactTerm("field2", 123.45);

		Assert.assertNotNull(query);

		Assert.assertEquals(3, getClauseSize(booleanQueryImpl.clauses()));

		expected = buildQueryToString("TermQueryImpl", "field2", "123.45");

		Assert.assertEquals(expected, query.toString());

		//test int
		query = booleanQueryImpl.addExactTerm("field3", 123);

		Assert.assertNotNull(query);

		Assert.assertEquals(4, getClauseSize(booleanQueryImpl.clauses()));

		expected = buildQueryToString("TermQueryImpl", "field3", "123");

		Assert.assertEquals(expected, query.toString());

		//test long
		query = booleanQueryImpl.addExactTerm("field4", (long)123456789);

		Assert.assertNotNull(query);

		Assert.assertEquals(5, getClauseSize(booleanQueryImpl.clauses()));

		expected = buildQueryToString("TermQueryImpl", "field4", "123456789");

		Assert.assertEquals(expected, query.toString());

		//test short
		query = booleanQueryImpl.addExactTerm("field5", (short)8);

		Assert.assertNotNull(query);

		Assert.assertEquals(6, getClauseSize(booleanQueryImpl.clauses()));

		expected = buildQueryToString("TermQueryImpl", "field5", "8");

		Assert.assertEquals(expected, query.toString());

		//test string
		query = booleanQueryImpl.addExactTerm("field6", "my string");

		Assert.assertNotNull(query);

		Assert.assertEquals(7, getClauseSize(booleanQueryImpl.clauses()));

		expected = buildQueryToString("TermQueryImpl", "field6", "my string");

		Assert.assertEquals(expected, query.toString());
	}

	@Test
	public void testAddNumericRangeTerm() {
		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		//test null
		List<Query> queryList = null;

		try {
			queryList = (List<Query>)booleanQueryImpl.addNumericRangeTerm(
				null, null, (Integer)null);
			Assert.fail();
		}
		catch (Exception e) {
			//null pointer exception here
		}

		Assert.assertNull(queryList);

		Assert.assertFalse(booleanQueryImpl.hasClauses());

		Assert.assertEquals(0, getClauseSize(booleanQueryImpl.clauses()));

		//test int
		queryList = (List<Query>)booleanQueryImpl.addNumericRangeTerm(
			"field1", 0, 10);

		Assert.assertNotNull(queryList);

		Assert.assertEquals(1, getClauseSize(booleanQueryImpl.clauses()));

		String expected = buildQueryRangeToString(
			"TermRangeQueryImpl", "field1", "0", "10");

		Assert.assertEquals(expected, queryList.get(0).toString());

		//test long
		queryList = (List<Query>)booleanQueryImpl.addNumericRangeTerm(
			"field2", (long)100, (long)123);

		Assert.assertNotNull(queryList);

		Assert.assertEquals(2, getClauseSize(booleanQueryImpl.clauses()));

		expected = buildQueryRangeToString(
			"TermRangeQueryImpl", "field2", "100", "123");

		Assert.assertEquals(expected, queryList.get(0).toString());

		//test short
		queryList = (List<Query>)booleanQueryImpl.addNumericRangeTerm(
			"field3", (short)1, (short)10);

		Assert.assertNotNull(queryList);

		Assert.assertEquals(3, getClauseSize(booleanQueryImpl.clauses()));

		expected = buildQueryRangeToString(
			"TermRangeQueryImpl", "field3", "1", "10");

		Assert.assertEquals(expected, queryList.get(0).toString());
	}

	@Test
	public void testAddRangeTerm() {
		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();
		Query query = null;
		//test null
		try {
			query = booleanQueryImpl.addRangeTerm(
				null, (Integer)null, (Integer)null);
			Assert.fail();
		}
		catch (Exception e) {
			//null pointer exception;
		}

		Assert.assertNull(query);

		Assert.assertFalse(booleanQueryImpl.hasClauses());

		Assert.assertEquals(0, getClauseSize(booleanQueryImpl.clauses()));

		//test int
		query = booleanQueryImpl.addRangeTerm("field1", 123, 133);

		Assert.assertNotNull(query);

		Assert.assertEquals(1, getClauseSize(booleanQueryImpl.clauses()));

		String expected = buildQueryRangeToString(
			"TermRangeQueryImpl", "field1", "123", "133");

		Assert.assertEquals(expected, query.toString());

		//test long
		query = booleanQueryImpl.addRangeTerm(
			"field2", (long)1234567, (long)1234577);

		Assert.assertNotNull(query);

		Assert.assertEquals(2, getClauseSize(booleanQueryImpl.clauses()));

		expected = buildQueryRangeToString(
			"TermRangeQueryImpl", "field2", "1234567", "1234577");

		Assert.assertEquals(expected, query.toString());

		//test short
		query = booleanQueryImpl.addRangeTerm("field3", (short)1, (short)8);

		Assert.assertNotNull(query);

		Assert.assertEquals(3, getClauseSize(booleanQueryImpl.clauses()));

		expected = buildQueryRangeToString(
			"TermRangeQueryImpl", "field3", "1", "8");

		Assert.assertEquals(expected, query.toString());

		//test string
		query = booleanQueryImpl.addRangeTerm("field4", "11", "88");

		Assert.assertNotNull(query);

		Assert.assertEquals(4, getClauseSize(booleanQueryImpl.clauses()));

		expected = buildQueryRangeToString(
			"TermRangeQueryImpl", "field4", "11", "88");

		Assert.assertEquals(expected, query.toString());
	}

	@Test
	public void testAddRequiredTerm() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		mockStatic(FieldQueryFactoryUtil.class, Mockito.CALLS_REAL_METHODS);
		stub(
			method(FieldQueryFactoryUtil.class, "createQuery")
		).toReturn(
			mock(Query.class)
		);

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		//test null
		Query query = booleanQueryImpl.addRequiredTerm(null, (Boolean)null);

		Assert.assertNotNull(query);

		Assert.assertTrue(booleanQueryImpl.hasClauses());

		Assert.assertEquals(1, getClauseSize(booleanQueryImpl.clauses()));

		//test boolean
		query = booleanQueryImpl.addRequiredTerm("field1", true);

		Assert.assertNotNull(query);

		Assert.assertEquals(2, getClauseSize(booleanQueryImpl.clauses()));

		//test double
		query = booleanQueryImpl.addRequiredTerm("field2", 8.88);

		Assert.assertNotNull(query);

		Assert.assertEquals(3, getClauseSize(booleanQueryImpl.clauses()));

		//test int
		query = booleanQueryImpl.addRequiredTerm("field3", 888);

		Assert.assertNotNull(query);

		Assert.assertEquals(4, getClauseSize(booleanQueryImpl.clauses()));

		//test long
		query = booleanQueryImpl.addRequiredTerm("field4", (long)888);

		Assert.assertNotNull(query);

		Assert.assertEquals(5, getClauseSize(booleanQueryImpl.clauses()));

		//test short
		query = booleanQueryImpl.addRequiredTerm("field5", (short)8);

		Assert.assertNotNull(query);

		Assert.assertEquals(6, getClauseSize(booleanQueryImpl.clauses()));

		//test string
		query = booleanQueryImpl.addRequiredTerm("field6", "88", true, true);

		Assert.assertNotNull(query);

		Assert.assertEquals(7, getClauseSize(booleanQueryImpl.clauses()));
	}

	@Test
	public void testAddTerm() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		mockStatic(FieldQueryFactoryUtil.class, Mockito.CALLS_REAL_METHODS);
		stub(
			method(FieldQueryFactoryUtil.class, "createQuery")
		).toReturn(
			mock(Query.class)
		);

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		//test null
		Query query = booleanQueryImpl.addTerm(null, (String)null);

		Assert.assertNotNull(query);

		Assert.assertTrue(booleanQueryImpl.hasClauses());

		Assert.assertEquals(1, getClauseSize(booleanQueryImpl.clauses()));

		//test long
		query = booleanQueryImpl.addTerm("field1", (long)888);

		Assert.assertNotNull(query);

		Assert.assertEquals(2, getClauseSize(booleanQueryImpl.clauses()));

		//test string
		query = booleanQueryImpl.addTerm("field2", "8888");

		Assert.assertNotNull(query);

		Assert.assertEquals(3, getClauseSize(booleanQueryImpl.clauses()));

		//test like to be true
		query = booleanQueryImpl.addTerm("field3", "88888", true);

		Assert.assertNotNull(query);

		Assert.assertEquals(4, getClauseSize(booleanQueryImpl.clauses()));

		//test like/parseKeywords to be true
		query = booleanQueryImpl.addTerm("field4", "88888", true, true);

		Assert.assertNotNull(query);

		Assert.assertEquals(5, getClauseSize(booleanQueryImpl.clauses()));
	}

	protected String buildQueryRangeToString(
		String className, String field, String start, String end) {

		StringBundler sb = new StringBundler(9);

		sb.append("{className=");
		sb.append(className);
		sb.append(", field=");
		sb.append(field);
		sb.append(", range=[");
		sb.append(start);
		sb.append(" TO ");
		sb.append(end);
		sb.append("]}");

		return sb.toString();
	}

	protected String buildQueryToString(
		String className, String field, String value) {

		StringBundler sb = new StringBundler(7);

		sb.append("{className=");
		sb.append(className);
		sb.append(", queryTerm={field=");
		sb.append(field);
		sb.append(", value=");
		sb.append(value);
		sb.append("}}");

		return sb.toString();
	}

	protected int getClauseSize(List<BooleanClause<Query>> clauseList) {
		return clauseList.size();
	}

}