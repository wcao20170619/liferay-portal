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

package com.liferay.portal.search.tuning.blueprints.web.poc.internal.util;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.search.tuning.blueprints.web.poc.internal.constants.BlueprintsWebPortletKeys;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	property = {
		"javax.portlet.name=" + BlueprintsWebPortletKeys.BLUEPRINTS_WEB,
	}, 
	service = BlueprintsLocalizationHelper.class
)
public class BlueprintsLocalizationHelper {

    public String get(Locale locale, String key) {
        return _language.get(
               _getResourceBundle(locale), key);
    }

    private ResourceBundle _getResourceBundle(Locale locale) {
            ResourceBundle resourceBundle = 
            		ResourceBundleUtil.getBundle(
                    "content.Language", locale, getClass());

            return new AggregateResourceBundle(
                    resourceBundle, _getCommonResourceBundle(locale));
    }
    
	private ResourceBundle _getCommonResourceBundle(Locale locale) {
		ResourceBundle classResourceBundle = ResourceBundleUtil.getBundle(
			locale, "com.liferay.portal.search.tuning.blueprints.lang");

		return new AggregateResourceBundle(
			classResourceBundle, _portal.getResourceBundle(locale));
	}
    
    @Reference
    private Language _language;
    
    @Reference
    private Portal _portal;
}
