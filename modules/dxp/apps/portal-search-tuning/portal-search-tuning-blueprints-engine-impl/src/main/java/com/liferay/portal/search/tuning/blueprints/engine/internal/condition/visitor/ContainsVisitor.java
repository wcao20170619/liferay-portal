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

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author Petteri Karttunen
 */
public class ContainsVisitor implements ConditionEvaluationVisitor {

	public ContainsVisitor(JSONObject conditionJSONObject, boolean not) {
		_conditionJSONObject = conditionJSONObject;
		_not = not;
	}

	@Override
	public boolean visit(BooleanParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean visit(DateParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean visit(DoubleParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean visit(FloatParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean visit(IntegerArrayParameter parameter)
		throws ParameterEvaluationException {

		Object object = _conditionJSONObject.get(
			ConditionConfigurationKeys.MATCH_VALUE.getJsonKey());

		Integer[] parameterValue = parameter.getValue();

		try {
			boolean match = false;

			if (object instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray)object;

				for (int i = 0; i < jsonArray.length(); i++) {
					Integer value = jsonArray.getInt(i);

					Stream<Integer> stream = Arrays.stream(parameterValue);

					if (stream.anyMatch(
							x -> x.intValue() == value.intValue())) {

						match = true;

						break;
					}
				}
			}
			else {
				Integer value = Integer.valueOf((String)object);

				Stream<Integer> stream = Arrays.stream(parameterValue);

				match = stream.anyMatch(
					x -> x.longValue() == value.longValue());
			}

			if (_not) {
				return !match;
			}

			return match;
		}
		catch (NumberFormatException numberFormatException) {
			_log.error(
				"Illegal clause condition match value  " + object.toString() +
					".",
				numberFormatException);

			throw new ParameterEvaluationException(
				new Message.Builder().className(
					getClass().getName()
				).localizationKey(
					"core.error.illegal-clause-condition-match-value"
				).msg(
					numberFormatException.getMessage()
				).rootObject(
					_conditionJSONObject
				).rootProperty(
					ConditionConfigurationKeys.MATCH_VALUE.getJsonKey()
				).rootValue(
					object.toString()
				).severity(
					Severity.ERROR
				).throwable(
					numberFormatException
				).build());
		}
	}

	@Override
	public boolean visit(IntegerParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean visit(LongArrayParameter parameter)
		throws ParameterEvaluationException {

		Object object = _conditionJSONObject.get(
			ConditionConfigurationKeys.MATCH_VALUE.getJsonKey());

		Long[] parameterValue = parameter.getValue();

		try {
			boolean match = false;

			if (object instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray)object;

				for (int i = 0; i < jsonArray.length(); i++) {
					Long value = jsonArray.getLong(i);

					Stream<Long> parameterValueStream = Arrays.stream(
						parameterValue);

					if (parameterValueStream.anyMatch(
							x -> x.longValue() == value.longValue())) {

						match = true;

						break;
					}
				}
			}
			else {
				Long value = Long.valueOf((String)object);

				Stream<Long> parameterValueStream = Arrays.stream(
					parameterValue);

				match = parameterValueStream.anyMatch(
					x -> x.longValue() == value.longValue());
			}

			if (_not) {
				return !match;
			}

			return match;
		}
		catch (NumberFormatException numberFormatException) {
			_log.error(
				"Illegal clause condition match value  " + object.toString() +
					".",
				numberFormatException);

			throw new ParameterEvaluationException(
				new Message.Builder().className(
					getClass().getName()
				).localizationKey(
					"core.error.illegal-clause-condition-match-value"
				).msg(
					numberFormatException.getMessage()
				).rootObject(
					_conditionJSONObject
				).rootProperty(
					ConditionConfigurationKeys.MATCH_VALUE.getJsonKey()
				).rootValue(
					object.toString()
				).severity(
					Severity.ERROR
				).throwable(
					numberFormatException
				).build());
		}
	}

	@Override
	public boolean visit(LongParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean visit(StringArrayParameter parameter)
		throws ParameterEvaluationException {

		Object object = _conditionJSONObject.get(
			ConditionConfigurationKeys.MATCH_VALUE.getJsonKey());

		String[] parameterValue = parameter.getValue();

		boolean match = false;

		if (object instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray)object;

			for (int i = 0; i < jsonArray.length(); i++) {
				String value = jsonArray.getString(i);

				Stream<String> stream = Arrays.stream(parameterValue);

				if (stream.anyMatch(value::equals)) {
					match = true;

					break;
				}
			}
		}
		else {
			String value = String.valueOf(object);

			Stream<String> stream = Arrays.stream(parameterValue);

			match = stream.anyMatch(value::equals);
		}

		if (_not) {
			return !match;
		}

		return match;
	}

	@Override
	public boolean visit(StringParameter parameter)
		throws ParameterEvaluationException {

		throw new UnsupportedOperationException();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContainsVisitor.class);

	private final JSONObject _conditionJSONObject;
	private final boolean _not;

}