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

package com.liferay.search.experiences.blueprints.engine.spi.suggester;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.suggest.Suggester;
import com.liferay.search.experiences.blueprints.engine.parameter.ParameterData;
import com.liferay.search.experiences.blueprints.message.Messages;

import java.util.Optional;

/**
 * @author Petteri Karttunen
 */
public interface SuggesterTranslator {

	public Optional<Suggester> translate(
		String suggesterName, JSONObject jsonObject,
		ParameterData parameterData, Messages messages);

}