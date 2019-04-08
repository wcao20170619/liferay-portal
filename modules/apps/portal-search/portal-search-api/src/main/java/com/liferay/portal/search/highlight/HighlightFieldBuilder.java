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

package com.liferay.portal.search.highlight;

import aQute.bnd.annotation.ProviderType;

import java.util.stream.Stream;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
@ProviderType
public interface HighlightFieldBuilder {

	public HighlightField build();

	public HighlightFieldBuilder fragment(String fragment);

	public HighlightFieldBuilder fragments(Stream<String> fragmentStream);

	public HighlightFieldBuilder name(String name);

}