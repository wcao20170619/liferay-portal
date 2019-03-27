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
 * @author Dylan Rebelak
 */
@ProviderType
public interface DocumentRequestExecutor {

	public BulkDocumentResponse executeBulkDocumentRequest(
		BulkDocumentRequest bulkDocumentRequest);

	public DeleteByQueryDocumentResponse executeDocumentRequest(
		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest);

	public DeleteDocumentResponse executeDocumentRequest(
		DeleteDocumentRequest deleteDocumentRequest);

	public GetDocumentResponse executeDocumentRequest(
		GetDocumentRequest getDocumentRequest);

	public IndexDocumentResponse executeDocumentRequest(
		IndexDocumentRequest indexDocumentRequest);

	public UpdateByQueryDocumentResponse executeDocumentRequest(
		UpdateByQueryDocumentRequest updateByQueryDocumentRequest);

	public UpdateDocumentResponse executeDocumentRequest(
		UpdateDocumentRequest updateDocumentRequest);

}