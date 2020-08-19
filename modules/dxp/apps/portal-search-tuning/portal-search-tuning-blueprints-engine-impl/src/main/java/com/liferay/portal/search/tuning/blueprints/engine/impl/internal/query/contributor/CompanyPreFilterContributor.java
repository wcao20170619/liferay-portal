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

package com.liferay.portal.search.tuning.blueprints.engine.impl.internal.query.contributor;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.tuning.blueprints.constants.json.values.Occur;
import com.liferay.portal.search.tuning.blueprints.engine.context.SearchRequestContext;
import com.liferay.portal.search.tuning.blueprints.engine.exception.SearchRequestDataException;
import com.liferay.portal.search.tuning.blueprints.engine.spi.query.QueryContributor;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = QueryContributor.class)
public class CompanyPreFilterContributor implements QueryContributor {

	@Override
	public Optional<Query> build(SearchRequestContext searchRequestContext)
		throws SearchRequestDataException {

		return Optional.of(
			_queries.term(
				Field.COMPANY_ID, searchRequestContext.getCompanyId()));
	}

	@Override
	public Occur getOccur() {
		return Occur.MUST;
	}

	@Reference
	private Queries _queries;

}