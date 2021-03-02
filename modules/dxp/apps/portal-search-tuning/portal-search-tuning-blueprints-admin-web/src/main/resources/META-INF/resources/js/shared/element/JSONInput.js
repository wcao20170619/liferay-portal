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

import getCN from 'classnames';
import React, {useState} from 'react';

import CodeMirrorEditor from '../CodeMirrorEditor';

function JSONInput({configKey, disabled, initialValue, onChange}) {
	const [value, setValue] = useState(
		JSON.stringify(initialValue, null, '\t')
	);

	const _handleBlur = () => {
		try {
			onChange(configKey, JSON.parse(value));
		}
		catch {}
	};

	return (
		<div className={getCN('custom-json', {disabled})} onBlur={_handleBlur}>
			<label>{Liferay.Language.get('json')}</label>

			<CodeMirrorEditor onChange={setValue} value={value} />
		</div>
	);
}

export default JSONInput;
