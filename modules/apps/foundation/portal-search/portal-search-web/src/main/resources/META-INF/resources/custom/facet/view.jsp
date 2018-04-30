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

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.facet.display.context.CustomSearchFacetDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.facet.display.context.CustomSearchFacetTermDisplayContext" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />

<style>
	.facet-checkbox-label {
		display: block;
	}
</style>

<%
CustomSearchFacetDisplayContext customSearchFacetDisplayContext = (CustomSearchFacetDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));
%>

<c:choose>
	<c:when test="<%= customSearchFacetDisplayContext.isRenderNothing() %>">
		<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(customSearchFacetDisplayContext.getParamName()) %>" type="hidden" value="<%= customSearchFacetDisplayContext.getParamValue() %>" />
	</c:when>
	<c:otherwise>
		<liferay-ui:panel-container
			extended="<%= true %>"
			id='<%= renderResponse.getNamespace() + "facetCustomPanelContainer" %>'
			markupView="lexicon"
			persistState="<%= true %>"
		>
			<liferay-ui:panel
				collapsible="<%= true %>"
				cssClass="search-facet"
				id='<%= renderResponse.getNamespace() + "facetCustomPanel" %>'
				markupView="lexicon"
				persistState="<%= true %>"
				title="<%= customSearchFacetDisplayContext.getFieldLabel() %>"
			>
				<aui:form method="post" name="customFacetForm">
					<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(customSearchFacetDisplayContext.getParamName()) %>" type="hidden" value="<%= customSearchFacetDisplayContext.getParamValue() %>" />
					<aui:input cssClass="facet-parameter-name" name="facet-parameter-name" type="hidden" value="<%= customSearchFacetDisplayContext.getParamName() %>" />

					<aui:fieldset>
						<ul class="list-unstyled">

							<%
							int i = 0;

							for (CustomSearchFacetTermDisplayContext customSearchFacetTermDisplayContext : customSearchFacetDisplayContext.getTermDisplayContexts()) {
								i++;
							%>

								<li class="facet-value">
									<label class="facet-checkbox-label" for="<portlet:namespace />term_<%= i %>">
										<input class="facet-term" data-term-id="<%= customSearchFacetTermDisplayContext.getFieldName() %>" id="<portlet:namespace />term_<%= i %>" name="<portlet:namespace />term_<%= i %>" onChange="Liferay.Search.FacetUtil.changeSelection(event);" type="checkbox" <%= customSearchFacetTermDisplayContext.isSelected() ? "checked" : StringPool.BLANK %> />

										<span class="term-name">
											<%= HtmlUtil.escape(customSearchFacetTermDisplayContext.getFieldName()) %>
										</span>

										<c:if test="<%= customSearchFacetTermDisplayContext.isFrequencyVisible() %>">
											<small class="term-count">
												(<%= customSearchFacetTermDisplayContext.getFrequency() %>)
											</small>
										</c:if>
									</label>
								</li>

							<%
							}
							%>

						</ul>
					</aui:fieldset>

					<c:if test="<%= !customSearchFacetDisplayContext.isNothingSelected() %>">
						<a class="text-default" href="javascript:;" onClick="Liferay.Search.FacetUtil.clearSelections(event);"><small><liferay-ui:message key="clear" /></small></a>
					</c:if>
				</aui:form>
			</liferay-ui:panel>
		</liferay-ui:panel-container>
	</c:otherwise>
</c:choose>

<aui:script use="liferay-search-facet-util"></aui:script>