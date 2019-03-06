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

package com.liferay.portal.search.script;

import aQute.bnd.annotation.ProviderType;

import java.util.Map;

/**
 * @author Michael C. Han
 */
@ProviderType
public interface Script {

	public String getIdOrCode();

	public String getLanguage();

	public Map<String, String> getOptions();

	public Map<String, Object> getParameters();

	public ScriptType getScriptType();

	@ProviderType
	public interface Builder {

		public Script build();

		public void clearOption(String optionName);

		public void clearOptions();

		public void clearParameter(String paramName);

		public void clearParameters();

		public void idOrCode(String idOrCode);

		public void language(String language);

		public void parameters(Map<String, Object> parameters);

		public void putOption(String optionName, String optionValue);

		public void putParameter(String paramName, Object paramValue);

		public void scriptType(ScriptType scriptType);

	}

	public enum ScriptType {

		INLINE, STORED

	}

}