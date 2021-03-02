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

package com.liferay.portal.search.tuning.blueprints.response.internal.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.web.interpreter.SearchResultInterpreter;
import com.liferay.portal.search.web.interpreter.SearchResultInterpreterProvider;

import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Olivia Yu
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = {})
public class ResponseUtil {

	public static int getStart(int totalHits, int pageSize, int start)
		throws ArithmeticException {

		if (totalHits < start) {
			int pageCount = (int)Math.ceil(totalHits * 1.0 / pageSize);

			start = (pageCount - 1) * pageSize;

			if (start < 0) {
				start = 0;
			}
		}

		return start;
	}

	@SuppressWarnings("deprecation")
	public static String getViewURL(
		Document document, PortletRequest portletRequest,
		PortletResponse portletResponse, boolean viewInContext) {

		SearchResultInterpreter searchResultInterpreter =
			_getSearchResultInterpreter();

		LiferayPortletResponse liferayPortletResponse =
			PortalUtil.getLiferayPortletResponse(portletResponse);
		LiferayPortletRequest liferayPortletRequest =
			PortalUtil.getLiferayPortletRequest(portletRequest);

		PortletURL viewContentURL = liferayPortletResponse.createRenderURL();

		String currentURL = _portal.getCurrentURL(portletRequest);

		try {
			viewContentURL.setParameter("mvcPath", "/view_content.jsp");
			viewContentURL.setParameter("redirect", currentURL);
			viewContentURL.setPortletMode(PortletMode.VIEW);
			viewContentURL.setWindowState(WindowState.MAXIMIZED);

			AssetEntry assetEntry = searchResultInterpreter.getAssetEntry(
				document);

			if (assetEntry == null) {
				return viewContentURL.toString();
			}

			viewContentURL.setParameter(
				"assetEntryId", String.valueOf(assetEntry.getEntryId()));
			viewContentURL.setParameter(
				"entryClassName", document.getString(Field.ENTRY_CLASS_NAME));
			viewContentURL.setParameter(
				"entryClassPK", document.getString(Field.ENTRY_CLASS_PK));

			if (!viewInContext) {
				return viewContentURL.toString();
			}

			String viewURL = searchResultInterpreter.getAssetURLViewInContext(
				document, liferayPortletRequest, liferayPortletResponse,
				viewContentURL.toString());

			if (Validator.isNull(viewURL)) {
				return viewContentURL.toString();
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			viewURL = HttpUtil.setParameter(
				viewURL, "inheritRedirect", viewInContext);

			Layout layout = themeDisplay.getLayout();

			String assetEntryLayoutUuid = assetEntry.getLayoutUuid();

			if (Validator.isNotNull(assetEntryLayoutUuid) &&
				!assetEntryLayoutUuid.equals(layout.getUuid())) {

				viewURL = HttpUtil.setParameter(
					viewURL, "redirect", currentURL);
			}

			return viewURL;
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to get search result view URL for class ",
					document.getString(Field.ENTRY_CLASS_NAME),
					" with primary key ",
					document.getString(Field.ENTRY_CLASS_PK)),
				exception);

			return StringPool.BLANK;
		}
	}

	public static String stripHTML(String string, int length) throws Exception {
		if (Validator.isBlank(string)) {
			return string;
		}

		// Replace other than highlight tags

		string = string.replaceAll("<liferay-hl>", "---LR-HL-START---");
		string = string.replaceAll("</liferay-hl>", "---LR-HL-STOP---");
		string = HtmlUtil.stripHtml(string);
		string = string.replaceAll("---LR-HL-START---", "<liferay-hl>");
		string = string.replaceAll("---LR-HL-STOP---", "</liferay-hl>");

		if ((length > -1) && (string.length() > length)) {
			String temp = string.substring(0, length);

			// Check that we are not breaking the HTML

			if (temp.lastIndexOf("<") > temp.lastIndexOf(">")) {
				temp = string.substring(
					0, 1 + string.indexOf('>', temp.lastIndexOf('<')));
			}

			string = temp.concat("...");
		}

		return string;
	}

	@Reference(unbind = "-")
	protected void setPortal(Portal portal) {
		_portal = portal;
	}

	@Reference(unbind = "-")
	protected void setSearchResultInterpreterProvider(
		SearchResultInterpreterProvider searchResultInterpreterProvider) {

		_searchResultInterpreterProvider = searchResultInterpreterProvider;
	}

	private static SearchResultInterpreter _getSearchResultInterpreter() {
		return _searchResultInterpreterProvider.getSearchResultInterpreter(
			"com_liferay_portal_search_tuning_blueprints_" +
				"web_internal_BlueprintsWebPortlet");
	}

	private static final Log _log = LogFactoryUtil.getLog(ResponseUtil.class);

	private static Portal _portal;
	private static SearchResultInterpreterProvider
		_searchResultInterpreterProvider;

}