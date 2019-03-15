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

import com.liferay.portal.search.script.Script;
import com.liferay.portal.search.significance.ChiSquareSignificanceHeuristic;
import com.liferay.portal.search.significance.GNDSignificanceHeuristic;
import com.liferay.portal.search.significance.JLHScoreSignificanceHeuristic;
import com.liferay.portal.search.significance.MutualInformationSignificanceHeuristic;
import com.liferay.portal.search.significance.PercentageScoreSignificanceHeuristic;
import com.liferay.portal.search.significance.ScriptSignificanceHeuristic;
import com.liferay.portal.search.significance.SignificanceHeuristicBuilders;
import com.liferay.portal.search.significance.SignificanceHeuristics;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = SignificanceHeuristics.class)
public class SignificanceHeuristicsImpl implements SignificanceHeuristics {

	public ChiSquareSignificanceHeuristic chiSquareSignificanceHeuristic(
		boolean backgroundIsSuperset, boolean includeNegatives) {

		ChiSquareSignificanceHeuristic.Builder builder =
			_significanceHeuristicBuilders.chiSquare();

		builder.backgroundIsSuperset(backgroundIsSuperset);
		builder.includeNegatives(includeNegatives);

		return builder.build();
	}

	public SignificanceHeuristicBuilders getSignificanceHeuristicBuilders() {
		return _significanceHeuristicBuilders;
	}

	public GNDSignificanceHeuristic gndSignificanceHeuristic(
		boolean backgroundIsSuperset) {

		GNDSignificanceHeuristic.Builder builder =
			_significanceHeuristicBuilders.gnd();

		builder.backgroundIsSuperset(backgroundIsSuperset);

		return builder.build();
	}

	public JLHScoreSignificanceHeuristic jlhScoreSignificanceHeuristic() {
		JLHScoreSignificanceHeuristic.Builder builder =
			_significanceHeuristicBuilders.jlhScore();

		return builder.build();
	}

	public MutualInformationSignificanceHeuristic
		mutualInformationSignificanceHeuristic(
			boolean backgroundIsSuperset, boolean includeNegatives) {

		MutualInformationSignificanceHeuristic.Builder builder =
			_significanceHeuristicBuilders.mutualInformation();

		builder.backgroundIsSuperset(backgroundIsSuperset);
		builder.includeNegatives(includeNegatives);

		return builder.build();
	}

	public PercentageScoreSignificanceHeuristic
		percentageScoreSignificanceHeuristic() {

		PercentageScoreSignificanceHeuristic.Builder builder =
			_significanceHeuristicBuilders.percentageScore();

		return builder.build();
	}

	public ScriptSignificanceHeuristic scriptSignificanceHeuristic(
		Script script) {

		ScriptSignificanceHeuristic.Builder builder =
			_significanceHeuristicBuilders.script();

		builder.script(script);

		return builder.build();
	}

	private final SignificanceHeuristicBuilders _significanceHeuristicBuilders =
		new SignificanceHeuristicBuildersImpl();

}