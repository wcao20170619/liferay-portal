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

package com.liferay.portal.search.internal.batch;

import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.batch.DynamicQueryBatchIndexingActionableFactory;

import org.osgi.service.component.annotations.Component;

/**
 * @author André de Oliveira
 */
@Component(
	immediate = true, service = DynamicQueryBatchIndexingActionableFactory.class
)
public class DynamicQueryBatchIndexingActionableFactoryImpl
	implements DynamicQueryBatchIndexingActionableFactory {

	@Override
	public BatchIndexingActionable getBatchIndexingActionable(
		IndexableActionableDynamicQuery indexableActionableDynamicQuery) {

		return new DynamicQueryBatchIndexingActionableAdapter(
			indexableActionableDynamicQuery);
	}

}