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

package com.liferay.portal.search.internal.contributor.document;

import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentContributor;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, service = DocumentContributor.class)
public class AuditedModelDocumentContributor implements DocumentContributor {

	@Override
	public void contribute(Document document, BaseModel baseModel) {
		if (!(baseModel instanceof AuditedModel)) {
			return;
		}

		AuditedModel auditedModel = (AuditedModel)baseModel;

		DocumentBuilder documentBuilder = documentBuilderFactory.getBuilder();

//		document.addKeyword(Field.COMPANY_ID, auditedModel.getCompanyId());
//		document.addDate(Field.CREATE_DATE, auditedModel.getCreateDate());
//		document.addDate(Field.MODIFIED_DATE, auditedModel.getModifiedDate());
//		document.addKeyword(Field.USER_ID, auditedModel.getUserId());
		documentBuilder.add(Field.COMPANY_ID, auditedModel.getCompanyId());
		documentBuilder.add(Field.CREATE_DATE, auditedModel.getCreateDate());
		documentBuilder.add(
			Field.MODIFIED_DATE, auditedModel.getModifiedDate());
		documentBuilder.add(Field.USER_ID, auditedModel.getUserId());

		String userName = portal.getUserName(
			auditedModel.getUserId(), auditedModel.getUserName());

		//document.addKeyword(Field.USER_NAME, userName, true);
		documentBuilder.add(Field.USER_NAME, userName, true);

		document = documentBuilder.build(document);
	}

	@Reference
	protected DocumentBuilderFactory documentBuilderFactory;

	@Reference
	protected Portal portal;

}