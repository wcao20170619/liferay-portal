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

package com.liferay.client.extension.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link ClientExtensionEntryRel}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ClientExtensionEntryRel
 * @generated
 */
public class ClientExtensionEntryRelWrapper
	extends BaseModelWrapper<ClientExtensionEntryRel>
	implements ClientExtensionEntryRel, ModelWrapper<ClientExtensionEntryRel> {

	public ClientExtensionEntryRelWrapper(
		ClientExtensionEntryRel clientExtensionEntryRel) {

		super(clientExtensionEntryRel);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put(
			"clientExtensionEntryRelId", getClientExtensionEntryRelId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("classNameId", getClassNameId());
		attributes.put("classPK", getClassPK());
		attributes.put(
			"cetExternalReferenceCode", getCETExternalReferenceCode());
		attributes.put("type", getType());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long clientExtensionEntryRelId = (Long)attributes.get(
			"clientExtensionEntryRelId");

		if (clientExtensionEntryRelId != null) {
			setClientExtensionEntryRelId(clientExtensionEntryRelId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long classNameId = (Long)attributes.get("classNameId");

		if (classNameId != null) {
			setClassNameId(classNameId);
		}

		Long classPK = (Long)attributes.get("classPK");

		if (classPK != null) {
			setClassPK(classPK);
		}

		String cetExternalReferenceCode = (String)attributes.get(
			"cetExternalReferenceCode");

		if (cetExternalReferenceCode != null) {
			setCETExternalReferenceCode(cetExternalReferenceCode);
		}

		String type = (String)attributes.get("type");

		if (type != null) {
			setType(type);
		}
	}

	@Override
	public ClientExtensionEntryRel cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the cet external reference code of this client extension entry rel.
	 *
	 * @return the cet external reference code of this client extension entry rel
	 */
	@Override
	public String getCETExternalReferenceCode() {
		return model.getCETExternalReferenceCode();
	}

	/**
	 * Returns the fully qualified class name of this client extension entry rel.
	 *
	 * @return the fully qualified class name of this client extension entry rel
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the class name ID of this client extension entry rel.
	 *
	 * @return the class name ID of this client extension entry rel
	 */
	@Override
	public long getClassNameId() {
		return model.getClassNameId();
	}

	/**
	 * Returns the class pk of this client extension entry rel.
	 *
	 * @return the class pk of this client extension entry rel
	 */
	@Override
	public long getClassPK() {
		return model.getClassPK();
	}

	/**
	 * Returns the client extension entry rel ID of this client extension entry rel.
	 *
	 * @return the client extension entry rel ID of this client extension entry rel
	 */
	@Override
	public long getClientExtensionEntryRelId() {
		return model.getClientExtensionEntryRelId();
	}

	/**
	 * Returns the company ID of this client extension entry rel.
	 *
	 * @return the company ID of this client extension entry rel
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the container model ID of this client extension entry rel.
	 *
	 * @return the container model ID of this client extension entry rel
	 */
	@Override
	public long getContainerModelId() {
		return model.getContainerModelId();
	}

	/**
	 * Returns the container name of this client extension entry rel.
	 *
	 * @return the container name of this client extension entry rel
	 */
	@Override
	public String getContainerModelName() {
		return model.getContainerModelName();
	}

	/**
	 * Returns the create date of this client extension entry rel.
	 *
	 * @return the create date of this client extension entry rel
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the external reference code of this client extension entry rel.
	 *
	 * @return the external reference code of this client extension entry rel
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the modified date of this client extension entry rel.
	 *
	 * @return the modified date of this client extension entry rel
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this client extension entry rel.
	 *
	 * @return the mvcc version of this client extension entry rel
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the parent container model ID of this client extension entry rel.
	 *
	 * @return the parent container model ID of this client extension entry rel
	 */
	@Override
	public long getParentContainerModelId() {
		return model.getParentContainerModelId();
	}

	/**
	 * Returns the primary key of this client extension entry rel.
	 *
	 * @return the primary key of this client extension entry rel
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the type of this client extension entry rel.
	 *
	 * @return the type of this client extension entry rel
	 */
	@Override
	public String getType() {
		return model.getType();
	}

	/**
	 * Returns the user ID of this client extension entry rel.
	 *
	 * @return the user ID of this client extension entry rel
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this client extension entry rel.
	 *
	 * @return the user name of this client extension entry rel
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this client extension entry rel.
	 *
	 * @return the user uuid of this client extension entry rel
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this client extension entry rel.
	 *
	 * @return the uuid of this client extension entry rel
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the cet external reference code of this client extension entry rel.
	 *
	 * @param cetExternalReferenceCode the cet external reference code of this client extension entry rel
	 */
	@Override
	public void setCETExternalReferenceCode(String cetExternalReferenceCode) {
		model.setCETExternalReferenceCode(cetExternalReferenceCode);
	}

	@Override
	public void setClassName(String className) {
		model.setClassName(className);
	}

	/**
	 * Sets the class name ID of this client extension entry rel.
	 *
	 * @param classNameId the class name ID of this client extension entry rel
	 */
	@Override
	public void setClassNameId(long classNameId) {
		model.setClassNameId(classNameId);
	}

	/**
	 * Sets the class pk of this client extension entry rel.
	 *
	 * @param classPK the class pk of this client extension entry rel
	 */
	@Override
	public void setClassPK(long classPK) {
		model.setClassPK(classPK);
	}

	/**
	 * Sets the client extension entry rel ID of this client extension entry rel.
	 *
	 * @param clientExtensionEntryRelId the client extension entry rel ID of this client extension entry rel
	 */
	@Override
	public void setClientExtensionEntryRelId(long clientExtensionEntryRelId) {
		model.setClientExtensionEntryRelId(clientExtensionEntryRelId);
	}

	/**
	 * Sets the company ID of this client extension entry rel.
	 *
	 * @param companyId the company ID of this client extension entry rel
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the container model ID of this client extension entry rel.
	 *
	 * @param containerModelId the container model ID of this client extension entry rel
	 */
	@Override
	public void setContainerModelId(long containerModelId) {
		model.setContainerModelId(containerModelId);
	}

	/**
	 * Sets the create date of this client extension entry rel.
	 *
	 * @param createDate the create date of this client extension entry rel
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the external reference code of this client extension entry rel.
	 *
	 * @param externalReferenceCode the external reference code of this client extension entry rel
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the modified date of this client extension entry rel.
	 *
	 * @param modifiedDate the modified date of this client extension entry rel
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this client extension entry rel.
	 *
	 * @param mvccVersion the mvcc version of this client extension entry rel
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the parent container model ID of this client extension entry rel.
	 *
	 * @param parentContainerModelId the parent container model ID of this client extension entry rel
	 */
	@Override
	public void setParentContainerModelId(long parentContainerModelId) {
		model.setParentContainerModelId(parentContainerModelId);
	}

	/**
	 * Sets the primary key of this client extension entry rel.
	 *
	 * @param primaryKey the primary key of this client extension entry rel
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the type of this client extension entry rel.
	 *
	 * @param type the type of this client extension entry rel
	 */
	@Override
	public void setType(String type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this client extension entry rel.
	 *
	 * @param userId the user ID of this client extension entry rel
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this client extension entry rel.
	 *
	 * @param userName the user name of this client extension entry rel
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this client extension entry rel.
	 *
	 * @param userUuid the user uuid of this client extension entry rel
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this client extension entry rel.
	 *
	 * @param uuid the uuid of this client extension entry rel
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected ClientExtensionEntryRelWrapper wrap(
		ClientExtensionEntryRel clientExtensionEntryRel) {

		return new ClientExtensionEntryRelWrapper(clientExtensionEntryRel);
	}

}