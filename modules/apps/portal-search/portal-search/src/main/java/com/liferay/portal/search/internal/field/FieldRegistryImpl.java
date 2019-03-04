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

/**
 * @author Wade Cao
 */
public class FieldRegistryImpl implements FieldRegistry {

	public FieldRegistryImpl() {
	}

	@Override
	public Optional<Mapping> getMappingOptional(String fieldName) {
		return _mappingsHolder.getMappingOptional(fieldName);
	}

	@Override
	public Map<String, Mapping> getMappings() {
		return _mappingsHolder.getMappings();
	}

	public MappingsHolder getMappingsHolder() {
		return _mappingsHolder;
	}

	@Override
	public boolean isSortableTextField(String name) {
		return _sortableTextFields.contains(name);
	}

	public void register() {
		registerContributedMappings();
		registerSortableFields();
	}

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

		_mappingsHolder.putMapping(fieldName, mapping);

		Stream<FieldRegistryListener> stream =
			_fieldRegistryListenersHolder.getAll();

		stream.forEach(
			fieldRegistryListener ->
				fieldRegistryListener.onFieldRegistered(
					null, fieldName, mapping));
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
		Optional<Mapping> optional = _mappingsHolder.getMappingOptional(
			fieldName);

		if (optional.isPresent()) {
			throw new IllegalArgumentException(
				"Mapping already exists: " + fieldName);
		}
	}

	protected void registerContributedMappings() {
		Stream<FieldRegistryContributor> stream =
			_fieldRegistryContributorsHolder.getAll();

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

	protected void setFieldRegistryContributorsHolder(
		FieldRegistryContributorsHolder fieldRegistryContributorsHolder) {

		_fieldRegistryContributorsHolder = fieldRegistryContributorsHolder;
	}

	protected void setFieldRegistryListenersHolder(
		FieldRegistryListenersHolder fieldRegistryListenersHolder) {

		_fieldRegistryListenersHolder = fieldRegistryListenersHolder;
	}

	protected void setMappingsHolder(MappingsHolder mappingsHolder) {
		_mappingsHolder = mappingsHolder;
	}

	private FieldRegistryContributorsHolder _fieldRegistryContributorsHolder;
	private FieldRegistryListenersHolder _fieldRegistryListenersHolder;
	private MappingsHolder _mappingsHolder;
	private final Set<String> _sortableTextFields = new HashSet<>();

}