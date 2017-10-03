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

package com.liferay.portal.search.internal.facet.tag;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.internal.facet.FacetImpl;

/**
 * @author André de Oliveira
 * @author Wade Cao
 */
public class AssetTagNamesFacet extends FacetImpl {

	public AssetTagNamesFacet(SearchContext searchContext) {
		super(Field.ASSET_TAG_NAMES, searchContext);
	}

	@Override
	public void select(String... selections) {
		if (selections != null) {
			String[] selectionsLowerCase = new String[selections.length];
			int i = 0;

			for (String selection : selections) {
				selectionsLowerCase[i++] = StringUtil.toLowerCase(selection);
			}

			super.select(selectionsLowerCase);
		}
	}

}