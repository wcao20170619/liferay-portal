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

<%
ViewElementsDisplayContext viewElementsDisplayContext = (ViewElementsDisplayContext)request.getAttribute(BlueprintsAdminWebKeys.VIEW_ELEMENTS_DISPLAY_CONTEXT);
%>

<portlet:actionURL name="<%= BlueprintsAdminMVCCommandNames.DELETE_ELEMENT %>" var="deleteElementURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<portlet:actionURL name="<%= BlueprintsAdminMVCCommandNames.EDIT_ELEMENT %>" var="hideElementURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="<%= Constants.CMD %>" value="<%= BlueprintsAdminWebKeys.HIDE %>" />
	<portlet:param name="hidden" value="<%= Boolean.TRUE.toString() %>" />
</portlet:actionURL>

<portlet:actionURL name="<%= BlueprintsAdminMVCCommandNames.EDIT_ELEMENT %>" var="showElementURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="<%= Constants.CMD %>" value="<%= BlueprintsAdminWebKeys.HIDE %>" />
	<portlet:param name="hidden" value="<%= Boolean.FALSE.toString() %>" />
</portlet:actionURL>

<clay:management-toolbar
	additionalProps='<%=
		HashMapBuilder.<String, Object>put(
			"deleteElementURL", deleteElementURL
		).put(
			"hideElementURL", hideElementURL
		).put(
			"showElementURL", showElementURL
		).build()
	%>'
	managementToolbarDisplayContext="<%= (ViewElementsManagementToolbarDisplayContext)request.getAttribute(BlueprintsAdminWebKeys.VIEW_ELEMENTS_MANAGEMENT_TOOLBAR_DISPLAY_CONTEXT) %>"
	propsTransformer="js/view_elements/ElementEntriesManagementToolbarPropsTransformer"
	searchContainerId="elementEntries"
	supportsBulkActions="<%= true %>"
/>

<clay:container-fluid>
	<aui:form method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

		<liferay-ui:search-container
			cssClass="blueprints-search-container"
			id="elementEntries"
			searchContainer="<%= viewElementsDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.search.experiences.blueprints.model.Element"
				keyProperty="elementId"
				modelVar="entry"
			>
				<%@ include file="/element_entry_search_columns.jspf" %>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="<%= viewElementsDisplayContext.getDisplayStyle() %>"
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>