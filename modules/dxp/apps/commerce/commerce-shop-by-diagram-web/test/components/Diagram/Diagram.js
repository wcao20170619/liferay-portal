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

import Diagram from '../../../src/main/resources/META-INF/resources/js/Diagram/Diagram';

import '@testing-library/jest-dom/extend-expect';
import {act, fireEvent, render} from '@testing-library/react';
import fetchMock from 'fetch-mock';
import React from 'react';

import '../../../dev/public/js/static-env-utils';
import {
	adminPinsData,
	defaultDiagramProps,
	frontStorePinsData,
	load,
	mockCommonEndpoints,
} from '../../utilities';

describe('Diagram', () => {
	beforeEach(() => {
		mockCommonEndpoints();
	});

	afterEach(() => {
		fetchMock.restore();
	});

	describe('Default Renderer Admin', () => {
		let diagram;

		beforeEach(async () => {
			await act(async () => {
				diagram = await render(
					<Diagram {...defaultDiagramProps} isAdmin={true} />
				);
			});

			const image = diagram.container.querySelector('svg image');

			await load(image, 100);
		});

		afterEach(() => {
			diagram?.unmount();
		});

		it('must render a Diagram', async () => {
			expect(diagram.container).toBeInTheDocument();
		});

		it('must show a pin size button when on admin', async () => {
			expect(diagram.container).toHaveTextContent('pin-size');
		});

		it('must show an image within an svg', () => {
			const image = diagram.container.querySelector('image');

			expect(image).toBeInTheDocument();

			expect(image.getAttribute('href')).toBe(
				defaultDiagramProps.imageURL
			);
		});

		it('must show the pins', async () => {
			const pinsNodes = await diagram.findAllByRole('pin');

			const sequences = adminPinsData.map((pin) => pin.sequence);

			expect(sequences.length).toBe(pinsNodes.length);

			pinsNodes.forEach((pin) => {
				expect(sequences).toContain(pin.textContent);
			});
		});

		it('must show a tooltip when a pin is clicked', async () => {
			const pinsNodes = await diagram.findAllByRole('pin');
			fireEvent.click(pinsNodes[0]);

			const tooltip = document.querySelector('.diagram-tooltip');

			expect(tooltip).toBeInTheDocument();
		});

		it('must show consistent data within the tooltip', async () => {
			const pinsNodes = await diagram.findAllByRole('pin');

			pinsNodes.forEach((pin, index) => {
				fireEvent.click(pin);

				const sequenceInput = document.getElementById('sequenceInput');
				const typeInput = document.getElementById('typeInput');
				const quantityInput = document.getElementById('quantityInput');

				expect(sequenceInput.value).toBe(adminPinsData[index].sequence);

				expect(typeInput.value).toBe(
					adminPinsData[index].mappedProduct.type
				);

				if (adminPinsData[index].mappedProduct.type === 'diagram') {
					expect(quantityInput).not.toBeInTheDocument();
				}
				else {
					expect(Number(quantityInput.value)).toBe(
						Number(adminPinsData[index].mappedProduct.quantity)
					);
				}
			});
		});
	});

	describe('Default Renderer Frontstore', () => {
		let diagram;

		beforeEach(async () => {
			await act(async () => {
				diagram = await render(
					<Diagram {...defaultDiagramProps} isAdmin={false} />
				);
			});

			const image = diagram.container.querySelector('svg image');

			await load(image, 100);
		});

		afterEach(() => {
			diagram?.unmount();
		});

		it('must not show a pin size button when on the frontStore', async () => {
			expect(diagram.container).not.toHaveTextContent('pin-size');
		});

		it('must show the pins', async () => {
			const pinsNodes = await diagram.findAllByRole('pin');
			const sequences = frontStorePinsData.map((pin) => pin.sequence);

			expect(sequences.length).toBe(pinsNodes.length);

			pinsNodes.forEach((pin) => {
				expect(sequences).toContain(pin.textContent);
			});
		});

		it('must show consistent data within the tooltip', async () => {
			const pinsNodes = await diagram.findAllByRole('pin');

			fireEvent.click(pinsNodes[0]);

			const tooltip = document.querySelector(
				'.diagram-storefront-tooltip'
			);

			const image = tooltip.querySelector('.sticker-img');
			const link = tooltip.querySelector('a');
			const title = tooltip.querySelector('h4');

			const productName =
				frontStorePinsData[0].mappedProduct.productName[
					Liferay.ThemeDisplay.getLanguageId()
				];

			expect(image.alt).toBe(productName);
			expect(image.src).toContain(
				frontStorePinsData[0].mappedProduct.thumbnail
			);
			expect(link.href).toContain(
				frontStorePinsData[0].mappedProduct.urls[
					Liferay.ThemeDisplay.getLanguageId()
				]
			);
			expect(title.textContent).toBe(productName);
		});
	});
});
