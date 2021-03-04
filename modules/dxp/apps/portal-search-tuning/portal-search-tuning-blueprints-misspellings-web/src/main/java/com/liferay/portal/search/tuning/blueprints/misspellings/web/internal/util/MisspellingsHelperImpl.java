/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.blueprints.misspellings.web.internal.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.CountSearchRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.tuning.blueprints.misspellings.index.MisspellingSet;
import com.liferay.portal.search.tuning.blueprints.misspellings.index.name.MisspellingSetIndexNameBuilder;
import com.liferay.portal.search.tuning.blueprints.misspellings.util.MisspellingsHelper;
import com.liferay.portal.search.tuning.blueprints.misspellings.web.internal.index.DocumentToMisspellingSetTranslator;
import com.liferay.portal.search.tuning.blueprints.misspellings.web.internal.index.MisspellingSetIndexReader;
import com.liferay.portal.search.tuning.blueprints.misspellings.web.internal.index.MisspellingSetIndexWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = MisspellingsHelper.class)
public class MisspellingsHelperImpl implements MisspellingsHelper {

	@Override
	public void addMisspellingSet(
		long companyId, long groupId, String languageId, String phrase,
		List<String> misspellings) {

		_misspellingSetIndexWriter.create(
			_misspellingSetIndexNameBuilder.getMisspellingSetIndexName(
				companyId),
			new MisspellingSet.MisspellingSetBuilder().created(
				new Date()
			).groupId(
				groupId
			).languageId(
				languageId
			).misspellings(
				misspellings
			).modified(
				new Date()
			).phrase(
				phrase
			).build());
	}

	@Override
	public void deleteCompanyMisspellings(long companyId) {
		int count = GetterUtil.getInteger(
			getCompanyMisspellingsCount(companyId));

		if (count == 0) {
			return;
		}

		SearchHits searchHits = _searchCompanyMisspellings(companyId, 0, count);

		_deleteCompanyMisspellings(companyId, searchHits);
	}

	@Override
	public List<MisspellingSet> getCompanyMisspellings(long companyId) {
		SearchHits searchHits = _searchCompanyMisspellings(
			companyId, 0, getCompanyMisspellingsCount(companyId));

		if (searchHits.getTotalHits() == 0) {
			return Collections.emptyList();
		}

		return _translateToMisspellings(searchHits);
	}

	@Override
	public int getCompanyMisspellingsCount(long companyId) {
		CountSearchRequest countSearchRequest = new CountSearchRequest();

		countSearchRequest.setIndexNames(
			_misspellingSetIndexNameBuilder.getMisspellingSetIndexName(
				companyId
			).getIndexName());
		countSearchRequest.setQuery(_queries.matchAll());

		CountSearchResponse countSearchResponse = _searchEngineAdapter.execute(
			countSearchRequest);

		return GetterUtil.getInteger(countSearchResponse.getCount());
	}

	@Override
	public void updateMisspellingSet(
		long companyId, long groupId, String misspellingSetId,
		String languageId, String phrase, List<String> misspellings) {

		_misspellingSetIndexWriter.update(
			_misspellingSetIndexNameBuilder.getMisspellingSetIndexName(
				companyId),
			new MisspellingSet.MisspellingSetBuilder().groupId(
				groupId
			).languageId(
				languageId
			).misspellings(
				misspellings
			).misspellingSetId(
				misspellingSetId
			).modified(
				new Date()
			).phrase(
				phrase
			).build());
	}

	private void _deleteCompanyMisspellings(
		long companyId, SearchHits searchHits) {

		List<SearchHit> hits = searchHits.getSearchHits();

		Stream<SearchHit> stream = hits.stream();

		stream.forEach(
			s -> _misspellingSetIndexWriter.remove(
				_misspellingSetIndexNameBuilder.getMisspellingSetIndexName(
					companyId),
				s.getId()));
	}

	private SearchHits _searchCompanyMisspellings(
		long companyId, int start, int size) {

		return _searchCompanyMisspellings(
			companyId, _queries.matchAll(), start, size);
	}

	private SearchHits _searchCompanyMisspellings(
		long companyId, Query query, int start, int size) {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setIndexNames(
			_misspellingSetIndexNameBuilder.getMisspellingSetIndexName(
				companyId
			).getIndexName());
		searchSearchRequest.setPreferLocalCluster(false);
		searchSearchRequest.setQuery(query);
		searchSearchRequest.setSize(size);
		searchSearchRequest.setStart(start);

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		return searchSearchResponse.getSearchHits();
	}

	private List<MisspellingSet> _translateToMisspellings(
		SearchHits searchHits) {

		List<MisspellingSet> misspellings = new ArrayList<>();

		List<SearchHit> hits = searchHits.getSearchHits();

		Stream<SearchHit> stream = hits.stream();

		stream.forEach(
			s -> misspellings.add(
				_documentToMisspellingSetTranslator.translate(
					s.getDocument(), s.getId())));

		return misspellings;
	}

	@Reference
	private DocumentToMisspellingSetTranslator
		_documentToMisspellingSetTranslator;

	@Reference
	private MisspellingSetIndexNameBuilder _misspellingSetIndexNameBuilder;

	@Reference
	private MisspellingSetIndexReader _misspellingSetIndexReader;

	@Reference
	private MisspellingSetIndexWriter _misspellingSetIndexWriter;

	@Reference
	private MisspellingsQueryHelper _misspellingsQueryHelper;

	@Reference
	private Queries _queries;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}