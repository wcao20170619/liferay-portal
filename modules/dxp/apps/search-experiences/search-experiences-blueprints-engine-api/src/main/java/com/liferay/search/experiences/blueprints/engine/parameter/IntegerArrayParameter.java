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

package com.liferay.search.experiences.blueprints.engine.parameter;

import com.liferay.petra.string.StringBundler;
import com.liferay.search.experiences.blueprints.engine.exception.ParameterEvaluationException;
import com.liferay.search.experiences.blueprints.engine.parameter.visitor.EvaluationVisitor;
import com.liferay.search.experiences.blueprints.engine.parameter.visitor.ToStringVisitor;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Petteri Karttunen
 */
public class IntegerArrayParameter implements Parameter {

	public IntegerArrayParameter(
		String name, String templateVariable, Integer[] value) {

		_name = name;
		_templateVariable = templateVariable;
		_value = value;
	}

	@Override
	public boolean accept(EvaluationVisitor visitor)
		throws ParameterEvaluationException {

		return visitor.visit(this);
	}

	@Override
	public String accept(ToStringVisitor visitor, Map<String, String> options)
		throws Exception {

		return visitor.visit(this, options);
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getTemplateVariable() {
		return _templateVariable;
	}

	@Override
	public Integer[] getValue() {
		return _value;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(7);

		sb.append("IntegerArrayParameter [_name=");
		sb.append(_name);
		sb.append(", _templateVariable=");
		sb.append(_templateVariable);
		sb.append(", _value=");
		sb.append(Arrays.toString(_value));
		sb.append("]");

		return sb.toString();
	}

	private final String _name;
	private final String _templateVariable;
	private final Integer[] _value;

}