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

package com.liferay.dynamic.data.mapping.form.report.web.internal.display.context;

import com.liferay.dynamic.data.mapping.form.report.web.internal.portlet.DDMFormReportPortlet;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceReport;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.ResourceBundle;

import javax.portlet.RenderRequest;

/**
 * @author Bruno Farache
 */
public class DDMFormReportDisplayContext {

	public DDMFormReportDisplayContext(
		long ddmFormInstanceId,
		DDMFormInstanceRecordLocalService ddmFormInstanceRecordLocalService,
		DDMFormInstanceReport ddmFormInstanceReport,
		RenderRequest renderRequest) {

		_ddmFormInstanceId = ddmFormInstanceId;
		_ddmFormInstanceRecordLocalService = ddmFormInstanceRecordLocalService;
		_ddmFormInstanceReport = ddmFormInstanceReport;
		_renderRequest = renderRequest;
	}

	public DDMFormInstanceReport getDDMFormInstanceReport() {
		return _ddmFormInstanceReport;
	}

	public String getLastModifiedDate() {
		if (_ddmFormInstanceReport == null) {
			return StringPool.BLANK;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			themeDisplay.getLocale(), DDMFormReportPortlet.class);

		String languageKey = "report-was-last-modified-on-x";

		Date modifiedDate = _ddmFormInstanceReport.getModifiedDate();

		int daysBetween = DateUtil.getDaysBetween(
			new Date(modifiedDate.getTime()), new Date(),
			themeDisplay.getTimeZone());

		if (daysBetween < 2) {
			languageKey = "report-was-last-modified-x";
		}

		String relativeTimeDescription = StringUtil.removeSubstring(
			Time.getRelativeTimeDescription(
				modifiedDate, themeDisplay.getLocale(),
				themeDisplay.getTimeZone()),
			StringPool.PERIOD);

		if (daysBetween < 2) {
			relativeTimeDescription = StringUtil.toLowerCase(
				relativeTimeDescription);
		}

		return LanguageUtil.format(
			resourceBundle, languageKey, relativeTimeDescription, false);
	}

	public int getTotalItems() {
		return _ddmFormInstanceRecordLocalService.getFormInstanceRecordsCount(
			_ddmFormInstanceId, WorkflowConstants.STATUS_APPROVED);
	}

	private final long _ddmFormInstanceId;
	private final DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;
	private final DDMFormInstanceReport _ddmFormInstanceReport;
	private final RenderRequest _renderRequest;

}