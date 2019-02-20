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

package com.liferay.portal.search.test.util.aggregation.bucket;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.FiltersAggregation;
import com.liferay.portal.search.aggregation.bucket.FiltersAggregationResult;
import com.liferay.portal.search.aggregation.metrics.SumAggregation;
import com.liferay.portal.search.aggregation.metrics.SumAggregationResult;
import com.liferay.portal.search.internal.aggregation.bucket.FiltersAggregationImpl;
import com.liferay.portal.search.internal.aggregation.metrics.SumAggregationImpl;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.test.util.BaseQueryTestCase;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael C. Han
 */
public abstract class BaseFiltersAggregationTestCase extends BaseQueryTestCase {

	@Test
	public void testFilter() throws Exception {
		addDocument(
			document -> {
				document.addKeyword(Field.USER_NAME, "SomeUser1");
				document.addNumber(Field.PRIORITY, 1);
			});
		addDocument(
			document -> {
				document.addKeyword(Field.USER_NAME, "SomeUser1");
				document.addNumber(Field.PRIORITY, 2);
			});
		addDocument(
			document -> {
				document.addKeyword(Field.USER_NAME, "SomeUser1");
				document.addNumber(Field.PRIORITY, 3);
			});
		addDocument(
			document -> {
				document.addKeyword(Field.USER_NAME, "SomeUser2");
				document.addNumber(Field.PRIORITY, 4);
			});
		addDocument(
			document -> {
				document.addKeyword(Field.USER_NAME, "SomeUser2");
				document.addNumber(Field.PRIORITY, 5);
			});
		addDocument(
			document -> {
				document.addKeyword(Field.USER_NAME, "SomeUser2");
				document.addNumber(Field.PRIORITY, 6);
			});
		addDocument(
			document -> {
				document.addKeyword(Field.USER_NAME, "SomeUser2");
				document.addNumber(Field.PRIORITY, 7);
			});

		FiltersAggregation filtersAggregation = new FiltersAggregationImpl(
			"filter", Field.USER_NAME);

		Query someUser1Query = queries.termQuery(Field.USER_NAME, "SomeUser1");

		filtersAggregation.addKeyedQuery("SomeUser1", someUser1Query);

		Query someUser2Query = queries.termQuery(Field.USER_NAME, "SomeUser2");

		filtersAggregation.addKeyedQuery("SomeUser2", someUser2Query);

		SumAggregation sumAggregation = new SumAggregationImpl(
			"sum", Field.PRIORITY);

		filtersAggregation.addChildAggregation(sumAggregation);

		assertSearch(
			indexingTestHelper -> {
				indexingTestHelper.defineRequest(
					searchRequestBuilder -> searchRequestBuilder.addAggregation(
						filtersAggregation));

				indexingTestHelper.search();

				FiltersAggregationResult filtersAggregationResult =
					indexingTestHelper.getAggregationResult(filtersAggregation);

				Assert.assertEquals(
					"Expected number of buckets", 2,
					filtersAggregationResult.getBuckets().size());

				Bucket someUser1Bucket = filtersAggregationResult.getBucket(
					"SomeUser1");

				Assert.assertEquals(
					"SomeUser1 bucket size", 3, someUser1Bucket.getDocCount());

				Map<String, AggregationResult>
					sumUser1ChildrenAggregationResults =
						someUser1Bucket.getChildrenAggregationResults();

				Assert.assertEquals(
					"Number of children aggregation results", 1,
					sumUser1ChildrenAggregationResults.size());

				SumAggregationResult sumAggregationResult =
					(SumAggregationResult)
						sumUser1ChildrenAggregationResults.get("sum");

				Assert.assertEquals(
					"SumUser1 total priorities", 6,
					sumAggregationResult.getValue(), 0);

				Bucket someUser2Bucket = filtersAggregationResult.getBucket(
					"SomeUser2");

				Assert.assertEquals(
					"SomeUser2 bucket size", 4, someUser2Bucket.getDocCount());

				Map<String, AggregationResult>
					sumUser2ChildrenAggregationResults =
						someUser2Bucket.getChildrenAggregationResults();

				Assert.assertEquals(
					"Number of children aggregation results", 1,
					sumUser2ChildrenAggregationResults.size());

				SumAggregationResult sumAggregationResult2 =
					(SumAggregationResult)
						sumUser2ChildrenAggregationResults.get("sum");

				Assert.assertEquals(
					"SumUser1 total priorities", 22,
					sumAggregationResult2.getValue(), 0);
			});
	}

}