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

package com.liferay.portal.search.tuning.blueprints.engine.internal.condition.visitor;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.tuning.blueprints.constants.json.keys.query.ConditionConfigurationKeys;
import com.liferay.portal.search.tuning.blueprints.engine.exception.ParameterEvaluationException;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.BooleanParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.ConditionEvaluationVisitor;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.DateParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.DoubleParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.FloatParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.IntegerArrayParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.IntegerParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.LongArrayParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.LongParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.StringArrayParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.StringParameter;
import com.liferay.portal.search.tuning.blueprints.message.Message;
import com.liferay.portal.search.tuning.blueprints.message.Severity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * @author Petteri Karttunen
 */
public class GreaterThanVisitor implements ConditionEvaluationVisitor {

	public GreaterThanVisitor(
		JSONObject configurationJSONObject, boolean not, boolean equal) {

		_conditionJSONObject = configurationJSONObject;
		_not = not;
		_equal = equal;
	}

	@Override
	public boolean visit(BooleanParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean visit(DateParameter parameter)
		throws ParameterEvaluationException {

		String dateString = _conditionJSONObject.getString(
			ConditionConfigurationKeys.MATCH_VALUE.getJsonKey());

		String dateFormatString = _conditionJSONObject.getString(
			ConditionConfigurationKeys.DATE_FORMAT.getJsonKey());

		if (Validator.isNull(dateFormatString)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Clause condition date parameter format is missing [ " +
						_conditionJSONObject + " ].");
			}

			throw new ParameterEvaluationException(
				new Message.Builder().className(
					getClass().getName()
				).localizationKey(
					"core.error.clause-condition-date-format-missing"
				).msg(
					"Clause condition date format is missing"
				).rootObject(
					_conditionJSONObject
				).rootProperty(
					ConditionConfigurationKeys.DATE_FORMAT.getJsonKey()
				).rootValue(
					dateFormatString
				).severity(
					Severity.ERROR
				).build());
		}

		try {
			DateFormat dateFormat = new SimpleDateFormat(dateFormatString);

			Date date = dateFormat.parse(dateString);

			Date parameterValue = parameter.getValue();

			if (_not) {
				return parameterValue.after(date);
			}

			return parameterValue.before(date);
		}
		catch (IllegalArgumentException | NullPointerException | ParseException
					exception) {

			_log.error(
				"Unable to parse clause condition date " + dateString + ".",
				exception);

			throw new ParameterEvaluationException(
				new Message.Builder().className(
					getClass().getName()
				).localizationKey(
					"core.error.clause-condition-date-parsing-error"
				).msg(
					exception.getMessage()
				).rootObject(
					_conditionJSONObject
				).rootProperty(
					ConditionConfigurationKeys.MATCH_VALUE.getJsonKey()
				).rootValue(
					dateString
				).severity(
					Severity.ERROR
				).throwable(
					exception
				).build());
		}
	}

	@Override
	public boolean visit(DoubleParameter parameter)
		throws ParameterEvaluationException {

		Double value = _conditionJSONObject.getDouble(
			ConditionConfigurationKeys.MATCH_VALUE.getJsonKey());

		Double parameterValue = parameter.getValue();

		boolean greaterThan = false;

		if (_equal) {
			if (parameterValue.compareTo(value) >= 0) {
				greaterThan = true;
			}
		}
		else if (parameterValue.compareTo(value) > 0) {
			greaterThan = true;
		}

		if (_not) {
			return !greaterThan;
		}

		return greaterThan;
	}

	@Override
	public boolean visit(FloatParameter parameter)
		throws ParameterEvaluationException {

		Float value = GetterUtil.getFloat(
			_conditionJSONObject.get(
				ConditionConfigurationKeys.MATCH_VALUE.getJsonKey()));

		Float parameterValue = parameter.getValue();

		boolean greaterThan = false;

		if (_equal) {
			if (parameterValue.compareTo(value) >= 0) {
				greaterThan = true;
			}
		}
		else if (parameterValue.compareTo(value) > 0) {
			greaterThan = true;
		}

		if (_not) {
			return !greaterThan;
		}

		return greaterThan;
	}

	@Override
	public boolean visit(IntegerArrayParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean visit(IntegerParameter parameter)
		throws ParameterEvaluationException {

		Integer value = _conditionJSONObject.getInt(
			ConditionConfigurationKeys.MATCH_VALUE.getJsonKey());

		Integer parameterValue = parameter.getValue();

		boolean greaterThan = false;

		if (_equal) {
			if (parameterValue.compareTo(value) >= 0) {
				greaterThan = true;
			}
		}
		else if (parameterValue.compareTo(value) > 0) {
			greaterThan = true;
		}

		if (_not) {
			return !greaterThan;
		}

		return greaterThan;
	}

	@Override
	public boolean visit(LongArrayParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean visit(LongParameter parameter)
		throws ParameterEvaluationException {

		Long value = _conditionJSONObject.getLong(
			ConditionConfigurationKeys.MATCH_VALUE.getJsonKey());

		Long parameterValue = parameter.getValue();

		boolean greaterThan = false;

		if (_equal) {
			if (parameterValue.compareTo(value) >= 0) {
				greaterThan = true;
			}
		}
		else if (parameterValue.compareTo(value) > 0) {
			greaterThan = true;
		}

		if (_not) {
			return !greaterThan;
		}

		return greaterThan;
	}

	@Override
	public boolean visit(StringArrayParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean visit(StringParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GreaterThanVisitor.class);

	private final JSONObject _conditionJSONObject;
	private final boolean _equal;
	private final boolean _not;

}