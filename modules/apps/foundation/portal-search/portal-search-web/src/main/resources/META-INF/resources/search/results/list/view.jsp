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

<portlet:defineObjects />

<%
com.liferay.portal.search.web.search.results.list.portlet.SearchResultsListDisplayContext dc =
	new com.liferay.portal.search.web.search.results.list.portlet.SearchResultsListDisplayContext(request);
%>

<%-- --%>

<p>
User
</p>

<p>
Wiki Page
</p>

<p>
Blog Entry
</p>

<p style="font-size:0.5em">
Search Results List (q=<%= dc.getQ() %>)
</p>