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

package com.liferay.portal.search.model.uid;

import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;

/**
 * @author André de Oliveira
 */
public interface UIDStamper {

	public String getUIDM(ClassedModel classedModel);

	@Deprecated
	public String getUIDM(com.liferay.portal.kernel.search.Document document);

	public String getUIDM(Document document);

	@Deprecated
	public void setUIDM(
		ClassedModel classedModel,
		com.liferay.portal.kernel.search.Document document);

	public void setUIDM(
		ClassedModel classedModel, DocumentBuilder documentBuilder);

}