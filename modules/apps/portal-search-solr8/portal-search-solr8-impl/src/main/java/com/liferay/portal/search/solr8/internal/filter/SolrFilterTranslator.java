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

package com.liferay.portal.search.solr8.internal.filter;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.DateRangeTermFilter;
import com.liferay.portal.kernel.search.filter.ExistsFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.GeoBoundingBoxFilter;
import com.liferay.portal.kernel.search.filter.GeoDistanceFilter;
import com.liferay.portal.kernel.search.filter.GeoDistanceRangeFilter;
import com.liferay.portal.kernel.search.filter.GeoPolygonFilter;
import com.liferay.portal.kernel.search.filter.MissingFilter;
import com.liferay.portal.kernel.search.filter.PrefixFilter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.filter.RangeTermFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.search.filter.DateRangeFilter;
import com.liferay.portal.search.filter.FilterVisitor;
import com.liferay.portal.search.filter.TermsSetFilter;

import org.apache.lucene.search.Query;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	immediate = true, property = "search.engine.impl=Solr",
	service = FilterTranslator.class
)
public class SolrFilterTranslator
	implements FilterTranslator<String>, FilterVisitor<Query> {

	@Override
	public String translate(Filter filter) {
		String filterString = StringPool.BLANK;

		if (filter != null) {
			Query luceneQuery = filter.accept(this);

			if (luceneQuery != null) {
				filterString = luceneQuery.toString();
			}
		}

		return filterString;
	}

	@Override
	public Query visit(BooleanFilter booleanFilter) {
		return _booleanFilterTranslator.translate(booleanFilter, this);
	}

	@Override
	public Query visit(DateRangeFilter dateRangeFilter) {
		return _dateRangeFilterTranslator.translate(dateRangeFilter);
	}

	@Override
	public Query visit(DateRangeTermFilter dateRangeTermFilter) {
		return _dateRangeTermFilterTranslator.translate(dateRangeTermFilter);
	}

	@Override
	public Query visit(ExistsFilter existsFilter) {
		return _existsFilterTranslator.translate(existsFilter);
	}

	@Override
	public Query visit(GeoBoundingBoxFilter geoBoundingBoxFilter) {
		return _geoBoundingBoxFilterTranslator.translate(geoBoundingBoxFilter);
	}

	@Override
	public Query visit(GeoDistanceFilter geoDistanceFilter) {
		return _geoDistanceFilterTranslator.translate(geoDistanceFilter);
	}

	@Override
	public Query visit(GeoDistanceRangeFilter geoDistanceRangeFilter) {
		return _geoDistanceRangeFilterTranslator.translate(
			geoDistanceRangeFilter);
	}

	@Override
	public Query visit(GeoPolygonFilter geoPolygonFilter) {
		return _geoPolygonFilterTranslator.translate(geoPolygonFilter);
	}

	@Override
	public Query visit(MissingFilter missingFilter) {
		return _missingFilterTranslator.translate(missingFilter);
	}

	@Override
	public Query visit(PrefixFilter prefixFilter) {
		return _prefixFilterTranslator.translate(prefixFilter);
	}

	@Override
	public Query visit(QueryFilter queryFilter) {
		return _queryFilterTranslator.translate(queryFilter);
	}

	@Override
	public Query visit(RangeTermFilter rangeTermFilter) {
		return _rangeTermFilterTranslator.translate(rangeTermFilter);
	}

	@Override
	public Query visit(TermFilter termFilter) {
		return _termFilterTranslator.translate(termFilter);
	}

	@Override
	public Query visit(TermsFilter termsFilter) {
		return _termsFilterTranslator.translate(termsFilter);
	}

	@Override
	public Query visit(TermsSetFilter termsSetFilter) {
		throw new UnsupportedOperationException();
	}

	@Reference
	private BooleanFilterTranslator _booleanFilterTranslator;

	@Reference
	private DateRangeFilterTranslator _dateRangeFilterTranslator;

	@Reference
	private DateRangeTermFilterTranslator _dateRangeTermFilterTranslator;

	@Reference
	private ExistsFilterTranslator _existsFilterTranslator;

	@Reference
	private GeoBoundingBoxFilterTranslator _geoBoundingBoxFilterTranslator;

	@Reference
	private GeoDistanceFilterTranslator _geoDistanceFilterTranslator;

	@Reference
	private GeoDistanceRangeFilterTranslator _geoDistanceRangeFilterTranslator;

	@Reference
	private GeoPolygonFilterTranslator _geoPolygonFilterTranslator;

	@Reference
	private MissingFilterTranslator _missingFilterTranslator;

	@Reference
	private PrefixFilterTranslator _prefixFilterTranslator;

	@Reference
	private QueryFilterTranslator _queryFilterTranslator;

	@Reference
	private RangeTermFilterTranslator _rangeTermFilterTranslator;

	@Reference
	private TermFilterTranslator _termFilterTranslator;

	@Reference
	private TermsFilterTranslator _termsFilterTranslator;

}