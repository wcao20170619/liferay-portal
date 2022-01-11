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

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import ChangeDefaultLanguage from '../../../src/main/resources/META-INF/resources/js/ChangeDefaultLanguage.es';

const defaultStrings = {
	ca_ES: 'Catalan (ES)',
	en_US: 'English (US)',
	es_ES: 'Spanish (ES)',
};

const defaultLanguages = [
	{icon: 'en-US', label: 'en_US'},
	{icon: 'es-ES', label: 'es_ES'},
	{icon: 'ca-ES', label: 'ca_ES'},
];

function _renderChangeDefaultLanguageComponent({
	defaultLanguage = 'en_US',
	languages = defaultLanguages,
	strings = defaultStrings,
} = {}) {
	return render(
		<ChangeDefaultLanguage
			defaultLanguage={defaultLanguage}
			languages={languages}
			strings={strings}
		/>,
		{
			baseElement: document.body,
		}
	);
}

describe('ChangeDefaultLanguage', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('render', () => {
		const {getByText} = _renderChangeDefaultLanguageComponent();

		expect(getByText('change')).toBeTruthy();
	});

	it('render the default language', () => {
		const {getByText} = _renderChangeDefaultLanguageComponent({
			defaultLanguage: 'es_ES',
		});

		expect(getByText('Spanish (ES)')).toBeTruthy();
	});

	it('change default language', async () => {
		const {
			findByText,
			getByText,
			getByTitle,
		} = _renderChangeDefaultLanguageComponent();

		fireEvent.click(getByTitle('es_ES'));

		await findByText('Spanish (ES)');

		expect(getByText('Spanish (ES)')).toBeTruthy();
	});

	it('to fire default locale changed event', () => {
		const {getByTitle} = _renderChangeDefaultLanguageComponent();

		const button = getByTitle('es_ES');

		fireEvent.click(button);

		expect(Liferay.fire).toHaveBeenCalled();

		expect(Liferay.fire.mock.calls[0][0]).toBe(
			'inputLocalized:defaultLocaleChanged'
		);

		expect(Liferay.fire.mock.calls[0][1].item.dataset.value).toBe('es_ES');
	});
});
