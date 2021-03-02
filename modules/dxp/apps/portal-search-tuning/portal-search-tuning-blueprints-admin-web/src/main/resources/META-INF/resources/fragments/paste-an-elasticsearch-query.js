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

export default {
	fragmentTemplateJSON: {
		category: 'custom',
		clauses: [
			{
				context: 'query',
				occur: '${config.occur}',
				query: {
					query: '${config.query}',
				},
				type: 'wrapper',
			},
		],
		conditions: [],
		description: {
			en_US: 'Paste any Elasticsearch query body in the fragment as is',
		},
		enabled: true,
		icon: 'custom-field',
		title: {
			en_US: 'Paste any Elasticsearch query',
		},
	},
	uiConfigurationJSON: [
		{
			defaultValue: 'must',
			key: 'occur',
			name: 'Occur',
			type: 'single-select',
			typeOptions: [
				{
					label: 'MUST',
					value: 'must',
				},
				{
					label: 'SHOULD',
					value: 'should',
				},
				{
					label: 'MUST NOT',
					value: 'must_not',
				},
				{
					label: 'FILTER',
					value: 'filter',
				},
			],
		},
		{
			defaultValue: {
				query_string: {
					default_operator: 'or',
					fields: [
						'content',
						'content_en_US',
						'title',
						'title_en_US^2',
					],
					query: '${keywords}',
				},
			},
			key: 'query',
			type: 'json',
		},
	],
};
