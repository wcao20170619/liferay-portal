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

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;

/**
 * @author André de Oliveira
 */
public class SidecarPathsImpl implements SidecarPaths {

	public SidecarPathsImpl(Props props) {
		_props = props;
	}

	@Override
	public String getLib() {
		return _props.get(PropsKeys.LIFERAY_LIB_GLOBAL_DIR);
	}

	@Override
	public String getWork() {
		return _props.get(PropsKeys.LIFERAY_HOME);
	}

	private final Props _props;

}