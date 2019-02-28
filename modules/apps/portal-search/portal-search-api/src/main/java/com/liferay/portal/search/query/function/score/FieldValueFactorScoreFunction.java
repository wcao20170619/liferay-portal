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

package com.liferay.portal.search.query.function.score;

import aQute.bnd.annotation.ProviderType;

/**
 * @author Michael C. Han
 */
@ProviderType
public interface FieldValueFactorScoreFunction extends ScoreFunction {

	@Override
	public <T> T accept(ScoreFunctionTranslator<T> scoreFunctionTranslator);

	public Float getFactor();

	public String getField();

	public Double getMissing();

	public Modifier getModifier();

	public void setFactor(Float factor);

	public void setMissing(Double missing);

	public void setModifier(Modifier modifier);

	public enum Modifier {

		LN, LN1P, LN2P, LOG, LOG1P, LOG2P, NONE, RECIPROCAL, SQRT, SQUARE

	}

}