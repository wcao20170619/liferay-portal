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

import {fireEvent, render, within} from '@testing-library/react';
import React from 'react';

import EditBlueprint from '../../../src/main/resources/META-INF/resources/js/edit_blueprint/index';
import {DEFAULT_BASELINE_ELEMENTS} from '../../../src/main/resources/META-INF/resources/js/utils/data';
const Utils = require('../../../src/main/resources/META-INF/resources/js/utils/utils');
import {SELECTED_ELEMENTS} from '../mocks/data';

import '@testing-library/jest-dom/extend-expect';

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/shared/CodeMirrorEditor',
	() => ({onChange, value}) => (
		<textarea aria-label="text-area" onChange={onChange} value={value} />
	)
);

// Prevents "TypeError: Liferay.component is not a function" error on openToast

Utils.openSuccessToast = jest.fn();

function renderEditBlueprint(props) {
	return render(
		<EditBlueprint
			context={{
				availableLanguages: {},
				contextPath: '',
				defaultLocale: 'en_US',
				locale: 'en_US',
				namespace:
					'_com_liferay_portal_search_tuning_gsearch_configuration_web_internal_portlet_SearchConfigurationAdminPortlet_',
			}}
			props={{
				blueprintId: '0',
				blueprintType: 0,
				entityJSON: {},
				initialDescription: {},
				initialTitle: {
					'en-US': 'Test Title',
				},
				redirectURL: '',
				searchableAssetTypes: [],
				submitFormURL: '',
				...props,
			}}
		/>
	);
}

describe('EditBlueprint', () => {
	it('renders the configuration set form', () => {
		const {container} = renderEditBlueprint();

		expect(container).not.toBeNull();
	});

	it('renders the default query element', () => {
		const {container} = renderEditBlueprint();

		const {getByText} = within(container.querySelector('.builder'));

		DEFAULT_BASELINE_ELEMENTS.map((element) =>
			getByText(element.elementTemplateJSON.title['en_US'])
		);
	});

	it('adds additional query element from sidebar', () => {
		const {container, getByLabelText} = renderEditBlueprint();

		const elementCountBefore = container.querySelectorAll(
			'.configuration-element-sheet'
		).length;

		fireEvent.mouseOver(container.querySelectorAll('.list-group-title')[1]);

		fireEvent.click(getByLabelText('add'));

		const elementCountAfter = container.querySelectorAll(
			'.configuration-element-sheet'
		).length;

		expect(elementCountAfter).toBe(elementCountBefore + 1);
	});

	it('enables removal of additional query elements', () => {
		const {
			container,
			getAllByLabelText,
			getAllByText,
		} = renderEditBlueprint({
			props: {
				blueprintId: '1',
				initialSelectedElementsString: JSON.stringify({
					query_configuration: SELECTED_ELEMENTS,
				}),
			},
		});

		const elementCountBefore = container.querySelectorAll(
			'.configuration-element-sheet'
		).length;

		fireEvent.click(getAllByLabelText('dropdown')[0]);

		fireEvent.click(getAllByText('remove')[0]);

		const elementCountAfter = container.querySelectorAll(
			'.configuration-element-sheet'
		).length;

		expect(elementCountAfter).toBe(elementCountBefore - 1);
	});
});
