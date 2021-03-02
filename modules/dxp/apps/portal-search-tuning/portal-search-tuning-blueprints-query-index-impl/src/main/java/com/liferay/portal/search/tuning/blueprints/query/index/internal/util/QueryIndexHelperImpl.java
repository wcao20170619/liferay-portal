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

package com.liferay.portal.search.tuning.blueprints.query.index.internal.util;

import com.liferay.portal.search.tuning.blueprints.query.index.util.QueryIndexHelper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = QueryIndexHelper.class)
public class QueryIndexHelperImpl implements QueryIndexHelper {

	@Override
	public String getIndexName(String configurationId) {

		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public void indexQuery(
		String queryIndexConfigurationId, Long companyId, String languageId,
		String keywords) {

		// TODO Auto-generated method stub

	}

}