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

package com.liferay.portal.search.internal.significance;

import com.liferay.portal.search.significance.ChiSquareSignificanceHeuristic;
import com.liferay.portal.search.significance.GNDSignificanceHeuristic;
import com.liferay.portal.search.significance.JLHScoreSignificanceHeuristic;
import com.liferay.portal.search.significance.MutualInformationSignificanceHeuristic;
import com.liferay.portal.search.significance.PercentageScoreSignificanceHeuristic;
import com.liferay.portal.search.significance.ScriptSignificanceHeuristic;
import com.liferay.portal.search.significance.SignificanceHeuristicBuilders;

/**
 * @author André de Oliveira
 */
public class SignificanceHeuristicBuildersImpl
	implements SignificanceHeuristicBuilders {

	@Override
	public ChiSquareSignificanceHeuristic.Builder chiSquare() {
		return new ChiSquareSignificanceHeuristicImpl.Builder();
	}

	@Override
	public GNDSignificanceHeuristic.Builder gnd() {
		return new GNDSignificanceHeuristicImpl.Builder();
	}

	@Override
	public JLHScoreSignificanceHeuristic.Builder jlhScore() {
		return new JLHScoreSignificanceHeuristicImpl.Builder();
	}

	@Override
	public MutualInformationSignificanceHeuristic.Builder mutualInformation() {
		return new MutualInformationSignificanceHeuristicImpl.Builder();
	}

	@Override
	public PercentageScoreSignificanceHeuristic.Builder percentageScore() {
		return new PercentageScoreSignificanceHeuristicImpl.Builder();
	}

	@Override
	public ScriptSignificanceHeuristic.Builder script() {
		return new ScriptSignificanceHeuristicImpl.Builder();
	}

}