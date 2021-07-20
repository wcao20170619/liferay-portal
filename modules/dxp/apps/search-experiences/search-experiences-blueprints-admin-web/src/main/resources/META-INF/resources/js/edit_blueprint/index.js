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

import ClayBadge from '@clayui/badge';
import ClayButton from '@clayui/button';
import ClayToolbar from '@clayui/toolbar';
import getCN from 'classnames';
import {useFormik} from 'formik';
import {fetch, navigate} from 'frontend-js-web';
import {PropTypes} from 'prop-types';
import React, {useContext, useRef, useState} from 'react';

import ErrorBoundary from '../shared/ErrorBoundary';
import PageToolbar from '../shared/PageToolbar';
import SubmitWarningModal from '../shared/SubmitWarningModal';
import ThemeContext from '../shared/ThemeContext';
import {CUSTOM_JSON_ELEMENT} from '../utils/data';
import {INPUT_TYPES} from '../utils/inputTypes';
import {openErrorToast, openSuccessToast} from '../utils/toasts';
import {getElementOutput, getUIConfigurationValues} from '../utils/utils';
import {
	validateBoost,
	validateJSON,
	validateNumberRange,
	validateRequired,
} from '../utils/validation';
import AddElementSidebar from './AddElementSidebar';
import PreviewSidebar from './PreviewSidebar';
import ClauseContributors from './clause_contributors';
import QueryBuilder from './query_builder';
import Settings from './settings';

// Tabs in display order

/* eslint-disable sort-keys */
const TABS = {
	'query-builder': Liferay.Language.get('query-builder'),
	'clause-contributors': Liferay.Language.get('clause-contributors'),
	settings: Liferay.Language.get('settings'),
};
/* eslint-enable sort-keys */

function EditBlueprintForm({
	blueprintId,
	entityJSON,
	initialConfigurationString = '{}',
	initialDescription = {},
	initialSelectedElementsString = '{}',
	indexFields,
	initialTitle = {},
	keywordQueryContributors,
	modelPrefilterContributors,
	queryElements = [],
	queryPrefilterContributors,
	redirectURL = '',
	searchableAssetTypes,
	searchResultsURL,
	submitFormURL = '',
	validateBlueprintURL,
}) {
	const {namespace} = useContext(ThemeContext);

	const [errors, setErrors] = useState([]);
	const [previewInfo, setPreviewInfo] = useState(() => ({
		loading: false,
		results: {},
	}));
	const [showSidebar, setShowSidebar] = useState(true);
	const [showPreview, setShowPreview] = useState(false);
	const [showSubmitWarningModal, setShowSubmitWarningModal] = useState(false);
	const [tab, setTab] = useState('query-builder');

	const form = useRef();
	const sidebarQueryElements = useRef([
		...queryElements,
		CUSTOM_JSON_ELEMENT,
	]);

	const initialConfiguration = JSON.parse(initialConfigurationString);
	const initialSelectedElements = JSON.parse(initialSelectedElementsString);

	const elementIdCounter = useRef(
		initialSelectedElements['query_configuration'].length
	);

	/**
	 * Abstracts title and description from the existing form, as
	 * query builder has several inputs that should not be included in
	 * submission.
	 * @param {FormData} formData
	 */
	const _appendTitleAndDescription = (formData) => {
		for (const pair of new FormData(form.current).entries()) {
			if (
				pair[0].includes(`${namespace}title`) ||
				pair[0].includes(`${namespace}description`)
			) {
				formData.append(pair[0], pair[1]);
			}
		}
	};

	/**
	 * Formats the form values for the "configuration" parameter to send to
	 * the server. Sets defaults so the JSON.parse calls don't break.
	 * @param {Object} values Form values
	 * @return {String}
	 */
	const _getConfigurationString = ({
		advancedConfig,
		aggregationConfig,
		facetConfig,
		frameworkConfig,
		highlightConfig,
		parameterConfig,
		selectedQueryElements,
		sortConfig,
	}) => {
		return JSON.stringify({
			advanced_configuration: advancedConfig
				? JSON.parse(advancedConfig)
				: {},
			aggregation_configuration: aggregationConfig
				? JSON.parse(aggregationConfig)
				: {},
			facet_configuration: facetConfig ? JSON.parse(facetConfig) : [],
			framework_configuration: frameworkConfig,
			highlight_configuration: highlightConfig
				? JSON.parse(highlightConfig)
				: {},
			parameter_configuration: parameterConfig
				? JSON.parse(parameterConfig)
				: {},
			query_configuration: selectedQueryElements.map(getElementOutput),
			sort_configuration: sortConfig ? JSON.parse(sortConfig) : {},
		});
	};

	const _handleFormikSubmit = async (values) => {
		const formData = new FormData();

		_appendTitleAndDescription(formData);

		try {
			formData.append(
				`${namespace}configuration`,
				_getConfigurationString(values)
			);

			formData.append(
				`${namespace}selectedElements`,
				JSON.stringify({
					query_configuration: values.selectedQueryElements.map(
						(item) =>
							item.uiConfigurationJSON
								? {
										elementTemplateJSON:
											item.elementTemplateJSON,
										uiConfigurationJSON:
											item.uiConfigurationJSON,
										uiConfigurationValues:
											item.uiConfigurationValues,
								  } // Removes ID field
								: {
										elementTemplateJSON: getElementOutput(
											item
										),
								  }
					),
				})
			);
		}
		catch (error) {
			openErrorToast({
				message: Liferay.Language.get(
					'the-configuration-has-missing-or-invalid-values'
				),
			});

			if (process.env.NODE_ENV === 'development') {
				console.error(error);
			}

			return;
		}

		formData.append(`${namespace}blueprintId`, blueprintId);
		formData.append(`${namespace}redirect`, redirectURL);

		try {

			// If the warning modal is already open, assume the form was submitted
			// using the "Continue To Save" action and should skip the schema
			// validation step.

			if (!showSubmitWarningModal) {
				const validateErrors = await fetch(validateBlueprintURL, {
					body: formData,
					method: 'POST',
				}).then((response) => response.json());

				if (validateErrors.errors?.length) {
					setErrors(validateErrors.errors);
					setShowSubmitWarningModal(true);

					return;
				}
			}

			const responseContent = await fetch(submitFormURL, {
				body: formData,
				method: 'POST',
			}).then((response) => response.json());

			if (
				Object.prototype.hasOwnProperty.call(responseContent, 'errors')
			) {
				responseContent.errors.forEach((message) =>
					openErrorToast({message})
				);
			}
			else {
				navigate(redirectURL);
			}
		}
		catch (error) {
			openErrorToast();

			if (process.env.NODE_ENV === 'development') {
				console.error(error);
			}
		}
	};

	const _handleFormikValidate = (values) => {
		const errors = {};

		// Validate the elements added to the query builder

		const selectedQueryElementsArray = [];

		values.selectedQueryElements.map(
			(
				{
					elementTemplateJSON,
					uiConfigurationJSON,
					uiConfigurationValues,
				},
				index
			) => {
				if (!elementTemplateJSON.enabled) {
					return;
				}

				const configErrors = {};

				if (Array.isArray(uiConfigurationJSON?.fieldSets)) {
					uiConfigurationJSON.fieldSets.map(({fields = []}) => {
						fields.map(({name, type, typeOptions = {}}) => {
							const configValue = uiConfigurationValues[name];

							const configError =
								validateRequired(
									configValue,
									type,
									typeOptions.required
								) ||
								validateBoost(configValue, type) ||
								validateNumberRange(
									configValue,
									type,
									typeOptions
								) ||
								validateJSON(configValue, type);

							if (configError) {
								configErrors[name] = configError;
							}
						});
					});
				}
				else if (!uiConfigurationJSON) {
					const configValue =
						uiConfigurationValues?.elementTemplateJSON;

					const configError =
						validateRequired(configValue, INPUT_TYPES.JSON) ||
						validateJSON(configValue, INPUT_TYPES.JSON);

					if (configError) {
						configErrors.elementTemplateJSON = configError;
					}
				}

				if (Object.keys(configErrors).length > 0) {
					selectedQueryElementsArray[index] = {
						uiConfigurationValues: configErrors,
					};
				}
			}
		);

		if (selectedQueryElementsArray.length > 0) {
			errors.selectedQueryElements = selectedQueryElementsArray;
		}

		// Validate all JSON inputs on the settings tab

		[
			'advancedConfig',
			'aggregationConfig',
			'facetConfig',
			'highlightConfig',
			'parameterConfig',
			'sortConfig',
		].map((configName) => {
			const configError = validateJSON(
				values[configName],
				INPUT_TYPES.JSON
			);

			if (configError) {
				errors[configName] = configError;
			}
		});

		return errors;
	};

	const formik = useFormik({
		initialValues: {
			advancedConfig: JSON.stringify(
				initialConfiguration['advanced_configuration'],
				null,
				'\t'
			),
			aggregationConfig: JSON.stringify(
				initialConfiguration['aggregation_configuration'],
				null,
				'\t'
			),
			facetConfig: JSON.stringify(
				initialConfiguration['facet_configuration'],
				null,
				'\t'
			),
			frameworkConfig: initialConfiguration['framework_configuration'],
			highlightConfig: JSON.stringify(
				initialConfiguration['highlight_configuration'],
				null,
				'\t'
			),
			parameterConfig: JSON.stringify(
				initialConfiguration['parameter_configuration'],
				null,
				'\t'
			),
			selectedQueryElements: initialSelectedElements[
				'query_configuration'
			].map((selectedElement, index) => ({
				...selectedElement,
				id: index,
			})),
			sortConfig: JSON.stringify(
				initialConfiguration['sort_configuration'],
				null,
				'\t'
			),
		},
		onSubmit: _handleFormikSubmit,
		validate: _handleFormikValidate,
	});

	const _handleAddElement = (element) => {
		if (formik.touched?.selectedQueryElements) {
			formik.setTouched({
				...formik.touched,
				selectedQueryElements: [
					undefined,
					...formik.touched.selectedQueryElements,
				],
			});
		}

		formik.setFieldValue('selectedQueryElements', [
			{
				...element,
				id: elementIdCounter.current++,
				uiConfigurationValues: getUIConfigurationValues(
					element.uiConfigurationJSON
				),
			},
			...formik.values.selectedQueryElements,
		]);
	};

	const _handleDeleteElement = (id) => {
		const index = formik.values.selectedQueryElements.findIndex(
			(item) => item.id == id
		);

		if (formik.touched?.selectedQueryElements) {
			formik.setTouched({
				...formik.touched,
				selectedQueryElements: formik.touched.selectedQueryElements.filter(
					(_, i) => i !== index
				),
			});
		}

		formik.setFieldValue(
			'selectedQueryElements',
			formik.values.selectedQueryElements.filter((item) => item.id !== id)
		);

		openSuccessToast({
			message: Liferay.Language.get('element-removed'),
		});
	};

	const _handleFetchPreviewSearch = (value, delta, page) => {
		setPreviewInfo((previewInfo) => ({
			...previewInfo,
			loading: true,
		}));

		const formData = new FormData();

		_appendTitleAndDescription(formData);

		try {
			formData.append(
				`${namespace}configuration`,
				_getConfigurationString(formik.values)
			);
		}
		catch (error) {
			setPreviewInfo({
				loading: false,
				results: {
					errors: [
						{
							msg: Liferay.Language.get(
								'the-configuration-has-missing-or-invalid-values'
							),
						},
					],
				},
			});

			if (process.env.NODE_ENV === 'development') {
				console.error(error);
			}

			return;
		}

		formData.append(`${namespace}page`, page);
		formData.append(`${namespace}q`, value);
		formData.append(`${namespace}size`, delta);

		return fetch(searchResultsURL, {
			body: formData,
			method: 'POST',
		})
			.then((response) => response.json())
			.then((responseContent) => {
				setPreviewInfo({
					loading: false,
					results: {
						...responseContent,
						warnings: !formik.isValid
							? [
									{
										msg: Liferay.Language.get(
											'the-configuration-has-missing-or-invalid-values'
										),
									},
							  ]
							: [],
					},
				});
			})
			.catch(() => {
				setTimeout(() => {
					setPreviewInfo({
						loading: false,
						results: {
							errors: [
								{
									msg: Liferay.Language.get(
										'an-unexpected-error-occurred'
									),
									severity: Liferay.Language.get('error'),
								},
							],
						},
					});
				}, 1000);
			});
	};

	const _handleFocusElement = (prefixedId) => {
		const element = document.getElementById(prefixedId);

		if (element) {
			window.scrollTo({
				behavior: 'smooth',
				top:
					element.getBoundingClientRect().top +
					window.pageYOffset -
					55 - // Control menu height
					104 - // Page toolbar height
					20, // Additional padding
			});

			element.classList.remove('focus');

			void element.offsetWidth; // Triggers reflow to restart animation

			element.classList.add('focus');
		}
	};

	const _handleSubmit = (event) => {
		event.preventDefault();

		formik.handleSubmit();

		if (!formik.isValid) {
			openErrorToast({
				message: Liferay.Language.get(
					'unable-to-save-due-to-invalid-or-missing-configuration-values'
				),
			});
		}
	};

	const _renderTabContent = () => {
		switch (tab) {
			case 'settings':
				return (
					<Settings
						advancedConfig={formik.values.advancedConfig}
						aggregationConfig={formik.values.aggregationConfig}
						errors={formik.errors}
						facetConfig={formik.values.facetConfig}
						highlightConfig={formik.values.highlightConfig}
						parameterConfig={formik.values.parameterConfig}
						setFieldTouched={formik.setFieldTouched}
						setFieldValue={formik.setFieldValue}
						sortConfig={formik.values.sortConfig}
						touched={formik.touched}
					/>
				);
			case 'clause-contributors':
				return (
					<ClauseContributors
						initialContributors={[
							{
								label: 'KeywordQueryContributor',
								value: keywordQueryContributors.sort(),
							},
							{
								label: 'ModelPrefilterContributor',
								value: modelPrefilterContributors.sort(),
							},
							{
								label: 'QueryPrefilterContributor',
								value: queryPrefilterContributors.sort(),
							},
						]}
					/>
				);
			default:
				return (
					<>
						<AddElementSidebar
							elements={sidebarQueryElements.current}
							emptyMessage={Liferay.Language.get(
								'no-query-elements-found'
							)}
							onAddElement={_handleAddElement}
							onToggle={setShowSidebar}
							title={Liferay.Language.get('add-query-elements')}
							visible={showSidebar}
						/>

						<div
							className={getCN('query-builder', {
								'open-preview': showPreview,
								'open-sidebar': showSidebar,
							})}
						>
							<QueryBuilder
								entityJSON={entityJSON}
								errors={formik.errors.selectedQueryElements}
								frameworkConfig={formik.values.frameworkConfig}
								indexFields={indexFields}
								isSubmitting={
									formik.isSubmitting || previewInfo.loading
								}
								onBlur={formik.handleBlur}
								onChange={formik.handleChange}
								onDeleteElement={_handleDeleteElement}
								onFrameworkConfigChange={(value) =>
									formik.setFieldValue('frameworkConfig', {
										...formik.values.frameworkConfig,
										...value,
									})
								}
								onToggleSidebar={() => {
									setShowPreview(false);
									setShowSidebar(!showSidebar);
								}}
								searchableAssetTypes={searchableAssetTypes}
								selectedElements={
									formik.values.selectedQueryElements
								}
								setFieldTouched={formik.setFieldTouched}
								setFieldValue={formik.setFieldValue}
								touched={formik.touched.selectedQueryElements}
							/>
						</div>
					</>
				);
		}
	};

	return (
		<form ref={form}>
			<SubmitWarningModal
				errors={errors}
				isSubmitting={formik.isSubmitting}
				message={Liferay.Language.get(
					'the-blueprint-configuration-has-errors-that-may-cause-unexpected-results.-use-the-preview-panel-to-review-these-errors'
				)}
				onClose={() => setShowSubmitWarningModal(false)}
				onSubmit={_handleSubmit}
				visible={showSubmitWarningModal}
			/>

			<PageToolbar
				initialDescription={initialDescription}
				initialTitle={initialTitle}
				isSubmitting={formik.isSubmitting}
				onCancel={redirectURL}
				onChangeTab={setTab}
				onSubmit={_handleSubmit}
				tab={tab}
				tabs={TABS}
			>
				<ClayToolbar.Item>
					<ClayButton
						borderless
						className={getCN({
							active: showPreview,
						})}
						displayType="secondary"
						onClick={() => {
							setShowSidebar(false);
							setShowPreview(!showPreview);
						}}
						small
					>
						{Liferay.Language.get('preview')}

						{!!previewInfo.results.errors?.length && (
							<span className="inline-item inline-item-after">
								<ClayBadge
									displayType="danger"
									label={previewInfo.results.errors.length}
								/>
							</span>
						)}
					</ClayButton>
				</ClayToolbar.Item>
			</PageToolbar>

			<PreviewSidebar
				loading={previewInfo.loading}
				onFetchResults={_handleFetchPreviewSearch}
				onFocusElement={_handleFocusElement}
				onToggle={setShowPreview}
				results={previewInfo.results}
				visible={showPreview}
			/>

			<div
				className={getCN({
					'open-preview': showPreview,
				})}
			>
				{_renderTabContent()}
			</div>
		</form>
	);
}

EditBlueprintForm.propTypes = {
	blueprintId: PropTypes.string,
	entityJSON: PropTypes.object,
	initialConfigurationString: PropTypes.string,
	initialDescription: PropTypes.object,
	initialSelectedElementsString: PropTypes.string,
	initialTitle: PropTypes.object,
	queryElements: PropTypes.arrayOf(PropTypes.object),
	redirectURL: PropTypes.string,
	searchResultsURL: PropTypes.string,
	searchableAssetTypes: PropTypes.arrayOf(PropTypes.string),
	submitFormURL: PropTypes.string,
};

React.memo(EditBlueprintForm);

export default function ({context, props}) {
	return (
		<ThemeContext.Provider value={context}>
			<div className="edit-blueprint-root">
				<ErrorBoundary>
					<EditBlueprintForm {...props} />
				</ErrorBoundary>
			</div>
		</ThemeContext.Provider>
	);
}
