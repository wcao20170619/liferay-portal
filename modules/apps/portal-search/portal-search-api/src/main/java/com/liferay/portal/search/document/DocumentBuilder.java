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

package com.liferay.portal.search.document;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.search.geolocation.GeoPoint;

/**
 * @author Wade Cao
 */
@ProviderType
public interface DocumentBuilder {

	public DocumentBuilder addDate(String name, String value);

	public DocumentBuilder addDates(String name, String... values);

	public DocumentBuilder addDouble(String name, double values);

	public DocumentBuilder addDoubles(String name, Double... values);

	public DocumentBuilder addFloat(String name, float value);

	public DocumentBuilder addFloats(String name, Float... values);

	public DocumentBuilder addGeoPoint(String name, GeoPoint value);

	public DocumentBuilder addGeoPoints(String name, GeoPoint... values);

	public DocumentBuilder addInteger(String name, int value);

	public DocumentBuilder addIntegers(String name, Integer... values);

	public DocumentBuilder addLong(String name, long value);

	public DocumentBuilder addLongs(String name, Long... values);

	public DocumentBuilder addString(String name, String value);

	public DocumentBuilder addStrings(String name, String... value);

	public DocumentBuilder addUncheckedValue(String name, Object value);

	public DocumentBuilder addUncheckedValues(String name, Object[] values);

	public Document build();

}