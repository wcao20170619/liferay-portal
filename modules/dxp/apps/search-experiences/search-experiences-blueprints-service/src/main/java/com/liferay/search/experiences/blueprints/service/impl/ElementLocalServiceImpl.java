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

package com.liferay.search.experiences.blueprints.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.search.experiences.blueprints.exception.BlueprintValidationException;
import com.liferay.search.experiences.blueprints.exception.ElementValidationException;
import com.liferay.search.experiences.blueprints.model.Element;
import com.liferay.search.experiences.blueprints.service.base.ElementLocalServiceBaseImpl;
import com.liferay.search.experiences.blueprints.validator.ElementValidator;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The implementation of the element local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the <code>com.liferay.search.experiences.elements.service.ElementLocalService</code> interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ElementLocalServiceBaseImpl
 */
@Component(
	property = "model.class.name=com.liferay.search.experiences.blueprints.model.Element",
	service = AopService.class
)
public class ElementLocalServiceImpl extends ElementLocalServiceBaseImpl {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Use <code>com.liferay.search.experiences.elements.service.ElementLocalService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>com.liferay.search.experiences.blueprints.service.ElementLocalServiceUtil</code>.
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Element addElement(
			long userId, long groupId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String configuration, int type,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_validate(titleMap, configuration, type, serviceContext);

		long elementId = counterLocalService.increment(Element.class.getName());

		Element element = createElement(elementId);

		element.setGroupId(groupId);
		element.setCompanyId(user.getCompanyId());
		element.setUserId(user.getUserId());
		element.setUserName(user.getFullName());
		element.setCreateDate(serviceContext.getCreateDate(new Date()));
		element.setModifiedDate(serviceContext.getModifiedDate(new Date()));
		element.setUuid(serviceContext.getUuid());

		element.setHidden(false);
		element.setReadOnly(_isReadOnly(serviceContext));
		element.setTitleMap(titleMap);
		element.setDescriptionMap(descriptionMap);

		int status = WorkflowConstants.STATUS_APPROVED;

		element.setStatus(status);

		element.setConfiguration(configuration);
		element.setType(type);

		element = super.addElement(element);

		_resourceLocalService.addModelResources(element, serviceContext);

		return element;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public Element deleteElement(Element element) throws PortalException {
		if (element.getReadOnly()) {
			throw new PortalException("Cannot delete system read-only element");
		}

		_resourceLocalService.deleteResource(
			element, ResourceConstants.SCOPE_INDIVIDUAL);

		return super.deleteElement(element);
	}

	@Override
	public Element deleteElement(long elementId) throws PortalException {
		Element element = elementPersistence.findByPrimaryKey(elementId);

		return deleteElement(element);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public Element deleteSystemElement(Element element) throws PortalException {
		_resourceLocalService.deleteResource(
			element, ResourceConstants.SCOPE_INDIVIDUAL);

		return super.deleteElement(element);
	}

	@Override
	public int getCompanyElementsCount(long companyId, int type) {
		return elementPersistence.countByC_T(companyId, type);
	}

	@Override
	public List<Element> getGroupElements(
		long groupId, int type, int start, int end) {

		return getGroupElements(
			groupId, WorkflowConstants.STATUS_APPROVED, type, start, end);
	}

	@Override
	public List<Element> getGroupElements(
		long groupId, int status, int type, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return elementPersistence.findByG_T(groupId, type, start, end);
		}

		return elementPersistence.findByG_S_T(
			groupId, status, type, start, end);
	}

	@Override
	public List<Element> getGroupElements(
		long groupId, int status, int type, int start, int end,
		OrderByComparator<Element> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return elementPersistence.findByG_T(
				groupId, type, start, end, orderByComparator);
		}

		return elementPersistence.findByG_S_T(
			groupId, status, type, start, end, orderByComparator);
	}

	@Override
	public List<Element> getGroupElements(
		long groupId, int type, int start, int end,
		OrderByComparator<Element> orderByComparator) {

		return getGroupElements(
			groupId, WorkflowConstants.STATUS_APPROVED, type, start, end,
			orderByComparator);
	}

	@Override
	public int getGroupElementsCount(long groupId, int type) {
		return getGroupElementsCount(
			groupId, WorkflowConstants.STATUS_APPROVED, type);
	}

	@Override
	public int getGroupElementsCount(long groupId, int status, int type) {
		return elementPersistence.countByG_S_T(groupId, status, type);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Element updateElement(
			long userId, long elementId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String configuration,
			boolean hidden, ServiceContext serviceContext)
		throws PortalException {

		Element element = getElement(elementId);

		_validate(titleMap, configuration, element.getType(), serviceContext);

		element.setDescriptionMap(descriptionMap);
		element.setModifiedDate(serviceContext.getModifiedDate(new Date()));
		element.setHidden(hidden);
		element.setTitleMap(titleMap);

		element.setConfiguration(configuration);

		return updateElement(element);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Element updateStatus(long userId, long elementId, int status)
		throws PortalException {

		Element element = getElement(elementId);

		element.setStatus(status);

		return updateElement(element);
	}

	private Boolean _isReadOnly(ServiceContext serviceContext) {
		return GetterUtil.getBoolean(serviceContext.getAttribute("read-only"));
	}

	private void _validate(
			Map<Locale, String> titleMap, String configuration, int type,
			ServiceContext serviceContext)
		throws BlueprintValidationException, ElementValidationException {

		if (!GetterUtil.getBoolean(
				serviceContext.getAttribute("skip.element.validation"))) {

			_elementValidator.validateElement(titleMap, configuration, type);
		}
	}

	@Reference
	private ElementValidator _elementValidator;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}