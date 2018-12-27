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

package com.liferay.portal.search.internal.legacy.document;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.geolocation.GeoLocationPoint;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.geolocation.GeoPoint;
import com.liferay.portal.search.internal.document.DocumentBuilderImpl;
import com.liferay.portal.search.legacy.document.DocumentBuilderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(immediate = true, service = DocumentBuilderFactory.class)
public class DocumentBuilderFactoryImpl implements DocumentBuilderFactory {

	@Override
	public DocumentBuilder getBuilder(Document document) {
		DocumentBuilder documentBuilder = new DocumentBuilderImpl(
			_fieldRegistry);

		Map<String, Field> legacyFields = document.getFields();

		legacyFields.forEach(
			(name, legacyField) -> addField(legacyField, documentBuilder));

		return documentBuilder;
	}

	protected void addField(Field field, DocumentBuilder documentBuilder) {
		String[] values = field.getValues();

		if (ArrayUtil.isEmpty(values)) {
			return;
		}

		GeoLocationPoint geoLocationPoint = field.getGeoLocationPoint();

		if (geoLocationPoint != null) {
			documentBuilder.addUncheckedValue(
				field.getName(),
				new GeoPoint(
					geoLocationPoint.getLatitude(),
					geoLocationPoint.getLongitude()));

			return;
		}

		List<Object> valuesList = new ArrayList<>(values.length);

		for (String value : values) {
			if (value == null) {
				continue;
			}

			valuesList.add(translateValue(field, value));
		}

		if (valuesList.isEmpty()) {
			return;
		}

		documentBuilder.addUncheckedValues(
			field.getName(), valuesList.toArray(new Object[valuesList.size()]));
	}

	@Reference
	protected void setFieldRegistry(FieldRegistry fieldRegistry) {
		_fieldRegistry = fieldRegistry;
	}

	protected Object translateValue(Field field, String value) {
		Class<? extends Number> clazz = field.getNumericClass();

		if (Double.class.equals(clazz)) {
			return Double.valueOf(value);
		}
		else if (Float.class.equals(clazz)) {
			return Float.valueOf(value);
		}
		else if (Integer.class.equals(clazz)) {
			return Integer.valueOf(value);
		}
		else if (Long.class.equals(clazz)) {
			return Long.valueOf(value);
		}
		else if (Short.class.equals(clazz)) {
			return Short.valueOf(value);
		}

		return value.trim();
	}

	private FieldRegistry _fieldRegistry;

}