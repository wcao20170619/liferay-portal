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

import 'codemirror/addon/display/autorefresh';

import 'codemirror/addon/edit/closebrackets';

import 'codemirror/addon/edit/closetag';

import 'codemirror/addon/edit/matchbrackets';

import 'codemirror/addon/fold/brace-fold';

import 'codemirror/addon/fold/comment-fold';

import 'codemirror/addon/fold/foldcode';

import 'codemirror/addon/fold/foldgutter.css';

import 'codemirror/addon/fold/foldgutter';

import 'codemirror/addon/fold/indent-fold';

import 'codemirror/lib/codemirror.css';

import 'codemirror/mode/javascript/javascript';
import CodeMirror from 'codemirror';
import React, {useEffect, useRef} from 'react';

const MODES = {
	json: {
		name: 'JSON',
		type: 'application/json',
	},
};

//https://itnext.io/reusing-the-ref-from-forwardref-with-react-hooks-4ce9df693dd

function useCombinedRefs(...refs) {
	const targetRef = React.useRef();

	React.useEffect(() => {
		refs.forEach((ref) => {
			if (!ref) {
				return;
			}

			if (typeof ref === 'function') {
				ref(targetRef.current);
			}
			else {
				ref.current = targetRef.current;
			}
		});
	}, [refs]);

	return targetRef;
}

const CodeMirrorEditor = React.forwardRef(
	(
		{onChange = () => {}, mode = 'json', value = '', readOnly = false},
		ref
	) => {
		const innerRef = useRef(ref);
		const editorWrapper = useRef();
		const editor = useCombinedRefs(ref, innerRef);

		useEffect(() => {
			if (editorWrapper.current) {
				const codeMirror = CodeMirror(editorWrapper.current, {
					autoCloseTags: true,
					autoRefresh: true,
					extraKeys: {
						'Ctrl-Space': 'autocomplete',
					},
					foldGutter: true,
					gutters: [
						'CodeMirror-linenumbers',
						'CodeMirror-foldgutter',
					],
					indentWithTabs: true,
					inputStyle: 'contenteditable',
					lineNumbers: true,
					matchBrackets: true,
					mode: {globalVars: true, name: MODES[mode].type},
					readOnly,
					tabSize: 2,
					value,
					viewportMargin: Infinity,
				});

				codeMirror.on('change', (cm) => {
					onChange(cm.getValue());
				});

				editor.current = codeMirror;
			}
		}, [editorWrapper]); // eslint-disable-line

		return (
			<div
				className="codemirror-editor-wrapper"
				ref={editorWrapper}
			></div>
		);
	}
);

export default CodeMirrorEditor;
