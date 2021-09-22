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

package com.liferay.portal.search.tuning.rankings.web.internal.portlet.action;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.tuning.rankings.web.internal.constants.ResultRankingsConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class EditRankingMVCActionCommandTest
	extends BaseRankingsPortletActionTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_editRankingMVCActionCommand = new EditRankingMVCActionCommand();

		ReflectionTestUtil.setFieldValue(
			_editRankingMVCActionCommand, "duplicateQueryStringsDetector",
			duplicateQueryStringsDetector);
		ReflectionTestUtil.setFieldValue(
			_editRankingMVCActionCommand, "indexNameBuilder", indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_editRankingMVCActionCommand, "portal", portal);
		ReflectionTestUtil.setFieldValue(
			_editRankingMVCActionCommand, "rankingIndexNameBuilder",
			rankingIndexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_editRankingMVCActionCommand, "rankingIndexReader",
			rankingIndexReader);
		ReflectionTestUtil.setFieldValue(
			_editRankingMVCActionCommand, "rankingStorageAdapter",
			rankingStorageAdapter);
	}

	@Test
	public void testDoProcessActionAddWithException() throws Exception {
		_setUpPortletRequest();

		setUpPortal();
		setUpPortletRequestParamValue(
			_actionRequest, Constants.ADD, Constants.CMD);
		setUpPortalUtil();

		_editRankingMVCActionCommand.doProcessAction(
			_actionRequest, _actionResponse);

		Mockito.verify(
			_actionRequest, Mockito.times(1)
		).setAttribute(
			Mockito.anyString(), Mockito.anyObject()
		);
	}

	@Test
	public void testDoProcessActionWithActivate() throws Exception {
		_setUpPortletRequest();

		setUpPortal();
		setUpPortalUtil();
		setUpPortletRequestParamValue(
			_actionRequest, ResultRankingsConstants.ACTIVATE, Constants.CMD);

		_editRankingMVCActionCommand.doProcessAction(
			_actionRequest, _actionResponse);

		Mockito.verify(
			_actionResponse, Mockito.times(1)
		).sendRedirect(
			Mockito.anyString()
		);
	}

	@Test
	public void testDoProcessActionWithAdd() throws Exception {
		_setUpPortletRequest();
		_setUpPortletURLFactoryUtil();

		setUpDuplicateQueryStringsDetector();
		setUpPortal();
		setUpPortalUtil();
		setUpPortletRequestParamValue(
			_actionRequest, Constants.ADD, Constants.CMD);
		setUpRankingIndexReader();

		_editRankingMVCActionCommand.doProcessAction(
			_actionRequest, _actionResponse);

		Mockito.verify(
			_actionResponse, Mockito.times(1)
		).sendRedirect(
			Mockito.anyString()
		);
	}

	@Test
	public void testDoProcessActionWithDeactivate() throws Exception {
		_setUpPortletRequest();

		setUpPortal();
		setUpPortalUtil();
		setUpPortletRequestParamValue(
			_actionRequest, ResultRankingsConstants.DEACTIVATE, Constants.CMD);

		_editRankingMVCActionCommand.doProcessAction(
			_actionRequest, _actionResponse);

		Mockito.verify(
			_actionResponse, Mockito.times(1)
		).sendRedirect(
			Mockito.anyString()
		);
	}

	@Test
	public void testDoProcessActionWithDelete() throws Exception {
		_setUpPortletRequest();

		setUpPortal();
		setUpPortalUtil();
		setUpPortletRequestParamValue(
			_actionRequest, Constants.DELETE, Constants.CMD);
		setUpPortletRequestParamValue(
			_actionRequest, "resultsRankingUid", "resultsRankingUid");

		_editRankingMVCActionCommand.doProcessAction(
			_actionRequest, _actionResponse);

		Mockito.verify(
			_actionResponse, Mockito.times(1)
		).sendRedirect(
			Mockito.anyString()
		);
	}

	@Test
	public void testDoProcessActionWithUpdate() throws Exception {
		_setUpPortletRequest();

		setUpDuplicateQueryStringsDetector();
		setUpPortal();
		setUpPortalUtil();
		setUpPortletRequestParamValue(
			_actionRequest, Constants.UPDATE, Constants.CMD);
		setUpRankingIndexReader();

		_editRankingMVCActionCommand.doProcessAction(
			_actionRequest, _actionResponse);

		Mockito.verify(
			_actionResponse, Mockito.never()
		).sendRedirect(
			Mockito.anyString()
		);
		Mockito.verify(
			_actionResponse, Mockito.times(1)
		).setRenderParameter(
			Mockito.anyString(), Mockito.anyString()
		);
	}

	@Test
	public void testDoProcessActionWithUpdateException() throws Exception {
		setUpPortal();
		setUpPortalUtil();
		setUpPortletRequestParamValue(
			_actionRequest, Constants.UPDATE, Constants.CMD);

		_editRankingMVCActionCommand.doProcessAction(
			_actionRequest, _actionResponse);

		Mockito.verify(
			_actionResponse, Mockito.times(1)
		).setRenderParameter(
			Mockito.anyString(), Mockito.anyString()
		);
	}

	private void _setUpPortletRequest() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Layout layout = Mockito.mock(Layout.class);

		Mockito.doReturn(
			true
		).when(
			layout
		).isTypeControlPanel();

		Mockito.doReturn(
			layout
		).when(
			themeDisplay
		).getLayout();

		Mockito.doReturn(
			Mockito.mock(LayoutTypePortlet.class)
		).when(
			themeDisplay
		).getLayoutTypePortlet();

		PortletConfig portletConfig = Mockito.mock(PortletConfig.class);

		Mockito.doReturn(
			"thePortletName"
		).when(
			portletConfig
		).getPortletName();

		Mockito.when(
			_actionRequest.getAttribute(Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> {
				String argument = (String)invocationOnMock.getArguments()[0];

				if (argument.equals(WebKeys.THEME_DISPLAY)) {
					return themeDisplay;
				}
				else if (argument.equals(WebKeys.PORTLET_ID)) {
					return "thePortletId";
				}
				else if (argument.equals(JavaConstants.JAVAX_PORTLET_CONFIG)) {
					return portletConfig;
				}
				else {
					return "undefined";
				}
			}
		);
	}

	private void _setUpPortletURLFactoryUtil() {
		PortletURLFactory portletURLFactory = Mockito.mock(
			PortletURLFactory.class);

		Mockito.doReturn(
			Mockito.mock(LiferayPortletURL.class)
		).when(
			portletURLFactory
		).create(
			Matchers.any(PortletRequest.class), Matchers.anyString(),
			Matchers.anyString()
		);

		PortletURLFactoryUtil portletURLFactoryUtil =
			new PortletURLFactoryUtil();

		portletURLFactoryUtil.setPortletURLFactory(portletURLFactory);
	}

	@Mock
	private ActionRequest _actionRequest;

	@Mock
	private ActionResponse _actionResponse;

	private EditRankingMVCActionCommand _editRankingMVCActionCommand;

}