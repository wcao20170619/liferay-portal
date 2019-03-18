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

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.search.script.Script;
import com.liferay.portal.search.significance.ScriptSignificanceHeuristic;

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
@ProviderType
public class ScriptSignificanceHeuristicImpl
	implements ScriptSignificanceHeuristic {

	public ScriptSignificanceHeuristicImpl(
		ScriptSignificanceHeuristicImpl scriptSignificanceHeuristicImpl) {

		if (scriptSignificanceHeuristicImpl == null) {
			return;
		}

		_script = scriptSignificanceHeuristicImpl._script;
	}

	@Override
	public Script getScript() {
		return _script;
	}

	public static class Builder implements ScriptSignificanceHeuristic.Builder {

		@Override
		public ScriptSignificanceHeuristic build() {
			return new ScriptSignificanceHeuristicImpl(
				_scriptSignificanceHeuristicImpl);
		}

		@Override
		public Builder script(Script script) {
			_scriptSignificanceHeuristicImpl._script = script;

			return this;
		}

		private final ScriptSignificanceHeuristicImpl
			_scriptSignificanceHeuristicImpl =
				new ScriptSignificanceHeuristicImpl(null);

	}

	private Script _script;

}