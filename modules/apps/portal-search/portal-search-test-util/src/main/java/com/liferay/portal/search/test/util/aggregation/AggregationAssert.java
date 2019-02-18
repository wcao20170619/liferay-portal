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

package com.liferay.portal.search.test.util.aggregation;

import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.BucketAggregationResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;

/**
 * @author André de Oliveira
 */
public class AggregationAssert {

	public static void assertBuckets(
		String expected, BucketAggregationResult bucketAggregationResult) {

		Assert.assertEquals(
			expected,
			String.valueOf(
				new ArrayList<>(bucketAggregationResult.getBuckets())));
	}

	public static void assertBucketValues(
		String expected, Function<Bucket, Double> function,
		BucketAggregationResult bucketAggregationResult) {

		Collection<Bucket> buckets = bucketAggregationResult.getBuckets();

		Stream<Bucket> stream = buckets.stream();

		Assert.assertEquals(
			expected,
			stream.map(
				function
			).map(
				String::valueOf
			).collect(
				Collectors.joining(", ")
			));
	}

}