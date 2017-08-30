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

package com.liferay.portal.search.facet.faceted.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.search.facet.tag.AssetTagNamesFacetFactory;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
@Sync
public class SelectionsFacetedSearcherTest extends BaseFacetedSearcherTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		Registry registry = RegistryUtil.getRegistry();

		_assetTagNamesFacetFactory = registry.getService(
			AssetTagNamesFacetFactory.class);
	}

	@Test
	public void testSearchByFacet() throws Exception {
		String tag = "enterprise. open-source for life";
		String tag1 = "I have other assetTagName.";

		User user = addUser(tag);
		addUser(tag);
		addUser(tag1);

		//test for keyword=tag
		SearchContext searchContext = getSearchContext(tag);

		Facet facet = _assetTagNamesFacetFactory.newInstance(searchContext);

		searchContext.addFacet(facet);

		Hits hits = search(searchContext);

		Assert.assertEquals(hits.toString(), 2, hits.getLength());

		Map<String, Integer> frequencies = Collections.singletonMap(tag, 2);

		assertFrequencies(facet.getFieldName(), searchContext, frequencies);

		//test facet.setValues for userID and still keyword=tag
		MultiValueFacet assetTagNamesFacet =
			(MultiValueFacet)_assetTagNamesFacetFactory.newInstance(
				searchContext);

		assetTagNamesFacet.setFieldName(Field.USER_ID);
		assetTagNamesFacet.setStatic(true);

		long[] userIds = {user.getUserId()};
		//setValues on the facet. expect to get
		//fewer results, filtered by selections
		assetTagNamesFacet.setValues(userIds);

		searchContext.addFacet(assetTagNamesFacet);

		hits = search(searchContext);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());

		String[] assetTagNames = {tag};

		Map<String, String> expected = userSearchFixture.toMap(
			user, assetTagNames);

		assertTags(tag, hits, expected);

		//search for userId with no match keyword
		searchContext = getSearchContext(tag1);

		//test facet.setValues for the same userID and
		//keyword=tag1. expect miss-match
		assetTagNamesFacet =
			(MultiValueFacet)_assetTagNamesFacetFactory.newInstance(
				searchContext);

		assetTagNamesFacet.setFieldName(Field.USER_ID);
		assetTagNamesFacet.setStatic(true);

		assetTagNamesFacet.setValues(userIds);

		searchContext.addFacet(assetTagNamesFacet);

		hits = search(searchContext);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());
	}

	protected User addUser(String... assetTagNames) throws Exception {
		Group group = userSearchFixture.addGroup();

		return addUser(group, assetTagNames);
	}

	private AssetTagNamesFacetFactory _assetTagNamesFacetFactory;

}