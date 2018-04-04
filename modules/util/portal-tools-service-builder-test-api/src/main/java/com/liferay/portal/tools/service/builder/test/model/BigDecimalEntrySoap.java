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

package com.liferay.portal.tools.service.builder.test.model;

import aQute.bnd.annotation.ProviderType;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by SOAP remote services.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public class BigDecimalEntrySoap implements Serializable {
	public static BigDecimalEntrySoap toSoapModel(BigDecimalEntry model) {
		BigDecimalEntrySoap soapModel = new BigDecimalEntrySoap();

		soapModel.setBigDecimalEntryId(model.getBigDecimalEntryId());
		soapModel.setBigDecimalValue(model.getBigDecimalValue());

		return soapModel;
	}

	public static BigDecimalEntrySoap[] toSoapModels(BigDecimalEntry[] models) {
		BigDecimalEntrySoap[] soapModels = new BigDecimalEntrySoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static BigDecimalEntrySoap[][] toSoapModels(
		BigDecimalEntry[][] models) {
		BigDecimalEntrySoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new BigDecimalEntrySoap[models.length][models[0].length];
		}
		else {
			soapModels = new BigDecimalEntrySoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static BigDecimalEntrySoap[] toSoapModels(
		List<BigDecimalEntry> models) {
		List<BigDecimalEntrySoap> soapModels = new ArrayList<BigDecimalEntrySoap>(models.size());

		for (BigDecimalEntry model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new BigDecimalEntrySoap[soapModels.size()]);
	}

	public BigDecimalEntrySoap() {
	}

	public long getPrimaryKey() {
		return _bigDecimalEntryId;
	}

	public void setPrimaryKey(long pk) {
		setBigDecimalEntryId(pk);
	}

	public long getBigDecimalEntryId() {
		return _bigDecimalEntryId;
	}

	public void setBigDecimalEntryId(long bigDecimalEntryId) {
		_bigDecimalEntryId = bigDecimalEntryId;
	}

	public BigDecimal getBigDecimalValue() {
		return _bigDecimalValue;
	}

	public void setBigDecimalValue(BigDecimal bigDecimalValue) {
		_bigDecimalValue = bigDecimalValue;
	}

	private long _bigDecimalEntryId;
	private BigDecimal _bigDecimalValue;
}