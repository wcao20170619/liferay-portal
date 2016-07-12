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

package com.liferay.portal.search.web.search.params;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author André de Oliveira
 */
public class SearchParametersImpl implements SearchParameters {

	public SearchParametersImpl(
		HttpServletRequest request,
		SearchParametersConfiguration configuration) {

		_request = request;
		_configuration = configuration;
		_portal = PortalUtil.getPortal();
	}

	@Override
	public String getQParameter() {
		return getParameter(_configuration.getQParameterName());
	}

	protected String getParameter(String name) {
		HttpServletRequest request = _portal.getOriginalServletRequest(
			_request);

		return ParamUtil.getString(request, name);
	}

	private final SearchParametersConfiguration _configuration;
	private final Portal _portal;
	private final HttpServletRequest _request;

}