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

package com.liferay.search.experiences.blueprints.engine.internal.clause.translator;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.WrapperQuery;
import com.liferay.search.experiences.blueprints.constants.json.keys.query.WrapperQueryConfigurationKeys;
import com.liferay.search.experiences.blueprints.engine.parameter.ParameterData;
import com.liferay.search.experiences.blueprints.engine.spi.clause.ClauseTranslator;
import com.liferay.search.experiences.blueprints.message.Messages;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, property = "name=wrapper",
	service = ClauseTranslator.class
)
public class WrapperClauseTranslator implements ClauseTranslator {

	@Override
	public Optional<Query> translate(
		JSONObject jsonObject, ParameterData parameterData, Messages messages) {

		WrapperQuery wrapperQuery = _queries.wrapper(
			jsonObject.getString(
				WrapperQueryConfigurationKeys.QUERY.getJsonKey()));

		return Optional.of(wrapperQuery);
	}

	@Reference
	private Queries _queries;

}