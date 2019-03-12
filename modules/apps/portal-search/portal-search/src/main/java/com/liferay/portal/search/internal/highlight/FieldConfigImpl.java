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

package com.liferay.portal.search.internal.highlight;

import com.liferay.portal.search.highlight.FieldConfig;

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
public class FieldConfigImpl implements FieldConfig {

	public FieldConfigImpl() {
	}

	public FieldConfigImpl(FieldConfigImpl fieldConfigImpl) {
		_field = fieldConfigImpl._field;
		_fragmentOffset = fieldConfigImpl._fragmentOffset;
		_fragmentSize = fieldConfigImpl._fragmentSize;
		_numFragments = fieldConfigImpl._numFragments;
	}

	@Override
	public String getField() {
		return _field;
	}

	@Override
	public Integer getFragmentOffset() {
		return _fragmentOffset;
	}

	@Override
	public Integer getFragmentSize() {
		return _fragmentSize;
	}

	@Override
	public Integer getNumFragments() {
		return _numFragments;
	}

	protected void setField(String field) {
		_field = field;
	}

	protected void setFragmentOffset(Integer fragmentOffset) {
		_fragmentOffset = fragmentOffset;
	}

	protected void setFragmentSize(Integer fragmentSize) {
		_fragmentSize = fragmentSize;
	}

	protected void setNumFragments(Integer numFragments) {
		_numFragments = numFragments;
	}

	private String _field;
	private Integer _fragmentOffset;
	private Integer _fragmentSize;
	private Integer _numFragments;

}