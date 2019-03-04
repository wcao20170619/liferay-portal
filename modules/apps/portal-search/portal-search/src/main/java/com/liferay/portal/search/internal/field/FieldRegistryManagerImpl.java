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

import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.field.FieldRegistryManager;
import com.liferay.portal.search.field.MappingsHolder;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = FieldRegistryManager.class)
public class FieldRegistryManagerImpl implements FieldRegistryManager {

	@Activate
	public void activate() {
		register(LIFERAY_COMPANY_INDEX_NAME);
	}

	@Override
	public FieldRegistry getFieldRegistry() {
		return _fieldRegistryMap.get(LIFERAY_COMPANY_INDEX_NAME);
	}

	@Override
	public FieldRegistry getFieldRegistry(String indexName) {
		return _fieldRegistryMap.get(indexName);
	}

	@Override
	public MappingsHolder getMappingsHolder() {
		FieldRegistry fieldRegistry = _fieldRegistryMap.get(
			LIFERAY_COMPANY_INDEX_NAME);

		return fieldRegistry.getMappingsHolder();
	}

	@Override
	public MappingsHolder getMappingsHolder(String indexName) {
		FieldRegistry fieldRegistry = _fieldRegistryMap.get(indexName);

		if (fieldRegistry != null) {
			return fieldRegistry.getMappingsHolder();
		}

		return getMappingsHolder();
	}

	@Override
	public FieldRegistry register(String indexName) {
		FieldRegistry fieldRegistry = _fieldRegistryMap.get(indexName);

		if (fieldRegistry == null) {
			fieldRegistry = new FieldRegistryImpl() {
				{
					setFieldRegistryContributorsHolder(
						_fieldRegistryContributorsHolder);
					setFieldRegistryListenersHolder(
						_fieldRegistryListenersHolder);
					setMappingsHolder(new MappingsHolderImpl());
				}
			};
		}

		((FieldRegistryImpl)fieldRegistry).register();

		_fieldRegistryMap.put(indexName, fieldRegistry);

		return fieldRegistry;
	}

	@Override
	public FieldRegistry register(
		String indexName, MappingsHolder mappingsHolder) {

		FieldRegistry fieldRegistry = register(indexName);

		if (mappingsHolder != null) {
			FieldRegistryImpl fieldRegistryImpl =
				(FieldRegistryImpl)fieldRegistry;

			fieldRegistryImpl.setMappingsHolder(mappingsHolder);

			fieldRegistryImpl.register();
		}

		return fieldRegistry;
	}

	@Reference(unbind = "-")
	protected void setFieldRegistryContributorsHolder(
		FieldRegistryContributorsHolder fieldRegistryContributorsHolder) {

		_fieldRegistryContributorsHolder = fieldRegistryContributorsHolder;
	}

	@Reference(unbind = "-")
	protected void setFieldRegistryListenersHolder(
		FieldRegistryListenersHolder fieldRegistryListenersHolder) {

		_fieldRegistryListenersHolder = fieldRegistryListenersHolder;
	}

	private FieldRegistryContributorsHolder _fieldRegistryContributorsHolder;
	private FieldRegistryListenersHolder _fieldRegistryListenersHolder;
	private final Map<String, FieldRegistry> _fieldRegistryMap =
		new HashMap<>();

}