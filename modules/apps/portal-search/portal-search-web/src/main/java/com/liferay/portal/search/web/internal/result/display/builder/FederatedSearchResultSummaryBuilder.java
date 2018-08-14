package com.liferay.portal.search.web.internal.result.display.builder;

import com.liferay.portal.kernel.search.Document;

/**
 * @author Bryan Engler
 */
public interface FederatedSearchResultSummaryBuilder {

	public FederatedSearchSummary getSummary(Document document);

	public String getSource();
}
