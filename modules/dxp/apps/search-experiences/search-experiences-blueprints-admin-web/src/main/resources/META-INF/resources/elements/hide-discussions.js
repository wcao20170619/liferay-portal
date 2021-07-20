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
		category: 'hide',
		clauses: [
			{
				context: 'query',
				occur: 'filter',
				query: {
					wrapper: {
						query: {
							bool: {
								should: [
									{
										bool: {
											must_not: [
												{
													exists: {
														field: 'discussion',
													},
												},
											],
										},
									},
									{
										bool: {
											must: [
												{
													term: {
														discussion: false,
													},
												},
											],
										},
									},
								],
							},
						},
					},
				},
			},
		],
		conditions: {},
		description: {
			en_US: 'Do not search for comments',
		},
		enabled: true,
		icon: 'hidden',
		title: {
			en_US: 'Hide Comments',
		},
	},
	uiConfigurationJSON: {},
};
