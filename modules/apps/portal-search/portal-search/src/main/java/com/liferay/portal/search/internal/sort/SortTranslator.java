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

package com.liferay.portal.search.internal.sort;

import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = SortTranslator.class)
public class SortTranslator {

	public Sort translate(com.liferay.portal.search.sort.Sort sort) {
		String field = sort.getField();

		if (Validator.isBlank(field)) {
			return new Sort(null, Sort.SCORE_TYPE, sort.isReverse());
		}

		return new Sort(field, Sort.CUSTOM_TYPE, sort.isReverse());
	}

}