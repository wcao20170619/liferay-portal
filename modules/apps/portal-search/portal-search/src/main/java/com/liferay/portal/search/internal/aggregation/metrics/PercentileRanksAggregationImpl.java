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

import com.liferay.portal.search.aggregation.AggregationVisitor;
import com.liferay.portal.search.aggregation.metrics.PercentileRanksAggregation;
import com.liferay.portal.search.aggregation.metrics.PercentilesMethod;
import com.liferay.portal.search.internal.aggregation.BaseFieldAggregation;

/**
 * @author Michael C. Han
 */
public class PercentileRanksAggregationImpl
	extends BaseFieldAggregation implements PercentileRanksAggregation {

	public PercentileRanksAggregationImpl(
		String name, String field, double... values) {

		super(name, field);

		_values = values;
	}

	@Override
	public <T> T accept(AggregationVisitor<T> aggregationVisitor) {
		return aggregationVisitor.visit(this);
	}

	@Override
	public Integer getCompression() {
		return _compression;
	}

	@Override
	public Integer getHdrSignificantValueDigits() {
		return _hdrSignificantValueDigits;
	}

	@Override
	public Boolean getKeyed() {
		return _keyed;
	}

	@Override
	public PercentilesMethod getPercentilesMethod() {
		return _percentilesMethod;
	}

	@Override
	public double[] getValues() {
		return _values;
	}

	@Override
	public void setCompression(Integer compression) {
		_compression = compression;
	}

	@Override
	public void setHdrSignificantValueDigits(
		Integer hdrSignificantValueDigits) {

		_hdrSignificantValueDigits = hdrSignificantValueDigits;
	}

	@Override
	public void setKeyed(Boolean keyed) {
		_keyed = keyed;
	}

	@Override
	public void setPercentilesMethod(PercentilesMethod percentilesMethod) {
		_percentilesMethod = percentilesMethod;
	}

	@Override
	public void setValues(double... values) {
		_values = values;
	}

	private Integer _compression;
	private Integer _hdrSignificantValueDigits;
	private Boolean _keyed;
	private PercentilesMethod _percentilesMethod;
	private double[] _values;

}