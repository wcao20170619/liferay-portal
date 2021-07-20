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

import ClayAutocomplete from '@clayui/autocomplete';
import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayForm, {ClayInput, ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {FocusScope} from '@clayui/shared';
import {ClayTooltipProvider} from '@clayui/tooltip';
import fuzzy from 'fuzzy';
import React, {useContext, useEffect, useRef, useState} from 'react';

import {alphabeticalSort, stringLengthSort} from '../../utils/sort';
import {isDefined} from '../../utils/utils';
import ThemeContext from '../ThemeContext';

const USER_LANGUAGE_VARIABLE = '${context.language_id}';

/**
 * Used for displaying autocomplete results. Filters a list of index field
 * objects and sorts them by string length or alphabetically if lengths are the
 * same.
 * @param {Array} indexFields List of index field objects.
 * @param {String} field Field name to filter the field objects.
 * @returns
 */
function filterAndSortIndexFields(indexFields, field) {
	return indexFields
		.filter((indexField) =>
			indexField.name.toLowerCase().includes(field.toLowerCase())
		)
		.sort((a, b) => {
			const sort = stringLengthSort(a.name, b.name);

			if (sort === 0 || field === '') {
				return alphabeticalSort(a.name, b.name);
			}

			return sort;
		});
}

function AutocompleteItem({indexField, match = '', onClick}) {
	const fuzzyMatch = fuzzy.match(match, indexField.name, {
		post: '</strong>',
		pre: '<strong>',
	});

	return (
		<ClayDropDown.Item onClick={onClick}>
			{fuzzyMatch ? (
				<span
					dangerouslySetInnerHTML={{
						__html: fuzzyMatch.rendered,
					}}
				/>
			) : (
				indexField.name
			)}

			{indexField.language_id_position > -1 && (
				<ClayIcon symbol="globe" />
			)}

			<span className="type">{indexField.type}</span>
		</ClayDropDown.Item>
	);
}

/**
 * Displays an autocomplete input for selecting an indexed field.
 *
 * Example index field object:
 * {
 * 	language_id_position: -1,
 * 	name: 'ddmTemplateKey',
 * 	type: 'keyword'
 * }
 */
function FieldRow({
	boost = 1,
	disabled,
	field,
	id,
	index = 0,
	indexFields = [],
	languageIdPosition,
	locale,
	onBlur,
	onChange,
	onDelete,
	showBoost,
}) {
	const {availableLanguages} = useContext(ThemeContext);

	const inputRef = useRef();

	const [filteredIndexFields, setFilteredIndexFields] = useState(
		filterAndSortIndexFields(indexFields, field)
	);
	const [showDropDown, setShowDropDown] = useState(false);

	useEffect(() => {
		setFilteredIndexFields(filterAndSortIndexFields(indexFields, field));
	}, [indexFields, field]);

	const _getIndexField = (fieldName) => {
		return indexFields.find((item) => item.name === fieldName);
	};

	const _handleFieldChange = (event) => {
		const indexField = _getIndexField(event.target.value) || {};

		let languageIdPosition = isDefined(indexField.language_id_position)
			? indexField.language_id_position
			: -1;

		if (indexField.locale && languageIdPosition === -1) {
			languageIdPosition = event.target.value.length;
		}

		onChange({
			field: event.target.value,
			languageIdPosition,
			locale: languageIdPosition > -1 ? '' : undefined,
		});
	};

	const _handleAutocompleteItemClick = (indexField) => () => {

		// Special case if the same field name has both a localized and
		// non-localized option.
		//
		// For example 'title':
		// {name: 'title', type: 'text', language_id_position: 5}
		// {name: 'title', type: 'text', language_id_position: -1}
		//
		// Reset the locale to "no localization" if the non-localized field
		// is autocompleted to.

		if (indexField.language_id_position === -1) {
			onChange({
				field: indexField.name,
				languageIdPosition: indexField.language_id_position,
				locale: '',
			});
		}
		else {
			onChange({
				field: indexField.name,
				languageIdPosition: indexField.language_id_position,
			});
		}

		inputRef.current.focus();

		setShowDropDown(false);
	};

	const _handleFieldKeyDown = (event) => {
		if (event.key === 'Tab' || (event.shiftKey && event.key === 'Tab')) {
			return;
		}

		setShowDropDown(true);
	};

	const _handleBoostChange = (event) => onChange({boost: event.target.value});

	const _handleLocaleChange = (event) =>
		onChange({locale: event.target.value});

	const _isLocalizable = () =>
		languageIdPosition > -1 || locale !== undefined;

	return (
		<ClayForm.Group>
			<ClayInput.Group small>
				<ClayInput.GroupItem>
					<FocusScope>
						<ClayAutocomplete>
							<ClayAutocomplete.Input
								aria-label={Liferay.Language.get('field')}
								autoComplete="off"
								disabled={disabled}
								id={id}
								onBlur={onBlur}
								onChange={_handleFieldChange}
								onFocus={() => setShowDropDown(true)}
								onKeyDown={_handleFieldKeyDown}
								ref={inputRef}
								sizing="sm"
								value={field}
							/>

							<ClayAutocomplete.DropDown
								active={
									showDropDown && filteredIndexFields.length
								}
								onSetActive={setShowDropDown}
							>
								<ClayDropDown.ItemList className="blueprint-field-row-dropdown">
									{filteredIndexFields.map(
										(indexField, index) => (
											<AutocompleteItem
												indexField={indexField}
												key={index}
												match={field}
												onClick={_handleAutocompleteItemClick(
													indexField
												)}
											/>
										)
									)}
								</ClayDropDown.ItemList>
							</ClayAutocomplete.DropDown>
						</ClayAutocomplete>
					</FocusScope>
				</ClayInput.GroupItem>

				{_isLocalizable() && (
					<ClayInput.GroupItem>
						<ClaySelect
							aria-label={Liferay.Language.get('locale')}
							className="form-control-sm"
							disabled={disabled}
							id={`${id}_locale`}
							onBlur={onBlur}
							onChange={_handleLocaleChange}
							value={locale}
						>
							<ClaySelect.Option
								key={`no-localization-${index}`}
								label={Liferay.Language.get('no-localization')}
								value=""
							/>

							<ClaySelect.Option
								key={`users-language-${index}`}
								label={Liferay.Language.get('users-language')}
								value={USER_LANGUAGE_VARIABLE}
							/>

							{Object.keys(availableLanguages).map((locale) => (
								<ClaySelect.Option
									key={`${index}-${locale}`}
									label={availableLanguages[locale]}
									value={locale}
								/>
							))}
						</ClaySelect>
					</ClayInput.GroupItem>
				)}

				{showBoost && (
					<ClayInput.GroupItem shrink>
						<ClayTooltipProvider>
							<ClayInput
								aria-label={Liferay.Language.get('boost')}
								className="field-boost-input"
								data-tooltip-align="top"
								disabled={disabled}
								id={`${id}_boost`}
								min="0"
								onBlur={onBlur}
								onChange={_handleBoostChange}
								title={Liferay.Language.get('boost')}
								type="number"
								value={boost}
							/>
						</ClayTooltipProvider>
					</ClayInput.GroupItem>
				)}

				{onDelete && (
					<ClayInput.GroupItem shrink>
						<ClayButton
							aria-label={Liferay.Language.get('delete')}
							disabled={disabled}
							displayType="unstyled"
							monospaced
							onClick={onDelete}
							small
						>
							<ClayIcon symbol="times-circle" />
						</ClayButton>
					</ClayInput.GroupItem>
				)}
			</ClayInput.Group>
		</ClayForm.Group>
	);
}

export default FieldRow;
