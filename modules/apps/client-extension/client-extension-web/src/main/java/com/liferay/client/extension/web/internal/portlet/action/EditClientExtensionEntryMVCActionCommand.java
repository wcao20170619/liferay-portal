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

package com.liferay.client.extension.web.internal.portlet.action;

import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryService;
import com.liferay.client.extension.type.factory.CETFactory;
import com.liferay.client.extension.web.internal.constants.ClientExtensionAdminPortletKeys;
import com.liferay.client.extension.web.internal.constants.ClientExtensionAdminWebKeys;
import com.liferay.client.extension.web.internal.display.context.EditClientExtensionEntryDisplayContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Basto
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + ClientExtensionAdminPortletKeys.CLIENT_EXTENSION_ADMIN,
		"mvc.command.name=/client_extension_admin/edit_client_extension_entry"
	},
	service = MVCActionCommand.class
)
public class EditClientExtensionEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

			if (cmd.equals(Constants.ADD)) {
				_add(actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				_update(actionRequest);
			}

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (Validator.isNotNull(redirect)) {
				actionResponse.sendRedirect(redirect);
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			SessionErrors.add(actionRequest, exception.getClass());

			actionRequest.setAttribute(
				ClientExtensionAdminWebKeys.
					EDIT_CLIENT_EXTENSION_ENTRY_DISPLAY_CONTEXT,
				new EditClientExtensionEntryDisplayContext(
					_cetFactory, _fetchClientExtensionEntry(actionRequest),
					actionRequest));

			actionResponse.setRenderParameter(
				"mvcPath", "/admin/edit_client_extension_entry.jsp");
		}
	}

	private void _add(ActionRequest actionRequest) throws PortalException {
		String description = ParamUtil.getString(actionRequest, "description");
		Map<Locale, String> nameMap = LocalizationUtil.getLocalizationMap(
			actionRequest, "name");
		String sourceCodeURL = ParamUtil.getString(
			actionRequest, "sourceCodeURL");
		String type = ParamUtil.getString(actionRequest, "type");

		_clientExtensionEntryService.addClientExtensionEntry(
			StringPool.BLANK, description, nameMap,
			ParamUtil.getString(actionRequest, "properties"), sourceCodeURL,
			type, _cetFactory.typeSettings(actionRequest, type));
	}

	private ClientExtensionEntry _fetchClientExtensionEntry(
			ActionRequest actionRequest)
		throws PortalException {

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		return _clientExtensionEntryService.
			fetchClientExtensionEntryByExternalReferenceCode(
				_portal.getCompanyId(actionRequest), externalReferenceCode);
	}

	private void _update(ActionRequest actionRequest) throws PortalException {
		ClientExtensionEntry clientExtensionEntry = _fetchClientExtensionEntry(
			actionRequest);

		String description = ParamUtil.getString(actionRequest, "description");
		Map<Locale, String> nameMap = LocalizationUtil.getLocalizationMap(
			actionRequest, "name");
		String properties = ParamUtil.getString(actionRequest, "properties");
		String sourceCodeURL = ParamUtil.getString(
			actionRequest, "sourceCodeURL");

		_clientExtensionEntryService.updateClientExtensionEntry(
			clientExtensionEntry.getClientExtensionEntryId(), description,
			nameMap, properties, sourceCodeURL,
			_cetFactory.typeSettings(
				actionRequest, clientExtensionEntry.getType()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditClientExtensionEntryMVCActionCommand.class);

	@Reference
	private CETFactory _cetFactory;

	@Reference
	private ClientExtensionEntryService _clientExtensionEntryService;

	@Reference
	private Portal _portal;

}