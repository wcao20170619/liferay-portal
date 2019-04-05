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

package com.liferay.portal.search.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.search.ranking.CustomRankingsIndexer;
import com.liferay.portal.search.web.internal.search.results.constants.SearchResultsPortletKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

/**
 * @author Bryan Engler
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + SearchResultsPortletKeys.SEARCH_RESULTS,
		"mvc.command.name=/result/ranking"
	},
	service = MVCActionCommand.class
)
public class ResultRankingMVCActionCommand extends BaseMVCActionCommand{

	@Override
	protected void doProcessAction(
		ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		String index = ParamUtil.getString(actionRequest, "index");
		String keywords = ParamUtil.getString(actionRequest, "keywords");
		String uid = ParamUtil.getString(actionRequest, "uid");
		String[] pinnedDocuments = ParamUtil.getStringValues(
			actionRequest, "pinnedDocuments");
		String[] hiddenDocuments = ParamUtil.getStringValues(
			actionRequest, "hiddenDocuments");

		if (cmd.equals("pin")) {
			sendRedirect(actionRequest, actionResponse);
		}
		else if (cmd.equals("hide")) {
			sendRedirect(actionRequest, actionResponse);
		}
		else if (cmd.equals("add")) {
			String doc = customRankingsIndexer.getCustomRanking(
				index, keywords);

			if (doc == null) {
				customRankingsIndexer.addCustomRanking(
					index, keywords, pinnedDocuments, hiddenDocuments);
			}
			else {
				throw new Exception("ranking already exists for keywords");
			}
		}
		else if (cmd.equals("update")) {
			customRankingsIndexer.updateCustomRanking(
				index, keywords, pinnedDocuments, hiddenDocuments);
		}
		else if (cmd.equals("delete")) {
			customRankingsIndexer.deleteCustomRanking(index, keywords);
		}
	}

	@Reference
	protected CustomRankingsIndexer customRankingsIndexer;
}