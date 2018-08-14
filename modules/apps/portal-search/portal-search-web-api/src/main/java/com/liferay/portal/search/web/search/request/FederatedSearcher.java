package com.liferay.portal.search.web.search.request;

import aQute.bnd.annotation.ProviderType;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;

/**
 * @author Bryan Engler
 */
@ProviderType
public interface FederatedSearcher {
	public Hits getHits(SearchContext searchContext);

	public String getSource();

}
