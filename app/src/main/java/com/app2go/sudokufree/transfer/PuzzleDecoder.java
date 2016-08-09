/*
 * Andoku - a sudoku puzzle game for Android.
 * Copyright (C) 2009, 2010  Markus Wiederkehr
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

package com.app2go.sudokufree.transfer;

import com.app2go.sudokufree.model.ExtraRegion;
import com.app2go.sudokufree.model.ExtraRegions;
import com.app2go.sudokufree.model.Puzzle;
import com.app2go.sudokufree.model.Solution;

public class PuzzleDecoder {
	private PuzzleDecoder() {
	}

	// format: clues|areas|x
	public static Puzzle decode(String puzzleStr) {
		String[] parts = puzzleStr.split("\\|");
		if (parts.length == 0)
			throw new IllegalArgumentException();

		String clues = parts[0];
		String areas = parts.length > 1 ? parts[1] : "";
		String extra = parts.length > 2 ? parts[2] : "";

		int size = (int) Math.sqrt(clues.length());
		if (clues.length() != size * size)
			throw new IllegalArgumentException();

		if (size < 5 || size > 9)
			throw new IllegalArgumentException();

		int[][] areaCodes = parseAreaCodes(size, areas);
		ExtraRegion[] extraRegions = parseExtraRegions(size, extra);

		Puzzle puzzle = new Puzzle(areaCodes, extraRegions);

		int idx = 0;
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				char clueChar = clues.charAt(idx++);
				if (clueChar == ' ' || clueChar == '.')
					continue;

				int clue = decode(clueChar);
				if (clue < 0 || clue >= size)
					throw new IllegalArgumentException();

				puzzle.set(row, col, clue);
			}
		}

		return puzzle;
	}

	public static Solution decodeValues(String values) {
		int size = (int) Math.sqrt(values.length());
		if (values.length() != size * size)
			throw new IllegalArgumentException();

		if (size < 5 || size > 9)
			throw new IllegalArgumentException();

		int[][] result = new int[size][size];

		int idx = 0;
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				char valueChar = values.charAt(idx++);
				if (valueChar == ' ' || valueChar == '.') {
					result[row][col] = Puzzle.UNDEFINED;
					continue;
				}

				int value = decode(valueChar);
				if (value < 0 || value >= size)
					throw new IllegalArgumentException();

				result[row][col] = value;
			}
		}

		return new Solution(result);
	}

	private static int[][] parseAreaCodes(int size, String areas) {
		if (areas.length() == 0)
			return StandardAreas.getAreas(size);

		if (areas.length() != size * size)
			throw new IllegalArgumentException();

		int idx = 0;
		int[][] areaCodes = new int[size][size];
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				char areaChar = areas.charAt(idx++);
				int areaCode = decode(areaChar);
				if (areaCode < 0 || areaCode >= size)
					throw new IllegalArgumentException();

				areaCodes[row][col] = areaCode;
			}
		}

		return areaCodes;
	}

	private static ExtraRegion[] parseExtraRegions(int size, String extra) {
		if (extra.length() == 0)
			return ExtraRegions.none();
		else if (extra.equalsIgnoreCase("X"))
			return ExtraRegions.x(size);
		else if (extra.equalsIgnoreCase("H"))
			return ExtraRegions.hyper(size);
		else if (extra.equalsIgnoreCase("P"))
			return ExtraRegions.percent(size);
		else if (extra.equalsIgnoreCase("C"))
			return ExtraRegions.color(size);
		else
			throw new IllegalArgumentException("Unsupported extra regions: " + extra);
	}

	private static int decode(char encodedValue) {
		if (encodedValue >= '1' && encodedValue <= '9')
			return encodedValue - '1';
		else if (encodedValue == '0')
			return 9;
		else if (encodedValue >= 'A' && encodedValue <= 'Z')
			return encodedValue + 10 - 'A';
		else if (encodedValue >= 'a' && encodedValue <= 'z')
			return encodedValue + 10 - 'a';
		else
			throw new IllegalArgumentException();
	}
}
