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
page import="com.liferay.portal.search.web.internal.search.morelikethis.portlet.SearchMoreLikeThisPortletPreferences" %><%@
page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<portlet:defineObjects />

<%
	SearchMoreLikeThisPortletPreferences searchMoreLikeThisPortletPreferences = 
		new com.liferay.portal.search.web.internal.search.morelikethis.portlet.SearchMoreLikeThisPortletPreferencesImpl(java.util.Optional.ofNullable(portletPreferences));
	int maxDocFrequency = searchMoreLikeThisPortletPreferences.getMaxDocFrequency();
	int maxQueryTerms = searchMoreLikeThisPortletPreferences.getMaxQueryTerms();
	int maxWordLength = searchMoreLikeThisPortletPreferences.getMaxWordLength();
	int minDocFrequency = searchMoreLikeThisPortletPreferences.getMinDocFrequency();
	String minShouldMatch = searchMoreLikeThisPortletPreferences.getMinShouldMatch();
	int minTermFrequency = searchMoreLikeThisPortletPreferences.getMinTermFrequency();
	int minWordLength = searchMoreLikeThisPortletPreferences.getMinWordLength();
	//Optional<String> stopWords = searchMoreLikeThisPortletPreferences.getStopWords();
	String termBoost = searchMoreLikeThisPortletPreferences.getTermBoost();
	String analyzer = searchMoreLikeThisPortletPreferences.getAnalyzer();
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />
 
<div class="portlet-configuration-body-content">

	<div class="container-fluid-1280">

		 <aui:form 
		 	action="<%= configurationActionURL %>" 
		 	method="post" 
		 	name="fm"
		 >
		
		 	<aui:input
				name="<%= Constants.CMD %>"
				type="hidden"
				value="<%= Constants.UPDATE %>"
			/>
		
			<aui:fieldset>
			
				<aui:input name="cmd" type="hidden" value="update" />
				
			
			</aui:fieldset>
			
			<aui:button-row>
				<aui:button type="submit"></aui:button>
			</aui:button-row>	
		
		</aui:form>
	</div>
</div>