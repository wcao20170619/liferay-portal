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

import ClayMultiSelect from '@clayui/multi-select';
import React, {useState} from 'react';

import NullableCheckbox from './NullableCheckbox';

function MultiSelectInput({
	disabled,
	label,
	name,
	nullable,
	setFieldTouched,
	setFieldValue,
	value,
}) {
	const [inputValue, setInputValue] = useState('');

	return (
		<>
			<ClayMultiSelect
				aria-label={label}
				disabled={disabled || value === null}
				inputValue={inputValue}
				items={value || []}
				onBlur={() => {
					if (inputValue) {
						setFieldValue(name, [
							...value,
							{label: inputValue, value: inputValue},
						]);

						setInputValue('');
					}

					setFieldTouched(name);
				}}
				onChange={setInputValue}
				onItemsChange={(value) => setFieldValue(name, value)}
			/>

			{nullable && (
				<NullableCheckbox
					defaultValue={[]}
					disabled={disabled}
					onChange={(val) => setFieldValue(name, val)}
					value={value}
				/>
			)}
		</>
	);
}

export default MultiSelectInput;
