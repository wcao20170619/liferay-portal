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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Michael C. Han
 */
public class ScriptImpl implements Script {

	@Override
	public String getIdOrCode() {
		return _idOrCode;
	}

	@Override
	public String getLanguage() {
		return _language;
	}

	@Override
	public Map<String, String> getOptions() {
		return Collections.unmodifiableMap(_options);
	}

	@Override
	public Map<String, Object> getParameters() {
		return Collections.unmodifiableMap(_parameters);
	}

	@Override
	public ScriptType getScriptType() {
		return _scriptType;
	}

	protected ScriptImpl() {
	}

	protected ScriptImpl(ScriptImpl scriptImpl) {
		_idOrCode = scriptImpl._idOrCode;
		_language = scriptImpl._language;

		_options.putAll(scriptImpl._options);
		_scriptType = scriptImpl._scriptType;

		_parameters = Optional.ofNullable(
			scriptImpl._parameters
		).orElse(
			new HashMap<>(scriptImpl._parameters)
		);
	}

	protected void clearOption(String optionName) {
		_options.remove(optionName);
	}

	protected void clearOptions() {
		_options.clear();
	}

	protected void clearParameter(String paramName) {
		_parameters.remove(paramName);
	}

	protected void clearParameters() {
		_parameters.clear();
	}

	protected void putOption(String optionName, String optionValue) {
		_options.put(optionName, optionValue);
	}

	protected void putParameter(String paramName, Object paramValue) {
		_parameters.put(paramName, paramValue);
	}

	protected void setIdOrCode(String idOrCode) {
		_idOrCode = idOrCode;
	}

	protected void setLanguage(String language) {
		_language = language;
	}

	protected void setParameters(Map<String, Object> parameters) {
		_parameters = parameters;
	}

	protected void setScriptType(ScriptType scriptType) {
		_scriptType = scriptType;
	}

	private String _idOrCode;
	private String _language;
	private Map<String, String> _options = new HashMap<>();
	private Map<String, Object> _parameters;
	private ScriptType _scriptType;

}