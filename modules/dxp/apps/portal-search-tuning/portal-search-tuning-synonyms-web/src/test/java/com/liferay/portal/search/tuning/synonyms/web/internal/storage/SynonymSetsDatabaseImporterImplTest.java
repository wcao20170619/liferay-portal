/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.storage;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.tuning.synonyms.web.internal.BaseSynonymsWebTestCase;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.DocumentToSynonymSetTranslator;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.DocumentToSynonymSetTranslatorImpl;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReindexer;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class SynonymSetsDatabaseImporterImplTest
	extends BaseSynonymsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_synonymSetsDatabaseImporterImpl =
			new SynonymSetsDatabaseImporterImpl();
		_documentToSynonymSetTranslator =
			new DocumentToSynonymSetTranslatorImpl();

		ReflectionTestUtil.setFieldValue(
			_synonymSetsDatabaseImporterImpl, "documentToSynonymSetTranslator",
			_documentToSynonymSetTranslator);

		ReflectionTestUtil.setFieldValue(
			_synonymSetsDatabaseImporterImpl, "queries", _queries);
		ReflectionTestUtil.setFieldValue(
			_synonymSetsDatabaseImporterImpl, "searchEngineAdapter",
			searchEngineAdapter);
		ReflectionTestUtil.setFieldValue(
			_synonymSetsDatabaseImporterImpl, "synonymSetIndexNameBuilder",
			synonymSetIndexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_synonymSetsDatabaseImporterImpl, "synonymSetIndexReindexer",
			_synonymSetIndexReindexer);
		ReflectionTestUtil.setFieldValue(
			_synonymSetsDatabaseImporterImpl, "synonymSetJSONStorageHelper",
			_synonymSetJSONStorageHelper);
	}

	@Test
	public void testPopulateDatabase() {
		setUpSynonymSetIndexNameBuilder();
		setUpSearchEngineAdapter(setUpSearchHits("car,automobile"));

		_synonymSetsDatabaseImporterImpl.populateDatabase(111L);

		Mockito.verify(
			_synonymSetIndexReindexer, Mockito.times(1)
		).reindex(
			Mockito.anyObject()
		);
	}

	@Test
	public void testPopulateDatabaseExceptionBeforeReindex() {
		_synonymSetsDatabaseImporterImpl.populateDatabase(111L);

		Mockito.verify(
			_synonymSetIndexReindexer, Mockito.never()
		).reindex(
			Mockito.anyObject()
		);
	}

	private DocumentToSynonymSetTranslator _documentToSynonymSetTranslator;

	@Mock
	private Queries _queries;

	@Mock
	private SynonymSetIndexReindexer _synonymSetIndexReindexer;

	@Mock
	private SynonymSetJSONStorageHelper _synonymSetJSONStorageHelper;

	private SynonymSetsDatabaseImporterImpl _synonymSetsDatabaseImporterImpl;

}