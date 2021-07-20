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

package com.liferay.search.experiences.predict.keyword.index.web.internal.index;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.search.experiences.predict.keyword.index.constants.KeywordEntryStatus;
import com.liferay.search.experiences.predict.keyword.index.index.KeywordEntry;
import com.liferay.search.experiences.predict.keyword.index.web.internal.util.KeywordIndexUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;

/**
 * @author Petteri Karttunen
 */
@Component(service = DocumentToKeywordEntryTranslator.class)
public class DocumentToKeywordEntryTranslatorImpl
	implements DocumentToKeywordEntryTranslator {

	@Override
	public KeywordEntry translate(Document document, String id) {
		return new KeywordEntry.KeywordEntryBuilder().content(
			document.getString(KeywordEntryFields.CONTENT)
		).created(
			KeywordIndexUtil.fromIndexDateString(
				document.getDate(KeywordEntryFields.CREATED))
		).groupId(
			document.getLong(KeywordEntryFields.GROUP_ID)
		).hitCount(
			document.getLong(KeywordEntryFields.HIT_COUNT)
		).languageId(
			document.getString(KeywordEntryFields.LANGUAGE_ID)
		).lastAccessed(
			KeywordIndexUtil.fromIndexDateString(
				document.getDate(KeywordEntryFields.LAST_ACCESSED))
		).lastReported(
			KeywordIndexUtil.fromIndexDateString(
				document.getDate(KeywordEntryFields.LAST_REPORTED))
		).modified(
			KeywordIndexUtil.fromIndexDateString(
				document.getDate(KeywordEntryFields.MODIFIED))
		).keywordEntryId(
			id
		).reportCount(
			document.getLong(KeywordEntryFields.REPORT_COUNT)
		).reports(
			document.getStrings(KeywordEntryFields.REPORTS)
		).status(
			_parseStatusFieldValue(
				document.getString(KeywordEntryFields.STATUS))
		).statusDate(
			KeywordIndexUtil.fromIndexDateString(
				document.getDate(KeywordEntryFields.STATUS_DATE))
		).build();
	}

	@Override
	public List<KeywordEntry> translateAll(SearchHits searchHits) {
		List<SearchHit> list = searchHits.getSearchHits();

		Stream<SearchHit> stream = list.stream();

		return stream.map(
			searchHit -> translate(searchHit.getDocument(), searchHit.getId())
		).collect(
			Collectors.toList()
		);
	}

	protected KeywordEntry.KeywordEntryBuilder builder() {
		return new KeywordEntry.KeywordEntryBuilder();
	}

	private KeywordEntryStatus _parseStatusFieldValue(String status) {
		try {
			return KeywordEntryStatus.valueOf(StringUtil.toUpperCase(status));
		}
		catch (IllegalArgumentException illegalArgumentException) {
			_log.error(
				illegalArgumentException.getMessage(),
				illegalArgumentException);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DocumentToKeywordEntryTranslatorImpl.class);

}