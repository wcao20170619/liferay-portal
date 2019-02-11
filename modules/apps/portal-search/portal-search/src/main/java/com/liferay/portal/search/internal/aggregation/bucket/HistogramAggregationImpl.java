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

import com.liferay.portal.search.aggregation.AggregationVisitor;
import com.liferay.portal.search.aggregation.bucket.HistogramAggregation;
import com.liferay.portal.search.aggregation.bucket.Order;
import com.liferay.portal.search.internal.aggregation.BaseFieldAggregation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Michael C. Han
 */
public class HistogramAggregationImpl
	extends BaseFieldAggregation implements HistogramAggregation {

	public HistogramAggregationImpl(String name, String field) {
		super(name, field);
	}

	@Override
	public <T> T accept(AggregationVisitor<T> aggregationVisitor) {
		return aggregationVisitor.visit(this);
	}

	@Override
	public void addOrders(Order... orders) {
		Collections.addAll(_orders, orders);
	}

	@Override
	public Double getInterval() {
		return _interval;
	}

	@Override
	public Boolean getKeyed() {
		return _keyed;
	}

	@Override
	public Double getMaxBound() {
		return _maxBound;
	}

	@Override
	public Double getMinBound() {
		return _minBound;
	}

	@Override
	public Long getMinDocCount() {
		return _minDocCount;
	}

	@Override
	public Double getOffset() {
		return _offset;
	}

	@Override
	public List<Order> getOrders() {
		return Collections.unmodifiableList(_orders);
	}

	@Override
	public void setBounds(Double minBound, Double maxBound) {
		_minBound = minBound;
		_maxBound = maxBound;
	}

	@Override
	public void setInterval(Double interval) {
		_interval = interval;
	}

	@Override
	public void setKeyed(Boolean keyed) {
		_keyed = keyed;
	}

	@Override
	public void setMinDocCount(Long minDocCount) {
		_minDocCount = minDocCount;
	}

	@Override
	public void setOffset(Double offset) {
		_offset = offset;
	}

	private Double _interval;
	private Boolean _keyed;
	private Double _maxBound;
	private Double _minBound;
	private Long _minDocCount;
	private Double _offset;
	private final List<Order> _orders = new ArrayList<>();

}