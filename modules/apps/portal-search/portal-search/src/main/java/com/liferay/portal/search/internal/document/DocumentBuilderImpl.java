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

package com.liferay.portal.search.internal.document;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.FieldBuilder;
import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.field.Mapping;
import com.liferay.portal.search.geolocation.GeoPoint;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Wade Cao
 */
public class DocumentBuilderImpl implements DocumentBuilder {

	public DocumentBuilderImpl(FieldRegistry fieldRegistry) {
		_fieldRegistry = fieldRegistry;
	}

	@Override
	public DocumentBuilder addDate(String name, String value) {
		guardType(name, "date");

		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addDates(String name, String... values) {
		guardType(name, "date");

		addFieldValue(name, values);

		return this;
	}

	@Override
	public DocumentBuilder addDouble(String name, double value) {
		guardType(name, "double");

		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addDoubles(String name, Double... values) {
		guardType(name, "double");

		FieldBuilder fieldBuilder = new FieldBuilderImpl();

		_documentImpl.addField(
			fieldBuilder.name(
				name
			).values(
				values
			).build());

		return this;
	}

	@Override
	public DocumentBuilder addFloat(String name, float value) {
		guardType(name, "float");

		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addFloats(String name, Float... values) {
		guardType(name, "float");

		addFieldValues(name, values);

		return this;
	}

	@Override
	public DocumentBuilder addGeoPoint(String name, GeoPoint value) {
		guardType(name, "geo_point");

		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addGeoPoints(String name, GeoPoint... values) {
		guardType(name, "geo_point");

		addFieldValue(name, values);

		return this;
	}

	@Override
	public DocumentBuilder addInteger(String name, int value) {
		guardType(name, "integer");

		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addIntegers(String name, Integer... values) {
		guardType(name, "integer");

		addFieldValues(name, values);

		return this;
	}

	@Override
	public DocumentBuilder addLong(String name, long value) {
		guardType(name, "long");

		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addLongs(String name, Long... values) {
		guardType(name, "long");

		addFieldValues(name, values);

		return this;
	}

	@Override
	public DocumentBuilder addString(String name, String value) {
		guardTypeString(name);

		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addStrings(String name, String... values) {
		guardTypeString(name);

		addFieldValues(name, values);

		return this;
	}

	@Override
	public DocumentBuilder addUncheckedValue(String name, Object value) {
		addFieldValue(name, value);

		return this;
	}

	@Override
	public DocumentBuilder addUncheckedValues(String name, Object[] values) {
		addFieldValues(name, values);

		return this;
	}

	@Override
	public Document build() {
		return new DocumentImpl(_documentImpl);
	}

	protected void addFieldValue(String name, Object value) {
		FieldBuilder fieldBuilder = new FieldBuilderImpl();

		_documentImpl.addField(
			fieldBuilder.name(
				name
			).value(
				value
			).build());
	}

	protected void addFieldValues(String name, Object[] values) {
		FieldBuilder fieldBuilder = new FieldBuilderImpl();

		_documentImpl.addField(
			fieldBuilder.name(
				name
			).values(
				values
			).build());
	}

	protected Field buildField(String name, String... values) {
		Field field = new Field(name);

		field.setValues(values);

		return field;
	}

	protected void guardType(String name, Function<Mapping, Boolean> function) {
		Optional<Mapping> optional = _fieldRegistry.getMappingOptional(name);

		if (!optional.isPresent()) {
			throw new IllegalArgumentException(
				"Must either add unchecked, or register a mapping for field " +
					name);
		}

		Mapping mapping = optional.get();

		if (!function.apply(mapping)) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Values for field ", name, " must be of type ",
					mapping.getType()));
		}
	}

	protected void guardType(String name, String type) {
		guardType(name, mapping -> isType(mapping, type));
	}

	protected void guardTypeString(String name) {
		guardType(name, this::isTypeString);
	}

	protected boolean isType(Mapping mapping, String type) {
		return Objects.equals(type, mapping.getType());
	}

	protected boolean isTypeString(Mapping mapping) {
		String type = mapping.getType();

		if ("keyword".equals(type) || "text".equals(type)) {
			return true;
		}

		return false;
	}

	private final DocumentImpl _documentImpl = new DocumentImpl();
	private final FieldRegistry _fieldRegistry;

}