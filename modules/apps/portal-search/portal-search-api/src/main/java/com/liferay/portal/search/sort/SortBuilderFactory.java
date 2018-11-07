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

package com.liferay.portal.search.sort;

import java.util.Collection;
import java.util.Collections;

import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

/**
 * @author Wade Cao
 */
public interface SortBuilderFactory {

	public SortBuilder getBuilder();	
		
	public static SortBuilderFactory getSortBuilderFactory() {
		Registry registry = RegistryUtil.getRegistry();
		
		Collection<SortBuilderFactory> sortBuilderFactories = Collections.emptySet();
		try {
			sortBuilderFactories = registry.getServices(
				SortBuilderFactory.class, null);
		} 
		catch (Exception e) {
		}
		
		if (sortBuilderFactories.isEmpty()) {
			return null;
		}
		return sortBuilderFactories.iterator().next();
	}
	
}
