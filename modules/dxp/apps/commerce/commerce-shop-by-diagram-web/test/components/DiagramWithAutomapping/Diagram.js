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

import Diagram from '../../../src/main/resources/META-INF/resources/js/DiagramWithAutomapping/Diagram';

import '@testing-library/jest-dom/extend-expect';
import {act, fireEvent, render, waitFor} from '@testing-library/react';
import fetchMock from 'fetch-mock';
import React from 'react';

import {
	adminMappedProducts,
	defaultDiagramProps,
	frontStoreMappedProducts,
	mockCommonEndpoints,
} from '../../utilities';

import '../../../dev/public/js/static-env-utils';

const svgContent = `
    <svg>
        <g class="sequences">
            <text>first</text>
            <text>second</text>
            <text>third</text>
            <text>fourth</text>
        </g>
    </svg>
`;

describe('Diagram', () => {
	beforeEach(() => {
		mockCommonEndpoints();

		fetchMock.mock(defaultDiagramProps.imageURL, () => {
			return {
				body: svgContent,
				headers: new Headers({'Content-Type': 'text/html'}),
			};
		});
	});

	afterEach(() => {
		fetchMock.restore();
	});

	describe('SVG Renderer Admin', () => {
		let diagram;
		let pinsNodes;

		beforeEach(async () => {
			await act(async () => {
				diagram = await render(
					<Diagram {...defaultDiagramProps} isAdmin={true} />
				);
			});

			await waitFor(async () =>
				diagram.container.querySelector('.sequences')
			);

			pinsNodes = diagram.container.querySelectorAll(
				defaultDiagramProps.pinsCSSSelectors
			);
		});

		afterEach(() => {
			diagram?.unmount();
		});

		it('must render a Diagram', () => {
			expect(diagram.container).toBeInTheDocument();
		});

		it('must not show a pin size button when on admin', () => {
			expect(diagram.container).not.toHaveTextContent('pin-size');
		});

		it('must render a SVG image within a component', () => {
			const svg = diagram.container.querySelector('svg .sequences');

			expect(svg).toBeInTheDocument();
		});

		it('must show the pins', () => {
			expect(pinsNodes.length).toBeGreaterThan(0);
		});

		it('must show a tooltip when a pin is clicked', () => {
			fireEvent.click(pinsNodes[0]);

			const tooltip = document.querySelector('.diagram-tooltip');

			expect(tooltip).toBeInTheDocument();
		});

		it('must handle linked skus', () => {
			const skuNode = pinsNodes[0];
			const skuMappedProduct = adminMappedProducts[0];

			fireEvent.click(skuNode);

			const tooltip = document.querySelector('.diagram-admin-tooltip');

			expect(tooltip).toBeInTheDocument();

			const sequenceInput = document.getElementById('sequenceInput');
			const typeInput = document.getElementById('typeInput');
			const quantityInput = document.getElementById('quantityInput');

			expect(sequenceInput.value).toBe(skuNode.textContent);

			expect(typeInput.value).toBe(skuMappedProduct.type);

			expect(Number(quantityInput.value)).toBe(
				Number(skuMappedProduct.quantity)
			);
		});

		it('must handle linked diagrams', () => {
			const diagramNode = pinsNodes[1];
			const skuMappedProduct = adminMappedProducts[1];

			fireEvent.click(diagramNode);

			const tooltip = document.querySelector('.diagram-admin-tooltip');

			expect(tooltip).toBeInTheDocument();

			const sequenceInput = document.getElementById('sequenceInput');
			const typeInput = document.getElementById('typeInput');
			const quantityInput = document.getElementById('quantityInput');

			expect(sequenceInput.value).toBe(diagramNode.textContent);

			expect(typeInput.value).toBe(skuMappedProduct.type);

			expect(quantityInput).not.toBeInTheDocument();
		});
	});

	describe('SVG Renderer Frontstore', () => {
		let diagram;

		beforeEach(async () => {
			await act(async () => {
				diagram = await render(
					<Diagram {...defaultDiagramProps} isAdmin={false} />
				);
			});

			await waitFor(async () =>
				diagram.container.querySelector('.sequences')
			);
		});

		afterEach(() => {
			diagram?.unmount();
		});

		it('must not show a pin size button when on the frontStore', async () => {
			expect(diagram.container).not.toHaveTextContent('pin-size');
		});

		it('must show the pins', async () => {
			const pinsNodes = diagram.container.querySelectorAll(
				defaultDiagramProps.pinsCSSSelectors
			);

			expect(pinsNodes.length).toBeGreaterThan(0);
		});

		it('must do nothing when an unmapped pin is clicked', () => {
			const unselectablePins = diagram.container.querySelectorAll(
				defaultDiagramProps.pinsCSSSelectors + `:not(.pin)`
			);

			unselectablePins.forEach((unselectablePin) => {
				fireEvent.click(unselectablePin);

				const tooltip = document.querySelector(
					'.diagram-storefront-tooltip'
				);

				expect(tooltip).not.toBeInTheDocument();
			});
		});

		it('must show consistent data within the tooltip', async () => {
			const pinNodes = diagram.container.querySelectorAll('.pin');

			expect(pinNodes.length).toBe(3);

			fireEvent.click(pinNodes[0]);

			const tooltip = document.querySelector(
				'.diagram-storefront-tooltip'
			);

			const image = tooltip.querySelector('.sticker-img');
			const link = tooltip.querySelector('a');
			const title = tooltip.querySelector('h4');

			const productName =
				frontStoreMappedProducts[0].productName[
					Liferay.ThemeDisplay.getLanguageId()
				];

			expect(image.alt).toBe(productName);
			expect(image.src).toContain(frontStoreMappedProducts[0].thumbnail);
			expect(link.href).toContain(
				frontStoreMappedProducts[0].urls[
					Liferay.ThemeDisplay.getLanguageId()
				]
			);
			expect(title.textContent).toBe(productName);
		});
	});
});
