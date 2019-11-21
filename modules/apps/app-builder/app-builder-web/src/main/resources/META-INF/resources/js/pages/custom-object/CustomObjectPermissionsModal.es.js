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

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import React, {useState} from 'react';

import ManagementToolbar from '../../components/management-toolbar/ManagementToolbar.es';
import Table from '../../components/table/Table.es';
import {useRequest} from '../../hooks/index.es';

const ADD_DATA_RECORD = 'ADD_DATA_RECORD';
const DELETE_DATA_RECORD = 'DELETE_DATA_RECORD';
const UPDATE_DATA_RECORD = 'UPDATE_DATA_RECORD';
const VIEW_DATA_RECORD = 'VIEW_DATA_RECORD';
const ACTIONS = [
	ADD_DATA_RECORD,
	DELETE_DATA_RECORD,
	UPDATE_DATA_RECORD,
	VIEW_DATA_RECORD
];

const COLUMNS = [
	{
		key: 'name',
		sortable: false,
		value: Liferay.Language.get('role')
	},
	{
		key: ADD_DATA_RECORD,
		sortable: false,
		value: Liferay.Language.get('add-entry')
	},
	{
		key: DELETE_DATA_RECORD,
		sortable: false,
		value: Liferay.Language.get('delete')
	},
	{
		key: UPDATE_DATA_RECORD,
		sortable: false,
		value: Liferay.Language.get('update')
	},
	{
		key: VIEW_DATA_RECORD,
		sortable: false,
		value: Liferay.Language.get('view')
	}
];

export default ({onClose, visible}) => {
	const {observer} = useModal({
		onClose
	});

	const {
		response: {items: roles = []},
		isLoading
	} = useRequest('/o/headless-admin-user/v1.0/roles');

	const [permissions, setPermissions] = useState([]);
	const [keywords, setKeywords] = useState('');

	if (!visible) {
		return <></>;
	}

	if (isLoading) {
		return <></>;
	}

	const isChecked = (roleName, actionId) =>
		permissions.some(
			({actionsIds, roleName: name}) =>
				name === roleName && actionsIds.includes(actionId)
		);

	const onSearch = ({target: {value}}) => {
		setKeywords(value);
	};

	const togglePermission = (roleName, actionId) => {
		const exists = permissions.some(
			({roleName: name}) => roleName === name
		);

		let newPermissions = exists
			? permissions
			: permissions.concat({actionsIds: [], roleName});

		newPermissions = newPermissions.map(permission => {
			if (permission.roleName !== roleName) {
				return permission;
			}

			const {actionsIds} = permission;

			return {
				...permission,
				actionsIds: actionsIds.includes(actionId)
					? actionsIds.filter(id => id !== actionId)
					: actionsIds.concat(actionId)
			};
		});

		setPermissions(newPermissions);
	};

	const filteredRoles = roles
		.filter(
			({name, roleType}) =>
				name !== 'Administrator' &&
				name !== 'Site Administrator' &&
				name !== 'Site Owner' &&
				roleType !== 'organization'
		)
		.filter(({name}) => new RegExp(keywords, 'ig').test(name))
		.map(({name, roleType}) => {
			let item = {
				name: (
					<>
						<ClayIcon
							symbol={roleType === 'site' ? 'sites' : 'user'}
						/>{' '}
						{name}
					</>
				)
			};

			ACTIONS.forEach(action => {
				item = {
					...item,
					[action]: (
						<input
							checked={isChecked(name, action)}
							name={action}
							onClick={() => togglePermission(name, action)}
							type="checkbox"
						/>
					)
				};
			});

			return item;
		});

	return (
		<ClayModal observer={observer} size="full-screen">
			<ClayModal.Header>
				{Liferay.Language.get('permissions')}
			</ClayModal.Header>
			<ClayModal.Body>
				<ManagementToolbar>
					<ClayInput.Group>
						<ClayInput.GroupItem>
							<ClayInput
								aria-label={Liferay.Language.get('search-for')}
								className="input-group-inset input-group-inset-after"
								onChange={onSearch}
								placeholder={Liferay.Language.get('search-for')}
								type="text"
								value={keywords}
							/>
							<ClayInput.GroupInsetItem after>
								<ClayButtonWithIcon
									displayType="unstyled"
									symbol="search"
								/>
							</ClayInput.GroupInsetItem>
						</ClayInput.GroupItem>
					</ClayInput.Group>
				</ManagementToolbar>

				<Table align="center" columns={COLUMNS} items={filteredRoles} />
			</ClayModal.Body>
			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary">
							{Liferay.Language.get('cancel')}
						</ClayButton>
						<ClayButton>{Liferay.Language.get('save')}</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};
