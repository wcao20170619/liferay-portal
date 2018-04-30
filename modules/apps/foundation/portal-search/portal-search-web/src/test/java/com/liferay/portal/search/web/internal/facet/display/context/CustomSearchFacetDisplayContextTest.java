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

package com.liferay.portal.search.web.internal.facet.display.context;

import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.web.internal.facet.display.builder.CustomSearchFacetDisplayBuilder;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Wade Cao
 */
public class CustomSearchFacetDisplayContextTest {

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Mockito.doReturn(
			_facetCollector
		).when(
			_facet
		).getFacetCollector();
	}

	@Test
	public void testEmptyFieldLabelSearchResults() throws Exception {
		String fieldLabel = "";
		String fieldToAggregate = "groupId";
		String paramValue = "";

		CustomSearchFacetDisplayContext customSearchFacetDisplayContext =
			createDisplayContext(fieldLabel, fieldToAggregate, paramValue);

		List<CustomSearchFacetTermDisplayContext>
			customSearchFacetTermDisplayContexts =
				customSearchFacetDisplayContext.getTermDisplayContexts();

		Assert.assertEquals(
			customSearchFacetTermDisplayContexts.toString(), 0,
			customSearchFacetTermDisplayContexts.size());

		Assert.assertTrue(customSearchFacetDisplayContext.isNothingSelected());
		Assert.assertTrue(customSearchFacetDisplayContext.isRenderNothing());
		Assert.assertEquals(
			fieldToAggregate, customSearchFacetDisplayContext.getFieldLabel());
	}

	@Test
	public void testEmptySearchResultsWithPreviousSelection() throws Exception {
		String custom = RandomTestUtil.randomString();

		String paramValue = custom;

		CustomSearchFacetDisplayContext customSearchFacetDisplayContext =
			createDisplayContext(
				"fieldLabelName", "fieldToAggregate", paramValue);

		List<CustomSearchFacetTermDisplayContext>
			customSearchFacetTermDisplayContexts =
				customSearchFacetDisplayContext.getTermDisplayContexts();

		Assert.assertEquals(
			customSearchFacetTermDisplayContexts.toString(), 1,
			customSearchFacetTermDisplayContexts.size());

		CustomSearchFacetTermDisplayContext
			customSearchFacetTermDisplayContext =
				customSearchFacetTermDisplayContexts.get(0);

		Assert.assertEquals(
			0, customSearchFacetTermDisplayContext.getFrequency());
		Assert.assertEquals(
			custom, customSearchFacetTermDisplayContext.getFieldName());
		Assert.assertTrue(customSearchFacetTermDisplayContext.isSelected());
		Assert.assertTrue(
			customSearchFacetTermDisplayContext.isFrequencyVisible());

		Assert.assertEquals(
			paramValue, customSearchFacetDisplayContext.getParamValue());
		Assert.assertFalse(customSearchFacetDisplayContext.isNothingSelected());
		Assert.assertFalse(customSearchFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testOneTerm() throws Exception {
		String fieldName = RandomTestUtil.randomString();

		int count = RandomTestUtil.randomInt();

		setUpOneTermCollector(fieldName, count);

		String paramValue = "";

		CustomSearchFacetDisplayContext customSearchFacetDisplayContext =
			createDisplayContext("fieldLabel", "fieldToAggregate", paramValue);

		List<CustomSearchFacetTermDisplayContext>
			customSearchFacetTermDisplayContexts =
				customSearchFacetDisplayContext.getTermDisplayContexts();

		Assert.assertEquals(
			customSearchFacetTermDisplayContexts.toString(), 1,
			customSearchFacetTermDisplayContexts.size());

		CustomSearchFacetTermDisplayContext
			customSearchFacetTermDisplayContext =
				customSearchFacetTermDisplayContexts.get(0);

		Assert.assertEquals(
			count, customSearchFacetTermDisplayContext.getFrequency());
		Assert.assertEquals(
			fieldName, customSearchFacetTermDisplayContext.getFieldName());
		Assert.assertFalse(customSearchFacetTermDisplayContext.isSelected());
		Assert.assertTrue(
			customSearchFacetTermDisplayContext.isFrequencyVisible());

		Assert.assertEquals(
			paramValue, customSearchFacetDisplayContext.getParamValue());
		Assert.assertTrue(customSearchFacetDisplayContext.isNothingSelected());
		Assert.assertFalse(customSearchFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testOneTermWithPreviousSelection() throws Exception {
		String fieldName = RandomTestUtil.randomString();

		int count = RandomTestUtil.randomInt();

		setUpOneTermCollector(fieldName, count);

		String paramValue = fieldName;

		CustomSearchFacetDisplayContext customSearchFacetDisplayContext =
			createDisplayContext("fieldLabel", "fieldToAggregate", paramValue);

		List<CustomSearchFacetTermDisplayContext>
			customSearchFacetTermDisplayContexts =
				customSearchFacetDisplayContext.getTermDisplayContexts();

		Assert.assertEquals(
			customSearchFacetTermDisplayContexts.toString(), 1,
			customSearchFacetTermDisplayContexts.size());

		CustomSearchFacetTermDisplayContext
			customSearchFacetTermDisplayContext =
				customSearchFacetTermDisplayContexts.get(0);

		Assert.assertEquals(
			count, customSearchFacetTermDisplayContext.getFrequency());
		Assert.assertEquals(
			fieldName, customSearchFacetTermDisplayContext.getFieldName());
		Assert.assertTrue(customSearchFacetTermDisplayContext.isSelected());
		Assert.assertTrue(
			customSearchFacetTermDisplayContext.isFrequencyVisible());

		Assert.assertEquals(
			paramValue, customSearchFacetDisplayContext.getParamValue());
		Assert.assertFalse(customSearchFacetDisplayContext.isNothingSelected());
		Assert.assertFalse(customSearchFacetDisplayContext.isRenderNothing());
	}

	protected CustomSearchFacetDisplayContext createDisplayContext(
		String fieldLabel, String fieldToAggregate, String paramValue) {

		CustomSearchFacetDisplayBuilder customSearchFacetDisplayBuilder =
			new CustomSearchFacetDisplayBuilder();

		customSearchFacetDisplayBuilder.setFacet(_facet);
		customSearchFacetDisplayBuilder.setParamName("custom");
		customSearchFacetDisplayBuilder.setParamValue(paramValue);
		customSearchFacetDisplayBuilder.setFrequenciesVisible(true);

		customSearchFacetDisplayBuilder.setFrequencyThreshold(0);
		customSearchFacetDisplayBuilder.setMaxTerms(0);

		customSearchFacetDisplayBuilder.setFieldLabel(fieldLabel);
		customSearchFacetDisplayBuilder.setFieldToAggregate(fieldToAggregate);

		return customSearchFacetDisplayBuilder.build();
	}

	protected TermCollector createTermCollector(String fieldName, int count) {
		TermCollector termCollector = Mockito.mock(TermCollector.class);

		Mockito.doReturn(
			count
		).when(
			termCollector
		).getFrequency();

		Mockito.doReturn(
			fieldName
		).when(
			termCollector
		).getTerm();

		return termCollector;
	}

	protected void setUpOneTermCollector(String fieldName, int count) {
		Mockito.doReturn(
			Collections.singletonList(createTermCollector(fieldName, count))
		).when(
			_facetCollector
		).getTermCollectors();
	}

	@Mock
	private Facet _facet;

	@Mock
	private FacetCollector _facetCollector;

}