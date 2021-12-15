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

package com.liferay.search.experiences.internal.blueprint.search.request.body.contributor;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.filter.ComplexQueryPartBuilder;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.rescore.Rescore.ScoreMode;
import com.liferay.portal.search.rescore.RescoreBuilder;
import com.liferay.portal.search.rescore.RescoreBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.search.experiences.internal.blueprint.condition.SXPConditionEvaluator;
import com.liferay.search.experiences.internal.blueprint.exception.InvalidQueryEntryException;
import com.liferay.search.experiences.internal.blueprint.parameter.SXPParameterData;
import com.liferay.search.experiences.internal.blueprint.query.QueryConverter;
import com.liferay.search.experiences.rest.dto.v1_0.Clause;
import com.liferay.search.experiences.rest.dto.v1_0.Condition;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.QueryConfiguration;
import com.liferay.search.experiences.rest.dto.v1_0.QueryEntry;
import com.liferay.search.experiences.rest.dto.v1_0.Rescore;

import java.beans.ExceptionListener;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Petteri Karttunen
 */
public class QuerySXPSearchRequestBodyContributor
	implements SXPSearchRequestBodyContributor {

	public QuerySXPSearchRequestBodyContributor(
		ComplexQueryPartBuilderFactory complexQueryPartBuilderFactory,
		QueryConverter queryConverter,
		RescoreBuilderFactory rescoreBuilderFactory) {

		_complexQueryPartBuilderFactory = complexQueryPartBuilderFactory;
		_queryConverter = queryConverter;
		_rescoreBuilderFactory = rescoreBuilderFactory;
	}

	@Override
	public void contribute(
		Configuration configuration, SearchRequestBuilder searchRequestBuilder,
		SXPParameterData sxpParameterData) {

		QueryConfiguration queryConfiguration =
			configuration.getQueryConfiguration();

		if (queryConfiguration == null) {
			return;
		}

		RuntimeException runtimeException = new RuntimeException();

		_processQueryEntries(
			runtimeException::addSuppressed,
			queryConfiguration.getQueryEntries(), searchRequestBuilder,
			sxpParameterData);

		if (ArrayUtil.isNotEmpty(runtimeException.getSuppressed())) {
			throw runtimeException;
		}

		if (queryConfiguration.getApplyIndexerClauses() != null) {
			searchRequestBuilder.withSearchContext(
				searchContext -> searchContext.setAttribute(
					"search.full.query.suppress.indexer.provided.clauses",
					!queryConfiguration.getApplyIndexerClauses()));
		}
	}

	@Override
	public String getName() {
		return "query";
	}

	private boolean _evaluate(
		Condition condition, SXPParameterData sxpParameterData) {

		if (condition == null) {
			return true;
		}

		SXPConditionEvaluator sxpConditionEvaluator = new SXPConditionEvaluator(
			sxpParameterData);

		return sxpConditionEvaluator.evaluate(condition);
	}

	private <X, Y> void _process(
		Consumer<Y> consumer, ExceptionListener exceptionListener,
		Function<X, Y> function, X[] objects) {

		ArrayUtil.isNotEmptyForEach(
			objects,
			object -> {
				try {
					consumer.accept(function.apply(object));
				}
				catch (Exception exception) {
					exceptionListener.exceptionThrown(exception);
				}
			});
	}

	@SuppressWarnings("unchecked")
	private JSONObject _processFieldLocaleAndBoost(JSONObject queryJSONObject) {
		if (queryJSONObject == null) {
			return queryJSONObject;
		}

		Iterator<String> iterator = queryJSONObject.keys();

		if (!iterator.hasNext()) {
			return queryJSONObject;
		}

		JSONObject valueJSONObject = queryJSONObject.getJSONObject(
			iterator.next());

		JSONArray fieldsJSONArray = valueJSONObject.getJSONArray("fields");

		if (fieldsJSONArray == null) {
			return queryJSONObject;
		}

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		fieldsJSONArray.forEach(
			fieldJSONObject -> {
				JSONObject jsonObject = (JSONObject)fieldJSONObject;

				String locale = jsonObject.getString("locale");

				if (!Validator.isBlank(locale)) {
					String field = jsonObject.getString("field") + locale;

					String boost = jsonObject.getString("boost");

					if (!Validator.isBlank(boost)) {
						field += "^" + boost;
					}

					jsonArray.put(field);
				}
			});

		if (jsonArray.length() > 0) {
			valueJSONObject.put("fields", jsonArray);
		}

		return queryJSONObject;
	}

	private void _processQueryEntries(
		ExceptionListener exceptionListener, QueryEntry[] queryEntries,
		SearchRequestBuilder searchRequestBuilder,
		SXPParameterData sxpParameterData) {

		if (ArrayUtil.isEmpty(queryEntries)) {
			return;
		}

		for (int index = 0; index < queryEntries.length; index++) {
			try {
				_processQueryEntry(
					index, queryEntries[index], searchRequestBuilder,
					sxpParameterData);
			}
			catch (Exception exception) {
				exceptionListener.exceptionThrown(exception);
			}
		}
	}

	private void _processQueryEntry(
		int index, QueryEntry queryEntry,
		SearchRequestBuilder searchRequestBuilder,
		SXPParameterData sxpParameterData) {

		if (!GetterUtil.getBoolean(queryEntry.getEnabled(), true) ||
			!_evaluate(queryEntry.getCondition(), sxpParameterData)) {

			return;
		}

		InvalidQueryEntryException invalidQueryEntryException =
			InvalidQueryEntryException.at(index);

		ExceptionListener exceptionListener =
			invalidQueryEntryException::addSuppressed;

		_process(
			searchRequestBuilder::addComplexQueryPart, exceptionListener,
			this::_toComplexQueryPart, queryEntry.getClauses());
		_process(
			searchRequestBuilder::addPostFilterQueryPart, exceptionListener,
			this::_toComplexQueryPart, queryEntry.getPostFilterClauses());
		_process(
			searchRequestBuilder::addRescore, exceptionListener,
			this::_toRescore, queryEntry.getRescores());

		if (ArrayUtil.isNotEmpty(invalidQueryEntryException.getSuppressed())) {
			throw invalidQueryEntryException;
		}
	}

	private ComplexQueryPart _toComplexQueryPart(Clause clause) {
		ComplexQueryPartBuilder complexQueryPartBuilder =
			_complexQueryPartBuilderFactory.builder();

		if (clause.getAdditive() != null) {
			complexQueryPartBuilder.additive(clause.getAdditive());
		}

		if (clause.getDisabled() != null) {
			complexQueryPartBuilder.disabled(clause.getDisabled());
		}

		return complexQueryPartBuilder.boost(
			clause.getBoost()
		).field(
			clause.getField()
		).name(
			clause.getName()
		).occur(
			clause.getOccur()
		).parent(
			clause.getParent()
		).query(
			_queryConverter.toQuery(
				_processFieldLocaleAndBoost((JSONObject)clause.getQuery()))
		).type(
			clause.getType()
		).value(
			clause.getValue()
		).build();
	}

	private com.liferay.portal.search.rescore.Rescore _toRescore(
		Rescore rescore) {

		RescoreBuilder rescoreBuilder = _rescoreBuilderFactory.builder(
			_queryConverter.toQuery((JSONObject)rescore.getQuery()));

		if (rescore.getScoreMode() != null) {
			rescoreBuilder.scoreMode(
				ScoreMode.valueOf(
					StringUtil.toUpperCase(rescore.getScoreMode())));
		}

		return rescoreBuilder.queryWeight(
			rescore.getQueryWeight()
		).rescoreQueryWeight(
			rescore.getRescoreQueryWeight()
		).windowSize(
			rescore.getWindowSize()
		).build();
	}

	private final ComplexQueryPartBuilderFactory
		_complexQueryPartBuilderFactory;
	private final QueryConverter _queryConverter;
	private final RescoreBuilderFactory _rescoreBuilderFactory;

}