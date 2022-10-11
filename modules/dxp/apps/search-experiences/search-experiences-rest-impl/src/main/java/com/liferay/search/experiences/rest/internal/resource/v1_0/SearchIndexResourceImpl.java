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

package com.liferay.search.experiences.rest.internal.resource.v1_0;

import com.liferay.portal.search.index.IndexInformation;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.search.experiences.rest.dto.v1_0.SearchIndex;
import com.liferay.search.experiences.rest.resource.v1_0.SearchIndexResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	enabled = false,
	properties = "OSGI-INF/liferay/rest/v1_0/search-index.properties",
	scope = ServiceScope.PROTOTYPE, service = SearchIndexResource.class
)
public class SearchIndexResourceImpl extends BaseSearchIndexResourceImpl {

	public List<SearchIndex> getSearchIndexes() {
		List<SearchIndex> searchIndexes = new ArrayList<>();

		String companyIndexName = _indexInformation.getCompanyIndexName(
			contextCompany.getCompanyId());

		String indexNamePrefix = companyIndexName.substring(
			0,
			companyIndexName.indexOf(
				String.valueOf(contextCompany.getCompanyId())));

		String[] indexNames = _indexInformation.getIndexNames();

		for (String indexName : indexNames) {
			if (Objects.equals(indexName, companyIndexName) ||
				Objects.equals(indexName, indexNamePrefix + "0") ||
				!indexName.startsWith(
					String.valueOf(contextCompany.getCompanyId()))) {

				continue;
			}

			searchIndexes.add(
				new SearchIndex() {
					{
						fullName = indexName;
						name = _getIndexName(indexName, indexNamePrefix);
					}
				});
		}

		return searchIndexes;
	}

	@Override
	public Page<SearchIndex> getSearchIndexesPage() throws Exception {
		return Page.of(getSearchIndexes());
	}

	private String _getIndexName(String indexName, String indexNamePrefix) {
		if ((indexName != null) && (indexNamePrefix != null) &&
			indexName.startsWith(indexNamePrefix)) {

			return indexName.substring(indexNamePrefix.length());
		}

		return indexName;
	}

	@Reference
	private IndexInformation _indexInformation;

}