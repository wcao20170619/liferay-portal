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

package com.liferay.portal.search.internal.aggregation.metrics;

import com.liferay.portal.search.aggregation.metrics.PercentilesAggregationResult;
import com.liferay.portal.search.internal.aggregation.BaseAggregationResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class PercentilesAggregationResultImpl
	extends BaseAggregationResult implements PercentilesAggregationResult {

	public PercentilesAggregationResultImpl(String name) {
		super(name);
	}

	@Override
	public void addPercentile(double percent, double percentile) {
		_percentiles.put(percent, percentile);
	}

	@Override
	public Map<Double, Double> getPercentiles() {
		return Collections.unmodifiableMap(_percentiles);
	}

	private final Map<Double, Double> _percentiles = new HashMap<>();

}