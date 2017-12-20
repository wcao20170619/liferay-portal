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

package com.liferay.apio.architect.writer;

import static com.liferay.apio.architect.writer.url.URLCreator.createBinaryURL;
import static com.liferay.apio.architect.writer.url.URLCreator.createNestedCollectionURL;
import static com.liferay.apio.architect.writer.url.URLCreator.createSingleURL;

import com.liferay.apio.architect.list.FunctionalList;
import com.liferay.apio.architect.related.RelatedCollection;
import com.liferay.apio.architect.related.RelatedModel;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.request.RequestInfo;
import com.liferay.apio.architect.response.control.Fields;
import com.liferay.apio.architect.single.model.SingleModel;
import com.liferay.apio.architect.uri.Path;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Writes the different fields declared on a {@link Representor}.
 *
 * @author Alejandro Hernández
 */
public class FieldsWriter<T, U> {

	/**
	 * Returns the {@link SingleModel} version of a {@link RelatedModel}.
	 *
	 * @param  relatedModel the related model
	 * @param  parentSingleModel the related model's parent single model
	 * @return the single model version of the related model
	 */
	public static <T, V> Optional<SingleModel<V>> getSingleModel(
		RelatedModel<T, V> relatedModel, SingleModel<T> parentSingleModel) {

		Optional<V> optional = relatedModel.getModelFunction(
		).apply(
			parentSingleModel.getModel()
		);

		Class<V> modelClass = relatedModel.getModelClass();

		return optional.map(model -> new SingleModel<>(model, modelClass));
	}

	public FieldsWriter(
		SingleModel<T> singleModel, RequestInfo requestInfo,
		Representor<T, U> representor, Path path,
		FunctionalList<String> embeddedPathElements) {

		_singleModel = singleModel;
		_requestInfo = requestInfo;
		_representor = representor;
		_path = path;
		_embeddedPathElements = embeddedPathElements;
	}

	/**
	 * Returns the {@link Fields} predicate from the internal {@link
	 * RequestInfo}. If no {@code Fields} information is provided to the {@code
	 * RequestInfo}, this method returns an always-successful predicate.
	 *
	 * @return the {@code Fields} predicate, if {@code Fields} information
	 *         exists; an always-successful predicate otherwise
	 */
	public Predicate<String> getFieldsPredicate() {
		Fields fields = _requestInfo.getFields();

		return fields.apply(_representor.getTypes());
	}

	/**
	 * Writes binary resources. This method uses a {@code BiConsumer} so each
	 * {@code javax.ws.rs.ext.MessageBodyWriter} can write each binary
	 * differently.
	 *
	 * @param biConsumer the {@code BiConsumer} called to write each binary
	 */
	public void writeBinaries(BiConsumer<String, String> biConsumer) {
		Function<String, String> urlFunction = binaryId -> createBinaryURL(
			_requestInfo.getServerURL(), binaryId, _path);

		writeFields(
			Representor::getBinaryFunctions,
			entry -> biConsumer.accept(
				entry.getKey(), urlFunction.apply(entry.getKey())));
	}

	/**
	 * Writes the model's boolean fields. This method uses a {@code BiConsumer}
	 * so each {@code javax.ws.rs.ext.MessageBodyWriter} can write each field
	 * differently.
	 *
	 * @param biConsumer the {@code BiConsumer} called to write each field
	 */
	public void writeBooleanFields(BiConsumer<String, Boolean> biConsumer) {
		writeFields(Representor::getBooleanFunctions, writeField(biConsumer));
	}

	/**
	 * Returns a consumer for entries of a {@code Map<String, Function<T, V>}.
	 * The consumer uses a value function to get the final value, then uses the
	 * bi-consumer provided as the second parameter to process the key and the
	 * final value. This consumer is called only when the final data isn't empty
	 * or {@code null}.
	 *
	 * @param  biConsumer the consumer used to process the key-value pair
	 * @return the consumer for entries of a {@code Map<String, Function<T, V>}
	 */
	public <V> Consumer<Entry<String, Function<T, V>>> writeField(
		BiConsumer<String, V> biConsumer) {

		return writeField(
			function -> function.apply(_singleModel.getModel()), biConsumer);
	}

	/**
	 * Returns a consumer for entries of a {@code Map<String, V>}. The consumer
	 * uses the function provided as the first parameter to get the final value,
	 * then uses the bi-consumer provided as the second parameter to process the
	 * key and the final value. This consumer is called only when the final data
	 * isn't empty or {@code null}.
	 *
	 * @param  function the function used to get the final value
	 * @param  biConsumer the consumer used to process the key-value pair
	 * @return the consumer for entries of a {@code Map<String, V>}
	 */
	public <V, W> Consumer<Entry<String, V>> writeField(
		Function<V, W> function, BiConsumer<String, W> biConsumer) {

		return entry -> {
			W data = function.apply(entry.getValue());

			if (data instanceof String) {
				if ((data != null) && !((String)data).isEmpty()) {
					biConsumer.accept(entry.getKey(), data);
				}
			}
			else if (data != null) {
				biConsumer.accept(entry.getKey(), data);
			}
		};
	}

	/**
	 * Writes a {@code Map<String, V>} returned by a {@link Representor}
	 * function. This method uses a consumer so each caller can decide what to
	 * do with each entry. Each member of the map is filtered using the {@link
	 * Fields} predicate provided by {@link #getFieldsPredicate()}.
	 *
	 * @param representorFunction the {@code Representor} function that returns
	 *        the map being written
	 * @param consumer the consumer used to process each filtered entry
	 */
	public <V> void writeFields(
		Function<Representor<T, U>, Map<String, V>> representorFunction,
		Consumer<Entry<String, V>> consumer) {

		Map<String, V> map = representorFunction.apply(_representor);

		Set<Entry<String, V>> entries = map.entrySet();

		Stream<Entry<String, V>> stream = entries.stream();

		stream.filter(
			entry -> {
				Predicate<String> fieldsPredicate = getFieldsPredicate();

				return fieldsPredicate.test(entry.getKey());
			}
		).forEach(
			consumer
		);
	}

	/**
	 * Writes the model's links. This method uses a {@code BiConsumer} so each
	 * {@code javax.ws.rs.ext.MessageBodyWriter} can write each link
	 * differently.
	 *
	 * @param biConsumer the {@code BiConsumer} called to write each link
	 */
	public void writeLinks(BiConsumer<String, String> biConsumer) {
		writeFields(
			Representor::getLinks, writeField(Function.identity(), biConsumer));
	}

	/**
	 * Writes a model's localized string fields. This method uses a {@code
	 * BiConsumer} so each {@code javax.ws.rs.ext.MessageBodyWriter} can write
	 * each field differently.
	 *
	 * @param biConsumer the {@code BiConsumer} called to write each field
	 */
	public void writeLocalizedStringFields(
		BiConsumer<String, String> biConsumer) {

		writeFields(
			Representor::getLocalizedStringFunctions,
			writeField(
				biFunction -> biFunction.apply(
					_singleModel.getModel(), _requestInfo.getLanguage()),
				biConsumer));
	}

	/**
	 * Writes a model's number fields. This method uses a {@code BiConsumer} so
	 * each {@code javax.ws.rs.ext.MessageBodyWriter} can write each field
	 * differently.
	 *
	 * @param biConsumer the {@code BiConsumer} called to write each field
	 */
	public void writeNumberFields(BiConsumer<String, Number> biConsumer) {
		writeFields(Representor::getNumberFunctions, writeField(biConsumer));
	}

	/**
	 * Writes the related collection's URL, using a {@code BiConsumer}.
	 *
	 * @param relatedCollection the related collection
	 * @param parentEmbeddedPathElements the list of embedded path elements
	 * @param biConsumer the {@code BiConsumer} that writes the related
	 *        collection URL
	 */
	public <V> void writeRelatedCollection(
		RelatedCollection<T, V> relatedCollection, String resourceName,
		FunctionalList<String> parentEmbeddedPathElements,
		BiConsumer<String, FunctionalList<String>> biConsumer) {

		Predicate<String> fieldsPredicate = getFieldsPredicate();

		String key = relatedCollection.getKey();

		if (!fieldsPredicate.test(key)) {
			return;
		}

		String url = createNestedCollectionURL(
			_requestInfo.getServerURL(), _path, resourceName);

		FunctionalList<String> embeddedPathElements = new FunctionalList<>(
			parentEmbeddedPathElements, key);

		biConsumer.accept(url, embeddedPathElements);
	}

	/**
	 * Writes the related collections contained in the {@link Representor} this
	 * writer handles. This method uses a consumer so each {@code
	 * javax.ws.rs.ext.MessageBodyWriter} can write the related model
	 * differently.
	 *
	 * @param nameFunction the function that gets a class's {@code
	 *        com.liferay.apio.architect.resource.CollectionResource} name
	 * @param biConsumer the consumer that writes a linked related model's URL
	 */
	public void writeRelatedCollections(
		Function<String, Optional<String>> nameFunction,
		BiConsumer<String, FunctionalList<String>> biConsumer) {

		Stream<RelatedCollection<T, ?>> stream =
			_representor.getRelatedCollections();

		stream.forEach(
			relatedCollection -> {
				Class<?> modelClass = relatedCollection.getModelClass();

				Optional<String> optional = nameFunction.apply(
					modelClass.getName());

				optional.ifPresent(
					name -> writeRelatedCollection(
						relatedCollection, name, _embeddedPathElements,
						biConsumer));
			});
	}

	/**
	 * Writes a related model. This method uses three consumers: one that writes
	 * the model's info, one that writes its URL if it's a linked related model,
	 * and one that writes its URL if it's an embedded related model. Therefore,
	 * each {@code javax.ws.rs.ext.MessageBodyWriter} can write the related
	 * model differently.
	 *
	 * @param relatedModel the related model instance
	 * @param pathFunction the function that gets a single model's path
	 * @param modelBiConsumer the consumer that writes the related model's
	 *        information
	 * @param linkedURLBiConsumer the consumer that writes a linked related
	 *        model's URL
	 * @param embeddedURLBiConsumer the consumer that writes an embedded related
	 *        model's url
	 */
	public <V> void writeRelatedModel(
		RelatedModel<T, V> relatedModel,
		Function<SingleModel<?>, Optional<Path>> pathFunction,
		BiConsumer<SingleModel<?>, FunctionalList<String>> modelBiConsumer,
		BiConsumer<String, FunctionalList<String>> linkedURLBiConsumer,
		BiConsumer<String, FunctionalList<String>> embeddedURLBiConsumer) {

		writeRelatedModel(
			relatedModel, pathFunction,
			(url, embeddedPathElements) -> {
				Optional<SingleModel<V>> singleModelOptional = getSingleModel(
					relatedModel, _singleModel);

				if (!singleModelOptional.isPresent()) {
					return;
				}

				Predicate<String> embedded = _requestInfo.getEmbedded();

				SingleModel<V> singleModel = singleModelOptional.get();

				Stream<String> stream = Stream.concat(
					Stream.of(embeddedPathElements.head()),
					embeddedPathElements.tailStream());

				String embeddedPath = String.join(
					".", stream.collect(Collectors.toList()));

				if (embedded.test(embeddedPath)) {
					embeddedURLBiConsumer.accept(url, embeddedPathElements);
					modelBiConsumer.accept(singleModel, embeddedPathElements);
				}
				else {
					linkedURLBiConsumer.accept(url, embeddedPathElements);
				}
			});
	}

	/**
	 * Writes a related model. This method uses a consumer so each {@code
	 * javax.ws.rs.ext.MessageBodyWriter} can write the related model
	 * differently.
	 *
	 * @param relatedModel the related model instance
	 * @param pathFunction the function that gets the path of a {@link
	 *        SingleModel}
	 * @param biConsumer the consumer that writes a related model's URL and
	 *        embedded path elements
	 */
	public <V> void writeRelatedModel(
		RelatedModel<T, V> relatedModel,
		Function<SingleModel<?>, Optional<Path>> pathFunction,
		BiConsumer<String, FunctionalList<String>> biConsumer) {

		Predicate<String> fieldsPredicate = getFieldsPredicate();

		String key = relatedModel.getKey();

		if (!fieldsPredicate.test(key)) {
			return;
		}

		Optional<SingleModel<V>> optional = getSingleModel(
			relatedModel, _singleModel);

		FunctionalList<String> embeddedPathElements = new FunctionalList<>(
			_embeddedPathElements, key);

		optional.flatMap(
			pathFunction
		).map(
			path -> createSingleURL(_requestInfo.getServerURL(), path)
		).ifPresent(
			url -> biConsumer.accept(url, embeddedPathElements)
		);
	}

	/**
	 * Writes the related models contained in the {@link Representor} this
	 * writer handles. This method uses three consumers: one that writes the
	 * model's info, one that writes its URL if it's a linked related model, and
	 * one that writes its URL if it's an embedded related model. Therefore,
	 * each {@code javax.ws.rs.ext.MessageBodyWriter} can write the related
	 * model differently.
	 *
	 * @param pathFunction the function that gets a single model's path
	 * @param modelBiConsumer the consumer that writes the related model's
	 *        information
	 * @param linkedURLBiConsumer the consumer that writes a linked related
	 *        model's URL
	 * @param embeddedURLBiConsumer the consumer that writes an embedded related
	 *        model's URL
	 */
	public void writeRelatedModels(
		Function<SingleModel<?>, Optional<Path>> pathFunction,
		BiConsumer<SingleModel<?>, FunctionalList<String>> modelBiConsumer,
		BiConsumer<String, FunctionalList<String>> linkedURLBiConsumer,
		BiConsumer<String, FunctionalList<String>> embeddedURLBiConsumer) {

		List<RelatedModel<T, ?>> embeddedRelatedModels =
			_representor.getRelatedModels();

		embeddedRelatedModels.forEach(
			relatedModel -> writeRelatedModel(
				relatedModel, pathFunction, modelBiConsumer,
				linkedURLBiConsumer, embeddedURLBiConsumer));
	}

	/**
	 * Writes the the handled resource's single URL. This method uses a consumer
	 * so each {@code javax.ws.rs.ext.MessageBodyWriter} can write the URL
	 * differently.
	 *
	 * @param urlConsumer the consumer that writes the URL
	 */
	public void writeSingleURL(Consumer<String> urlConsumer) {
		String url = createSingleURL(_requestInfo.getServerURL(), _path);

		urlConsumer.accept(url);
	}

	/**
	 * Writes the model's string fields. This method uses a consumer so each
	 * {@code javax.ws.rs.ext.MessageBodyWriter} can write each field
	 * differently.
	 *
	 * @param biConsumer the consumer that writes each field
	 */
	public void writeStringFields(BiConsumer<String, String> biConsumer) {
		writeFields(Representor::getStringFunctions, writeField(biConsumer));
	}

	/**
	 * Writes the model's types. This method uses a consumer so each {@link
	 * javax.ws.rs.ext.MessageBodyWriter} can write the types differently.
	 *
	 * @param consumer the consumer that writes the types
	 */
	public void writeTypes(Consumer<List<String>> consumer) {
		consumer.accept(_representor.getTypes());
	}

	private final FunctionalList<String> _embeddedPathElements;
	private final Path _path;
	private final Representor<T, U> _representor;
	private final RequestInfo _requestInfo;
	private final SingleModel<T> _singleModel;

}