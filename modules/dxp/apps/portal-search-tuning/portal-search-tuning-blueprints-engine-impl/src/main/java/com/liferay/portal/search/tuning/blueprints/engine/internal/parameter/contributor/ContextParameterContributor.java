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

package com.liferay.portal.search.tuning.blueprints.engine.internal.parameter.contributor;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.search.tuning.blueprints.attributes.BlueprintsAttributes;
import com.liferay.portal.search.tuning.blueprints.engine.constants.ReservedParameterNames;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.LongParameter;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.ParameterDataBuilder;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.ParameterDefinition;
import com.liferay.portal.search.tuning.blueprints.engine.parameter.StringParameter;
import com.liferay.portal.search.tuning.blueprints.engine.spi.parameter.ParameterContributor;
import com.liferay.portal.search.tuning.blueprints.message.Message;
import com.liferay.portal.search.tuning.blueprints.message.Messages;
import com.liferay.portal.search.tuning.blueprints.message.Severity;
import com.liferay.portal.search.tuning.blueprints.model.Blueprint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, property = "name=context",
	service = ParameterContributor.class
)
public class ContextParameterContributor implements ParameterContributor {

	@Override
	public void contribute(
		ParameterDataBuilder parameterDataBuilder, Blueprint blueprint,
		BlueprintsAttributes blueprintsAttributes, Messages messages) {

		_addCompanyId(parameterDataBuilder, blueprintsAttributes);

		_addScopeGroupId(parameterDataBuilder, blueprintsAttributes);

		_addLanguage(parameterDataBuilder, blueprintsAttributes);

		_addLayoutNameCurrentValue(
			parameterDataBuilder, blueprintsAttributes, messages);

		_addPlid(parameterDataBuilder, blueprintsAttributes);
	}

	public List<ParameterDefinition> getParameterDefinitions() {
		List<ParameterDefinition> parameterDefinitions = new ArrayList<>();

		parameterDefinitions.add(
			new ParameterDefinition(
				_getTemplateVariableName(
					ReservedParameterNames.COMPANY_ID.getKey()),
				LongParameter.class.getName(), "parameter.context.company-id"));

		parameterDefinitions.add(
			new ParameterDefinition(
				_getTemplateVariableName(
					ReservedParameterNames.SCOPE_GROUP_ID.getKey()),
				LongParameter.class.getName(),
				"parameter.context.scope-group-id"));

		parameterDefinitions.add(
			new ParameterDefinition(
				_getTemplateVariableName(
					ReservedParameterNames.LAYOUT_LOCALE_NAME.getKey()),
				StringParameter.class.getName(),
				"parameter.context.layout-locale-name"));

		parameterDefinitions.add(
			new ParameterDefinition(
				_getTemplateVariableName(ReservedParameterNames.PLID.getKey()),
				LongParameter.class.getName(), "parameter.context.plid"));

		parameterDefinitions.add(
				new ParameterDefinition(
					_getTemplateVariableName(
						ReservedParameterNames.LANGUAGE.getKey()),
					StringParameter.class.getName(),
					"parameter.context.language"));

		parameterDefinitions.add(
			new ParameterDefinition(
				_getTemplateVariableName(
					ReservedParameterNames.LANGUAGE_ID.getKey()),
				StringParameter.class.getName(),
				"parameter.context.language-id"));

		return parameterDefinitions;
	}

	private void _addCompanyId(
		ParameterDataBuilder parameterDataBuilder,
		BlueprintsAttributes blueprintsAttributes) {

		parameterDataBuilder.addParameter(
			new LongParameter(
				ReservedParameterNames.COMPANY_ID.getKey(),
				_getTemplateVariableName(
					ReservedParameterNames.COMPANY_ID.getKey()),
				blueprintsAttributes.getCompanyId()));
	}

	private void _addLanguage(
		ParameterDataBuilder parameterDataBuilder,
		BlueprintsAttributes blueprintsAttributes) {

		parameterDataBuilder.addParameter(
			new StringParameter(
				ReservedParameterNames.LANGUAGE_ID.getKey(),
				_getTemplateVariableName(
					ReservedParameterNames.LANGUAGE_ID.getKey()), "_" +
				_language.getLanguageId(blueprintsAttributes.getLocale())));

		parameterDataBuilder.addParameter(
				new StringParameter(
					ReservedParameterNames.LANGUAGE.getKey(),
					_getTemplateVariableName(
						ReservedParameterNames.LANGUAGE.getKey()),
					blueprintsAttributes.getLocale().getLanguage()));

	}

	private void _addLayoutNameCurrentValue(
		ParameterDataBuilder parameterDataBuilder,
		BlueprintsAttributes blueprintsAttributes, Messages messages) {

		Optional<Object> optional = blueprintsAttributes.getAttributeOptional(
			ReservedParameterNames.PLID.getKey());

		if (!optional.isPresent()) {
			return;
		}

		try {
			Layout layout = _layoutLocalService.getLayout(
				GetterUtil.getLong(optional.get()));

			parameterDataBuilder.addParameter(
				new StringParameter(
					ReservedParameterNames.LAYOUT_LOCALE_NAME.getKey(),
					_getTemplateVariableName(
						ReservedParameterNames.LAYOUT_LOCALE_NAME.getKey()),
					layout.getName(blueprintsAttributes.getLocale(), true)));
		}
		catch (PortalException portalException) {
 			messages.addMessage(
				new Message.Builder().className(
					getClass().getName()
				).localizationKey(
						"core.error.layout-not-found"
				).msg(
						portalException.getMessage()
				).rootValue((String)optional.get()
				).severity(
					Severity.ERROR
				).throwable(portalException
				).build());
 			
			_log.error(portalException.getMessage(), portalException);
		}
	}

	private void _addPlid(
		ParameterDataBuilder parameterDataBuilder,
		BlueprintsAttributes blueprintsAttributes) {

		Optional<Object> optional = blueprintsAttributes.getAttributeOptional(
			ReservedParameterNames.PLID.getKey());

		if (!optional.isPresent()) {
			return;
		}

		parameterDataBuilder.addParameter(
			new LongParameter(
				ReservedParameterNames.PLID.getKey(),
				_getTemplateVariableName(ReservedParameterNames.PLID.getKey()),
				GetterUtil.getLong(optional.get())));
	}

	private void _addScopeGroupId(
		ParameterDataBuilder parameterDataBuilder,
		BlueprintsAttributes blueprintsAttributes) {

		Optional<Object> optional = blueprintsAttributes.getAttributeOptional(
			ReservedParameterNames.SCOPE_GROUP_ID.getKey());

		if (!optional.isPresent()) {
			return;
		}

		parameterDataBuilder.addParameter(
			new LongParameter(
				ReservedParameterNames.SCOPE_GROUP_ID.getKey(),
				_getTemplateVariableName(
					ReservedParameterNames.SCOPE_GROUP_ID.getKey()),
				GetterUtil.getLong(optional.get())));
	}

	private String _getTemplateVariableName(String key) {
		StringBundler sb = new StringBundler(3);

		sb.append("${context.");
		sb.append(key);
		sb.append("}");

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContextParameterContributor.class);

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

}