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

package com.liferay.search.experiences.internal.blueprint.util;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portlet.expando.util.test.ExpandoTestUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wade Cao
 */
public class SXPBlueprintTestUtil {

	public static List<ExpandoColumn> addExpandoColumn(
			ExpandoTable expandoTable, long companyId, int indexType,
			String... columns)
		throws Exception {

		List<ExpandoColumn> expandoColumns = new ArrayList<>();

		for (String column : columns) {
			ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
				expandoTable, column, indexType);

			expandoColumns.add(expandoColumn);

			UnicodeProperties unicodeProperties =
				expandoColumn.getTypeSettingsProperties();

			unicodeProperties.setProperty(
				ExpandoColumnConstants.INDEX_TYPE, String.valueOf(indexType));

			expandoColumn.setTypeSettingsProperties(unicodeProperties);

			ExpandoColumnLocalServiceUtil.updateExpandoColumn(expandoColumn);
		}

		return expandoColumns;
	}

	public static ExpandoTable addExpandoTable(
			long companyId, List<ExpandoTable> expandoTables)
		throws Exception {

		ExpandoTable expandoTable = ExpandoTableLocalServiceUtil.fetchTable(
			companyId,
			ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class),
			"CUSTOM_FIELDS");

		if (expandoTable == null) {
			expandoTable = ExpandoTableLocalServiceUtil.addTable(
				companyId,
				ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class),
				"CUSTOM_FIELDS");

			expandoTables.add(expandoTable);
		}

		return expandoTable;
	}

	public static User addGroupUser(Group group, String roleName)
		throws Exception {

		Role role = RoleTestUtil.addRole(roleName, RoleConstants.TYPE_REGULAR);

		return UserTestUtil.addGroupUser(group, role.getName());
	}

}