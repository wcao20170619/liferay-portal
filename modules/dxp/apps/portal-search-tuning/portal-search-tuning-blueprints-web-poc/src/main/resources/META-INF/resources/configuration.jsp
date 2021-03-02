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
PortletPreferences preferences = renderRequest.getPreferences();

boolean didYouMeanEnabled = GetterUtil.getBoolean(preferences.getValue(BlueprintsWebPortletPreferenceKeys.DID_YOU_MEAN_ENABLED, "true"));

int didYouMeanHitsThreshold = GetterUtil.getInteger(preferences.getValue(BlueprintsWebPortletPreferenceKeys.DID_YOU_MEAN_HITS_THRESHOLD, "5"));

int maxDidYouMeanSuggestions = GetterUtil.getInteger(preferences.getValue(BlueprintsWebPortletPreferenceKeys.MAX_DID_YOU_MEAN_QUERY_SUGGESTIONS, "10"));

int maxTypeaheadSuggestions = GetterUtil.getInteger(preferences.getValue(BlueprintsWebPortletPreferenceKeys.MAX_TYPEAHEAD_SUGGESTIONS, "10"));

boolean misspellingsEnabled = GetterUtil.getBoolean(preferences.getValue(BlueprintsWebPortletPreferenceKeys.MISSPELLINGS_ENABLED, "true"));

boolean queryIndexingEnabled = GetterUtil.getBoolean(preferences.getValue(BlueprintsWebPortletPreferenceKeys.QUERY_INDEXING_ENABLED, "true"));

int queryIndexingHitsThreshold = GetterUtil.getInteger(preferences.getValue(BlueprintsWebPortletPreferenceKeys.QUERY_INDEXING_HITS_THRESHOLD, "3"));

boolean typeaheadEnabled = GetterUtil.getBoolean(preferences.getValue(BlueprintsWebPortletPreferenceKeys.TYPEAHEAD_ENABLED, "true"));
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
				<aui:input helpMessage="search-blueprint-id-help" label="search-blueprint-id" name='<%= "preferences--" + BlueprintsWebPortletPreferenceKeys.SEARCH_BLUEPRINT_ID + "--" %>' required="<%= true %>" type="text" value='<%= preferences.getValue(BlueprintsWebPortletPreferenceKeys.SEARCH_BLUEPRINT_ID, "0") %>' />

				<hr />

				<aui:input helpMessage="typeahead-enabled-help" id="typeaheadEnabled" label="enable-typeahead" name="preferences--typeaheadEnabled--" type="checkbox" value="<%= typeaheadEnabled %>" />

				<div class="options-container <%= !typeaheadEnabled ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />typeaheadOptionsContainer">
					<aui:input label="maximum-number-of-typeahead-suggestions" name="preferences--maxTypeaheadSuggestions--" size="10" type="text" value="<%= maxTypeaheadSuggestions %>" />
				</div>

				<hr />

				<aui:input helpMessage="misspellings-enabled-help" id="misspellingsEnabled" label="enable-misspellings" name="preferences--misspellingsEnabled--" type="checkbox" value="<%= misspellingsEnabled %>" />

				<hr />

				<aui:input helpMessage="did-you-mean-enabled-help" id="didYouMeanEnabled" label="enable-did-you-mean" name="preferences--didYouMeanEnabled--" type="checkbox" value="<%= didYouMeanEnabled %>" />

				<div class="options-container <%= !didYouMeanEnabled ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />didYouMeanOptionsContainer">
					<aui:input helpMessage="query-indexing-threshold-help" label="hits-threshold-for-showing-did-you-mean-suggestions" name="preferences--didYouMeanHitsThreshold--" size="10" type="text" value="<%= didYouMeanHitsThreshold %>" />

					<aui:input label="maximum-number-of-did-you-mean-suggestions" name="preferences--maxDidYouMeanSuggestions--" size="10" type="text" value="<%= maxDidYouMeanSuggestions %>" />
				</div>

				<hr />

				<aui:input helpMessage="query-indexing-enabled-help" id="queryIndexingEnabled" label="enable-query-indexing" name="preferences--queryIndexingEnabled--" type="checkbox" value="<%= queryIndexingEnabled %>" />

				<div class="options-container <%= !queryIndexingEnabled ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />queryIndexingOptionsContainer">
					<aui:input label="hits-threshold-for-indexing-queries" name="preferences--queryIndexingHitsThreshold--" size="3" type="text" value="<%= queryIndexingHitsThreshold %>" />

			</liferay-frontend:fieldset>
		</liferay-frontend:fieldset-group>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<aui:button type="submit" />

		<aui:button type="cancel" />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script>
	Liferay.Util.toggleBoxes(
		'<portlet:namespace />didYouMeanEnabled',
		'<portlet:namespace />didYouMeanOptionsContainer'
	);
	Liferay.Util.toggleBoxes(
		'<portlet:namespace />queryIndexingEnabled',
		'<portlet:namespace />queryIndexingOptionsContainer'
	);
	Liferay.Util.toggleBoxes(
		'<portlet:namespace />typeaheadEnabled',
		'<portlet:namespace />typeaheadOptionsContainer'
	);
</aui:script>