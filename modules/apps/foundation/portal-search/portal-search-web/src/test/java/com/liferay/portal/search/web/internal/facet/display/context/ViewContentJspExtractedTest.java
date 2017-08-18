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

package com.liferay.portal.search.web.internal.facet.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.RegistryUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Wade Cao
 */
@PrepareForTest(AssetRendererFactoryRegistryUtil.class)
@RunWith(PowerMockRunner.class)
public class ViewContentJspExtractedTest {

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		MockitoAnnotations.initMocks(this);

		PowerMockito.mockStatic(AssetRendererFactoryRegistryUtil.class);
		PowerMockito.when(
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByType(
				Mockito.anyString())
		).thenReturn(
			_assetRendererFactory
		);

		Mockito.doReturn(
			_assetEntry
		).when(
			_assetRendererFactory
		).getAssetEntry(
			Mockito.anyLong()
		);

		Mockito.doReturn(
			_assetRenderer
		).when(
			_assetEntry
		).getAssetRenderer();
	}

	@Test
	public void testViewContentWithoutViewPermissionCheck() throws Exception {
		Mockito.doReturn(
			true
		).when(
			_assetEntry
		).isVisible();

		//fake assetEntryId/type
		long assetEntryId = 111;
		String type = "anyType";

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByType(
				type);

		AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
			assetEntryId);

		AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();
		//the first checkpoint TRUE
		Assert.assertNotNull(assetEntry);

		Assert.assertTrue(assetEntry.isVisible());

		//the second checkpoint FALSE for edit permission redirect
		boolean hasEditPermission = assetRenderer.hasEditPermission(
			_permissionChecker);

		Assert.assertFalse(hasEditPermission);

		//display contents as long as the page is not redirect
		Assert.assertNotNull(assetEntry);
		Assert.assertNotNull(assetRenderer);
		Assert.assertNotNull(assetRendererFactory);
	}

	@Test
	public void testViewContentWithViewPermissionCheck() throws Exception {
		Mockito.doReturn(
			false
		).when(
			_assetRenderer
		).hasViewPermission(
			Mockito.anyObject()
		);

		Mockito.doReturn(
			true
		).when(
			_assetEntry
		).isVisible();

		//fake assetEntryId/type
		long assetEntryId = 111;
		String type = "anyType";

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByType(
				type);

		AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
			assetEntryId);

		AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();
		//the first checkpoint TRUE
		Assert.assertNotNull(assetEntry);

		Assert.assertTrue(assetEntry.isVisible());

		boolean viewPermission = assetRenderer.hasViewPermission(
			_permissionChecker);

		//fix for the view permission check should be FALSE in our case
		Assert.assertFalse(viewPermission);

		if (viewPermission) {

			//the second checkpoint FALSE for edit permission redirect
			boolean hasEditPermission = assetRenderer.hasEditPermission(
				_permissionChecker);

			Assert.assertFalse(hasEditPermission);

			//display contents as long as the page is not redirect
			Assert.assertNotNull(assetEntry);
			Assert.assertNotNull(assetRenderer);
			Assert.assertNotNull(assetRendererFactory);
		}
	}

	@Mock
	private AssetEntry _assetEntry;

	@Mock
	private AssetRenderer<?> _assetRenderer;

	@Mock
	@SuppressWarnings("rawtypes")
	private AssetRendererFactory _assetRendererFactory;

	@Mock
	private PermissionChecker _permissionChecker;

}