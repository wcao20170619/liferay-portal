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

package com.liferay.apio.architect.writer.alias;

import com.liferay.apio.architect.function.TriFunction;
import com.liferay.apio.architect.uri.Path;

import java.util.Optional;

/**
 * Defines a type alias for a function that receives an identifier, the
 * identifier's class, and the identifier's model class. The function returns an
 * optional {@link Path} for the identifier.
 *
 * @author Alejandro Hernández
 */
@FunctionalInterface
public interface PathFunction
	extends TriFunction<Object, Class<?>, Class<?>, Optional<Path>> {
}