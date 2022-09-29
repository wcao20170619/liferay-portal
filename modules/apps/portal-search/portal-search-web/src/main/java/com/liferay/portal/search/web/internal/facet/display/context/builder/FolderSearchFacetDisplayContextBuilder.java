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

package com.liferay.portal.search.web.internal.facet.display.context.builder;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetTermDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FolderTitleLookup;
import com.liferay.portal.search.web.internal.facet.display.context.SearchFacetTermDisplayContext;
import com.liferay.portal.search.web.internal.folder.facet.configuration.FolderFacetPortletInstanceConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.RenderRequest;

/**
 * @author Lino Alves
 */
public class FolderSearchFacetDisplayContextBuilder {

	public FolderSearchFacetDisplayContextBuilder(RenderRequest renderRequest)
		throws ConfigurationException {

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		_folderFacetPortletInstanceConfiguration =
			portletDisplay.getPortletInstanceConfiguration(
				FolderFacetPortletInstanceConfiguration.class);
	}

	public FolderSearchFacetDisplayContext build() {
		FolderSearchFacetDisplayContext folderSearchFacetDisplayContext =
			new FolderSearchFacetDisplayContext();

		List<FolderSearchFacetTermDisplayContext>
			folderSearchFacetTermDisplayContexts =
				_buildFolderSearchFacetTermDisplayContexts();

		folderSearchFacetDisplayContext.setDisplayStyleGroupId(
			getDisplayStyleGroupId());
		folderSearchFacetDisplayContext.setFolderSearchFacetTermDisplayContexts(
			folderSearchFacetTermDisplayContexts);
		folderSearchFacetDisplayContext.
			setFolderFacetPortletInstanceConfiguration(
				_folderFacetPortletInstanceConfiguration);
		folderSearchFacetDisplayContext.setNothingSelected(isNothingSelected());
		folderSearchFacetDisplayContext.setPaginationStartParameterName(
			_paginationStartParameterName);
		folderSearchFacetDisplayContext.setParameterName(_parameterName);
		folderSearchFacetDisplayContext.setParameterValue(
			getFirstParameterValueString());
		folderSearchFacetDisplayContext.setParameterValues(
			getParameterValueStrings());
		folderSearchFacetDisplayContext.setRenderNothing(
			isRenderNothing(
				getTermCollectors(), folderSearchFacetTermDisplayContexts));

		return folderSearchFacetDisplayContext;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setFolderTitleLookup(FolderTitleLookup folderTitleLookup) {
		_folderTitleLookup = folderTitleLookup;
	}

	public void setFrequenciesVisible(boolean frequenciesVisible) {
		_frequenciesVisible = frequenciesVisible;
	}

	public void setFrequencyThreshold(int frequencyThreshold) {
		_frequencyThreshold = frequencyThreshold;
	}

	public void setMaxTerms(int maxTerms) {
		_maxTerms = maxTerms;
	}

	public void setOrder(String order) {
		_order = order;
	}

	public void setPaginationStartParameterName(
		String paginationStartParameterName) {

		_paginationStartParameterName = paginationStartParameterName;
	}

	public void setParameterName(String parameterName) {
		_parameterName = parameterName;
	}

	public void setParameterValue(String parameterValue) {
		setParameterValues(GetterUtil.getString(parameterValue));
	}

	public void setParameterValues(String... parameterValues) {
		Objects.requireNonNull(parameterValues);

		_selectedFolderIds = Stream.of(
			parameterValues
		).map(
			GetterUtil::getLong
		).filter(
			folderId -> folderId > 0
		).collect(
			Collectors.toList()
		);
	}

	protected long getDisplayStyleGroupId() {
		long displayStyleGroupId =
			_folderFacetPortletInstanceConfiguration.displayStyleGroupId();

		if (displayStyleGroupId <= 0) {
			displayStyleGroupId = _themeDisplay.getScopeGroupId();
		}

		return displayStyleGroupId;
	}

	protected String getFirstParameterValueString() {
		if (_selectedFolderIds.isEmpty()) {
			return StringPool.BLANK;
		}

		return String.valueOf(_selectedFolderIds.get(0));
	}

	protected List<String> getParameterValueStrings() {
		Stream<Long> groupIdsStream = _selectedFolderIds.stream();

		Stream<String> parameterValuesStream = groupIdsStream.map(
			String::valueOf);

		return parameterValuesStream.collect(Collectors.toList());
	}

	protected List<TermCollector> getTermCollectors() {
		if (_facet == null) {
			return Collections.emptyList();
		}

		FacetCollector facetCollector = _facet.getFacetCollector();

		if (facetCollector == null) {
			return Collections.emptyList();
		}

		return facetCollector.getTermCollectors();
	}

	protected boolean isNothingSelected() {
		if (_selectedFolderIds.isEmpty()) {
			return true;
		}

		return false;
	}

	protected boolean isRenderNothing(
		List<TermCollector> termCollectors,
		List<FolderSearchFacetTermDisplayContext> termDisplayContexts) {

		if ((isNothingSelected() && ListUtil.isEmpty(termCollectors)) ||
			ListUtil.isEmpty(termDisplayContexts)) {

			return true;
		}

		return false;
	}

	protected boolean isSelected(long folderId) {
		if (_selectedFolderIds.contains(folderId)) {
			return true;
		}

		return false;
	}

	private FolderSearchFacetTermDisplayContext
		_buildFolderSearchFacetTermDisplayContext(
			long folderId, String displayName, int frequency,
			boolean selected) {

		FolderSearchFacetTermDisplayContext
			folderSearchFacetTermDisplayContext =
				new FolderSearchFacetTermDisplayContext();

		folderSearchFacetTermDisplayContext.setDisplayName(displayName);
		folderSearchFacetTermDisplayContext.setFolderId(folderId);
		folderSearchFacetTermDisplayContext.setFrequency(frequency);
		folderSearchFacetTermDisplayContext.setFrequencyVisible(
			_frequenciesVisible);
		folderSearchFacetTermDisplayContext.setSelected(selected);

		return folderSearchFacetTermDisplayContext;
	}

	private FolderSearchFacetTermDisplayContext
		_buildFolderSearchFacetTermDisplayContext(TermCollector termCollector) {

		long folderId = GetterUtil.getLong(termCollector.getTerm());

		String displayName = _getDisplayName(folderId);

		if ((folderId == 0) || (displayName == null)) {
			return null;
		}

		return _buildFolderSearchFacetTermDisplayContext(
			folderId, displayName, termCollector.getFrequency(),
			isSelected(folderId));
	}

	private List<FolderSearchFacetTermDisplayContext>
		_buildFolderSearchFacetTermDisplayContexts() {

		List<TermCollector> termCollectors = getTermCollectors();

		if (termCollectors.isEmpty()) {
			return _getEmptyFolderSearchFacetTermDisplayContexts();
		}

		List<FolderSearchFacetTermDisplayContext>
			folderSearchFacetTermDisplayContexts = new ArrayList<>(
				termCollectors.size());

		for (int i = 0; i < termCollectors.size(); i++) {
			if ((_maxTerms > 0) && (i >= _maxTerms)) {
				break;
			}

			TermCollector termCollector = termCollectors.get(i);

			if ((_frequencyThreshold > 0) &&
				(_frequencyThreshold > termCollector.getFrequency())) {

				break;
			}

			FolderSearchFacetTermDisplayContext
				folderSearchFacetTermDisplayContext =
					_buildFolderSearchFacetTermDisplayContext(termCollector);

			if (folderSearchFacetTermDisplayContext != null) {
				folderSearchFacetTermDisplayContexts.add(
					folderSearchFacetTermDisplayContext);
			}
		}

		if (_order.equals("count:asc")) {
			folderSearchFacetTermDisplayContexts.sort(
				SearchFacetTermDisplayContext.COMPARATOR_FREQUENCY_ASC);
		}
		else if (_order.equals("count:desc")) {
			folderSearchFacetTermDisplayContexts.sort(
				SearchFacetTermDisplayContext.COMPARATOR_FREQUENCY_DESC);
		}
		else if (_order.equals("key:asc")) {
			folderSearchFacetTermDisplayContexts.sort(
				SearchFacetTermDisplayContext.COMPARATOR_TERM_ASC);
		}
		else if (_order.equals("key:desc")) {
			folderSearchFacetTermDisplayContexts.sort(
				SearchFacetTermDisplayContext.COMPARATOR_TERM_DESC);
		}

		return folderSearchFacetTermDisplayContexts;
	}

	private String _getDisplayName(long folderId) {
		String title = _folderTitleLookup.getFolderTitle(folderId);

		if (Validator.isNotNull(title)) {
			return title;
		}

		return null;
	}

	private FolderSearchFacetTermDisplayContext
		_getEmptyFolderSearchFacetTermDisplayContext(long folderId) {

		return _buildFolderSearchFacetTermDisplayContext(
			folderId, _getDisplayName(folderId), 0, true);
	}

	private List<FolderSearchFacetTermDisplayContext>
		_getEmptyFolderSearchFacetTermDisplayContexts() {

		Stream<Long> folderIdsStream = _selectedFolderIds.stream();

		Stream<FolderSearchFacetTermDisplayContext>
			folderSearchFacetTermDisplayContextsStream = folderIdsStream.map(
				this::_getEmptyFolderSearchFacetTermDisplayContext);

		return folderSearchFacetTermDisplayContextsStream.collect(
			Collectors.toList());
	}

	private Facet _facet;
	private final FolderFacetPortletInstanceConfiguration
		_folderFacetPortletInstanceConfiguration;
	private FolderTitleLookup _folderTitleLookup;
	private boolean _frequenciesVisible;
	private int _frequencyThreshold;
	private int _maxTerms;
	private String _order;
	private String _paginationStartParameterName;
	private String _parameterName;
	private List<Long> _selectedFolderIds = Collections.emptyList();
	private final ThemeDisplay _themeDisplay;

}