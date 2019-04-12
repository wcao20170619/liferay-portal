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
ResultsRankingsDisplayContext resultsRankingsDisplayContext = (ResultsRankingsDisplayContext)request.getAttribute(SearchRankingPortletKeys.RESULTS_RANKING_DISPLAY_CONTEXT);
%>

<clay:management-toolbar
	actionDropdownItems="<%= resultsRankingsDisplayContext.getActionDropdownItems() %>"
	clearResultsURL="<%= resultsRankingsDisplayContext.getClearResultsURL() %>"
	componentId="resultsRankingEntriesManagementToolbar"
	creationMenu="<%= resultsRankingsDisplayContext.getCreationMenu() %>"
	disabled="<%= resultsRankingsDisplayContext.isDisabledManagementBar() %>"
	filterDropdownItems="<%= resultsRankingsDisplayContext.getFilterItemsDropdownItems() %>"
	itemsTotal="<%= resultsRankingsDisplayContext.getTotalItems() %>"
	searchActionURL="<%= resultsRankingsDisplayContext.getSearchActionURL() %>"
	searchContainerId="resultsRankingEntries"
	searchFormName="searchFm"
	selectable="<%= true %>"
	showCreationMenu="<%= resultsRankingsDisplayContext.isShowCreationMenu() %>"
	sortingOrder="<%= resultsRankingsDisplayContext.getOrderByType() %>"
	sortingURL="<%= resultsRankingsDisplayContext.getSortingURL() %>"
/>

<portlet:actionURL name="deleteResultsRankingsEntry" var="deleteResultsRankingsEntryURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= deleteResultsRankingsEntryURL %>" cssClass="container-fluid-1280" method="post" name="resultsRankingEntriesFm">
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

	<liferay-ui:search-container
		id="resultsRankingEntries"
		searchContainer="<%= resultsRankingsDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.search.Document"
			keyProperty="resultsRankingsEntryId"
			modelVar="document"
		>

			<%
			ResultsRankingDisplayContext resultsRankingDisplayContext = java.util.Objects.requireNonNull(resultsRankingsDisplayContext.getResultsRankingDisplayContext(document));
			%>

			<portlet:renderURL var="rowURL">
				<portlet:param name="mvcRenderCommandName" value="addResultsRankingsEntry" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="uid" value="<%= resultsRankingDisplayContext.getUid() %>" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand table-title"
				href="<%= rowURL %>"
				name="search-term-aliases"
				value="<%= resultsRankingDisplayContext.getKeywords() %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-smallest table-cell-minw-150"
				name="pinned-results"
				value="<%= resultsRankingDisplayContext.getPinned() %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-smallest table-cell-minw-150"
				name="hidden-results"
				value="<%= resultsRankingDisplayContext.getHidden() %>"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand-smallest table-cell-minw-150 table-cell-ws-nowrap"
				name="modified-date"
				value="<%= resultsRankingDisplayContext.getModifiedDate() %>"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand-smallest table-cell-minw-150 table-cell-ws-nowrap"
				name="display-date"
				value="<%= resultsRankingDisplayContext.getDisplayDate() %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-smallest table-cell-minw-150"
				name="status"
				value="status"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>