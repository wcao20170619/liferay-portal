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

package com.liferay.portal.search.tuning.blueprints.engine.impl.internal.parameter.visitor;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.tuning.blueprints.constants.json.keys.ClauseConfigurationKeys;
import com.liferay.portal.search.tuning.blueprints.engine.exception.ParameterEvaluationException;
import com.liferay.portal.search.tuning.blueprints.engine.message.Message;
import com.liferay.portal.search.tuning.blueprints.engine.message.Severity;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.BooleanParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.DateParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.DoubleParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.FloatParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.IntegerArrayParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.IntegerParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.LongArrayParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.LongParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.Parameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.StringArrayParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.StringParameter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * @author Petteri Karttunen
 */
public class EqualsVisitor implements ClauseConditionEvaluationVisitor {

	public EqualsVisitor(JSONObject configurationJsonObject, boolean not) {
		_conditionJsonObject = configurationJsonObject;
		_not = not;
	}

	@Override
	public boolean visit(BooleanParameter parameter)
		throws ParameterEvaluationException {

		Boolean value = _conditionJsonObject.getBoolean(
			ClauseConfigurationKeys.MATCH_VALUE.getJsonKey());

		if (_not) {
			if (value.booleanValue() != parameter.getValue(
				).booleanValue()) {

				return true;
			}

			return false;
		}

		if (value.booleanValue() == parameter.getValue(
			).booleanValue()) {

			return true;
		}

		return false;
	}

	@Override
	public boolean visit(DateParameter parameter)
		throws ParameterEvaluationException {

		String dateString = _conditionJsonObject.getString(
			ClauseConfigurationKeys.MATCH_VALUE.getJsonKey());

		String dateFormatString = _conditionJsonObject.getString(
			ClauseConfigurationKeys.DATE_FORMAT.getJsonKey());

		if (Validator.isNull(dateFormatString)) {
			throw new ParameterEvaluationException(
				new Message(
					Severity.ERROR, "core",
					"core.error.clause-condition-date-format-missing",
					_conditionJsonObject,
					ClauseConfigurationKeys.DATE_FORMAT.getJsonKey(),
					dateFormatString));
		}

		try {
			DateFormat dateFormat = new SimpleDateFormat(dateFormatString);

			Date date = dateFormat.parse(dateString);

			return parameter.getValue(
			).equals(
				date
			);
		}
		catch (Exception e) {
			throw new ParameterEvaluationException(
				new Message(
					Severity.ERROR, "core",
					"core.error.clause-condition-date-parsing-error",
					e.getMessage(), e, _conditionJsonObject,
					ClauseConfigurationKeys.MATCH_VALUE.getJsonKey(),
					dateString));
		}
	}

	@Override
	public boolean visit(DoubleParameter parameter)
		throws ParameterEvaluationException {

		Double value = _conditionJsonObject.getDouble(
			ClauseConfigurationKeys.MATCH_VALUE.getJsonKey());

		if (_not) {
			return !parameter.equalsTo(value);
		}

		return parameter.equalsTo(value);
	}

	@Override
	public boolean visit(FloatParameter parameter)
		throws ParameterEvaluationException {

		Float value = GetterUtil.getFloat(
			_conditionJsonObject.get(
				ClauseConfigurationKeys.MATCH_VALUE.getJsonKey()));

		if (_not) {
			return !parameter.equalsTo(value);
		}

		return parameter.equalsTo(value);
	}

	@Override
	public boolean visit(IntegerArrayParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public boolean visit(IntegerParameter parameter)
		throws ParameterEvaluationException {

		Integer value = _conditionJsonObject.getInt(
			ClauseConfigurationKeys.MATCH_VALUE.getJsonKey());

		if (_not) {
			return !parameter.equalsTo(value);
		}

		return parameter.equalsTo(value);
	}

	@Override
	public boolean visit(LongArrayParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public boolean visit(LongParameter parameter)
		throws ParameterEvaluationException {

		Long value = _conditionJsonObject.getLong(
			ClauseConfigurationKeys.MATCH_VALUE.getJsonKey());

		if (_not) {
			return !parameter.equalsTo(value);
		}

		return parameter.equalsTo(value);
	}

	@Override
	public boolean visit(Parameter parameter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean visit(StringArrayParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public boolean visit(StringParameter parameter)
		throws ParameterEvaluationException {

		String value = _conditionJsonObject.getString(
			ClauseConfigurationKeys.MATCH_VALUE.getJsonKey());

		if (_not) {
			return !parameter.getValue(
			).equals(
				value
			);
		}

		return parameter.getValue(
		).equals(
			value
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(EqualsVisitor.class);

	private final JSONObject _conditionJsonObject;
	private final boolean _not;

}