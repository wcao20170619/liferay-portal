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

package com.liferay.client.extension.type.internal.manager;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.exception.ClientExtensionEntryTypeException;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.deployer.CETDeployer;
import com.liferay.client.extension.type.factory.CETFactory;
import com.liferay.client.extension.type.internal.CETCustomElementImpl;
import com.liferay.client.extension.type.internal.CETGlobalCSSImpl;
import com.liferay.client.extension.type.internal.CETGlobalJSImpl;
import com.liferay.client.extension.type.internal.CETIFrameImpl;
import com.liferay.client.extension.type.internal.CETThemeCSSImpl;
import com.liferay.client.extension.type.internal.CETThemeFaviconImpl;
import com.liferay.client.extension.type.internal.CETThemeJSImpl;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(immediate = true, service = CETManager.class)
public class CETManagerImpl implements CETManager {

	@Override
	public CET addCET(
			String baseURL, long companyId, String description,
			String externalReferenceCode, String name, Properties properties,
			String sourceCodeURL, String type, String typeSettings)
		throws PortalException {

		CET cet = null;

		if (Objects.equals(
				type, ClientExtensionEntryConstants.TYPE_CUSTOM_ELEMENT)) {

			cet = new CETCustomElementImpl(
				baseURL, companyId, description, externalReferenceCode, name,
				properties, sourceCodeURL, typeSettings);
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_GLOBAL_CSS)) {

			cet = new CETGlobalCSSImpl(
				baseURL, companyId, description, externalReferenceCode, name,
				properties, sourceCodeURL, typeSettings);
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_GLOBAL_JS)) {

			cet = new CETGlobalJSImpl(
				baseURL, companyId, description, externalReferenceCode, name,
				properties, sourceCodeURL, typeSettings);
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_IFRAME)) {

			cet = new CETIFrameImpl(
				baseURL, companyId, description, externalReferenceCode, name,
				properties, sourceCodeURL, typeSettings);
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_THEME_CSS)) {

			cet = new CETThemeCSSImpl(
				baseURL, companyId, description, externalReferenceCode, name,
				properties, sourceCodeURL, typeSettings);
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_THEME_FAVICON)) {

			cet = new CETThemeFaviconImpl(
				baseURL, companyId, description, externalReferenceCode, name,
				properties, sourceCodeURL, typeSettings);
		}
		else if (Objects.equals(
					type, ClientExtensionEntryConstants.TYPE_THEME_JS)) {

			cet = new CETThemeJSImpl(
				baseURL, companyId, description, externalReferenceCode, name,
				properties, sourceCodeURL, typeSettings);
		}
		else {
			throw new ClientExtensionEntryTypeException("Invalid type " + type);
		}

		Map<String, CET> cetsMap = _getCETsMap(cet.getCompanyId());

		cetsMap.put(externalReferenceCode, cet);

		_serviceRegistrationsMaps.put(
			externalReferenceCode, _cetDeployer.deploy(cet));

		return cet;
	}

	@Override
	public void deleteCET(CET cet) {
		Map<String, CET> cetsMap = _getCETsMap(cet.getCompanyId());

		cetsMap.remove(cet.getExternalReferenceCode());

		_undeployCET(cet);
	}

	@Override
	public CET getCET(long companyId, String externalReferenceCode) {
		Map<String, CET> cetsMap = _getCETsMap(companyId);

		return cetsMap.get(externalReferenceCode);
	}

	@Override
	public List<CET> getCETs(
			long companyId, String keywords, String type, Pagination pagination,
			Sort sort)
		throws PortalException {

		// TODO Sort

		return ListUtil.subList(
			_getCETs(companyId, keywords, type), pagination.getStartPosition(),
			pagination.getEndPosition());
	}

	@Override
	public int getCETsCount(long companyId, String keywords, String type)
		throws PortalException {

		List<CET> cets = _getCETs(companyId, keywords, type);

		return cets.size();
	}

	@Deactivate
	protected void deactivate() {
		for (Map.Entry<Long, Map<String, CET>> entry1 : _cetsMaps.entrySet()) {
			Map<String, CET> cetsMap = entry1.getValue();

			for (Map.Entry<String, CET> entry2 : cetsMap.entrySet()) {
				CET cet = entry2.getValue();

				_undeployCET(cet);
			}
		}
	}

	private boolean _contains(String string1, String string2) {
		if ((string1 == null) || (string2 == null)) {
			return false;
		}

		string1 = StringUtil.toLowerCase(string1);
		string2 = StringUtil.toLowerCase(string2);

		return string1.contains(string2);
	}

	private List<CET> _getCETs(long companyId, String keywords, String type)
		throws PortalException {

		List<CET> cets = new ArrayList<>();

		for (ClientExtensionEntry clientExtensionEntry :
				_clientExtensionEntryLocalService.getClientExtensionEntries(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			CET cet = _cetFactory.cet(clientExtensionEntry);

			if (_isInclude(cet, keywords, type)) {
				cets.add(cet);
			}
		}

		Map<String, CET> cetsMap = _getCETsMap(companyId);

		for (Map.Entry<String, CET> entry : cetsMap.entrySet()) {
			CET cet = entry.getValue();

			if (_isInclude(cet, keywords, type)) {
				cets.add(cet);
			}
		}

		return cets;
	}

	private Map<String, CET> _getCETsMap(long companyId) {
		Map<String, CET> cetsMap = _cetsMaps.get(companyId);

		if (cetsMap == null) {
			cetsMap = new ConcurrentHashMap<>();

			_cetsMaps.put(companyId, cetsMap);
		}

		return cetsMap;
	}

	private boolean _isInclude(CET cet, String keywords, String type) {
		if (Validator.isNotNull(type) && !Objects.equals(type, cet.getType())) {
			return false;
		}

		if (Validator.isNotNull(keywords) &&
			!_contains(cet.getDescription(), keywords) &&
			!_contains(
				cet.getName(LocaleUtil.getMostRelevantLocale()), keywords) &&
			!_contains(cet.getSourceCodeURL(), keywords)) {

			return false;
		}

		return true;
	}

	private void _undeployCET(CET cet) {
		List<ServiceRegistration<?>> serviceRegistrations =
			_serviceRegistrationsMaps.remove(cet.getExternalReferenceCode());

		if (serviceRegistrations != null) {
			for (ServiceRegistration<?> serviceRegistration :
					serviceRegistrations) {

				serviceRegistration.unregister();
			}
		}
	}

	@Reference
	private CETDeployer _cetDeployer;

	@Reference
	private CETFactory _cetFactory;

	private final Map<Long, Map<String, CET>> _cetsMaps =
		new ConcurrentHashMap<>();

	@Reference
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	private final Map<String, List<ServiceRegistration<?>>>
		_serviceRegistrationsMaps = new ConcurrentHashMap<>();

}