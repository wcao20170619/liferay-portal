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

<%@ include file="/init.jsp" %>

<%
SearchResultContentDisplayBuilder searchResultContentDisplayBuilder = new SearchResultContentDisplayBuilder();

searchResultContentDisplayBuilder.setPermissionChecker(permissionChecker);
searchResultContentDisplayBuilder.setLocale(locale);
searchResultContentDisplayBuilder.setAssetEntryId(ParamUtil.getLong(request, "assetEntryId"));
searchResultContentDisplayBuilder.setType(ParamUtil.getString(request, "type"));
searchResultContentDisplayBuilder.setRenderRequest(renderRequest);
searchResultContentDisplayBuilder.setRenderResponse(renderResponse);

SearchResultContentDisplayContext searchResultContentDisplayContext = searchResultContentDisplayBuilder.build();
%>

<c:if test="<%= searchResultContentDisplayContext.isVisible() %>">
	<liferay-ui:header
		localizeTitle="<%= false %>"
		title="<%= searchResultContentDisplayContext.getTitle() %>"
	/>

	<c:if test="<%= searchResultContentDisplayContext.hasEditPermission() %>">

		<%
		String displayNamespace = HtmlUtil.escape(portletDisplay.getNamespace());
		searchResultContentDisplayContext.setDataId(displayNamespace);

		String titleEscaped = HtmlUtil.escape(searchResultContentDisplayContext.getTitle());
		searchResultContentDisplayContext.setDataTitle(request, titleEscaped);
		searchResultContentDisplayContext.setMessage(request, titleEscaped);
		%>

		<div class="asset-actions lfr-meta-actions">
			<liferay-ui:icon
				cssClass="visible-interaction"
				data="<%= searchResultContentDisplayContext.getData() %>"
				icon="pencil"
				label="<%= false %>"
				markupView="lexicon"
				message="<%= searchResultContentDisplayContext.getMessage() %>"
				method="get"
				url="<%= searchResultContentDisplayContext.getEditPortletURL() %>"
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