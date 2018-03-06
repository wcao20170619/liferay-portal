/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.search.spi.model.registrar;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.QueryConfigContributor;
import com.liferay.portal.search.spi.model.query.contributor.QueryPreFilterContributor;
import com.liferay.portal.search.spi.model.query.contributor.SearchContextContributor;
import com.liferay.portal.search.spi.model.result.contributor.ModelPermissionPostFilter;
import com.liferay.portal.search.spi.model.result.contributor.ModelSummaryContributor;

/**
 * @author André de Oliveira
 */
@ProviderType
public interface ModelSearchConfigurator<T extends BaseModel<?>> {

	public void close();

	public String getClassName();

	public Iterable<KeywordQueryContributor> getKeywordQueryContributors();

	public Iterable<ModelDocumentContributor> getModelDocumentContributors();

	public ModelIndexerWriterContributor<T> getModelIndexerWriterContributor();

	public ModelPermissionPostFilter getModelPermissionPostFilter();

	public ModelSearchSettings getModelSearchSettings();

	public ModelSummaryContributor getModelSummaryBuilder();

	public Iterable<QueryConfigContributor> getQueryConfigContributors();

	public Iterable<QueryPreFilterContributor> getQueryPreFilterContributors();

	public Iterable<SearchContextContributor> getSearchContextContributors();

}