package com.liferay.portal.search.elasticsearch7.internal.connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.liferay.portal.search.elasticsearch7.internal.ElasticsearchSearchEngineReconnectTest;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnection;

public class LogMessageForPetraLibMissingTest {
	
	@Before
	public void setUp() {
		ElasticsearchConnectionFixture elasticsearchConnectionFixture =
			ElasticsearchConnectionFixture.builder(
			).clusterName(
				ElasticsearchSearchEngineReconnectTest.class.getSimpleName()
			).sidecarReplacesEmbedded(
				true
			).build();
		
		elasticsearchConnectionFixture.createNode();
		
		_elasticsearchConnectionFixture = elasticsearchConnectionFixture;
	}

	@After
	public void tearDown() {
		_elasticsearchConnectionFixture.destroyNode();
	}

	@Test
	public void testLogMessageForPetraLib() {
		

		ElasticsearchConnection elasticsearchConnection =
			_elasticsearchConnectionFixture.createElasticsearchConnection();

		elasticsearchConnection.close();

	}

	private static ElasticsearchConnectionFixture
		_elasticsearchConnectionFixture;	
}