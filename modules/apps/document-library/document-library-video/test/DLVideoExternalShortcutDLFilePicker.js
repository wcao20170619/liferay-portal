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
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';

import DLVideoExternalShortcutDLFilePicker from '../src/main/resources/META-INF/resources/js/DLVideoExternalShortcutDLFilePicker';

global.onFilePickCallback = jest.fn();

const defaultProps = {
	getDLVideoExternalShortcutFieldsURL:
		'http://localhost/getDLVideoExternalShortcutFieldsURL',
	namespace: 'namespace',
	onFilePickCallback: 'onFilePickCallback',
};

const renderComponent = (props) =>
	render(<DLVideoExternalShortcutDLFilePicker {...props} />);

describe('DLVideoExternalShortcutDLFilePicker', () => {
	describe('when rendered with the default props', () => {
		let result;

		beforeEach(() => {
			result = renderComponent(defaultProps);
		});

		it('has a url input', () => {
			expect(result.getByLabelText('video-url')).toBeInTheDocument();
		});

		it('has a video preview placeholder with video icon', () => {
			expect(
				document.querySelector(
					'.video-preview .video-preview-placeholder'
				)
			).toBeInTheDocument();
			expect(
				document.querySelector('svg.lexicon-icon-video')
			).toBeInTheDocument();
		});
	});

	describe('when rendered with initial video', () => {
		let result;
		const props = {
			dlVideoExternalShortcutHTML: '<iframe data-video-liferay></iframe>',
			dlVideoExternalShortcutURL: 'VIDEO-URL',
		};

		beforeEach(() => {
			result = renderComponent({
				...defaultProps,
				...props,
			});
		});

		it('has a url input filled with the video url', () => {
			expect(result.getByLabelText('video-url').value).toBe(
				props.dlVideoExternalShortcutURL
			);
		});

		it('has a video preview with embedded iframe', () => {
			expect(
				document.querySelector('iframe[data-video-liferay]')
			).toBeInTheDocument();
		});
	});

	describe('when there is a valid server response', () => {
		const responseFields = {
			DESCRIPTION: 'DESCRIPTION',
			HTML: '<iframe data-video-liferay></iframe>',
			THUMBNAIL_URL: 'https://thumbnail-url',
			TITLE: 'VIDEO TITLE',
			URL: 'https://video-url.com',
		};

		beforeEach(async () => {
			jest.useFakeTimers();

			fetch.mockResponseOnce(JSON.stringify(responseFields));

			const {getByLabelText} = renderComponent(defaultProps);

			fireEvent.change(getByLabelText('video-url'), {
				target: {value: 'https://video-url.com'},
			});

			jest.advanceTimersByTime(500);

			await waitForElementToBeRemoved(() =>
				document.querySelector('span.loading-animation')
			);
		});

		afterEach(() => {
			jest.clearAllTimers();
			jest.clearAllMocks();
		});

		afterAll(() => {
			jest.useRealTimers();
		});

		it('sends a GET request to the server with supported URL', () => {
			expect(fetch).toHaveBeenCalledWith(
				'http://localhost/getDLVideoExternalShortcutFieldsURL?namespacedlVideoExternalShortcutURL=https%3A%2F%2Fvideo-url.com',
				expect.anything()
			);
		});

		it('updates the video preview with the response markup from the server', () => {
			expect(
				document.querySelector('iframe[data-video-liferay]')
			).toBeInTheDocument();
		});

		it('calls the onFilePickCallback with the responseFields', () => {
			expect(window.onFilePickCallback).toHaveBeenCalledWith(
				responseFields
			);
		});
	});

	describe('when there is an invalid server response', () => {
		let result;

		beforeEach(async () => {
			jest.useFakeTimers();
			fetch.mockResponseOnce('');

			result = renderComponent(defaultProps);
			const {getByLabelText} = result;

			fireEvent.change(getByLabelText('video-url'), {
				target: {value: 'https://unsupported-video-url.com'},
			});

			jest.advanceTimersByTime(500);

			await waitForElementToBeRemoved(() =>
				document.querySelector('span.loading-animation')
			);
		});

		afterEach(() => {
			jest.clearAllTimers();
			jest.clearAllMocks();
		});

		afterAll(() => {
			jest.useRealTimers();
		});

		it('sends a GET request to the server with unsupported URL', () => {
			expect(fetch).toHaveBeenCalledWith(
				'http://localhost/getDLVideoExternalShortcutFieldsURL?namespacedlVideoExternalShortcutURL=https%3A%2F%2Funsupported-video-url.com',
				expect.anything()
			);
		});

		it('updates the video preview with error', () => {
			expect(
				result.getByText('sorry,-this-platform-is-not-supported')
			).toBeInTheDocument();
		});

		it('does not call the onFilePickCallback', () => {
			expect(window.onFilePickCallback).not.toBeCalled();
		});
	});
});
