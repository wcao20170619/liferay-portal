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

import com.liferay.portal.kernel.search.IndexSearcher;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.suggest.QuerySuggester;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch6.internal.connection.IndexCreator;
import com.liferay.portal.search.elasticsearch6.internal.connection.IndexName;
import com.liferay.portal.search.elasticsearch6.internal.document.DefaultElasticsearchDocumentFactory;
import com.liferay.portal.search.elasticsearch6.internal.document.ElasticsearchDocumentFactory;
import com.liferay.portal.search.elasticsearch6.internal.facet.DefaultFacetProcessor;
import com.liferay.portal.search.elasticsearch6.internal.facet.FacetProcessor;
import com.liferay.portal.search.elasticsearch6.internal.field.ElasticsearchFieldRegistryListener;
import com.liferay.portal.search.elasticsearch6.internal.field.FieldRegistrySynchronizer;
import com.liferay.portal.search.elasticsearch6.internal.field.FieldRegistrySynchronizerImpl;
import com.liferay.portal.search.elasticsearch6.internal.index.IndexNameBuilder;
import com.liferay.portal.search.elasticsearch6.internal.index.create.CreateIndexContributor;
import com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.ElasticsearchEngineAdapterFixture;
import com.liferay.portal.search.elasticsearch6.internal.suggest.ElasticsearchSuggesterTranslator;
import com.liferay.portal.search.elasticsearch6.internal.suggest.PhraseSuggesterTranslatorImpl;
import com.liferay.portal.search.elasticsearch6.internal.suggest.TermSuggesterTranslatorImpl;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.field.FieldRegistry;
import com.liferay.portal.search.field.MappingsHolder;
import com.liferay.portal.search.internal.document.DocumentBuilderFactoryImpl;
import com.liferay.portal.search.internal.field.FieldRegistryContributorsHolder;
import com.liferay.portal.search.internal.field.FieldRegistryContributorsHolderImpl;
import com.liferay.portal.search.internal.field.FieldRegistryImpl;
import com.liferay.portal.search.internal.field.FieldRegistryListenersHolder;
import com.liferay.portal.search.internal.field.FieldRegistryListenersHolderImpl;
import com.liferay.portal.search.internal.field.MappingsHolderImpl;
import com.liferay.portal.search.internal.legacy.searcher.SearchRequestBuilderFactoryImpl;
import com.liferay.portal.search.internal.legacy.searcher.SearchResponseBuilderFactoryImpl;
import com.liferay.portal.search.spi.field.contributor.FieldRegistryContributor;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.search.test.util.indexing.IndexingFixture.IndexingFixtureListenerHelper;
import com.liferay.portal.util.DigesterImpl;
import com.liferay.portal.util.LocalizationImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class ElasticsearchIndexingFixture
	implements IndexingFixture, IndexingFixtureListenerHelper {

	@Override
	public void addFieldRegistryContributor(
		FieldRegistryContributor fieldRegistryContributor) {

		_fieldRegistryContributorsHolderImpl.addFieldRegistryContributor(
			fieldRegistryContributor);
	}

	@Override
	public void addIndexingFixtureListener(
		IndexingFixtureListener indexingFixtureListener) {

		_indexingFixtureListeners.add(indexingFixtureListener);
	}

	@Override
	public DocumentBuilderFactory getDocumentBuilderFactory() {
		return _documentBuilderFactory;
	}

	public ElasticsearchFixture getElasticsearchFixture() {
		return _elasticsearchFixture;
	}

	@Override
	public FieldRegistry getFieldRegistry() {
		return _fieldRegistry;
	}

	public FieldRegistrySynchronizer getFieldRegistrySynchronizer() {
		return _fieldRegistrySynchronizer;
	}

	@Override
	public IndexSearcher getIndexSearcher() {
		return _indexSearcher;
	}

	@Override
	public IndexWriter getIndexWriter() {
		return _indexWriter;
	}

	@Override
	public boolean isSearchEngineAvailable() {
		return true;
	}

	@Override
	public void setUp() throws Exception {
		_elasticsearchFixture.setUp();

		FieldRegistryContributorsHolderImpl
			fieldRegistryContributorsHolderImpl =
				new FieldRegistryContributorsHolderImpl();

		FieldRegistryListenersHolderImpl fieldRegistryListenersHolderImpl =
			new FieldRegistryListenersHolderImpl();

		MappingsHolder mappingsHolder = new MappingsHolderImpl();

		FieldRegistryImpl fieldRegistryImpl = createFieldRegistry(
			fieldRegistryContributorsHolderImpl,
			fieldRegistryListenersHolderImpl, mappingsHolder);

		DocumentBuilderFactory documentBuilderFactory =
			createDocumentBuilderFactory(fieldRegistryImpl);

		ElasticsearchEngineAdapterFixture elasticsearchEngineAdapterFixture =
			createElasticsearchEngineAdapterFixture(
				_elasticsearchFixture, fieldRegistryImpl, getFacetProcessor());

		elasticsearchEngineAdapterFixture.setUp();

		SearchEngineAdapter searchEngineAdapter =
			elasticsearchEngineAdapterFixture.getSearchEngineAdapter();

		IndexNameBuilder indexNameBuilder = String::valueOf;

		Localization localization = new LocalizationImpl();

		ElasticsearchIndexSearcher elasticsearchIndexSearcher =
			createIndexSearcher(
				_elasticsearchFixture, searchEngineAdapter, indexNameBuilder,
				localization);

		IndexWriter indexWriter = createIndexWriter(
			searchEngineAdapter, indexNameBuilder, localization);

		FieldRegistrySynchronizer fieldRegistrySynchronizer =
			createFieldRegistrySynchronizer(
				_elasticsearchFixture, mappingsHolder);

		fieldRegistryListenersHolderImpl.addFieldRegistryListener(
			new ElasticsearchFieldRegistryListener() {
				{
					setFieldRegistrySynchronizer(fieldRegistrySynchronizer);
				}
			});

		_documentBuilderFactory = documentBuilderFactory;
		_fieldRegistry = fieldRegistryImpl;
		_fieldRegistryContributorsHolderImpl =
			fieldRegistryContributorsHolderImpl;
		_fieldRegistrySynchronizer = fieldRegistrySynchronizer;
		_indexSearcher = elasticsearchIndexSearcher;
		_indexWriter = indexWriter;

		notifyListenersBeforeActivate();

		elasticsearchIndexSearcher.activate(
			_elasticsearchFixture.getElasticsearchConfigurationProperties());
		fieldRegistryImpl.activate();

		createIndex(indexNameBuilder);
	}

	@Override
	public void tearDown() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	protected static DocumentBuilderFactory createDocumentBuilderFactory(
		FieldRegistry fieldRegistry1) {

		return new DocumentBuilderFactoryImpl() {
			{
				fieldRegistry = fieldRegistry1;
			}
		};
	}

	protected static ElasticsearchDocumentFactory
		createElasticsearchDocumentFactory(FieldRegistry fieldRegistry) {

		return new DefaultElasticsearchDocumentFactory() {
			{
				setDocumentBuilderFactory(
					createLegacyDocumentBuilderFactory(fieldRegistry));
			}
		};
	}

	protected static ElasticsearchEngineAdapterFixture
		createElasticsearchEngineAdapterFixture(
			ElasticsearchClientResolver elasticsearchClientResolver,
			FieldRegistry fieldRegistry,
			FacetProcessor<SearchRequestBuilder> facetProcessor) {

		return new ElasticsearchEngineAdapterFixture() {
			{
				setElasticsearchClientResolver(elasticsearchClientResolver);
				setElasticsearchDocumentFactory(
					createElasticsearchDocumentFactory(fieldRegistry));
				setFacetProcessor(facetProcessor);
			}
		};
	}

	protected static QuerySuggester createElasticsearchQuerySuggester(
		ElasticsearchClientResolver elasticsearchClientResolver,
		IndexNameBuilder indexNameBuilder, Localization localization) {

		return new ElasticsearchQuerySuggester() {
			{
				setElasticsearchClientResolver(elasticsearchClientResolver);
				setIndexNameBuilder(indexNameBuilder);
				setLocalization(localization);
				setSuggesterTranslator(
					createElasticsearchSuggesterTranslator());
			}
		};
	}

	protected static ElasticsearchSpellCheckIndexWriter
		createElasticsearchSpellCheckIndexWriter(
			SearchEngineAdapter searchEngineAdapter,
			IndexNameBuilder indexNameBuilder, Localization localization) {

		return new ElasticsearchSpellCheckIndexWriter() {
			{
				digester = new DigesterImpl();

				setIndexNameBuilder(indexNameBuilder);
				setLocalization(localization);
				setSearchEngineAdapter(searchEngineAdapter);
			}
		};
	}

	protected static ElasticsearchSuggesterTranslator
		createElasticsearchSuggesterTranslator() {

		return new ElasticsearchSuggesterTranslator() {
			{
				setPhraseSuggesterTranslator(
					new PhraseSuggesterTranslatorImpl());
				setTermSuggesterTranslator(new TermSuggesterTranslatorImpl());
			}
		};
	}

	protected static FieldRegistryImpl createFieldRegistry(
		FieldRegistryContributorsHolder fieldRegistryContributorsHolder1,
		FieldRegistryListenersHolder fieldRegistryListenersHolder1,
		MappingsHolder mappingsHolder1) {

		return new FieldRegistryImpl() {
			{
				fieldRegistryContributorsHolder =
					fieldRegistryContributorsHolder1;
				fieldRegistryListenersHolder = fieldRegistryListenersHolder1;
				mappingsHolder = mappingsHolder1;
			}
		};
	}

	protected static FieldRegistrySynchronizer createFieldRegistrySynchronizer(
		ElasticsearchClientResolver elasticsearchClientResolver,
		MappingsHolder mappingsHolder) {

		return new FieldRegistrySynchronizerImpl() {
			{
				setElasticsearchClientResolver(elasticsearchClientResolver);
				setMappingsHolder(mappingsHolder);
			}
		};
	}

	protected static ElasticsearchIndexSearcher createIndexSearcher(
		ElasticsearchFixture elasticsearchFixture,
		SearchEngineAdapter searchEngineAdapter,
		IndexNameBuilder indexNameBuilder, Localization localization) {

		return new ElasticsearchIndexSearcher() {
			{
				setIndexNameBuilder(indexNameBuilder);
				setProps(createProps());
				setSearchEngineAdapter(searchEngineAdapter);
				setSearchRequestBuilderFactory(
					new SearchRequestBuilderFactoryImpl());
				setSearchResponseBuilderFactory(
					new SearchResponseBuilderFactoryImpl());

				setQuerySuggester(
					createElasticsearchQuerySuggester(
						elasticsearchFixture, indexNameBuilder, localization));
			}
		};
	}

	protected static IndexWriter createIndexWriter(
		SearchEngineAdapter searchEngineAdapter,
		IndexNameBuilder indexNameBuilder, Localization localization) {

		return new ElasticsearchIndexWriter() {
			{
				setSearchEngineAdapter(searchEngineAdapter);
				setIndexNameBuilder(indexNameBuilder);

				setSpellCheckIndexWriter(
					createElasticsearchSpellCheckIndexWriter(
						searchEngineAdapter, indexNameBuilder, localization));
			}
		};
	}

	protected static
		com.liferay.portal.search.legacy.document.DocumentBuilderFactory
		createLegacyDocumentBuilderFactory(FieldRegistry fieldRegistry) {

		return new
			com.liferay.portal.
				search.internal.legacy.document.DocumentBuilderFactoryImpl() {
			{
				setFieldRegistry(fieldRegistry);
			}
		};
	}

	protected static Props createProps() {
		Props props = Mockito.mock(Props.class);

		Mockito.doReturn(
			"20"
		).when(
			props
		).get(
			PropsKeys.INDEX_SEARCH_LIMIT
		);

		return props;
	}

	protected void addCreateIndexContributor(
		CreateIndexContributor createIndexContributor) {

		_createIndexContributors.add(createIndexContributor);
	}

	protected void createIndex(IndexNameBuilder indexNameBuilder) {
		IndexCreator indexCreator = new IndexCreator() {
			{
				setElasticsearchClientResolver(_elasticsearchFixture);
				setFieldRegistrySynchronizer(_fieldRegistrySynchronizer);
				setLiferayMappingsAddedToIndex(_liferayMappingsAddedToIndex);

				_createIndexContributors.forEach(
					this::addCreateIndexContributor);
			}
		};

		indexCreator.createIndex(
			new IndexName(indexNameBuilder.getIndexName(_companyId)));
	}

	protected FacetProcessor<SearchRequestBuilder> getFacetProcessor() {
		if (_facetProcessor != null) {
			return _facetProcessor;
		}

		return new DefaultFacetProcessor();
	}

	protected void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	protected void setElasticsearchFixture(
		ElasticsearchFixture elasticsearchFixture) {

		_elasticsearchFixture = elasticsearchFixture;
	}

	protected void setFacetProcessor(
		FacetProcessor<SearchRequestBuilder> facetProcessor) {

		_facetProcessor = facetProcessor;
	}

	protected void setLiferayMappingsAddedToIndex(
		boolean liferayMappingsAddedToIndex) {

		_liferayMappingsAddedToIndex = liferayMappingsAddedToIndex;
	}

	private long _companyId;
	private final List<CreateIndexContributor> _createIndexContributors =
		new ArrayList<>();
	private ElasticsearchFixture _elasticsearchFixture;
	private FacetProcessor<SearchRequestBuilder> _facetProcessor;

	protected void notifyListenersBeforeActivate() {
		_indexingFixtureListeners.forEach(
			indexingFixtureListener -> indexingFixtureListener.beforeActivate(
				this));
	}

	private DocumentBuilderFactory _documentBuilderFactory;
	private FieldRegistry _fieldRegistry;
	private FieldRegistryContributorsHolderImpl
		_fieldRegistryContributorsHolderImpl;
	private FieldRegistrySynchronizer _fieldRegistrySynchronizer;
	private final Collection<IndexingFixtureListener>
		_indexingFixtureListeners = new ArrayList<>(1);
	private IndexSearcher _indexSearcher;
	private IndexWriter _indexWriter;
	private boolean _liferayMappingsAddedToIndex;

}