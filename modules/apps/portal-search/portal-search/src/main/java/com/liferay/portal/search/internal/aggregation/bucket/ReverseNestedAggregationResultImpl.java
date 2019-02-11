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

package com.liferay.portal.search.internal.aggregation.bucket;

import com.liferay.portal.search.aggregation.bucket.ReverseNestedAggregationResult;

/**
 * @author Michael C. Han
 */
public class ReverseNestedAggregationResultImpl
	extends BaseHierarchicalAggregationResult
	implements ReverseNestedAggregationResult {

	public ReverseNestedAggregationResultImpl(String name, long docCount) {
		super(name);

		_docCount = docCount;
	}

	@Override
	public long getDocCount() {
		return _docCount;
	}

	@Override
	public void setDocCount(long docCount) {
		_docCount = docCount;
	}

	private long _docCount;

}