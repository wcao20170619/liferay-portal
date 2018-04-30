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

package com.liferay.portal.search.web.internal.facet.display.builder;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.web.internal.facet.display.context.CustomSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.CustomSearchFacetTermDisplayContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Wade Cao
 */
public class CustomSearchFacetDisplayBuilder {

	public CustomSearchFacetDisplayContext build() {
		boolean nothingSelected = isNothingSelected();

		List<TermCollector> termCollectors = getTermsCollectors();

		boolean renderNothing = false;

		if (nothingSelected && termCollectors.isEmpty()) {
			renderNothing = true;
		}

		String fieldLabel = _fieldLabel;

		if (_fieldLabel.equals(StringPool.BLANK)) {
			fieldLabel = _fieldToAggregate;

			if (_fieldToAggregate.equals(StringPool.BLANK)) {
				fieldLabel = "custom";
			}
		}

		CustomSearchFacetDisplayContext customSearchFacetDisplayContext =
			new CustomSearchFacetDisplayContext();

		customSearchFacetDisplayContext.setFieldLabel(fieldLabel);
		customSearchFacetDisplayContext.setNothingSelected(nothingSelected);
		customSearchFacetDisplayContext.setParamName(_paramName);
		customSearchFacetDisplayContext.setParamValue(getFirstParamValue());
		customSearchFacetDisplayContext.setParamValues(_paramValues);
		customSearchFacetDisplayContext.setRenderNothing(renderNothing);
		customSearchFacetDisplayContext.setTermDisplayContexts(
			buildTermDisplayContexts(termCollectors));

		return customSearchFacetDisplayContext;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setFieldLabel(String fieldLabel) {
		_fieldLabel = fieldLabel;
	}

	public void setFieldToAggregate(String fieldToAggregate) {
		_fieldToAggregate = fieldToAggregate;
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

	public void setParamName(String paramName) {
		_paramName = paramName;
	}

	public void setParamValue(String paramValue) {
		paramValue = StringUtil.trim(Objects.requireNonNull(paramValue));

		if (paramValue.isEmpty()) {
			return;
		}

		_paramValues = Collections.singletonList(paramValue);
	}

	public void setParamValues(List<String> paramValues) {
		_paramValues = paramValues;
	}

	protected CustomSearchFacetTermDisplayContext buildTermDisplayContext(
		TermCollector termCollector) {

		String term = GetterUtil.getString(termCollector.getTerm());

		CustomSearchFacetTermDisplayContext
			customSearchFacetTermDisplayContext =
				new CustomSearchFacetTermDisplayContext();

		customSearchFacetTermDisplayContext.setFrequency(
			termCollector.getFrequency());
		customSearchFacetTermDisplayContext.setFrequencyVisible(
			_frequenciesVisible);
		customSearchFacetTermDisplayContext.setSelected(isSelected(term));
		customSearchFacetTermDisplayContext.setFieldName(term);

		return customSearchFacetTermDisplayContext;
	}

	protected List<CustomSearchFacetTermDisplayContext>
		buildTermDisplayContexts(List<TermCollector> termCollectors) {

		if (termCollectors.isEmpty()) {
			return getEmptyTermDisplayContexts();
		}

		List<CustomSearchFacetTermDisplayContext>
			customSearchFacetTermDisplayContexts = new ArrayList<>(
				termCollectors.size());

		for (int i = 0; i < termCollectors.size(); i++) {
			TermCollector termCollector = termCollectors.get(i);

			if (((_maxTerms > 0) && (i >= _maxTerms)) ||
				((_frequencyThreshold > 0) &&
				 (_frequencyThreshold > termCollector.getFrequency()))) {

				break;
			}

			customSearchFacetTermDisplayContexts.add(
				buildTermDisplayContext(termCollector));
		}

		return customSearchFacetTermDisplayContexts;
	}

	protected List<CustomSearchFacetTermDisplayContext>
		getEmptyTermDisplayContexts() {

		if (_paramValues.isEmpty()) {
			return Collections.emptyList();
		}

		CustomSearchFacetTermDisplayContext
			customSearchFacetTermDisplayContext =
				new CustomSearchFacetTermDisplayContext();

		customSearchFacetTermDisplayContext.setFrequency(0);
		customSearchFacetTermDisplayContext.setFrequencyVisible(
			_frequenciesVisible);
		customSearchFacetTermDisplayContext.setSelected(true);
		customSearchFacetTermDisplayContext.setFieldName(_paramValues.get(0));

		return Collections.singletonList(customSearchFacetTermDisplayContext);
	}

	protected String getFirstParamValue() {
		if (_paramValues.isEmpty()) {
			return StringPool.BLANK;
		}

		return _paramValues.get(0);
	}

	protected List<TermCollector> getTermsCollectors() {
		FacetCollector facetCollector = _facet.getFacetCollector();

		if (facetCollector != null) {
			return facetCollector.getTermCollectors();
		}

		return Collections.<TermCollector>emptyList();
	}

	protected boolean isNothingSelected() {
		if (_paramValues.isEmpty()) {
			return true;
		}

		return false;
	}

	protected boolean isSelected(String value) {
		if (_paramValues.contains(value)) {
			return true;
		}

		return false;
	}

	private Facet _facet;
	private String _fieldLabel;
	private String _fieldToAggregate;
	private boolean _frequenciesVisible;
	private int _frequencyThreshold;
	private int _maxTerms;
	private String _paramName;
	private List<String> _paramValues = Collections.emptyList();

}