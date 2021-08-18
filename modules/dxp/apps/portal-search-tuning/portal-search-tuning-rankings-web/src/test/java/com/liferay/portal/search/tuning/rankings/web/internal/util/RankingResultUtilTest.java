/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.rankings.web.internal.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.tuning.rankings.web.internal.BaseRankingsWebTestCase;
import com.liferay.portal.search.web.interpreter.SearchResultInterpreter;
import com.liferay.portal.search.web.interpreter.SearchResultInterpreterProvider;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class RankingResultUtilTest extends BaseRankingsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		ReflectionTestUtil.setFieldValue(
			_rankingResultUtil, "_documentBuilderFactory",
			_documentBuilderFactory);
		ReflectionTestUtil.setFieldValue(_rankingResultUtil, "_portal", portal);
		ReflectionTestUtil.setFieldValue(
			_rankingResultUtil, "_searchResultInterpreterProvider",
			_searchResultInterpreterProvider);
	}

	@Test
	public void testGetAssetRenderer() {
		_setupDocumentBuilderFactory();

		SearchResultInterpreter searchResultInterpreter = Mockito.mock(
			SearchResultInterpreter.class);

		AssetRenderer<?> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.doReturn(
			assetRenderer
		).when(
			searchResultInterpreter
		).getAssetRenderer(
			Mockito.anyObject()
		);

		Mockito.doReturn(
			searchResultInterpreter
		).when(
			_searchResultInterpreterProvider
		).getSearchResultInterpreter(
			Mockito.anyString()
		);

		Assert.assertEquals(
			assetRenderer,
			RankingResultUtil.getAssetRenderer(
				"entryClassName", Long.valueOf(1111)));
	}

	@Test
	public void testGetRankingResultViewURL() throws Exception {
		Document document = _setupDocument();
		PortletURL portletURL = _setupPortletURL();
		ResourceRequest resourceRequest = _setupResourceRequest("223");
		ResourceResponse resourceResponse = _setupResourceResponse(portletURL);

		SearchResultInterpreter searchResultInterpreter =
			_setupGetRankingResultViewURLMocks();

		Mockito.doReturn(
			String.valueOf("444")
		).when(
			searchResultInterpreter
		).getAssetURLViewInContext(
			Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(),
			Mockito.anyString()
		);

		Assert.assertEquals(
			"httpurl",
			RankingResultUtil.getRankingResultViewURL(
				document, resourceRequest, resourceResponse, true));
	}

	@Test
	public void testGetRankingResultViewURLException() throws Exception {
		Document document = _setupDocument();
		PortletURL portletURL = _setupPortletURL();
		ResourceRequest resourceRequest = _setupResourceRequest("223");
		ResourceResponse resourceResponse = _setupResourceResponse(portletURL);

		SearchResultInterpreter searchResultInterpreter =
			_setupGetRankingResultViewURLMocks();

		Mockito.doThrow(
			Exception.class
		).when(
			searchResultInterpreter
		).getAssetURLViewInContext(
			Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(),
			Mockito.anyString()
		);

		Assert.assertEquals(
			StringPool.BLANK,
			RankingResultUtil.getRankingResultViewURL(
				document, resourceRequest, resourceResponse, true));
	}

	private AssetEntry _setupAssetEntry(AssetEntry assetEntry) {
		if (assetEntry == null)

			return assetEntry;

		Mockito.doReturn(
			111L
		).when(
			assetEntry
		).getEntryId();

		Mockito.doReturn(
			"222"
		).when(
			assetEntry
		).getLayoutUuid();

		return assetEntry;
	}

	private Document _setupDocument() throws Exception {
		Document document = Mockito.mock(Document.class);

		Mockito.doReturn(
			"1111"
		).when(
			document
		).getString(
			Mockito.anyString()
		);

		return document;
	}

	private void _setupDocumentBuilderFactory() {
		DocumentBuilder documentBuilder = Mockito.mock(DocumentBuilder.class);

		Mockito.doReturn(
			documentBuilder
		).when(
			documentBuilder
		).setString(
			Mockito.anyObject(), Mockito.anyObject()
		);

		Mockito.doReturn(
			documentBuilder
		).when(
			documentBuilder
		).setLong(
			Mockito.anyObject(), Mockito.anyObject()
		);

		Mockito.doReturn(
			Mockito.mock(Document.class)
		).when(
			documentBuilder
		).build();

		Mockito.doReturn(
			documentBuilder
		).when(
			_documentBuilderFactory
		).builder();
	}

	private SearchResultInterpreter _setupGetRankingResultViewURLMocks()
		throws Exception {

		_setupPortalGetCurrentURL("myurl");
		_setupHttpUtil();

		setupPortalUtil();

		AssetEntry assetEntry = _setupAssetEntry(
			Mockito.mock(AssetEntry.class));
		SearchResultInterpreter searchResultInterpreter = Mockito.mock(
			SearchResultInterpreter.class);

		Mockito.doReturn(
			assetEntry
		).when(
			searchResultInterpreter
		).getAssetEntry(
			Mockito.anyObject()
		);

		Mockito.doThrow(
			Exception.class
		).when(
			searchResultInterpreter
		).getAssetURLViewInContext(
			Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(),
			Mockito.anyString()
		);

		Mockito.doReturn(
			searchResultInterpreter
		).when(
			_searchResultInterpreterProvider
		).getSearchResultInterpreter(
			Mockito.anyString()
		);

		return searchResultInterpreter;
	}

	private void _setupHttpUtil() {
		Http http = Mockito.mock(Http.class);

		Mockito.doReturn(
			"httpurl"
		).when(
			http
		).setParameter(
			Matchers.anyString(), Matchers.anyString(), Matchers.anyString()
		);

		HttpUtil httpUtil = new HttpUtil();

		httpUtil.setHttp(http);
	}

	private void _setupPortalGetCurrentURL(String currentURL) {
		Mockito.doReturn(
			currentURL
		).when(
			portal
		).getCurrentURL(
			Mockito.any(PortletRequest.class)
		);
	}

	@SuppressWarnings("deprecation")
	private PortletURL _setupPortletURL() throws Exception {
		PortletURL portletURL = Mockito.mock(PortletURL.class);

		Mockito.doNothing(
		).when(
			portletURL
		).setParameter(
			Mockito.anyString(), Mockito.anyString()
		);

		Mockito.doNothing(
		).when(
			portletURL
		).setPortletMode(
			Mockito.anyObject()
		);

		Mockito.doNothing(
		).when(
			portletURL
		).setWindowState(
			Mockito.anyObject()
		);

		return portletURL;
	}

	private ResourceRequest _setupResourceRequest(String uuid) {
		Layout layout = Mockito.mock(Layout.class);

		Mockito.doReturn(
			uuid
		).when(
			layout
		).getUuid();

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.doReturn(
			layout
		).when(
			themeDisplay
		).getLayout();

		ResourceRequest resourceRequest = Mockito.mock(ResourceRequest.class);

		Mockito.doReturn(
			themeDisplay
		).when(
			resourceRequest
		).getAttribute(
			Mockito.anyString()
		);

		return resourceRequest;
	}

	private ResourceResponse _setupResourceResponse(PortletURL portletURL) {
		ResourceResponse resourceResponse = Mockito.mock(
			ResourceResponse.class);

		Mockito.doReturn(
			portletURL
		).when(
			resourceResponse
		).createRenderURL();

		return resourceResponse;
	}

	@Mock
	private DocumentBuilderFactory _documentBuilderFactory;

	private final RankingResultUtil _rankingResultUtil =
		new RankingResultUtil();

	@Mock
	private SearchResultInterpreterProvider _searchResultInterpreterProvider;

}