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

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.constants.DDMFormInstanceReportConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceReport;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.service.base.DDMFormInstanceReportLocalServiceBaseImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstancePersistence;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Date;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstanceReport",
	service = AopService.class
)
public class DDMFormInstanceReportLocalServiceImpl
	extends DDMFormInstanceReportLocalServiceBaseImpl {

	@Override
	public DDMFormInstanceReport addFormInstanceReport(long formInstanceId)
		throws PortalException {

		DDMFormInstanceReport formInstanceReport =
			ddmFormInstanceReportPersistence.create(
				counterLocalService.increment());

		DDMFormInstance formInstance =
			_formInstancePersistence.findByPrimaryKey(formInstanceId);

		formInstanceReport.setGroupId(formInstance.getGroupId());
		formInstanceReport.setCompanyId(formInstance.getCompanyId());

		formInstanceReport.setCreateDate(new Date());
		formInstanceReport.setFormInstanceId(formInstance.getFormInstanceId());

		return ddmFormInstanceReportPersistence.update(formInstanceReport);
	}

	public DDMFormInstanceReport getFormInstanceReportByFormInstanceId(
			long formInstanceId)
		throws PortalException {

		return ddmFormInstanceReportPersistence.findByFormInstanceId(
			formInstanceId);
	}

	@Override
	public DDMFormInstanceReport updateFormInstanceReport(
			long formInstanceReportId, long formInstanceRecordVersionId,
			String formInstanceReportEvent)
		throws PortalException {

		DDMFormInstanceReport formInstanceReport =
			ddmFormInstanceReportPersistence.findByPrimaryKey(
				formInstanceReportId);

		formInstanceReport.setModifiedDate(new Date());
		formInstanceReport.setData(
			_getData(formInstanceRecordVersionId, formInstanceReportEvent));

		return ddmFormInstanceReportPersistence.update(formInstanceReport);
	}

	private String _getData(
			long formInstanceRecordVersionId, String formInstanceReportEvent)
		throws PortalException {

		DDMFormInstanceRecordVersion formInstanceRecordVersion =
			_formInstanceRecordVersionLocalService.
				getDDMFormInstanceRecordVersion(formInstanceRecordVersionId);

		DDMFormInstanceReport formInstanceReport =
			ddmFormInstanceReportPersistence.findByFormInstanceId(
				formInstanceRecordVersion.getFormInstanceId());

		JSONObject formInstanceReportDataJSONObject =
			JSONFactoryUtil.createJSONObject(formInstanceReport.getData());

		DDMFormValues ddmFormValues =
			formInstanceRecordVersion.getDDMFormValues();

		for (DDMFormFieldValue ddmFormFieldValue :
				ddmFormValues.getDDMFormFieldValues()) {

			JSONObject fieldJSONObject =
				formInstanceReportDataJSONObject.getJSONObject(
					ddmFormFieldValue.getName());

			if (fieldJSONObject == null) {
				fieldJSONObject = JSONUtil.put(
					"type", ddmFormFieldValue.getType()
				).put(
					"values", JSONFactoryUtil.createJSONObject()
				);

				formInstanceReportDataJSONObject.put(
					ddmFormFieldValue.getName(), fieldJSONObject);
			}

			if (StringUtil.equals(ddmFormFieldValue.getType(), "radio")) {
				JSONObject valuesJSONObject = fieldJSONObject.getJSONObject(
					"values");

				Value value = ddmFormFieldValue.getValue();

				for (Locale availableLocale : value.getAvailableLocales()) {
					String key = value.getString(availableLocale);

					int count = valuesJSONObject.getInt(key);

					if (formInstanceReportEvent.equals(
							DDMFormInstanceReportConstants.
								EVENT_ADD_RECORD_VERSION)) {

						count++;
					}
					else if (formInstanceReportEvent.equals(
								DDMFormInstanceReportConstants.
									EVENT_DELETE_RECORD_VERSION)) {

						count--;
					}

					valuesJSONObject.put(key, count);
				}
			}
		}

		return formInstanceReportDataJSONObject.toString();
	}

	@Reference
	private DDMFormInstancePersistence _formInstancePersistence;

	@Reference
	private DDMFormInstanceRecordVersionLocalService
		_formInstanceRecordVersionLocalService;

}