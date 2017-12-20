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

package com.liferay.apio.architect.representor;

import aQute.bnd.annotation.ConsumerType;

import com.liferay.apio.architect.representor.Representor.Builder;

/**
 * Instances of this interface represent the mapping between an internal model
 * and its generic representation.
 *
 * <p>
 * Representors created by the {@link #representor(Representor.Builder)} method
 * hold all the information needed to write your domain models' hypermedia
 * representations.
 * </p>
 *
 * <p>
 * The union of an instance of this interface and one or more routers from the
 * {@code com.liferay.apio.architect.router} package, such as {@link
 * com.liferay.apio.architect.router.ItemRouter} creates a complete resource
 * that will behave as its own API.
 * </p>
 *
 * @author Alejandro Hernández
 * @see    Representor.Builder
 * @review
 */
@ConsumerType
public interface Representable<T, U> {

	/**
	 * Returns the resource's name.
	 *
	 * @return the resource's name
	 */
	public String getName();

	/**
	 * Creates a {@link Representor} for a certain domain model from the
	 * provided {@link Representor.Builder}.
	 *
	 * <p>
	 * To construct a representor, you must call {@link
	 * Representor.Builder.FirstStep#build()} ()}. Before calling this method,
	 * you must call the other representor builder methods to populate the
	 * builder with data. This ensures that the resulting representor contains
	 * the data.
	 * </p>
	 *
	 * @param builder the representor builder used to create the representor
	 * @see   Representor.Builder
	 */
	public Representor<T, U> representor(Builder<T, U> builder);

}