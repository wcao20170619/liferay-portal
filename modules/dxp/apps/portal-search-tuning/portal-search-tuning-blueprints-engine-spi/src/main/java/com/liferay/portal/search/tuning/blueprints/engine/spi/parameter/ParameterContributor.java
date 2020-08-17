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

package com.liferay.portal.search.tuning.blueprints.engine.spi.parameter;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.ParameterDefinition;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.SearchParameterData;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Petteri Karttunen
 */
public interface ParameterContributor {

	public void contribute(
		HttpServletRequest httpServletRequest,
		SearchParameterData searchParameterData);

	public void contribute(
		SearchContext searchContext, SearchParameterData searchParameterData);

	public List<ParameterDefinition> getParameterDefinitions();

}