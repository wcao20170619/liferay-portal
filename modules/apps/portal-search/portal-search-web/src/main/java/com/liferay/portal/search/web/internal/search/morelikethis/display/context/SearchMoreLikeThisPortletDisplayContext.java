package com.liferay.portal.search.web.internal.search.morelikethis.display.context;

import java.io.Serializable;

import com.liferay.portal.kernel.dao.search.SearchContainer;

public class SearchMoreLikeThisPortletDisplayContext implements Serializable  {
	
	private static final long serialVersionUID = 1L;

	public SearchContainer<SearchMoreLikeThisEntryDisplayContext> getSearchContainer() {
		return _searchContainer;
	}
	
	public void setSearchContainer(
		SearchContainer<SearchMoreLikeThisEntryDisplayContext> searchContainer) {

		_searchContainer = searchContainer;
	}
	
	private SearchContainer<SearchMoreLikeThisEntryDisplayContext> _searchContainer;

}
