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

package com.liferay.apio.architect.resource;

import aQute.bnd.annotation.ConsumerType;

import com.liferay.apio.architect.router.NestedCollectionRouter;

/**
 * Maps your domain models to resources that Apio can understand.
 *
 * <p>
 * Resources behave like an API so you must add the API's name via the {@link
 * #getName()} method.
 * </p>
 *
 * <p>
 * Representors created by the {@link
 * #representor(com.liferay.apio.architect.representor.Representor.Builder)}
 * method hold all the information needed to write your domain models'
 * hypermedia representations.
 * </p>
 *
 * <p>
 * You can add the different supported routes for the collection resource via
 * the {@link
 * #collectionRoutes(
 * com.liferay.apio.architect.routes.NestedCollectionRoutes.Builder)} method.
 * </p>
 *
 * <p>
 * You can add the different supported routes for the single resource via the
 * {@link #itemRoutes(com.liferay.apio.architect.routes.ItemRoutes.Builder)}
 * (Routes.Builder)} method.
 * </p>
 *
 * @author Alejandro Hernández
 * @see    com.liferay.apio.architect.representor.Representor.Builder
 * @see    com.liferay.apio.architect.routes.ItemRoutes.Builder
 * @see    com.liferay.apio.architect.routes.NestedCollectionRoutes.Builder
 * @review
 */
@ConsumerType
public interface NestedCollectionResource <T, U, V, W>
	extends ItemResource<T, U>, NestedCollectionRouter<T, V, W> {
}