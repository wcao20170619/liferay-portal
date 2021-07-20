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

/**
 * Keep these in sync with the elements in
 * search-experiences-blueprints-resources/
 * src/main/resources/META-INF/search/elements
 */
export default {
	elementTemplateJSON: {
		category: 'boost',
		clauses: [
			{
				context: 'query',
				occur: 'should',
				query: {
					wrapper: {
						query: {
							function_score: {
								boost: '${configuration.boost}',
								field_value_factor: {
									factor: '${configuration.factor}',
									field: 'versionCount_sortable',
									missing: 1,
									modifier: '${configuration.modifier}',
								},
							},
						},
					},
				},
			},
		],
		conditions: {},
		description: {
			en_US: 'Boost contents edited more often',
		},
		enabled: true,
		icon: 'thumbs-up',
		title: {
			en_US: 'Boost Contents With More Versions',
		},
	},
	uiConfigurationJSON: {
		fieldSets: [
			{
				fields: [
					{
						defaultValue: 10,
						label: 'Boost',
						name: 'boost',
						type: 'number',
						typeOptions: {
							min: 0,
						},
					},
					{
						defaultValue: 1.2,
						label: 'Factor',
						name: 'factor',
						type: 'number',
						typeOptions: {
							min: 0,
						},
					},
					{
						defaultValue: 'sqrt',
						label: 'Modifier',
						name: 'modifier',
						type: 'select',
						typeOptions: {
							options: [
								{
									label: 'None',
									value: 'none',
								},
								{
									label: 'log',
									value: 'log',
								},
								{
									label: 'log1p',
									value: 'log1p',
								},
								{
									label: 'log2p',
									value: 'log2p',
								},
								{
									label: 'ln',
									value: 'ln',
								},
								{
									label: 'ln1p',
									value: 'ln1p',
								},
								{
									label: 'ln2p',
									value: 'ln2p',
								},
								{
									label: 'Square',
									value: 'square',
								},
								{
									label: 'Square Root',
									value: 'sqrt',
								},
								{
									label: 'Reciprocal',
									value: 'reciprocal',
								},
							],
						},
					},
				],
			},
		],
	},
};
