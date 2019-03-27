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

package com.liferay.portal.search.engine.adapter.document;

import aQute.bnd.annotation.ProviderType;

/**
 * @author Bryan Engler
 */
@ProviderType
public class GetDocumentResponse implements DocumentResponse {

	public GetDocumentResponse(boolean exists, String source) {
		_exists = exists;
		_source = source;
	}

	public String getSource() {
		return _source;
	}

	public boolean isExists() {
		return _exists;
	}

	private final boolean _exists;
	private final String _source;

}