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

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
@ProviderType
public interface FieldConfig {

	public String getField();

	public Integer getFragmentOffset();

	public Integer getFragmentSize();

	public Integer getNumFragments();

	@ProviderType
	public interface Builder {

		public FieldConfig build();

		public Builder field(String field);

		public Builder fragmentOffset(Integer fragmentOffset);

		public Builder fragmentSize(Integer fragmentSize);

		public Builder numFragments(Integer numFragments);

	}

}