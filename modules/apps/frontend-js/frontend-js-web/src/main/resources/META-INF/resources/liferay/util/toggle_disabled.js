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

/**
 * Toggles disabled class on received element
 * @param nodes
 * @param state
 */
export default function toggleDisabled(nodes, state) {
	if (typeof nodes === 'string') {
		nodes = document.querySelectorAll(nodes);
	}
	else if (nodes._node) {
		nodes = [nodes._node];
	}
	else if (nodes._nodes) {
		nodes = nodes._nodes;
	}
	else if (nodes.nodeType === Node.ELEMENT_NODE) {
		nodes = [nodes];
	}

	nodes.forEach((node) => {
		node.disabled = state;

		if (state) {
			node.classList.add('disabled');
		}
		else {
			node.classList.remove('disabled');
		}
	});
}
