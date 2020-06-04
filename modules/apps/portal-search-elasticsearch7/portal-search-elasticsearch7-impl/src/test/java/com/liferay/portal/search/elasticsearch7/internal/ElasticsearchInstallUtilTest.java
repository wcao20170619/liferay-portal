package com.liferay.portal.search.elasticsearch7.internal;

import com.liferay.portal.search.elasticsearch7.internal.sidecar.ElasticsearchInstallUtil;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ElasticsearchInstallUtilTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Test
	public void testElasticsearchInstallUtil() throws Exception {
		ElasticsearchInstallUtil.downloadAndInstall(
			"/home/cao/liferay/servers/liferay-portal-0/elasticsearch7cao");
	}

}