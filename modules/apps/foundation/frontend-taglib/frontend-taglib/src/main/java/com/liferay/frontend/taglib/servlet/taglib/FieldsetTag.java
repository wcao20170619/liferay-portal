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

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.aui.AUIUtil;
import com.liferay.taglib.util.IncludeTag;

import javax.portlet.PortletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author Eudaldo Alonso
 */
public class FieldsetTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public void setCollapsed(boolean collapsed) {
		_collapsed = collapsed;
	}

	public void setCollapsible(boolean collapsible) {
		_collapsible = collapsible;
	}

	public void setColumn(boolean column) {
		_column = column;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setHelpMessage(String helpMessage) {
		_helpMessage = helpMessage;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public void setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		servletContext = ServletContextUtil.getServletContext();
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_collapsed = false;
		_collapsible = false;
		_column = false;
		_cssClass = null;
		_helpMessage = null;
		_id = null;
		_label = null;
		_localizeLabel = true;
	}

	@Override
	protected String getEndPage() {
		return _END_PAGE;
	}

	@Override
	protected String getStartPage() {
		return _START_PAGE;
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		if (Validator.isNull(_id) && Validator.isNotNull(_label) &&
			_collapsible) {

			String id = PortalUtil.getUniqueElementId(
				request, _getNamespace(), AUIUtil.normalizeId(_label));

			setId(_getNamespace() + id);
		}

		request.setAttribute(
			"liferay-frontend:fieldset:collapsed", String.valueOf(_collapsed));
		request.setAttribute(
			"liferay-frontend:fieldset:collapsible",
			String.valueOf(_collapsible));
		request.setAttribute(
			"liferay-frontend:fieldset:column", String.valueOf(_column));
		request.setAttribute("liferay-frontend:fieldset:cssClass", _cssClass);
		request.setAttribute(
			"liferay-frontend:fieldset:helpMessage", _helpMessage);
		request.setAttribute("liferay-frontend:fieldset:id", _id);
		request.setAttribute("liferay-frontend:fieldset:label", _label);
		request.setAttribute(
			"liferay-frontend:fieldset:localizeLabel",
			String.valueOf(_localizeLabel));
	}

	private String _getNamespace() {
		HttpServletRequest request =
			(HttpServletRequest)pageContext.getRequest();

		PortletResponse portletResponse = (PortletResponse)request.getAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE);

		if (portletResponse != null) {
			return portletResponse.getNamespace();
		}

		return StringPool.BLANK;
	}

	private static final String _ATTRIBUTE_NAMESPACE =
		"liferay-frontend:fieldset:";

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

	private static final String _END_PAGE = "/fieldset/end.jsp";

	private static final String _START_PAGE = "/fieldset/start.jsp";

	private boolean _collapsed;
	private boolean _collapsible;
	private boolean _column;
	private String _cssClass;
	private String _helpMessage;
	private String _id;
	private String _label;
	private boolean _localizeLabel = true;

}