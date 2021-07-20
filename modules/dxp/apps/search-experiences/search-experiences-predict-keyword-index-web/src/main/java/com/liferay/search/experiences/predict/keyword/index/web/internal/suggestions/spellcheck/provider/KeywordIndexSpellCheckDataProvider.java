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

package com.liferay.search.experiences.predict.keyword.index.web.internal.suggestions.spellcheck.provider;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.MultiMatchQuery;
import com.liferay.portal.search.query.Operator;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.search.experiences.predict.keyword.index.configuration.KeywordIndexConfiguration;
import com.liferay.search.experiences.predict.keyword.index.index.name.KeywordIndexNameBuilder;
import com.liferay.search.experiences.predict.keyword.index.web.internal.index.KeywordEntryFields;
import com.liferay.search.experiences.predict.keyword.index.web.internal.util.SuggestionsDataProviderHelper;
import com.liferay.search.experiences.predict.suggestions.attributes.SuggestionsAttributes;
import com.liferay.search.experiences.predict.suggestions.spi.provider.SpellCheckDataProvider;
import com.liferay.search.experiences.predict.suggestions.suggestion.Suggestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "com.liferay.search.experiences.blueprints.keyword.index.configuration.KeywordIndexConfiguration",
	immediate = true, property = "name=keyword_index",
	service = SpellCheckDataProvider.class
)
public class KeywordIndexSpellCheckDataProvider
	implements SpellCheckDataProvider {

	@Override
	public List<Suggestion> getSuggestions(
		SuggestionsAttributes suggestionsAttributes) {

		if (!_keywordIndexConfiguration.enableSpellcheckDataProvider()) {
			return new ArrayList<>();
		}

		SearchHits searchHits = _suggestionsProviderHelper.search(
			_getQuery(suggestionsAttributes), suggestionsAttributes);

		if (searchHits.getTotalHits() == 0) {
			return new ArrayList<>();
		}

		return _suggestionsProviderHelper.getSuggestions(
			searchHits.getSearchHits());
	}

	@Override
	public int getWeight() {
		return _keywordIndexConfiguration.spellCheckDataProviderWeight();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_keywordIndexConfiguration = ConfigurableUtil.createConfigurable(
			KeywordIndexConfiguration.class, properties);
	}

	private void _addSearchClauses(
		BooleanQuery booleanQuery,
		SuggestionsAttributes suggestionsAttributes) {

		MultiMatchQuery multiMatchQuery = _queries.multiMatch(
			suggestionsAttributes.getKeywords(),
			_getFields(suggestionsAttributes));

		multiMatchQuery.setFuzziness("1");
		multiMatchQuery.setOperator(Operator.AND);

		booleanQuery.addMustQueryClauses(multiMatchQuery);
	}

	private Map<String, Float> _getFields(
		SuggestionsAttributes suggestionsAttributes) {

		Map<String, Float> fieldsBoosts = HashMapBuilder.put(
			KeywordEntryFields.CONTENT, 1.0F
		).put(
			KeywordEntryFields.CONTENT + "._2gram", 1.0F
		).put(
			KeywordEntryFields.CONTENT + "._3gram", 1.0F
		).put(
			KeywordEntryFields.CONTENT + "_snowball", 2.0F
		).build();

		_suggestionsProviderHelper.addLocalizedFields(
			fieldsBoosts, suggestionsAttributes.getLanguageId());

		return fieldsBoosts;
	}

	private Query _getQuery(SuggestionsAttributes suggestionsAttributes) {
		BooleanQuery booleanQuery = _queries.booleanQuery();

		_suggestionsProviderHelper.addGroupFilterClause(
			booleanQuery, suggestionsAttributes);

		_suggestionsProviderHelper.addStatusFilterClause(
			booleanQuery, suggestionsAttributes);

		_addSearchClauses(booleanQuery, suggestionsAttributes);

		_suggestionsProviderHelper.addLanguageBoosterClause(
			booleanQuery, suggestionsAttributes);

		return booleanQuery;
	}

	private volatile KeywordIndexConfiguration _keywordIndexConfiguration;

	@Reference
	private KeywordIndexNameBuilder _keywordIndexNameBuilder;

	@Reference
	private Queries _queries;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private SuggestionsDataProviderHelper _suggestionsProviderHelper;

}