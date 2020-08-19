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

package com.liferay.portal.search.tuning.blueprints.engine.impl.internal.clause.builder;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.WrapperQuery;
import com.liferay.portal.search.tuning.blueprints.constants.json.keys.query.SimpleQueryStringConfigurationKeys;
import com.liferay.portal.search.tuning.blueprints.engine.context.SearchRequestContext;
import com.liferay.portal.search.tuning.blueprints.engine.spi.clause.ClauseBuilder;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, property = "type=wrapper", service = ClauseBuilder.class
)
public class WrapperClauseBuilder implements ClauseBuilder {

	@Override
	public Optional<Query> buildClause(
		SearchRequestContext queryContext, JSONObject configurationJsonObject) {

		String query = configurationJsonObject.getString(
			SimpleQueryStringConfigurationKeys.QUERY.getJsonKey());

		WrapperQuery wrapperQuery = _queries.wrapper(query);

		return Optional.of(wrapperQuery);
	}

	@Reference
	private Queries _queries;

}