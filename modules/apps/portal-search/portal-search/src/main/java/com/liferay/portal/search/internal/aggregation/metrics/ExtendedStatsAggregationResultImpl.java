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

import com.liferay.portal.search.aggregation.metrics.ExtendedStatsAggregationResult;

/**
 * @author Michael C. Han
 */
public class ExtendedStatsAggregationResultImpl
	extends StatsAggregationResultImpl
	implements ExtendedStatsAggregationResult {

	public ExtendedStatsAggregationResultImpl(
		String name, double avg, long count, double min, double max, double sum,
		double sumOfSquares, double variance, double stdDeviation) {

		super(name, avg, count, min, max, sum);

		_sumOfSquares = sumOfSquares;
		_variance = variance;
		_stdDeviation = stdDeviation;
	}

	@Override
	public double getStdDeviation() {
		return _stdDeviation;
	}

	@Override
	public double getSumOfSquares() {
		return _sumOfSquares;
	}

	@Override
	public double getVariance() {
		return _variance;
	}

	@Override
	public void setStdDeviation(double stdDeviation) {
		_stdDeviation = stdDeviation;
	}

	@Override
	public void setSumOfSquares(double sumOfSquares) {
		_sumOfSquares = sumOfSquares;
	}

	@Override
	public void setVariance(double variance) {
		_variance = variance;
	}

	private double _stdDeviation;
	private double _sumOfSquares;
	private double _variance;

}