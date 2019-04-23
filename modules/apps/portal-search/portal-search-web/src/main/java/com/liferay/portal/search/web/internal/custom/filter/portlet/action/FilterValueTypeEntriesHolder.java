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

package com.liferay.portal.search.web.internal.custom.filter.portlet.action;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wade Cao
 */
public class FilterValueTypeEntriesHolder {

	public FilterValueTypeEntriesHolder() {
		add("dateRange", "Date Range", "DateValue");
		add("fieldValue", "Field Value", "SingleValue");
		add("rangeValue", "Range Value", "RangeValue");
	}
	
	public List<FilterValueTypeEntry> getFilterValueTypeEntries() {
		return _filterValueTypeEntries;
	}
	
	public String getFilterValueSectionIdByTypeId(
		String typeId) {
		String sectionId = "";
		FilterValueTypeEntry filterValueTypeEntry = 
			_filterValueTypeEntries.stream()
				.filter(
					entry -> typeId.equals(
						entry.getTypeId()))
				.findFirst()
				.orElse(null);
		
		if (filterValueTypeEntry != null) {
			sectionId = filterValueTypeEntry.getSectionId();
		}
		
		return sectionId;
	}
	
	protected void add(String typeId, String name, String sectionId) {
		_filterValueTypeEntries.add(
			new FilterValueTypeEntry() {
				{
					setTypeId(typeId);
					setName(name);
					setSectionId(sectionId);
				}
			});
	}
	
	private final List<FilterValueTypeEntry> _filterValueTypeEntries = new ArrayList<>();

}
