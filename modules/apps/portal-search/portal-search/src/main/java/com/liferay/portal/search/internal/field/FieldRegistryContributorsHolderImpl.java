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

import com.liferay.portal.search.spi.field.contributor.FieldRegistryContributor;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author André de Oliveira
 */
@Component(immediate = true, service = FieldRegistryContributorsHolder.class)
public class FieldRegistryContributorsHolderImpl
	implements FieldRegistryContributorsHolder {

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	public void addFieldRegistryContributor(
		FieldRegistryContributor fieldRegistryContributor) {

		_fieldRegistryContributors.add(fieldRegistryContributor);
	}

	@Override
	public Stream<FieldRegistryContributor> getAll() {
		return _fieldRegistryContributors.stream();
	}

	protected void removeFieldRegistryContributor(
		FieldRegistryContributor fieldRegistryContributor) {

		_fieldRegistryContributors.remove(fieldRegistryContributor);
	}

	private final Collection<FieldRegistryContributor>
		_fieldRegistryContributors = new CopyOnWriteArrayList<>();

}