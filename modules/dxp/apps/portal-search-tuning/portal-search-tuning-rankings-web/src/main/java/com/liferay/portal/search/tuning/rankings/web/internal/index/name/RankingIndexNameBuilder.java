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

package com.liferay.portal.search.tuning.rankings.web.internal.index.name;

import com.liferay.portal.search.index.IndexNameBuilder;

/**
 * @author Adam Brandizzi
 */
public interface RankingIndexNameBuilder extends IndexNameBuilder {

	public RankingIndexName getRankingIndexName(long companyId);
	
	public RankingIndexName getRankingIndexName(String indexName);
	
	public RankingIndexName getRanking73IndexName(String companyIndexName);
}