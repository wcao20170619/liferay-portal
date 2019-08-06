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

package com.liferay.portal.search.related.results.web.internal.display.context;

/**
 * @author Kevin Tan
 */
public class SearchRelatedResultsDocumentDisplayContext {

	public String getCategoriesString() {
		return _categoriesString;
	}

	public String getContent() {
		return _content;
	}

	public String getCreationDateString() {
		return _creationDateString;
	}

	public String getCreatorUserName() {
		return _creatorUserName;
	}

	public String getIconId() {
		if (_iconId == null) {
			_iconId = "documents-and-media";
		}

		return _iconId;
	}

	public String getModelResource() {
		return _modelResource;
	}

	public String getThumbnailURLString() {
		if (_thumbnailURLString == null) {
			_thumbnailURLString = "";
		}

		return _thumbnailURLString;
	}

	public String getTitle() {
		return _title;
	}

	public String getViewURL() {
		return _viewURL;
	}

	public boolean isTemporarilyUnavailable() {
		return _temporarilyUnavailable;
	}

	public void setCategoriesString(String categoriesString) {
		_categoriesString = categoriesString;
	}

	public void setContent(String content) {
		_content = content;
	}

	public void setCreationDateString(String creationDateString) {
		_creationDateString = creationDateString;
	}

	public void setCreatorUserName(String creatorUserName) {
		_creatorUserName = creatorUserName;
	}

	public void setIconId(String iconId) {
		_iconId = iconId;
	}

	public void setModelResource(String modelResource) {
		_modelResource = modelResource;
	}

	public void setTemporarilyUnavailable(boolean temporarilyUnavailable) {
		_temporarilyUnavailable = temporarilyUnavailable;
	}

	public void setThumbnailURLString(String thumbnailURLString) {
		_thumbnailURLString = thumbnailURLString;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setViewURL(String viewURL) {
		_viewURL = viewURL;
	}

	private String _categoriesString;
	private String _content;
	private String _creationDateString;
	private String _creatorUserName;
	private String _iconId;
	private String _modelResource;
	private boolean _temporarilyUnavailable;
	private String _thumbnailURLString;
	private String _title;
	private String _viewURL;

}