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

import ClayButton from '@clayui/button';
import ClayDatePicker from '@clayui/date-picker';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import moment from 'moment';
import React from 'react';

function DateInput({configKey, disabled, onChange, value}) {
	return (
		<div className="date-picker-input">
			<ClayDatePicker
				dateFormat="MM/dd/yyyy"
				disabled={disabled}
				onValueChange={(value) => {
					onChange(configKey, moment(value).unix());
				}}
				placeholder="MM/DD/YYYY"
				readOnly
				sizing="sm"
				value={value ? moment.unix(value).format('MM/DD/YYYY') : ''}
				years={{
					end: 2024,
					start: 1997,
				}}
			/>

			{!!value && (
				<ClayInput.GroupItem shrink>
					<ClayButton
						aria-label={Liferay.Language.get('delete')}
						disabled={disabled}
						displayType="unstyled"
						monospaced
						onClick={() => onChange(configKey, '')}
						small
					>
						<ClayIcon symbol="times-circle" />
					</ClayButton>
				</ClayInput.GroupItem>
			)}
		</div>
	);
}

export default DateInput;
