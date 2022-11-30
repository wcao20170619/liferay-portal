/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import {INDEX_FIELDS, mockClassNames} from './data';
import {QUERY_SXP_ELEMENTS} from './sxpElements';

function trimURL(url) {
	let trimmedURL = url.href || url; // Handling if url is a URL object or a string

	// Remove baseURL

	if (trimmedURL.startsWith('http://localhost:8080')) {
		trimmedURL = trimmedURL.replace('http://localhost:8080', '');
	}

	// Remove query string parameters

	return trimmedURL.split('?')[0];
}

async function mockFetch(url) {
	switch (trimURL(url)) {
		case '/o/search-experiences-rest/v1.0/field-mapping-infos': {
			return {
				json: async () => ({
					items: INDEX_FIELDS,
					page: 1,
					totalCount: INDEX_FIELDS.length,
				}),
				ok: true,
				status: 200,
			};
		}
		case '/o/search-experiences-rest/v1.0/keyword-query-contributors': {
			return {
				json: async () => ({
					items: mockClassNames('KeywordQueryContributor'),
					page: 1,
					totalCount: 10,
				}),
				ok: true,
				status: 200,
			};
		}
		case '/o/search-experiences-rest/v1.0/model-prefilter-contributors': {
			return {
				json: async () => ({
					items: mockClassNames('ModelPrefilterContributor'),
					page: 1,
					totalCount: 10,
				}),
				ok: true,
				status: 200,
			};
		}
		case '/o/search-experiences-rest/v1.0/query-prefilter-contributors': {
			return {
				json: async () => ({
					items: mockClassNames('QueryPrefilterContributor'),
					page: 1,
					totalCount: 10,
				}),
				ok: true,
				status: 200,
			};
		}
		case '/o/search-experiences-rest/v1.0/searchable-asset-names/en_US': {
			return {
				json: async () => ({
					items: mockClassNames('SearchableAssetType'),
					page: 1,
					totalCount: 10,
				}),
				ok: true,
				status: 200,
			};
		}
		case '/o/search-experiences-rest/v1.0/sxp-elements': {
			return {
				json: async () => ({
					items: QUERY_SXP_ELEMENTS,
					page: 1,
					totalCount: QUERY_SXP_ELEMENTS.length,
				}),
				ok: true,
				status: 200,
			};
		}
		default: {
			console.warn(`Unhandled request: ${url}`);

			return {
				json: async () => ({
					items: [],
					page: 1,
					totalCount: 0,
				}),
				ok: true,
				status: 200,
			};
		}
	}
}

beforeAll(() => jest.spyOn(window, 'fetch'));

beforeEach(() => window.fetch.mockImplementation(mockFetch));
