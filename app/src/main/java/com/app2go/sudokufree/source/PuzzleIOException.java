/*
 * Andoku - a sudoku puzzle game for Android.
 * Copyright (C) 2009  Markus Wiederkehr
 *
 * This file is part of Andoku.
 *
 * Andoku is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Andoku is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Andoku.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.app2go.sudokufree.source;

import java.io.IOException;

public class PuzzleIOException extends IOException {
	private static final long serialVersionUID = 1L;

	public PuzzleIOException(String detailMessage) {
		super(detailMessage);
	}

	public PuzzleIOException(Throwable throwable) {
		super(throwable == null ? null : throwable.toString());
		initCause(throwable);
	}

	public PuzzleIOException(String detailMessage, Throwable throwable) {
		super(detailMessage);
		initCause(throwable);
	}
}
