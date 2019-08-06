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

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<%@ page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.search.document.Document" %><%@
page import="com.liferay.portal.search.related.results.web.internal.configuration.SearchRelatedResultsPortletInstanceConfiguration" %><%@
page import="com.liferay.portal.search.related.results.web.internal.display.context.SearchRelatedResultsDisplayContext" %><%@
page import="com.liferay.portal.search.related.results.web.internal.portlet.SearchRelatedResultsPortletPreferences" %><%@
page import="com.liferay.portal.search.related.results.web.internal.portlet.SearchRelatedResultsPortletPreferencesImpl" %><%@
page import="com.liferay.portal.search.related.results.web.internal.util.PortletPreferencesJspUtil" %>

<portlet:defineObjects />

<%
SearchRelatedResultsDisplayContext searchRelatedResultsDisplayContext = new SearchRelatedResultsDisplayContext(request);

SearchRelatedResultsPortletInstanceConfiguration searchRelatedResultsPortletInstanceConfiguration = searchRelatedResultsDisplayContext.getSearchRelatedResultsPortletInstanceConfiguration();

SearchRelatedResultsPortletPreferences searchRelatedResultsPortletPreferences = new SearchRelatedResultsPortletPreferencesImpl(java.util.Optional.ofNullable(portletPreferences));
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />
<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<liferay-frontend:edit-form
	action="<%= configurationActionURL %>"
	method="post"
	name="fm"
>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<liferay-frontend:edit-form-body>
		<liferay-frontend:fieldset-group>
			<liferay-frontend:fieldset>
				<div class="display-template">
					<liferay-ddm:template-selector
						className="<%= Document.class.getName() %>"
						displayStyle="<%= searchRelatedResultsPortletInstanceConfiguration.displayStyle() %>"
						displayStyleGroupId="<%= searchRelatedResultsDisplayContext.getDisplayStyleGroupId() %>"
						refreshURL="<%= configurationRenderURL %>"
						showEmptyOption="<%= false %>"
					/>
				</div>
			</liferay-frontend:fieldset>
		</liferay-frontend:fieldset-group>

		<liferay-frontend:fieldset-group>
			<aui:fieldset>
				<aui:input helpMessage="fields-help" label="fields" name="<%= PortletPreferencesJspUtil.getInputName(searchRelatedResultsPortletPreferences.PREFERENCE_KEY_FIELDS) %>" type="text" value="<%= searchRelatedResultsPortletPreferences.getFields() %>" />
				<aui:input helpMessage="max-query-terms-help" label="max-query-terms" name="<%= PortletPreferencesJspUtil.getInputName(searchRelatedResultsPortletPreferences.PREFERENCE_KEY_MAX_QUERY_TERMS) %>" type="text" value="<%= searchRelatedResultsPortletPreferences.getMaxQueryTerms() %>" />
				<aui:input helpMessage="min-term-freq-help" label="min-term-freq" name="<%= PortletPreferencesJspUtil.getInputName(searchRelatedResultsPortletPreferences.PREFERENCE_KEY_MIN_TERM_FREQUENCY) %>" type="text" value="<%= searchRelatedResultsPortletPreferences.getMinTermFrequency() %>" />
				<aui:input helpMessage="min-doc-freq-help" label="min-doc-freq" name="<%= PortletPreferencesJspUtil.getInputName(searchRelatedResultsPortletPreferences.PREFERENCE_KEY_MIN_DOC_FREQUENCY) %>" type="text" value="<%= searchRelatedResultsPortletPreferences.getMinDocFrequency() %>" />
				<aui:input helpMessage="max-doc-freq-help" label="max-doc-freq" name="<%= PortletPreferencesJspUtil.getInputName(searchRelatedResultsPortletPreferences.PREFERENCE_KEY_MAX_DOC_FREQUENCY) %>" type="text" value="<%= searchRelatedResultsPortletPreferences.getMaxDocFrequency() %>" />
				<aui:input helpMessage="min-word-length-help" label="min-word-length" name="<%= PortletPreferencesJspUtil.getInputName(searchRelatedResultsPortletPreferences.PREFERENCE_KEY_MIN_WORD_LENGTH) %>" type="text" value="<%= searchRelatedResultsPortletPreferences.getMinWordLength() %>" />
				<aui:input helpMessage="max-word-length-help" label="max-word-length" name="<%= PortletPreferencesJspUtil.getInputName(searchRelatedResultsPortletPreferences.PREFERENCE_KEY_MAX_WORD_LENGTH) %>" type="text" value="<%= searchRelatedResultsPortletPreferences.getMaxWordLength() %>" />
				<aui:input helpMessage="stop-words-help" label="stop-words" name="<%= PortletPreferencesJspUtil.getInputName(searchRelatedResultsPortletPreferences.PREFERENCE_KEY_STOP_WORDS) %>" type="text" value="<%= searchRelatedResultsPortletPreferences.getStopWords() %>" />
				<aui:input helpMessage="analyzer-help" label="analyzer" name="<%= PortletPreferencesJspUtil.getInputName(searchRelatedResultsPortletPreferences.PREFERENCE_KEY_ANALYZER) %>" type="text" value="<%= searchRelatedResultsPortletPreferences.getAnalyzer() %>" />

				<aui:input helpMessage="min-should-match-help" label="min-should-match" name="<%= PortletPreferencesJspUtil.getInputName(searchRelatedResultsPortletPreferences.PREFERENCE_KEY_MIN_SHOULD_MATCH) %>" type="text" value="<%= searchRelatedResultsPortletPreferences.getMinShouldMatch() %>" />
				<aui:input helpMessage="term-boost-help" label="term-boost" name="<%= PortletPreferencesJspUtil.getInputName(searchRelatedResultsPortletPreferences.PREFERENCE_KEY_TERM_BOOST) %>" type="text" value="<%= searchRelatedResultsPortletPreferences.getTermBoost() %>" />
			</aui:fieldset>
		</liferay-frontend:fieldset-group>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<aui:button type="submit" />

		<aui:button type="cancel" />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>