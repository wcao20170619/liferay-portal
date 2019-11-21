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

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_3_1;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rodrigo Paulino
 */
public class UpgradeDDMFormFieldValidation extends UpgradeProcess {

	public UpgradeDDMFormFieldValidation(JSONFactory jsonFactory) {
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement ps1 = connection.prepareStatement(
				"select structureId, definition from DDMStructure where " +
					"classNameId = ? ");
			PreparedStatement ps2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructure set definition = ? where " +
						"structureId = ?");
			PreparedStatement ps3 = connection.prepareStatement(
				"select structureVersionId, definition from " +
					"DDMStructureVersion where structureId = ?");
			PreparedStatement ps4 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructureVersion set definition = ? where " +
						"structureVersionId = ?")) {

			ps1.setLong(
				1,
				PortalUtil.getClassNameId(
					"com.liferay.dynamic.data.mapping.model.DDMFormInstance"));

			try (ResultSet rs = ps1.executeQuery()) {
				while (rs.next()) {
					String definition = rs.getString("definition");

					ps2.setString(1, _upgradeDefinition(definition));

					long structureId = rs.getLong("structureId");

					ps2.setLong(2, structureId);

					ps2.addBatch();

					ps3.setLong(1, structureId);

					try (ResultSet rs2 = ps3.executeQuery()) {
						while (rs2.next()) {
							definition = rs2.getString("definition");

							ps4.setString(1, _upgradeDefinition(definition));

							long structureVersionId = rs2.getLong(
								"structureVersionId");

							ps4.setLong(2, structureVersionId);

							ps4.addBatch();
						}
					}
				}
			}

			ps2.executeBatch();

			ps4.executeBatch();
		}
	}

	private Map<String, String> _dissect(
		String expression, String name, String regex) {

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(expression);

		matcher.find();

		String parameter = matcher.group(2);

		return HashMapBuilder.put(
			"name", name
		).put(
			"parameter", parameter
		).put(
			"value", StringUtil.replace(expression, parameter, "{parameter}")
		).build();
	}

	private Map<String, String> _dissectExpression(String expression) {
		if (expression.matches("NOT\\(contains\\((.+), \"(.*)\"\\)\\)")) {
			return _dissect(
				expression, "notContains",
				"NOT\\(contains\\((.+), \"(.*)\"\\)\\)");
		}

		if (expression.matches("contains\\((.+), \"(.*)\"\\)")) {
			return _dissect(
				expression, "contains", "contains\\((.+), \"(.*)\"\\)");
		}

		if (expression.matches("isURL\\((.+)\\)")) {
			return HashMapBuilder.put(
				"name", "url"
			).put(
				"parameter", StringPool.BLANK
			).put(
				"value", expression
			).build();
		}

		if (expression.matches("isEmailAddress\\((.+)\\)")) {
			return HashMapBuilder.put(
				"name", "email"
			).put(
				"parameter", StringPool.BLANK
			).put(
				"value", expression
			).build();
		}

		if (expression.matches("match\\((.+), \"(.*)\"\\)")) {
			return _dissect(
				expression, "regularExpression", "match\\((.+), \"(.*)\"\\)");
		}

		if (expression.matches("(.+)<(\\d+\\.?\\d*)?")) {
			return _dissect(expression, "lt", "(.+)<(\\d+\\.?\\d*)?");
		}

		if (expression.matches("(.+)<=(\\d+\\.?\\d*)?")) {
			return _dissect(expression, "lteq", "(.+)<=(\\d+\\.?\\d*)?");
		}

		if (expression.matches("(.+)==(\\d+\\.?\\d*)?")) {
			return _dissect(expression, "eq", "(.+)==(\\d+\\.?\\d*)?");
		}

		if (expression.matches("(.+)>(\\d+\\.?\\d*)?")) {
			return _dissect(expression, "gt", "(.+)>(\\d+\\.?\\d*)?");
		}

		if (expression.matches("(.+)>=(\\d+\\.?\\d*)?")) {
			return _dissect(expression, "gteq", "(.+)>=(\\d+\\.?\\d*)?");
		}

		return HashMapBuilder.put(
			"name", StringPool.BLANK
		).put(
			"parameter", StringPool.BLANK
		).put(
			"value", expression
		).build();
	}

	private String _upgradeDefinition(String definition)
		throws PortalException {

		JSONObject jsonObject = _jsonFactory.createJSONObject(definition);

		_upgradeFields(
			jsonObject.getJSONArray("availableLanguageIds"),
			jsonObject.getJSONArray("fields"));

		return jsonObject.toJSONString();
	}

	private void _upgradeFields(
		JSONArray availableLanguageIdsJSONArray, JSONArray fieldsJSONArray) {

		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject jsonObject = fieldsJSONArray.getJSONObject(i);

			JSONObject validationJSONObject = jsonObject.getJSONObject(
				"validation");

			if (validationJSONObject != null) {
				String expression = validationJSONObject.getString(
					"expression");

				if (Validator.isNull(expression)) {
					jsonObject.remove("validation");
				}
				else {
					JSONObject expressionJSONObject =
						validationJSONObject.getJSONObject("expression");

					if (expressionJSONObject == null) {
						_upgradeValidation(
							availableLanguageIdsJSONArray, expression,
							validationJSONObject);
					}
					else if (expressionJSONObject.isNull("name")) {
						_upgradeValidation(
							availableLanguageIdsJSONArray,
							expressionJSONObject.getString("value"),
							validationJSONObject);
					}
				}
			}

			JSONArray nestedFieldsJSONArray = jsonObject.getJSONArray(
				"nestedFields");

			if (nestedFieldsJSONArray != null) {
				_upgradeFields(
					availableLanguageIdsJSONArray, nestedFieldsJSONArray);
			}
		}
	}

	private void _upgradeValidation(
		JSONArray availableLanguageIdsJSONArray, String expression,
		JSONObject validationJSONObject) {

		Map<String, String> expressionParts = _dissectExpression(expression);

		String parameter = expressionParts.remove("parameter");

		validationJSONObject.put("expression", expressionParts);

		Map<String, String> localizedValue = new HashMap<>();

		for (int j = 0; j < availableLanguageIdsJSONArray.length(); j++) {
			localizedValue.put(
				availableLanguageIdsJSONArray.getString(j), parameter);
		}

		validationJSONObject.put("parameter", localizedValue);
	}

	private final JSONFactory _jsonFactory;

}