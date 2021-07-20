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

package com.liferay.search.experiences.searchresponse.json.translator.internal.contributor;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.SearchTimeValue;
import com.liferay.search.experiences.blueprints.engine.attributes.BlueprintsAttributes;
import com.liferay.search.experiences.blueprints.engine.constants.ReservedParameterNames;
import com.liferay.search.experiences.blueprints.message.Messages;
import com.liferay.search.experiences.blueprints.model.Blueprint;
import com.liferay.search.experiences.searchresponse.json.translator.constants.JSONKeys;
import com.liferay.search.experiences.searchresponse.json.translator.constants.ResponseAttributeKeys;
import com.liferay.search.experiences.searchresponse.json.translator.spi.contributor.JSONTranslationContributor;

import java.util.IllegalFormatException;
import java.util.Optional;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;

/**
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, property = "name=meta",
	service = JSONTranslationContributor.class
)
public class MetaJSONTranslationContributor
	implements JSONTranslationContributor {

	@Override
	public void contribute(
		JSONObject responseJSONObject, SearchResponse searchResponse,
		Blueprint blueprint, BlueprintsAttributes blueprintsAttributes,
		ResourceBundle resourceBundle, Messages messages) {

		responseJSONObject.put(
			JSONKeys.META,
			_getMetaJSONObject(searchResponse, blueprintsAttributes));
	}

	private JSONObject _getMetaJSONObject(
		SearchResponse searchResponse,
		BlueprintsAttributes blueprintsAttributes) {

		SearchHits searchHits = searchResponse.getSearchHits();

		JSONObject jsonObject = JSONUtil.put(
			JSONKeys.TOTAL_HITS, searchHits.getTotalHits());

		jsonObject.put(JSONKeys.KEYWORDS, blueprintsAttributes.getKeywords());

		_setExecutionTime(jsonObject, searchResponse);

		if (_includeRequestString(blueprintsAttributes)) {
			_setRequestString(jsonObject, searchResponse);
		}

		_setResponseString(jsonObject, searchResponse);

		_setShowingInsteadOf(jsonObject, blueprintsAttributes);

		return jsonObject;
	}

	private boolean _includeRequestString(
		BlueprintsAttributes blueprintsAttributes) {

		Optional<Object> includeOptional =
			blueprintsAttributes.getAttributeOptional(
				ResponseAttributeKeys.INCLUDE_REQUEST_STRING);

		if (!includeOptional.isPresent()) {
			return false;
		}

		return GetterUtil.getBoolean(includeOptional.get());
	}

	private void _setExecutionTime(
		JSONObject jsonObject, SearchResponse searchResponse) {

		SearchTimeValue searchTimeValue = searchResponse.getSearchTimeValue();

		if (searchTimeValue == null) {
			return;
		}

		try {
			jsonObject.put(
				JSONKeys.EXECUTION_TIME,
				String.format("%.3f", searchTimeValue.getDuration() / 1000F));
		}
		catch (IllegalFormatException illegalFormatException) {
			_log.error(
				illegalFormatException.getMessage(), illegalFormatException);
		}
	}

	private void _setRequestString(
		JSONObject jsonObject, SearchResponse searchResponse) {

		if (!Validator.isBlank(searchResponse.getRequestString())) {
			jsonObject.put("requestString", searchResponse.getRequestString());
		}
	}

	private void _setResponseString(
		JSONObject jsonObject, SearchResponse searchResponse) {

		if (!Validator.isBlank(searchResponse.getResponseString())) {
			jsonObject.put("requestString", searchResponse.getResponseString());
		}
	}

	private void _setShowingInsteadOf(
		JSONObject jsonObject, BlueprintsAttributes blueprintsAttributes) {

		Optional<Object> showingInsteadOfOptional =
			blueprintsAttributes.getAttributeOptional(
				ReservedParameterNames.SHOWING_INSTEAD_OF.getKey());

		if (showingInsteadOfOptional.isPresent()) {
			jsonObject.put(
				JSONKeys.SHOWING_INSTEAD_OF, showingInsteadOfOptional.get());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MetaJSONTranslationContributor.class);

}