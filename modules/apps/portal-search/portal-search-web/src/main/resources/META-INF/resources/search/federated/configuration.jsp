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
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.search.web.internal.search.federated.portlet.VideoResultsPortletPreferences" %><%@
page import="com.liferay.portal.search.web.internal.search.federated.portlet.VideoResultsPortletPreferencesImpl" %><%@
page import="com.liferay.portal.search.web.internal.search.federated.portlet.VideoResultsDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<%@ page import="java.util.Objects" %>
<%@ page
	import="com.liferay.portal.search.web.search.request.FederatedSearcher" %>
<%@ page import="java.util.Collection" %>
<%@ page import="com.liferay.registry.RegistryUtil" %>
<%@ page import="com.liferay.registry.Registry" %>
<portlet:defineObjects />

<%
Registry registry = RegistryUtil.getRegistry();

Collection<FederatedSearcher> federatedSearchers = registry.getServices(FederatedSearcher.class, null);

VideoResultsPortletPreferences videoResultsPortletPreferences = 
  new VideoResultsPortletPreferencesImpl(java.util.Optional.of(portletPreferences), federatedSearchers);

VideoResultsDisplayContext videoResultsDisplayContext =
  new VideoResultsDisplayContext(renderRequest, videoResultsPortletPreferences);

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
			<aui:select label="Federated Search Source Name" name="<%= PortletPreferencesJspUtil.getInputName(VideoResultsPortletPreferences.PREFERENCE_KEY_FEDERATED_SEARCH_SOURCE_NAME) %>">
			<%
				String[] sourceNames = videoResultsPortletPreferences.getFederatedSearchSourceNames();

				for (String sourceName : sourceNames) {
			%>
			<aui:option label="<%= sourceName %>" selected="<%= sourceName.equals(videoResultsPortletPreferences.getFederatedSearchSourceName()) %>" value="<%= sourceName %>" />
			<%
				}
			%>
			</aui:select>
			<liferay-frontend:fieldset
				collapsed="<%= false %>"
				collapsible="<%= true %>"
				label="display-settings"
			>
				
				<aui:select inlineField="<%= true %>" label="preset-frame-size" name="<%= PortletPreferencesJspUtil.getInputName(VideoResultsPortletPreferences.PREFERENCE_FRAME_SIZE) %>" onChange='<%= renderResponse.getNamespace() + "updateFrameSize(this.value);" %>' value="<%= videoResultsDisplayContext.getPresetSize() %>">
					<aui:option label="custom" selected='<%= Objects.equals(videoResultsDisplayContext.getPresetSize(), "custom") %>' value="custom" />
					<aui:option label="standard-360-4-3" selected='<%= Objects.equals(videoResultsDisplayContext.getPresetSize(), "480x360") %>' value="480x360" />
					<aui:option label="standard-360-16-9" selected='<%= Objects.equals(videoResultsDisplayContext.getPresetSize(), "640x360") %>' value="640x360" />
					<aui:option label="enhanced-480-4-3" selected='<%= Objects.equals(videoResultsDisplayContext.getPresetSize(), "640x480") %>' value="640x480" />
					<aui:option label="enhanced-480-16-9" selected='<%= Objects.equals(videoResultsDisplayContext.getPresetSize(), "854x480") %>' value="854x480" />
					<aui:option label="hd-720-4-3" selected='<%= Objects.equals(videoResultsDisplayContext.getPresetSize(), "960x720") %>' value="960x720" />
					<aui:option label="hd-720-16-9" selected='<%= Objects.equals(videoResultsDisplayContext.getPresetSize(), "1280x720") %>' value="1280x720" />
					<aui:option label="full-hd-1080-4-3" selected='<%= Objects.equals(videoResultsDisplayContext.getPresetSize(), "1440x1080") %>' value="1440x1080" />
					<aui:option label="full-hd-1080-16-9" selected='<%= Objects.equals(videoResultsDisplayContext.getPresetSize(), "1920x1080") %>' value="1920x1080" />
				</aui:select>

				<aui:input disabled="<%= !videoResultsDisplayContext.isCustomSize() %>" inlineField="<%= true %>" label="frame-width" name="<%= PortletPreferencesJspUtil.getInputName(VideoResultsPortletPreferences.PREFERENCE_WIDTH) %>" value="<%= videoResultsDisplayContext.getWidth() %>">
					<aui:validator name="digits" />
				</aui:input>

				<aui:input disabled="<%= !videoResultsDisplayContext.isCustomSize() %>" inlineField="<%= true %>" label="frame-height" name="<%= PortletPreferencesJspUtil.getInputName(VideoResultsPortletPreferences.PREFERENCE_HEIGHT) %>" value="<%= videoResultsDisplayContext.getHeight() %>">
					<aui:validator name="digits" />
				</aui:input>
			</liferay-frontend:fieldset>

			<liferay-frontend:fieldset
				collapsed="<%= true %>"
				collapsible="<%= true %>"
				label="advanced-options"
			>
				<aui:input label="watch-this-video-at-youtube" name="<%= PortletPreferencesJspUtil.getInputName(VideoResultsPortletPreferences.PREFERENCE_SHOW_THUMBNAIL) %>" type="toggle-switch" value="<%= videoResultsDisplayContext.isShowThumbnail() %>" />

				<div class="<%= videoResultsDisplayContext.isShowThumbnail() ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />videoPreferences">
					<aui:input inlineField="<%= true %>" label="auto-play" name="<%= PortletPreferencesJspUtil.getInputName(VideoResultsPortletPreferences.PREFERENCE_AUTO_PLAY) %>" type="toggle-switch" value="<%= videoResultsDisplayContext.isAutoPlay() %>" />

					<aui:input inlineField="<%= true %>" name="<%= PortletPreferencesJspUtil.getInputName(VideoResultsPortletPreferences.PREFERENCE_LOOP) %>" type="toggle-switch" value="<%= videoResultsDisplayContext.isLoop() %>" />

					<aui:input inlineField="<%= true %>" name="<%= PortletPreferencesJspUtil.getInputName(VideoResultsPortletPreferences.PREFERENCE_ENABLE_KEYBOARD_CONTROLS) %>" type="toggle-switch" value="<%= videoResultsDisplayContext.isEnableKeyboardControls() %>" />

					<aui:input inlineField="<%= true %>" name="<%= PortletPreferencesJspUtil.getInputName(VideoResultsPortletPreferences.PREFERENCE_ANNOTATIONS) %>" type="toggle-switch" value="<%= videoResultsDisplayContext.isAnnotations() %>" />

					<aui:input inlineField="<%= true %>" name="<%= PortletPreferencesJspUtil.getInputName(VideoResultsPortletPreferences.PREFERENCE_CLOSED_CAPTIONING) %>" type="toggle-switch" value="<%= videoResultsDisplayContext.isClosedCaptioning() %>" />

				</div>
			</liferay-frontend:fieldset>
		</liferay-frontend:fieldset-group>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<aui:button type="submit" />

		<aui:button type="cancel" />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script>
	function <portlet:namespace />updateFrameSize(value) {
		var Util = Liferay.Util;

		var heightNode = document.querySelector('#<portlet:namespace />height');
		var widthNode = document.querySelector('#<portlet:namespace />width');

		var useDefaults = value != 'custom';

		Util.toggleDisabled(heightNode, useDefaults);
		Util.toggleDisabled(widthNode, useDefaults);

		if (useDefaults) {
			var dimensions = value.split('x');

			heightNode.value = dimensions[1];
			widthNode.value = dimensions[0];
		}
	}

	Liferay.Util.toggleBoxes('<portlet:namespace />showThumbnail','<portlet:namespace />videoPreferences','<%= videoResultsDisplayContext.isShowThumbnail() %>');
</aui:script>