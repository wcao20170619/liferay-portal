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

package com.liferay.portal.search.test.util.document;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexRequest;
import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.internal.document.DefaultDocumentBuilderFactory;
import com.liferay.portal.search.internal.document.DocumentTranslatorImpl;
import com.liferay.portal.search.internal.field.DefaultFieldRegistry;
import com.liferay.portal.search.internal.sort.DefaultSortBuilderFactory;
import com.liferay.portal.search.internal.sort.SortTranslator;
import com.liferay.portal.search.sort.SortBuilder;
import com.liferay.portal.search.sort.SortBuilderFactory;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelper;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.Before;

/**
 * @author Wade Cao
 */
public abstract class BaseSearchDocumentTestCase extends BaseDocumentTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		populateDates();
		super.setUp();
	}

	protected void addDocumentField(String fieldName) throws Exception {
		addDoubleField(
			fieldName,
			value -> DocumentCreationHelpers.singleNumberSortable(
				fieldName, value));
	}

	protected void addDocuments(
			Function<Double, DocumentCreationHelper> function, double... values)
		throws Exception {

		for (double value : values) {
			addDocument(function.apply(value));
		}
	}

	protected void addDoubleField(
			String fieldName, Function<Double, DocumentCreationHelper> function)
		throws Exception {

		double[] values = {10, 1, 40, 5.3};

		addDocuments(function, values);
	}

	protected FieldRegistry createFieldRegistry() {
		FieldRegistry fieldRegistry = new DefaultFieldRegistry();

		fieldRegistry.registerFieldType("screenName", "text");

		fieldRegistry.registerFieldType(CUSTOM_DATE, "date");

		fieldRegistry.registerFieldType(CREATE_DATE, "date");

		fieldRegistry.registerFieldType(FIELD_DOUBLE, "text");
		fieldRegistry.registerFieldType(FIELD_FLOAT, "text");
		fieldRegistry.registerFieldType(FIELD_INTEGER, "text");
		fieldRegistry.registerFieldType(FIELD_LONG, "text");

		fieldRegistry.registerFieldType(FIELD_DOUBLE_ARRAY, "text");
		fieldRegistry.registerFieldType(FIELD_FLOAT_ARRAY, "text");
		fieldRegistry.registerFieldType(FIELD_INTEGER_ARRAY, "text");
		fieldRegistry.registerFieldType(FIELD_LONG_ARRAY, "text");

		fieldRegistry.registerFieldType(FIELD_DOUBLE_NUMBER, "double");
		fieldRegistry.registerFieldType(FIELD_FLOAT_NUMBER, "float");
		fieldRegistry.registerFieldType(FIELD_INTEGER_NUMBER, "integer");
		fieldRegistry.registerFieldType(FIELD_LONG_NUMBER, "long");

		fieldRegistry.registerFieldType(FIELD_DOUBLE_ARRAY_NUMBER, "double");
		fieldRegistry.registerFieldType(FIELD_FLOAT_ARRAY_NUMBER, "float");
		fieldRegistry.registerFieldType(FIELD_INTEGER_ARRAY_NUMBER, "integer");
		fieldRegistry.registerFieldType(FIELD_LONG_ARRAY_NUMBER, "long");

		return fieldRegistry;
	}

	protected Sort createSort(String field, boolean reverse) {
		SortBuilderFactory sortBuilderFactory =
			new DefaultSortBuilderFactory() {
				{
					sortTranslator = new SortTranslator();
					fieldRegistry = createFieldRegistry();
				}
			};

		SortBuilder sortBuilder = sortBuilderFactory.getBuilder();

		sortBuilder.setField(field);
		sortBuilder.setReverse(reverse);

		return sortBuilder.build();
	}

	protected DocumentBuilder getDocumentBuilder() {
		DocumentBuilderFactory documentBuilderFactory =
			new DefaultDocumentBuilderFactory() {
				{
					documentTranslator = new DocumentTranslatorImpl();
					fieldRegistry = createFieldRegistry();
				}
			};

		return documentBuilderFactory.getBuilder();
	}

	@Override
	protected void populate(Document document, String screenName) {
		DocumentBuilder documentBuilder = getDocumentBuilder();

		documentBuilder.add(
			"firstName", screenName.replaceFirst("user", StringPool.BLANK));
		documentBuilder.add("lastName", "Smith");

		documentBuilder.add(MODIFIED_DATE, dates.get(screenName));

		documentBuilder.add(CUSTOM_DATE, dates.get(screenName));

		documentBuilder.add(CREATE_DATE, dates.get(screenName));

		documentBuilder.add("screenName", screenName);

		documentBuilder.add(FIELD_DOUBLE, doubles.get(screenName));
		documentBuilder.add(FIELD_FLOAT, floats.get(screenName));
		documentBuilder.add(FIELD_INTEGER, integers.get(screenName));
		documentBuilder.add(FIELD_LONG, longs.get(screenName));

		documentBuilder.add(FIELD_DOUBLE_ARRAY, doubleArrays.get(screenName));
		documentBuilder.add(FIELD_FLOAT_ARRAY, floatArrays.get(screenName));
		documentBuilder.add(FIELD_INTEGER_ARRAY, integerArrays.get(screenName));
		documentBuilder.add(FIELD_LONG_ARRAY, longArrays.get(screenName));

		documentBuilder.addNumber(FIELD_DOUBLE_NUMBER, doubles.get(screenName));
		documentBuilder.addNumber(FIELD_FLOAT_NUMBER, floats.get(screenName));
		documentBuilder.addNumber(
			FIELD_INTEGER_NUMBER, integers.get(screenName));
		documentBuilder.addNumber(FIELD_LONG_NUMBER, longs.get(screenName));

		documentBuilder.addNumber(
			FIELD_DOUBLE_ARRAY_NUMBER, doubleArrays.get(screenName));
		documentBuilder.addNumber(
			FIELD_FLOAT_ARRAY_NUMBER, floatArrays.get(screenName));
		documentBuilder.addNumber(
			FIELD_INTEGER_ARRAY_NUMBER, integerArrays.get(screenName));
		documentBuilder.addNumber(
			FIELD_LONG_ARRAY_NUMBER, longArrays.get(screenName));

		document = documentBuilder.build(document);
	}

	protected void populateDates() throws Exception {
		dates.put("fifthuser", dateFormat.parse("20181005"));
		dates.put("firstuser", dateFormat.parse("20181001"));
		dates.put("fourthuser", dateFormat.parse("20181004"));
		dates.put("seconduser", dateFormat.parse("20181002"));
		dates.put("sixthuser", dateFormat.parse("20181006"));
		dates.put("thirduser", dateFormat.parse("20181003"));
	}

	protected void putMappingIndexDynamicValue(String value) {
		String mappingName = "LiferayDocumentType";
		String mappingSource = "{\"dynamic\":\"" + value + "\"}";

		PutMappingIndexRequest putMappingIndexRequest =
			new PutMappingIndexRequest(
				new String[] {String.valueOf(COMPANY_ID)}, mappingName,
				mappingSource);

		getSearchEngineAdapter().execute(putMappingIndexRequest);
	}

	protected static final String CREATE_DATE = "createDate";

	protected static final String CUSTOM_DATE = "cd";

	protected static final String FIELD_DOUBLE_ARRAY_NUMBER = "mdn";

	protected static final String FIELD_DOUBLE_NUMBER = "sdn";

	protected static final String FIELD_FLOAT_ARRAY_NUMBER = "mfn";

	protected static final String FIELD_FLOAT_NUMBER = "sfn";

	protected static final String FIELD_INTEGER_ARRAY_NUMBER = "min";

	protected static final String FIELD_INTEGER_NUMBER = "sin";

	protected static final String FIELD_LONG_ARRAY_NUMBER = "mln";

	protected static final String FIELD_LONG_NUMBER = "sln";

	protected static final String MODIFIED_DATE = "modified";

	protected DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	protected final Map<String, Date> dates = new HashMap<>();

}