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

import com.liferay.portal.search.aggregation.metrics.StatsAggregationResult;
import com.liferay.portal.search.internal.aggregation.BaseAggregationResult;

/**
 * @author Michael C. Han
 */
public class StatsAggregationResultImpl
	extends BaseAggregationResult implements StatsAggregationResult {

	public StatsAggregationResultImpl(
		String name, double avg, long count, double min, double max,
		double sum) {

		super(name);

		_avg = avg;
		_count = count;
		_min = min;
		_max = max;
		_sum = sum;
	}

	@Override
	public double getAvg() {
		return _avg;
	}

	@Override
	public long getCount() {
		return _count;
	}

	@Override
	public double getMax() {
		return _max;
	}

	@Override
	public double getMin() {
		return _min;
	}

	@Override
	public double getSum() {
		return _sum;
	}

	private final double _avg;
	private final long _count;
	private final double _max;
	private final double _min;
	private final double _sum;

}