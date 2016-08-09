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

package com.app2go.sudokufree.db;

public class GameStatistics {
	public final int numGamesSolved;
	public final long sumTime;
	public final long minTime;

	public GameStatistics(int numGamesSolved, long sumTime, long minTime) {
		this.numGamesSolved = numGamesSolved;
		this.sumTime = sumTime;
		this.minTime = minTime;
	}

	public long getAverageTime() {
		return numGamesSolved == 0 ? 0 : sumTime / numGamesSolved;
	}
}
