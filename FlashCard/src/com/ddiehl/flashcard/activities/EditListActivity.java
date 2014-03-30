package com.ddiehl.flashcard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.ddiehl.flashcard.R;

public class EditListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_list);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_list, menu);
		return true;
	}

}
