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

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayModal, {useModal} from '@clayui/modal';
import React, {useState} from 'react';

import {openSuccessToast} from '../utils/utils';
import CodeMirrorEditor from './CodeMirrorEditor';

const PreviewModal = ({body, children, size = 'md', title}) => {
	const [visible, setVisible] = useState(false);
	const {observer} = useModal({
		onClose: () => setVisible(false),
	});

	return (
		<>
			{visible && (
				<ClayModal
					className="preview-modal"
					observer={observer}
					size={size}
				>
					<ClayModal.Header>{title}</ClayModal.Header>

					<ClayModal.Body>{body}</ClayModal.Body>
				</ClayModal>
			)}

			<div onClick={() => setVisible(!visible)}>{children}</div>
		</>
	);
};

export const PreviewModalWithCopyDownload = ({
	children,
	fileName,
	folded = false,
	readOnly = true,
	size,
	text,
	title,
	type = 'application/json',
}) => {
	const _handleCopyToClipboard = () => {
		navigator.clipboard.writeText(text);

		openSuccessToast({
			message: Liferay.Language.get('copied-to-clipboard'),
		});
	};

	return (
		<PreviewModal
			body={
				<>
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={_handleCopyToClipboard}
							small
						>
							<span className="inline-item inline-item-before">
								<ClayIcon symbol="copy" />
							</span>

							{Liferay.Language.get('copy-to-clipboard')}
						</ClayButton>

						<ClayLink
							displayType="secondary"
							download={fileName}
							href={URL.createObjectURL(
								new Blob([text], {
									type,
								})
							)}
							onClick={() => {
								openSuccessToast({
									message: Liferay.Language.get('downloaded'),
								});
							}}
							outline
						>
							<span className="inline-item inline-item-before">
								<ClayIcon symbol="download" />
							</span>

							{Liferay.Language.get('download')}
						</ClayLink>
					</ClayButton.Group>

					<CodeMirrorEditor
						folded={folded}
						readOnly={readOnly}
						value={text}
					/>
				</>
			}
			size={size}
			title={title}
		>
			{children}
		</PreviewModal>
	);
};

export default PreviewModal;
