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

package com.liferay.apio.architect.form;

import static co.unruly.matchers.StreamMatchers.contains;

import static com.liferay.apio.architect.form.FieldType.BOOLEAN;
import static com.liferay.apio.architect.form.FormUtil.getOptionalBoolean;
import static com.liferay.apio.architect.form.FormUtil.getOptionalDate;
import static com.liferay.apio.architect.form.FormUtil.getOptionalDouble;
import static com.liferay.apio.architect.form.FormUtil.getOptionalFile;
import static com.liferay.apio.architect.form.FormUtil.getOptionalFormFieldStream;
import static com.liferay.apio.architect.form.FormUtil.getOptionalLong;
import static com.liferay.apio.architect.form.FormUtil.getOptionalString;
import static com.liferay.apio.architect.form.FormUtil.getRequiredBoolean;
import static com.liferay.apio.architect.form.FormUtil.getRequiredDate;
import static com.liferay.apio.architect.form.FormUtil.getRequiredDouble;
import static com.liferay.apio.architect.form.FormUtil.getRequiredFile;
import static com.liferay.apio.architect.form.FormUtil.getRequiredFormFieldStream;
import static com.liferay.apio.architect.form.FormUtil.getRequiredLong;
import static com.liferay.apio.architect.form.FormUtil.getRequiredString;

import static java.nio.charset.StandardCharsets.UTF_8;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

import com.liferay.apio.architect.alias.form.FieldFormBiConsumer;
import com.liferay.apio.architect.file.BinaryFile;
import com.liferay.apio.architect.functional.Try;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.ws.rs.BadRequestException;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class FormUtilTest {

	@Test(expected = UnsupportedOperationException.class)
	public void testConstructorThrowsException() throws Throwable {
		Constructor<?> constructor =
			FormUtil.class.getDeclaredConstructors()[0];

		constructor.setAccessible(true);

		try {
			constructor.newInstance();
		}
		catch (InvocationTargetException ite) {
			throw ite.getTargetException();
		}
	}

	@Test
	public void testGetOptionalBooleanDoesNotFailIfNotPresent() {
		List<Boolean> list = new ArrayList<>();

		FieldFormBiConsumer<List<Boolean>, Boolean> fieldFormBiConsumer =
			getOptionalBoolean(__ -> Optional.empty(), list);

		fieldFormBiConsumer.accept("boolean", booleanList -> booleanList::add);

		assertThat(list, is(empty()));
	}

	@Test
	public void testGetOptionalDateDoesNotFailIfNotPresent() {
		List<Date> list = new ArrayList<>();

		FieldFormBiConsumer<List<Date>, Date> fieldFormBiConsumer =
			getOptionalDate(__ -> Optional.empty(), list);

		fieldFormBiConsumer.accept("date", dateList -> dateList::add);

		assertThat(list, is(empty()));
	}

	@Test
	public void testGetOptionalDateExtractsDate() {
		List<Date> list = new ArrayList<>();

		FieldFormBiConsumer<List<Date>, Date> fieldFormBiConsumer =
			getOptionalDate(_valueBody("2017-04-03T18:36Z"), list);

		fieldFormBiConsumer.accept("date", dateList -> dateList::add);

		assertThat(list, hasSize(1));

		Date date = list.get(0);

		assertThat(date, is(new Date(1491244560000L)));
	}

	@Test(expected = BadRequestException.class)
	public void testGetOptionalDateFailsIfNotAnISO8601Date() {
		List<Date> list = new ArrayList<>();

		FieldFormBiConsumer<List<Date>, Date> fieldFormBiConsumer =
			getOptionalDate(_valueBody("2017-04-03"), list);

		fieldFormBiConsumer.accept("date", dateList -> dateList::add);
	}

	@Test
	public void testGetOptionalDoubleDoesNotFailIfNotPresent() {
		List<Double> list = new ArrayList<>();

		FieldFormBiConsumer<List<Double>, Double> fieldFormBiConsumer =
			getOptionalDouble(__ -> Optional.empty(), list);

		fieldFormBiConsumer.accept("double", doubleList -> doubleList::add);

		assertThat(list, is(empty()));
	}

	@Test
	public void testGetOptionalDoubleExtractsDouble() {
		List<Double> list = new ArrayList<>();

		FieldFormBiConsumer<List<Double>, Double> fieldFormBiConsumer =
			getOptionalDouble(_valueBody("42.3"), list);

		fieldFormBiConsumer.accept("double", doubleList -> doubleList::add);

		assertThat(list, hasSize(1));

		Double aDouble = list.get(0);

		assertThat(aDouble, is(42.3D));
	}

	@Test(expected = BadRequestException.class)
	public void testGetOptionalDoubleFailsIfNotADouble() {
		List<Double> list = new ArrayList<>();

		FieldFormBiConsumer<List<Double>, Double> fieldFormBiConsumer =
			getOptionalDouble(_valueBody("Apio"), list);

		fieldFormBiConsumer.accept("double", doubleList -> doubleList::add);
	}

	@Test
	public void testGetOptionalFileDoesNotFailIfNotPresent() {
		List<BinaryFile> list = new ArrayList<>();

		FieldFormBiConsumer<List<BinaryFile>, BinaryFile> fieldFormBiConsumer =
			getOptionalFile(__ -> Optional.empty(), list);

		fieldFormBiConsumer.accept("file", fileList -> fileList::add);

		assertThat(list, is(empty()));
	}

	@Test
	public void testGetOptionalFileExtractsBoolean() {
		List<Boolean> list = new ArrayList<>();

		FieldFormBiConsumer<List<Boolean>, Boolean> fieldFormBiConsumer =
			getOptionalBoolean(_valueBody("true"), list);

		fieldFormBiConsumer.accept("boolean", booleanList -> booleanList::add);

		assertThat(list, hasSize(1));

		Boolean aBoolean = list.get(0);

		assertThat(aBoolean, is(true));
	}

	@Test
	public void testGetOptionalFileExtractsFile() {
		List<BinaryFile> list = new ArrayList<>();

		FieldFormBiConsumer<List<BinaryFile>, BinaryFile> fieldFormBiConsumer =
			getOptionalFile(_fileBody(), list);

		fieldFormBiConsumer.accept("file", fileList -> fileList::add);

		assertThat(list, hasSize(1));

		BinaryFile binaryFile = list.get(0);

		assertThat(_readBinaryFile(binaryFile), is("content"));
		assertThat(binaryFile.getMimeType(), is("type"));
	}

	@Test
	public void testGetOptionalFormFieldStream() {
		Map<String, String> map = new LinkedHashMap<String, String>() {
			{
				put("first", "first");
				put("second", "second");
			}
		};

		Stream<FormField> stream = getOptionalFormFieldStream(map, BOOLEAN);

		FormField firstFormField = new FormField("first", false, BOOLEAN);
		FormField secondFormField = new FormField("second", false, BOOLEAN);

		assertThat(stream, contains(firstFormField, secondFormField));
	}

	@Test
	public void testGetOptionalLongDoesNotFailIfNotPresent() {
		List<Long> list = new ArrayList<>();

		FieldFormBiConsumer<List<Long>, Long> fieldFormBiConsumer =
			getOptionalLong(__ -> Optional.empty(), list);

		fieldFormBiConsumer.accept("long", longList -> longList::add);

		assertThat(list, is(empty()));
	}

	@Test
	public void testGetOptionalLongExtractsLong() {
		List<Long> list = new ArrayList<>();

		FieldFormBiConsumer<List<Long>, Long> fieldFormBiConsumer =
			getOptionalLong(_valueBody("42"), list);

		fieldFormBiConsumer.accept("long", longList -> longList::add);

		assertThat(list, hasSize(1));

		Long aLong = list.get(0);

		assertThat(aLong, is(42L));
	}

	@Test(expected = BadRequestException.class)
	public void testGetOptionalLongFailsIfNotALong() {
		List<Long> list = new ArrayList<>();

		FieldFormBiConsumer<List<Long>, Long> fieldFormBiConsumer =
			getOptionalLong(_valueBody("Apio"), list);

		fieldFormBiConsumer.accept("long", longList -> longList::add);
	}

	@Test
	public void testGetOptionalStringDoesNotFailIfNotPresent() {
		List<String> list = new ArrayList<>();

		FieldFormBiConsumer<List<String>, String> fieldFormBiConsumer =
			getOptionalString(__ -> Optional.empty(), list);

		fieldFormBiConsumer.accept("string", stringList -> stringList::add);

		assertThat(list, is(empty()));
	}

	@Test
	public void testGetOptionalStringExtractsString() {
		List<String> list = new ArrayList<>();

		FieldFormBiConsumer<List<String>, String> fieldFormBiConsumer =
			getOptionalString(_valueBody("Apio"), list);

		fieldFormBiConsumer.accept("string", stringList -> stringList::add);

		assertThat(list, hasSize(1));

		String string = list.get(0);

		assertThat(string, is("Apio"));
	}

	@Test
	public void testGetRequiredBooleanExtractsBoolean() {
		List<Boolean> list = new ArrayList<>();

		FieldFormBiConsumer<List<Boolean>, Boolean> fieldFormBiConsumer =
			getRequiredBoolean(_valueBody("true"), list);

		fieldFormBiConsumer.accept("boolean", booleanList -> booleanList::add);

		assertThat(list, hasSize(1));

		Boolean aBoolean = list.get(0);

		assertThat(aBoolean, is(true));
	}

	@Test(expected = BadRequestException.class)
	public void testGetRequiredBooleanFailsIfNotPresent() {
		List<Boolean> list = new ArrayList<>();

		FieldFormBiConsumer<List<Boolean>, Boolean> fieldFormBiConsumer =
			getRequiredBoolean(__ -> Optional.empty(), list);

		fieldFormBiConsumer.accept("boolean", booleanList -> booleanList::add);
	}

	@Test
	public void testGetRequiredDateExtractsDate() {
		List<Date> list = new ArrayList<>();

		FieldFormBiConsumer<List<Date>, Date> fieldFormBiConsumer =
			getRequiredDate(_valueBody("2017-04-03T18:36Z"), list);

		fieldFormBiConsumer.accept("date", dateList -> dateList::add);

		assertThat(list, hasSize(1));

		Date date = list.get(0);

		assertThat(date, is(new Date(1491244560000L)));
	}

	@Test(expected = BadRequestException.class)
	public void testGetRequiredDateFailsIfNotAnISO8601Date() {
		List<Date> list = new ArrayList<>();

		FieldFormBiConsumer<List<Date>, Date> fieldFormBiConsumer =
			getRequiredDate(_valueBody("2017-04-03"), list);

		fieldFormBiConsumer.accept("date", dateList -> dateList::add);
	}

	@Test(expected = BadRequestException.class)
	public void testGetRequiredDateFailsIfNotPresent() {
		List<Date> list = new ArrayList<>();

		FieldFormBiConsumer<List<Date>, Date> fieldFormBiConsumer =
			getRequiredDate(__ -> Optional.empty(), list);

		fieldFormBiConsumer.accept("date", dateList -> dateList::add);
	}

	@Test
	public void testGetRequiredDoubleExtractsDouble() {
		List<Double> list = new ArrayList<>();

		FieldFormBiConsumer<List<Double>, Double> fieldFormBiConsumer =
			getRequiredDouble(_valueBody("42.3"), list);

		fieldFormBiConsumer.accept("double", doubleList -> doubleList::add);

		assertThat(list, hasSize(1));

		Double aDouble = list.get(0);

		assertThat(aDouble, is(42.3D));
	}

	@Test(expected = BadRequestException.class)
	public void testGetRequiredDoubleFailsIfNotADouble() {
		List<Double> list = new ArrayList<>();

		FieldFormBiConsumer<List<Double>, Double> fieldFormBiConsumer =
			getRequiredDouble(_valueBody("Apio"), list);

		fieldFormBiConsumer.accept("double", doubleList -> doubleList::add);
	}

	@Test(expected = BadRequestException.class)
	public void testGetRequiredDoubleFailsIfNotPresent() {
		List<Double> list = new ArrayList<>();

		FieldFormBiConsumer<List<Double>, Double> fieldFormBiConsumer =
			getRequiredDouble(__ -> Optional.empty(), list);

		fieldFormBiConsumer.accept("double", doubleList -> doubleList::add);
	}

	@Test
	public void testGetRequiredFileExtractsFile() {
		List<BinaryFile> list = new ArrayList<>();

		FieldFormBiConsumer<List<BinaryFile>, BinaryFile> fieldFormBiConsumer =
			getRequiredFile(_fileBody(), list);

		fieldFormBiConsumer.accept("file", fileList -> fileList::add);

		assertThat(list, hasSize(1));

		BinaryFile binaryFile = list.get(0);

		assertThat(_readBinaryFile(binaryFile), is("content"));
		assertThat(binaryFile.getMimeType(), is("type"));
	}

	@Test(expected = BadRequestException.class)
	public void testGetRequiredFileFailsIfNotPresent() {
		List<BinaryFile> list = new ArrayList<>();

		FieldFormBiConsumer<List<BinaryFile>, BinaryFile> fieldFormBiConsumer =
			getRequiredFile(__ -> Optional.empty(), list);

		fieldFormBiConsumer.accept("file", fileList -> fileList::add);
	}

	@Test
	public void testGetRequiredFormFieldStream() {
		Map<String, String> map = new LinkedHashMap<String, String>() {
			{
				put("first", "first");
				put("second", "second");
			}
		};

		Stream<FormField> stream = getRequiredFormFieldStream(map, BOOLEAN);

		FormField firstFormField = new FormField("first", true, BOOLEAN);
		FormField secondFormField = new FormField("second", true, BOOLEAN);

		assertThat(stream, contains(firstFormField, secondFormField));
	}

	@Test
	public void testGetRequiredLongExtractsLong() {
		List<Long> list = new ArrayList<>();

		FieldFormBiConsumer<List<Long>, Long> fieldFormBiConsumer =
			getRequiredLong(_valueBody("42"), list);

		fieldFormBiConsumer.accept("long", longList -> longList::add);

		assertThat(list, hasSize(1));

		Long aLong = list.get(0);

		assertThat(aLong, is(42L));
	}

	@Test(expected = BadRequestException.class)
	public void testGetRequiredLongFailsIfNotALong() {
		List<Long> list = new ArrayList<>();

		FieldFormBiConsumer<List<Long>, Long> fieldFormBiConsumer =
			getRequiredLong(_valueBody("Apio"), list);

		fieldFormBiConsumer.accept("long", longList -> longList::add);
	}

	@Test(expected = BadRequestException.class)
	public void testGetRequiredLongFailsIfNotPresent() {
		List<Long> list = new ArrayList<>();

		FieldFormBiConsumer<List<Long>, Long> fieldFormBiConsumer =
			getRequiredLong(__ -> Optional.empty(), list);

		fieldFormBiConsumer.accept("long", longList -> longList::add);
	}

	@Test
	public void testGetRequiredStringExtractsString() {
		List<String> list = new ArrayList<>();

		FieldFormBiConsumer<List<String>, String> fieldFormBiConsumer =
			getRequiredString(_valueBody("Apio"), list);

		fieldFormBiConsumer.accept("string", stringList -> stringList::add);

		assertThat(list, hasSize(1));

		String string = list.get(0);

		assertThat(string, is("Apio"));
	}

	@Test(expected = BadRequestException.class)
	public void testGetRequiredStringFailsIfNotPresent() {
		List<String> list = new ArrayList<>();

		FieldFormBiConsumer<List<String>, String> fieldFormBiConsumer =
			getRequiredString(__ -> Optional.empty(), list);

		fieldFormBiConsumer.accept("string", stringList -> stringList::add);
	}

	private static Body _fileBody() {
		BinaryFile binaryFile = new BinaryFile(
			new ByteArrayInputStream("content".getBytes(UTF_8)), 0L, "type");

		return Body.create(
			__ -> Optional.empty(), __ -> Optional.empty(),
			__ -> Optional.empty(), __ -> Optional.of(binaryFile));
	}

	private static Body _valueBody(String string) {
		return __ -> Optional.of(string);
	}

	private String _readBinaryFile(BinaryFile binaryFile) {
		return Try.fromFallibleWithResources(
			() -> new BufferedReader(new InputStreamReader(
				binaryFile.getInputStream())),
			BufferedReader::readLine).getUnchecked();
	}

}