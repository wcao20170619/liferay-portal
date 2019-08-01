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
package com.liferay.portal.search.web.internal.search.morelikethis.portlet.shared.search;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.web.internal.util.SearchStringUtil;
import com.liferay.portal.search.query.MoreLikeThisQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.web.internal.search.morelikethis.constants.SearchMoreLikeThisPortletKeys;
import com.liferay.portal.search.web.internal.search.morelikethis.portlet.SearchMoreLikeThisPortletPreferences;
import com.liferay.portal.search.web.internal.search.morelikethis.portlet.SearchMoreLikeThisPortletPreferencesImpl;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

/**
 * @author Wade Cao
 */
@Component(
	immediate = true,
	property = "javax.portlet.name=" + SearchMoreLikeThisPortletKeys.MORE_LIKE_THIS_PORTLET,
	service = PortletSharedSearchContributor.class
)
public class SearchMoreLikeThisPortletSharedSearchContributor 
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {
			
		SearchMoreLikeThisPortletPreferences searchMoreLikeThisPortletPreferences =
			new SearchMoreLikeThisPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferencesOptional());
		
		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(
				searchMoreLikeThisPortletPreferences.getFederatedSearchKeyOptional());	
		
		searchRequestBuilder.moreLikeThisEnabled(true);
		
		String uid = getUID(portletSharedSearchSettings);
		
		if (Validator.isBlank(uid)) {
			return;
		}
		
		Optional<String> keywords = getKeywords(portletSharedSearchSettings);
			
//		searchRequestBuilder.addComplexQueryPart(
//			_complexQueryPartBuilderFactory.builder(			
//			).query(
//				getMoreLikeThisQuery(
//					searchMoreLikeThisPortletPreferences, uid, keywords)
//			).build());
	}
	
	protected String getUID(
		PortletSharedSearchSettings portletSharedSearchSettings) {
			
		return getParameterValue(Field.UID,
				portletSharedSearchSettings);
	}
	
	protected String getParameterValue(String parameterName,
		PortletSharedSearchSettings portletSharedSearchSettings) {
			
		Optional<String> parameterValue = 
			portletSharedSearchSettings.getParameterOptional(
				parameterName);
			
		return parameterValue.orElse(null);
	}
	


	protected Optional<String> getKeywords(
			PortletSharedSearchSettings portletSharedSearchSettings) {
		Optional<String> parameterName = 
			portletSharedSearchSettings.getKeywordsParameterName();
			
		Optional<String> parameterValue = 
			portletSharedSearchSettings.getParameterOptional(
				parameterName.orElse(""));
		return parameterValue;
	}
	
	@Reference
	private Queries _queries;
	
	@Reference
	private ComplexQueryPartBuilderFactory _complexQueryPartBuilderFactory;

}