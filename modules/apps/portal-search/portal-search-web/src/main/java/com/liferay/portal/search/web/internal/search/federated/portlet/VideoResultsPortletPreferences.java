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
/**
 * @author Wade Cao
 */
public interface VideoResultsPortletPreferences {
	
	public static final String PREFERENCE_FRAME_SIZE = "frameSize";  
	
	public static final String PREFERENCE_HEIGHT = "height";
	
	public static final String PREFERENCE_WIDTH = "width";
	
	public static final String PREFERENCE_VIDEO_ID = "videoId";
	
	public static final String PREFERENCE_CUSTOM_SIZE = "customSize";
	
	public static final String PREFERENCE_SHOW_THUMBNAIL = "showThumbnail";
	
	public static final String PREFERENCE_AUTO_PLAY = "autoPlay";
	
	public static final String PREFERENCE_LOOP = "loop";
	
	public static final String PREFERENCE_ENABLE_KEYBOARD_CONTROLS = "enableKeyboardControls";
	
	public static final String PREFERENCE_ANNOTATIONS = "annotations";
	
	public static final String PREFERENCE_CLOSED_CAPTIONING = "closedCaptioning";
	
	public static final String PREFERENCE_START_TIME = "startTime";
	
	public static final String PREFERENCE_URL = "url";
	
	public static final String PREFERENCE_KEY_FEDERATED_SEARCH_SOURCE_NAME =
			"federatedSearchSourceName";

	public String getFrameSize();
	
	public String getHeight();
	
	public String getVideoId();
	
	public String getWidth();
	
	public boolean isCustomSize();
	
	public boolean isShowThumbnail();
	
	public boolean isAutoPlay();
	
	public boolean isLoop();
	
	public boolean isEnableKeyboardControls();
	
	public boolean isAnnotations();
	
	public boolean isClosedCaptioning();
	
	public String getStartTime();
	
	public String getURL();
	
	public String[] getFederatedSearchSourceNames();

	public String getFederatedSearchSourceName();
}
