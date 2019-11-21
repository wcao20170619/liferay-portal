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

import com.liferay.portal.search.elasticsearch7.configuration.CCRClusterType;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;

import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.KeyStore;

import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author Wade Cao
 */
public abstract class CCRBaseElasticsearchConnection
	extends BaseElasticsearchConnection {

	@Override
	public OperationMode getOperationMode() {
		return OperationMode.REMOTE;
	}

	protected void configureSecurity(RestClientBuilder restClientBuilder) {
		restClientBuilder.setHttpClientConfigCallback(
			new RestClientBuilder.HttpClientConfigCallback() {

				@Override
				public HttpAsyncClientBuilder customizeHttpClient(
					HttpAsyncClientBuilder httpClientBuilder) {

					httpClientBuilder.setDefaultCredentialsProvider(
						createCredentialsProvider());

					if (getElasticsearchConfiguration().httpSSLEnabled()) {
						httpClientBuilder.setSSLContext(createSSLContext());
					}

					return httpClientBuilder;
				}

			});
	}

	protected CredentialsProvider createCredentialsProvider() {
		CredentialsProvider credentialsProvider =
			new BasicCredentialsProvider();

		credentialsProvider.setCredentials(
			AuthScope.ANY,
			new UsernamePasswordCredentials(
				getElasticsearchConfiguration().username(),
				getElasticsearchConfiguration().password()));

		return credentialsProvider;
	}

	protected RestHighLevelClient createRestHighLevelClient() {
		String[] networkHostAddresses =
			getElasticsearchConfiguration().networkHostAddresses();

		HttpHost[] httpHosts = new HttpHost[networkHostAddresses.length];

		for (int i = 0; i < networkHostAddresses.length; i++) {
			httpHosts[i] = HttpHost.create(networkHostAddresses[i]);
		}

		RestClientBuilder restClientBuilder = RestClient.builder(httpHosts);

		if (getElasticsearchConfiguration().authenticationEnabled()) {
			configureSecurity(restClientBuilder);
		}

		return new RestHighLevelClient(restClientBuilder);
	}

	protected SSLContext createSSLContext() {
		try {
			KeyStore keyStore = KeyStore.getInstance(
				getElasticsearchConfiguration().truststoreType());
			String truststorePath =
				getElasticsearchConfiguration().truststorePath();
			String truststorePassword =
				getElasticsearchConfiguration().truststorePassword();

			Path path = Paths.get(truststorePath);

			InputStream is = Files.newInputStream(path);

			keyStore.load(is, truststorePassword.toCharArray());

			SSLContextBuilder sslContextBuilder = SSLContexts.custom();

			sslContextBuilder.loadKeyMaterial(
				keyStore, truststorePassword.toCharArray());
			sslContextBuilder.loadTrustMaterial(keyStore, null);

			return sslContextBuilder.build();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract CCRClusterType getCCRClusterType();

	protected abstract ElasticsearchConfiguration
		getElasticsearchConfiguration();

	protected synchronized void modified(Map<String, Object> properties) {
		elasticsearchConfiguration = getElasticsearchConfiguration();

		if (isConnected()) {
			close();
		}

		if (!isConnected()) {
			connect();
		}
	}

}