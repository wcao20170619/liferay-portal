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
import {openSelectionModal} from 'frontend-js-web';
import React from 'react';

import NullableCheckbox from './NullableCheckbox';

function ItemSelectorInput({
	disabled,
	entityJSON,
	itemType,
	label,
	name,
	nullable,
	setFieldTouched,
	setFieldValue,
	value,
}) {
	const _handleMultipleEntitySelect = (itemType) => {
		if (entityJSON[itemType].multiple) {
			openSelectionModal({
				buttonAddLabel: Liferay.Language.get('select'),
				multiple: true,
				onSelect: (selectedItems) => {
					if (selectedItems) {
						setFieldValue(
							name,
							selectedItems.map((item) => ({
								label: item.name,
								value: item.id,
							}))
						);
					}
				},
				selectEventName: 'selectEntity',
				title: entityJSON[itemType].title,
				url: entityJSON[itemType].url,
			});
		}
		else {
			openSelectionModal({
				buttonAddLabel: Liferay.Language.get('select'),
				onSelect: (event) => {
					setFieldValue(name, [
						{
							label: event.entityname,
							value: event.entityid,
						},
					]);
				},
				selectEventName: 'selectEntity',
				title: entityJSON[itemType].title,
				url: entityJSON[itemType].url,
			});
		}
	};

	return (
		<>
			<ClayInput.Group onBlur={() => setFieldTouched(name)} small>
				<ClayInput.GroupItem>
					<ClayInput
						aria-label={label}
						disabled={disabled || value === null}
						name={name}
						readOnly
						type="text"
						value={
							value && value.length > 0
								? value.map((item) => item.label).join(', ')
								: ''
						}
					/>
				</ClayInput.GroupItem>

				{value && value.length > 0 && (
					<ClayInput.GroupItem shrink>
						<ClayButton
							aria-label={Liferay.Language.get('delete')}
							className="component-action"
							disabled={disabled || value === null}
							displayType="unstyled"
							onClick={() => setFieldValue(name, [])}
						>
							<ClayIcon symbol="times-circle" />
						</ClayButton>
					</ClayInput.GroupItem>
				)}

				<ClayInput.GroupItem shrink>
					<ClayButton
						aria-label={Liferay.Language.get('select')}
						disabled={disabled || !entityJSON || value === null}
						displayType="secondary"
						onClick={() => {
							if (entityJSON) {
								_handleMultipleEntitySelect(itemType);
							}
						}}
						small
					>
						{Liferay.Language.get('select')}
					</ClayButton>
				</ClayInput.GroupItem>
			</ClayInput.Group>

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

export default ItemSelectorInput;
