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

	/*public void add(double latitude, double longitude);

	public void add(Field field);

	public void add(String name, BigDecimal value);

	public void add(String name, BigDecimal[] values);

	public void add(String name, boolean value);

	public void add(String name, Boolean value);

	public void add(String name, boolean[] values);

	public void add(String name, Boolean[] values);

	public void add(String name, byte[] bytes, String fileExt)
		throws IOException;

	public void add(String name, Date value);

	public void add(String name, Date[] values);

	public void add(String name, double value);

	public void add(String name, double latitude, double longitude);

	public void add(String name, Double value);

	public void add(String name, double[] values);

	public void add(String name, Double[] values);

	public void add(String name, File file, String fileExt) throws IOException;

	public void add(String name, float value);

	public void add(String name, Float value);

	public void add(String name, float[] values);

	public void add(String name, Float[] values);

	public void add(String name, InputStream is, String fileExt)
		throws IOException;

	public void add(
			String name, InputStream is, String fileExt, int maxStringLength)
		throws IOException;

	public void add(String name, int value);

	public void add(String name, int[] values);

	public void add(String name, Integer value);

	public void add(String name, Integer[] values);

	public void add(
		String field, Locale siteDefaultLocale, Map<Locale, String> map);

	public void add(String name, Locale locale, String[] values);

	public void add(String name, long value);

	public void add(String name, Long value);

	public void add(String name, long[] values);

	public void add(String name, Long[] values);

	public void add(String name, Map<Locale, String> values);

	public void add(String name, Map<Locale, String> values, boolean lowerCase);

	public void add(
		String name, Map<Locale, String> values, boolean lowerCase,
		boolean sortable);

	public void add(String name, short value);

	public void add(String name, Short value);

	public void add(String name, short[] values);

	public void add(String name, Short[] values);

	public void add(String name, String value);

	public void add(String name, String value, boolean lowerCase);

	public void add(String name, String languageId, String value);

	public void add(String name, String languageId, String[] values);

	public void add(String name, String[] values);

	public void addLocalizedText(String name, Map<Locale, String> values);

	public void addNumber(String name, BigDecimal value);

	public void addNumber(String name, BigDecimal[] values);

	public void addNumber(String name, double value);

	public void addNumber(String name, Double value);

	public void addNumber(String name, double[] values);

	public void addNumber(String name, Double[] values);

	public void addNumber(String name, float value);

	public void addNumber(String name, Float value);

	public void addNumber(String name, float[] values);

	public void addNumber(String name, Float[] values);

	public void addNumber(String name, int value);

	public void addNumber(String name, int[] values);

	public void addNumber(String name, Integer value);

	public void addNumber(String name, Integer[] values);

	public void addNumber(String name, long value);

	public void addNumber(String name, Long value);

	public void addNumber(String name, long[] values);

	public void addNumber(String name, Long[] values);

	public void addNumber(String name, String value);

	public void addNumber(String name, String[] values);

	public void addUID(String portletId, long field1);

	public void addUID(String portletId, long field1, String field2);

	public void addUID(String portletId, Long field1);

	public void addUID(String portletId, Long field1, String field2);

	public void addUID(String portletId, String field1);

	public void addUID(String portletId, String field1, String field2);

	public void addUID(
		String portletId, String field1, String field2, String field3);

	public void addUID(
		String portletId, String field1, String field2, String field3,
		String field4);

	public boolean hasField(String name);

	public void remove(String name);*/
}