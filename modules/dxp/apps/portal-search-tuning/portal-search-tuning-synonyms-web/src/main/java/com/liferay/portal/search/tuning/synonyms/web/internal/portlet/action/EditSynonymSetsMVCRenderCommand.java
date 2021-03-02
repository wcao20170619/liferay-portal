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

package com.liferay.portal.search.tuning.synonyms.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.constants.SynonymsPortletKeys;
import com.liferay.portal.search.tuning.synonyms.web.internal.display.context.EditSynonymSetsDisplayBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReader;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Filipe Oshiro
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + SynonymsPortletKeys.SYNONYMS,
		"mvc.command.name=/synonyms/edit_synonym_sets"
	},
	service = MVCRenderCommand.class
)
public class EditSynonymSetsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		EditSynonymSetsDisplayBuilder editSynonymSetsDisplayBuilder =
			new EditSynonymSetsDisplayBuilder(
				_portal.getHttpServletRequest(renderRequest), _portal,
				renderRequest, renderResponse, _synonymSetIndexNameBuilder,
				_synonymSetIndexReader);

		renderRequest.setAttribute(
			SynonymsPortletKeys.EDIT_SYNONYM_SET_DISPLAY_CONTEXT,
			editSynonymSetsDisplayBuilder.build());

		return "/edit_synonym_sets.jsp";
	}

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private Portal _portal;

	@Reference
	private SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder;

	@Reference
	private SynonymSetIndexReader _synonymSetIndexReader;

}