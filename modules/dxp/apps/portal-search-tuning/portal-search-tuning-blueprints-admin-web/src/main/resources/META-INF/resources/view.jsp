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

<%@ include file="/init.jsp" %>

<liferay-ui:error key="<%= BlueprintsAdminWebKeys.ERROR_DETAILS %>">
	<liferay-ui:message arguments='<%= SessionErrors.get(liferayPortletRequest, "errorDetails") %>' key="error.blueprint-service-error" />
</liferay-ui:error>

<%
final String tabs = ParamUtil.getString(request, "tabs", "blueprints");
PortletURL configurationsURL = renderResponse.createRenderURL();
configurationsURL.setParameter(BlueprintsAdminWebKeys.BLUEPRINT_TYPE, String.valueOf(BlueprintTypes.BLUEPRINT));

PortletURL templatesURL = renderResponse.createRenderURL();
templatesURL.setParameter(BlueprintsAdminWebKeys.BLUEPRINT_TYPE, String.valueOf(BlueprintTypes.TEMPLATE));

PortletURL queryFragmentsURL = renderResponse.createRenderURL();
queryFragmentsURL.setParameter(BlueprintsAdminWebKeys.BLUEPRINT_TYPE, String.valueOf(BlueprintTypes.QUERY_FRAGMENT));
%>

<clay:navigation-bar
	inverted="<%= false %>"
	navigationItems='<%=
		new JSPNavigationItemList(pageContext) {
			{
				add(
					navigationItem -> {
						navigationItem.putData(BlueprintsAdminWebKeys.BLUEPRINT_TYPE, String.valueOf(BlueprintTypes.BLUEPRINT));
						navigationItem.setActive(tabs.equals("blueprints"));
						navigationItem.setHref(configurationsURL, "tabs", "blueprints");
						navigationItem.setLabel(LanguageUtil.get(request, "blueprints"));
					});
				add(
					navigationItem -> {
						navigationItem.setActive(tabs.equals("templates"));
						navigationItem.setHref(templatesURL, "tabs", "templates");
						navigationItem.setLabel(LanguageUtil.get(request, "templates"));
					});
				add(
					navigationItem -> {
						navigationItem.putData(BlueprintsAdminWebKeys.BLUEPRINT_TYPE, String.valueOf(BlueprintTypes.QUERY_FRAGMENT));
						navigationItem.setActive(tabs.equals("fragments"));
						navigationItem.setHref(queryFragmentsURL, "tabs", "fragments");
						navigationItem.setLabel(LanguageUtil.get(request, "fragments"));
					});
			}
		}
	%>'
/>

<liferay-util:include page="/view_blueprints.jsp" servletContext="<%= application %>" />