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

package com.liferay.portal.search.searcher;

import java.util.concurrent.TimeUnit;

/**
 * @author Wade Cao
 */
public final class SearchTimeValue {

	public long getDays() {
		return _timeUnit.toDays(_duration);
	}

	public double getDaysFrac() {
		return (double)getNanos() / _C6;
	}

	public long getHours() {
		return _timeUnit.toHours(_duration);
	}

	public double getHoursFrac() {
		return (double)getNanos() / _C5;
	}

	public long getMicros() {
		return _timeUnit.toMicros(_duration);
	}

	public double getMicrosFrac() {
		return (double)getNanos() / _C1;
	}

	public long getMillis() {
		return _timeUnit.toMillis(_duration);
	}

	public double getMillisFrac() {
		return (double)getNanos() / _C2;
	}

	public long getMinutes() {
		return _timeUnit.toMinutes(_duration);
	}

	public double getMinutesFrac() {
		return (double)getNanos() / _C4;
	}

	public long getNanos() {
		return _timeUnit.toNanos(_duration);
	}

	public long getSeconds() {
		return _timeUnit.toSeconds(_duration);
	}

	public double getSecondsFrac() {
		return (double)getNanos() / _C3;
	}

	public static class Builder {

		public static Builder newBuilder(long duration, TimeUnit timeUnit) {
			return new Builder(duration, timeUnit);
		}

		public SearchTimeValue build() {
			return _searchTimeValue;
		}

		private Builder(long duration, TimeUnit timeUnit) {
			_searchTimeValue._duration = duration;
			_searchTimeValue._timeUnit = timeUnit;
		}

		private final SearchTimeValue _searchTimeValue = new SearchTimeValue();

	}

	private SearchTimeValue() {
	}

	private static final long _C0 = 1L;

	private static final long _C1 = _C0 * 1000L;

	private static final long _C2 = _C1 * 1000L;

	private static final long _C3 = _C2 * 1000L;

	private static final long _C4 = _C3 * 60L;

	private static final long _C5 = _C4 * 60L;

	private static final long _C6 = _C5 * 24L;

	private long _duration;
	private TimeUnit _timeUnit;

}