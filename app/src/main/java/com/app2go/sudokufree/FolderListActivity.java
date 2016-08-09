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

package com.app2go.sudokufree;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.app2go.sudokufree.db.SudokuDatabase;
import com.app2go.sudokufree.source.PuzzleSourceIds;

public class FolderListActivity extends ListActivity {
	private static final String TAG = FolderListActivity.class.getName();

	private static final String APP_STATE_SELECTED_FOLDER_ID = "selectedFolderId";

	private static final int MENU_CREATE_FOLDER = Menu.FIRST;

	private static final int MENU_ITEM_RENAME = Menu.FIRST;
	private static final int MENU_ITEM_DELETE = Menu.FIRST + 1;

	private static final int DIALOG_CREATE_FOLDER = 0;
	private static final int DIALOG_RENAME_FOLDER = 1;
	private static final int DIALOG_CONFIRM_DELETE_FOLDER = 2;

	private long parentFolderId;
	private SudokuDatabase db;
	private Cursor cursor;

	private long selectedFolderId;

	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (Constants.LOG_V)
			Log.v(TAG, "onCreate(" + savedInstanceState + ")");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.folders);

		parentFolderId = getFolderIdFromIntent();

		db = new SudokuDatabase(this);
		cursor = db.getFolders(parentFolderId);
		startManagingCursor(cursor);

		SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cursor,
				new String[] { SudokuDatabase.COL_FOLDER_NAME }, new int[] { android.R.id.text1 });
		setListAdapter(listAdapter);

		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onOpenFolder(id);
			}
		});

		getListView().setOnCreateContextMenuListener(this);

		View backButton = findViewById(R.id.backButton);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		if (Constants.LOG_V)
			Log.v(TAG, "onDestroy()");

		super.onDestroy();

		if (db != null) {
			db.close();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (Constants.LOG_V)
			Log.v(TAG, "onSaveInstanceState()");

		super.onSaveInstanceState(outState);

		outState.putLong(APP_STATE_SELECTED_FOLDER_ID, selectedFolderId);
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		if (Constants.LOG_V)
			Log.v(TAG, "onRestoreInstanceState()");

		super.onRestoreInstanceState(state);

		selectedFolderId = state.getLong(APP_STATE_SELECTED_FOLDER_ID, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_CREATE_FOLDER, Menu.NONE, R.string.menu_create_folder).setIcon(
				android.R.drawable.ic_menu_add);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_CREATE_FOLDER:
				onCreateFolder();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (!(menuInfo instanceof AdapterView.AdapterContextMenuInfo)) {
			Log.e(TAG, "bad menuInfo");
			return;
		}

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
		if (cursor == null) {
			// For some reason the requested item isn't available, do nothing
			return;
		}

		menu.setHeaderTitle(cursor.getString(SudokuDatabase.IDX_FOLDERS_NAME));
		menu.add(0, MENU_ITEM_RENAME, 0, R.string.context_menu_rename_folder);
		menu.add(0, MENU_ITEM_DELETE, 1, R.string.context_menu_delete_folder);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ContextMenuInfo menuInfo = item.getMenuInfo();
		if (!(menuInfo instanceof AdapterView.AdapterContextMenuInfo)) {
			Log.e(TAG, "bad menuInfo");
			return false;
		}

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		selectedFolderId = info.id;

		switch (item.getItemId()) {
			case MENU_ITEM_RENAME:
				onRenameFolder();
				return true;
			case MENU_ITEM_DELETE:
				onDeleteFolder();
				return true;
		}

		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (Constants.LOG_V)
			Log.v(TAG, "onCreateDialog(" + id + ")");

		switch (id) {
			case DIALOG_CREATE_FOLDER:
				return createCreateFolderDialog();
			case DIALOG_RENAME_FOLDER:
				return createRenameFolderDialog();
			case DIALOG_CONFIRM_DELETE_FOLDER:
				return createConfirmDeleteFolderDialog();
			default:
				return null;
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (Constants.LOG_V)
			Log.v(TAG, "onPrepareDialog(" + id + ")");

		super.onPrepareDialog(id, dialog);

		switch (id) {
			case DIALOG_CREATE_FOLDER:
				prepareCreateFolderDialog((AlertDialog) dialog);
				break;
			case DIALOG_RENAME_FOLDER:
				prepareRenameFolderDialog((AlertDialog) dialog);
				break;
			case DIALOG_CONFIRM_DELETE_FOLDER:
				prepareConfirmDeleteFolderDialog((AlertDialog) dialog);
				break;
			default:
				return;
		}
	}

	private long getFolderIdFromIntent() {
		Bundle extras = getIntent().getExtras();
		return extras.getLong(Constants.EXTRA_FOLDER_ID, SudokuDatabase.ROOT_FOLDER_ID);
	}

	private void onOpenFolder(long folderId) {
		cancelToast();

		if (!db.hasPuzzles(folderId)) {
			String message = getResources().getString(R.string.message_empty_folder,
					db.getFolderName(folderId));
			toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			toast.show();
			return;
		}

		String puzzleSourceId = PuzzleSourceIds.forDbFolder(folderId);

		new GameLauncher(this, db).startNewGame(puzzleSourceId);
	}

	private void onCreateFolder() {
		showDialog(DIALOG_CREATE_FOLDER);
	}

	private void createFolder(String name) {
		if (!SudokuDatabase.isValidFolderName(name)) {
			String message = getResources().getString(R.string.message_invalid_folder_name, name);
			toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
			toast.show();
		}
		else if (db.folderExists(parentFolderId, name)) {
			String message = getResources().getString(R.string.message_folder_exists, name);
			toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
			toast.show();
		}
		else {
			db.createFolder(parentFolderId, name);
			cursor.requery();
		}
	}

	private Dialog createCreateFolderDialog() {
		final ContextThemeWrapper context = new ContextThemeWrapper(this,
				android.R.style.Theme_Dialog);
		final View layout = View.inflate(context, R.layout.dialog_edit, null);
		final EditText nameText = (EditText) layout.findViewById(R.id.name);

		TextView label = (TextView) layout.findViewById(R.id.label);
		label.setText(getResources().getString(R.string.message_create_folder));

		return new AlertDialog.Builder(this).setView(layout).setIcon(R.drawable.edit_dialog_icon)
				.setTitle(R.string.dialog_create_folder)
				.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						createFolder(nameText.getText().toString());
					}
				})
				.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).create();
	}

	private void prepareCreateFolderDialog(AlertDialog dialog) {
		EditText nameText = (EditText) dialog.findViewById(R.id.name);
		nameText.setText("");
	}

	private void onRenameFolder() {
		showDialog(DIALOG_RENAME_FOLDER);
	}

	private void renameFolder(String name) {
		String oldName = getSelectedFolderName();
		if (name.equals(oldName))
			return;

		if (!SudokuDatabase.isValidFolderName(name)) {
			String message = getResources().getString(R.string.message_invalid_folder_name, name);
			toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
			toast.show();
		}
		else if (db.folderExists(parentFolderId, name)) {
			String message = getResources().getString(R.string.message_folder_exists, name);
			toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
			toast.show();
		}
		else {
			db.renameFolder(selectedFolderId, name);
			cursor.requery();
		}
	}

	private Dialog createRenameFolderDialog() {
		final ContextThemeWrapper context = new ContextThemeWrapper(this,
				android.R.style.Theme_Dialog);
		final View layout = View.inflate(context, R.layout.dialog_edit, null);
		final EditText nameText = (EditText) layout.findViewById(R.id.name);

		return new AlertDialog.Builder(this).setView(layout).setIcon(R.drawable.edit_dialog_icon)
				.setTitle(R.string.dialog_rename_folder)
				.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						renameFolder(nameText.getText().toString());
					}
				})
				.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).create();
	}

	private void prepareRenameFolderDialog(AlertDialog dialog) {
		Resources resources = getResources();
		String name = getSelectedFolderName();

		TextView label = (TextView) dialog.findViewById(R.id.label);
		label.setText(resources.getString(R.string.message_rename_folder, name));
		EditText nameText = (EditText) dialog.findViewById(R.id.name);
		nameText.setText(name);
	}

	private void onDeleteFolder() {
		if (db.isEmpty(selectedFolderId)) {
			deleteFolder(selectedFolderId);
		}
		else {
			showDialog(DIALOG_CONFIRM_DELETE_FOLDER);
		}
	}

	private void deleteFolder(long folderId) {
		db.deleteFolder(folderId);
		cursor.requery();
	}

	private Dialog createConfirmDeleteFolderDialog() {
		return new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.dialog_delete_folder).setMessage("")
				.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						deleteFolder(selectedFolderId);
					}
				})
				.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).create();
	}

	private void prepareConfirmDeleteFolderDialog(AlertDialog dialog) {
		dialog.setMessage(getResources().getString(R.string.message_delete_folder,
				getSelectedFolderName()));
	}

	private String getSelectedFolderName() {
		return db.getFolderName(selectedFolderId);
	}

	private void cancelToast() {
		if (toast != null) {
			toast.cancel();
			toast = null;
		}
	}
}
