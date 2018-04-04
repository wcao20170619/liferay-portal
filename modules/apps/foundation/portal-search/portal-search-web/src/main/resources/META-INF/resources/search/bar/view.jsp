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

<%@ page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortletDisplayContext" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%
SearchBarPortletDisplayContext searchBarPortletDisplayContext = (SearchBarPortletDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));
%>

<portlet:actionURL name="redirectSearchBar" var="portletURL">
	<portlet:param name="mvcActionCommandName" value="redirectSearchBar" />
</portlet:actionURL>

<aui:form action="<%= portletURL %>" method="post" name="fm">
	<aui:fieldset id="searchContainer">
		<div class="input-group search-bar">
			<aui:field-wrapper inlineField="<%= true %>">
				<aui:input autoFocus="<%= true %>" cssClass="search-bar-keywords-input search-input" label="" name="<%= searchBarPortletDisplayContext.getKeywordsParameterName() %>" placeholder="search-..." title="search" type="text" value="<%= searchBarPortletDisplayContext.getKeywords() %>" />
			</aui:field-wrapper>

			<c:choose>
				<c:when test="<%= searchBarPortletDisplayContext.isLetTheUserChooseTheSearchScope() %>">
					<aui:field-wrapper inlineField="<%= true %>">
						<aui:select cssClass="search-select" label="" name="<%= searchBarPortletDisplayContext.getScopeParameterName() %>" title="scope">
							<c:if test="<%= searchBarPortletDisplayContext.isAvailableEverythingSearchScope() %>">
								<aui:option label="all-sites" selected="<%= searchBarPortletDisplayContext.isSelectedEverythingSearchScope() %>" value="<%= searchBarPortletDisplayContext.getEverythingSearchScopeParameterString() %>" />
							</c:if>

							<aui:option label="this-site" selected="<%= searchBarPortletDisplayContext.isSelectedCurrentSiteSearchScope() %>" value="<%= searchBarPortletDisplayContext.getCurrentSiteSearchScopeParameterString() %>" />
						</aui:select>
					</aui:field-wrapper>
				</c:when>
				<c:otherwise>
					<aui:input name="<%= searchBarPortletDisplayContext.getScopeParameterName() %>" type="hidden" value="<%= searchBarPortletDisplayContext.getScopeParameterValue() %>" />
				</c:otherwise>
			</c:choose>

			<aui:field-wrapper cssClass="input-group-btn search-field" inlineField="<%= true %>">
				<liferay-ui:icon
					cssClass="icon-monospaced search-bar-search-button"
					icon="search"
					markupView="lexicon"
				/>
			</aui:field-wrapper>
		</div>
	</aui:fieldset>
</aui:form>

<aui:script use="liferay-search-bar">
	new Liferay.Search.SearchBar(A.one('#<portlet:namespace/>fm'));
</aui:script>