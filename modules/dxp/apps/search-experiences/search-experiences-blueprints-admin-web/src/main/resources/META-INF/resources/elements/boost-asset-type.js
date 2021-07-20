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
							term: {
								entryClassName: {
									boost: '${configuration.boost}',
									value: '${configuration.entry_class_name}',
								},
							},
						},
					},
				},
			},
		],
		conditions: {},
		description: {
			en_US: 'Boost the given asset type over others',
		},
		enabled: true,
		icon: 'thumbs-up',
		title: {
			en_US: 'Boost Asset Type',
		},
	},
	uiConfigurationJSON: {
		fieldSets: [
			{
				fields: [
					{
						label: 'Asset Type',
						name: 'entry_class_name',
						type: 'select',
						typeOptions: {
							options: [
								{
									label: 'Blogs Entry',
									value: 'com.liferay.blogs.model.BlogsEntry',
								},
								{
									label: 'Bookmark Entry',
									value:
										'com.liferay.bookmarks.model.BookmarksEntry',
								},
								{
									label: 'Bookmarks Folder',
									value:
										'com.liferay.bookmarks.model.BookmarksFolder',
								},
								{
									label: 'Calendar Event',
									value:
										'com.liferay.calendar.model.CalendarBooking',
								},
								{
									label: 'Commerce Product',
									value:
										'com.liferay.commerce.product.model.CPDefinition',
								},
								{
									label: 'Document',
									value:
										'com.liferay.document.library.kernel.model.DLFileEntry',
								},
								{
									label: 'Documents Folder',
									value:
										'com.liferay.document.library.kernel.model.DLFolder',
								},
								{
									label: 'Dynamic Data List Record',
									value:
										'com.liferay.dynamic.data.lists.model.DDLRecord',
								},
								{
									label: 'Form',
									value:
										'com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord',
								},
								{
									label: 'Knowledge Base Article',
									value:
										'com.liferay.knowledge.base.model.KBArticle',
								},
								{
									label: 'Message Boards Message',
									value:
										'com.liferay.message.boards.model.MBMessage',
								},
								{
									label: 'Page',
									value:
										'com.liferay.portal.kernel.model.Layout',
								},
								{
									label: 'User',
									value:
										'com.liferay.portal.kernel.model.User',
								},
								{
									label: 'Web Content Article',
									value:
										'com.liferay.journal.model.JournalArticle',
								},
								{
									label: 'Web Content Folder',
									value:
										'com.liferay.journal.model.JournalFolder',
								},
								{
									label: 'Wiki Page',
									value: 'com.liferay.wiki.model.WikiPage',
								},
							],
						},
					},
					{
						defaultValue: 10,
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
