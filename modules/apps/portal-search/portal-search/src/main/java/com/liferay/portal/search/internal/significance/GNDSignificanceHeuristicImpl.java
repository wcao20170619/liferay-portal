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

import com.liferay.portal.search.significance.GNDSignificanceHeuristic;

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
public class GNDSignificanceHeuristicImpl implements GNDSignificanceHeuristic {

	public GNDSignificanceHeuristicImpl(
		GNDSignificanceHeuristicImpl gndSignificanceHeuristicImpl) {

		if (gndSignificanceHeuristicImpl == null) {
			return;
		}

		_backgroundIsSuperset =
			gndSignificanceHeuristicImpl._backgroundIsSuperset;
	}

	@Override
	public boolean isBackgroundIsSuperset() {
		return _backgroundIsSuperset;
	}

	public static class Builder implements GNDSignificanceHeuristic.Builder {

		@Override
		public Builder backgroundIsSuperset(boolean backgroundIsSuperset) {
			_gndSignificanceHeuristicImpl._backgroundIsSuperset =
				backgroundIsSuperset;

			return this;
		}

		@Override
		public GNDSignificanceHeuristic build() {
			return new GNDSignificanceHeuristicImpl(
				_gndSignificanceHeuristicImpl);
		}

		private final GNDSignificanceHeuristicImpl
			_gndSignificanceHeuristicImpl = new GNDSignificanceHeuristicImpl(
				null);

	}

	private boolean _backgroundIsSuperset;

}