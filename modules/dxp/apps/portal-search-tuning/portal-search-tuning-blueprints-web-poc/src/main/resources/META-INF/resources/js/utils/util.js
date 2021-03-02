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

/**
 * Sets parameters on a url.
 * @param {String} baseUrl The base url to modify
 * @param {Object} params Key-value pairs for parameters to set.
 * @return {String} The complete url string.
 */
export function buildUrl(baseUrl, params) {
	const url = new URL(baseUrl);

	const searchParams = url.searchParams;

	if (params) {
		Object.keys(params).forEach((key) =>
			searchParams.set(key, params[key])
		);
	}

	return url.href;
}

/**
 * Utility function for filtering out a key in an object.
 *
 * Examples:
 * filterObject({file: 'pdf', name: 'Workplace Training', size: '30'}, ['size'])
 * => {file: 'pdf', name: 'Workplace Training'}
 *
 * @param {Object} json Object
 * @param {Array} removed List of keys to be removed
 * @return {Object} Object that has been filtered out
 */
export function filterObject(json, removed) {
	return Object.keys(json)
		.filter((key) => !removed.includes(key))
		.reduce((obj, key) => {
			obj[key] = json[key];

			return obj;
		}, {});
}

/**
 * Utility function for formatting date into YYYY-MM-DD.
 *
 * Examples:
 * formatDate('Tue Aug 04 2020 12:00:00 GMT-0700 (Pacific Daylight Time)')
 * => '2020-08-04'
 *
 * @param {string} date Date in string that needs to be formatted
 * @return {string} Date in YYYY-MM-DD
 */
export function formatDate(date) {
	var d = new Date(date),
		month = '' + (d.getMonth() + 1),
		day = '' + d.getDate(),
		year = d.getFullYear();

	if (month.length < 2) {
		month = '0' + month;
	}
	if (day.length < 2) {
		day = '0' + day;
	}

	return [year, month, day].join('-');
}

/**
 * Utility function for formatting the facets for fetchURL. Turns the value into a
 * concatenated string of the labels, and does not include empty lists of facets.
 *
 * Examples:
 * const sampleFacets = {
	entryClassName: [
		{frequency: '1586', name: 'com.liferay.wiki.model.WikiPage'},
		{frequency: '110', name: 'com.liferay.message.boards.model.MBMessage'},
	],
	extension: [
		{frequency: 113, name: 'pdf', text: 'pdf (113)'},
		{frequency: 21, name: 'war', text: 'war (21)'},
	],
};
 * formatFacets(sampleFacets, '', 'name')
 * => {entryClassName: 'com.liferay.wiki.model.WikiPage,com.liferay.message.boards.model.MBMessage', extension: 'pdf,war'}
 *
 * @param {string} facets Object with the list of values as an array of objects
 * @param {string} namespace Namespace to insert before param
 * @param {label} string Name of the key being used as part of the concatenated string
 * @return {Object} Object formatted with the list of values as a concatenated string
 */
export function formatFacets(facets, namespace, label) {
	const facetParams = {};

	Object.keys(facets).map((param) => {
		if (facets[param].length !== 0) {
			facetParams[`${namespace}${param}`] = facets[param]
				.map((facet) => facet[label])
				.join();
		}
	});

	return facetParams;
}

/**
 * Utility function for determining whether the end date is later than start.
 *
 * Examples:
 * validDateRange('Tue Aug 04 2020 12:00:00 GMT-0700 (Pacific Daylight Time)',
 * 	'Sat Aug 29 2020 12:00:00 GMT-0700 (Pacific Daylight Time)')
 * => true
 *
 * @param {string} start Beginning date
 * @param {string} end End date
 * @return {boolean} Returns true if end date is past start date
 */
export function validDateRange(start, end) {
	const startDate = new Date(start);
	const endDate = new Date(end);

	return startDate.getTime() <= endDate.getTime();
}
