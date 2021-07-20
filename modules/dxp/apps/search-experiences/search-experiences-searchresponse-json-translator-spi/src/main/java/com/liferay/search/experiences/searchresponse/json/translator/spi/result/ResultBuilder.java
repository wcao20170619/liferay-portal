/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.search.experiences.searchresponse.json.translator.spi.result;

import com.liferay.portal.search.document.Document;
import com.liferay.search.experiences.blueprints.engine.attributes.BlueprintsAttributes;

/**
 * @author Petteri Karttunen
 */
public interface ResultBuilder {

	public String getAuthor(
			Document document, BlueprintsAttributes blueprintsAttributes)
		throws Exception;

	public String getCreateDate(
			Document document, BlueprintsAttributes blueprintsAttributes)
		throws Exception;

	public String getModificationDate(
			Document document, BlueprintsAttributes blueprintsAttributes)
		throws Exception;

	public String getSummary(
			Document document, BlueprintsAttributes blueprintsAttributes)
		throws Exception;

	public String getThumbnail(
			Document document, BlueprintsAttributes blueprintsAttributes)
		throws Exception;

	public String getTitle(
			Document document, BlueprintsAttributes blueprintsAttributes)
		throws Exception;

	public String getType(Document document) throws Exception;

}