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
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.facet.display.context.AssetTagsSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.AssetTagsSearchFacetTermDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.SearchFacetTermDisplayContext;
import com.liferay.portal.search.web.internal.tag.facet.configuration.TagFacetPortletInstanceConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.portlet.RenderRequest;

/**
 * @author Lino Alves
 */
public class AssetTagsSearchFacetDisplayContextBuilder {

	public AssetTagsSearchFacetDisplayContextBuilder(
			RenderRequest renderRequest)
		throws ConfigurationException {

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		_tagFacetPortletInstanceConfiguration =
			portletDisplay.getPortletInstanceConfiguration(
				TagFacetPortletInstanceConfiguration.class);
	}

	public AssetTagsSearchFacetDisplayContext build() {
		AssetTagsSearchFacetDisplayContext assetTagsSearchFacetDisplayContext =
			new AssetTagsSearchFacetDisplayContext();

		assetTagsSearchFacetDisplayContext.setCloudWithCount(
			_isCloudWithCount());
		assetTagsSearchFacetDisplayContext.setDisplayStyleGroupId(
			getDisplayStyleGroupId());
		assetTagsSearchFacetDisplayContext.setFacetLabel(_getFacetLabel());
		assetTagsSearchFacetDisplayContext.setNothingSelected(
			isNothingSelected());
		assetTagsSearchFacetDisplayContext.setPaginationStartParameterName(
			_paginationStartParameterName);
		assetTagsSearchFacetDisplayContext.setParameterName(_parameterName);
		assetTagsSearchFacetDisplayContext.setParameterValue(
			getFirstParameterValue());
		assetTagsSearchFacetDisplayContext.setParameterValues(_selectedTags);
		assetTagsSearchFacetDisplayContext.setRenderNothing(isRenderNothing());
		assetTagsSearchFacetDisplayContext.
			setTagFacetPortletInstanceConfiguration(
				_tagFacetPortletInstanceConfiguration);
		assetTagsSearchFacetDisplayContext.setTermDisplayContexts(
			buildTermDisplayContexts());

		return assetTagsSearchFacetDisplayContext;
	}

	public long getDisplayStyleGroupId() {
		long displayStyleGroupId =
			_tagFacetPortletInstanceConfiguration.displayStyleGroupId();

		if (displayStyleGroupId <= 0) {
			displayStyleGroupId = _themeDisplay.getScopeGroupId();
		}

		return displayStyleGroupId;
	}

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
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
		parameterValue = StringUtil.trim(
			Objects.requireNonNull(parameterValue));

		if (parameterValue.isEmpty()) {
			return;
		}

		_selectedTags = Collections.singletonList(parameterValue);
	}

	public void setParameterValues(String... parameterValues) {
		_selectedTags = Arrays.asList(parameterValues);
	}

	protected AssetTagsSearchFacetTermDisplayContext buildTermDisplayContext(
		TermCollector termCollector, int maxCount, int minCount,
		double multiplier) {

		int frequency = termCollector.getFrequency();

		int popularity = (int)getPopularity(
			frequency, minCount, maxCount, multiplier);

		String value = termCollector.getTerm();

		AssetTagsSearchFacetTermDisplayContext
			assetTagsSearchFacetTermDisplayContext =
				new AssetTagsSearchFacetTermDisplayContext();

		assetTagsSearchFacetTermDisplayContext.setFrequency(frequency);
		assetTagsSearchFacetTermDisplayContext.setFrequencyVisible(
			_frequenciesVisible);
		assetTagsSearchFacetTermDisplayContext.setPopularity(popularity);
		assetTagsSearchFacetTermDisplayContext.setSelected(isSelected(value));
		assetTagsSearchFacetTermDisplayContext.setValue(value);

		return assetTagsSearchFacetTermDisplayContext;
	}

	protected List<AssetTagsSearchFacetTermDisplayContext>
		buildTermDisplayContexts() {

		List<TermCollector> termCollectors = getTermCollectors();

		if (termCollectors.isEmpty()) {
			return getEmptySearchResultTermDisplayContexts();
		}

		List<AssetTagsSearchFacetTermDisplayContext>
			assetTagsSearchFacetTermDisplayContexts = new ArrayList<>(
				termCollectors.size());

		int maxCount = 1;
		int minCount = 1;

		if (_isCloudWithCount()) {

			// The cloud style may not list tags in the order of frequency.
			// Keep looking through the results until we reach the maximum
			// number of terms or we run out of terms

			for (int i = 0, j = 0; i < termCollectors.size(); i++, j++) {
				if ((_maxTerms > 0) && (j >= _maxTerms)) {
					break;
				}

				TermCollector termCollector = termCollectors.get(i);

				int frequency = termCollector.getFrequency();

				if ((_frequencyThreshold > 0) &&
					(_frequencyThreshold > frequency)) {

					j--;

					continue;
				}

				maxCount = Math.max(maxCount, frequency);
				minCount = Math.min(minCount, frequency);
			}
		}

		double multiplier = 1;

		if (maxCount != minCount) {
			multiplier = (double)5 / (maxCount - minCount);
		}

		for (int i = 0, j = 0; i < termCollectors.size(); i++, j++) {
			if ((_maxTerms > 0) && (j >= _maxTerms)) {
				break;
			}

			TermCollector termCollector = termCollectors.get(i);

			int frequency = termCollector.getFrequency();

			if ((_frequencyThreshold > 0) &&
				(_frequencyThreshold > frequency)) {

				j--;

				continue;
			}

			AssetTagsSearchFacetTermDisplayContext
				assetTagsSearchFacetTermDisplayContext =
					buildTermDisplayContext(
						termCollector, maxCount, minCount, multiplier);

			if (assetTagsSearchFacetTermDisplayContext != null) {
				assetTagsSearchFacetTermDisplayContexts.add(
					assetTagsSearchFacetTermDisplayContext);
			}
		}

		if (_order.equals("count:asc")) {
			assetTagsSearchFacetTermDisplayContexts.sort(
				SearchFacetTermDisplayContext.COMPARATOR_FREQUENCY_ASC);
		}
		else if (_order.equals("count:desc")) {
			assetTagsSearchFacetTermDisplayContexts.sort(
				SearchFacetTermDisplayContext.COMPARATOR_FREQUENCY_DESC);
		}
		else if (_order.equals("key:asc")) {
			assetTagsSearchFacetTermDisplayContexts.sort(
				SearchFacetTermDisplayContext.COMPARATOR_TERM_ASC);
		}
		else if (_order.equals("key:desc")) {
			assetTagsSearchFacetTermDisplayContexts.sort(
				SearchFacetTermDisplayContext.COMPARATOR_TERM_DESC);
		}

		return assetTagsSearchFacetTermDisplayContexts;
	}

	protected List<AssetTagsSearchFacetTermDisplayContext>
		getEmptySearchResultTermDisplayContexts() {

		if (_selectedTags.isEmpty()) {
			return Collections.emptyList();
		}

		AssetTagsSearchFacetTermDisplayContext
			assetTagsSearchFacetTermDisplayContext =
				new AssetTagsSearchFacetTermDisplayContext();

		assetTagsSearchFacetTermDisplayContext.setFrequency(0);
		assetTagsSearchFacetTermDisplayContext.setFrequencyVisible(
			_frequenciesVisible);
		assetTagsSearchFacetTermDisplayContext.setPopularity(0);
		assetTagsSearchFacetTermDisplayContext.setSelected(true);
		assetTagsSearchFacetTermDisplayContext.setValue(_selectedTags.get(0));

		return Collections.singletonList(
			assetTagsSearchFacetTermDisplayContext);
	}

	protected String getFirstParameterValue() {
		if (_selectedTags.isEmpty()) {
			return StringPool.BLANK;
		}

		return _selectedTags.get(0);
	}

	protected double getPopularity(
		int frequency, int minCount, int maxCount, double multiplier) {

		double popularity = maxCount - (maxCount - (frequency - minCount));

		return 1 + (popularity * multiplier);
	}

	protected List<TermCollector> getTermCollectors() {
		if (_facet == null) {
			return Collections.emptyList();
		}

		FacetCollector facetCollector = _facet.getFacetCollector();

		return facetCollector.getTermCollectors();
	}

	protected boolean isNothingSelected() {
		if (_selectedTags.isEmpty()) {
			return true;
		}

		return false;
	}

	protected boolean isRenderNothing() {
		List<TermCollector> termCollectors = getTermCollectors();

		if (isNothingSelected() && termCollectors.isEmpty()) {
			return true;
		}

		return false;
	}

	protected boolean isSelected(String value) {
		if (_selectedTags.contains(value)) {
			return true;
		}

		return false;
	}

	private String _getFacetLabel() {
		if (_facet != null) {
			FacetConfiguration facetConfiguration =
				_facet.getFacetConfiguration();

			if (facetConfiguration != null) {
				return facetConfiguration.getLabel();
			}

			return StringPool.BLANK;
		}

		return StringPool.BLANK;
	}

	private boolean _isCloudWithCount() {
		if (_frequenciesVisible && _displayStyle.equals("cloud")) {
			return true;
		}

		return false;
	}

	private String _displayStyle;
	private Facet _facet;
	private boolean _frequenciesVisible;
	private int _frequencyThreshold;
	private int _maxTerms;
	private String _order;
	private String _paginationStartParameterName;
	private String _parameterName;
	private List<String> _selectedTags = Collections.emptyList();
	private final TagFacetPortletInstanceConfiguration
		_tagFacetPortletInstanceConfiguration;
	private final ThemeDisplay _themeDisplay;

}