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
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.search.admin.web.internal.display.context.SearchAdminDisplayContext" %><%@
page import="com.liferay.portal.kernel.backgroundtask.BackgroundTask" %><%@
page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskConstants" %><%@
page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil" %><%@
page import="com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay" %><%@
page import="com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplayFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.model.CompanyConstants" %><%@
page import="com.liferay.portal.kernel.search.Indexer" %><%@
page import="com.liferay.portal.kernel.search.IndexerClassNameComparator" %><%@
page import="com.liferay.portal.kernel.search.IndexerRegistryUtil" %>

<%@ page import="java.io.Serializable" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Collection" %><%@
page import="java.util.Collections" %><%@
page import="java.util.HashMap" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %>

<%@ page import="javax.portlet.PortletURL" %>

<portlet:defineObjects />

<%
SearchAdminDisplayContext searchAdminDisplayContext = (SearchAdminDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("mvcRenderCommandName", "/search_admin/view");
%>

<portlet:renderURL var="redirectURL">
	<portlet:param name="mvcRenderCommandName" value="/search_admin/view" />
</portlet:renderURL>

<aui:form action="<%= portletURL.toString() %>" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= redirectURL %>" />

	<liferay-ui:panel-container
		extended="<%= true %>"
		id="adminSearchAdministrationActionsPanelContainer"
		persistState="<%= true %>"
	>
		<div class="panel panel-default search-admin-tabs id="adminSearchInformationPanel">
			<div class="panel-body">
				<c:choose>
					<c:when test="<%= !searchAdminDisplayContext.isMissingSearchEngine() %>">
						<div class="alert alert-info">
							<strong><liferay-ui:message key="search-engine" /></strong>: <%= searchAdminDisplayContext.getStatusString() %>
						</div>
					</c:when>
					<c:otherwise>
						<div class="alert alert-warning">
							<liferay-ui:message key="no-search-engine-detected-help" />
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<liferay-ui:panel
			collapsible="<%= true %>"
			cssClass="search-admin-actions-panel"
			extended="<%= true %>"
			id="adminSearchAdminIndexActionsPanel"
			markupView="lexicon"
			persistState="<%= true %>"
			title="index-actions"
		>

			<%
			Map<String, BackgroundTaskDisplay> classNameToBackgroundTaskDisplayMap = new HashMap<>();

			List<BackgroundTask> reindexPortalBackgroundTasks = BackgroundTaskManagerUtil.getBackgroundTasks(CompanyConstants.SYSTEM, "com.liferay.portal.search.internal.background.task.ReindexPortalBackgroundTaskExecutor", BackgroundTaskConstants.STATUS_IN_PROGRESS);
			List<BackgroundTask> reindexSingleBackgroundTasks = BackgroundTaskManagerUtil.getBackgroundTasks(CompanyConstants.SYSTEM, "com.liferay.portal.search.internal.background.task.ReindexSingleIndexerBackgroundTaskExecutor", BackgroundTaskConstants.STATUS_IN_PROGRESS);

			if (!reindexSingleBackgroundTasks.isEmpty()) {
				for (BackgroundTask backgroundTask : reindexSingleBackgroundTasks) {
					Map<String, Serializable> taskContextMap = backgroundTask.getTaskContextMap();

					String className = (String)taskContextMap.get("className");

					classNameToBackgroundTaskDisplayMap.put(className, BackgroundTaskDisplayFactoryUtil.getBackgroundTaskDisplay(backgroundTask));
				}
			}
			%>

			<ul class="list-group system-action-group">
				<li class="clearfix list-group-item">
					<div class="pull-left">
						<h5><liferay-ui:message key="reindex-all-search-indexes" /></h5>
					</div>

					<%
					BackgroundTask backgroundTask = null;
					BackgroundTaskDisplay backgroundTaskDisplay = null;

					if (!reindexPortalBackgroundTasks.isEmpty()) {
						backgroundTask = reindexPortalBackgroundTasks.get(0);

						backgroundTaskDisplay = BackgroundTaskDisplayFactoryUtil.getBackgroundTaskDisplay(backgroundTask);
					}
					%>

					<div class="index-action-wrapper pull-right" data-type="portal">
						<c:choose>
							<c:when test="<%= (backgroundTaskDisplay == null) || !backgroundTaskDisplay.hasPercentage() %>">

								<%
								long timeout = ParamUtil.getLong(request, "timeout");
								%>

								<aui:button cssClass="save-server-button" data-blocking='<%= ParamUtil.getBoolean(request, "blocking") %>' data-cmd="reindex" data-timeout="<%= (timeout == 0) ? StringPool.BLANK : timeout %>" value="execute" />
							</c:when>
							<c:otherwise>
								<%= backgroundTaskDisplay.renderDisplayTemplate() %>
							</c:otherwise>
						</c:choose>
					</div>
				</li>
				<li class="clearfix list-group-item">
					<div class="pull-left">
						<h5><liferay-ui:message key="reindex-all-spell-check-indexes" /></h5>
					</div>

					<div class="pull-right">
						<aui:button cssClass="save-server-button" data-cmd="reindexDictionaries" value="execute" />
					</div>
				</li>

				<%
				List<Indexer<?>> indexers = new ArrayList<>(IndexerRegistryUtil.getIndexers());

				Collections.sort(indexers, new IndexerClassNameComparator(true));

				for (Indexer<?> indexer : indexers) {
					backgroundTaskDisplay = classNameToBackgroundTaskDisplayMap.get(indexer.getClassName());
				%>

					<li class="clearfix list-group-item">
						<div class="pull-left">
							<h5><liferay-ui:message arguments="<%= indexer.getClassName() %>" key="reindex-x" /></h5>
						</div>

						<div class="index-action-wrapper pull-right" data-type="<%= indexer.getClassName() %>">
							<c:choose>
								<c:when test="<%= (backgroundTaskDisplay == null) || !backgroundTaskDisplay.hasPercentage() %>">
									<aui:button cssClass="save-server-button" data-classname="<%= indexer.getClassName() %>" data-cmd="reindex" disabled="<%= !indexer.isIndexerEnabled() %>" value="execute" />
								</c:when>
								<c:otherwise>
									<%= backgroundTaskDisplay.renderDisplayTemplate() %>
								</c:otherwise>
							</c:choose>
						</div>
					</li>

				<%
				}
				%>

			</ul>
		</liferay-ui:panel>
	</liferay-ui:panel-container>
</aui:form>

<portlet:renderURL var="redirectURL">
	<portlet:param name="mvcRenderCommandName" value="/search_admin/view" />
</portlet:renderURL>

<aui:script use="liferay-admin">
	new Liferay.Portlet.Admin(
		{
			form: document.<portlet:namespace />fm,
			indexActionsPanel: '#adminSearchAdminIndexActionsPanel',
			namespace: '<portlet:namespace />',
			redirectUrl: '<%= redirectURL %>',
			submitButton: '.save-server-button',
			url: '<portlet:actionURL name="/search_admin/edit_search" />'
		}
	);
</aui:script>