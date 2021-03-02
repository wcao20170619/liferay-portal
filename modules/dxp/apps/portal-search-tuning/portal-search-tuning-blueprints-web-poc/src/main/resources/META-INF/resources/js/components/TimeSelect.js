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

import ClayDatePicker from '@clayui/date-picker';
import ClayForm, {ClaySelect} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import {PropTypes} from 'prop-types';
import React from 'react';

import {validDateRange} from '../utils/util';

const YEAR_RANGE = {
	end: 2024,
	start: 1997,
};

export default function TimeSelect({children, timeRange, updateTimeRange}) {
	const DEFAULT_TIME = 'any';

	const TIME_OPTIONS = [
		{
			label: Liferay.Language.get('anytime'),
			value: 'any',
		},
		{
			label: Liferay.Language.get('last-hour'),
			value: 'last-hour',
		},
		{
			label: Liferay.Language.get('last-day'),
			value: 'last-day',
		},
		{
			label: Liferay.Language.get('last-week'),
			value: 'last-week',
		},
		{
			label: Liferay.Language.get('last-month'),
			value: 'last-month',
		},
		{
			label: Liferay.Language.get('last-year'),
			value: 'last-year',
		},
		{
			label: Liferay.Language.get('custom-range'),
			value: 'custom-range',
		},
	];

	function updateTimeOption(event) {
		const value = event.target.value;

		if (value === DEFAULT_TIME) {
			updateTimeRange({});
		}
		else {
			updateTimeRange({time: value});
		}
	}

	function updateTimeFrom(value) {
		updateTimeRange({
			...timeRange,
			timeFrom: value,
		});
	}

	function updateTimeTo(value) {
		updateTimeRange({
			...timeRange,
			timeTo: value,
		});
	}

	const _hasError = () =>
		timeRange.timeTo && timeRange.timeFrom && !validDateRange(timeRange);

	return (
		<>
			<ClayLayout.Row justify="center">
				<ClayLayout.Col size={4}>
					<ClayForm.Group className="form-group-autofit" small>
						<div className="form-group-item">
							<ClaySelect
								aria-label={Liferay.Language.get('time-range')}
								onChange={updateTimeOption}
								value={
									timeRange.time
										? timeRange.time
										: DEFAULT_TIME
								}
							>
								{TIME_OPTIONS.map((item) => (
									<ClaySelect.Option
										key={item.value}
										label={item.label}
										value={item.value}
									/>
								))}
							</ClaySelect>
						</div>

						<div className="form-group-item form-group-item-shrink">
							{children}
						</div>
					</ClayForm.Group>
				</ClayLayout.Col>
			</ClayLayout.Row>

			{timeRange.time === 'custom-range' && (
				<ClayLayout.Row justify="center">
					<ClayLayout.Col size={8}>
						<ClayForm.Group
							className={`form-group-autofit ${
								_hasError() ? `has-error` : ``
							}`}
							small
						>
							<div className="form-group-item form-group-item-label form-group-item-shrink">
								<label>{Liferay.Language.get('from')}</label>
							</div>

							<div className="form-group-item">
								<ClayDatePicker
									ariaLabels={Liferay.Language.get(
										'time-from'
									)}
									onValueChange={updateTimeFrom}
									placeholder="YYYY-MM-DD"
									value={timeRange.timeFrom}
									years={YEAR_RANGE}
								/>
							</div>

							<div className="form-group-item form-group-item-label form-group-item-shrink">
								<label>{Liferay.Language.get('to')}</label>
							</div>

							<div className="form-group-item">
								<ClayDatePicker
									ariaLabels={Liferay.Language.get('time-to')}
									onValueChange={updateTimeTo}
									placeholder="YYYY-MM-DD"
									value={timeRange.timeTo}
									years={YEAR_RANGE}
								/>
								{_hasError() && (
									<div className="form-group-item form-group-item-shrink">
										<div className="form-feedback-group">
											<div className="form-feedback-item">
												{Liferay.Language.get(
													'search-custom-range-invalid-date-range'
												)}
											</div>
										</div>
									</div>
								)}
							</div>
						</ClayForm.Group>
					</ClayLayout.Col>
				</ClayLayout.Row>
			)}
		</>
	);
}

TimeSelect.propTypes = {
	timeRange: PropTypes.object,
	updateTimeRange: PropTypes.func,
};
