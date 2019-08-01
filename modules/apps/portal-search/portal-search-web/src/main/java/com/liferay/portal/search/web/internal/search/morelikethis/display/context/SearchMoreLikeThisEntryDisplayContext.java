package com.liferay.portal.search.web.internal.search.morelikethis.display.context;

import com.liferay.portal.search.document.Document;

public class SearchMoreLikeThisEntryDisplayContext {
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public String getId() {
		return _id;
	}
	
	public Document getDocument() {
		return _document;
	}

	public void setDocument(Document document) {
		_document = document;
	}

	public void setId(String id) {
		_id = id;
	}
	
	private String _name;
	private String _id;
	private Document _document;

}