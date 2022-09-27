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

package com.liferay.portal.search.web.internal.facet.display.context;

import java.io.Serializable;

import java.util.Comparator;

/**
 * @author Wade Cao
 */
public abstract class FacetDisplayContext implements Serializable {

	public static final Comparator<FacetDisplayContext>
		COMPARATOR_FREQUENCY_ASC = new Comparator<FacetDisplayContext>() {

			public int compare(
				FacetDisplayContext displayContext1,
				FacetDisplayContext displayContext2) {

				int result =
					displayContext1.getFrequency() -
						displayContext2.getFrequency();

				if (result == 0) {
					return _compareDisplayNames(
						displayContext1.getDisplayName(),
						displayContext2.getDisplayName());
				}

				return result;
			}

		};

	public static final Comparator<FacetDisplayContext>
		COMPARATOR_FREQUENCY_DESC = new Comparator<FacetDisplayContext>() {

			@Override
			public int compare(
				FacetDisplayContext displayContext1,
				FacetDisplayContext displayContext2) {

				int result =
					displayContext2.getFrequency() -
						displayContext1.getFrequency();

				if (result == 0) {
					return _compareDisplayNames(
						displayContext1.getDisplayName(),
						displayContext2.getDisplayName());
				}

				return result;
			}

		};

	public static final Comparator<FacetDisplayContext> COMPARATOR_TERM_ASC =
		new Comparator<FacetDisplayContext>() {

			@Override
			public int compare(
				FacetDisplayContext displayContext1,
				FacetDisplayContext displayContext2) {

				int result = _compareDisplayNames(
					displayContext1.getDisplayName(),
					displayContext2.getDisplayName());

				if (result == 0) {
					return displayContext2.getFrequency() -
						displayContext1.getFrequency();
				}

				return result;
			}

		};

	public static final Comparator<FacetDisplayContext> COMPARATOR_TERM_DESC =
		new Comparator<FacetDisplayContext>() {

			@Override
			public int compare(
				FacetDisplayContext displayContext1,
				FacetDisplayContext displayContext2) {

				int result = _compareDisplayNames(
					displayContext2.getDisplayName(),
					displayContext1.getDisplayName());

				if (result == 0) {
					return displayContext2.getFrequency() -
						displayContext1.getFrequency();
				}

				return result;
			}

		};

	public abstract String getDisplayName();

	public abstract int getFrequency();

	private static int _compareDisplayNames(
		String displayName1, String displayName2) {

		return displayName1.compareTo(displayName2);
	}

}