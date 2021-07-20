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

import ClayForm from '@clayui/form';
import ClayLayout from '@clayui/layout';
import getCN from 'classnames';
import React from 'react';

import CodeMirrorEditor from '../../shared/CodeMirrorEditor';

function SettingsTab({
	advancedConfig,
	aggregationConfig,
	errors,
	facetConfig,
	highlightConfig,
	parameterConfig,
	setFieldTouched,
	setFieldValue,
	sortConfig,
	touched,
}) {
	const _renderEditor = (configName, configValue) => (
		<div
			className={getCN({
				'has-error': touched[configName] && errors[configName],
			})}
			onBlur={() => setFieldTouched(configName)}
		>
			<CodeMirrorEditor
				onChange={(value) => setFieldValue(configName, value)}
				value={configValue}
			/>

			{touched[configName] && errors[configName] && (
				<ClayForm.FeedbackGroup>
					<ClayForm.FeedbackItem>
						<ClayForm.FeedbackIndicator symbol="exclamation-full" />
						{errors[configName]}
					</ClayForm.FeedbackItem>
				</ClayForm.FeedbackGroup>
			)}
		</div>
	);

	return (
		<ClayLayout.ContainerFluid className="builder" size="xl">
			<div className="builder-content-shift sheet">
				<h2 className="sheet-title">
					{Liferay.Language.get('settings')}
				</h2>

				<div className="sheet-text">
					{Liferay.Language.get(
						'enter-additional-blueprints-configuration-settings-below-refer-to-the-documentation-for-help'
					)}
				</div>

				<ClayForm.Group>
					<label htmlFor="aggregation-configuration">
						{Liferay.Language.get('aggregation-configuration')}
					</label>

					{_renderEditor('aggregationConfig', aggregationConfig)}
				</ClayForm.Group>

				<ClayForm.Group>
					<label htmlFor="facet-configuration">
						{Liferay.Language.get('facet-configuration')}
					</label>

					{_renderEditor('facetConfig', facetConfig)}
				</ClayForm.Group>

				<ClayForm.Group>
					<label htmlFor="highlight-configuration">
						{Liferay.Language.get('highlight-configuration')}
					</label>

					{_renderEditor('highlightConfig', highlightConfig)}
				</ClayForm.Group>

				<ClayForm.Group>
					<label htmlFor="sort-configuration">
						{Liferay.Language.get('sort-configuration')}
					</label>

					{_renderEditor('sortConfig', sortConfig)}
				</ClayForm.Group>

				<ClayForm.Group>
					<label htmlFor="parameter-configuration">
						{Liferay.Language.get('parameter-configuration')}
					</label>

					{_renderEditor('parameterConfig', parameterConfig)}
				</ClayForm.Group>

				<ClayForm.Group>
					<label htmlFor="advanced-configuration">
						{Liferay.Language.get('advanced-configuration')}
					</label>

					{_renderEditor('advancedConfig', advancedConfig)}
				</ClayForm.Group>
			</div>
		</ClayLayout.ContainerFluid>
	);
}

export default React.memo(SettingsTab);
