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

/**
 * @author Wade Cao
 */
package com.liferay.portal.search.test.util.facet;

import java.util.ArrayList;

import org.mockito.Mockito;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.RangeFacet;
import com.liferay.portal.search.test.util.indexing.QueryContributors;

public abstract class BaseRangeFacetTestCase extends BaseFacetTestCase {

	@Override
	protected Facet createFacet(SearchContext searchContext) {
		Facet facet = new RangeFacet(searchContext);

		facet.setFieldName(Field.MODIFIED_DATE);

		return facet;
	}
	
	@Override
	protected String getField() {
		return Field.MODIFIED_DATE;
	}
	
	protected void testModifiedStatic() throws Exception {
		addDocument("200001011200");
		addDocument("201101011200");

		assertFacet(
			setUpRangValue("200001011200", "201101011200", setUpRangesValue()),

			new ArrayList<String>() {
				{
					add("200001011200 TO 201101011200=5"); 
				}
			});
	}

	protected JSONObject setUpRangesValue() {
		JSONObject jsonObject = Mockito.mock(JSONObject.class);
		
		JSONArray jsonArray = Mockito.mock(JSONArray.class);
		Mockito.doReturn(
			1
		).when(
			jsonArray
		).length();
		
		Mockito.doReturn(
			jsonArray
		).when(
			jsonObject
		).getJSONArray(
			"ranges"
		);
		
		Mockito.doReturn(
			true
		).when(
			jsonObject
		).has("ranges");

		return jsonObject;
	}

	protected JSONObject setUpRangValue(
			String start, String end, JSONObject jsonObject) {

		JSONArray rangesJSONArray = jsonObject.getJSONArray("ranges");
		
		JSONObject rangeJSONObject = Mockito.mock(JSONObject.class);
		Mockito.doReturn(
			start + " TO " + end
		).when(
			rangeJSONObject
		).getString(
			"range"
		);
		
		Mockito.doReturn(
			rangeJSONObject
		).when(
			rangesJSONArray
		).getJSONObject(0);

		return jsonObject;
	}
}
