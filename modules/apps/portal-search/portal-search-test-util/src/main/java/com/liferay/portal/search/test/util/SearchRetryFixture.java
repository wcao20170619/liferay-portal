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

package com.liferay.portal.search.test.util;

import com.liferay.portal.kernel.util.GetterUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author André de Oliveira
 */
public class SearchRetryFixture {

	public static Builder builder() {
		return new Builder();
	}

	public void assertSearch(Runnable runnable) {
		if (GetterUtil.getLong(_attempts) == 1) {
			runnable.run();
		}
		else {
			retrySearch(runnable);
		}
	}

	public static class Builder {

		public Builder attempts(Integer attempts) {
			_searchRetryFixture._attempts = attempts;

			return this;
		}

		public SearchRetryFixture build() {
			return new SearchRetryFixture(_searchRetryFixture);
		}

		public Builder timeout(Long timeout, TimeUnit timeoutTimeUnit) {
			_searchRetryFixture._timeout = timeout;
			_searchRetryFixture._timeoutTimeUnit = timeoutTimeUnit;

			return this;
		}

		private final SearchRetryFixture _searchRetryFixture =
			new SearchRetryFixture();

	}

	protected void retrySearch(Runnable runnable) {
		try {
			IdempotentRetryAssert.retryAssert(
				GetterUtil.getLong(_timeout, 30),
				(TimeUnit)GetterUtil.getObject(
					_timeoutTimeUnit, TimeUnit.SECONDS),
				runnable);
		}
		catch (RuntimeException runtimeException) {
			throw runtimeException;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private SearchRetryFixture() {
	}

	private SearchRetryFixture(SearchRetryFixture searchRetryFixture) {
		_attempts = searchRetryFixture._attempts;
		_timeout = searchRetryFixture._timeout;
		_timeoutTimeUnit = searchRetryFixture._timeoutTimeUnit;
	}

	private Integer _attempts;
	private Long _timeout;
	private TimeUnit _timeoutTimeUnit = TimeUnit.SECONDS;

}