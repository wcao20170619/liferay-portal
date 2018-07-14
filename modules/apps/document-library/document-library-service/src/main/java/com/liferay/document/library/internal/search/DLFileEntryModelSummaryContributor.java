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

package com.liferay.document.library.internal.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.spi.model.result.contributor.ModelSummaryContributor;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = ModelSummaryContributor.class
)
public class DLFileEntryModelSummaryContributor
	implements ModelSummaryContributor {

	@Override
	public Summary getSummary(
		Document document, Locale locale, String snippet) {

		String prefix = Field.SNIPPET + StringPool.UNDERLINE;

		String title = document.get(prefix + Field.TITLE, Field.TITLE);
		String content = document.get(
			locale, prefix + Field.CONTENT, Field.CONTENT);

		if (Validator.isNull(content)) {
			content = document.get(
				prefix + Field.DESCRIPTION, Field.DESCRIPTION);
		}

		Summary summary = new Summary(title, content);

		int defaultContentLength = 200;

		if (defaultContentLength < content.length()) {
			String strippedContent = HtmlUtil.stripHtml(content);

			int strippedLength = strippedContent.length();

			if (strippedLength < defaultContentLength) {
				defaultContentLength = content.length();
			}
			else if (strippedLength < content.length()) {
				String strippedBeginning = HtmlUtil.stripHtml(
					content.substring(0, defaultContentLength));

				defaultContentLength =
					defaultContentLength * 2 - strippedBeginning.length();
			}
		}

		summary.setMaxContentLength(defaultContentLength);

		return summary;
	}

}