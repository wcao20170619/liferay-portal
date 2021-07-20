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

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import PreviewSidebar from '../../../src/main/resources/META-INF/resources/js/edit_blueprint/PreviewSidebar';
import {mockSearchResults} from '../mocks/data';

import '@testing-library/jest-dom/extend-expect';

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/shared/CodeMirrorEditor',
	() => ({onChange, value}) => (
		<textarea aria-label="text-area" onChange={onChange} value={value} />
	)
);

const COLLAPSED_VIEW = ['type', 'description', 'date', 'userName'];
const SEARCH_RESULTS = mockSearchResults();

function renderPreviewSidebar(props) {
	return render(
		<PreviewSidebar
			loading={false}
			onClose={jest.fn()}
			onFetchResults={jest.fn()}
			results={SEARCH_RESULTS}
			visible={true}
			{...props}
		/>
	);
}

describe('PreviewSidebar', () => {
	global.URL.createObjectURL = jest.fn();

	it('renders the preview', () => {
		const {container} = renderPreviewSidebar();

		expect(container).not.toBeNull();
	});

	it('renders the introduction', () => {
		const {getByText} = renderPreviewSidebar({
			loading: false,
			results: {},
		});

		getByText('perform-a-search-to-preview-your-blueprints-search-results');
	});

	it('renders the loading icon', () => {
		const {container} = renderPreviewSidebar({
			loading: true,
			results: {},
		});

		container.querySelector('.loading-animation');
	});

	it('renders the titles for the search results', () => {
		const {getByText} = renderPreviewSidebar();

		SEARCH_RESULTS.hits.map((result) => {
			getByText(result.b_title);
		});
	});

	it('expands the result when clicked on', () => {
		const {getAllByLabelText, queryAllByText} = renderPreviewSidebar();

		fireEvent.click(getAllByLabelText('expand')[0]);

		Object.keys(SEARCH_RESULTS.hits[0]).map((key) => {
			if (!COLLAPSED_VIEW.includes(key)) {
				queryAllByText(`${SEARCH_RESULTS.hits[0][key]}`);
			}
		});
	});

	it('renders warning messages', () => {
		const errors = [
			{
				msg: 'invalid',
				severity: 'error',
			},
			{
				msg: 'missing text',
				severity: 'error',
			},
		];

		const {getByText} = renderPreviewSidebar({
			loading: false,
			results: {
				errors,
			},
		});

		errors.map((error) => getByText(error.msg));
	});
});
