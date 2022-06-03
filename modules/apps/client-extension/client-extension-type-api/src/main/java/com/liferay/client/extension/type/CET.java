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

package com.liferay.client.extension.type;

import java.util.Locale;
import java.util.Properties;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface CET {

	public String getBaseURL();

	public long getCompanyId();

	public String getDescription();

	public String getExternalReferenceCode();

	public String getName(Locale locale);

	public Properties getProperties();

	public String getSourceCodeURL();

	public int getStatus();

	public String getType();

	public boolean isReadOnly();

}