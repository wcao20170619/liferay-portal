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

import {waitForElementToBeRemoved} from '@testing-library/dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import ListApps from '../../../../src/main/resources/META-INF/resources/js/pages/apps/ListApps.es';
import AppContextProviderWrapper from '../../AppContextProviderWrapper.es';
import {RESPONSES} from '../../constants.es';

describe('ListApp', () => {
	afterEach(cleanup);

	it('renders', async () => {
		fetch.mockResponse(JSON.stringify(RESPONSES.ONE_ITEM));

		const {asFragment} = render(
			<ListApps match={{params: {dataDefinitionId: ''}, url: '/'}} />,
			{wrapper: AppContextProviderWrapper}
		);

		await waitForElementToBeRemoved(() =>
			document.querySelector('span.loading-animation')
		);

		expect(asFragment()).toMatchSnapshot();
	});

	it('renders with dataDefinitionId and 5 apps in the list', async () => {
		fetch.mockResponse(JSON.stringify(RESPONSES.MANY_ITEMS(5)));

		const {container} = render(
			<ListApps match={{params: {dataDefinitionId: '1'}, url: '/'}} />,
			{wrapper: AppContextProviderWrapper}
		);

		await waitForElementToBeRemoved(() =>
			document.querySelector('span.loading-animation')
		);

		expect(container.querySelector('tbody').children.length).toEqual(5);
	});

	it('renders with no dataDefinitionId and 5 apps in the list', async () => {
		fetch.mockResponse(JSON.stringify(RESPONSES.MANY_ITEMS(5)));

		const {container} = render(
			<ListApps match={{params: {dataDefinitionId: ''}, url: '/'}} />,
			{wrapper: AppContextProviderWrapper}
		);

		await waitForElementToBeRemoved(() =>
			document.querySelector('span.loading-animation')
		);

		expect(container.querySelector('tbody').children.length).toEqual(5);
	});

	it('renders with empty state', async () => {
		fetch.mockResponse(JSON.stringify(RESPONSES.NO_ITEMS));

		const {container} = render(
			<ListApps match={{params: {dataDefinitionId: ''}, url: '/'}} />,
			{wrapper: AppContextProviderWrapper}
		);

		await waitForElementToBeRemoved(() =>
			document.querySelector('span.loading-animation')
		);

		expect(
			container.querySelector('.taglib-empty-result-message-title')
				.textContent
		).toEqual('there-are-no-apps-yet');
	});
});
