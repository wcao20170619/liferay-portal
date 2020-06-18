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

package com.liferay.blogs.internal.search.spi.model.query.contributor;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.search.query.QueryHelper;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Gamarra
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.blogs.model.BlogsEntry",
	service = KeywordQueryContributor.class
)
public class BlogsEntryKeywordQueryContributor
	implements KeywordQueryContributor {

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		BooleanQuery searchTermQuery = new BooleanQueryImpl();

		_queryHelper.addSearchLocalizedTerm(
			searchTermQuery, searchContext, Field.CONTENT, false);
		_queryHelper.addSearchTerm(
			searchTermQuery, searchContext, Field.SUBTITLE, false);
		_queryHelper.addSearchLocalizedTerm(
			searchTermQuery, searchContext, Field.TITLE, false);

		BooleanQuery entryClassQuery = new BooleanQueryImpl();

		try {
			entryClassQuery.add(
				new TermQueryImpl(
					Field.ENTRY_CLASS_NAME, BlogsEntry.class.getName()),
				BooleanClauseOccur.MUST);
			entryClassQuery.add(searchTermQuery, BooleanClauseOccur.MUST);

			booleanQuery.add(entryClassQuery, BooleanClauseOccur.SHOULD);
		}
		catch (ParseException parseException) {
			throw new SystemException(parseException);
		}
	}

	@Reference
	private QueryHelper _queryHelper;

}