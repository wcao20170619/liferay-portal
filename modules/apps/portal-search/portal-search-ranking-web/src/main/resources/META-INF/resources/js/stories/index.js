import React from 'react';

import {addDecorator, storiesOf} from '@storybook/react';

import '../../css/main.scss';
import 'clay-css/lib/css/atlas.css';

import Alias from 'components/Alias.es';
import PageToolbar from 'components/PageToolbar.es';
import ResultsRankingForm from 'components/ResultsRankingForm.es';
import ThemeContext from 'ThemeContext.es';

const context = {
	spritemap: '/o/admin-theme/images/lexicon/icons.svg'
};

addDecorator(
	storyFn => (
		<ThemeContext.Provider value={context}>
			{storyFn()}
		</ThemeContext.Provider>
	)
);

storiesOf('ResultsRankingForm', module).add(
	'default',
	() => <ResultsRankingForm cancelUrl="" searchTerm="hello" />
);

storiesOf('PageToolbar', module)
	.add('submit enabled', () => <PageToolbar submitDisabled={false} />)
	.add('submit disabled', () => <PageToolbar submitDisabled />);

storiesOf('Alias', module)
	.add('empty list of aliases', () => <Alias keywords={[]} />);