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

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.elasticsearch7.configuration.CCRClusterType;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Wade Cao
 */
public class CCRElasticsearchConnectionManagerTest {

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		resetMockConnections();

		_ccrElasticsearchConnectionManager =
			createElasticsearchConnectionManager(
				_ccrRemoteElasticsearchConnection,
				_ccrLocalElasticsearchConnection);
	}

	@Test
	public void testActivateMustNotOpenAnyConnection() {
		_ccrElasticsearchConnectionManager.activate();

		verifyNeverCloseNeverConnect(_ccrRemoteElasticsearchConnection);
		verifyNeverCloseNeverConnect(_ccrLocalElasticsearchConnection);
	}

	@Test
	public void testActivateThenConnect() {
		_ccrElasticsearchConnectionManager.activate();

		_ccrElasticsearchConnectionManager.connect();

		verifyConnectNeverClose(_ccrRemoteElasticsearchConnection);
		verifyConnectNeverClose(_ccrLocalElasticsearchConnection);
	}

	@Test
	public void testBothModifyMustNotResetConnection() {
		modify(CCRClusterType.REMOTE);
		modify(CCRClusterType.LOCAL);

		resetMockConnections();

		modify(CCRClusterType.REMOTE);
		modify(CCRClusterType.LOCAL);

		verifyNeverCloseNeverConnect(_ccrRemoteElasticsearchConnection);
		verifyNeverCloseNeverConnect(_ccrLocalElasticsearchConnection);
	}

	@Test
	public void testGetRestHighLevelClient() {
		modify(CCRClusterType.REMOTE);
		modify(CCRClusterType.LOCAL);

		_ccrElasticsearchConnectionManager.getRemoteRestHighLevelClient();

		Mockito.verify(
			_ccrRemoteElasticsearchConnection
		).getRestHighLevelClient();

		_ccrElasticsearchConnectionManager.getLocalRestHighLevelClient();

		Mockito.verify(
			_ccrLocalElasticsearchConnection
		).getRestHighLevelClient();
	}

	@Test
	public void testOneOfModifyMustNotResetConnection() {
		modify(CCRClusterType.REMOTE);

		verifyNeverCloseNeverConnect(_ccrRemoteElasticsearchConnection);
		verifyNeverCloseNeverConnect(_ccrLocalElasticsearchConnection);

		resetMockConnections();

		modify(CCRClusterType.LOCAL);

		verifyNeverCloseNeverConnect(_ccrRemoteElasticsearchConnection);
		verifyNeverCloseNeverConnect(_ccrLocalElasticsearchConnection);
	}

	@Test
	public void testUnsetElasticsearchConnection() {
		_ccrElasticsearchConnectionManager.unsetRemoteElasticsearchConnection(
			_ccrRemoteElasticsearchConnection);
		_ccrElasticsearchConnectionManager.unsetLocalElasticsearchConnection(
			_ccrLocalElasticsearchConnection);

		verifyCloseNeverConnect(_ccrRemoteElasticsearchConnection);
		verifyCloseNeverConnect(_ccrLocalElasticsearchConnection);

		resetMockConnections();

		try {
			modify(CCRClusterType.REMOTE);

			Assert.fail();
		}
		catch (MissingCCRClusterTypeException mccrcte) {
			String message = mccrcte.getMessage();

			Assert.assertTrue(
				message,
				message.contains(String.valueOf(CCRClusterType.REMOTE)));
		}

		verifyNeverCloseNeverConnect(_ccrRemoteElasticsearchConnection);
		verifyNeverCloseNeverConnect(_ccrLocalElasticsearchConnection);
	}

	protected CCRElasticsearchConnectionManager
		createElasticsearchConnectionManager(
			ElasticsearchConnection ccrRemoteElasticsearchConnection,
			ElasticsearchConnection ccrLcalElasticsearchConnection) {

		CCRElasticsearchConnectionManager
			ccrElasticsearchConnectionManagerImpl =
				new CCRElasticsearchConnectionManager();

		ccrElasticsearchConnectionManagerImpl.
			setCCRRemoteElasticsearchConnection(
				ccrRemoteElasticsearchConnection);
		ccrElasticsearchConnectionManagerImpl.
			setCCRLocalElasticsearchConnection(ccrLcalElasticsearchConnection);

		return ccrElasticsearchConnectionManagerImpl;
	}

	protected void modify(CCRClusterType ccrClusterType) {
		HashMap<String, Object> properties = HashMapBuilder.<String, Object>put(
			"ccrClusterType", ccrClusterType
		).build();

		ElasticsearchConnection elasticsearchConnection =
			_ccrElasticsearchConnectionManager.getElasticsearchConnection(
				ccrClusterType);

		if (elasticsearchConnection instanceof
				CCRRemoteElasticsearchConnection) {

			CCRRemoteElasticsearchConnection ccrRemoteElasticsearchConnection =
				(CCRRemoteElasticsearchConnection)elasticsearchConnection;

			ccrRemoteElasticsearchConnection.modified(properties);
		}
		else if (elasticsearchConnection instanceof
					CCRLocalElasticsearchConnection) {

			CCRLocalElasticsearchConnection ccrLocalElasticsearchConnection =
				(CCRLocalElasticsearchConnection)elasticsearchConnection;

			ccrLocalElasticsearchConnection.modified(properties);
		}
		else {
			_ccrElasticsearchConnectionManager.validate(ccrClusterType);
		}
	}

	protected void resetMockConnections() {
		Mockito.reset(
			_ccrRemoteElasticsearchConnection,
			_ccrLocalElasticsearchConnection);
	}

	protected void verifyCloseNeverConnect(
		ElasticsearchConnection elasticsearchConnection) {

		Mockito.verify(
			elasticsearchConnection
		).close();

		Mockito.verify(
			elasticsearchConnection, Mockito.never()
		).connect();
	}

	protected void verifyConnectNeverClose(
		ElasticsearchConnection elasticsearchConnection) {

		Mockito.verify(
			elasticsearchConnection, Mockito.never()
		).close();

		Mockito.verify(
			elasticsearchConnection
		).connect();
	}

	protected void verifyNeverCloseNeverConnect(
		ElasticsearchConnection elasticsearchConnection) {

		Mockito.verify(
			elasticsearchConnection, Mockito.never()
		).close();

		Mockito.verify(
			elasticsearchConnection, Mockito.never()
		).connect();
	}

	private CCRElasticsearchConnectionManager
		_ccrElasticsearchConnectionManager;

	@Mock
	private CCRLocalElasticsearchConnection _ccrLocalElasticsearchConnection;

	@Mock
	private CCRRemoteElasticsearchConnection _ccrRemoteElasticsearchConnection;

}