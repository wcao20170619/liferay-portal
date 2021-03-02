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

import {fetch, navigate} from 'frontend-js-web';
import {PropTypes} from 'prop-types';
import React, {useCallback, useContext, useRef, useState} from 'react';

import ThemeContext from '../ThemeContext';
import {DEFAULT_FRAGMENT} from '../utils/data';
import {openErrorToast} from '../utils/utils';
import Builder from './Builder';
import PageToolbar from './PageToolbar';
import Sidebar from './Sidebar';

const DEFAULT_SELECTED_FRAGMENTS = [
	{
		...DEFAULT_FRAGMENT,
		id: 0,
		jsonString: JSON.stringify(DEFAULT_FRAGMENT, null, '\t'),
	},
];

function BlueprintForm({
	blueprintId,
	blueprintType,
	initialDescription = {},
	initialQueryConfiguration,
	initialTitle = {},
	redirectURL = '',
	submitFormURL = '',
}) {
	const {namespace} = useContext(ThemeContext);

	const [showSidebar] = useState(true);

	const form = useRef();

	const fragmentIdCounter = useRef(1); // 0 reserved for default fragment

	const [isSubmitting, setIsSubmitting] = useState(false);

	const [selectedFragments, setSelectedFragments] = useState(
		blueprintId !== '0'
			? initialQueryConfiguration.map((configString) => ({
					...JSON.parse(configString),
					id: fragmentIdCounter.current++,
					jsonString: configString,
			  }))
			: DEFAULT_SELECTED_FRAGMENTS
	);

	const onAddFragment = useCallback((fragment) => {
		setSelectedFragments((selectedFragments) => [
			{
				...fragment,
				id: fragmentIdCounter.current++,
				jsonString: JSON.stringify(fragment, null, '\t'),
			},
			...selectedFragments,
		]);
	}, []);

	const deleteFragment = useCallback((id) => {
		setSelectedFragments((selectedFragments) =>
			selectedFragments.filter((item) => item.id !== id)
		);
	}, []);

	const handleSubmit = useCallback(
		(event) => {
			event.preventDefault();

			setIsSubmitting(true);

			const formData = new FormData(form.current);

			// JSON needs to be stringified as an array. We have to parse the
			// jsonString first to avoid performing stringify twice.

			try {
				formData.append(
					`${namespace}queryConfiguration`,
					JSON.stringify(
						selectedFragments.map((fragment) =>
							JSON.parse(fragment.jsonString)
						)
					)
				);
			}
			catch {
				openErrorToast({
					message: Liferay.Language.get('the-json-is-invalid'),
				});

				setIsSubmitting(false);

				return;
			}

			formData.append(`${namespace}type`, blueprintType);
			formData.append(`${namespace}blueprintId`, blueprintId);
			formData.append(`${namespace}redirect`, redirectURL);

			return fetch(submitFormURL, {
				body: formData,
				method: 'POST',
			})
				.then((response) => response.json())
				.then((responseContent) => {
					if (
						Object.prototype.hasOwnProperty.call(
							responseContent,
							'errors'
						)
					) {
						responseContent.errors.forEach((message) =>
							openErrorToast({message})
						);

						setIsSubmitting(false);
					}
					else {
						navigate(redirectURL);
					}
				})
				.catch(() => {
					openErrorToast();

					setIsSubmitting(false);
				});
		},
		[
			blueprintId,
			blueprintType,
			namespace,
			redirectURL,
			selectedFragments,
			submitFormURL,
		]
	);

	const updateFragment = useCallback((index, fragment) => {
		setSelectedFragments((selectedFragments) => [
			...selectedFragments.slice(0, index),
			fragment,
			...selectedFragments.slice(index + 1),
		]);
	}, []);

	return (
		<form ref={form}>
			<PageToolbar
				initialDescription={initialDescription}
				initialTitle={initialTitle}
				isSubmitting={isSubmitting}
				onCancel={redirectURL}
				onSubmit={handleSubmit}
			/>

			{showSidebar && <Sidebar onAddFragment={onAddFragment} />}

			<div className={`${showSidebar ? 'shifted' : ''}`}>
				<Builder
					deleteFragment={deleteFragment}
					selectedFragments={selectedFragments}
					updateFragment={updateFragment}
				/>
			</div>
		</form>
	);
}

BlueprintForm.propTypes = {
	blueprintId: PropTypes.string,
	blueprintType: PropTypes.number,
	initialDescription: PropTypes.object,
	initialQueryConfiguration: PropTypes.arrayOf(PropTypes.string),
	initialTitle: PropTypes.object,
	redirectURL: PropTypes.string,
	submitFormURL: PropTypes.string,
};

export default React.memo(BlueprintForm);