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
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClaySlider from '@clayui/slider';
import React, {useState} from 'react';

import NullableCheckbox from './NullableCheckbox';

function SliderInput({
	disabled,
	id,
	label,
	max,
	min,
	name,
	nullable,
	onBlur,
	onChange,
	setFieldTouched,
	setFieldValue,
	step,
	value,
}) {
	const [active, setActive] = useState(false);

	const _handleSliderChange = (value) => {
		setFieldValue(name, value);
	};

	return (
		<>
			<ClayInput.Group small>
				<ClayInput.GroupItem className="arrowless-input">
					<ClayInput
						aria-label={label}
						disabled={disabled || value === null}
						id={id}
						insetAfter
						max={max}
						min={min}
						name={name}
						onBlur={onBlur}
						onChange={onChange}
						step={step}
						type="number"
						value={value === null ? '' : value}
					/>

					<ClayInput.GroupInsetItem after>
						<ClayButton
							aria-label={Liferay.Language.get('slider')}
							disabled={disabled || value === null}
							displayType="unstyled"
							onClick={() => setActive(!active)}
						>
							<ClayIcon symbol="control-panel" />
						</ClayButton>
					</ClayInput.GroupInsetItem>
				</ClayInput.GroupItem>
			</ClayInput.Group>

			{active && (
				<div className="slider-configuration">
					<ClaySlider
						max={max}
						min={min}
						onBlur={() => setFieldTouched(name)}
						onValueChange={_handleSliderChange}
						step={step}
						value={value === null ? '' : value}
					/>
				</div>
			)}

			{nullable && (
				<NullableCheckbox
					disabled={disabled}
					onChange={(val) => setFieldValue(name, val)}
					value={value}
				/>
			)}
		</>
	);
}

export default SliderInput;
