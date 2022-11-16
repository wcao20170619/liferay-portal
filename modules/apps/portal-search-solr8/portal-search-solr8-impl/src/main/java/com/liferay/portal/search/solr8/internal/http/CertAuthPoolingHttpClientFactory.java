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

package com.liferay.portal.search.solr8.internal.http;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.search.solr8.configuration.SolrHttpClientFactoryConfiguration;

import java.util.Map;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author László Csontos
 * @author André de Oliveira
 */
@Component(
	configurationPid = "com.liferay.portal.search.solr8.configuration.SolrHttpClientFactoryConfiguration",
	immediate = true, property = "type=CERT", service = HttpClientFactory.class
)
public class CertAuthPoolingHttpClientFactory
	extends BasePoolingHttpClientFactory {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_solrHttpClientFactoryConfiguration =
			ConfigurableUtil.createConfigurable(
				SolrHttpClientFactoryConfiguration.class, properties);

		setDefaultMaxConnectionsPerRoute(
			_solrHttpClientFactoryConfiguration.
				defaultMaxConnectionsPerRoute());

		setMaxTotalConnections(
			_solrHttpClientFactoryConfiguration.maxTotalConnections());
	}

	@Override
	protected void configure(HttpClientBuilder httpClientBuilder) {
	}

	@Override
	protected PoolingHttpClientConnectionManager
			createPoolingHttpClientConnectionManager()
		throws Exception {

		Registry<ConnectionSocketFactory> schemeRegistry = createSchemeRegistry(
			_sslSocketFactoryBuilder.build());

		return new PoolingHttpClientConnectionManager(schemeRegistry);
	}

	protected Registry<ConnectionSocketFactory> createSchemeRegistry(
		SSLConnectionSocketFactory sslConnectionSocketFactory) {

		RegistryBuilder<ConnectionSocketFactory> registryBuilder =
			RegistryBuilder.create();

		registryBuilder.register("https", sslConnectionSocketFactory);

		return registryBuilder.build();
	}

	@Deactivate
	protected void deactivate() {
		shutdown();
	}

	@Reference(
		cardinality = ReferenceCardinality.AT_LEAST_ONE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void setHttpRequestInterceptor(
		HttpRequestInterceptor httpRequestInterceptor) {

		addHttpRequestInterceptor(httpRequestInterceptor);
	}

	protected void unsetHttpRequestInterceptor(
		HttpRequestInterceptor httpRequestInterceptor) {

		removeHttpRequestInterceptor(httpRequestInterceptor);
	}

	private volatile SolrHttpClientFactoryConfiguration
		_solrHttpClientFactoryConfiguration;

	@Reference
	private SSLSocketFactoryBuilder _sslSocketFactoryBuilder;

}