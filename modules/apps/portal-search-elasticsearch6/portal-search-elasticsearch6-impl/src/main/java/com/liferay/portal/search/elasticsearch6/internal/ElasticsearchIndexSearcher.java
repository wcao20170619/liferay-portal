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

package com.liferay.portal.search.elasticsearch6.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexSearcher;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.HitsImpl;
import com.liferay.portal.kernel.search.IndexSearcher;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.suggest.QuerySuggester;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.elasticsearch6.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch6.internal.index.IndexNameBuilder;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.CountSearchRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;

import java.util.Map;

import org.apache.commons.lang.time.StopWatch;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Milen Dyankov
 */
@Component(
	configurationPid = "com.liferay.portal.search.elasticsearch6.configuration.ElasticsearchConfiguration",
	immediate = true, property = "search.engine.impl=Elasticsearch",
	service = IndexSearcher.class
)
public class ElasticsearchIndexSearcher extends BaseIndexSearcher {

	@Override
	public String getQueryString(SearchContext searchContext, Query query) {
		return searchEngineAdapter.getQueryString(query);
	}

	@Override
	public Hits search(SearchContext searchContext, Query query)
		throws SearchException {

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		try {
			int start = searchContext.getStart();
			int end = searchContext.getEnd();

			if (start == QueryUtil.ALL_POS) {
				start = 0;
			}
			else if (start < 0) {
				throw new IllegalArgumentException("Invalid start " + start);
			}

			if (end == QueryUtil.ALL_POS) {
				end = GetterUtil.getInteger(
					props.get(PropsKeys.INDEX_SEARCH_LIMIT));
			}
			else if (end < 0) {
				throw new IllegalArgumentException("Invalid end " + end);
			}

			Hits hits = null;

			while (true) {
				SearchSearchRequest searchSearchRequest =
					new SearchSearchRequest();

				QueryConfig queryConfig = searchContext.getQueryConfig();

				searchSearchRequest.setAlternateUidFieldName(
					queryConfig.getAlternateUidFieldName());

				boolean basicFacetSelection = GetterUtil.getBoolean(
					searchContext.getAttribute(
						SearchContextAttributes.
							ATTRIBUTE_KEY_BASIC_FACET_SELECTION));

				searchSearchRequest.setBasicFacetSelection(basicFacetSelection);

				String[] indexNames = getSelectedIndexNames(
					queryConfig, searchContext);

				searchSearchRequest.setIndexNames(indexNames);

				searchSearchRequest.putAllFacets(searchContext.getFacets());

				searchSearchRequest.setGroupBy(searchContext.getGroupBy());

				searchSearchRequest.setHighlightEnabled(
					queryConfig.isHighlightEnabled());
				searchSearchRequest.setHighlightFieldNames(
					queryConfig.getHighlightFieldNames());
				searchSearchRequest.setHighlightFragmentSize(
					queryConfig.getHighlightFragmentSize());
				searchSearchRequest.setHighlightSnippetSize(
					queryConfig.getHighlightSnippetSize());
				searchSearchRequest.setLocale(queryConfig.getLocale());
				searchSearchRequest.setHighlightRequireFieldMatch(
					queryConfig.isHighlightRequireFieldMatch());

				boolean luceneSyntax = GetterUtil.getBoolean(
					searchContext.getAttribute(
						SearchContextAttributes.ATTRIBUTE_KEY_LUCENE_SYNTAX));

				searchSearchRequest.setLuceneSyntax(luceneSyntax);

				searchSearchRequest.setQuery(query);
				searchSearchRequest.setPostFilter(query.getPostFilter());

				searchSearchRequest.setScoreEnabled(
					queryConfig.isScoreEnabled());
				searchSearchRequest.setSelectedFieldNames(
					queryConfig.getSelectedFieldNames());

				int size = end - start;

				searchSearchRequest.setSize(size);

				searchSearchRequest.setStart(start);

				searchSearchRequest.setSorts(searchContext.getSorts());
				searchSearchRequest.setStats(searchContext.getStats());

				SearchSearchResponse searchSearchResponse =
					searchEngineAdapter.execute(searchSearchRequest);

				searchContext.setAttribute(
					"queryString",
					searchSearchResponse.getSearchRequestString());

				hits = searchSearchResponse.getHits();

				Document[] documents = hits.getDocs();

				if ((documents.length != 0) || (start == 0)) {
					break;
				}

				int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
					start, end, hits.getLength());

				start = startAndEnd[0];
				end = startAndEnd[1];
			}

			hits.setStart(stopWatch.getStartTime());

			return hits;
		}
		catch (Exception e) {
			if (!handle(e)) {
				if (_logExceptionsOnly) {
					if (_log.isWarnEnabled()) {
						_log.warn(e, e);
					}
				}
				else {
					throw new SearchException(e.getMessage(), e);
				}
			}

			return new HitsImpl();
		}
		finally {
			if (_log.isInfoEnabled()) {
				stopWatch.stop();

				_log.info(
					StringBundler.concat(
						"Searching ", query.toString(), " took ",
						stopWatch.getTime(), " ms"));
			}
		}
	}

	@Override
	public long searchCount(SearchContext searchContext, Query query)
		throws SearchException {

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		try {
			CountSearchRequest countSearchRequest = new CountSearchRequest();

			QueryConfig queryConfig = searchContext.getQueryConfig();

			String[] indexNames = getSelectedIndexNames(
				queryConfig, searchContext);

			countSearchRequest.setIndexNames(indexNames);

			countSearchRequest.setPostFilter(query.getPostFilter());
			countSearchRequest.setQuery(query);

			CountSearchResponse countSearchResponse =
				searchEngineAdapter.execute(countSearchRequest);

			searchContext.setAttribute(
				"queryString", countSearchResponse.getSearchRequestString());

			return countSearchResponse.getCount();
		}
		catch (Exception e) {
			if (!handle(e)) {
				if (_logExceptionsOnly) {
					if (_log.isWarnEnabled()) {
						_log.warn(e, e);
					}
				}
				else {
					throw new SearchException(e.getMessage(), e);
				}
			}

			return 0;
		}
		finally {
			if (_log.isInfoEnabled()) {
				stopWatch.stop();

				_log.info(
					StringBundler.concat(
						"Searching ", query.toString(), " took ",
						stopWatch.getTime(), " ms"));
			}
		}
	}

	@Override
	@Reference(target = "(search.engine.impl=Elasticsearch)", unbind = "-")
	public void setQuerySuggester(QuerySuggester querySuggester) {
		super.setQuerySuggester(querySuggester);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_elasticsearchConfiguration = ConfigurableUtil.createConfigurable(
			ElasticsearchConfiguration.class, properties);

		_logExceptionsOnly = _elasticsearchConfiguration.logExceptionsOnly();
	}

	protected String[] getSelectedIndexNames(
		QueryConfig queryConfig, SearchContext searchContext) {

		String[] selectedIndexNames = queryConfig.getSelectedIndexNames();

		if (ArrayUtil.isNotEmpty(selectedIndexNames)) {
			return selectedIndexNames;
		}

		String indexName = indexNameBuilder.getIndexName(
			searchContext.getCompanyId());

		return new String[] {indexName};
	}

	protected boolean handle(Exception e) {
		Throwable throwable = e.getCause();

		if (throwable == null) {
			return false;
		}

		String message = throwable.getMessage();

		if (message == null) {
			return false;
		}

		if (message.contains(
				"Fielddata is disabled on text fields by default.")) {

			_log.error("Unable to aggregate facet on a nonkeyword field", e);

			return true;
		}

		return false;
	}

	@Reference
	protected IndexNameBuilder indexNameBuilder;

	@Reference
	protected Props props;

	@Reference(target = "(search.engine.impl=Elasticsearch)")
	protected SearchEngineAdapter searchEngineAdapter;

	private static final Log _log = LogFactoryUtil.getLog(
		ElasticsearchIndexSearcher.class);

	private volatile ElasticsearchConfiguration _elasticsearchConfiguration;
	private boolean _logExceptionsOnly;

}