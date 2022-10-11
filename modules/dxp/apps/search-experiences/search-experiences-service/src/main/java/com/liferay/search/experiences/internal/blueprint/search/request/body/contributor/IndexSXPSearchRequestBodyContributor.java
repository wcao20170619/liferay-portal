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

package com.liferay.search.experiences.internal.blueprint.search.request.body.contributor;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.index.IndexInformation;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.search.experiences.internal.blueprint.parameter.SXPParameterData;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.IndexConfiguration;

/**
 * @author Wade Cao
 */
public class IndexSXPSearchRequestBodyContributor
	implements SXPSearchRequestBodyContributor {

	public IndexSXPSearchRequestBodyContributor(
		IndexInformation indexInformation) {

		_indexInformation = indexInformation;
	}

	@Override
	public void contribute(
		Configuration configuration, SearchRequestBuilder searchRequestBuilder,
		SXPParameterData sxpParameterData) {

		IndexConfiguration indexConfiguration =
			configuration.getIndexConfiguration();

		if (indexConfiguration == null) {
			return;
		}

		String indexName = _getFullIndexName(indexConfiguration.getIndexName());

		if (!Validator.isBlank(indexName)) {
			searchRequestBuilder.addIndex(indexName);
		}
	}

	@Override
	public String getName() {
		return "indexConfiguration";
	}

	private String _getFullIndexName(String indexName) {
		String companyId = indexName.substring(
			0, indexName.indexOf(StringPool.DASH));

		try {
			String companyIndexName = _indexInformation.getCompanyIndexName(
				Long.valueOf(companyId));

			String indexNamePrefix = companyIndexName.substring(
				0, companyIndexName.indexOf(companyId));

			return indexNamePrefix + indexName;
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to parse company ID" + companyId,
					numberFormatException);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexSXPSearchRequestBodyContributor.class);

	private final IndexInformation _indexInformation;

}