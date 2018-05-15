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

package com.liferay.portal.search.elasticsearch6.internal.highlight;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.generic.StringQuery;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.elasticsearch6.internal.ElasticsearchIndexingFixture;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.highlight.BaseHighlightTestCase;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelpers;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class ElasticsearchHighlightMultiLangTest extends BaseHighlightTestCase {

	@Ignore
	@Override
	@Test
	public void testEllipsis() throws Exception {
	}

	//@Test
	public void testEnglishWithUnifiedHighlighter() throws Exception {
		String fieldName = Field.TITLE;

		addDocuments(
			value -> DocumentCreationHelpers.singleText(fieldName, value),
			Arrays.asList(
				"あいうえお　かきくけこ　日本語", 
				"さしすせそ　たちつてと　日本語", 
				"English Japanese AND OR NOT"
			));

		Query query = new StringQuery(fieldName.concat(":English Japanese"));

		assertSearch(
			fieldName, query,
			queryConfig -> queryConfig.setHighlightFragmentSize(40),
			toFullHighlights(
				"<H>English</H> <H>Japanese</H> AND OR NOT"
			));
	}
	
	@Test
	public void testJapaneseWithUnifiedHighlighter() throws Exception {
		String fieldName = Field.TITLE + "_ja_JP";

		addDocuments(
			value -> DocumentCreationHelpers.singleText(fieldName, value),
			Arrays.asList(
				"あいうえお　かきくけこ　日本語", 
				"さしすせそ　たちつてと　日本語", 
				"English Japanese AND OR NOT"
			));

		Query query = new StringQuery(fieldName.concat(":あいうえお日本語"));

		assertSearch(
			fieldName, query,
			queryConfig -> queryConfig.setHighlightFragmentSize(40),
			toFullHighlights(
				"<liferay-hl>あ</liferay-hl>"
				+ "<liferay-hl>い</liferay-hl>" 
				+ "<liferay-hl>う</liferay-hl>"
				+ "<liferay-hl>え</liferay-hl>"
				+ "<liferay-hl>お</liferay-hl>"
				+ "　かきくけこ　<liferay-hl>"
				+ "日</liferay-hl>" 
				+ "<liferay-hl>本</liferay-hl>"
				+ "<liferay-hl>語</liferay-hl>"
				+ ", さしすせそ　たちつてと　"
				+ "<liferay-hl>日</liferay-hl>"
				+ "<liferay-hl>本</liferay-hl>"
				+ "<liferay-hl>語</liferay-hl>"
			));
	}
	
	protected SearchContext createSearchContext() {
		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(COMPANY_ID);
		searchContext.setGroupIds(new long[] {GROUP_ID});
		searchContext.setLocale(LocaleUtil.JAPAN);
		
		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setHitsProcessingEnabled(true);
		queryConfig.setScoreEnabled(false);

		searchContext.setStart(QueryUtil.ALL_POS);

		return searchContext;
	}

	@Override
	protected IndexingFixture createIndexingFixture() {
		return new ElasticsearchIndexingFixture(
			new ElasticsearchFixture(
				ElasticsearchHighlightTest.class.getSimpleName()),
			BaseIndexingTestCase.COMPANY_ID);
	}

}