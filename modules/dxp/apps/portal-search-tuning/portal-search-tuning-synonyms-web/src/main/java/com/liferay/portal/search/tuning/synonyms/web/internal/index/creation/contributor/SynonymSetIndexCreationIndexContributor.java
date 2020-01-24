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

package com.liferay.portal.search.tuning.synonyms.web.internal.index.creation.contributor;

import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.spi.model.index.contributor.IndexContributor;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReader;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.synchronizer.IndexToFilterSynchronizer;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = IndexContributor.class)
public class SynonymSetIndexCreationIndexContributor
	implements IndexContributor {

	@Override
	public void onAfterCreate(String companyIndexName) {
		if (Objects.equals(
				_searchEngineInformation.getVendorString(), "Solr")) {

			return;
		}

		if (!_synonymSetIndexReader.isExists(
				_synonymSetIndexNameBuilder.getSynonymSetIndexName(
					companyIndexName))) {

			return;
		}

		_indexToFilterSynchronizer.copyToFilter(companyIndexName);
	}

	@Reference
	private IndexToFilterSynchronizer _indexToFilterSynchronizer;

	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile SearchEngineInformation _searchEngineInformation;

	@Reference
	private SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder;

	@Reference
	private SynonymSetIndexReader _synonymSetIndexReader;

}