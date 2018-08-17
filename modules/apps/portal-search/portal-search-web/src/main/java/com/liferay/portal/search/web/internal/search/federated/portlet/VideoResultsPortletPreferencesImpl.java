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

package com.liferay.portal.search.web.internal.search.federated.portlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.portlet.PortletPreferences;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.web.internal.util.PortletPreferencesHelper;
import com.liferay.portal.search.web.search.request.FederatedSearcher;
/**
 * @author Wade Cao
 */
public class VideoResultsPortletPreferencesImpl implements VideoResultsPortletPreferences {

	public VideoResultsPortletPreferencesImpl(
			Optional<PortletPreferences> portletPreferencesOptional,
			Collection<FederatedSearcher> federatedSearchers) {
		_portletPreferencesHelper = new PortletPreferencesHelper(
				portletPreferencesOptional);
		_federatedSearchers = federatedSearchers;
	}
	
	@Override
	public String getFrameSize() {
		return _portletPreferencesHelper.getString(
				VideoResultsPortletPreferences.PREFERENCE_FRAME_SIZE, "480x360");
	}

	@Override
	public String getHeight() {
		return _portletPreferencesHelper.getString(
			VideoResultsPortletPreferences.PREFERENCE_HEIGHT, "360");
	}

	@Override
	public String getVideoId() {
		return _portletPreferencesHelper.getString(
			VideoResultsPortletPreferences.PREFERENCE_VIDEO_ID, null);
	}

	@Override
	public String getWidth() {
		return _portletPreferencesHelper.getString(
			VideoResultsPortletPreferences.PREFERENCE_WIDTH, "480");
	}

	@Override
	public boolean isCustomSize() {
		return _portletPreferencesHelper.getBoolean(
			VideoResultsPortletPreferences.PREFERENCE_CUSTOM_SIZE, false);
	}

	@Override
	public boolean isShowThumbnail() {
		return _portletPreferencesHelper.getBoolean(
			VideoResultsPortletPreferences.PREFERENCE_SHOW_THUMBNAIL, false);
	}

	@Override
	public boolean isAutoPlay() {
		return _portletPreferencesHelper.getBoolean(
			VideoResultsPortletPreferences.PREFERENCE_AUTO_PLAY, false);
	}

	@Override
	public boolean isLoop() {
		return _portletPreferencesHelper.getBoolean(
			VideoResultsPortletPreferences.PREFERENCE_LOOP, false);
	}

	@Override
	public boolean isEnableKeyboardControls() {
		return _portletPreferencesHelper.getBoolean(
			VideoResultsPortletPreferences.PREFERENCE_ENABLE_KEYBOARD_CONTROLS, true);
	}

	@Override
	public boolean isAnnotations() {
		return _portletPreferencesHelper.getBoolean(
			VideoResultsPortletPreferences.PREFERENCE_ANNOTATIONS, true);
	}

	@Override
	public boolean isClosedCaptioning() {
		return _portletPreferencesHelper.getBoolean(
			VideoResultsPortletPreferences.PREFERENCE_CLOSED_CAPTIONING, false);
	}

	@Override
	public String getStartTime() {
		return _portletPreferencesHelper.getString(
			VideoResultsPortletPreferences.PREFERENCE_START_TIME, StringPool.BLANK);
	}
	

	@Override
	public String getURL() {
		return _portletPreferencesHelper.getString(
			VideoResultsPortletPreferences.PREFERENCE_URL, StringPool.BLANK);
	}
	
	public String[] getFederatedSearchSourceNames() {
		List<String> sources = new ArrayList<>();

		for (FederatedSearcher federatedSearcher : _federatedSearchers) {
			sources.add(federatedSearcher.getSource());
		}

		return ArrayUtil.toStringArray(sources);
	}

	@Override
	public String getFederatedSearchSourceName() {
		return _portletPreferencesHelper.getString(
				VideoResultsPortletPreferences.
				PREFERENCE_KEY_FEDERATED_SEARCH_SOURCE_NAME,
				"federated");
	}
	
	private final PortletPreferencesHelper _portletPreferencesHelper;

	private Collection<FederatedSearcher> _federatedSearchers;
}
