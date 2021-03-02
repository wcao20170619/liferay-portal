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
import React from 'react';

import CodeMirrorEditor from '../components/CodeMirrorEditor';

function Aggregations({aggregationConfig, onAggregationConfigChange}) {
	return (
		<ClayLayout.ContainerFluid className="builder" size="md">
			<div className="sheet">
				<h2 className="sheet-title">
					{Liferay.Language.get('aggregations')}
				</h2>

				<ClayForm.Group>
					<label htmlFor="aggregation-configuration">
						{Liferay.Language.get('aggregation-configuration')}
					</label>

					<CodeMirrorEditor
						onChange={(value) => onAggregationConfigChange(value)}
						value={aggregationConfig}
					/>
				</ClayForm.Group>
			</div>
		</ClayLayout.ContainerFluid>
	);
}

export default React.memo(Aggregations);
