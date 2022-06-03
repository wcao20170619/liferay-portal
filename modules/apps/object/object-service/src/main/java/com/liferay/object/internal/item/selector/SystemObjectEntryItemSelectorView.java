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

package com.liferay.object.internal.item.selector;

import com.liferay.info.item.selector.InfoItemSelectorView;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.system.SystemObjectDefinitionMetadata;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Gabriel Albuquerque
 * @author Marcela Cunha
 */
public class SystemObjectEntryItemSelectorView
	implements InfoItemSelectorView,
			   ItemSelectorView<InfoItemItemSelectorCriterion> {

	public SystemObjectEntryItemSelectorView(
		ItemSelectorViewDescriptorRenderer<InfoItemItemSelectorCriterion>
			itemSelectorViewDescriptorRenderer,
		ObjectDefinition objectDefinition,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelatedModelsProviderRegistry objectRelatedModelsProviderRegistry,
		Portal portal,
		SystemObjectDefinitionMetadata systemObjectDefinitionMetadata) {

		_itemSelectorViewDescriptorRenderer =
			itemSelectorViewDescriptorRenderer;
		_objectDefinition = objectDefinition;
		_objectEntryLocalService = objectEntryLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectRelatedModelsProviderRegistry =
			objectRelatedModelsProviderRegistry;
		_portal = portal;
		_systemObjectDefinitionMetadata = systemObjectDefinitionMetadata;
	}

	@Override
	public String getClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	public Class<InfoItemItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return InfoItemItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _objectDefinition.getPluralLabel(locale);
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse, infoItemItemSelectorCriterion,
			portletURL, itemSelectedEventName, search,
			new SystemObjectItemSelectorViewDescriptor(
				(HttpServletRequest)servletRequest, _objectDefinition,
				_objectEntryLocalService, _objectRelatedModelsProviderRegistry,
				portletURL));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SystemObjectEntryItemSelectorView.class);

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new InfoItemItemSelectorReturnType());

	private final ItemSelectorViewDescriptorRenderer
		<InfoItemItemSelectorCriterion> _itemSelectorViewDescriptorRenderer;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;
	private final Portal _portal;
	private final SystemObjectDefinitionMetadata
		_systemObjectDefinitionMetadata;

	private class SystemObjectEntryItemDescriptor
		implements ItemSelectorViewDescriptor.ItemDescriptor {

		public SystemObjectEntryItemDescriptor(
			BaseModel<?> baseModel, HttpServletRequest httpServletRequest) {

			_baseModel = baseModel;
			_httpServletRequest = httpServletRequest;
		}

		@Override
		public String getIcon() {
			return null;
		}

		@Override
		public String getImageURL() {
			return null;
		}

		@Override
		public Date getModifiedDate() {
			Map<String, Object> modelAttributes =
				_baseModel.getModelAttributes();

			return (Date)modelAttributes.get("modifiedDate");
		}

		@Override
		public String getPayload() {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return JSONUtil.put(
				"className", _objectDefinition.getClassName()
			).put(
				"classNameId",
				_portal.getClassNameId(_objectDefinition.getClassName())
			).put(
				"classPK", _baseModel.getPrimaryKeyObj()
			).put(
				"title",
				StringBundler.concat(
					_objectDefinition.getLabel(themeDisplay.getLocale()),
					StringPool.SPACE, _baseModel.getPrimaryKeyObj())
			).toString();
		}

		@Override
		public String getSubtitle(Locale locale) {
			return String.valueOf(_baseModel.getPrimaryKeyObj());
		}

		@Override
		public String getTitle(Locale locale) {
			ObjectField objectField = _objectFieldLocalService.fetchObjectField(
				_objectDefinition.getTitleObjectFieldId());

			if (objectField == null) {
				return StringPool.BLANK;
			}

			Map<String, Object> modelAttributes =
				_baseModel.getModelAttributes();

			return (String)modelAttributes.get(objectField.getDBColumnName());
		}

		@Override
		public long getUserId() {
			Map<String, Object> modelAttributes =
				_baseModel.getModelAttributes();

			return (Long)modelAttributes.get("userId");
		}

		@Override
		public String getUserName() {
			Map<String, Object> modelAttributes =
				_baseModel.getModelAttributes();

			return _portal.getUserName(
				(Long)modelAttributes.get("userId"), StringPool.BLANK);
		}

		private final BaseModel<?> _baseModel;
		private final HttpServletRequest _httpServletRequest;

	}

	private class SystemObjectItemSelectorViewDescriptor
		implements ItemSelectorViewDescriptor<BaseModel<?>> {

		public SystemObjectItemSelectorViewDescriptor(
			HttpServletRequest httpServletRequest,
			ObjectDefinition objectDefinition,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectRelatedModelsProviderRegistry
				objectRelatedModelsProviderRegistry,
			PortletURL portletURL) {

			_httpServletRequest = httpServletRequest;
			_objectDefinition = objectDefinition;
			_objectEntryLocalService = objectEntryLocalService;
			_objectRelatedModelsProviderRegistry =
				objectRelatedModelsProviderRegistry;
			_portletURL = portletURL;

			_portletRequest = (PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);
			_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);
		}

		@Override
		public String getDefaultDisplayStyle() {
			return "descriptive";
		}

		@Override
		public ItemDescriptor getItemDescriptor(BaseModel<?> baseModel) {
			return new SystemObjectEntryItemDescriptor(
				baseModel, _httpServletRequest);
		}

		@Override
		public ItemSelectorReturnType getItemSelectorReturnType() {
			return new InfoItemItemSelectorReturnType();
		}

		@Override
		public SearchContainer<BaseModel<?>> getSearchContainer()
			throws PortalException {

			SearchContainer<BaseModel<?>> searchContainer =
				new SearchContainer<>(
					_portletRequest, _portletURL, null,
					"no-entries-were-found");

			try {
				ObjectRelatedModelsProvider objectRelatedModelsProvider =
					_objectRelatedModelsProviderRegistry.
						getObjectRelatedModelsProvider(
							_objectDefinition.getClassName(),
							ParamUtil.getString(
								_portletRequest, "objectRelationshipType"));

				List<BaseModel<?>> baseModels =
					objectRelatedModelsProvider.getUnrelatedModels(
						_themeDisplay.getCompanyId(),
						_themeDisplay.getScopeGroupId(), _objectDefinition,
						ParamUtil.getLong(_portletRequest, "objectEntryId"),
						ParamUtil.getLong(
							_portletRequest, "objectRelationshipId"));

				searchContainer.setResultsAndTotal(
					() -> baseModels, baseModels.size());
			}
			catch (Exception exception) {
				_log.error(exception);

				searchContainer.setResultsAndTotal(ArrayList::new, 0);
			}

			return searchContainer;
		}

		private final HttpServletRequest _httpServletRequest;
		private final ObjectDefinition _objectDefinition;
		private final ObjectEntryLocalService _objectEntryLocalService;
		private final ObjectRelatedModelsProviderRegistry
			_objectRelatedModelsProviderRegistry;
		private final PortletRequest _portletRequest;
		private final PortletURL _portletURL;
		private final ThemeDisplay _themeDisplay;

	}

}