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
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<%@ page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.search.web.internal.morelikethis.portlet.MoreLikeThisPortletPreferences" %><%@
page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<portlet:defineObjects />

<%
MoreLikeThisPortletPreferences moreLikeThisPortletPreferences = new com.liferay.portal.search.web.internal.morelikethis.portlet.MoreLikeThisPortletPreferencesImpl(java.util.Optional.ofNullable(portletPreferences));
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
			<aui:fieldset>
				<aui:input helpMessage="fields-help" label="fields" name="<%= PortletPreferencesJspUtil.getInputName(moreLikeThisPortletPreferences.PREFERENCE_KEY_FIELDS) %>" type="text" value="<%= moreLikeThisPortletPreferences.getFields() %>" />
				<aui:input helpMessage="max-query-terms-help" label="max-query-terms" name="<%= PortletPreferencesJspUtil.getInputName(moreLikeThisPortletPreferences.PREFERENCE_KEY_MAX_QUERY_TERMS) %>" type="text" value="<%= moreLikeThisPortletPreferences.getMaxQueryTerms() %>" />
				<aui:input helpMessage="min-term-freq-help" label="min-term-freq" name="<%= PortletPreferencesJspUtil.getInputName(moreLikeThisPortletPreferences.PREFERENCE_KEY_MIN_TERM_FREQUENCY) %>" type="text" value="<%= moreLikeThisPortletPreferences.getMinTermFrequency() %>" />
				<aui:input helpMessage="min-doc-freq-help" label="min-doc-freq" name="<%= PortletPreferencesJspUtil.getInputName(moreLikeThisPortletPreferences.PREFERENCE_KEY_MIN_DOC_FREQUENCY) %>" type="text" value="<%= moreLikeThisPortletPreferences.getMinDocFrequency() %>" />
				<aui:input helpMessage="max-doc-freq-help" label="max-doc-freq" name="<%= PortletPreferencesJspUtil.getInputName(moreLikeThisPortletPreferences.PREFERENCE_KEY_MAX_DOC_FREQUENCY) %>" type="text" value="<%= moreLikeThisPortletPreferences.getMaxDocFrequency() %>" />
				<aui:input helpMessage="min-word-length-help" label="min-word-length" name="<%= PortletPreferencesJspUtil.getInputName(moreLikeThisPortletPreferences.PREFERENCE_KEY_MIN_WORD_LENGTH) %>" type="text" value="<%= moreLikeThisPortletPreferences.getMinWordLength() %>" />
				<aui:input helpMessage="max-word-length-help" label="max-word-length" name="<%= PortletPreferencesJspUtil.getInputName(moreLikeThisPortletPreferences.PREFERENCE_KEY_MAX_WORD_LENGTH) %>" type="text" value="<%= moreLikeThisPortletPreferences.getMaxWordLength() %>" />
				<aui:input helpMessage="stop-words-help" label="stop-words" name="<%= PortletPreferencesJspUtil.getInputName(moreLikeThisPortletPreferences.PREFERENCE_KEY_STOP_WORDS) %>" type="text" value="<%= moreLikeThisPortletPreferences.getStopWords() %>" />
				<aui:input helpMessage="analyzer-help" label="analyzer" name="<%= PortletPreferencesJspUtil.getInputName(moreLikeThisPortletPreferences.PREFERENCE_KEY_ANALYZER) %>" type="text" value="<%= moreLikeThisPortletPreferences.getAnalyzer() %>" />

				<aui:input helpMessage="min-should-match-help" label="min-should-match" name="<%= PortletPreferencesJspUtil.getInputName(moreLikeThisPortletPreferences.PREFERENCE_KEY_MIN_SHOULD_MATCH) %>" type="text" value="<%= moreLikeThisPortletPreferences.getMinShouldMatch() %>" />
				<aui:input helpMessage="term-boost-help" label="term-boost" name="<%= PortletPreferencesJspUtil.getInputName(moreLikeThisPortletPreferences.PREFERENCE_KEY_TERM_BOOST) %>" type="text" value="<%= moreLikeThisPortletPreferences.getTermBoost() %>" />
			</aui:fieldset>
		</liferay-frontend:fieldset-group>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<aui:button type="submit" />
		<aui:button type="cancel" />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>