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

package com.liferay.search.experiences.searchresponse.json.translator;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.search.experiences.blueprints.engine.attributes.BlueprintsAttributes;
import com.liferay.search.experiences.blueprints.message.Message;
import com.liferay.search.experiences.blueprints.message.Messages;
import com.liferay.search.experiences.blueprints.model.Blueprint;

import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Petteri Karttunen
 */
public interface SearchResponseJSONTranslator {

	public JSONObject translate(
			SearchResponse searchResponse, Blueprint blueprint,
			BlueprintsAttributes blueprintsAttributes,
			ResourceBundle resourceBundle, Messages messages)
		throws PortalException;

	public JSONObject translateErrorMessages(
		List<Message> errorMessages, ResourceBundle getResourceBundle);

}