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

import static com.liferay.apio.architect.date.DateTransformer.asDate;

import com.liferay.apio.architect.alias.form.FieldFormBiConsumer;
import com.liferay.apio.architect.file.BinaryFile;
import com.liferay.apio.architect.functional.Try;

import java.text.NumberFormat;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.ws.rs.BadRequestException;

/**
 * Provides utility functions for forms.
 *
 * <p>
 * This class shouldn't be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 */
public class FormUtil {

	/**
	 * Returns a field form consumer that tries to extract a boolean from the
	 * HTTP request body and store it in the provided {@code T} instance. If the
	 * field isn't a boolean, a {@code javax.ws.rs.BadRequestException} is
	 * thrown.
	 *
	 * @param  body the HTTP request body
	 * @param  t the form values store
	 * @return the field form consumer
	 */
	public static <T> FieldFormBiConsumer<T, Boolean> getOptionalBoolean(
		Body body, T t) {

		return (key, function) -> _getBoolean(
			body, key, false, function.apply(t));
	}

	/**
	 * Returns a field form consumer that tries to extract a date from the HTTP
	 * request body and store it in the provided {@code T} instance. If the
	 * field isn't an ISO-8601 date, a {@code javax.ws.rs.BadRequestException}
	 * is thrown.
	 *
	 * @param  body the HTTP request body
	 * @param  t the form values store
	 * @return the field form consumer
	 */
	public static <T> FieldFormBiConsumer<T, Date> getOptionalDate(
		Body body, T t) {

		return (key, function) -> _getDate(body, key, false, function.apply(t));
	}

	/**
	 * Returns a field form consumer that tries to extract a double from the
	 * HTTP request body and store it in the provided {@code T} instance. If the
	 * field isn't a double, a {@code javax.ws.rs.BadRequestException} is
	 * thrown.
	 *
	 * @param  body the HTTP request body
	 * @param  t the form values store
	 * @return the field form consumer
	 */
	public static <T> FieldFormBiConsumer<T, Double> getOptionalDouble(
		Body body, T t) {

		return (key, function) -> _getDouble(
			body, key, false, function.apply(t));
	}

	/**
	 * Returns a field form consumer that tries to extract a file from the HTTP
	 * request body and store it in the provided {@code T} instance. If the
	 * field isn't a file, a {@code javax.ws.rs.BadRequestException} is thrown.
	 *
	 * @param  body the HTTP request body
	 * @param  t the form values store
	 * @return the field form consumer
	 */
	public static <T> FieldFormBiConsumer<T, BinaryFile> getOptionalFile(
		Body body, T t) {

		return (key, function) -> _getFile(body, key, false, function.apply(t));
	}

	/**
	 * Returns a stream that contains the optional {@code FormField} extracted
	 * from a map whose keys are the {@code FormField} names.
	 *
	 * @param  map the map
	 * @param  fieldType the {@code FieldType} of the map's fields
	 * @return the stream
	 */
	public static Stream<FormField> getOptionalFormFieldStream(
		Map<String, ?> map, FieldType fieldType) {

		return _getFormFieldStream(map, false, fieldType);
	}

	/**
	 * Returns a field form consumer that tries to extract a long from the HTTP
	 * request body and store it in the provided {@code T} instance. If the
	 * field isn't a long, a {@code javax.ws.rs.BadRequestException} is thrown.
	 *
	 * @param  body the HTTP request body
	 * @param  t the form values store
	 * @return the field form consumer
	 */
	public static <T> FieldFormBiConsumer<T, Long> getOptionalLong(
		Body body, T t) {

		return (key, function) -> _getLong(body, key, false, function.apply(t));
	}

	/**
	 * Returns a field form consumer that tries to extract a string from the
	 * HTTP request body and store it in the provided {@code T} instance. If the
	 * field isn't a string, a {@code javax.ws.rs.BadRequestException} is
	 * thrown.
	 *
	 * @param  body the HTTP request body
	 * @param  t the form values store
	 * @return the field form consumer
	 */
	public static <T> FieldFormBiConsumer<T, String> getOptionalString(
		Body body, T t) {

		return (key, function) -> _getString(
			body, key, false, function.apply(t));
	}

	/**
	 * Returns a field form consumer that extracts a boolean from the HTTP
	 * request body and stores it in the provided {@code T} instance. If the
	 * field isn't found, or it isn't a boolean, a {@code BadRequestException}
	 * is thrown.
	 *
	 * @param  body the HTTP request body
	 * @param  t the form values store
	 * @return the field form consumer
	 */
	public static <T> FieldFormBiConsumer<T, Boolean> getRequiredBoolean(
		Body body, T t) {

		return (key, function) -> _getBoolean(
			body, key, true, function.apply(t));
	}

	/**
	 * Returns a field form consumer that extracts a date from the HTTP request
	 * body and stores it in the provided {@code T} instance. If the field isn't
	 * found, or it isn't an ISO-8601 date, a {@code BadRequestException} is
	 * thrown.
	 *
	 * @param  body the HTTP request body
	 * @param  t the form values store
	 * @return the field form consumer
	 */
	public static <T> FieldFormBiConsumer<T, Date> getRequiredDate(
		Body body, T t) {

		return (key, function) -> _getDate(body, key, true, function.apply(t));
	}

	/**
	 * Returns a field form consumer that extracts a double from the HTTP
	 * request body and stores it in the provided {@code T} instance. If the
	 * field isn't found, or it isn't a double, a {@code BadRequestException} is
	 * thrown.
	 *
	 * @param  body the HTTP request body
	 * @param  t the form values store
	 * @return the field form consumer
	 */
	public static <T> FieldFormBiConsumer<T, Double> getRequiredDouble(
		Body body, T t) {

		return (key, function) -> _getDouble(
			body, key, true, function.apply(t));
	}

	/**
	 * Returns a field form consumer that extracts a file from the HTTP request
	 * body and stores it in the provided {@code T} instance. If the field isn't
	 * found, or it isn't a file, a {@code BadRequestException} is thrown.
	 *
	 * @param  body the HTTP request body
	 * @param  t the form values store
	 * @return the field form consumer
	 */
	public static <T> FieldFormBiConsumer<T, BinaryFile> getRequiredFile(
		Body body, T t) {

		return (key, function) -> _getFile(body, key, true, function.apply(t));
	}

	/**
	 * Returns a stream that contains the required {@code FormField} extracted
	 * from a map whose keys are the {@code FormField} names.
	 *
	 * @param  map the map
	 * @param  fieldType the {@code FieldType} of the map's fields
	 * @return the stream
	 */
	public static Stream<FormField> getRequiredFormFieldStream(
		Map<String, ?> map, FieldType fieldType) {

		return _getFormFieldStream(map, true, fieldType);
	}

	/**
	 * Returns a field form consumer that extracts a long from the HTTP request
	 * body and stores it in the provided {@code T} instance. If the field isn't
	 * found, or it isn't a long, a {@code javax.ws.rs.BadRequestException} is
	 * thrown.
	 *
	 * @param  body the HTTP request body
	 * @param  t the form values store
	 * @return the field form consumer
	 */
	public static <T> FieldFormBiConsumer<T, Long> getRequiredLong(
		Body body, T t) {

		return (key, function) -> _getLong(body, key, true, function.apply(t));
	}

	/**
	 * Returns a field form consumer that extracts a string from the HTTP
	 * request body and stores it in the provided {@code T} instance. If the
	 * field isn't found, or it isn't a string, a {@code BadRequestException} is
	 * thrown.
	 *
	 * @param  body the HTTP request body
	 * @param  t the form values store
	 * @return the field form consumer
	 */
	public static <T> FieldFormBiConsumer<T, String> getRequiredString(
		Body body, T t) {

		return (key, function) -> _getString(
			body, key, true, function.apply(t));
	}

	private static void _getBoolean(
		Body body, String key, boolean required, Consumer<Boolean> consumer) {

		_getField(
			body, key, required,
			value -> consumer.accept(Boolean.valueOf(value)));
	}

	private static void _getDate(
		Body body, String key, boolean required, Consumer<Date> consumer) {

		StringBuilder stringBuilder = new StringBuilder();

		String message = stringBuilder.append(
			"Field \""
		).append(
			key
		).append(
			"\" should be a string date in ISO-8601 format: "
		).append(
			"yyyy-MM-dd'T'HH:mm'Z'"
		).toString();

		_getString(
			body, key, required,
			string -> {
				Try<Date> dateTry = asDate(string);

				Date date = dateTry.orElseThrow(
					() -> new BadRequestException(message));

				consumer.accept(date);
			});
	}

	private static void _getDouble(
		Body body, String key, boolean required, Consumer<Double> consumer) {

		_getNumber(body, key, required, Number::doubleValue, consumer);
	}

	private static void _getField(
		Body body, String key, boolean required, Consumer<String> consumer) {

		Optional<String> optional = body.getValueOptional(key);

		if (optional.isPresent()) {
			consumer.accept(optional.get());
		}
		else if (required) {
			throw new BadRequestException("Field \"" + key + "\" is required");
		}
	}

	private static void _getFile(
		Body body, String key, boolean required,
		Consumer<BinaryFile> consumer) {

		Optional<BinaryFile> optional = body.getFileOptional(key);

		if (optional.isPresent()) {
			consumer.accept(optional.get());
		}
		else if (required) {
			throw new BadRequestException("Field \"" + key + "\" is required");
		}
	}

	private static Stream<FormField> _getFormFieldStream(
		Map<String, ?> map, Boolean required, FieldType fieldType) {

		Set<String> keys = map.keySet();

		Stream<String> stream = keys.stream();

		return stream.map(name -> new FormField(name, required, fieldType));
	}

	private static void _getLong(
		Body body, String key, boolean required, Consumer<Long> consumer) {

		_getNumber(body, key, required, Number::longValue, consumer);
	}

	private static <T extends Number> void _getNumber(
		Body body, String key, boolean required, Function<Number, T> function,
		Consumer<T> consumer) {

		_getField(
			body, key, required,
			value -> {
				Try<String> stringTry = Try.success(value);

				stringTry.map(
					NumberFormat.getInstance()::parse
				).map(
					function::apply
				).voidFold(
					__ -> {
						throw new BadRequestException(
							"Field \"" + key + "\" should be a number");
					},
					consumer::accept
				);
			});
	}

	private static void _getString(
		Body body, String key, boolean required, Consumer<String> consumer) {

		_getField(body, key, required, consumer);
	}

	private FormUtil() {
		throw new UnsupportedOperationException();
	}

}