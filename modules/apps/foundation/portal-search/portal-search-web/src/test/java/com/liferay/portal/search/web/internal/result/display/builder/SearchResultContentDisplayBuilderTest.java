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

package com.liferay.portal.search.web.internal.result.display.builder;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultContentDisplayContext;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.RegistryUtil;

import java.util.Locale;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

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
@PrepareForTest({AssetRendererFactoryRegistryUtil.class, PortalUtil.class})
@RunWith(PowerMockRunner.class)
public class SearchResultContentDisplayBuilderTest {

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

		PowerMockito.mockStatic(PortalUtil.class);
		PowerMockito.when(
			PortalUtil.getLiferayPortletResponse(Mockito.anyObject())
		).thenReturn(
			Mockito.mock(LiferayPortletResponse.class)
		);

		PowerMockito.when(
			PortalUtil.getLiferayPortletRequest(Mockito.anyObject())
		).thenReturn(
			Mockito.mock(LiferayPortletRequest.class)
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
	public void testAssetRendererFactoryNull() throws Exception {
		PowerMockito.when(
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByType(
				Mockito.anyString())
		).thenReturn(
			null
		);

		SearchResultContentDisplayBuilder searchResultContentDisplayBuilder =
			createSearchResultContentDisplayBuilder();

		SearchResultContentDisplayContext searchResultContentDisplayContext =
			searchResultContentDisplayBuilder.build();

		Assert.assertNull(
			searchResultContentDisplayContext.getAssetRendererFactory());
	}

	@Test
	public void testHasEditPermission() throws Exception {
		Mockito.doReturn(
			true
		).when(
			_assetRenderer
		).hasEditPermission(
			Mockito.anyObject()
		);

		Mockito.doReturn(
			_editPortletURL
		).when(
			_assetRenderer
		).getURLEdit(
			Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(),
			Mockito.anyObject()
		);

		Mockito.doReturn(
			"url.path.myPortletURL"
		).when(
			_editPortletURL
		).toString();

		Mockito.doReturn(
			Mockito.mock(PortletURL.class)
		).when(
			_renderResponse
		).createRenderURL();

		SearchResultContentDisplayBuilder searchResultContentDisplayBuilder =
			createSearchResultContentDisplayBuilder();

		SearchResultContentDisplayContext searchResultContentDisplayContext =
			searchResultContentDisplayBuilder.build();

		Assert.assertNotNull(searchResultContentDisplayContext);

		Assert.assertEquals(
			"url.path.myPortletURL",
			searchResultContentDisplayContext.getEditPortletURL());

		Assert.assertEquals(
			1, searchResultContentDisplayContext.getData().size());
	}

	@Test
	public void testResultContextWithVisiblity() throws Exception {
		SearchResultContentDisplayBuilder searchResultContentDisplayBuilder =
			createSearchResultContentDisplayBuilder();

		SearchResultContentDisplayContext searchResultContentDisplayContext =
			searchResultContentDisplayBuilder.build();

		Assert.assertNotNull(searchResultContentDisplayContext);

		Assert.assertFalse(searchResultContentDisplayContext.isVisible());

		Mockito.doReturn(
			true
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

		searchResultContentDisplayContext =
			searchResultContentDisplayBuilder.build();

		Assert.assertTrue(searchResultContentDisplayContext.isVisible());
	}

	@Test
	public void testTemporarilyUnavailable() throws Exception {
		Mockito.doReturn(
			null
		).when(
			_assetRendererFactory
		).getAssetEntry(
			Mockito.anyLong()
		);

		SearchResultContentDisplayBuilder searchResultContentDisplayBuilder =
			createSearchResultContentDisplayBuilder();

		SearchResultContentDisplayContext searchResultContentDisplayContext =
			searchResultContentDisplayBuilder.build();

		Assert.assertTrue(
			searchResultContentDisplayContext.isTemporarilyUnavailable());
	}

	protected SearchResultContentDisplayBuilder
		createSearchResultContentDisplayBuilder() {

		SearchResultContentDisplayBuilder searchResultContentDisplayBuilder =
			new SearchResultContentDisplayBuilder();

		searchResultContentDisplayBuilder.setPermissionChecker(
			_permissionChecker);
		searchResultContentDisplayBuilder.setLocale(Locale.US);
		searchResultContentDisplayBuilder.setAssetEntryId(123);
		searchResultContentDisplayBuilder.setType("myFakeType");
		searchResultContentDisplayBuilder.setRenderRequest(_renderRequest);
		searchResultContentDisplayBuilder.setRenderResponse(_renderResponse);

		return searchResultContentDisplayBuilder;
	}

	@Mock
	private AssetEntry _assetEntry;

	@Mock
	private AssetRenderer<?> _assetRenderer;

	@Mock
	@SuppressWarnings("rawtypes")
	private AssetRendererFactory _assetRendererFactory;

	@Mock
	private PortletURL _editPortletURL;

	@Mock
	private PermissionChecker _permissionChecker;

	@Mock
	private RenderRequest _renderRequest;

	@Mock
	private RenderResponse _renderResponse;

}