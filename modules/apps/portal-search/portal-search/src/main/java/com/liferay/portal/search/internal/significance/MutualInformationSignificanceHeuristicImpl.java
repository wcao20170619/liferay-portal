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

import com.liferay.portal.search.significance.MutualInformationSignificanceHeuristic;

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
public class MutualInformationSignificanceHeuristicImpl
	implements MutualInformationSignificanceHeuristic {

	@Override
	public boolean isBackgroundIsSuperset() {
		return _backgroundIsSuperset;
	}

	@Override
	public boolean isIncludeNegatives() {
		return _includeNegatives;
	}

	public static class Builder
		implements MutualInformationSignificanceHeuristic.Builder {

		@Override
		public Builder backgroundIsSuperset(boolean backgroundIsSuperset) {
			_mutualInformationSignificanceHeuristicImpl._backgroundIsSuperset =
				backgroundIsSuperset;

			return this;
		}

		@Override
		public MutualInformationSignificanceHeuristic build() {
			return new MutualInformationSignificanceHeuristicImpl(
				_mutualInformationSignificanceHeuristicImpl);
		}

		@Override
		public Builder includeNegatives(boolean includeNegatives) {
			_mutualInformationSignificanceHeuristicImpl._includeNegatives =
				includeNegatives;

			return this;
		}

		private final MutualInformationSignificanceHeuristicImpl
			_mutualInformationSignificanceHeuristicImpl =
				new MutualInformationSignificanceHeuristicImpl(null);

	}

	protected MutualInformationSignificanceHeuristicImpl(
		MutualInformationSignificanceHeuristicImpl
			mutualInformationSignificanceHeuristicImpl) {

		_backgroundIsSuperset =
			mutualInformationSignificanceHeuristicImpl._backgroundIsSuperset;
		_includeNegatives =
			mutualInformationSignificanceHeuristicImpl._includeNegatives;
	}

	private boolean _backgroundIsSuperset;
	private boolean _includeNegatives;

}