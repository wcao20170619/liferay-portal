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

package com.liferay.oauth2.provider.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link OAuth2AuthorizationLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2AuthorizationLocalService
 * @generated
 */
@ProviderType
public class OAuth2AuthorizationLocalServiceWrapper
	implements OAuth2AuthorizationLocalService,
		ServiceWrapper<OAuth2AuthorizationLocalService> {
	public OAuth2AuthorizationLocalServiceWrapper(
		OAuth2AuthorizationLocalService oAuth2AuthorizationLocalService) {
		_oAuth2AuthorizationLocalService = oAuth2AuthorizationLocalService;
	}

	/**
	* Adds the o auth2 authorization to the database. Also notifies the appropriate model listeners.
	*
	* @param oAuth2Authorization the o auth2 authorization
	* @return the o auth2 authorization that was added
	*/
	@Override
	public com.liferay.oauth2.provider.model.OAuth2Authorization addOAuth2Authorization(
		com.liferay.oauth2.provider.model.OAuth2Authorization oAuth2Authorization) {
		return _oAuth2AuthorizationLocalService.addOAuth2Authorization(oAuth2Authorization);
	}

	@Override
	public void addOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId, long oAuth2AuthorizationId) {
		_oAuth2AuthorizationLocalService.addOAuth2ScopeGrantOAuth2Authorization(oAuth2ScopeGrantId,
			oAuth2AuthorizationId);
	}

	@Override
	public void addOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId,
		com.liferay.oauth2.provider.model.OAuth2Authorization oAuth2Authorization) {
		_oAuth2AuthorizationLocalService.addOAuth2ScopeGrantOAuth2Authorization(oAuth2ScopeGrantId,
			oAuth2Authorization);
	}

	@Override
	public void addOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId,
		java.util.List<com.liferay.oauth2.provider.model.OAuth2Authorization> oAuth2Authorizations) {
		_oAuth2AuthorizationLocalService.addOAuth2ScopeGrantOAuth2Authorizations(oAuth2ScopeGrantId,
			oAuth2Authorizations);
	}

	@Override
	public void addOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId, long[] oAuth2AuthorizationIds) {
		_oAuth2AuthorizationLocalService.addOAuth2ScopeGrantOAuth2Authorizations(oAuth2ScopeGrantId,
			oAuth2AuthorizationIds);
	}

	@Override
	public void clearOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId) {
		_oAuth2AuthorizationLocalService.clearOAuth2ScopeGrantOAuth2Authorizations(oAuth2ScopeGrantId);
	}

	/**
	* Creates a new o auth2 authorization with the primary key. Does not add the o auth2 authorization to the database.
	*
	* @param oAuth2AuthorizationId the primary key for the new o auth2 authorization
	* @return the new o auth2 authorization
	*/
	@Override
	public com.liferay.oauth2.provider.model.OAuth2Authorization createOAuth2Authorization(
		long oAuth2AuthorizationId) {
		return _oAuth2AuthorizationLocalService.createOAuth2Authorization(oAuth2AuthorizationId);
	}

	/**
	* Deletes the o auth2 authorization with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param oAuth2AuthorizationId the primary key of the o auth2 authorization
	* @return the o auth2 authorization that was removed
	* @throws PortalException if a o auth2 authorization with the primary key could not be found
	*/
	@Override
	public com.liferay.oauth2.provider.model.OAuth2Authorization deleteOAuth2Authorization(
		long oAuth2AuthorizationId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _oAuth2AuthorizationLocalService.deleteOAuth2Authorization(oAuth2AuthorizationId);
	}

	/**
	* Deletes the o auth2 authorization from the database. Also notifies the appropriate model listeners.
	*
	* @param oAuth2Authorization the o auth2 authorization
	* @return the o auth2 authorization that was removed
	*/
	@Override
	public com.liferay.oauth2.provider.model.OAuth2Authorization deleteOAuth2Authorization(
		com.liferay.oauth2.provider.model.OAuth2Authorization oAuth2Authorization) {
		return _oAuth2AuthorizationLocalService.deleteOAuth2Authorization(oAuth2Authorization);
	}

	@Override
	public void deleteOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId, long oAuth2AuthorizationId) {
		_oAuth2AuthorizationLocalService.deleteOAuth2ScopeGrantOAuth2Authorization(oAuth2ScopeGrantId,
			oAuth2AuthorizationId);
	}

	@Override
	public void deleteOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId,
		com.liferay.oauth2.provider.model.OAuth2Authorization oAuth2Authorization) {
		_oAuth2AuthorizationLocalService.deleteOAuth2ScopeGrantOAuth2Authorization(oAuth2ScopeGrantId,
			oAuth2Authorization);
	}

	@Override
	public void deleteOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId,
		java.util.List<com.liferay.oauth2.provider.model.OAuth2Authorization> oAuth2Authorizations) {
		_oAuth2AuthorizationLocalService.deleteOAuth2ScopeGrantOAuth2Authorizations(oAuth2ScopeGrantId,
			oAuth2Authorizations);
	}

	@Override
	public void deleteOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId, long[] oAuth2AuthorizationIds) {
		_oAuth2AuthorizationLocalService.deleteOAuth2ScopeGrantOAuth2Authorizations(oAuth2ScopeGrantId,
			oAuth2AuthorizationIds);
	}

	/**
	* @throws PortalException
	*/
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _oAuth2AuthorizationLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _oAuth2AuthorizationLocalService.dynamicQuery();
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
		return _oAuth2AuthorizationLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _oAuth2AuthorizationLocalService.dynamicQuery(dynamicQuery,
			start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _oAuth2AuthorizationLocalService.dynamicQuery(dynamicQuery,
			start, end, orderByComparator);
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
		return _oAuth2AuthorizationLocalService.dynamicQueryCount(dynamicQuery);
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
		return _oAuth2AuthorizationLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Authorization fetchOAuth2Authorization(
		long oAuth2AuthorizationId) {
		return _oAuth2AuthorizationLocalService.fetchOAuth2Authorization(oAuth2AuthorizationId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return _oAuth2AuthorizationLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return _oAuth2AuthorizationLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	* Returns the o auth2 authorization with the primary key.
	*
	* @param oAuth2AuthorizationId the primary key of the o auth2 authorization
	* @return the o auth2 authorization
	* @throws PortalException if a o auth2 authorization with the primary key could not be found
	*/
	@Override
	public com.liferay.oauth2.provider.model.OAuth2Authorization getOAuth2Authorization(
		long oAuth2AuthorizationId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _oAuth2AuthorizationLocalService.getOAuth2Authorization(oAuth2AuthorizationId);
	}

	/**
	* Returns a range of all the o auth2 authorizations.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of o auth2 authorizations
	* @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	* @return the range of o auth2 authorizations
	*/
	@Override
	public java.util.List<com.liferay.oauth2.provider.model.OAuth2Authorization> getOAuth2Authorizations(
		int start, int end) {
		return _oAuth2AuthorizationLocalService.getOAuth2Authorizations(start,
			end);
	}

	/**
	* Returns the number of o auth2 authorizations.
	*
	* @return the number of o auth2 authorizations
	*/
	@Override
	public int getOAuth2AuthorizationsCount() {
		return _oAuth2AuthorizationLocalService.getOAuth2AuthorizationsCount();
	}

	@Override
	public java.util.List<com.liferay.oauth2.provider.model.OAuth2Authorization> getOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId) {
		return _oAuth2AuthorizationLocalService.getOAuth2ScopeGrantOAuth2Authorizations(oAuth2ScopeGrantId);
	}

	@Override
	public java.util.List<com.liferay.oauth2.provider.model.OAuth2Authorization> getOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId, int start, int end) {
		return _oAuth2AuthorizationLocalService.getOAuth2ScopeGrantOAuth2Authorizations(oAuth2ScopeGrantId,
			start, end);
	}

	@Override
	public java.util.List<com.liferay.oauth2.provider.model.OAuth2Authorization> getOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<com.liferay.oauth2.provider.model.OAuth2Authorization> orderByComparator) {
		return _oAuth2AuthorizationLocalService.getOAuth2ScopeGrantOAuth2Authorizations(oAuth2ScopeGrantId,
			start, end, orderByComparator);
	}

	@Override
	public int getOAuth2ScopeGrantOAuth2AuthorizationsCount(
		long oAuth2ScopeGrantId) {
		return _oAuth2AuthorizationLocalService.getOAuth2ScopeGrantOAuth2AuthorizationsCount(oAuth2ScopeGrantId);
	}

	/**
	* Returns the oAuth2ScopeGrantIds of the o auth2 scope grants associated with the o auth2 authorization.
	*
	* @param oAuth2AuthorizationId the oAuth2AuthorizationId of the o auth2 authorization
	* @return long[] the oAuth2ScopeGrantIds of o auth2 scope grants associated with the o auth2 authorization
	*/
	@Override
	public long[] getOAuth2ScopeGrantPrimaryKeys(long oAuth2AuthorizationId) {
		return _oAuth2AuthorizationLocalService.getOAuth2ScopeGrantPrimaryKeys(oAuth2AuthorizationId);
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	@Override
	public java.lang.String getOSGiServiceIdentifier() {
		return _oAuth2AuthorizationLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _oAuth2AuthorizationLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId, long oAuth2AuthorizationId) {
		return _oAuth2AuthorizationLocalService.hasOAuth2ScopeGrantOAuth2Authorization(oAuth2ScopeGrantId,
			oAuth2AuthorizationId);
	}

	@Override
	public boolean hasOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId) {
		return _oAuth2AuthorizationLocalService.hasOAuth2ScopeGrantOAuth2Authorizations(oAuth2ScopeGrantId);
	}

	@Override
	public void setOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId, long[] oAuth2AuthorizationIds) {
		_oAuth2AuthorizationLocalService.setOAuth2ScopeGrantOAuth2Authorizations(oAuth2ScopeGrantId,
			oAuth2AuthorizationIds);
	}

	/**
	* Updates the o auth2 authorization in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param oAuth2Authorization the o auth2 authorization
	* @return the o auth2 authorization that was updated
	*/
	@Override
	public com.liferay.oauth2.provider.model.OAuth2Authorization updateOAuth2Authorization(
		com.liferay.oauth2.provider.model.OAuth2Authorization oAuth2Authorization) {
		return _oAuth2AuthorizationLocalService.updateOAuth2Authorization(oAuth2Authorization);
	}

	@Override
	public OAuth2AuthorizationLocalService getWrappedService() {
		return _oAuth2AuthorizationLocalService;
	}

	@Override
	public void setWrappedService(
		OAuth2AuthorizationLocalService oAuth2AuthorizationLocalService) {
		_oAuth2AuthorizationLocalService = oAuth2AuthorizationLocalService;
	}

	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;
}