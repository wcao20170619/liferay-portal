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

package com.liferay.portal.search.tuning.blueprints.engine.parameter;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.tuning.blueprints.constants.json.values.EvaluationType;
import com.liferay.portal.search.tuning.blueprints.engine.exception.ParameterEvaluationException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Petteri Karttunen
 */
public class DateParameter implements Parameter {

	public DateParameter(
		String name, String role, String configurationVariable, Date value) {

		_name = name;
		_role = role;
		_configurationVariable = configurationVariable;
		_value = value;
	}

	@Override
	public boolean accept(EvaluationVisitor evaluationVisitor)
		throws ParameterEvaluationException {

		return evaluationVisitor.visit(this);
	}
	
	@Override
	public String accept(ToStringVisitor parameterToStringVisitor) throws Exception {
		throw new UnsupportedOperationException();
	}
	
	public String accept(ToStringVisitor toStringVisitor, String dateOutputFormat) throws Exception {
		return toStringVisitor.visit(this, dateOutputFormat);		
	}

	@Override
	public String getConfigurationVariable() {
		return _configurationVariable;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Optional<String> getRoleOptional() {
		if (!Validator.isBlank(_role)) {
			return Optional.of(_role);
		}

		return Optional.empty();
	}

	@Override
	public List<EvaluationType> getSupportedEvaluationTypes() {
		List<EvaluationType> matchTypes = new ArrayList<>();

		matchTypes.add(EvaluationType.EQ);
		matchTypes.add(EvaluationType.NE);
		matchTypes.add(EvaluationType.GT);
		matchTypes.add(EvaluationType.GTE);
		matchTypes.add(EvaluationType.LTE);
		matchTypes.add(EvaluationType.IN_RANGE);
		matchTypes.add(EvaluationType.NOT_IN_RANGE);

		return matchTypes;
	}

	@Override
	public Date getValue() {
		return _value;
	}

	private final String _configurationVariable;
	private final String _name;
	private final String _role;
	private final Date _value;
}