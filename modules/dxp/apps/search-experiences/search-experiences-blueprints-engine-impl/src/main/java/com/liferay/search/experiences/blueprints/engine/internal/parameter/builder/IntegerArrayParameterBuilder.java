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

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.search.experiences.blueprints.constants.json.keys.parameter.CustomParameterConfigurationKeys;
import com.liferay.search.experiences.blueprints.engine.attributes.BlueprintsAttributes;
import com.liferay.search.experiences.blueprints.engine.internal.attributes.util.BlueprintsAttributesHelper;
import com.liferay.search.experiences.blueprints.engine.parameter.IntegerArrayParameter;
import com.liferay.search.experiences.blueprints.engine.parameter.Parameter;
import com.liferay.search.experiences.blueprints.message.Messages;
import com.liferay.search.experiences.blueprints.util.util.BlueprintJSONUtil;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, property = "name=integer_array",
	service = ParameterBuilder.class
)
public class IntegerArrayParameterBuilder implements ParameterBuilder {

	@Override
	public Optional<Parameter> build(
		BlueprintsAttributes blueprintsAttributes, JSONObject jsonObject,
		Messages messages) {

		String parameterName = jsonObject.getString(
			CustomParameterConfigurationKeys.PARAMETER_NAME.getJsonKey());

		Optional<String[]> stringArrayOptional = _getValueArrayOptional(
			blueprintsAttributes, jsonObject, parameterName);

		if (!stringArrayOptional.isPresent()) {
			return Optional.empty();
		}

		return _toLongArrayParameter(stringArrayOptional.get(), parameterName);
	}

	private Optional<String[]> _getValueArrayOptional(
		BlueprintsAttributes blueprintsAttributes, JSONObject jsonObject,
		String parameterName) {

		Optional<String[]> stringArrayOptional =
			_blueprintsAttributesHelper.getStringArrayOptional(
				blueprintsAttributes, parameterName);

		if (!stringArrayOptional.isPresent()) {
			stringArrayOptional = BlueprintJSONUtil.getStringArray(
				jsonObject,
				CustomParameterConfigurationKeys.DEFAULT.getJsonKey());
		}

		return stringArrayOptional;
	}

	private Optional<Parameter> _toLongArrayParameter(
		String[] arr, String parameterName) {

		try {
			Stream<String> stream = Arrays.stream(arr);

			Integer[] integerArray = stream.map(
				Long::valueOf
			).toArray(
				Integer[]::new
			);

			return Optional.of(
				new IntegerArrayParameter(
					parameterName, "${parameter." + parameterName + "}",
					integerArray));
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					numberFormatException.getMessage(), numberFormatException);
			}
		}

		return Optional.empty();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IntegerArrayParameterBuilder.class);

	@Reference
	private BlueprintsAttributesHelper _blueprintsAttributesHelper;

}