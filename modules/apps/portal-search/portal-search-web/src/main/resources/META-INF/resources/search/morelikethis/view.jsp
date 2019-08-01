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

<%@ taglib uri="http://liferay.com/tld/asset" prefix="liferay-asset" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.search.web.internal.result.display.builder.SearchResultContentDisplayBuilder" %><%@
page import="com.liferay.portal.search.web.internal.result.display.context.SearchResultContentDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.search.morelikethis.display.context.SearchMoreLikeThisPortletDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.search.morelikethis.constants.SearchMoreLikeThisPortletKeys" %><%@
page import="com.liferay.portal.search.web.internal.search.morelikethis.display.context.SearchMoreLikeThisEntryDisplayContext" %>

<%@ page import="java.util.HashMap" %><%@
page import="java.util.Map" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
SearchMoreLikeThisPortletDisplayContext searchMoreLikeThisPortletDisplayContext = (SearchMoreLikeThisPortletDisplayContext)request.getAttribute(SearchMoreLikeThisPortletKeys.MORE_LIKE_THIS_PORTLET_DISPLAY_CONTEXT);
%>

<%

SearchResultContentDisplayContext searchResultContentDisplayContext = 
   (SearchResultContentDisplayContext)request.getAttribute("view_content");
%>

<c:if test="<%= searchResultContentDisplayContext.isVisible() %>">


	<c:if test="<%= searchResultContentDisplayContext.hasEditPermission() %>">
		<div class="asset-actions lfr-meta-actions">

			<%
			Map<String, Object> data = new HashMap<String, Object>();

			data.put("destroyOnHide", true);
			data.put("id", HtmlUtil.escape(portletDisplay.getNamespace()) + "editAsset");
			data.put("title", LanguageUtil.format(request, "edit-x", HtmlUtil.escape(searchResultContentDisplayContext.getIconEditTarget()), false));
			%>

			<liferay-ui:icon
				cssClass="visible-interaction"
				data="<%= data %>"
				icon="pencil"
				label="<%= false %>"
				markupView="lexicon"
				message='<%= LanguageUtil.format(request, "edit-x-x", new Object[] {"hide-accessible", HtmlUtil.escape(searchResultContentDisplayContext.getIconEditTarget())}, false) %>'
				method="get"
				url="<%= searchResultContentDisplayContext.getIconURLString() %>"
				useDialog="<%= true %>"
			/>
		</div>
	</c:if>

	<liferay-asset:asset-display
		assetEntry="<%= searchResultContentDisplayContext.getAssetEntry() %>"
		assetRenderer="<%= searchResultContentDisplayContext.getAssetRenderer() %>"
		assetRendererFactory="<%= searchResultContentDisplayContext.getAssetRendererFactory() %>"
	/>
</c:if>

<liferay-ui:search-container
		id="resultsMLTEntries"
		searchContainer="<%= searchMoreLikeThisPortletDisplayContext.getSearchContainer() %>"
	>
	
	<liferay-ui:search-container-row
		className="com.liferay.portal.search.web.internal.search.morelikethis.display.context.SearchMoreLikeThisEntryDisplayContext"
		modelVar="searchMLTDisplayContextVar"
	>
		
	<%
	SearchMoreLikeThisEntryDisplayContext searchMoreLikeThisEntryDisplayContext = 
		searchMLTDisplayContextVar;
	%>
			<div class="">
					<%= searchMoreLikeThisEntryDisplayContext.getId() %>
			</div>
	</liferay-ui:search-container-row>
	
</liferay-ui:search-container>
	
