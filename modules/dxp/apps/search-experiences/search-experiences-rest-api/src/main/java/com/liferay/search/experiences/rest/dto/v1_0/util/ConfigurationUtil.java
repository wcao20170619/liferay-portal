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

package com.liferay.search.experiences.rest.dto.v1_0.util;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.search.experiences.rest.dto.v1_0.AggregationConfiguration;
import com.liferay.search.experiences.rest.dto.v1_0.Clause;
import com.liferay.search.experiences.rest.dto.v1_0.Condition;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.IndexConfiguration;
import com.liferay.search.experiences.rest.dto.v1_0.QueryConfiguration;
import com.liferay.search.experiences.rest.dto.v1_0.Rescore;
import com.liferay.search.experiences.rest.dto.v1_0.SortConfiguration;

/**
 * @author André de Oliveira
 */
public class ConfigurationUtil {

	public static Configuration toConfiguration(String json) {
		if (Validator.isNull(json)) {
			return null;
		}

		return unpack(Configuration.unsafeToDTO(json));
	}

	protected static Configuration unpack(Configuration configuration) {
		if (configuration == null) {
			return null;
		}

		AggregationConfiguration aggregationConfiguration =
			configuration.getAggregationConfiguration();

		if (aggregationConfiguration != null) {
			aggregationConfiguration.setAggs(
				UnpackUtil.unpack(aggregationConfiguration.getAggs()));
		}

		IndexConfiguration indexConfiguration =
			configuration.getIndexConfiguration();

		if (indexConfiguration != null) {
			indexConfiguration.setIndexName(indexConfiguration.getIndexName());
		}

		QueryConfiguration queryConfiguration =
			configuration.getQueryConfiguration();

		if (queryConfiguration != null) {
			ArrayUtil.isNotEmptyForEach(
				queryConfiguration.getQueryEntries(),
				queryEntry -> {
					ArrayUtil.isNotEmptyForEach(
						queryEntry.getClauses(), ConfigurationUtil::_unpack);
					ArrayUtil.isNotEmptyForEach(
						queryEntry.getPostFilterClauses(),
						ConfigurationUtil::_unpack);
					ArrayUtil.isNotEmptyForEach(
						queryEntry.getRescores(), ConfigurationUtil::_unpack);

					Condition condition = queryEntry.getCondition();

					if (condition != null) {
						queryEntry.setCondition(
							ConditionUtil.unpack(condition));
					}
				});
		}

		SortConfiguration sortConfiguration =
			configuration.getSortConfiguration();

		if (sortConfiguration != null) {
			sortConfiguration.setSorts(
				UnpackUtil.unpack(sortConfiguration.getSorts()));
		}

		return configuration;
	}

	private static void _unpack(Clause clause) {
		if (clause == null) {
			return;
		}

		clause.setQuery(UnpackUtil.unpack(clause.getQuery()));
	}

	private static void _unpack(Rescore rescore) {
		if (rescore == null) {
			return;
		}

		rescore.setQuery(UnpackUtil.unpack(rescore.getQuery()));
	}

}