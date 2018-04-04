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

package com.liferay.portal.tools.service.builder.test.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.osgi.util.tracker.ServiceTracker;

import java.math.BigDecimal;

import java.util.List;

/**
 * The persistence utility for the big decimal entry service. This utility wraps {@link com.liferay.portal.tools.service.builder.test.service.persistence.impl.BigDecimalEntryPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see BigDecimalEntryPersistence
 * @see com.liferay.portal.tools.service.builder.test.service.persistence.impl.BigDecimalEntryPersistenceImpl
 * @generated
 */
@ProviderType
public class BigDecimalEntryUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(BigDecimalEntry bigDecimalEntry) {
		getPersistence().clearCache(bigDecimalEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<BigDecimalEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<BigDecimalEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<BigDecimalEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<BigDecimalEntry> orderByComparator) {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static BigDecimalEntry update(BigDecimalEntry bigDecimalEntry) {
		return getPersistence().update(bigDecimalEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static BigDecimalEntry update(BigDecimalEntry bigDecimalEntry,
		ServiceContext serviceContext) {
		return getPersistence().update(bigDecimalEntry, serviceContext);
	}

	/**
	* Returns all the big decimal entries where bigDecimalValue = &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @return the matching big decimal entries
	*/
	public static List<BigDecimalEntry> findByBigDecimalValue(
		BigDecimal bigDecimalValue) {
		return getPersistence().findByBigDecimalValue(bigDecimalValue);
	}

	/**
	* Returns a range of all the big decimal entries where bigDecimalValue = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link BigDecimalEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param bigDecimalValue the big decimal value
	* @param start the lower bound of the range of big decimal entries
	* @param end the upper bound of the range of big decimal entries (not inclusive)
	* @return the range of matching big decimal entries
	*/
	public static List<BigDecimalEntry> findByBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end) {
		return getPersistence()
				   .findByBigDecimalValue(bigDecimalValue, start, end);
	}

	/**
	* Returns an ordered range of all the big decimal entries where bigDecimalValue = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link BigDecimalEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param bigDecimalValue the big decimal value
	* @param start the lower bound of the range of big decimal entries
	* @param end the upper bound of the range of big decimal entries (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching big decimal entries
	*/
	public static List<BigDecimalEntry> findByBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end,
		OrderByComparator<BigDecimalEntry> orderByComparator) {
		return getPersistence()
				   .findByBigDecimalValue(bigDecimalValue, start, end,
			orderByComparator);
	}

	/**
	* Returns an ordered range of all the big decimal entries where bigDecimalValue = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link BigDecimalEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param bigDecimalValue the big decimal value
	* @param start the lower bound of the range of big decimal entries
	* @param end the upper bound of the range of big decimal entries (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching big decimal entries
	*/
	public static List<BigDecimalEntry> findByBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end,
		OrderByComparator<BigDecimalEntry> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByBigDecimalValue(bigDecimalValue, start, end,
			orderByComparator, retrieveFromCache);
	}

	/**
	* Returns the first big decimal entry in the ordered set where bigDecimalValue = &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching big decimal entry
	* @throws NoSuchBigDecimalEntryException if a matching big decimal entry could not be found
	*/
	public static BigDecimalEntry findByBigDecimalValue_First(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.NoSuchBigDecimalEntryException {
		return getPersistence()
				   .findByBigDecimalValue_First(bigDecimalValue,
			orderByComparator);
	}

	/**
	* Returns the first big decimal entry in the ordered set where bigDecimalValue = &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching big decimal entry, or <code>null</code> if a matching big decimal entry could not be found
	*/
	public static BigDecimalEntry fetchByBigDecimalValue_First(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator) {
		return getPersistence()
				   .fetchByBigDecimalValue_First(bigDecimalValue,
			orderByComparator);
	}

	/**
	* Returns the last big decimal entry in the ordered set where bigDecimalValue = &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching big decimal entry
	* @throws NoSuchBigDecimalEntryException if a matching big decimal entry could not be found
	*/
	public static BigDecimalEntry findByBigDecimalValue_Last(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.NoSuchBigDecimalEntryException {
		return getPersistence()
				   .findByBigDecimalValue_Last(bigDecimalValue,
			orderByComparator);
	}

	/**
	* Returns the last big decimal entry in the ordered set where bigDecimalValue = &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching big decimal entry, or <code>null</code> if a matching big decimal entry could not be found
	*/
	public static BigDecimalEntry fetchByBigDecimalValue_Last(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator) {
		return getPersistence()
				   .fetchByBigDecimalValue_Last(bigDecimalValue,
			orderByComparator);
	}

	/**
	* Returns the big decimal entries before and after the current big decimal entry in the ordered set where bigDecimalValue = &#63;.
	*
	* @param bigDecimalEntryId the primary key of the current big decimal entry
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next big decimal entry
	* @throws NoSuchBigDecimalEntryException if a big decimal entry with the primary key could not be found
	*/
	public static BigDecimalEntry[] findByBigDecimalValue_PrevAndNext(
		long bigDecimalEntryId, BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.NoSuchBigDecimalEntryException {
		return getPersistence()
				   .findByBigDecimalValue_PrevAndNext(bigDecimalEntryId,
			bigDecimalValue, orderByComparator);
	}

	/**
	* Removes all the big decimal entries where bigDecimalValue = &#63; from the database.
	*
	* @param bigDecimalValue the big decimal value
	*/
	public static void removeByBigDecimalValue(BigDecimal bigDecimalValue) {
		getPersistence().removeByBigDecimalValue(bigDecimalValue);
	}

	/**
	* Returns the number of big decimal entries where bigDecimalValue = &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @return the number of matching big decimal entries
	*/
	public static int countByBigDecimalValue(BigDecimal bigDecimalValue) {
		return getPersistence().countByBigDecimalValue(bigDecimalValue);
	}

	/**
	* Returns all the big decimal entries where bigDecimalValue &gt; &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @return the matching big decimal entries
	*/
	public static List<BigDecimalEntry> findByGtBigDecimalValue(
		BigDecimal bigDecimalValue) {
		return getPersistence().findByGtBigDecimalValue(bigDecimalValue);
	}

	/**
	* Returns a range of all the big decimal entries where bigDecimalValue &gt; &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link BigDecimalEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param bigDecimalValue the big decimal value
	* @param start the lower bound of the range of big decimal entries
	* @param end the upper bound of the range of big decimal entries (not inclusive)
	* @return the range of matching big decimal entries
	*/
	public static List<BigDecimalEntry> findByGtBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end) {
		return getPersistence()
				   .findByGtBigDecimalValue(bigDecimalValue, start, end);
	}

	/**
	* Returns an ordered range of all the big decimal entries where bigDecimalValue &gt; &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link BigDecimalEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param bigDecimalValue the big decimal value
	* @param start the lower bound of the range of big decimal entries
	* @param end the upper bound of the range of big decimal entries (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching big decimal entries
	*/
	public static List<BigDecimalEntry> findByGtBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end,
		OrderByComparator<BigDecimalEntry> orderByComparator) {
		return getPersistence()
				   .findByGtBigDecimalValue(bigDecimalValue, start, end,
			orderByComparator);
	}

	/**
	* Returns an ordered range of all the big decimal entries where bigDecimalValue &gt; &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link BigDecimalEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param bigDecimalValue the big decimal value
	* @param start the lower bound of the range of big decimal entries
	* @param end the upper bound of the range of big decimal entries (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching big decimal entries
	*/
	public static List<BigDecimalEntry> findByGtBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end,
		OrderByComparator<BigDecimalEntry> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByGtBigDecimalValue(bigDecimalValue, start, end,
			orderByComparator, retrieveFromCache);
	}

	/**
	* Returns the first big decimal entry in the ordered set where bigDecimalValue &gt; &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching big decimal entry
	* @throws NoSuchBigDecimalEntryException if a matching big decimal entry could not be found
	*/
	public static BigDecimalEntry findByGtBigDecimalValue_First(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.NoSuchBigDecimalEntryException {
		return getPersistence()
				   .findByGtBigDecimalValue_First(bigDecimalValue,
			orderByComparator);
	}

	/**
	* Returns the first big decimal entry in the ordered set where bigDecimalValue &gt; &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching big decimal entry, or <code>null</code> if a matching big decimal entry could not be found
	*/
	public static BigDecimalEntry fetchByGtBigDecimalValue_First(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator) {
		return getPersistence()
				   .fetchByGtBigDecimalValue_First(bigDecimalValue,
			orderByComparator);
	}

	/**
	* Returns the last big decimal entry in the ordered set where bigDecimalValue &gt; &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching big decimal entry
	* @throws NoSuchBigDecimalEntryException if a matching big decimal entry could not be found
	*/
	public static BigDecimalEntry findByGtBigDecimalValue_Last(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.NoSuchBigDecimalEntryException {
		return getPersistence()
				   .findByGtBigDecimalValue_Last(bigDecimalValue,
			orderByComparator);
	}

	/**
	* Returns the last big decimal entry in the ordered set where bigDecimalValue &gt; &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching big decimal entry, or <code>null</code> if a matching big decimal entry could not be found
	*/
	public static BigDecimalEntry fetchByGtBigDecimalValue_Last(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator) {
		return getPersistence()
				   .fetchByGtBigDecimalValue_Last(bigDecimalValue,
			orderByComparator);
	}

	/**
	* Returns the big decimal entries before and after the current big decimal entry in the ordered set where bigDecimalValue &gt; &#63;.
	*
	* @param bigDecimalEntryId the primary key of the current big decimal entry
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next big decimal entry
	* @throws NoSuchBigDecimalEntryException if a big decimal entry with the primary key could not be found
	*/
	public static BigDecimalEntry[] findByGtBigDecimalValue_PrevAndNext(
		long bigDecimalEntryId, BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.NoSuchBigDecimalEntryException {
		return getPersistence()
				   .findByGtBigDecimalValue_PrevAndNext(bigDecimalEntryId,
			bigDecimalValue, orderByComparator);
	}

	/**
	* Removes all the big decimal entries where bigDecimalValue &gt; &#63; from the database.
	*
	* @param bigDecimalValue the big decimal value
	*/
	public static void removeByGtBigDecimalValue(BigDecimal bigDecimalValue) {
		getPersistence().removeByGtBigDecimalValue(bigDecimalValue);
	}

	/**
	* Returns the number of big decimal entries where bigDecimalValue &gt; &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @return the number of matching big decimal entries
	*/
	public static int countByGtBigDecimalValue(BigDecimal bigDecimalValue) {
		return getPersistence().countByGtBigDecimalValue(bigDecimalValue);
	}

	/**
	* Returns all the big decimal entries where bigDecimalValue &lt; &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @return the matching big decimal entries
	*/
	public static List<BigDecimalEntry> findByLtBigDecimalValue(
		BigDecimal bigDecimalValue) {
		return getPersistence().findByLtBigDecimalValue(bigDecimalValue);
	}

	/**
	* Returns a range of all the big decimal entries where bigDecimalValue &lt; &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link BigDecimalEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param bigDecimalValue the big decimal value
	* @param start the lower bound of the range of big decimal entries
	* @param end the upper bound of the range of big decimal entries (not inclusive)
	* @return the range of matching big decimal entries
	*/
	public static List<BigDecimalEntry> findByLtBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end) {
		return getPersistence()
				   .findByLtBigDecimalValue(bigDecimalValue, start, end);
	}

	/**
	* Returns an ordered range of all the big decimal entries where bigDecimalValue &lt; &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link BigDecimalEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param bigDecimalValue the big decimal value
	* @param start the lower bound of the range of big decimal entries
	* @param end the upper bound of the range of big decimal entries (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching big decimal entries
	*/
	public static List<BigDecimalEntry> findByLtBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end,
		OrderByComparator<BigDecimalEntry> orderByComparator) {
		return getPersistence()
				   .findByLtBigDecimalValue(bigDecimalValue, start, end,
			orderByComparator);
	}

	/**
	* Returns an ordered range of all the big decimal entries where bigDecimalValue &lt; &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link BigDecimalEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param bigDecimalValue the big decimal value
	* @param start the lower bound of the range of big decimal entries
	* @param end the upper bound of the range of big decimal entries (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching big decimal entries
	*/
	public static List<BigDecimalEntry> findByLtBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end,
		OrderByComparator<BigDecimalEntry> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByLtBigDecimalValue(bigDecimalValue, start, end,
			orderByComparator, retrieveFromCache);
	}

	/**
	* Returns the first big decimal entry in the ordered set where bigDecimalValue &lt; &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching big decimal entry
	* @throws NoSuchBigDecimalEntryException if a matching big decimal entry could not be found
	*/
	public static BigDecimalEntry findByLtBigDecimalValue_First(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.NoSuchBigDecimalEntryException {
		return getPersistence()
				   .findByLtBigDecimalValue_First(bigDecimalValue,
			orderByComparator);
	}

	/**
	* Returns the first big decimal entry in the ordered set where bigDecimalValue &lt; &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching big decimal entry, or <code>null</code> if a matching big decimal entry could not be found
	*/
	public static BigDecimalEntry fetchByLtBigDecimalValue_First(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator) {
		return getPersistence()
				   .fetchByLtBigDecimalValue_First(bigDecimalValue,
			orderByComparator);
	}

	/**
	* Returns the last big decimal entry in the ordered set where bigDecimalValue &lt; &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching big decimal entry
	* @throws NoSuchBigDecimalEntryException if a matching big decimal entry could not be found
	*/
	public static BigDecimalEntry findByLtBigDecimalValue_Last(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.NoSuchBigDecimalEntryException {
		return getPersistence()
				   .findByLtBigDecimalValue_Last(bigDecimalValue,
			orderByComparator);
	}

	/**
	* Returns the last big decimal entry in the ordered set where bigDecimalValue &lt; &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching big decimal entry, or <code>null</code> if a matching big decimal entry could not be found
	*/
	public static BigDecimalEntry fetchByLtBigDecimalValue_Last(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator) {
		return getPersistence()
				   .fetchByLtBigDecimalValue_Last(bigDecimalValue,
			orderByComparator);
	}

	/**
	* Returns the big decimal entries before and after the current big decimal entry in the ordered set where bigDecimalValue &lt; &#63;.
	*
	* @param bigDecimalEntryId the primary key of the current big decimal entry
	* @param bigDecimalValue the big decimal value
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next big decimal entry
	* @throws NoSuchBigDecimalEntryException if a big decimal entry with the primary key could not be found
	*/
	public static BigDecimalEntry[] findByLtBigDecimalValue_PrevAndNext(
		long bigDecimalEntryId, BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.NoSuchBigDecimalEntryException {
		return getPersistence()
				   .findByLtBigDecimalValue_PrevAndNext(bigDecimalEntryId,
			bigDecimalValue, orderByComparator);
	}

	/**
	* Removes all the big decimal entries where bigDecimalValue &lt; &#63; from the database.
	*
	* @param bigDecimalValue the big decimal value
	*/
	public static void removeByLtBigDecimalValue(BigDecimal bigDecimalValue) {
		getPersistence().removeByLtBigDecimalValue(bigDecimalValue);
	}

	/**
	* Returns the number of big decimal entries where bigDecimalValue &lt; &#63;.
	*
	* @param bigDecimalValue the big decimal value
	* @return the number of matching big decimal entries
	*/
	public static int countByLtBigDecimalValue(BigDecimal bigDecimalValue) {
		return getPersistence().countByLtBigDecimalValue(bigDecimalValue);
	}

	/**
	* Caches the big decimal entry in the entity cache if it is enabled.
	*
	* @param bigDecimalEntry the big decimal entry
	*/
	public static void cacheResult(BigDecimalEntry bigDecimalEntry) {
		getPersistence().cacheResult(bigDecimalEntry);
	}

	/**
	* Caches the big decimal entries in the entity cache if it is enabled.
	*
	* @param bigDecimalEntries the big decimal entries
	*/
	public static void cacheResult(List<BigDecimalEntry> bigDecimalEntries) {
		getPersistence().cacheResult(bigDecimalEntries);
	}

	/**
	* Creates a new big decimal entry with the primary key. Does not add the big decimal entry to the database.
	*
	* @param bigDecimalEntryId the primary key for the new big decimal entry
	* @return the new big decimal entry
	*/
	public static BigDecimalEntry create(long bigDecimalEntryId) {
		return getPersistence().create(bigDecimalEntryId);
	}

	/**
	* Removes the big decimal entry with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param bigDecimalEntryId the primary key of the big decimal entry
	* @return the big decimal entry that was removed
	* @throws NoSuchBigDecimalEntryException if a big decimal entry with the primary key could not be found
	*/
	public static BigDecimalEntry remove(long bigDecimalEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.NoSuchBigDecimalEntryException {
		return getPersistence().remove(bigDecimalEntryId);
	}

	public static BigDecimalEntry updateImpl(BigDecimalEntry bigDecimalEntry) {
		return getPersistence().updateImpl(bigDecimalEntry);
	}

	/**
	* Returns the big decimal entry with the primary key or throws a {@link NoSuchBigDecimalEntryException} if it could not be found.
	*
	* @param bigDecimalEntryId the primary key of the big decimal entry
	* @return the big decimal entry
	* @throws NoSuchBigDecimalEntryException if a big decimal entry with the primary key could not be found
	*/
	public static BigDecimalEntry findByPrimaryKey(long bigDecimalEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.NoSuchBigDecimalEntryException {
		return getPersistence().findByPrimaryKey(bigDecimalEntryId);
	}

	/**
	* Returns the big decimal entry with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param bigDecimalEntryId the primary key of the big decimal entry
	* @return the big decimal entry, or <code>null</code> if a big decimal entry with the primary key could not be found
	*/
	public static BigDecimalEntry fetchByPrimaryKey(long bigDecimalEntryId) {
		return getPersistence().fetchByPrimaryKey(bigDecimalEntryId);
	}

	public static java.util.Map<java.io.Serializable, BigDecimalEntry> fetchByPrimaryKeys(
		java.util.Set<java.io.Serializable> primaryKeys) {
		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	* Returns all the big decimal entries.
	*
	* @return the big decimal entries
	*/
	public static List<BigDecimalEntry> findAll() {
		return getPersistence().findAll();
	}

	/**
	* Returns a range of all the big decimal entries.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link BigDecimalEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of big decimal entries
	* @param end the upper bound of the range of big decimal entries (not inclusive)
	* @return the range of big decimal entries
	*/
	public static List<BigDecimalEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	* Returns an ordered range of all the big decimal entries.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link BigDecimalEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of big decimal entries
	* @param end the upper bound of the range of big decimal entries (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of big decimal entries
	*/
	public static List<BigDecimalEntry> findAll(int start, int end,
		OrderByComparator<BigDecimalEntry> orderByComparator) {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the big decimal entries.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link BigDecimalEntryModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of big decimal entries
	* @param end the upper bound of the range of big decimal entries (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of big decimal entries
	*/
	public static List<BigDecimalEntry> findAll(int start, int end,
		OrderByComparator<BigDecimalEntry> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findAll(start, end, orderByComparator, retrieveFromCache);
	}

	/**
	* Removes all the big decimal entries from the database.
	*/
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of big decimal entries.
	*
	* @return the number of big decimal entries
	*/
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static BigDecimalEntryPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<BigDecimalEntryPersistence, BigDecimalEntryPersistence> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(BigDecimalEntryPersistence.class);

		ServiceTracker<BigDecimalEntryPersistence, BigDecimalEntryPersistence> serviceTracker =
			new ServiceTracker<BigDecimalEntryPersistence, BigDecimalEntryPersistence>(bundle.getBundleContext(),
				BigDecimalEntryPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}
}