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

package com.liferay.portal.search.internal.aggregation.pipeline;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.search.aggregation.pipeline.StatsBucketPipelineAggregationResult;
import com.liferay.portal.search.internal.aggregation.metrics.StatsAggregationResultImpl;

/**
 * @author Michael C. Han
 */
@ProviderType
public class StatsBucketPipelineAggregationResultImpl
	extends StatsAggregationResultImpl
	implements StatsBucketPipelineAggregationResult {

	public StatsBucketPipelineAggregationResultImpl(
		String name, double avg, long count, double min, double max,
		double sum) {

		super(name, avg, count, min, max, sum);
	}

}