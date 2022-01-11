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

import {act, fireEvent, render, waitFor, within} from '@testing-library/react';
import React from 'react';

import ResultRankingsForm from '../../../src/main/resources/META-INF/resources/js/components/ResultRankingsForm.es';
import {
	FETCH_HIDDEN_DOCUMENTS_URL,
	FETCH_SEARCH_DOCUMENTS_URL,
	FETCH_VISIBLE_DOCUMENTS_URL,
	FORM_NAME,
	VALIDATE_FORM_URL,
} from '../mocks/data.es';

import '@testing-library/jest-dom/extend-expect';

const HIDE_BUTTON_LABEL = 'hide-result';
const PIN_BUTTON_LABEL = 'pin-result';
const SHOW_BUTTON_LABEL = 'show-result';
const UNPIN_BUTTON_LABEL = 'unpin-result';

function renderTestResultRankingsForm(props) {
	return render(
		<ResultRankingsForm
			cancelUrl="cancel"
			fetchDocumentsHiddenUrl={FETCH_HIDDEN_DOCUMENTS_URL}
			fetchDocumentsSearchUrl={FETCH_SEARCH_DOCUMENTS_URL}
			fetchDocumentsVisibleUrl={FETCH_VISIBLE_DOCUMENTS_URL}
			formName={FORM_NAME}
			initialInactive={false}
			searchQuery=""
			validateFormUrl={VALIDATE_FORM_URL}
			{...props}
		/>
	);
}

jest.useFakeTimers();

afterEach(() => {
	act(() => jest.runAllTimers());
});

describe('ResultRankingsForm', () => {
	it('renders the results ranking form', () => {
		const {container} = renderTestResultRankingsForm();

		expect(
			container.querySelector('.result-rankings-form-root')
		).toBeInTheDocument();
	});

	xit.each`
		tab          | expected
		${'visible'} | ${['100', '101', '102', '103', '104', '105', '106', '107', '108', '109']}
		${'hidden'}  | ${['200', '201', '202', '203', '204', '205', '206', '207', '208', '209']}
	`(
		'renders the results ranking items after loading in the $tab tab',
		async ({expected, tab}) => {
			const {
				findByTestId,
				getByTestId,
				getByText,
			} = renderTestResultRankingsForm();

			fireEvent.click(getByText(tab));

			await findByTestId(expected[0]);

			expected.forEach((id) => {
				expect(getByTestId(id)).toBeInTheDocument();
			});
		}
	);

	it.each`
		initialAliases             | addedAliases | expected                           | description
		${['one', 'two', 'three']} | ${[]}        | ${['one', 'two', 'three']}         | ${'initial aliases'}
		${[]}                      | ${['one']}   | ${['one']}                         | ${'added alias'}
		${['one', 'two', 'three']} | ${['four']}  | ${['one', 'two', 'three', 'four']} | ${'added alias with initial'}
		${[]}                      | ${[' ']}     | ${[]}                              | ${'blank alias'}
		${[]}                      | ${[' one ']} | ${['one']}                         | ${'trimmed alias'}
		${['one', 'two', 'three']} | ${['one']}   | ${['one', 'two', 'three']}         | ${'no duplicate aliases'}
	`('renders $description', ({addedAliases, expected, initialAliases}) => {
		const {container} = renderTestResultRankingsForm({
			initialAliases,
		});

		const input = container.querySelector('.form-control-inset');

		addedAliases.forEach((alias) => {
			fireEvent.change(input, {target: {value: alias}});

			fireEvent.keyDown(input, {key: 'Enter', keyCode: 13, which: 13});
		});

		expect(input.getAttribute('value')).toBe('');

		const tagsElement = container.querySelectorAll('.label-item-expand');

		expect(tagsElement).toHaveLength(expected.length);

		tagsElement.forEach((element, i) => {
			expect(element).toHaveTextContent(expected[i]);
		});
	});

	it('removes an initial alias after clicking delete', async () => {
		const {container} = renderTestResultRankingsForm({
			initialAliases: ['one', 'two', 'three'],
		});
		const tagsElementClose = container.querySelectorAll(
			'.label-item-after button'
		);

		fireEvent.click(tagsElementClose[0]);

		const tagsElement = container.querySelectorAll('.label-item-expand');

		expect(tagsElement[0]).not.toHaveTextContent('one');
	});

	it.each`
		id       | button               | selector
		${'100'} | ${HIDE_BUTTON_LABEL} | ${'#hiddenIdsAdded'}
		${'200'} | ${SHOW_BUTTON_LABEL} | ${'#hiddenIdsRemoved'}
	`('updates the $selector', async ({button, id, selector}) => {
		const {
			container,
			getByTestId,
			getByText,
		} = renderTestResultRankingsForm();

		if (selector.includes('Removed')) {
			fireEvent.click(getByText('hidden'));
		}

		await waitFor(() => getByTestId(id));

		fireEvent.click(within(getByTestId(id)).getByTitle(button));

		expect(container.querySelector(selector).value).toEqual(id);
	});

	it.each`
		id       | button               | newButton            | selector
		${'100'} | ${HIDE_BUTTON_LABEL} | ${SHOW_BUTTON_LABEL} | ${'#hiddenIdsAdded'}
		${'200'} | ${SHOW_BUTTON_LABEL} | ${HIDE_BUTTON_LABEL} | ${'#hiddenIdsRemoved'}
	`(
		'updates the $selector back',
		async ({button, id, newButton, selector}) => {
			const {
				container,
				findByTestId,
				getByTestId,
				getByText,
			} = renderTestResultRankingsForm();

			const order = selector.includes('Removed')
				? ['hidden', 'visible']
				: ['visible', 'hidden'];

			fireEvent.click(getByText(order[0]));

			await findByTestId(id);

			fireEvent.click(within(getByTestId(id)).getByTitle(button));

			fireEvent.click(getByText(order[1]));

			fireEvent.click(within(getByTestId(id)).getByTitle(newButton));

			expect(container.querySelector(selector).value).toEqual('');
		}
	);

	it.each`
		id       | button                | expected
		${'105'} | ${PIN_BUTTON_LABEL}   | ${'100,101,102,103,104,105'}
		${'100'} | ${UNPIN_BUTTON_LABEL} | ${'101,102,103,104'}
	`('updates the pinnedIds by $button', async ({button, expected, id}) => {
		const {
			container,
			findByTestId,
			getByTestId,
		} = renderTestResultRankingsForm();

		await findByTestId(id);

		fireEvent.click(within(getByTestId(id)).getByTitle(button));

		expect(container.querySelector('#pinnedIds').value).toEqual(expected);
	});

	it('fetches more results after clicking on load more button', async () => {
		const {
			container,
			findByTestId,
			getByTestId,
		} = renderTestResultRankingsForm();

		await findByTestId('100');

		fireEvent.click(container.querySelector('.load-more-button'));

		await findByTestId('110');

		expect(getByTestId('110')).toHaveTextContent(
			'This is a Document Example'
		);
		expect(getByTestId('119')).toHaveTextContent(
			'This is a Web Content Example'
		);
	});

	it('has the same pinned end index if there are no additional pinned items loaded', async () => {
		const {container, findByTestId} = renderTestResultRankingsForm();

		const pinnedIdsEndIndexInput = container.querySelector(
			'#pinnedIdsEndIndex'
		);

		await findByTestId('100');

		expect(pinnedIdsEndIndexInput.value).toBe('4');

		fireEvent.click(container.querySelector('.load-more-button'));

		await findByTestId('110');

		expect(pinnedIdsEndIndexInput.value).toBe('4');
	});

	it.each`
		state         | newState      | expected
		${'active'}   | ${'inactive'} | ${true}
		${'inactive'} | ${'active'}   | ${false}
	`('updates the state to $newState', async ({expected, newState, state}) => {
		const {container, getByLabelText} = renderTestResultRankingsForm({
			initialInactive: !expected,
		});

		fireEvent.click(getByLabelText(state));

		expect(getByLabelText(newState)).toBeInTheDocument();

		expect(container.querySelector('#inactive').value).toBe(`${expected}`);
	});
});
