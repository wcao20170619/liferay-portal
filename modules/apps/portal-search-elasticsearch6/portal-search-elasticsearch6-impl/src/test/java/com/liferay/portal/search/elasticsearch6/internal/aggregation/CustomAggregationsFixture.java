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

package com.liferay.portal.search.elasticsearch6.internal.aggregation;

import com.liferay.portal.search.elasticsearch6.internal.aggregation.pipeline.CustomPipelineAggregationResultTranslator;
import com.liferay.portal.search.elasticsearch6.internal.aggregation.pipeline.CustomPipelineAggregationResultTranslatorImpl;
import com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.search2.SearchSearchResponseAssemblerImpl;
import com.liferay.portal.search.spi.aggregation.CustomAggregationResultTranslatorContributorRegistry;
import com.liferay.portal.search.spi.aggregation.pipeline.CustomPipelineAggregationResultTranslatorContributorRegistry;
import com.liferay.portal.search.test.util.aggregation.TestCustomAggregationResultTranslatorContributorRegistry;
import com.liferay.portal.search.test.util.aggregation.pipeline.TestCustomPipelineAggregationResultTranslatorContributorRegistry;

/**
 * @author André de Oliveira
 */
public class CustomAggregationsFixture {

	public static void injectCustomAggregations(
		SearchSearchResponseAssemblerImpl searchSearchResponseAssembler) {

		CustomAggregationResultTranslator customAggregationResultTranslator =
			createCustomAggregationResultTranslator();

		searchSearchResponseAssembler.setCustomAggregationResultTranslator(
			customAggregationResultTranslator);

		CustomPipelineAggregationResultTranslator
			customPipelineAggregationResultTranslator =
				createCustomPipelineAggregationResultTranslator();

		searchSearchResponseAssembler.
			setCustomPipelineAggregationResultTranslator(
				customPipelineAggregationResultTranslator);
	}

	protected static CustomAggregationResultTranslator
		createCustomAggregationResultTranslator() {

		CustomAggregationResultTranslatorContributorRegistry
			customAggregationResultTranslatorContributorRegistry =
				TestCustomAggregationResultTranslatorContributorRegistry.
					getInstance();

		CustomAggregationResultTranslator customAggregationResultTranslator =
			new CustomAggregationResultTranslatorImpl() {
				{
					setCustomAggregationResultTranslatorContributorRegistry(
						customAggregationResultTranslatorContributorRegistry);
				}
			};

		return customAggregationResultTranslator;
	}

	protected static CustomPipelineAggregationResultTranslator
		createCustomPipelineAggregationResultTranslator() {

		CustomPipelineAggregationResultTranslatorContributorRegistry
			customPipelineAggregationResultTranslatorContributorRegistry;

		customPipelineAggregationResultTranslatorContributorRegistry =
			TestCustomPipelineAggregationResultTranslatorContributorRegistry.
				getInstance();

		CustomPipelineAggregationResultTranslatorImpl
			customPipelineAggregationResultTranslator =
				new CustomPipelineAggregationResultTranslatorImpl();

		customPipelineAggregationResultTranslator.
			setCustomPipelineAggregationResultTranslatorContributorRegistry(
				customPipelineAggregationResultTranslatorContributorRegistry);

		return customPipelineAggregationResultTranslator;
	}

}