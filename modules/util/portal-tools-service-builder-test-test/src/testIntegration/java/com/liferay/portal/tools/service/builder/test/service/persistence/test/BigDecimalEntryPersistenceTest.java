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

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchBigDecimalEntryException;
import com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.BigDecimalEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.BigDecimalEntryUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class BigDecimalEntryPersistenceTest {
	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(new LiferayIntegrationTestRule(),
			PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = BigDecimalEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<BigDecimalEntry> iterator = _bigDecimalEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BigDecimalEntry bigDecimalEntry = _persistence.create(pk);

		Assert.assertNotNull(bigDecimalEntry);

		Assert.assertEquals(bigDecimalEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		BigDecimalEntry newBigDecimalEntry = addBigDecimalEntry();

		_persistence.remove(newBigDecimalEntry);

		BigDecimalEntry existingBigDecimalEntry = _persistence.fetchByPrimaryKey(newBigDecimalEntry.getPrimaryKey());

		Assert.assertNull(existingBigDecimalEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addBigDecimalEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BigDecimalEntry newBigDecimalEntry = _persistence.create(pk);

		newBigDecimalEntry.setBigDecimalValue(new BigDecimal(
				RandomTestUtil.nextDouble()));

		_bigDecimalEntries.add(_persistence.update(newBigDecimalEntry));

		BigDecimalEntry existingBigDecimalEntry = _persistence.findByPrimaryKey(newBigDecimalEntry.getPrimaryKey());

		Assert.assertEquals(existingBigDecimalEntry.getBigDecimalEntryId(),
			newBigDecimalEntry.getBigDecimalEntryId());
		Assert.assertEquals(existingBigDecimalEntry.getBigDecimalValue(),
			newBigDecimalEntry.getBigDecimalValue());
	}

	@Test
	public void testCountByBigDecimalValue() throws Exception {
		_persistence.countByBigDecimalValue((BigDecimal)null);

		_persistence.countByBigDecimalValue((BigDecimal)null);
	}

	@Test
	public void testCountByGtBigDecimalValue() throws Exception {
		_persistence.countByGtBigDecimalValue((BigDecimal)null);

		_persistence.countByGtBigDecimalValue((BigDecimal)null);
	}

	@Test
	public void testCountByLtBigDecimalValue() throws Exception {
		_persistence.countByLtBigDecimalValue((BigDecimal)null);

		_persistence.countByLtBigDecimalValue((BigDecimal)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		BigDecimalEntry newBigDecimalEntry = addBigDecimalEntry();

		BigDecimalEntry existingBigDecimalEntry = _persistence.findByPrimaryKey(newBigDecimalEntry.getPrimaryKey());

		Assert.assertEquals(existingBigDecimalEntry, newBigDecimalEntry);
	}

	@Test(expected = NoSuchBigDecimalEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			getOrderByComparator());
	}

	protected OrderByComparator<BigDecimalEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create("BigDecimalEntry",
			"bigDecimalEntryId", true, "bigDecimalValue", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		BigDecimalEntry newBigDecimalEntry = addBigDecimalEntry();

		BigDecimalEntry existingBigDecimalEntry = _persistence.fetchByPrimaryKey(newBigDecimalEntry.getPrimaryKey());

		Assert.assertEquals(existingBigDecimalEntry, newBigDecimalEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BigDecimalEntry missingBigDecimalEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingBigDecimalEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {
		BigDecimalEntry newBigDecimalEntry1 = addBigDecimalEntry();
		BigDecimalEntry newBigDecimalEntry2 = addBigDecimalEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBigDecimalEntry1.getPrimaryKey());
		primaryKeys.add(newBigDecimalEntry2.getPrimaryKey());

		Map<Serializable, BigDecimalEntry> bigDecimalEntries = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, bigDecimalEntries.size());
		Assert.assertEquals(newBigDecimalEntry1,
			bigDecimalEntries.get(newBigDecimalEntry1.getPrimaryKey()));
		Assert.assertEquals(newBigDecimalEntry2,
			bigDecimalEntries.get(newBigDecimalEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {
		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, BigDecimalEntry> bigDecimalEntries = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(bigDecimalEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {
		BigDecimalEntry newBigDecimalEntry = addBigDecimalEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBigDecimalEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, BigDecimalEntry> bigDecimalEntries = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, bigDecimalEntries.size());
		Assert.assertEquals(newBigDecimalEntry,
			bigDecimalEntries.get(newBigDecimalEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys()
		throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, BigDecimalEntry> bigDecimalEntries = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(bigDecimalEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey()
		throws Exception {
		BigDecimalEntry newBigDecimalEntry = addBigDecimalEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBigDecimalEntry.getPrimaryKey());

		Map<Serializable, BigDecimalEntry> bigDecimalEntries = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, bigDecimalEntries.size());
		Assert.assertEquals(newBigDecimalEntry,
			bigDecimalEntries.get(newBigDecimalEntry.getPrimaryKey()));
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		BigDecimalEntry newBigDecimalEntry = addBigDecimalEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(BigDecimalEntry.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq("bigDecimalEntryId",
				newBigDecimalEntry.getBigDecimalEntryId()));

		List<BigDecimalEntry> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		BigDecimalEntry existingBigDecimalEntry = result.get(0);

		Assert.assertEquals(existingBigDecimalEntry, newBigDecimalEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(BigDecimalEntry.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq("bigDecimalEntryId",
				RandomTestUtil.nextLong()));

		List<BigDecimalEntry> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		BigDecimalEntry newBigDecimalEntry = addBigDecimalEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(BigDecimalEntry.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property(
				"bigDecimalEntryId"));

		Object newBigDecimalEntryId = newBigDecimalEntry.getBigDecimalEntryId();

		dynamicQuery.add(RestrictionsFactoryUtil.in("bigDecimalEntryId",
				new Object[] { newBigDecimalEntryId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingBigDecimalEntryId = result.get(0);

		Assert.assertEquals(existingBigDecimalEntryId, newBigDecimalEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(BigDecimalEntry.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property(
				"bigDecimalEntryId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in("bigDecimalEntryId",
				new Object[] { RandomTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected BigDecimalEntry addBigDecimalEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BigDecimalEntry bigDecimalEntry = _persistence.create(pk);

		bigDecimalEntry.setBigDecimalValue(new BigDecimal(
				RandomTestUtil.nextDouble()));

		_bigDecimalEntries.add(_persistence.update(bigDecimalEntry));

		return bigDecimalEntry;
	}

	private List<BigDecimalEntry> _bigDecimalEntries = new ArrayList<BigDecimalEntry>();
	private BigDecimalEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;
}