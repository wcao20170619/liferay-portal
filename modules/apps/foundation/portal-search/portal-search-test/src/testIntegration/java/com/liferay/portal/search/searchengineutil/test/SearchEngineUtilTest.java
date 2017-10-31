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

package com.liferay.portal.search.searchengineutil.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.search.test.internal.util.AtomicState;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Peter Fellwock
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class SearchEngineUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_atomicState = new AtomicState();
	}

	@After
	public void tearDown() {
		_atomicState.close();
	}

	@Test
	public void testAfterPropertiesSet() {
		Assert.assertTrue(_atomicState.get());
	}

	private static AtomicState _atomicState;

}