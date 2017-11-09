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

package com.liferay.portal.search.searchengineutil.test;

import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.search.configurator.SearchEngineConfigurator;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Fellwock
 */
@Component(immediate = true, service = SearchEngineConfigurator.class)
public class TestSearchEngineConfigurator implements SearchEngineConfigurator {

	@Override
	public void afterPropertiesSet() {
		_atomicBoolean.set(Boolean.TRUE);
	}

	@Override
	public void destroy() {
		_atomicBoolean.set(Boolean.TRUE);
	}

	@Override
	public void setSearchEngines(Map<String, SearchEngine> searchEngines) {
		_atomicBoolean.set(Boolean.TRUE);
	}

	@Reference(target = "(test=AtomicState)", unbind = "-")
	protected void setAtomicBoolean(AtomicBoolean atomicBoolean) {
		_atomicBoolean = atomicBoolean;
	}

	private AtomicBoolean _atomicBoolean;

}