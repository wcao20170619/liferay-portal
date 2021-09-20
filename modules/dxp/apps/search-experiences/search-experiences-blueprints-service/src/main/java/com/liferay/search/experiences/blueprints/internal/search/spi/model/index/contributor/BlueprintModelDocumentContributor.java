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

package com.liferay.search.experiences.blueprints.internal.search.spi.model.index.contributor;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.search.experiences.blueprints.model.Blueprint;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.search.experiences.blueprints.model.Blueprint",
	service = ModelDocumentContributor.class
)
public class BlueprintModelDocumentContributor
	implements ModelDocumentContributor<Blueprint> {

	public void contribute(Document document, Blueprint blueprint) {
		document.addKeyword(Field.COMPANY_ID, blueprint.getCompanyId());
		document.addKeyword(Field.GROUP_ID, blueprint.getGroupId());
		document.addDate(Field.MODIFIED_DATE, blueprint.getModifiedDate());

		for (Locale locale :
				LanguageUtil.getAvailableLocales(blueprint.getGroupId())) {

			String languageId = LocaleUtil.toLanguageId(locale);

			document.addText(
				LocalizationUtil.getLocalizedName(
					Field.DESCRIPTION, languageId),
				blueprint.getDescription(locale));
			document.addText(
				LocalizationUtil.getLocalizedName(Field.TITLE, languageId),
				blueprint.getTitle(locale));
		}
	}

}