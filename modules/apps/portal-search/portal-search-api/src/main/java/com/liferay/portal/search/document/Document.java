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

package com.liferay.portal.search.document;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.search.Field;

/**
 * @author Wade Cao
 */
@ProviderType
public interface Document {
	
	public String get(Locale locale, String name);

	public String get(Locale locale, String name, String defaultName);

	public String get(String name);

	public String get(String name, String defaultName);

	public Date getDate(String name) throws ParseException;

	public Field getField(String name);

	public Map<String, Field> getFields();
	
	public String getPortletId();

	public String getUID();

	public String[] getValues(String name);

	public boolean hasField(String name);
}
