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

package com.liferay.portal.search.internal.script;

import com.liferay.portal.search.script.Script;
import com.liferay.portal.search.script.ScriptField;

/**
 * @author Michael C. Han
 */
public class ScriptFieldImpl implements ScriptField {

	public String getField() {
		return _field;
	}

	public Script getScript() {
		return _script;
	}

	public boolean isIgnoreFailure() {
		return _ignoreFailure;
	}

	protected ScriptFieldImpl() {
		_ignoreFailure = true;
	}

	protected ScriptFieldImpl(ScriptFieldImpl scriptFieldImpl) {
		_field = scriptFieldImpl._field;
		_ignoreFailure = scriptFieldImpl._ignoreFailure;
		_script = scriptFieldImpl._script;
	}

	protected void setField(String field) {
		_field = field;
	}

	protected void setIgnoreFailure(boolean ignoreFailure) {
		_ignoreFailure = ignoreFailure;
	}

	protected void setScript(Script script) {
		_script = script;
	}

	private String _field;
	private boolean _ignoreFailure;
	private Script _script;

}