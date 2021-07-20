/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.search.experiences.blueprints.engine.internal.clause;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.search.experiences.blueprints.engine.spi.clause.ClauseTranslator;

import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = ClauseTranslatorFactory.class)
public class ClauseTranslatorFactoryImpl implements ClauseTranslatorFactory {

	@Override
	public ClauseTranslator getTranslator(String name)
		throws IllegalArgumentException {

		ClauseTranslator clauseTranslator =
			_clauseTranslatorServiceTrackerMap.getService(name);

		if (clauseTranslator == null) {
			throw new IllegalArgumentException(
				"No registered clause translator " + name);
		}

		return clauseTranslator;
	}

	@Override
	public String[] getTranslatorNames() {
		Set<String> keySet = _clauseTranslatorServiceTrackerMap.keySet();

		return keySet.toArray(new String[0]);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_clauseTranslatorServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, ClauseTranslator.class, "name");
	}

	@Deactivate
	protected void deactivate() {
		_clauseTranslatorServiceTrackerMap.close();
	}

	private ServiceTrackerMap<String, ClauseTranslator>
		_clauseTranslatorServiceTrackerMap;

}