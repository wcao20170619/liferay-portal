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

package com.liferay.portal.search.aggregation.bucket;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.search.aggregation.FieldAggregation;

import java.util.List;

/**
 * @author Michael C. Han
 */
@ProviderType
public interface RangeAggregation extends FieldAggregation {

	public void addRange(Range range);

	public void addRanges(Range... ranges);

	public void addUnboundedFrom(Double from);

	public void addUnboundedFrom(String key, Double from);

	public void addUnboundedTo(Double to);

	public void addUnboundedTo(String key, Double to);

	public String getFormat();

	public Boolean getKeyed();

	public List<Range> getRanges();

	public void setFormat(String format);

	public void setKeyed(Boolean keyed);

}