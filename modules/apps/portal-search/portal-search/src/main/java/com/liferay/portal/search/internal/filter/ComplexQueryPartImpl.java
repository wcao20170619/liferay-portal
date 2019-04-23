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

package com.liferay.portal.search.internal.filter;

import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.filter.ComplexQueryPartBuilder;

/**
 * @author André de Oliveira
 */
public class ComplexQueryPartImpl implements ComplexQueryPart {

	public ComplexQueryPartImpl() {
	}

	public ComplexQueryPartImpl(ComplexQueryPartImpl complexQueryPartImpl) {
		_boost = complexQueryPartImpl._boost;
		_disabled = complexQueryPartImpl._disabled;
		_field = complexQueryPartImpl._field;
		_name = complexQueryPartImpl._name;
		_occur = complexQueryPartImpl._occur;
		_parent = complexQueryPartImpl._parent;
		_type = complexQueryPartImpl._type;
		_value = complexQueryPartImpl._value;
	}

	@Override
	public Float getBoost() {
		return _boost;
	}

	@Override
	public String getField() {
		return _field;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getOccur() {
		return _occur;
	}

	@Override
	public String getParent() {
		return _parent;
	}

	@Override
	public String getType() {
		return _type;
	}

	@Override
	public String getValue() {
		return _value;
	}

	@Override
	public boolean isDisabled() {
		return _disabled;
	}
	
	@Override
	public boolean isRange() {
		return _range;
	}
	
	@Override
	public String getLowerBound() {
		return _lowerBound;
	}

	@Override
	public String getUpperBound() {
		return _upperBound;
	}

	@Override
	public boolean isIncludesLower() {
		return _includesLower;
	}

	@Override
	public boolean isIncludesUpper() {
		return _includesUpper;
	}


	public static class Builder implements ComplexQueryPartBuilder {

		@Override
		public ComplexQueryPartBuilder boost(Float boost) {
			_complexQueryPartImpl._boost = boost;

			return this;
		}

		@Override
		public ComplexQueryPart build() {
			return new ComplexQueryPartImpl(_complexQueryPartImpl);
		}

		@Override
		public ComplexQueryPartBuilder disabled(boolean disabled) {
			_complexQueryPartImpl._disabled = disabled;

			return this;
		}

		@Override
		public Builder field(String field) {
			_complexQueryPartImpl._field = field;

			return this;
		}

		@Override
		public ComplexQueryPartBuilder name(String name) {
			_complexQueryPartImpl._name = name;

			return this;
		}

		@Override
		public ComplexQueryPartBuilder occur(String occur) {
			_complexQueryPartImpl._occur = occur;

			return this;
		}

		@Override
		public ComplexQueryPartBuilder parent(String parent) {
			_complexQueryPartImpl._parent = parent;

			return this;
		}
		
		@Override
		public ComplexQueryPartBuilder range(boolean range) {
			_complexQueryPartImpl._range = range;

			return this;
		}

		@Override
		public Builder type(String type) {
			_complexQueryPartImpl._type = type;

			return this;
		}

		@Override
		public Builder value(String value) {
			_complexQueryPartImpl._value = value;

			return this;
		}

		private final ComplexQueryPartImpl _complexQueryPartImpl =
			new ComplexQueryPartImpl();

		@Override
		public ComplexQueryPartBuilder includesLower(boolean includesLower) {
			_complexQueryPartImpl._includesLower = includesLower;
			
			return this;
		}

		@Override
		public ComplexQueryPartBuilder includesUpper(boolean includesUpper) {
			_complexQueryPartImpl._includesUpper = includesUpper;
			
			return this;
		}

		@Override
		public ComplexQueryPartBuilder lowerBound(String lowerBound) {
			_complexQueryPartImpl._lowerBound = lowerBound;
			
			return this;
		}

		@Override
		public ComplexQueryPartBuilder upperBound(String upperBound) {
			_complexQueryPartImpl._upperBound = upperBound;
			
			return this;
		}

	}

	private Float _boost;
	private boolean _disabled;
	private String _field;
	private String _name;
	private String _occur;
	private String _parent;
	private boolean _range;
	private boolean _includesLower;
	private boolean _includesUpper;
	private String _type;
	private String _value;
	private String _lowerBound;
	private String _upperBound;
	
}