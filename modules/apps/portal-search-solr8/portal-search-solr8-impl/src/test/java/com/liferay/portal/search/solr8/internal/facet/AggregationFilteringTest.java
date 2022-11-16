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

package com.liferay.portal.search.solr8.internal.facet;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.solr8.internal.SolrIndexingFixture;
import com.liferay.portal.search.test.util.facet.BaseAggregationFilteringTestCase;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.apache.solr.client.solrj.SolrQuery;

import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author André de Oliveira
 */
public class AggregationFilteringTest extends BaseAggregationFilteringTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	protected void addFacetProcessor(
		String className, FacetProcessor<SolrQuery> facetProcessor,
		CompositeFacetProcessor compositeFacetProcessor) {

		compositeFacetProcessor.setFacetProcessor(
			facetProcessor, Collections.singletonMap("class.name", className));
	}

	protected FacetProcessor<SolrQuery> createFacetProcessor() {
		CompositeFacetProcessor compositeFacetProcessor =
			new CompositeFacetProcessor();

		DefaultFacetProcessor defaultFacetProcessor =
			new DefaultFacetProcessor();

		ReflectionTestUtil.setFieldValue(
			defaultFacetProcessor, "_jsonFactory", _jsonFactory);

		ReflectionTestUtil.setFieldValue(
			compositeFacetProcessor, "_defaultFacetProcessor",
			defaultFacetProcessor);

		addFacetProcessor(
			"com.liferay.portal.search.internal.facet.ModifiedFacetImpl",
			new ModifiedFacetProcessor() {
				{
					jsonFactory = _jsonFactory;
				}
			},
			compositeFacetProcessor);

		return compositeFacetProcessor;
	}

	@Override
	protected IndexingFixture createIndexingFixture() throws Exception {
		SolrIndexingFixture solrIndexingFixture = new SolrIndexingFixture();

		solrIndexingFixture.setFacetProcessor(createFacetProcessor());

		return solrIndexingFixture;
	}

	private final JSONFactory _jsonFactory = new JSONFactoryImpl();

}