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

package com.liferay.search.experiences.blueprints.admin.web.internal.handler;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.search.experiences.blueprints.exception.BlueprintValidationException;
import com.liferay.search.experiences.blueprints.exception.ElementValidationException;
import com.liferay.search.experiences.blueprints.message.Message;

import java.io.IOException;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(immediate = true, service = BlueprintExceptionRequestHandler.class)
public class BlueprintExceptionRequestHandler {

	public void handlePortalException(
		ActionRequest actionRequest, ActionResponse actionResponse,
		PortalException portalException) {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		if (portalException instanceof BlueprintValidationException) {
			BlueprintValidationException blueprintValidationException =
				(BlueprintValidationException)portalException;

			_addErrors(
				jsonArray, blueprintValidationException.getMessages(),
				themeDisplay);
		}
		else if (portalException instanceof ElementValidationException) {
			ElementValidationException elementValidationException =
				(ElementValidationException)portalException;

			_addErrors(
				jsonArray, elementValidationException.getMessages(),
				themeDisplay);
		}

		if (portalException.getCause() instanceof JSONException) {
			jsonArray.put(
				_language.get(
					themeDisplay.getRequest(),
					"core.error.unable-to-parse-json"));
		}
		else {
			jsonArray.put(
				_language.get(
					themeDisplay.getRequest(), "an-unexpected-error-occurred"));
		}

		JSONObject jsonObject = JSONUtil.put("error", jsonArray);

		try {
			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}
		catch (IOException ioException) {
			_log.error(ioException.getMessage(), ioException);
		}
	}

	private void _addErrors(
		JSONArray jsonArray, List<Message> messages,
		ThemeDisplay themeDisplay) {

		messages.forEach(
			message -> jsonArray.put(
				_language.get(
					themeDisplay.getRequest(), message.getLocalizationKey())));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BlueprintExceptionRequestHandler.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}