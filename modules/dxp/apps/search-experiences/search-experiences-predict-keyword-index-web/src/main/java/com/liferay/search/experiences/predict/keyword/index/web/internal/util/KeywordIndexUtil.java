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

package com.liferay.search.experiences.predict.keyword.index.web.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.search.experiences.content.analysis.constants.ModerationReason;
import com.liferay.search.experiences.predict.keyword.index.index.KeywordEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = {})
public class KeywordIndexUtil {

	public static final String INDEX_DATE_FORMAT = "yyyyMMddHHmmss";

	public static final List<String> analyzedLanguages = new ArrayList<>(
		Arrays.asList("ar", "ja", "ko", "zh"));

	public static String createReportEntry(
		ModerationReason moderationReason, String reporter) {

		StringBundler sb = new StringBundler(5);

		sb.append(toIndexDateString(new Date()));
		sb.append("---");
		sb.append(moderationReason.name());
		sb.append("---");
		sb.append(reporter);

		return sb.toString();
	}

	public static Date fromIndexDateString(String dateStringFieldValue) {
		if (Validator.isBlank(dateStringFieldValue)) {
			return null;
		}

		try {
			DateFormat dateFormat = new SimpleDateFormat(INDEX_DATE_FORMAT);

			return dateFormat.parse(dateStringFieldValue);
		}
		catch (Exception exception) {
			_log.error(exception.getMessage(), exception);
		}

		return null;
	}

	public static Date getLastReported(KeywordEntry keywordEntry) {
		String report = _getLastReport(keywordEntry);

		if (report == null) {
			return null;
		}

		String[] arr = report.split("---");

		return fromIndexDateString(arr[0]);
	}

	public static String getLastReporter(KeywordEntry keywordEntry) {
		String report = _getLastReport(keywordEntry);

		if (report == null) {
			return StringPool.BLANK;
		}

		String[] arr = report.split("---");

		return arr[2];
	}

	public static String getLocalizedFieldName(
		String fieldName, String languageId) {

		StringBundler sb = new StringBundler(3);

		sb.append(fieldName);
		sb.append(StringPool.UNDERLINE);
		sb.append(languageId);

		return sb.toString();
	}

	public static String getSiteName(long groupId, Locale locale) {
		try {
			Group group = _groupLocalService.getGroup(groupId);

			return group.getName(locale, true);
		}
		catch (PortalException portalException) {
			_log.error(portalException.getMessage(), portalException);
		}

		return GetterUtil.getString(groupId);
	}

	public static boolean isAnalyzedLanguage(String languageId) {
		if (Validator.isBlank(languageId)) {
			return false;
		}

		Stream<String> stream = analyzedLanguages.stream();

		return stream.anyMatch(languageId::startsWith);
	}

	public static String toIndexDateString(Date date) {
		try {
			DateFormat dateFormat = new SimpleDateFormat(INDEX_DATE_FORMAT);

			return dateFormat.format(date);
		}
		catch (Exception exception) {
			_log.error(exception.getMessage(), exception);
		}

		return StringPool.BLANK;
	}

	@Reference(unbind = "-")
	protected void setGroupLocalService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	private static String _getLastReport(KeywordEntry keywordEntry) {
		List<String> reports = keywordEntry.getReports();

		if ((reports == null) || reports.isEmpty()) {
			return null;
		}

		return reports.get(reports.size() - 1);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KeywordIndexUtil.class);

	private static GroupLocalService _groupLocalService;

}