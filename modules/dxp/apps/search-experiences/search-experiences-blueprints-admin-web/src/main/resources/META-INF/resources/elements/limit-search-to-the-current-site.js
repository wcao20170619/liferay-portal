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
		category: 'filter',
		clauses: [
			{
				context: 'query',
				occur: 'filter',
				query: {
					wrapper: {
						query: {
							term: {
								scopeGroupId: '${context.scope_group_id}',
							},
						},
					},
				},
			},
		],
		conditions: {},
		description: {
			en_US: 'Limit search to the current site',
		},
		enabled: true,
		icon: 'filter',
		title: {
			en_US: 'Limit Search to the Current Site',
		},
	},
	uiConfigurationJSON: {},
};
