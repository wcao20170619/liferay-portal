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

import {ClayIconSpriteContext} from '@clayui/icon';
import {
	STORYBOOK_CONSTANTS,
	StorybookAddonActions,
	StorybookAddonKnobs,
	StorybookReact,
} from '@liferay/npm-scripts/src/storybook';
import React from 'react';

import '../../src/main/resources/META-INF/resources/css/main.scss';

import ClayLayout from '@clayui/layout';

import BlueprintForm from '../../src/main/resources/META-INF/resources/js/edit_blueprint';
import Sidebar from '../../src/main/resources/META-INF/resources/js/edit_blueprint/Sidebar';
import QueryBuilder from '../../src/main/resources/META-INF/resources/js/edit_blueprint/tabs/QueryBuilder';
import SelectAssetTypes from '../../src/main/resources/META-INF/resources/js/edit_blueprint/tabs/SelectAssetTypes';
import ElementForm from '../../src/main/resources/META-INF/resources/js/edit_element';
import ErrorBoundary from '../../src/main/resources/META-INF/resources/js/shared/ErrorBoundary';
import JSONElement from '../../src/main/resources/META-INF/resources/js/shared/JSONElement';
import PageToolbar from '../../src/main/resources/META-INF/resources/js/shared/PageToolbar';
import SearchInput from '../../src/main/resources/META-INF/resources/js/shared/SearchInput';
import Element from '../../src/main/resources/META-INF/resources/js/shared/element';
import {
	DEFAULT_EDIT_ELEMENT,
	DEFAULT_FRAMEWORK_CONFIGURATION,
} from '../../src/main/resources/META-INF/resources/js/utils/data';
import AddBlueprintModal from '../../src/main/resources/META-INF/resources/js/view_blueprints/AddBlueprintModal';
import {
	ENTITY_JSON,
	INDEX_FIELDS,
	SEARCHABLE_ASSET_TYPES,
	SELECTED_ELEMENTS,
} from '../js/mocks/data';

const {addDecorator, storiesOf} = StorybookReact;
const {action} = StorybookAddonActions;
const {withKnobs} = StorybookAddonKnobs;

const CONTEXT = {
	availableLanguages: {
		ar_SA: 'Arabic (Saudi Arabia)',
		ca_ES: 'Catalan (Spain)',
		en_US: 'English (United States)',
		nl_NL: 'Dutch (Netherlands)',
		zh_CN: 'Chinese (China)',
	},
	contextPath: '/o/portal-search-tuning-blueprints-admin-web',
	defaultLocale: 'en_US',
	locale: 'en_US',
	namespace:
		'_com_liferay_portal_search_tuning_gsearch_configuration_web_internal_portlet_SearchConfigurationAdminPortlet_',
	spritemap: STORYBOOK_CONSTANTS.SPRITEMAP_PATH,
};

addDecorator(withKnobs);

addDecorator((storyFn) => {
	return (
		<div className="portlet-blueprints-admin">
			<ClayIconSpriteContext.Provider value={CONTEXT.spritemap}>
				{storyFn()}
			</ClayIconSpriteContext.Provider>
		</div>
	);
});

const withBlueprintsClass = (storyFn) => (
	<ErrorBoundary>
		<div className="edit-blueprint-root">{storyFn()}</div>
	</ErrorBoundary>
);

const withBuilderClass = (storyFn) => (
	<div className="builder">{storyFn()}</div>
);

const withContainer = (storyFn) => (
	<ClayLayout.ContainerFluid size="md" view>
		{storyFn()}
	</ClayLayout.ContainerFluid>
);

const BLUEPRINT_FORM_PROPS = {
	blueprintId: '1',
	blueprintType: 0,
	entityJSON: ENTITY_JSON,
	indexFields: INDEX_FIELDS,
	initialDescription: {},
	initialTitle: {
		'en-US': 'Test Title',
	},
	redirectURL: '',
	searchableAssetTypes: SEARCHABLE_ASSET_TYPES,
	submitFormURL: '',
};

storiesOf('Pages|BlueprintForm', module)
	.add('default', () => (
		<BlueprintForm
			context={CONTEXT}
			props={{
				...BLUEPRINT_FORM_PROPS,
				initialSelectedElementsString: JSON.stringify({
					query_configuration: SELECTED_ELEMENTS,
				}),
			}}
		/>
	))
	.add('empty', () => (
		<BlueprintForm
			context={CONTEXT}
			props={{
				...BLUEPRINT_FORM_PROPS,
				initialSelectedElementsString: JSON.stringify({
					query_configuration: [],
				}),
			}}
		/>
	))
	.add('default element', () => (
		<BlueprintForm
			context={CONTEXT}
			props={{
				...BLUEPRINT_FORM_PROPS,
			}}
		/>
	));

storiesOf('Pages|ElementForm', module).add('default', () => (
	<ElementForm
		context={CONTEXT}
		props={{
			initialConfigurationString: JSON.stringify(DEFAULT_EDIT_ELEMENT),
			initialDescription: {'en-US': 'Description'},
			initialTitle: {
				'en-US': 'Test Title',
			},
			predefinedVariables: [
				{
					categoryName: 'User',
					parameterDefinitions: [
						{
							className:
								'com.liferay.portal.search.tuning.blueprints.engine.parameter.StringParameter',
							description: "User's ID",
							variable: '${user.user_id}',
						},
						{
							className:
								'com.liferay.portal.search.tuning.blueprints.engine.parameter.StringParameter',
							description: "User's First Name",
							variable: '${user.user_first_name}',
						},
					],
				},
				{
					categoryName: 'Context',
					parameterDefinitions: [
						{
							description: 'Company ID',
							variable: '${context.company_id}',
						},
						{
							description: 'Scope Group ID',
							variable: '${context.scope_group_id}',
						},
					],
				},
			],
			redirectURL: '',
			submitFormURL: '',
		}}
	/>
));

storiesOf('Components|AddBlueprintModal', module)
	.addDecorator(withBlueprintsClass)
	.add('AddBlueprintModal', () => (
		<AddBlueprintModal
			closeModal={action('closeModal')}
			contextPath="/o/portal-search-tuning-blueprints-admin-web/"
			dialogTitle="New Search Blueprint"
			initialVisible
		/>
	));

storiesOf('Components|Element', module)
	.addDecorator(withBlueprintsClass)
	.addDecorator(withBuilderClass)
	.addDecorator(withContainer)
	.add('Element', () => (
		<Element
			deleteElement={action('deleteElement')}
			elementTemplateJSON={SELECTED_ELEMENTS[0].elementTemplateJSON}
			uiConfigurationJSON={SELECTED_ELEMENTS[0].uiConfigurationJSON}
			uiConfigurationValues={SELECTED_ELEMENTS[0].uiConfigurationValues}
			updateElement={action('updateElement')}
		/>
	));

storiesOf('Components|Element', module)
	.addDecorator(withBlueprintsClass)
	.addDecorator(withBuilderClass)
	.addDecorator(withContainer)
	.add('JSONElement', () => (
		<JSONElement
			deleteElement={action('deleteElement')}
			description={SELECTED_ELEMENTS[0].elementTemplateJSON.description}
			disabled={false}
			elementTemplateJSON={SELECTED_ELEMENTS[0].elementTemplateJSON}
			icon={SELECTED_ELEMENTS[0].elementTemplateJSON.icon}
			title={SELECTED_ELEMENTS[0].elementTemplateJSON.title}
			updateJSON={() => {}}
		/>
	));

storiesOf('Components|PageToolbar', module)
	.addDecorator(withBlueprintsClass)
	.add('PageToolbar', () => (
		<PageToolbar
			initialTitle={{
				'en-US': 'Test Title',
			}}
			onCancel=""
			onPublish={action('onPublish')}
			tab={'query-builder'}
			tabs={{
				'query-builder': 'query-builder',
			}}
		/>
	));

storiesOf('Components|QueryBuilder', module)
	.addDecorator(withBlueprintsClass)
	.addDecorator(withContainer)
	.add('QueryBuilder', () => (
		<QueryBuilder
			deleteElement={action('buildElement')}
			frameworkConfig={DEFAULT_FRAMEWORK_CONFIGURATION}
			searchableAssetTypes={SEARCHABLE_ASSET_TYPES}
			selectedElements={SELECTED_ELEMENTS}
			updateElement={action('updateElement')}
		/>
	));

storiesOf('Components|SearchInput', module)
	.addDecorator(withBlueprintsClass)
	.add('SearchInput', () => (
		<SearchInput
			onChange={action('onChange')}
			onSubmit={action('onSubmit')}
		/>
	));

storiesOf('Components|SelectAssetTypes', module)
	.addDecorator(withBlueprintsClass)
	.addDecorator(withContainer)
	.add('SelectAssetTypes', () => (
		<SelectAssetTypes
			searchableAssetTypes={SEARCHABLE_ASSET_TYPES}
			selectedAssetTypes={[]}
			updateSelectedAssetTypes={action('updateSelectedAssetTypes')}
		/>
	));

storiesOf('Components|Sidebar', module)
	.addDecorator(withBlueprintsClass)
	.add('Sidebar', () => (
		<Sidebar
			addElement={action('addElement')}
			queryElements={SELECTED_ELEMENTS}
		/>
	));
