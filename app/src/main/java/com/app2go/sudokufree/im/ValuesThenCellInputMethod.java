/*
 * Andoku - a sudoku puzzle game for Android.
 * Copyright (C) 2010  Markus Wiederkehr
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

package com.app2go.sudokufree.im;

import android.os.Bundle;

import com.app2go.sudokufree.model.Position;
import com.app2go.sudokufree.model.ValueSet;

public class ValuesThenCellInputMethod implements InputMethod {
	private static final String APP_STATE_KEYPAD_VALUES = "keypadValues";

	private final InputMethodTarget target;

	private final ValueSet values = new ValueSet();

	public ValuesThenCellInputMethod(InputMethodTarget target) {
		this.target = target;
	}

	public boolean isValuesEmpty() {
		return values.isEmpty();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(APP_STATE_KEYPAD_VALUES, values.toInt());
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		int v = savedInstanceState.getInt(APP_STATE_KEYPAD_VALUES, 0);
		setValues(v);
	}

	@Override
	public void reset() {
		target.setMarkedPosition(null);
		target.highlightDigit(null);
		setValues(0);
	}

	@Override
	public void onMoveMark(int dy, int dx) {
	}

	@Override
	public void onKeypad(int digit) {
		if (values.contains(digit)) {
			values.remove(digit);
			target.checkButton(digit, false);
		}
		else {
			values.add(digit);
			target.checkButton(digit, true);
		}

		if (values.isEmpty())
			target.highlightDigit(null);
		else
			target.highlightDigit(digit);
	}

	@Override
	public void onClear() {
		setValues(0);

		target.highlightDigit(null);
	}

	@Override
	public void onInvert() {
		final int nButtons = target.getNumberOfDigitButtons();
		for (int digit = 0; digit < nButtons; digit++) {
			if (values.contains(digit))
				values.remove(digit);
			else
				values.add(digit);
		}

		checkButtons();
	}

	@Override
	public void onSweep() {
	}

	@Override
	public void onTap(Position position, boolean editable) {
		if (!editable || values.isEmpty())
			return;

		ValueSet cellValues = target.getCellValues(position);
		if (cellValues.containsAny(values)) {
			cellValues.removeAll(values);
			target.setCellValues(position, cellValues);
		}
		else {
			cellValues.addAll(values);
			target.setCellValues(position, cellValues);
		}
	}

	@Override
	public void onValuesChanged() {
	}

	private void setValues(int v) {
		values.setFromInt(v);

		checkButtons();
	}

	private void checkButtons() {
		final int nButtons = target.getNumberOfDigitButtons();
		for (int digit = 0; digit < nButtons; digit++)
			target.checkButton(digit, values.contains(digit));
	}
}
