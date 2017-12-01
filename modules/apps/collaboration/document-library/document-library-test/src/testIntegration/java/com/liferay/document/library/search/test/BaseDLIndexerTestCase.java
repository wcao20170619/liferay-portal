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

package com.liferay.document.library.search.test;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.service.test.ServiceTestUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Wade Cao
 */
public abstract class BaseDLIndexerTestCase {

	public void setUp() throws Exception {
		ServiceTestUtil.setUser(TestPropsValues.getUser());

		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());
	}

	protected long getDDMStructureId(FileEntry fileEntry)
		throws PortalException {

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

		List<DLFileEntryMetadata> dlFileEntryMetadatas =
			DLFileEntryMetadataLocalServiceUtil.
				getFileVersionFileEntryMetadatas(
					dlFileVersion.getFileVersionId());

		for (DLFileEntryMetadata dlFileEntryMetadata : dlFileEntryMetadatas) {
			if (dlFileEntryMetadata != null) {
				return dlFileEntryMetadata.getDDMStructureId();
			}
		}

		return 0;
	}

	protected abstract Group getGroup();

	protected abstract Indexer<?> getIndexer();

	protected SearchContext getSearchContext(String searchTerm, Locale locale)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			getGroup().getGroupId());

		searchContext.setKeywords(searchTerm);

		if (locale != null) {
			searchContext.setLocale(locale);
		}

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(StringPool.STAR);

		return searchContext;
	}

	protected Document getSingleDocument(Hits hits, int pos) {
		List<Document> documents = hits.toList();

		if (documents.isEmpty() || (pos >= documents.size())) {
			return null;
		}

		return documents.get(pos);
	}

	protected Document search(String searchTerm, Locale locale, int docPos) {
		try {
			SearchContext searchContext = getSearchContext(searchTerm, locale);

			Hits hits = getIndexer().search(searchContext);

			return getSingleDocument(hits, docPos);
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void serUID(
		Map<String, String> mapStrings, String modelClassName, long id) {

		String uid = modelClassName + "_PORTLET_" + id;

		mapStrings.put(Field.UID, uid);
	}

	protected void setGroupRoleId(Map<String, String> mapStrings)
		throws PortalException {

		Role role = RoleLocalServiceUtil.getDefaultGroupRole(
			getGroup().getGroupId());

		String groupRoleId =
			getGroup().getGroupId() + StringPool.DASH + role.getRoleId();

		mapStrings.put(Field.GROUP_ROLE_ID, groupRoleId);
	}

	protected void setRoleId(
		Map<String, String> mapStrings, Document document) {

		Role role = RoleLocalServiceUtil.fetchRole(
			getGroup().getCompanyId(), "Owner");

		String roleId = String.valueOf(role.getRoleId());

		if (!roleId.equals(document.get(Field.ROLE_ID))) {
			role = RoleLocalServiceUtil.fetchRole(
				getGroup().getCompanyId(), "Guest");

			roleId = String.valueOf(role.getRoleId());
		}

		mapStrings.put(Field.ROLE_ID, roleId);
	}

}