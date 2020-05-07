/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

const DEPLOYMENT_ACTION = {
	deploy: Liferay.Language.get('deploy'),
	undeploy: Liferay.Language.get('undeploy'),
};

const DEPLOYMENT_STATUS = {
	deployed: Liferay.Language.get('deployed'),
	undeployed: Liferay.Language.get('undeployed'),
};

const DEPLOYMENT_TYPES = {
	productMenu: Liferay.Language.get('product-menu'),
	standalone: Liferay.Language.get('standalone'),
	widget: Liferay.Language.get('widget'),
};

export {DEPLOYMENT_ACTION, DEPLOYMENT_STATUS, DEPLOYMENT_TYPES};
