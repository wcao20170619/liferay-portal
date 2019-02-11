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

import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.BucketAggregationResult;
import com.liferay.portal.search.internal.aggregation.BaseAggregationResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class BaseBucketAggregationResult
	extends BaseAggregationResult implements BucketAggregationResult {

	public BaseBucketAggregationResult(String name) {
		super(name);
	}

	@Override
	public Bucket addBucket(String key, long docCount) {
		Bucket bucket = new BucketImpl(key, docCount);

		_buckets.put(bucket.getKey(), bucket);

		return bucket;
	}

	@Override
	public Bucket getBucket(String key) {
		return _buckets.get(key);
	}

	@Override
	public Collection<Bucket> getBuckets() {
		return Collections.unmodifiableCollection(_buckets.values());
	}

	private final Map<String, Bucket> _buckets = new LinkedHashMap<>();

}