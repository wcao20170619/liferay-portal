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

package com.liferay.search.experiences.blueprints.engine.internal.parameter.builder;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.search.experiences.blueprints.constants.json.keys.parameter.CustomParameterConfigurationKeys;
import com.liferay.search.experiences.blueprints.engine.attributes.BlueprintsAttributes;
import com.liferay.search.experiences.blueprints.engine.constants.ReservedParameterNames;
import com.liferay.search.experiences.blueprints.engine.internal.attributes.util.BlueprintsAttributesHelper;
import com.liferay.search.experiences.blueprints.engine.parameter.DateParameter;
import com.liferay.search.experiences.blueprints.engine.parameter.Parameter;
import com.liferay.search.experiences.blueprints.message.Messages;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, property = "name=date", service = ParameterBuilder.class
)
public class DateParameterBuilder implements ParameterBuilder {

	@Override
	public Optional<Parameter> build(
		BlueprintsAttributes blueprintsAttributes,
		JSONObject configurationJSONObject, Messages messages) {

		String parameterName = configurationJSONObject.getString(
			CustomParameterConfigurationKeys.PARAMETER_NAME.getJsonKey());

		String dateString = _getDateString(blueprintsAttributes, parameterName);

		if (Validator.isBlank(dateString)) {
			return Optional.empty();
		}

		String timeZoneId = _getTimeZoneId(
			blueprintsAttributes, ReservedParameterNames.TIMEZONE_ID.getKey());

		if (Validator.isBlank(dateString) || Validator.isBlank(timeZoneId)) {
			return Optional.empty();
		}

		Date date = _getDate(configurationJSONObject, dateString, timeZoneId);

		if (date == null) {
			return Optional.empty();
		}

		return Optional.of(
			new DateParameter(
				parameterName, "${parameter." + parameterName + "}", date));
	}

	private Date _getDate(
		JSONObject configurationJSONObject, String dateString,
		String timeZoneId) {

		String dateFormat = configurationJSONObject.getString(
			CustomParameterConfigurationKeys.DATE_FORMAT.getJsonKey());

		try {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
				dateFormat);

			LocalDate localDate = LocalDate.parse(
				dateString, dateTimeFormatter);

			TimeZone timeZone = TimeZoneUtil.getTimeZone(timeZoneId);

			GregorianCalendar calendar = GregorianCalendar.from(
				localDate.atStartOfDay(timeZone.toZoneId()));

			return calendar.getTime();
		}
		catch (Exception exception) {
			_log.error(
				String.format(
					"Cannot parse date from string '%s'", dateString,
					exception));
		}

		return null;
	}

	private String _getDateString(
		BlueprintsAttributes blueprintsAttributes, String parameterName) {

		Optional<String> optional =
			_blueprintsAttributesHelper.getStringOptional(
				blueprintsAttributes, parameterName);

		return optional.orElse(StringPool.BLANK);
	}

	private String _getTimeZoneId(
		BlueprintsAttributes blueprintsAttributes, String parameterName) {

		Optional<String> optional =
			_blueprintsAttributesHelper.getStringOptional(
				blueprintsAttributes, parameterName);

		TimeZone defaultTimeZone = TimeZoneUtil.getDefault();

		return optional.orElse(defaultTimeZone.getID());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DateParameterBuilder.class);

	@Reference
	private BlueprintsAttributesHelper _blueprintsAttributesHelper;

}