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
		category: 'match',
		clauses: [
			{
				context: 'query',
				occur: 'must',
				query: {
					wrapper: {
						query: {
							multi_match: {
								boost: '${configuration.boost}',
								fields: '${configuration.fields}',
								fuzziness: '${configuration.fuzziness}',
								minimum_should_match:
									'${configuration.minimum_should_match}',
								operator: '${configuration.operator}',
								query: '${keywords}',
								slop: '${configuration.slop}',
								type: '${configuration.type}',
							},
						},
					},
				},
			},
		],
		conditions: {},
		description: {
			en_US: 'Search for a text match over multiple text fields',
		},
		enabled: true,
		icon: 'picture',
		title: {
			en_US: 'Text Match Over Multiple Fields',
		},
	},
	uiConfigurationJSON: {
		fieldSets: [
			{
				fields: [
					{
						defaultValue: [
							{
								boost: '2',
								field: 'localized_title',
								locale: '${context.language_id}',
							},
							{
								boost: '1',
								field: 'content',
								locale: '${context.language_id}',
							},
						],
						label: 'Fields',
						name: 'fields',
						type: 'fieldMappingList',
						typeOptions: {
							boost: true,
						},
					},
					{
						defaultValue: 'or',
						label: 'Operator',
						name: 'operator',
						type: 'select',
						typeOptions: {
							options: [
								{
									label: 'OR',
									value: 'or',
								},
								{
									label: 'AND',
									value: 'and',
								},
							],
						},
					},
					{
						defaultValue: 'best_fields',
						label: 'Match Type',
						name: 'type',
						type: 'select',
						typeOptions: {
							options: [
								{
									label: 'Best Fields',
									value: 'best_fields',
								},
								{
									label: 'Most Fields',
									value: 'most_fields',
								},
								{
									label: 'Cross Fields',
									value: 'cross_fields',
								},
								{
									label: 'Phrase',
									value: 'phrase',
								},
								{
									label: 'Phrase Prefix',
									value: 'phrase_prefix',
								},
								{
									label: 'Boolean Prefix',
									value: 'bool_prefix',
								},
							],
						},
					},
					{
						defaultValue: 'AUTO',
						helpText:
							'Only use fuzziness with the following match types: most fields, best fields, bool prefix.',
						label: 'Fuzziness',
						name: 'fuzziness',
						type: 'select',
						typeOptions: {
							nullable: true,
							options: [
								{
									label: 'Auto',
									value: 'AUTO',
								},
								{
									label: '0',
									value: '0',
								},
								{
									label: '1',
									value: '1',
								},
								{
									label: '2',
									value: '2',
								},
							],
						},
					},
					{
						defaultValue: '1',
						label: 'Minimum Should Match',
						name: 'minimum_should_match',
						type: 'text',
						typeOptions: {
							nullable: true,
						},
					},
					{
						defaultValue: null,
						helpText:
							'Only use slop with the following match types: phrase, phrase prefix.',
						label: 'Slop',
						name: 'slop',
						type: 'number',
						typeOptions: {
							min: 0,
							nullable: true,
							step: 1,
						},
					},
					{
						defaultValue: 1,
						label: 'Boost',
						name: 'boost',
						type: 'number',
						typeOptions: {
							min: 0,
						},
					},
				],
			},
		],
	},
};
