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
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

Blueprint blueprint = (Blueprint)row.getObject();

long blueprintId = blueprint.getBlueprintId();
int blueprintType = blueprint.getType();
long companyGroupId = themeDisplay.getCompanyGroupId();
%>

<liferay-ui:icon-menu
	direction="left-side"
	icon="<%= StringPool.BLANK %>"
	markupView="lexicon"
	message="<%= StringPool.BLANK %>"
	showWhenSingleIcon="<%= true %>"
>
	<c:if test="<%= BlueprintEntryPermission.contains(permissionChecker, blueprintId, blueprintType, ActionKeys.UPDATE) %>">
		<portlet:renderURL var="editBlueprintURL">
			<portlet:param name="mvcRenderCommandName" value="<%= BlueprintsAdminMVCCommandNames.EDIT_BLUEPRINT %>" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="<%= BlueprintsAdminWebKeys.BLUEPRINT_ID %>" value="<%= String.valueOf(blueprintId) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			message="edit"
			url="<%= editBlueprintURL %>"
		/>
	</c:if>

	<c:if test="<%= BlueprintPermission.contains(permissionChecker, companyGroupId, blueprintType, ActionKeys.ADD_ENTRY) %>">
		<portlet:actionURL name="<%= BlueprintsAdminMVCCommandNames.COPY_BLUEPRINT %>" var="copyBlueprintUrl">
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="<%= BlueprintsAdminWebKeys.BLUEPRINT_ID %>" value="<%= String.valueOf(blueprintId) %>" />
		</portlet:actionURL>

		<liferay-ui:icon
			message="copy"
			url="<%= copyBlueprintUrl %>"
		/>
	</c:if>

	<c:if test="<%= BlueprintPermission.contains(permissionChecker, companyGroupId, blueprintType, ActionKeys.PERMISSIONS) %>">
		<liferay-security:permissionsURL
			modelResource="<%= Blueprint.class.getName() %>"
			modelResourceDescription="<%= blueprint.getTitle(locale) %>"
			resourceGroupId="<%= String.valueOf(blueprint.getGroupId()) %>"
			resourcePrimKey="<%= String.valueOf(blueprintId) %>"
			var="permissionsURL"
			windowState="<%= LiferayWindowState.POP_UP.toString() %>"
		/>

		<liferay-ui:icon
			label="<%= true %>"
			message="permissions"
			method="get"
			url="<%= permissionsURL %>"
			useDialog="<%= true %>"
		/>
	</c:if>

	<portlet:resourceURL id="<%= BlueprintsAdminMVCCommandNames.EXPORT_BLUEPRINT %>" var="exportBlueprintUrl">
		<portlet:param name="redirect" value="<%= currentURL %>" />
		<portlet:param name="<%= BlueprintsAdminWebKeys.BLUEPRINT_ID %>" value="<%= String.valueOf(blueprintId) %>" />
	</portlet:resourceURL>

	<liferay-ui:icon
		message="export"
		url="<%= exportBlueprintUrl %>"
	/>

	<c:if test="<%= BlueprintEntryPermission.contains(permissionChecker, blueprintId, blueprintType, ActionKeys.DELETE) %>">
		<portlet:actionURL name="<%= BlueprintsAdminMVCCommandNames.DELETE_BLUEPRINT %>" var="deleteBlueprintURL">
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="<%= BlueprintsAdminWebKeys.BLUEPRINT_ID %>" value="<%= String.valueOf(blueprintId) %>" />
		</portlet:actionURL>

		<liferay-ui:icon-delete
			url="<%= deleteBlueprintURL %>"
		/>
	</c:if>
</liferay-ui:icon-menu>