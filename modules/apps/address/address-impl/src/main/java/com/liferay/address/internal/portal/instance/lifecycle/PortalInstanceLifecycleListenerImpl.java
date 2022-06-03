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

package com.liferay.address.internal.portal.instance.lifecycle;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class PortalInstanceLifecycleListenerImpl
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstancePreunregistered(Company company)
		throws Exception {

		_countryLocalService.deleteCompanyCountries(company.getCompanyId());
	}

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		int count = _countryLocalService.getCompanyCountriesCount(
			company.getCompanyId());

		if (count > 0) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Skipping country initialization. Countries are ",
						"already initialized for company ",
						company.getCompanyId(), "."));
			}

			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Initializing countries for company " + company.getCompanyId());
		}

		JSONArray countriesJSONArray = _getJSONArray(
			"com/liferay/address/dependencies/countries.json");

		for (int i = 0; i < countriesJSONArray.length(); i++) {
			JSONObject countryJSONObject = countriesJSONArray.getJSONObject(i);

			try {
				String name = countryJSONObject.getString("name");

				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setCompanyId(company.getCompanyId());

				User defaultUser = company.getDefaultUser();

				serviceContext.setUserId(defaultUser.getUserId());

				Country country = _countryLocalService.addCountry(
					countryJSONObject.getString("a2"),
					countryJSONObject.getString("a3"), true, true,
					countryJSONObject.getString("idd"), name,
					countryJSONObject.getString("number"), 0, true, false,
					countryJSONObject.getBoolean("zipRequired"),
					serviceContext);

				_processCountryRegions(country);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	@Override
	public void portalInstanceUnregistered(Company company) throws Exception {
		super.portalInstanceUnregistered(company);
	}

	private JSONArray _getJSONArray(String path) throws Exception {
		return _jsonFactory.createJSONArray(
			StringUtil.read(getClassLoader(), path, false));
	}

	private void _processCountryRegions(Country country) {
		String a2 = country.getA2();

		try {
			String path =
				"com/liferay/address/dependencies/regions/" + a2 + ".json";

			ClassLoader classLoader = getClassLoader();

			if (classLoader.getResource(path) == null) {
				return;
			}

			JSONArray regionsJSONArray = _getJSONArray(path);

			if (_log.isDebugEnabled()) {
				_log.debug("Regions found for country " + a2);
			}

			for (int i = 0; i < regionsJSONArray.length(); i++) {
				try {
					JSONObject regionJSONObject =
						regionsJSONArray.getJSONObject(i);

					ServiceContext serviceContext = new ServiceContext();

					serviceContext.setCompanyId(country.getCompanyId());
					serviceContext.setUserId(country.getUserId());

					Region region = _regionLocalService.addRegion(
						country.getCountryId(), true,
						regionJSONObject.getString("name"), 0,
						regionJSONObject.getString("regionCode"),
						serviceContext);

					JSONObject localizationsJSONObject =
						regionJSONObject.getJSONObject("localizations");

					if (localizationsJSONObject == null) {
						continue;
					}

					for (String key : localizationsJSONObject.keySet()) {
						_regionLocalService.updateRegionLocalization(
							region, key,
							localizationsJSONObject.getString(key));
					}
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("No regions found for country " + a2, exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalInstanceLifecycleListenerImpl.class);

	@Reference
	private CountryLocalService _countryLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private RegionLocalService _regionLocalService;

	@Reference(
		target = "(&(release.bundle.symbolic.name=portal)(release.schema.version>=9.2.0))"
	)
	private Release _release;

}