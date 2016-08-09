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

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class AboutActivity extends BackButtonActivity {
	private static final String TAG = AboutActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (Constants.LOG_V)
			Log.v(TAG, "onCreate(" + savedInstanceState + ")");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.about);

		View backButton = findViewById(R.id.backButton);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		TextView aboutNewView = (TextView) findViewById(R.id.aboutNewView);
		aboutNewView.setBackgroundColor(0);
		aboutNewView.setMovementMethod(new ScrollingMovementMethod());
		setTextViewContent(aboutNewView, R.string.html_page_about_new);

		TextView aboutView = (TextView) findViewById(R.id.aboutView);
		aboutView.setBackgroundColor(0);
		aboutView.setMovementMethod(new ScrollingMovementMethod());
		setTextViewContent(aboutView, R.string.html_page_about);
	}

	private void setTextViewContent(TextView textView, int assetFile) {
		// Read HTML from the asset file
		try {
			InputStream is = this.getAssets().open(getResources().getString(assetFile));
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			is.close();

			textView.setText(Html.fromHtml(new String(buffer)));
		} catch (IOException e) {
			Log.e(TAG, "Error setting HTML content to the view", e);
		}

	}
}
