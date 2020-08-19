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

package com.liferay.portal.search.tuning.blueprints.internal.validator;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.tuning.blueprints.exception.BlueprintValidationException;
import com.liferay.portal.search.tuning.blueprints.validation.BlueprintValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = BlueprintValidator.class)
public class BlueprintValidatorImpl
	implements BlueprintValidator {

	@Override
	public void validate(
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String configuration)
		throws BlueprintValidationException {

		List<String> errors = new ArrayList<>();

		if (!_isBlueprintValid(
				titleMap, descriptionMap, configuration, errors)) {

			throw new BlueprintValidationException(errors);
		}
	}

	private boolean _isConfigurationValid(
		String configuration, List<String> errors) {

		// TODO Auto-generated method stub

		return true;
	}

	private boolean _isDescriptionValid(
		final Map<Locale, String> descriptionMap, final List<String> errors) {

		boolean result = true;

		if (MapUtil.isEmpty(descriptionMap)) {
			errors.add("descriptionEmpty");
			result = false;
		}
		else {
			Locale defaultLocale = LocaleUtil.getDefault();

			if (Validator.isBlank(descriptionMap.get(defaultLocale))) {
				errors.add("defaultLocaleDescriptionEmpty");
				result = false;
			}
		}

		return result;
	}

	private boolean _isBlueprintValid(
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String configuration, List<String> errors)
		throws BlueprintValidationException {

		boolean result = true;

		// TODO: https://issues.liferay.com/browse/LPS-118942
		
		// result &= _isDescriptionValid(descriptionMap, errors);
		result &= _isConfigurationValid(configuration, errors);
		result &= _isTitleValid(titleMap, errors);

		return result;
	}

	private boolean _isTitleValid(
		final Map<Locale, String> titleMap, final List<String> errors) {

		boolean result = true;

		if (MapUtil.isEmpty(titleMap)) {
			errors.add("titleEmpty");
			result = false;
		}
		else {
			Locale defaultLocale = LocaleUtil.getDefault();

			if (Validator.isBlank(titleMap.get(defaultLocale))) {
				errors.add("defaultLocaleTitleEmpty");
				result = false;
			}
		}

		return result;
	}

}