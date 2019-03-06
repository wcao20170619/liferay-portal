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
import com.liferay.portal.search.script.Scripts;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = Scripts.class)
public class ScriptsImpl implements Scripts {

	@Override
	public Script.Builder getScriptBuilder() {
		Script.Builder builder = new Script.Builder() {

			@Override
			public Script build() {
				return new ScriptImpl(_scriptImpl);
			}

			@Override
			public void clearOption(String optionName) {
				_scriptImpl.clearOption(optionName);
			}

			@Override
			public void clearOptions() {
				_scriptImpl.clearOptions();
			}

			@Override
			public void clearParameter(String paramName) {
				_scriptImpl.clearParameter(paramName);
			}

			@Override
			public void clearParameters() {
				_scriptImpl.clearParameters();
			}

			@Override
			public void idOrCode(String idOrCode) {
				_scriptImpl.setIdOrCode(idOrCode);
			}

			@Override
			public void language(String language) {
				_scriptImpl.setLanguage(language);
			}

			@Override
			public void parameters(Map<String, Object> parameters) {
				_scriptImpl.setParameters(parameters);
			}

			@Override
			public void putOption(String optionName, String optionValue) {
				_scriptImpl.putOption(optionName, optionValue);
			}

			@Override
			public void putParameter(String paramName, Object paramValue) {
				_scriptImpl.putParameter(paramName, paramValue);
			}

			@Override
			public void scriptType(Script.ScriptType scriptType) {
				_scriptImpl.setScriptType(scriptType);
			}

			private final ScriptImpl _scriptImpl = new ScriptImpl();

		};

		return builder;
	}

	@Override
	public ScriptField.Builder getScriptFieldBuilder() {
		ScriptField.Builder builder = new ScriptField.Builder() {

			@Override
			public ScriptField build() {
				return new ScriptFieldImpl(_scriptFieldImpl);
			}

			@Override
			public void field(String field) {
				_scriptFieldImpl.setField(field);
			}

			@Override
			public void ignoreFailure(boolean ignoreFailure) {
				_scriptFieldImpl.setIgnoreFailure(ignoreFailure);
			}

			@Override
			public void script(Script script) {
				_scriptFieldImpl.setScript(script);
			}

			private final ScriptFieldImpl _scriptFieldImpl =
				new ScriptFieldImpl();

		};

		return builder;
	}

	public Script script(String scriptId) {
		Script.Builder builder = getScriptBuilder();

		builder.language(null);
		builder.idOrCode(scriptId);
		builder.scriptType(Script.ScriptType.STORED);
		builder.parameters(null);

		return builder.build();
	}

	public Script script(String language, String code) {
		Script.Builder builder = getScriptBuilder();

		builder.language(language);
		builder.idOrCode(code);
		builder.scriptType(Script.ScriptType.INLINE);
		builder.parameters(new HashMap<>());

		return builder.build();
	}

	public ScriptField scriptField(String field, Script script) {
		ScriptField.Builder builder = getScriptFieldBuilder();

		builder.field(field);
		builder.script(script);

		return builder.build();
	}

}