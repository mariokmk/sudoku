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

package com.app2go.sudokufree;

import com.app2go.sudokufree.im.AutomaticInputMethod;
import com.app2go.sudokufree.im.CellThenValuesInputMethod;
import com.app2go.sudokufree.im.InputMethod;
import com.app2go.sudokufree.im.InputMethodTarget;
import com.app2go.sudokufree.im.ValuesThenCellInputMethod;

public enum InputMethodPolicy {
	CELL_THEN_VALUES {
		@Override
		public InputMethod createInputMethod(InputMethodTarget target) {
			return new CellThenValuesInputMethod(target);
		}
	},
	VALUES_THEN_CELL {
		@Override
		public InputMethod createInputMethod(InputMethodTarget target) {
			return new ValuesThenCellInputMethod(target);
		}
	},
	AUTOMATIC {
		@Override
		public InputMethod createInputMethod(InputMethodTarget target) {
			return new AutomaticInputMethod(target);
		}
	};

	public abstract InputMethod createInputMethod(InputMethodTarget target);
}
