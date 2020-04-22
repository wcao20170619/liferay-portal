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

package com.liferay.portal.search.internal.indexer;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.index.IndexStatusManager;
import com.liferay.portal.search.index.UpdateDocumentIndexWriter;
import com.liferay.portal.search.indexer.BaseModelRetriever;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.indexer.IndexerWriter;
import com.liferay.portal.search.internal.index.contributor.helper.ModelIndexerWriterDocumentHelperImpl;
import com.liferay.portal.search.permission.SearchPermissionIndexWriter;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Michael C. Han
 */
public class IndexerWriterImpl<T extends BaseModel<?>>
	implements IndexerWriter<T> {

	public IndexerWriterImpl(
		ModelSearchSettings modelSearchSettings,
		BaseModelRetriever baseModelRetriever,
		ModelIndexerWriterContributor<T> modelIndexerWriterContributor,
		IndexerDocumentBuilder indexerDocumentBuilder,
		SearchPermissionIndexWriter searchPermissionIndexWriter,
		UpdateDocumentIndexWriter updateDocumentIndexWriter,
		IndexStatusManager indexStatusManager,
		IndexWriterHelper indexWriterHelper, Props props) {

		_modelSearchSettings = modelSearchSettings;
		_baseModelRetriever = baseModelRetriever;
		_modelIndexerWriterContributor = modelIndexerWriterContributor;
		_indexerDocumentBuilder = indexerDocumentBuilder;
		_searchPermissionIndexWriter = searchPermissionIndexWriter;
		_updateDocumentIndexWriter = updateDocumentIndexWriter;
		_indexStatusManager = indexStatusManager;
		_indexWriterHelper = indexWriterHelper;
		_props = props;
	}

	@Override
	public void delete(long companyId, String uid) {
		if (!isEnabled()) {
			return;
		}

		try {
			_indexWriterHelper.deleteDocument(
				_modelSearchSettings.getSearchEngineId(), companyId, uid,
				_modelSearchSettings.isCommitImmediately());
		}
		catch (SearchException searchException) {
			throw new RuntimeException(searchException);
		}
	}

	@Override
	public void delete(T baseModel) {
		if (baseModel == null) {
			return;
		}

		long companyId = _modelIndexerWriterContributor.getCompanyId(baseModel);

		String uid = _indexerDocumentBuilder.getDocumentUID(baseModel);

		delete(companyId, uid);
	}

	@Override
	public BatchIndexingActionable getBatchIndexingActionable() {
		BatchIndexingActionable batchIndexingActionable =
			_modelIndexerWriterContributor.getBatchIndexingActionable();

		batchIndexingActionable.setSearchEngineId(
			_modelSearchSettings.getSearchEngineId());

		return batchIndexingActionable;
	}

	@Override
	public boolean isEnabled() {
		if (_indexerEnabled == null) {
			String indexerEnabled = _props.get(
				PropsKeys.INDEXER_ENABLED,
				new Filter(_modelSearchSettings.getClassName()));

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						indexerEnabled, " indexer.enabled for ",
						_modelSearchSettings.getClassName()));
			}

			_indexerEnabled = GetterUtil.getBoolean(indexerEnabled, true);

			return _indexerEnabled;
		}

		if (_indexStatusManager.isIndexReadOnly() ||
			_indexStatusManager.isIndexReadOnly(
				_modelSearchSettings.getClassName()) ||
			!_indexerEnabled) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"STOP! _indexStatusManager=", _indexStatusManager,
						" | _indexStatusManager.isIndexReadOnly()=",
						_indexStatusManager.isIndexReadOnly(),
						" | _indexStatusManager.isIndexReadOnly(",
						_modelSearchSettings.getClassName(), ")=",
						_indexStatusManager.isIndexReadOnly(
							_modelSearchSettings.getClassName()),
						" | _indexerEnabled=", _indexerEnabled));
			}

			return false;
		}

		return true;
	}

	@Override
	public void reindex(Collection<T> baseModels) {
		if (!isEnabled()) {
			return;
		}

		if ((baseModels == null) || baseModels.isEmpty()) {
			return;
		}

		for (T baseModel : baseModels) {
			reindex(baseModel);
		}
	}

	@Override
	public void reindex(long classPK) {
		if (!isEnabled()) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Not enabled, skipping: Reindex ",
						_modelSearchSettings.getClassName(), " with classPK ",
						classPK));
			}

			return;
		}

		if (classPK <= 0) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Undefined key, skipping: Reindex ",
						_modelSearchSettings.getClassName(), " with classPK ",
						classPK));
			}

			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Reindex ", _modelSearchSettings.getClassName(),
					" with classPK ", classPK));
		}

		Optional<BaseModel<?>> baseModelOptional =
			_baseModelRetriever.fetchBaseModel(
				_modelSearchSettings.getClassName(), classPK);

		if (!baseModelOptional.isPresent()) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"NOT reindexing, fetch could NOT find ",
						_modelSearchSettings.getClassName(), " with classPK ",
						classPK));
			}
		}

		baseModelOptional.ifPresent(baseModel -> reindex((T)baseModel));
	}

	@Override
	public void reindex(String[] ids) {
		if (!isEnabled()) {
			return;
		}

		if (ArrayUtil.isEmpty(ids)) {
			return;
		}

		long companyThreadLocalCompanyId = CompanyThreadLocal.getCompanyId();

		try {
			for (String id : ids) {
				long companyId = GetterUtil.getLong(id);

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Reindexing all ",
							_modelSearchSettings.getClassName(),
							" for company: ", companyId));
				}

				CompanyThreadLocal.setCompanyId(companyId);

				BatchIndexingActionable batchIndexingActionable =
					getBatchIndexingActionable();

				batchIndexingActionable.setCompanyId(companyId);

				_modelIndexerWriterContributor.customize(
					batchIndexingActionable,
					new ModelIndexerWriterDocumentHelperImpl(
						_modelSearchSettings.getClassName(),
						_indexerDocumentBuilder));

				try {
					batchIndexingActionable.performActions();

					if (_log.isDebugEnabled()) {
						_log.debug(
							StringBundler.concat(
								"Reindexed all ",
								_modelSearchSettings.getClassName(),
								" for company: ", companyId));
					}
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Error reindexing all ",
								_modelSearchSettings.getClassName(),
								" for company: ", companyId),
							exception);
					}
				}
			}
		}
		finally {
			CompanyThreadLocal.setCompanyId(companyThreadLocalCompanyId);
		}
	}

	@Override
	public void reindex(T baseModel) {
		if (_log.isDebugEnabled()) {
			_log.debug("About to reindex model " + baseModel);
		}

		if (!isEnabled()) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"IndexerWriterImpl is NOT enabled, will NOT reindex " +
						baseModel);
			}

			return;
		}

		if (baseModel == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("null model, will NOT reindex");
			}

			return;
		}

		IndexerWriterMode indexerWriterMode = _getIndexerWriterMode(baseModel);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Reindex model ", baseModel, " in IndexerWriterMode ",
					indexerWriterMode));
		}

		if ((indexerWriterMode == IndexerWriterMode.UPDATE) ||
			(indexerWriterMode == IndexerWriterMode.PARTIAL_UPDATE)) {

			Document document = _indexerDocumentBuilder.getDocument(baseModel);

			_updateDocumentIndexWriter.updateDocument(
				_modelSearchSettings.getSearchEngineId(),
				_modelIndexerWriterContributor.getCompanyId(baseModel),
				document, _modelSearchSettings.isCommitImmediately());
		}
		else if (indexerWriterMode == IndexerWriterMode.DELETE) {
			delete(baseModel);
		}
		else if (indexerWriterMode == IndexerWriterMode.SKIP) {
			if (_log.isDebugEnabled()) {
				_log.debug("Skipping model " + baseModel);
			}
		}

		_modelIndexerWriterContributor.modelIndexed(baseModel);
	}

	@Override
	public void setEnabled(boolean enabled) {
		_indexerEnabled = enabled;
	}

	@Override
	public void updatePermissionFields(T baseModel) {
		_searchPermissionIndexWriter.updatePermissionFields(
			baseModel, _modelIndexerWriterContributor.getCompanyId(baseModel),
			_modelSearchSettings.getSearchEngineId(),
			_modelSearchSettings.isCommitImmediately());
	}

	private IndexerWriterMode _getIndexerWriterMode(T baseModel) {
		IndexerWriterMode indexerWriterMode =
			_modelIndexerWriterContributor.getIndexerWriterMode(baseModel);

		if (indexerWriterMode != null) {
			return indexerWriterMode;
		}

		if (baseModel instanceof TrashedModel &&
			baseModel instanceof WorkflowedModel) {

			TrashedModel trashedModel = (TrashedModel)baseModel;
			WorkflowedModel workflowedModel = (WorkflowedModel)baseModel;

			if (!workflowedModel.isApproved() && !trashedModel.isInTrash()) {
				return IndexerWriterMode.SKIP;
			}
		}

		return IndexerWriterMode.UPDATE;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexerWriterImpl.class);

	private final BaseModelRetriever _baseModelRetriever;
	private final IndexerDocumentBuilder _indexerDocumentBuilder;
	private Boolean _indexerEnabled;
	private final IndexStatusManager _indexStatusManager;
	private final IndexWriterHelper _indexWriterHelper;
	private final ModelIndexerWriterContributor<T>
		_modelIndexerWriterContributor;
	private final ModelSearchSettings _modelSearchSettings;
	private final Props _props;
	private final SearchPermissionIndexWriter _searchPermissionIndexWriter;
	private final UpdateDocumentIndexWriter _updateDocumentIndexWriter;

}