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

package com.liferay.portal.search.elasticsearch6.internal.index.create;

import com.liferay.portal.search.elasticsearch6.internal.field.FieldRegistrySynchronizer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(immediate = true, service = CreateIndexRequestFactory.class)
public class CreateIndexRequestFactoryImpl
	implements CreateIndexRequestFactory {

	@Override
	public CreateIndexRequest create(CreateIndexOptions createIndexOptions) {
		return new CreateIndexRequestImpl(
			createIndexOptions, fieldRegistrySynchronizer);
	}

	@Override
	public CreateIndexOptionsBuilder createOptionsBuilder() {
		return new CreateIndexOptionsBuilderImpl();
	}

	@Reference
	protected FieldRegistrySynchronizer fieldRegistrySynchronizer;

}