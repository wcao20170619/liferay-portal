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

package com.liferay.dynamic.data.mapping.service;

import aQute.bnd.annotation.ProviderType;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for DDMFormInstanceRecordVersion. This utility wraps
 * {@link com.liferay.dynamic.data.mapping.service.impl.DDMFormInstanceRecordVersionLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDMFormInstanceRecordVersionLocalService
 * @see com.liferay.dynamic.data.mapping.service.base.DDMFormInstanceRecordVersionLocalServiceBaseImpl
 * @see com.liferay.dynamic.data.mapping.service.impl.DDMFormInstanceRecordVersionLocalServiceImpl
 * @generated
 */
@ProviderType
public class DDMFormInstanceRecordVersionLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.liferay.dynamic.data.mapping.service.impl.DDMFormInstanceRecordVersionLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* Adds the ddm form instance record version to the database. Also notifies the appropriate model listeners.
	*
	* @param ddmFormInstanceRecordVersion the ddm form instance record version
	* @return the ddm form instance record version that was added
	*/
	public static com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion addDDMFormInstanceRecordVersion(
		com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion) {
		return getService()
				   .addDDMFormInstanceRecordVersion(ddmFormInstanceRecordVersion);
	}

	/**
	* Creates a new ddm form instance record version with the primary key. Does not add the ddm form instance record version to the database.
	*
	* @param formInstanceRecordVersionId the primary key for the new ddm form instance record version
	* @return the new ddm form instance record version
	*/
	public static com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion createDDMFormInstanceRecordVersion(
		long formInstanceRecordVersionId) {
		return getService()
				   .createDDMFormInstanceRecordVersion(formInstanceRecordVersionId);
	}

	/**
	* Deletes the ddm form instance record version from the database. Also notifies the appropriate model listeners.
	*
	* @param ddmFormInstanceRecordVersion the ddm form instance record version
	* @return the ddm form instance record version that was removed
	*/
	public static com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion deleteDDMFormInstanceRecordVersion(
		com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion) {
		return getService()
				   .deleteDDMFormInstanceRecordVersion(ddmFormInstanceRecordVersion);
	}

	/**
	* Deletes the ddm form instance record version with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param formInstanceRecordVersionId the primary key of the ddm form instance record version
	* @return the ddm form instance record version that was removed
	* @throws PortalException if a ddm form instance record version with the primary key could not be found
	*/
	public static com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion deleteDDMFormInstanceRecordVersion(
		long formInstanceRecordVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .deleteDDMFormInstanceRecordVersion(formInstanceRecordVersionId);
	}

	/**
	* @throws PortalException
	*/
	public static com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().deletePersistedModel(persistedModel);
	}

	public static com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	* Performs a dynamic query on the database and returns the matching rows.
	*
	* @param dynamicQuery the dynamic query
	* @return the matching rows
	*/
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @return the range of matching rows
	*/
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {
		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching rows
	*/
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {
		return getService()
				   .dynamicQuery(dynamicQuery, start, end, orderByComparator);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @return the number of rows matching the dynamic query
	*/
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @param projection the projection to apply to the query
	* @return the number of rows matching the dynamic query
	*/
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {
		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion fetchDDMFormInstanceRecordVersion(
		long formInstanceRecordVersionId) {
		return getService()
				   .fetchDDMFormInstanceRecordVersion(formInstanceRecordVersionId);
	}

	public static com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion fetchLatestFormInstanceRecordVersion(
		long userId, long ddmFormInstanceRecordId,
		java.lang.String ddmFormInstanceRecordVersion, int status) {
		return getService()
				   .fetchLatestFormInstanceRecordVersion(userId,
			ddmFormInstanceRecordId, ddmFormInstanceRecordVersion, status);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return getService().getActionableDynamicQuery();
	}

	/**
	* Returns the ddm form instance record version with the primary key.
	*
	* @param formInstanceRecordVersionId the primary key of the ddm form instance record version
	* @return the ddm form instance record version
	* @throws PortalException if a ddm form instance record version with the primary key could not be found
	*/
	public static com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion getDDMFormInstanceRecordVersion(
		long formInstanceRecordVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .getDDMFormInstanceRecordVersion(formInstanceRecordVersionId);
	}

	/**
	* Returns a range of all the ddm form instance record versions.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of ddm form instance record versions
	* @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	* @return the range of ddm form instance record versions
	*/
	public static java.util.List<com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion> getDDMFormInstanceRecordVersions(
		int start, int end) {
		return getService().getDDMFormInstanceRecordVersions(start, end);
	}

	/**
	* Returns the number of ddm form instance record versions.
	*
	* @return the number of ddm form instance record versions
	*/
	public static int getDDMFormInstanceRecordVersionsCount() {
		return getService().getDDMFormInstanceRecordVersionsCount();
	}

	public static com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion getFormInstanceRecordVersion(
		long ddmFormInstanceRecordVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .getFormInstanceRecordVersion(ddmFormInstanceRecordVersionId);
	}

	public static com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion getFormInstanceRecordVersion(
		long ddmFormInstanceId, java.lang.String version)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .getFormInstanceRecordVersion(ddmFormInstanceId, version);
	}

	public static java.util.List<com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion> getFormInstanceRecordVersions(
		long ddmFormInstanceRecordId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion> orderByComparator) {
		return getService()
				   .getFormInstanceRecordVersions(ddmFormInstanceRecordId,
			start, end, orderByComparator);
	}

	public static int getFormInstanceRecordVersionsCount(
		long ddmFormInstanceRecordId) {
		return getService()
				   .getFormInstanceRecordVersionsCount(ddmFormInstanceRecordId);
	}

	public static com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return getService().getIndexableActionableDynamicQuery();
	}

	public static com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion getLatestFormInstanceRecordVersion(
		long ddmFormInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getLatestFormInstanceRecordVersion(ddmFormInstanceId);
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	public static java.lang.String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	* Updates the ddm form instance record version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param ddmFormInstanceRecordVersion the ddm form instance record version
	* @return the ddm form instance record version that was updated
	*/
	public static com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion updateDDMFormInstanceRecordVersion(
		com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion) {
		return getService()
				   .updateDDMFormInstanceRecordVersion(ddmFormInstanceRecordVersion);
	}

	public static DDMFormInstanceRecordVersionLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<DDMFormInstanceRecordVersionLocalService, DDMFormInstanceRecordVersionLocalService> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(DDMFormInstanceRecordVersionLocalService.class);

		ServiceTracker<DDMFormInstanceRecordVersionLocalService, DDMFormInstanceRecordVersionLocalService> serviceTracker =
			new ServiceTracker<DDMFormInstanceRecordVersionLocalService, DDMFormInstanceRecordVersionLocalService>(bundle.getBundleContext(),
				DDMFormInstanceRecordVersionLocalService.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}
}