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

package com.app2go.sudokufree.solver;

import com.app2go.sudokufree.model.Puzzle;

/**
 * Puzzle reporter that counts the number of solutions of a sudoku puzzle.
 */
public final class SolutionCounterReporter implements PuzzleReporter {
	private long counter;

	@Override
	public boolean report(Puzzle solution) {
		counter++;
		return true;
	}

	public long getCounter() {
		return counter;
	}
}
