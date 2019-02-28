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

package com.liferay.portal.search.internal.query.function.score;

import com.liferay.portal.search.query.function.score.FieldValueFactorScoreFunction;
import com.liferay.portal.search.query.function.score.ScoreFunctionTranslator;

/**
 * @author Michael C. Han
 */
public class FieldValueFactorScoreFunctionImpl
	extends ScoreFunctionImpl implements FieldValueFactorScoreFunction {

	public FieldValueFactorScoreFunctionImpl(String field) {
		_field = field;
	}

	@Override
	public <T> T accept(ScoreFunctionTranslator<T> scoreFunctionTranslator) {
		return scoreFunctionTranslator.translate(this);
	}

	public Float getFactor() {
		return _factor;
	}

	public String getField() {
		return _field;
	}

	public Double getMissing() {
		return _missing;
	}

	public Modifier getModifier() {
		return _modifier;
	}

	public void setFactor(Float factor) {
		_factor = factor;
	}

	public void setMissing(Double missing) {
		_missing = missing;
	}

	public void setModifier(Modifier modifier) {
		_modifier = modifier;
	}

	private Float _factor;
	private final String _field;
	private Double _missing;
	private Modifier _modifier;

}