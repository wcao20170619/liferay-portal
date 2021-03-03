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
ViewActiveEntriesManagementToolbarDisplayContext viewActiveEntriesManagementToolbarDisplayContext = (ViewActiveEntriesManagementToolbarDisplayContext)request.getAttribute(QueryIndexWebKeys.ACTIVE_ENTRIES_MANAGEMENT_TOOLBAR_DISPLAY_CONTEXT);

ViewQueryStringsDisplayContext viewQueryStringsDisplayContext = (ViewQueryStringsDisplayContext)request.getAttribute(QueryIndexWebKeys.VIEW_QUERY_STRINGS_DISPLAY_CONTEXT);
%>

<clay:management-toolbar-v2
	displayContext="<%= viewActiveEntriesManagementToolbarDisplayContext %>"
	searchContainerId="activeEntries"
	supportsBulkActions="<%= true %>"
/>

<clay:container-fluid>
	<aui:form method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

		<liferay-ui:search-container
			cssClass="blueprints-search-container"
			id="activeEntries"
			searchContainer="<%= viewQueryStringsDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.portal.search.tuning.blueprints.query.index.web.internal.index.QueryString"
				keyProperty="queryStringId"
				modelVar="entry"
			>
				<%@ include file="/active_entry_search_columns.jspf" %>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="<%= viewQueryStringsDisplayContext.getDisplayStyle() %>"
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>

<liferay-frontend:component
	componentId="<%= viewActiveEntriesManagementToolbarDisplayContext.getDefaultEventHandler() %>"
	module="js/view_active_entries/ViewActiveEntriesManagementToolbarDefaultEventHandler"
/>

<aui:script sandbox="<%= true %>">
	var submitForm = function (url) {
		var searchContainer = document.getElementById(
			'<portlet:namespace />activeEntries'
		);

		if (searchContainer) {
			Liferay.Util.postForm(document.<portlet:namespace />fm, {
				data: {
					actionFormInstanceIds: Liferay.Util.listCheckedExcept(
						searchContainer,
						'<portlet:namespace />allRowIds'
					),
				},
				url: url,
			});
		}
	};

	var blacklistEntries = function () {
		if (
			confirm(
				'<liferay-ui:message key="are-you-sure-you-want-to-blacklist-selected-entries" />'
			)
		) {
			<portlet:actionURL name="<%= QueryIndexMVCCommandNames.EDIT_QUERY_STRING %>" var="blacklistEntryURL">
				<portlet:param name="<%= QueryIndexWebKeys.STATUS %>" value="<%= QueryStringStatus.BLACKLISTED.name() %>" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
			</portlet:actionURL>

			submitForm('<%= blacklistEntryURL %>');
		}
	};

	var deleteEntries = function () {
		if (
			confirm(
				'<liferay-ui:message key="are-you-sure-you-want-to-delete-selected-entries" />'
			)
		) {
			<portlet:actionURL name="<%= QueryIndexMVCCommandNames.DELETE_QUERY_STRING %>" var="deleteEntryURL">
				<portlet:param name="redirect" value="<%= currentURL %>" />
			</portlet:actionURL>

			submitForm('<%= deleteEntryURL %>');
		}
	};

	var ACTIONS = {
		blacklistEntries: blacklistEntries,
		deleteEntries: deleteEntries,
	};

	Liferay.componentReady('activeEntriesManagementToolbar').then(
		(managementToolbar) => {
			managementToolbar.on('actionItemClicked', (event) => {
				var itemData = event.data.item.data;

				if (itemData && itemData.action && ACTIONS[itemData.action]) {
					ACTIONS[itemData.action]();
				}
			});
		}
	);
</aui:script>