/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.search.experiences.blueprints.exception;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.search.experiences.blueprints.message.Message;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Petteri Karttunen
 */
public class BlueprintValidationException extends PortalException {

	public BlueprintValidationException() {
	}

	public BlueprintValidationException(String msg) {
		super(msg);
	}

	public BlueprintValidationException(String msg, List<Message> messages) {
		super(msg);

		_messages = messages;
	}

	public BlueprintValidationException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public BlueprintValidationException(Throwable throwable) {
		super(throwable);
	}

	public List<Message> getMessages() {
		return _messages;
	}

	private static final long serialVersionUID = 1L;

	private List<Message> _messages;

}