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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.search.document.Document" %><%@
page import="com.liferay.portal.search.related.results.web.internal.display.context.SearchRelatedResultsDisplayContext" %><%@
page import="com.liferay.portal.search.related.results.web.internal.display.context.SearchRelatedResultsDocumentDisplayContext" %><%@
page import="com.liferay.portal.search.related.results.web.internal.configuration.SearchRelatedResultsPortletInstanceConfiguration" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.HashMap" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
SearchRelatedResultsDisplayContext searchRelatedResultsDisplayContext = new SearchRelatedResultsDisplayContext(request);

SearchRelatedResultsPortletInstanceConfiguration searchRelatedResultsPortletInstanceConfiguration = searchRelatedResultsDisplayContext.getSearchRelatedResultsPortletInstanceConfiguration();

Map<String, Object> contextObjects = new HashMap<String, Object>();

contextObjects.put("searchRelatedResultsDisplayContext", searchRelatedResultsDisplayContext);

List<SearchRelatedResultsDocumentDisplayContext> documentList_MOCK = new ArrayList<>();

SearchRelatedResultsDocumentDisplayContext document_MOCK = new SearchRelatedResultsDocumentDisplayContext();

document_MOCK.setCategoriesString("Marketing, Nutrition");
document_MOCK.setTitle("Test Title");
document_MOCK.setViewURL("#");
document_MOCK.setCreatorUserName("Test Test");
document_MOCK.setCreationDateString("Apr 18, 2019, 11:05 AM");
document_MOCK.setModelResource("Blogs Entry");
document_MOCK.setContent("This is a shortened description of the content...");

documentList_MOCK.add(document_MOCK);
documentList_MOCK.add(document_MOCK);
documentList_MOCK.add(document_MOCK);
documentList_MOCK.add(document_MOCK);
%>

<liferay-ddm:template-renderer
	className="<%= Document.class.getName() %>"
	contextObjects="<%= contextObjects %>"
	displayStyle="<%= searchRelatedResultsPortletInstanceConfiguration.displayStyle() %>"
	displayStyleGroupId="<%= searchRelatedResultsDisplayContext.getDisplayStyleGroupId() %>"
	entries="<%= documentList_MOCK %>"
>
	<h4><liferay-ui:message key="more-like-this" /></h4>

	<%@ include file="/display/compact.jspf" %>
</liferay-ddm:template-renderer>