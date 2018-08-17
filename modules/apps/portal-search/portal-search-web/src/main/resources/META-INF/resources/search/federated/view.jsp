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
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.HttpUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.theme.ThemeDisplay" %><%@
page import="com.liferay.portal.search.web.internal.search.federated.portlet.VideoResultsDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.result.display.builder.FederatedSearchSummary" %>

<%@ page import="java.util.Objects" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %>

<portlet:defineObjects />

<%
VideoResultsDisplayContext videoResultsDisplayContext = 
	(VideoResultsDisplayContext)java.util.Objects.requireNonNull(renderRequest.getAttribute(VideoResultsDisplayContext.ATTRIBUTE));

Map<String, List<FederatedSearchSummary>> federatedSearchSummaries = videoResultsDisplayContext.getFederatedSearchSummaries();

String source = videoResultsDisplayContext.getFederatedSearchSourceName();

List<FederatedSearchSummary> federatedSearchResultSummaries = federatedSearchSummaries.get(source);

int pos = videoResultsDisplayContext.getPos();
String keyword = videoResultsDisplayContext.getKeyword();

ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
String currentURL = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
currentURL = HttpUtil.addParameter(currentURL, "q", keyword);

%>
<% if (pos > -1 && federatedSearchResultSummaries.size() > pos) { 
	FederatedSearchSummary federatedSearchSummaryPos = federatedSearchResultSummaries.get(pos);
	String videoIdPos = federatedSearchSummaryPos.getURL();
%>

<c:choose>
	<c:when test="<%= Validator.isNotNull(videoResultsDisplayContext.getWatchURL()) %>">
		<c:choose>
			<c:when test="<%= videoResultsDisplayContext.isShowThumbnail() %>">
				<aui:a href="<%= videoResultsDisplayContext.getWatchURL() %>" rel="external" title='<%= HtmlUtil.escapeAttribute(LanguageUtil.get(request, "watch-this-video-at-youtube")) %>'>
					<img alt="<liferay-ui:message escapeAttribute="<%= true %>" key="youtube-video" />" height="<%= videoResultsDisplayContext.getHeight() %>" src="<%= videoResultsDisplayContext.getImageURL() %>" width="<%= videoResultsDisplayContext.getWidth() %>" />
				</aui:a>
			</c:when>
			<c:otherwise>
				<iframe allowfullscreen frameborder="0" height="<%= videoResultsDisplayContext.getHeight() %>" src="<%= videoResultsDisplayContext.getEmbedURL() %>" width="<%= videoResultsDisplayContext.getWidth() %>" wmode="Opaque" /></iframe>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<liferay-util:include page="/html/portal/portlet_not_setup.jsp" />
	</c:otherwise>
</c:choose>

<% } %>
<% 
if (federatedSearchResultSummaries != null && federatedSearchResultSummaries.size() > 0) {
%>
	<h3 >Federated Search Results</h3>

	<ul>
		<li>
			Source: <strong><%= source %></strong>
		</li>
	</ul>

	<ol>
	<%
	if (pos > -1 && federatedSearchResultSummaries != null && pos < federatedSearchResultSummaries.size()) {
		FederatedSearchSummary summary = 
				federatedSearchResultSummaries.get(pos);
	%>
		<li>
			Title: <%= summary.getTitle() %>
			<br>
			Content: <%= summary.getContent() %>
			<br>
			<br>
		</li>
	<% 
	}
	%>
	<%
	int indx = 0;
	for (FederatedSearchSummary federatedSearchSummary : federatedSearchResultSummaries) {
		String videoId = federatedSearchSummary.getURL();
		String videoURL = HttpUtil.addParameter(currentURL, "pos", indx);
		String thumbURL = "https://img.youtube.com/vi/" + federatedSearchSummary.getURL() + "/0.jpg";
		if (pos != indx) {
	%>
		<li>
			Title: <%= federatedSearchSummary.getTitle() %>
			<br>
			Content: <%= federatedSearchSummary.getContent() %>
			<br>

			<aui:a href="<%= videoURL %>" rel="external" title='<%= federatedSearchSummary.getTitle() %>'>
				<img alt="<liferay-ui:message escapeAttribute="<%= true %>" key="youtube-video" />" height="<%= 100 %>" src="<%= thumbURL %>" width="<%= 150 %>" />
			</aui:a>
			<br>
		</li>
	<%
		}
		indx++;
	}
	%>
	</ol>
<%
}
%>
