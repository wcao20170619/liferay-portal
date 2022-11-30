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

package com.liferay.search.experiences.rest.internal.resource.v1_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.index.IndexInformation;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.search.experiences.rest.dto.v1_0.FieldMappingInfo;
import com.liferay.search.experiences.rest.internal.web.cache.FieldMappingsWebCacheItem;
import com.liferay.search.experiences.rest.resource.v1_0.FieldMappingInfoResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	enabled = false,
	properties = "OSGI-INF/liferay/rest/v1_0/field-mapping-info.properties",
	scope = ServiceScope.PROTOTYPE, service = FieldMappingInfoResource.class
)
public class FieldMappingInfoResourceImpl
	extends BaseFieldMappingInfoResourceImpl {

	@Override
	public Page<FieldMappingInfo> getFieldMappingInfosPage(
			Boolean external, String indexName, String query)
		throws Exception {

		return Page.of(getFieldMappings(external, indexName, query));
	}

	public List<FieldMappingInfo> getFieldMappings(
		boolean external, String indexName, String query) {

		JSONObject jsonObject = FieldMappingsWebCacheItem.get(
			_indexInformation, _getIndexName(indexName), _jsonFactory);

		if (jsonObject.length() == 0) {
			return Collections.<FieldMappingInfo>emptyList();
		}

		List<FieldMappingInfo> fieldMappingInfos = new ArrayList<>();

		_addFieldMappingInfos(
			fieldMappingInfos, new HashSet<String>(), jsonObject,
			StringPool.BLANK, query);

		return fieldMappingInfos;
	}

	private void _addFieldMappingInfo(
		List<FieldMappingInfo> fieldMappingInfos, String fieldName,
		Set<String> fieldNames, JSONObject jsonObject,
		int languageIdPosition1) {

		String fieldNameWithPosition = fieldName + languageIdPosition1;

		if (!fieldNames.contains(fieldNameWithPosition)) {
			fieldMappingInfos.add(
				new FieldMappingInfo() {
					{
						languageIdPosition = languageIdPosition1;
						name = fieldName;
						type = jsonObject.getString("type");
					}
				});
			fieldNames.add(fieldNameWithPosition);
		}
	}

	private void _addFieldMappingInfos(
		List<FieldMappingInfo> fieldMappingInfos, Set<String> fieldNames,
		JSONObject jsonObject, String path, String query) {

		for (String fieldName : jsonObject.keySet()) {
			JSONObject fieldJSONObject = jsonObject.getJSONObject(fieldName);

			String fieldPath = fieldName;

			if (!Validator.isBlank(path)) {
				fieldPath = path + "." + fieldName;
			}

			if (Objects.equals(fieldJSONObject.getString("type"), "nested")) {
				_addFieldMappingInfos(
					fieldMappingInfos, fieldNames,
					fieldJSONObject.getJSONObject("properties"), fieldPath,
					query);
			}
			else {
				if (!Validator.isBlank(query) &&
					!StringUtil.containsIgnoreCase(
						fieldPath, query, StringPool.BLANK)) {

					continue;
				}

				String languageId = _getLanguageId(fieldName);

				int languageIdPosition = -1;

				if (!Validator.isBlank(languageId)) {
					languageIdPosition = fieldPath.lastIndexOf(languageId);

					fieldPath = StringUtil.removeSubstring(
						fieldPath, languageId);
				}

				_addFieldMappingInfo(
					fieldMappingInfos, fieldPath, fieldNames, fieldJSONObject,
					languageIdPosition);
			}
		}
	}

	private String _getIndexName(String indexName) {
		String fullIndexName = _indexNameBuilder.getIndexName(
			contextCompany.getCompanyId());

		if (Validator.isBlank(indexName)) {
			return fullIndexName;
		}

		fullIndexName = fullIndexName + StringPool.DASH + indexName;

		List<String> indexNames = Arrays.asList(
			_indexInformation.getIndexNames());

		if (indexNames.contains(fullIndexName)) {
			return fullIndexName;
		}

		return _indexNameBuilder.getIndexName(contextCompany.getCompanyId());
	}

	private String _getLanguageId(String fieldName) {
		Matcher matcher = _languageIdPattern.matcher(fieldName);

		if (matcher.matches()) {
			return matcher.group(2);
		}

		return null;
	}

	private static final Pattern _languageIdPattern = Pattern.compile(
		"(.*)(_[a-z]{2}_[A-Z]{2})(_.*)?");

	@Reference
	private IndexInformation _indexInformation;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private JSONFactory _jsonFactory;

}