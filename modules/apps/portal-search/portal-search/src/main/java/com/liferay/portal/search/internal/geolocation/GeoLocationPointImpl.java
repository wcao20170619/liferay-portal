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

package com.liferay.portal.search.internal.geolocation;

import com.liferay.portal.search.geolocation.GeoLocationPoint;

import java.util.Objects;

/**
 * @author Michael C. Han
 */
public class GeoLocationPointImpl implements GeoLocationPoint {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if ((object == null) || (getClass() != object.getClass())) {
			return false;
		}

		final GeoLocationPoint geoLocationPoint = (GeoLocationPoint)object;

		if ((_latitude == 0) && (_longitude == 0)) {
			return Objects.equals(geoLocationPoint.getGeoHash(), getGeoHash());
		}

		if (Double.compare(geoLocationPoint.getLatitude(), _latitude) != 0) {
			return false;
		}

		if (Double.compare(geoLocationPoint.getLongitude(), _longitude) != 0) {
			return false;
		}

		return true;
	}

	@Override
	public String getGeoHash() {
		return _geoHash;
	}

	@Override
	public double getLatitude() {
		return _latitude;
	}

	@Override
	public double getLongitude() {
		return _longitude;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_latitude, _longitude, _geoHash);
	}

	protected GeoLocationPointImpl(double latitude, double longitude) {
		_latitude = latitude;
		_longitude = longitude;

		_geoHash = null;
	}

	protected GeoLocationPointImpl(String geoHash) {
		_geoHash = geoHash;

		_longitude = 0;
		_latitude = 0;
	}

	private final String _geoHash;
	private final double _latitude;
	private final double _longitude;

}