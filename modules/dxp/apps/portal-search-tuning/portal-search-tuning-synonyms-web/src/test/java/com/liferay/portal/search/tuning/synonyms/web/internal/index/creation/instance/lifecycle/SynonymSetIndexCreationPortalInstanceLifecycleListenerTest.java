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

package com.liferay.portal.search.tuning.synonyms.web.internal.index.creation.instance.lifecycle;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.BaseSynonymsWebTestCase;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexCreator;
import com.liferay.portal.search.tuning.synonyms.web.internal.synchronizer.FilterToIndexSynchronizer;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class SynonymSetIndexCreationPortalInstanceLifecycleListenerTest
	extends BaseSynonymsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_synonymSetIndexCreationPortalInstanceLifecycleListener =
			new SynonymSetIndexCreationPortalInstanceLifecycleListener();

		ReflectionTestUtil.setFieldValue(
			_synonymSetIndexCreationPortalInstanceLifecycleListener,
			"_filterToIndexSynchronizer", _filterToIndexSynchronizer);
		ReflectionTestUtil.setFieldValue(
			_synonymSetIndexCreationPortalInstanceLifecycleListener,
			"_indexNameBuilder", _indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_synonymSetIndexCreationPortalInstanceLifecycleListener,
			"_synonymSetIndexCreator", _synonymSetIndexCreator);
		ReflectionTestUtil.setFieldValue(
			_synonymSetIndexCreationPortalInstanceLifecycleListener,
			"_synonymSetIndexNameBuilder", _synonymSetIndexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_synonymSetIndexCreationPortalInstanceLifecycleListener,
			"_synonymSetIndexReader", synonymSetIndexReader);
	}

	@Test
	public void testPortalInstanceRegisteredExistFalse() throws Exception {
		setUpSynonymSetIndexReader(false);

		_synonymSetIndexCreationPortalInstanceLifecycleListener.
			portalInstanceRegistered(Mockito.mock(Company.class));

		Mockito.verify(
			_synonymSetIndexCreator, Mockito.times(1)
		).create(
			Matchers.anyObject()
		);
		Mockito.verify(
			_filterToIndexSynchronizer, Mockito.times(1)
		).copyToIndex(
			Matchers.anyString(), Matchers.anyObject()
		);
	}

	@Test
	public void testPortalInstanceRegisteredExistTrue() throws Exception {
		setUpSynonymSetIndexReader(true);

		_synonymSetIndexCreationPortalInstanceLifecycleListener.
			portalInstanceRegistered(Mockito.mock(Company.class));

		Mockito.verify(
			_synonymSetIndexCreator, Mockito.never()
		).create(
			Matchers.anyObject()
		);
		Mockito.verify(
			_filterToIndexSynchronizer, Mockito.never()
		).copyToIndex(
			Matchers.anyString(), Matchers.anyObject()
		);
	}

	@Mock
	private FilterToIndexSynchronizer _filterToIndexSynchronizer;

	@Mock
	private IndexNameBuilder _indexNameBuilder;

	private SynonymSetIndexCreationPortalInstanceLifecycleListener
		_synonymSetIndexCreationPortalInstanceLifecycleListener;

	@Mock
	private SynonymSetIndexCreator _synonymSetIndexCreator;

	@Mock
	private SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder;

}