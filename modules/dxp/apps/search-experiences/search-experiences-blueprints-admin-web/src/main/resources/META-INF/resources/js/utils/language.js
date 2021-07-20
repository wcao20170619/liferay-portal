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
 * Function to return a new object with renamed keys.
 *
 * Example:
 * renameKeys({"en-US": "Hello", "zh-CN": "Ni Hao"}, (str) => str.replace('-', '_'))
 * => {en_US: "Hello", zh_CN: "Ni Hao"}
 *
 * @param {Object} object Original object
 * @return {Object}
 */
export const renameKeys = (object, func) => {
	const newObj = {};

	Object.keys(object).map((key) => {
		newObj[`${func(key)}`] = object[key];
	});

	return newObj;
};

const SPLIT_REGEX = /({\d+})/g;

/**
 * Utility function for substituting variables into language keys.
 *
 * Examples:
 * sub(Liferay.Language.get('search-x'), ['all'])
 * => 'search all'
 * sub(Liferay.Language.get('search-x'), [<b>all<b>], false)
 * => 'search <b>all</b>'
 *
 * @param {string} langKey This is the language key used from our properties file
 * @param {string} args Arguments to pass into language key
 * @param {string} join Boolean used to indicate whether to call `.join()` on
 * the array before it is returned. Use `false` if subbing in JSX.
 * @return {(string|Array)}
 */
export function sub(langKey, args, join = true) {
	const keyArray = langKey
		.split(SPLIT_REGEX)
		.filter((val) => val.length !== 0);

	for (let i = 0; i < args.length; i++) {
		const arg = args[i];

		const indexKey = `{${i}}`;

		let argIndex = keyArray.indexOf(indexKey);

		while (argIndex >= 0) {
			keyArray.splice(argIndex, 1, arg);

			argIndex = keyArray.indexOf(indexKey);
		}
	}

	return join ? keyArray.join('') : keyArray;
}

/**
 * Used for getting the element title and description. Titles and descriptions
 * handle both string `'title'` and a localized object `{'en_US': 'Title'}`.
 * @param {string|Object} value
 * @param {string} locale
 */
export function getLocalizedText(value, locale) {
	if (!value) {
		return '';
	}
	else if (value[locale]) {
		return value[locale];
	}
	else if (typeof value === 'string' || value instanceof String) {
		return value;
	}
	else if (value[Liferay.ThemeDisplay.getDefaultLanguageId()]) {
		return value[Liferay.ThemeDisplay.getDefaultLanguageId()];
	}
	else if (Object.keys(value).length > 0) {
		return value[Object.keys(value)[0]];
	}

	return '';
}
