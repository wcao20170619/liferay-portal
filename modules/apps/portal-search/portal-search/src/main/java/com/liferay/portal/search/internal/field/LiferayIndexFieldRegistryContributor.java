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

package com.liferay.portal.search.internal.field;

import com.liferay.portal.search.field.Mapping;
import com.liferay.portal.search.field.MappingBuilder;
import com.liferay.portal.search.spi.field.contributor.FieldRegistryContributor;
import com.liferay.portal.search.spi.field.contributor.helper.FieldRegistryContributorHelper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
@Component(immediate = true, service = FieldRegistryContributor.class)
public class LiferayIndexFieldRegistryContributor
	implements FieldRegistryContributor {

	@Override
	public void contribute(
		FieldRegistryContributorHelper fieldRegistryContributorHelper) {

		// TODO Liferay indexes only

		new Contribute(fieldRegistryContributorHelper) {
			{
				register("articleId", withAnalyzer("keyword_lowercase"));
				register("assetTagNames", withTermVector());
				register("content", withTermVector());
				register("ddmContent", withTermVector());
				register("description", withTermVector());
				register("geoLocation", "geo_point");

				// TODO Contributors per module?

				register("screenName", "keyword");

				register("subtitle", "text");
				register("title", withTermVector());

				if (false) {
					register("assetCategoryTitle", "text");
					register("assetCategoryTitles", "text");
					register("assetCount", "text");
					register("calendarId", "text");
					register("categoryId", "text");
					register("classNameId", "text");
					register("classPK", "text");
					register("country", "text");
				}
			}
		};
	}

	private static class Contribute {

		protected static Mapping createMappingTextWithTermVector() {
			MappingBuilder mappingBuilder = new MappingBuilderImpl();

			return mappingBuilder.store(
				true
			).termVector(
				"with_positions_offsets"
			).type(
				"text"
			).build();
		}

		protected void register(String name, Mapping mapping) {
			_fieldRegistryContributorHelper.register(name, mapping);
		}

		protected void register(String name, String type) {
			MappingBuilder mappingBuilder = new MappingBuilderImpl();

			_fieldRegistryContributorHelper.register(
				name,
				mappingBuilder.store(
					true
				).type(
					type
				).build());
		}

		protected Mapping withAnalyzer(String analyzer) {
			MappingBuilder mappingBuilder = new MappingBuilderImpl();

			return mappingBuilder.analyzer(
				analyzer
			).store(
				true
			).type(
				"text"
			).build();
		}

		protected Mapping withTermVector() {
			return TEXT_WITH_TERM_VECTOR;
		}

		protected static final Mapping TEXT_WITH_TERM_VECTOR =
			createMappingTextWithTermVector();

		private Contribute(
			FieldRegistryContributorHelper fieldRegistryContributorHelper) {

			_fieldRegistryContributorHelper = fieldRegistryContributorHelper;
		}

		private final FieldRegistryContributorHelper
			_fieldRegistryContributorHelper;

	}

}