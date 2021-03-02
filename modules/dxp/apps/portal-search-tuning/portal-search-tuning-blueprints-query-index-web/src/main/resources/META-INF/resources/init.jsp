<%--
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
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPNavigationItemList" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.servlet.SessionErrors" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.tuning.blueprints.query.index.web.internal.constants.QueryIndexMVCCommandNames" %><%@
page import="com.liferay.portal.search.tuning.blueprints.query.index.web.internal.constants.QueryIndexWebKeys" %><%@
page import="com.liferay.portal.search.tuning.blueprints.query.index.web.internal.constants.QueryStringStatus" %><%@
page import="com.liferay.portal.search.tuning.blueprints.query.index.web.internal.display.context.ActiveEntriesManagementToolbarDisplayContext" %><%@
page import="com.liferay.portal.search.tuning.blueprints.query.index.web.internal.display.context.BlacklistedEntriesManagementToolbarDisplayContext" %><%@
page import="com.liferay.portal.search.tuning.blueprints.query.index.web.internal.display.context.EditQueryStringDisplayContext" %><%@
page import="com.liferay.portal.search.tuning.blueprints.query.index.web.internal.display.context.ReportedEntriesManagementToolbarDisplayContext" %><%@
page import="com.liferay.portal.search.tuning.blueprints.query.index.web.internal.display.context.ViewQueryStringDisplayContext" %><%@
page import="com.liferay.portal.search.tuning.blueprints.query.index.web.internal.display.context.ViewQueryStringsDisplayContext" %><%@
page import="com.liferay.portal.search.tuning.blueprints.query.index.web.internal.index.QueryString" %><%@
page import="com.liferay.portal.search.tuning.blueprints.query.index.web.internal.util.QueryIndexUtil" %>

<%@ page import="javax.portlet.PortletURL" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />