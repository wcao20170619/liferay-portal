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

package com.liferay.portal.search.multilanguage.test;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ThemeLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.summary.Summary;
import com.liferay.portal.search.summary.SummaryBuilder;
import com.liferay.portal.search.summary.SummaryBuilderFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.util.test.LayoutTestUtil;

import java.util.List;
import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.portlet.MockPortletResponse;
import org.springframework.mock.web.portlet.MockRenderRequest;

/**
 * @author Wade Cao
 */
public abstract class BaseMultiLanguageSummaryTestCase {

	@Before
	public void setUp() throws Exception {
		WorkflowThreadLocal.setEnabled(false);
		_user = UserTestUtil.addUser();
		_group = GroupTestUtil.addGroup();
	}

	protected void assertHighlight(String title, Document document)
		throws Exception {

		Summary summary = getSummary(document);

		Assert.assertEquals(title, summary.getTitle());
	}

	protected void assertHighlight(
			String title, String content, Document document)
		throws Exception {

		Summary summary = getSummary(document);

		Assert.assertEquals(title, summary.getTitle());
		Assert.assertEquals(content, summary.getContent());
	}

	protected String buildExpectedPlain(int maxContentLength, String expected) {
		if ((maxContentLength <= 0) ||
			(expected.length() <= maxContentLength)) {

			return expected;
		}

		return StringUtil.shorten(expected, maxContentLength);
	}

	protected HttpServletRequest createHttpServletRequest(
		PortletRequest portletRequest) {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST, portletRequest);

		return httpServletRequest;
	}

	protected HttpServletResponse createHttpServletResponse() {
		return new MockHttpServletResponse();
	}

	protected PortletRequest createPortletRequest() throws Exception {
		PortletRequest portletRequest = new MockRenderRequest();

		HttpServletRequest request = createHttpServletRequest(portletRequest);

		HttpServletResponse response = createHttpServletResponse();

		ThemeDisplay themeDisplay = createThemeDisplay(request, response);

		portletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		request.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return portletRequest;
	}

	protected PortletResponse createPortletResponse() {
		return new MockPortletResponse();
	}

	protected ThemeDisplay createThemeDisplay(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.getCompany(getGroup().getCompanyId()));
		themeDisplay.setLayout(LayoutTestUtil.addLayout(getGroup()));

		LayoutSet layoutSet = getGroup().getPublicLayoutSet();

		themeDisplay.setLayoutSet(layoutSet);

		Theme theme = ThemeLocalServiceUtil.getTheme(
			getGroup().getCompanyId(), layoutSet.getThemeId());

		themeDisplay.setLookAndFeel(theme, null);

		themeDisplay.setRealUser(getUser());
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setResponse(httpServletResponse);
		themeDisplay.setTimeZone(TimeZoneUtil.getDefault());
		themeDisplay.setUser(getUser());

		return themeDisplay;
	}

	protected Group getGroup() {
		return _group;
	}

	protected abstract Indexer<?> getIndexer();

	protected SearchContext getSearchContext(
			String searchTerm, Locale locale, Group group)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			group.getGroupId());

		searchContext.setKeywords(searchTerm);

		searchContext.setLocale(locale);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(true);

		queryConfig.setSelectedFieldNames(StringPool.STAR);

		return searchContext;
	}

	protected Document getSingleDocument(
		String searchTerm, List<Document> documents) {

		if (documents.size() == 1) {
			return documents.get(0);
		}

		throw new AssertionError(searchTerm + "->" + documents);
	}

	protected Summary getSummary(Document document)
		throws Exception, SearchException {

		com.liferay.portal.kernel.search.Summary summary =
			getIndexer().getSummary(
				document, null, createPortletRequest(),
				createPortletResponse());

		if (summary != null) {
			summary.setHighlight(true);
			summary.setEscape(false);

			SummaryBuilder summaryBuilder = summaryBuilderFactory.newInstance();

			summaryBuilder.setHighlight(true);

			summaryBuilder.setContent(summary.getContent());
			summaryBuilder.setLocale(summary.getLocale());
			summaryBuilder.setMaxContentLength(summary.getMaxContentLength());
			summaryBuilder.setTitle(summary.getTitle());

			return summaryBuilder.build();
		}

		return null;
	}

	protected SummaryBuilder getSummaryBuilder() {
		return summaryBuilderFactory.newInstance();
	}

	protected User getUser() {
		return _user;
	}

	protected List<Document> search(
		String searchTerm, Locale locale, Group group) {

		try {
			SearchContext searchContext = getSearchContext(
				searchTerm, locale, group);

			Hits hits = getIndexer().search(searchContext);

			return hits.toList();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void testMaxContentLength(
		String content, int maxContentLength, String expected) {

		SummaryBuilder summaryBuilder = summaryBuilderFactory.newInstance();

		summaryBuilder.setContent(content);
		summaryBuilder.setMaxContentLength(maxContentLength);

		Summary summary = summaryBuilder.build();

		expected = buildExpectedPlain(maxContentLength, expected);

		Assert.assertEquals(expected, summary.getContent());
	}

	@Inject
	protected IndexerRegistry indexerRegistry;

	@Inject
	protected SummaryBuilderFactory summaryBuilderFactory;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user;

}