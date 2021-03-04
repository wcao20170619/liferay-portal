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

package com.liferay.portal.search.tuning.blueprints.misspellings.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.tuning.blueprints.misspellings.index.MisspellingSet;
import com.liferay.portal.search.tuning.blueprints.misspellings.index.name.MisspellingSetIndexName;
import com.liferay.portal.search.tuning.blueprints.misspellings.index.name.MisspellingSetIndexNameBuilder;
import com.liferay.portal.search.tuning.blueprints.misspellings.web.internal.constants.MisspellingsMVCCommandNames;
import com.liferay.portal.search.tuning.blueprints.misspellings.web.internal.constants.MisspellingsPortletKeys;
import com.liferay.portal.search.tuning.blueprints.misspellings.web.internal.constants.MisspellingsWebKeys;
import com.liferay.portal.search.tuning.blueprints.misspellings.web.internal.index.MisspellingSetIndexReader;
import com.liferay.portal.search.tuning.blueprints.misspellings.web.internal.index.MisspellingSetIndexWriter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + MisspellingsPortletKeys.MISSPELLINGS,
		"mvc.command.name=" + MisspellingsMVCCommandNames.DELETE_MISSPELLING_SET
	},
	service = MVCActionCommand.class
)
public class DeleteMisspellingSetMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		MisspellingSetIndexName misspellingSetIndexName =
			_misspellingSetIndexNameBuilder.getMisspellingSetIndexName(
				_portal.getCompanyId(actionRequest));

		_deleteMisspellingSets(
			misspellingSetIndexName,
			_getMisspellingSets(actionRequest, misspellingSetIndexName));

		sendRedirect(actionRequest, actionResponse);
	}

	private void _deleteMisspellingSets(
		MisspellingSetIndexName misspellingSetIndexName,
		List<MisspellingSet> misspellingSets) {

		for (MisspellingSet misspellingSet : misspellingSets) {
			_misspellingSetIndexWriter.remove(
				misspellingSetIndexName, misspellingSet.getMisspellingSetId());
		}
	}

	private String[] _getMisspellingSetIds(ActionRequest actionRequest) {
		return ParamUtil.getStringValues(
			actionRequest, MisspellingsWebKeys.ROW_IDS);
	}

	private List<MisspellingSet> _getMisspellingSets(
		ActionRequest actionRequest,
		MisspellingSetIndexName misspellingSetIndexName) {

		return Stream.of(
			_getMisspellingSetIds(actionRequest)
		).map(
			id -> _misspellingSetIndexReader.fetchMisspellingSetOptional(
				misspellingSetIndexName, id)
		).filter(
			Optional::isPresent
		).map(
			Optional::get
		).collect(
			Collectors.toList()
		);
	}

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private MisspellingSetIndexNameBuilder _misspellingSetIndexNameBuilder;

	@Reference
	private MisspellingSetIndexReader _misspellingSetIndexReader;

	@Reference
	private MisspellingSetIndexWriter _misspellingSetIndexWriter;

	@Reference
	private Portal _portal;

}