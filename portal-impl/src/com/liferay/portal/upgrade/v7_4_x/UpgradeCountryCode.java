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

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * @author Pei-Jung Lan
 */
public class UpgradeCountryCode extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		runSQL(
			"update Country set idd_ = '242', name = 'congo' where a2 = 'CG'");
		runSQL(
			"update Country set idd_ = '243', name = " +
				"'democratic-republic-of-congo' where a2 = 'CD'");
		runSQL("update Country set name = 'republic-of-congo' where a2 = 'CG'");

		_upgradeCountry();

		_upgradeRegion("ES", "Navarra", "NA");
		_upgradeRegion("IT", "Napoli", "NA");
	}

	private void _upgradeCountry() throws Exception {
		try (PreparedStatement preparedStatement1 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection.prepareStatement(
						StringBundler.concat(
							"insert into Country (mvccVersion, uuid_, ",
							"defaultLanguageId, countryId, companyId, userId, ",
							"createDate, modifiedDate, a2, a3, active_, ",
							"billingAllowed, groupFilterEnabled, idd_, name, ",
							"number_, position, shippingAllowed, ",
							"subjectToVAT, zipRequired) values (0, ?, ?, ?, ",
							"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
							"?)")));
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select User_.companyId, User_.languageId, ",
						"User_.userId from User_ join Company on ",
						"User_.companyId = Company.companyId where ",
						"User_.defaultUser = [$TRUE$] and Company.companyId ",
						"not in (select companyId from Country where a2 = ",
						"'NA')")));
			ResultSet resultSet = preparedStatement2.executeQuery()) {

			if (resultSet.next()) {
				Timestamp now = new Timestamp(System.currentTimeMillis());

				preparedStatement1.setString(1, PortalUUIDUtil.generate());
				preparedStatement1.setString(
					2, resultSet.getString("User_.languageId"));
				preparedStatement1.setLong(3, increment());
				preparedStatement1.setLong(
					4, resultSet.getLong("User_.companyId"));
				preparedStatement1.setLong(
					5, resultSet.getLong("User_.userId"));
				preparedStatement1.setTimestamp(6, now);
				preparedStatement1.setTimestamp(7, now);
				preparedStatement1.setString(8, "NA");
				preparedStatement1.setString(9, "NAM");
				preparedStatement1.setBoolean(10, true);
				preparedStatement1.setBoolean(11, true);
				preparedStatement1.setBoolean(12, false);
				preparedStatement1.setString(13, "674");
				preparedStatement1.setString(14, "namibia");
				preparedStatement1.setString(15, "516");
				preparedStatement1.setDouble(16, 0.0);
				preparedStatement1.setBoolean(17, true);
				preparedStatement1.setBoolean(18, false);
				preparedStatement1.setBoolean(19, false);

				preparedStatement1.addBatch();
			}

			preparedStatement1.executeBatch();
		}
	}

	private void _upgradeRegion(
			String countryA2, String regionName, String regionCode)
		throws Exception {

		try (PreparedStatement preparedStatement1 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection.prepareStatement(
						StringBundler.concat(
							"insert into Region (mvccVersion, uuid_, ",
							"defaultLanguageId, regionId, companyId, userId, ",
							"createDate, modifiedDate, countryId, active_, ",
							"name, position, regionCode) values (0, ?, ?, ?, ",
							"?, ?, ?, ?, ?, ?, ?, ?, ?)")));
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select Country.countryId, User_.companyId, ",
						"User_.languageId, User_.userId from User_ join ",
						"Country on User_.companyId = Country.companyId where ",
						"User_.defaultUser = [$TRUE$] and Country.a2 = '",
						countryA2, "' and Country.countryId not in (select ",
						"Country.countryId from Country join Region on ",
						"Country.countryId = Region.countryId where ",
						"Country.a2 = '", countryA2, "' and Region.regionCode ",
						"= '", regionCode, "')")));
			ResultSet resultSet = preparedStatement2.executeQuery()) {

			if (resultSet.next()) {
				Timestamp now = new Timestamp(System.currentTimeMillis());

				preparedStatement1.setString(1, PortalUUIDUtil.generate());
				preparedStatement1.setString(
					2, resultSet.getString("User_.languageId"));
				preparedStatement1.setLong(3, increment());
				preparedStatement1.setLong(
					4, resultSet.getLong("User_.companyId"));
				preparedStatement1.setLong(
					5, resultSet.getLong("User_.userId"));
				preparedStatement1.setTimestamp(6, now);
				preparedStatement1.setTimestamp(7, now);
				preparedStatement1.setLong(
					8, resultSet.getLong("Country.countryId"));
				preparedStatement1.setBoolean(9, true);
				preparedStatement1.setString(10, regionName);
				preparedStatement1.setDouble(11, 0.0);
				preparedStatement1.setString(12, regionCode);

				preparedStatement1.addBatch();
			}

			preparedStatement1.executeBatch();
		}
	}

}