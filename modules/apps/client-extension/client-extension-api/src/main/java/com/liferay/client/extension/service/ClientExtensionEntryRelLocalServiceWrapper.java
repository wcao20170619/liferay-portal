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

package com.liferay.client.extension.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ClientExtensionEntryRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ClientExtensionEntryRelLocalService
 * @generated
 */
public class ClientExtensionEntryRelLocalServiceWrapper
	implements ClientExtensionEntryRelLocalService,
			   ServiceWrapper<ClientExtensionEntryRelLocalService> {

	public ClientExtensionEntryRelLocalServiceWrapper() {
		this(null);
	}

	public ClientExtensionEntryRelLocalServiceWrapper(
		ClientExtensionEntryRelLocalService
			clientExtensionEntryRelLocalService) {

		_clientExtensionEntryRelLocalService =
			clientExtensionEntryRelLocalService;
	}

	/**
	 * Adds the client extension entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ClientExtensionEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param clientExtensionEntryRel the client extension entry rel
	 * @return the client extension entry rel that was added
	 */
	@Override
	public com.liferay.client.extension.model.ClientExtensionEntryRel
		addClientExtensionEntryRel(
			com.liferay.client.extension.model.ClientExtensionEntryRel
				clientExtensionEntryRel) {

		return _clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			clientExtensionEntryRel);
	}

	@Override
	public com.liferay.client.extension.model.ClientExtensionEntryRel
			addClientExtensionEntryRel(
				long userId, long classNameId, long classPK,
				String cetExternalReferenceCode, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			userId, classNameId, classPK, cetExternalReferenceCode, type);
	}

	/**
	 * Creates a new client extension entry rel with the primary key. Does not add the client extension entry rel to the database.
	 *
	 * @param clientExtensionEntryRelId the primary key for the new client extension entry rel
	 * @return the new client extension entry rel
	 */
	@Override
	public com.liferay.client.extension.model.ClientExtensionEntryRel
		createClientExtensionEntryRel(long clientExtensionEntryRelId) {

		return _clientExtensionEntryRelLocalService.
			createClientExtensionEntryRel(clientExtensionEntryRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the client extension entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ClientExtensionEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param clientExtensionEntryRel the client extension entry rel
	 * @return the client extension entry rel that was removed
	 */
	@Override
	public com.liferay.client.extension.model.ClientExtensionEntryRel
		deleteClientExtensionEntryRel(
			com.liferay.client.extension.model.ClientExtensionEntryRel
				clientExtensionEntryRel) {

		return _clientExtensionEntryRelLocalService.
			deleteClientExtensionEntryRel(clientExtensionEntryRel);
	}

	/**
	 * Deletes the client extension entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ClientExtensionEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param clientExtensionEntryRelId the primary key of the client extension entry rel
	 * @return the client extension entry rel that was removed
	 * @throws PortalException if a client extension entry rel with the primary key could not be found
	 */
	@Override
	public com.liferay.client.extension.model.ClientExtensionEntryRel
			deleteClientExtensionEntryRel(long clientExtensionEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.
			deleteClientExtensionEntryRel(clientExtensionEntryRelId);
	}

	@Override
	public void deleteClientExtensionEntryRels(long classNameId, long classPK) {
		_clientExtensionEntryRelLocalService.deleteClientExtensionEntryRels(
			classNameId, classPK);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _clientExtensionEntryRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _clientExtensionEntryRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _clientExtensionEntryRelLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _clientExtensionEntryRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.client.extension.model.impl.ClientExtensionEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _clientExtensionEntryRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.client.extension.model.impl.ClientExtensionEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _clientExtensionEntryRelLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _clientExtensionEntryRelLocalService.dynamicQueryCount(
			dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _clientExtensionEntryRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.client.extension.model.ClientExtensionEntryRel
		fetchClientExtensionEntryRel(long clientExtensionEntryRelId) {

		return _clientExtensionEntryRelLocalService.
			fetchClientExtensionEntryRel(clientExtensionEntryRelId);
	}

	@Override
	public com.liferay.client.extension.model.ClientExtensionEntryRel
		fetchClientExtensionEntryRel(
			long classNameId, long classPK, String type) {

		return _clientExtensionEntryRelLocalService.
			fetchClientExtensionEntryRel(classNameId, classPK, type);
	}

	/**
	 * Returns the client extension entry rel with the matching external reference code and company.
	 *
	 * @param companyId the primary key of the company
	 * @param externalReferenceCode the client extension entry rel's external reference code
	 * @return the matching client extension entry rel, or <code>null</code> if a matching client extension entry rel could not be found
	 */
	@Override
	public com.liferay.client.extension.model.ClientExtensionEntryRel
		fetchClientExtensionEntryRelByExternalReferenceCode(
			long companyId, String externalReferenceCode) {

		return _clientExtensionEntryRelLocalService.
			fetchClientExtensionEntryRelByExternalReferenceCode(
				companyId, externalReferenceCode);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #fetchClientExtensionEntryRelByExternalReferenceCode(long, String)}
	 */
	@Deprecated
	@Override
	public com.liferay.client.extension.model.ClientExtensionEntryRel
		fetchClientExtensionEntryRelByReferenceCode(
			long companyId, String externalReferenceCode) {

		return _clientExtensionEntryRelLocalService.
			fetchClientExtensionEntryRelByReferenceCode(
				companyId, externalReferenceCode);
	}

	/**
	 * Returns the client extension entry rel with the matching UUID and company.
	 *
	 * @param uuid the client extension entry rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching client extension entry rel, or <code>null</code> if a matching client extension entry rel could not be found
	 */
	@Override
	public com.liferay.client.extension.model.ClientExtensionEntryRel
		fetchClientExtensionEntryRelByUuidAndCompanyId(
			String uuid, long companyId) {

		return _clientExtensionEntryRelLocalService.
			fetchClientExtensionEntryRelByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _clientExtensionEntryRelLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the client extension entry rel with the primary key.
	 *
	 * @param clientExtensionEntryRelId the primary key of the client extension entry rel
	 * @return the client extension entry rel
	 * @throws PortalException if a client extension entry rel with the primary key could not be found
	 */
	@Override
	public com.liferay.client.extension.model.ClientExtensionEntryRel
			getClientExtensionEntryRel(long clientExtensionEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.getClientExtensionEntryRel(
			clientExtensionEntryRelId);
	}

	/**
	 * Returns the client extension entry rel with the matching external reference code and company.
	 *
	 * @param companyId the primary key of the company
	 * @param externalReferenceCode the client extension entry rel's external reference code
	 * @return the matching client extension entry rel
	 * @throws PortalException if a matching client extension entry rel could not be found
	 */
	@Override
	public com.liferay.client.extension.model.ClientExtensionEntryRel
			getClientExtensionEntryRelByExternalReferenceCode(
				long companyId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.
			getClientExtensionEntryRelByExternalReferenceCode(
				companyId, externalReferenceCode);
	}

	/**
	 * Returns the client extension entry rel with the matching UUID and company.
	 *
	 * @param uuid the client extension entry rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching client extension entry rel
	 * @throws PortalException if a matching client extension entry rel could not be found
	 */
	@Override
	public com.liferay.client.extension.model.ClientExtensionEntryRel
			getClientExtensionEntryRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.
			getClientExtensionEntryRelByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the client extension entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.client.extension.model.impl.ClientExtensionEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of client extension entry rels
	 * @param end the upper bound of the range of client extension entry rels (not inclusive)
	 * @return the range of client extension entry rels
	 */
	@Override
	public java.util.List
		<com.liferay.client.extension.model.ClientExtensionEntryRel>
			getClientExtensionEntryRels(int start, int end) {

		return _clientExtensionEntryRelLocalService.getClientExtensionEntryRels(
			start, end);
	}

	@Override
	public java.util.List
		<com.liferay.client.extension.model.ClientExtensionEntryRel>
			getClientExtensionEntryRels(long classNameId, long classPK) {

		return _clientExtensionEntryRelLocalService.getClientExtensionEntryRels(
			classNameId, classPK);
	}

	@Override
	public java.util.List
		<com.liferay.client.extension.model.ClientExtensionEntryRel>
			getClientExtensionEntryRels(
				long classNameId, long classPK, String type, int start,
				int end) {

		return _clientExtensionEntryRelLocalService.getClientExtensionEntryRels(
			classNameId, classPK, type, start, end);
	}

	/**
	 * Returns the number of client extension entry rels.
	 *
	 * @return the number of client extension entry rels
	 */
	@Override
	public int getClientExtensionEntryRelsCount() {
		return _clientExtensionEntryRelLocalService.
			getClientExtensionEntryRelsCount();
	}

	@Override
	public int getClientExtensionEntryRelsCount(
		long classNameId, long classPK, String type) {

		return _clientExtensionEntryRelLocalService.
			getClientExtensionEntryRelsCount(classNameId, classPK, type);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _clientExtensionEntryRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _clientExtensionEntryRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _clientExtensionEntryRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the client extension entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ClientExtensionEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param clientExtensionEntryRel the client extension entry rel
	 * @return the client extension entry rel that was updated
	 */
	@Override
	public com.liferay.client.extension.model.ClientExtensionEntryRel
		updateClientExtensionEntryRel(
			com.liferay.client.extension.model.ClientExtensionEntryRel
				clientExtensionEntryRel) {

		return _clientExtensionEntryRelLocalService.
			updateClientExtensionEntryRel(clientExtensionEntryRel);
	}

	@Override
	public ClientExtensionEntryRelLocalService getWrappedService() {
		return _clientExtensionEntryRelLocalService;
	}

	@Override
	public void setWrappedService(
		ClientExtensionEntryRelLocalService
			clientExtensionEntryRelLocalService) {

		_clientExtensionEntryRelLocalService =
			clientExtensionEntryRelLocalService;
	}

	private ClientExtensionEntryRelLocalService
		_clientExtensionEntryRelLocalService;

}