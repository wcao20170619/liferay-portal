<%--
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
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.related.results.web.internal.configuration.SearchRelatedResultsPortletInstanceConfiguration" %><%@
page import="com.liferay.portal.search.related.results.web.internal.display.context.SearchRelatedResultsDisplayContext" %><%@
page import="com.liferay.portal.search.related.results.web.internal.display.context.SearchRelatedResultsDocumentDisplayContext" %>

<%@ page import="java.util.HashMap" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
SearchRelatedResultsDisplayContext searchRelatedResultsDisplayContext = (SearchRelatedResultsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

SearchRelatedResultsPortletInstanceConfiguration searchRelatedResultsPortletInstanceConfiguration = searchRelatedResultsDisplayContext.getSearchRelatedResultsPortletInstanceConfiguration();

Map<String, Object> contextObjects = new HashMap<String, Object>();

contextObjects.put("searchRelatedResultsDisplayContext", searchRelatedResultsDisplayContext);

List<SearchRelatedResultsDocumentDisplayContext> documentList = searchRelatedResultsDisplayContext.getSearchRelatedResultsDocumentDisplayContexts();

if (documentList.isEmpty()) {
	renderRequest.setAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
}
%>
<c:choose>
	<c:when test="<%= documentList.isEmpty() %>">
		<div class="alert alert-info text-center">
			<div>
				<liferay-ui:message key="there-are-no-similar-results-available" />
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<liferay-ddm:template-renderer
			className="<%= SearchRelatedResultsDocumentDisplayContext.class.getName() %>"
			contextObjects="<%= contextObjects %>"
			displayStyle="<%= searchRelatedResultsPortletInstanceConfiguration.displayStyle() %>"
			displayStyleGroupId="<%= searchRelatedResultsDisplayContext.getDisplayStyleGroupId() %>"
			entries="<%= documentList %>"
		/>
	</c:otherwise>
</c:choose>