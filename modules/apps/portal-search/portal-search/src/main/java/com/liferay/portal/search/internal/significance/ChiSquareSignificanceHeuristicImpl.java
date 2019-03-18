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

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
public class ChiSquareSignificanceHeuristicImpl
	implements ChiSquareSignificanceHeuristic {

	@Override
	public boolean isBackgroundIsSuperset() {
		return _backgroundIsSuperset;
	}

	@Override
	public boolean isIncludeNegatives() {
		return _includeNegatives;
	}

	protected ChiSquareSignificanceHeuristicImpl(
		ChiSquareSignificanceHeuristicImpl chiSquareSignificanceHeuristicImpl) {

		if (chiSquareSignificanceHeuristicImpl == null) {
			return;
		}

		_backgroundIsSuperset =
			chiSquareSignificanceHeuristicImpl._backgroundIsSuperset;
		_includeNegatives =
			chiSquareSignificanceHeuristicImpl._includeNegatives;
	}

	protected static class Builder
		implements ChiSquareSignificanceHeuristic.Builder {

		@Override
		public Builder backgroundIsSuperset(boolean backgroundIsSuperset) {
			_chiSquareSignificanceHeuristicImpl._backgroundIsSuperset =
				backgroundIsSuperset;

			return this;
		}

		@Override
		public ChiSquareSignificanceHeuristic build() {
			return new ChiSquareSignificanceHeuristicImpl(
				_chiSquareSignificanceHeuristicImpl);
		}

		@Override
		public Builder includeNegatives(boolean includeNegatives) {
			_chiSquareSignificanceHeuristicImpl._includeNegatives =
				includeNegatives;

			return this;
		}

		private final ChiSquareSignificanceHeuristicImpl
			_chiSquareSignificanceHeuristicImpl =
				new ChiSquareSignificanceHeuristicImpl(null);

	}

	private boolean _backgroundIsSuperset;
	private boolean _includeNegatives;

}