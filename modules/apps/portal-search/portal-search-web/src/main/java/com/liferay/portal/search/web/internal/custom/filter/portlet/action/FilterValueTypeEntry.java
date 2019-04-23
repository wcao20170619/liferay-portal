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

/**
 * @author Wade Cao
 */
public class FilterValueTypeEntry {

	public String getName() {
		return _name;
	}
	
	public String getSectionId() {
		return _sectionId;
	}

	public String getTypeId() {
		return _typeId;
	}

	protected void setName(String name) {
		_name = name;
	}
	
	protected void setSectionId(String sectionId) {
		_sectionId = sectionId;
	}

	protected void setTypeId(String typeId) {
		_typeId = typeId;
	}

	private String _name;
	private String _sectionId;
	private String _typeId;

}
