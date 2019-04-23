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

package com.liferay.portal.search.filter;

import aQute.bnd.annotation.ProviderType;

/**
 * @author André de Oliveira
 */
@ProviderType
public interface ComplexQueryPartBuilder {

	public ComplexQueryPartBuilder boost(Float boost);

	public ComplexQueryPart build();

	public ComplexQueryPartBuilder disabled(boolean disabled);

	public ComplexQueryPartBuilder field(String field);

	public ComplexQueryPartBuilder name(String name);

	public ComplexQueryPartBuilder occur(String occur);

	public ComplexQueryPartBuilder parent(String parent);
	
	public ComplexQueryPartBuilder range(boolean range);

	public ComplexQueryPartBuilder type(String type);

	public ComplexQueryPartBuilder value(String value);
	
	public ComplexQueryPartBuilder includesLower(boolean includesLower);
	
	public ComplexQueryPartBuilder includesUpper(boolean includesUpper);
	
	public ComplexQueryPartBuilder lowerBound(String lowerBound);
	
	public ComplexQueryPartBuilder upperBound(String upperBound);

}