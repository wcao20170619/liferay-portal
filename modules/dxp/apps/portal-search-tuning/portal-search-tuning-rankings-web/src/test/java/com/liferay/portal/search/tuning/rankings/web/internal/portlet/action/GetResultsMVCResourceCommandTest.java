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

package com.liferay.portal.search.tuning.rankings.web.internal.portlet.action;

import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.tuning.rankings.web.internal.searcher.RankingSearchRequestHelper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class GetResultsMVCResourceCommandTest
	extends BaseRankingsPortletActionTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_getResultsMVCResourceCommand = new GetResultsMVCResourceCommand();

		ReflectionTestUtil.setFieldValue(
			_getResultsMVCResourceCommand, "complexQueryPartBuilderFactory",
			complexQueryPartBuilderFactory);
		ReflectionTestUtil.setFieldValue(
			_getResultsMVCResourceCommand, "dlAppLocalService",
			dlAppLocalService);
		ReflectionTestUtil.setFieldValue(
			_getResultsMVCResourceCommand, "fastDateFormatFactory",
			fastDateFormatFactory);
		ReflectionTestUtil.setFieldValue(
			_getResultsMVCResourceCommand, "portal", portal);
		ReflectionTestUtil.setFieldValue(
			_getResultsMVCResourceCommand, "queries", queries);
		ReflectionTestUtil.setFieldValue(
			_getResultsMVCResourceCommand, "rankingIndexNameBuilder",
			rankingIndexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_getResultsMVCResourceCommand, "rankingIndexReader",
			rankingIndexReader);
		ReflectionTestUtil.setFieldValue(
			_getResultsMVCResourceCommand, "rankingSearchRequestHelper",
			_rankingSearchRequestHelper);
		ReflectionTestUtil.setFieldValue(
			_getResultsMVCResourceCommand, "resourceActions", _resourceActions);
		ReflectionTestUtil.setFieldValue(
			_getResultsMVCResourceCommand, "searchEngineAdapter",
			searchEngineAdapter);
		ReflectionTestUtil.setFieldValue(
			_getResultsMVCResourceCommand, "searcher", searcher);
		ReflectionTestUtil.setFieldValue(
			_getResultsMVCResourceCommand, "searchRequestBuilderFactory",
			searchRequestBuilderFactory);
	}

	@Test
	public void testServeResourceGetHiddenResultsJSONObject() throws Exception {
		setupDLAppLocalService();
		setupFastDateFormatFactory();
		setupPortletRequestParamValue(
			resourceRequest, "getHiddenResultsJSONObject", Constants.CMD);
		setupRankingIndexReader();
		setupRankingResultUtil();
		setupResourceRequest();
		setupResourceResponse();

		setupSearchEngineAdapter(
			setupGetDocumentResponseGetDocument(
				setupDocumentWithGetString(), setupGetDocumentResponse()));

		_getResultsMVCResourceCommand.serveResource(
			resourceRequest, resourceResponse);

		Mockito.verify(
			resourceResponse, Mockito.times(1)
		).isCommitted();
	}

	@Test
	public void testServeResourceGetSearchResultsJSONObject() throws Exception {
		_setupServeResources();

		setupPortletRequestParamValue(
			resourceRequest, "getSearchResultsJSONObject", Constants.CMD);

		_getResultsMVCResourceCommand.serveResource(
			resourceRequest, resourceResponse);

		Mockito.verify(
			resourceResponse, Mockito.times(1)
		).isCommitted();
	}

	@Test
	public void testServeResourceGetVisibleResultsJSONObject()
		throws Exception {

		setupPortletRequestParamValue(
			resourceRequest, "getVisibleResultsJSONObject", Constants.CMD);

		_setupServeResources();

		_getResultsMVCResourceCommand.serveResource(
			resourceRequest, resourceResponse);

		Mockito.verify(
			resourceResponse, Mockito.times(1)
		).isCommitted();
	}

	private void _setupServeResources() throws Exception {
		setupDLAppLocalService();
		setupComplexQueryPartBuilderFactory(setupComplexQueryPartBuilder());
		setupFastDateFormatFactory();
		setupQuery();
		setupRankingIndexReader();
		setupRankingResultUtil();
		setupResourceRequest();
		setupResourceResponse();
		setupSearcher(setupSearchResponse(setupDocumentWithGetString()));

		SearchRequestBuilder searchRequestBuilder = setupSearchRequestBuilder();

		setupSearchRequestBuilderFactory(searchRequestBuilder);
	}

	private GetResultsMVCResourceCommand _getResultsMVCResourceCommand;

	@Mock
	private RankingSearchRequestHelper _rankingSearchRequestHelper;

	@Mock
	private ResourceActions _resourceActions;

}