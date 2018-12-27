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

import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.field.FieldRegistryListener;
import com.liferay.portal.search.field.Mapping;
import com.liferay.portal.search.field.MappingBuilder;
import com.liferay.portal.search.field.MappingsHolder;
import com.liferay.portal.search.spi.field.contributor.FieldRegistryContributor;
import com.liferay.portal.search.spi.field.contributor.helper.FieldRegistryContributorHelper;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = FieldRegistry.class)
public class FieldRegistryImpl implements FieldRegistry {

	@Activate
	public void activate() {
		registerContributedMappings();

		registerSortableFields();								// TODO ???????????????
	}

	@Override
	public Optional<Mapping> getMappingOptional(String fieldName) {
		return mappingsHolder.getMappingOptional(fieldName);
	}

	@Override
	public Map<String, Mapping> getMappings() {
		return mappingsHolder.getMappings();
	}

	@Override
	public boolean isSortableTextField(String name) {
		return _sortableTextFields.contains(name);
	}

	/*public boolean isTheFieldType(String name, String dataType) {
		Optional<Mapping> optional = mappingsHolder.getMappingOptional(name);

		return optional.map(
			mapping -> Objects.equals(dataType, mapping.getType())
		).orElse(
			false
		);

		//return mappedFieldType.isFieldTypeMatched(dataType);
	}

	*/

	@Override
	public void register(String name, Mapping mapping) {
		doRegister(name, mapping);
	}

	@Override
	public void registerSortableTextField(String name) {
		_sortableTextFields.add(name);
	}

	protected MappingBuilderImpl createMappingBuilder() {
		return new MappingBuilderImpl();
	}

	protected void doRegister(String fieldName, Mapping mapping) {
		guardCollision(fieldName);

		mappingsHolder.putMapping(fieldName, mapping);

		Stream<FieldRegistryListener> stream =
			fieldRegistryListenersHolder.getAll();

		stream.forEach(
			fieldRegistryListener -> fieldRegistryListener.onFieldRegistered(
				null, fieldName, mapping));								// TODO indexName
	}

	protected void doRegister(String fieldName, String type) {
		MappingBuilder mappingBuilder = createMappingBuilder();

		doRegister(
			fieldName,
			mappingBuilder.type(
				type
			).build());
	}

	protected void guardCollision(String fieldName) {
		Optional<Mapping> optional = mappingsHolder.getMappingOptional(
			fieldName);

		if (optional.isPresent()) {
			throw new IllegalArgumentException(
				"Mapping already exists: " + fieldName);
		}
	}

	protected void registerContributedMappings() {
		Stream<FieldRegistryContributor> stream =
			fieldRegistryContributorsHolder.getAll();

		stream.forEach(
			fieldRegistryContributor -> fieldRegistryContributor.contribute(
				new FieldRegistryContributorHelper() {

					@Override
					public void register(String name, Mapping mapping) {
						doRegister(name, mapping);
					}

					@Override
					public void register(String name, String type) {
						doRegister(name, type);
					}

				}));
	}

	protected void registerSortableFields() {
		Set<String> defaultSortableTextFields = SetUtil.fromArray(
			PropsUtil.getArray(PropsKeys.INDEX_SORTABLE_TEXT_FIELDS));

		defaultSortableTextFields.forEach(
			defaultSortableTextField -> registerSortableTextField(
				defaultSortableTextField));
	}

	@Reference
	protected FieldRegistryContributorsHolder fieldRegistryContributorsHolder;

	@Reference
	protected FieldRegistryListenersHolder fieldRegistryListenersHolder;

	@Reference
	protected MappingsHolder mappingsHolder;

	private final Set<String> _sortableTextFields = new HashSet<>();

}