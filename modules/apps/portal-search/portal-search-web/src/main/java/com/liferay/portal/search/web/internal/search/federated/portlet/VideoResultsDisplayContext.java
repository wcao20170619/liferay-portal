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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.portlet.RenderRequest;

import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.result.display.builder.FederatedSearchSummary;

/**
 * @author Wade Cao
 */
public class VideoResultsDisplayContext implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String ATTRIBUTE = "VideoResultsDisplayContext";

	public VideoResultsDisplayContext(
		RenderRequest request, 
		VideoResultsPortletPreferences portletPreferences) {
		_request = request;
		_portletPreferences = portletPreferences;
	}
	
	public Map<String, List<FederatedSearchSummary>> getFederatedSearchSummaries() {
		return _federatedSearchSummaries;
	}
	
	public void setFederatedSearchSummaries(Map<String, List<FederatedSearchSummary>> federatedSearchSummaries) {
		_federatedSearchSummaries = federatedSearchSummaries;
	}
	
	public String getFederatedSearchSourceName() {
		return _portletPreferences.getFederatedSearchSourceName();
	}
	
	public void processVideoId() {
		List<FederatedSearchSummary> federatedSearchSummaries =
			_federatedSearchSummaries.get(getFederatedSearchSourceName());
		if (_pos > -1 && federatedSearchSummaries != null && _pos < federatedSearchSummaries.size()) {
			FederatedSearchSummary federatedSearchSummary = 
				federatedSearchSummaries.get(_pos);
			_id = federatedSearchSummary.getURL();
		}
	}
	
	public String getEmbedURL() {
		StringBundler sb = new StringBundler(12);

		sb.append(HttpUtil.getProtocol(_request));
		sb.append("://www.youtube.com/embed/");
		sb.append(getId());
		sb.append("?wmode=transparent");

		if (isAutoPlay()) {
			sb.append("&amp;autoplay=1");
		}

		if (isClosedCaptioning()) {
			sb.append("&amp;cc_load_policy=1");
		}

		if (!isEnableKeyboardControls()) {
			sb.append("&amp;disablekb=1");
		}

		if (isAnnotations()) {
			sb.append("&amp;iv_load_policy=1");
		}
		else {
			sb.append("&amp;iv_load_policy=3");
		}

		if (isLoop()) {
			sb.append("&amp;loop=1&amp;playlist=");
			sb.append(getId());
		}

		if (Validator.isNotNull(getStartTime())) {
			sb.append("&amp;start=");
			sb.append(getStartTime());
		}

		return sb.toString();
	}

	public String getHeight() {
		
		if (_height != null) { 
			return _height;
		}

		if (isCustomSize()) {
			_height = _portletPreferences.getHeight();
		}
		else {
			String presetSize = getPresetSize();

			String[] dimensions = presetSize.split("x");

			_height = dimensions[1];
		}

		return _height;
	}

	public String getId() {
		if (_id != null) {
			return _id;
		}

		String url = getURL();

		_id = url.replaceAll("^.*?v=([a-zA-Z0-9_-]+).*$", "$1");

		return _id;
	}
	
	public void setId(String id) {
		_id = id;
	}
	
	public int getPos() {
		return _pos;
	}
	
	public void setPos(int pos) {
		_pos = pos;
	}

	public String getImageURL() {
		StringBundler sb = new StringBundler(4);

		sb.append(HttpUtil.getProtocol(_request));
		sb.append("://img.youtube.com/vi/");
		sb.append(getId());
		sb.append("/0.jpg");

		return sb.toString();
	}
	
	public String getImageURL(String id) {
		StringBundler sb = new StringBundler(4);

		sb.append(HttpUtil.getProtocol(_request));
		sb.append("://img.youtube.com/vi/");
		sb.append(id);
		sb.append("/0.jpg");

		return sb.toString();
	}

	public String getPresetSize() {
		if (_presetSize != null) {
			return _presetSize;
		}

		_presetSize = _portletPreferences.getFrameSize();

		return _presetSize;
	}

	public String getStartTime() {
		if (_startTime != null) {
			return _startTime;
		}

		_startTime = _portletPreferences.getStartTime();

		return _startTime;
	}

	public String getURL() {
		if (_url != null) {
			return _url;
		}

		_url = _portletPreferences.getURL();

		return _url;
	}

	public String getWatchURL() {
		return HttpUtil.getProtocol(_request) + "://www.youtube.com/watch?v=" + 
				getId();
	}
	
	public String getWatchURL(String id) {
		return HttpUtil.getProtocol(_request) + "://www.youtube.com/watch?v=" + 
				id;
	}

	public String getWidth() {
	
		if (_width != null) {
			return _width;
		}

		if (isCustomSize()) {
			_width = _portletPreferences.getWidth();
		}
		else {
			String presetSize = getPresetSize();

			String[] dimensions = presetSize.split("x");

			_width = dimensions[0];
		}

		return _width;
	}

	public boolean isAnnotations() {
		if (_annotations != null) {
			return _annotations;
		}

		_annotations = _portletPreferences.isAnnotations();

		return _annotations;
	}

	public boolean isAutoPlay() {
		if (_autoPlay != null) {
			return _autoPlay;
		}

		_autoPlay = _portletPreferences.isAutoPlay();

		return _autoPlay;
	}

	public boolean isClosedCaptioning() {
		if (_closedCaptioning != null) {
			return _closedCaptioning;
		}

		_closedCaptioning = 
			_portletPreferences.isClosedCaptioning();

		return _closedCaptioning;
	}

	public boolean isCustomSize() {
		String presetSize = getPresetSize();

		if (Objects.equals(presetSize, "custom")) {
			return true;
		}

		return false;
	}

	public boolean isEnableKeyboardControls() {
		if (_enableKeyboardControls != null) {
			return _enableKeyboardControls;
		}

		_enableKeyboardControls =
			_portletPreferences.isEnableKeyboardControls();

		return _enableKeyboardControls;
	}

	public boolean isLoop() {
		if (_loop != null) {
			return _loop;
		}

		_loop = _portletPreferences.isLoop();

		return _loop;
	}

	public boolean isShowThumbnail() {
		if (_showThumbnail != null) {
			return _showThumbnail;
		}

		_showThumbnail =
			_portletPreferences.isShowThumbnail();

		return _showThumbnail;
	}
	
	public String getKeyword() {
		return _keyword;
	}
	
	public void setKeyword(String keyword) {
		_keyword = keyword;
	}
	
	private Boolean _annotations;
	private Boolean _autoPlay;
	private Boolean _closedCaptioning;
	private Boolean _enableKeyboardControls;
	private String _height;
	private String _id;
	private Boolean _loop;
	private final VideoResultsPortletPreferences _portletPreferences;
	private String _presetSize;
	private final RenderRequest _request;
	private Boolean _showThumbnail;
	private String _startTime;
	private String _url;
	private String _width;
	private int _pos = -1;
	private String _keyword;
	
	private Map<String, List<FederatedSearchSummary>> _federatedSearchSummaries;

}
