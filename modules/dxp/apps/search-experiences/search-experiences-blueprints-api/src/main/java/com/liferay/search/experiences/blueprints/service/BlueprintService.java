/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.search.experiences.blueprints.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.search.experiences.blueprints.model.Blueprint;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for Blueprint. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see BlueprintServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface BlueprintService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.search.experiences.blueprints.service.impl.BlueprintServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the blueprint remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link BlueprintServiceUtil} if injection and service tracking are not available.
	 */
	public Blueprint addCompanyBlueprint(
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String configuration, String selectedElements,
			ServiceContext serviceContext)
		throws PortalException;

	public Blueprint addGroupBlueprint(
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String configuration, String selectedElements,
			ServiceContext serviceContext)
		throws PortalException;

	public Blueprint deleteBlueprint(long blueprintId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Blueprint getBlueprint(long blueprintId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Blueprint> getGroupBlueprints(long groupId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Blueprint> getGroupBlueprints(
		long groupId, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Blueprint> getGroupBlueprints(
		long groupId, int status, int start, int end,
		OrderByComparator<Blueprint> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Blueprint> getGroupBlueprints(
		long groupId, int start, int end,
		OrderByComparator<Blueprint> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupBlueprintsCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupBlueprintsCount(long groupId, int status);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public Blueprint updateBlueprint(
			long blueprintId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String configuration,
			String selectedElements, ServiceContext serviceContext)
		throws PortalException;

}