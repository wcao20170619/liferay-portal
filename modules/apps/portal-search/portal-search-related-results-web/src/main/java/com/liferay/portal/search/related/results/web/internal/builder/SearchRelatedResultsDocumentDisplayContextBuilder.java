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

package com.liferay.portal.search.related.results.web.internal.builder;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatConstants;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.legacy.document.DocumentBuilderFactory;
import com.liferay.portal.search.related.results.web.internal.display.context.SearchRelatedResultsDocumentDisplayContext;
import com.liferay.portal.search.related.results.web.internal.util.SearchStringUtil;
import com.liferay.portal.search.summary.Summary;
import com.liferay.portal.search.summary.SummaryBuilder;
import com.liferay.portal.search.summary.SummaryBuilderFactory;
import com.liferay.portal.search.web.search.result.SearchResultImage;
import com.liferay.portal.search.web.search.result.SearchResultImageContributor;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Wade Cao
 */
public class SearchRelatedResultsDocumentDisplayContextBuilder {

	public SearchRelatedResultsDocumentDisplayContext build() throws Exception {
		try {
			if (_documentBuilderFactory != null) {
				_document = _documentBuilderFactory.builder(
					_legacyDocument
				).build();
			}

			String className = getFieldValueString(Field.ENTRY_CLASS_NAME);

			long classPK = getEntryClassPK();

			return build(className, classPK);
		}
		catch (Exception e) {
			return buildTemporarilyUnavailable();
		}
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder
		setAssetEntryLocalService(
			AssetEntryLocalService assetEntryLocalService) {

		_assetEntryLocalService = assetEntryLocalService;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder setDocument(
		com.liferay.portal.kernel.search.Document legacyDocument) {

		_legacyDocument = legacyDocument;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder setDocument(
		Document document) {

		_document = document;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder
		setDocumentBuilderFactory(
			DocumentBuilderFactory documentBuilderFactory) {

		_documentBuilderFactory = documentBuilderFactory;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder
		setFastDateFormatFactory(FastDateFormatFactory fastDateFormatFactory) {

		_fastDateFormatFactory = fastDateFormatFactory;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder
		setHighlightEnabled(boolean highlightEnabled) {

		_highlightEnabled = highlightEnabled;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder setImageRequested(
		boolean imageRequested) {

		_imageRequested = imageRequested;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder setIndexerRegistry(
		IndexerRegistry indexerRegistry) {

		_indexerRegistry = indexerRegistry;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder setLocale(
		Locale locale) {

		_locale = locale;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder setPortal(
		Portal portal) {

		_portal = portal;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder setRenderRequest(
		RenderRequest renderRequest) {

		_renderRequest = renderRequest;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder setRenderResponse(
		RenderResponse renderResponse) {

		_renderResponse = renderResponse;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder setResourceActions(
		ResourceActions resourceActions) {

		_resourceActions = resourceActions;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder
		setSearchResultImageContributorsStream(
			Stream<SearchResultImageContributor>
				searchResultImageContributorsStream) {

		_searchResultImageContributorsStream =
			searchResultImageContributorsStream;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder
		setSummaryBuilderFactory(SummaryBuilderFactory summaryBuilderFactory) {

		_summaryBuilderFactory = summaryBuilderFactory;

		return this;
	}

	public SearchRelatedResultsDocumentDisplayContextBuilder setThemeDisplay(
		ThemeDisplay themeDisplay) {

		_themeDisplay = themeDisplay;

		return this;
	}

	protected SearchRelatedResultsDocumentDisplayContext build(
			String className, long classPK)
		throws Exception {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		AssetRenderer<?> assetRenderer = null;

		if (assetRendererFactory != null) {
			long resourcePrimKey = GetterUtil.getLong(
				getFieldValueString(Field.ROOT_ENTRY_CLASS_PK));

			if (resourcePrimKey > 0) {
				classPK = resourcePrimKey;
			}

			assetRenderer = getAssetRenderer(
				className, classPK, assetRendererFactory);
		}

		Summary summary = getSummary(className, assetRenderer);

		if (summary == null) {
			return null;
		}

		return build(summary, className, classPK, assetRenderer);
	}

	protected SearchRelatedResultsDocumentDisplayContext build(
			Summary summary, String className, long classPK,
			AssetRenderer<?> assetRenderer)
		throws PortalException, PortletException {

		SearchRelatedResultsDocumentDisplayContext
			searchRelatedResultsDocumentDisplayContext =
				new SearchRelatedResultsDocumentDisplayContext();

		if (Validator.isNotNull(summary.getContent())) {
			searchRelatedResultsDocumentDisplayContext.setContent(
				summary.getContent());
		}
		else {
			searchRelatedResultsDocumentDisplayContext.setContent(
				StringPool.BLANK);
		}

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			className, classPK);

		buildCreationDateString(searchRelatedResultsDocumentDisplayContext);
		buildCreatorUserName(searchRelatedResultsDocumentDisplayContext);
		buildImage(
			searchRelatedResultsDocumentDisplayContext, className, classPK);
		buildModelResource(
			searchRelatedResultsDocumentDisplayContext, className);

		buildCategoriesString(
			searchRelatedResultsDocumentDisplayContext, assetEntry);
		searchRelatedResultsDocumentDisplayContext.setTitle(summary.getTitle());
		searchRelatedResultsDocumentDisplayContext.setViewURL(
			_portal.getCurrentURL(_renderRequest));

		return searchRelatedResultsDocumentDisplayContext;
	}

	protected void buildCategoriesString(
		SearchRelatedResultsDocumentDisplayContext
			searchRelatedResultsDocumentDisplayContext,
		AssetEntry assetEntry) {

		searchRelatedResultsDocumentDisplayContext.setCategoriesString(
			StringPool.BLANK);

		if (assetEntry == null) {
			return;
		}

		List<AssetCategory> assetCategories = assetEntry.getCategories();

		assetCategories.forEach(
			assetCategory -> {
				if (Validator.isBlank(
						searchRelatedResultsDocumentDisplayContext.
							getCategoriesString())) {

					searchRelatedResultsDocumentDisplayContext.
						setCategoriesString(assetCategory.getName());
				}
				else {
					searchRelatedResultsDocumentDisplayContext.
						setCategoriesString("," + assetCategory.getName());
				}
			});
	}

	protected void buildCreationDateString(
		SearchRelatedResultsDocumentDisplayContext
			searchRelatedResultsDocumentDisplayContext) {

		Optional<String> dateStringOptional = SearchStringUtil.maybe(
			getFieldValueString(Field.CREATE_DATE));

		Optional<Date> dateOptional = dateStringOptional.map(
			this::parseDateStringFieldValue);

		dateOptional.ifPresent(
			date ->
				searchRelatedResultsDocumentDisplayContext.
					setCreationDateString(formatCreationDate(date)));
	}

	protected void buildCreatorUserName(
		SearchRelatedResultsDocumentDisplayContext
			searchRelatedResultsDocumentDisplayContext) {

		String creatorUserName = getFieldValueString(Field.USER_NAME);

		if (!Validator.isBlank(creatorUserName)) {
			searchRelatedResultsDocumentDisplayContext.setCreatorUserName(
				creatorUserName);
		}
		else {
			searchRelatedResultsDocumentDisplayContext.setCreatorUserName(
				StringPool.BLANK);
		}
	}

	protected void buildImage(
		SearchRelatedResultsDocumentDisplayContext
			searchRelatedResultsDocumentDisplayContext,
		String className, long classPK) {

		if (!_imageRequested) {
			return;
		}

		SearchResultImage searchResultImage = new SearchResultImage() {

			@Override
			public String getClassName() {
				return className;
			}

			@Override
			public long getClassPK() {
				return classPK;
			}

			@Override
			public void setIcon(String iconName) {
				searchRelatedResultsDocumentDisplayContext.setIconId(iconName);
			}

			@Override
			public void setThumbnail(String thumbnailURLString) {
				searchRelatedResultsDocumentDisplayContext.
					setThumbnailURLString(thumbnailURLString);
			}

		};

		_searchResultImageContributorsStream.forEach(
			searchResultImageContributor ->
				searchResultImageContributor.contribute(searchResultImage));
	}

	protected void buildModelResource(
		SearchRelatedResultsDocumentDisplayContext
			searchRelatedResultsDocumentDisplayContext,
		String className) {

		String modelResource = _resourceActions.getModelResource(
			_themeDisplay.getLocale(), className);

		if (!Validator.isBlank(modelResource)) {
			searchRelatedResultsDocumentDisplayContext.setModelResource(
				modelResource);
		}
	}

	protected SearchRelatedResultsDocumentDisplayContext
		buildTemporarilyUnavailable() {

		SearchRelatedResultsDocumentDisplayContext
			searchRelatedResultsDocumentDisplayContext =
				new SearchRelatedResultsDocumentDisplayContext();

		searchRelatedResultsDocumentDisplayContext.setTemporarilyUnavailable(
			true);

		return searchRelatedResultsDocumentDisplayContext;
	}

	protected String formatCreationDate(Date date) {
		Format format = _fastDateFormatFactory.getDateTime(
			FastDateFormatConstants.MEDIUM, FastDateFormatConstants.SHORT,
			_locale, _themeDisplay.getTimeZone());

		return format.format(date);
	}

	protected AssetRenderer<?> getAssetRenderer(
		String className, long classPK,
		AssetRendererFactory<?> assetRendererFactory) {

		try {
			return assetRendererFactory.getAssetRenderer(classPK);
		}
		catch (Exception e) {
			throw new IllegalStateException(
				StringBundler.concat(
					"Unable to get asset renderer for class ", className,
					" with primary key ", classPK),
				e);
		}
	}

	protected AssetRendererFactory<?> getAssetRendererFactoryByClassName(
		String className) {

		return AssetRendererFactoryRegistryUtil.
			getAssetRendererFactoryByClassName(className);
	}

	protected long getEntryClassPK() {
		return getFieldValueLong(Field.ENTRY_CLASS_PK);
	}

	protected long getFieldValueLong(String fieldName) {
		if (_document != null) {
			return GetterUtil.getLong(_document.getLong(fieldName));
		}

		return GetterUtil.getLong(_legacyDocument.get(fieldName));
	}

	protected String getFieldValueString(String fieldName) {
		if (_document != null) {
			return _document.getString(fieldName);
		}

		return _legacyDocument.get(fieldName);
	}

	protected Indexer<Object> getIndexer(String className) {
		if (_indexerRegistry != null) {
			return _indexerRegistry.getIndexer(className);
		}

		return IndexerRegistryUtil.getIndexer(className);
	}

	protected Summary getSummary(
			String className, AssetRenderer<?> assetRenderer)
		throws SearchException {

		SummaryBuilder summaryBuilder = _summaryBuilderFactory.newInstance();

		summaryBuilder.setHighlight(_highlightEnabled);

		Indexer<?> indexer = getIndexer(className);

		if (indexer != null) {
			String snippet = _document.getString(Field.SNIPPET);

			com.liferay.portal.kernel.search.Summary summary =
				indexer.getSummary(
					_legacyDocument, snippet, _renderRequest, _renderResponse);

			if (summary != null) {
				summaryBuilder.setContent(summary.getContent());
				summaryBuilder.setLocale(summary.getLocale());
				summaryBuilder.setMaxContentLength(
					summary.getMaxContentLength());
				summaryBuilder.setTitle(summary.getTitle());

				return summaryBuilder.build();
			}
			else if (assetRenderer != null) {
				summaryBuilder.setContent(
					assetRenderer.getSearchSummary(_locale));
				summaryBuilder.setLocale(_locale);
				summaryBuilder.setTitle(assetRenderer.getTitle(_locale));

				return summaryBuilder.build();
			}
		}
		else if (assetRenderer != null) {
			summaryBuilder.setContent(assetRenderer.getSearchSummary(_locale));
			summaryBuilder.setLocale(_locale);
			summaryBuilder.setTitle(assetRenderer.getTitle(_locale));

			return summaryBuilder.build();
		}

		return null;
	}

	protected Date parseDateStringFieldValue(String dateStringFieldValue) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		try {
			return dateFormat.parse(dateStringFieldValue);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(
				"Unable to parse date string: " + dateStringFieldValue, e);
		}
	}

	private AssetEntryLocalService _assetEntryLocalService;
	private Document _document;
	private DocumentBuilderFactory _documentBuilderFactory;
	private FastDateFormatFactory _fastDateFormatFactory;
	private boolean _highlightEnabled;
	private boolean _imageRequested;
	private IndexerRegistry _indexerRegistry;
	private com.liferay.portal.kernel.search.Document _legacyDocument;
	private Locale _locale;
	private Portal _portal;
	private RenderRequest _renderRequest;
	private RenderResponse _renderResponse;
	private ResourceActions _resourceActions;
	private Stream<SearchResultImageContributor>
		_searchResultImageContributorsStream = Stream.empty();
	private SummaryBuilderFactory _summaryBuilderFactory;
	private ThemeDisplay _themeDisplay;

}