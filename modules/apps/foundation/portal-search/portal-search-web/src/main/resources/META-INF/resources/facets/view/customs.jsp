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

<%@ include file="/facets/init.jsp" %>

<%
com.liferay.portal.search.web.internal.facet.display.builder.CustomSearchFacetDisplayBuilder customSearchFacetDisplayBuilder = new com.liferay.portal.search.web.internal.facet.display.builder.CustomSearchFacetDisplayBuilder();

customSearchFacetDisplayBuilder.setFacet(facet);
customSearchFacetDisplayBuilder.setFrequenciesVisible(dataJSONObject.getBoolean("showAssetCount", true));
customSearchFacetDisplayBuilder.setFrequencyThreshold(dataJSONObject.getInt("frequencyThreshold"));
customSearchFacetDisplayBuilder.setMaxTerms(dataJSONObject.getInt("maxTerms", 10));
customSearchFacetDisplayBuilder.setParamName(facet.getFieldId());
customSearchFacetDisplayBuilder.setParamValue(fieldParam);
customSearchFacetDisplayBuilder.setFieldToAggregate(dataJSONObject.getString("fieldToAggregate", ""));
customSearchFacetDisplayBuilder.setFieldLabel(dataJSONObject.getString("fieldLabel", ""));

com.liferay.portal.search.web.internal.facet.display.context.CustomSearchFacetDisplayContext customSearchFacetDisplayContext = customSearchFacetDisplayBuilder.build();
%>

<c:choose>
	<c:when test="<%= customSearchFacetDisplayContext.isRenderNothing() %>">
		<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(customSearchFacetDisplayContext.getParamName()) %>" type="hidden" value="<%= customSearchFacetDisplayContext.getParamValue() %>" />
	</c:when>
	<c:otherwise>
		<div class="panel panel-default">
			<div class="panel-heading">
				<div class="panel-title">
					<liferay-ui:message key="customs" />
				</div>
			</div>

			<div class="panel-body">
				<div class="<%= cssClass %>" data-facetFieldName="<%= HtmlUtil.escapeAttribute(customSearchFacetDisplayContext.getParamName()) %>" id="<%= randomNamespace %>facet">
					<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(customSearchFacetDisplayContext.getParamName()) %>" type="hidden" value="<%= customSearchFacetDisplayContext.getParamValue() %>" />

					<ul class="customs list-unstyled">
						<li class="default facet-value">
							<a class="<%= customSearchFacetDisplayContext.isNothingSelected() ? "text-primary" : "text-default" %>" data-value="" href="javascript:;"><liferay-ui:message key="<%= HtmlUtil.escape(facetConfiguration.getLabel()) %>" /></a>
						</li>

						<%
						java.util.List<com.liferay.portal.search.web.internal.facet.display.context.CustomSearchFacetTermDisplayContext> customSearchFacetTermDisplayContexts =
							customSearchFacetDisplayContext.getTermDisplayContexts();

						for (com.liferay.portal.search.web.internal.facet.display.context.CustomSearchFacetTermDisplayContext customSearchFacetTermDisplayContext : customSearchFacetTermDisplayContexts) {
						%>

							<li class="facet-value">
								<a class="<%= customSearchFacetTermDisplayContext.isSelected() ? "text-primary" : "text-default" %>" data-value="<%= HtmlUtil.escapeAttribute(customSearchFacetTermDisplayContext.getFieldName()) %>" href="javascript:;">
									<%= HtmlUtil.escape(customSearchFacetTermDisplayContext.getFieldName()) %>

									<c:if test="<%= customSearchFacetTermDisplayContext.isFrequencyVisible() %>">
										<span class="frequency">(<%= customSearchFacetTermDisplayContext.getFrequency() %>)</span>
									</c:if>
								</a>
							</li>

						<%
						}
						%>

					</ul>
				</div>
			</div>
		</div>
	</c:otherwise>
</c:choose>