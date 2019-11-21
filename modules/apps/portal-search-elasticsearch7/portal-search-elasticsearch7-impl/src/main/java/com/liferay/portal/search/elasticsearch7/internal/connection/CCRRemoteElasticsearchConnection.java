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

package com.liferay.portal.search.elasticsearch7.internal.connection;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.search.elasticsearch7.configuration.CCRClusterType;
import com.liferay.portal.search.elasticsearch7.configuration.CCRRemoteElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Wade Cao
 */
@Component(
	configurationPid = "com.liferay.portal.search.elasticsearch7.configuration.CCRRemoteElasticsearchConfiguration",
	immediate = true, property = "ccr.cluster.type=REMOTE",
	service = ElasticsearchConnection.class
)
public class CCRRemoteElasticsearchConnection
	extends CCRBaseElasticsearchConnection {

	@Override
	public CCRClusterType getCCRClusterType() {
		return CCRClusterType.REMOTE;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		elasticsearchConfiguration = ConfigurableUtil.createConfigurable(
			CCRRemoteElasticsearchConfiguration.class, properties);
	}

	@Deactivate
	protected void deactivate(Map<String, Object> properties) {
		close();
	}

	@Override
	protected ElasticsearchConfiguration getElasticsearchConfiguration() {
		return elasticsearchConfiguration;
	}

	@Modified
	protected synchronized void modified(Map<String, Object> properties) {
		elasticsearchConfiguration = ConfigurableUtil.createConfigurable(
			CCRRemoteElasticsearchConfiguration.class, properties);

		super.modified(properties);
	}

}