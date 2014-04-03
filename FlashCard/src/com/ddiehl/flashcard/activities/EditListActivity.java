package com.ddiehl.flashcard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.quizsession.PhraseCollection;

public class EditListActivity extends Activity {
	private static final String TAG = "EditListActivity";
	private PhraseCollection pc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_list);
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("PhraseCollection")) {
			pc = extras.getParcelable("PhraseCollection");
			TextView tv = (TextView) findViewById(R.id.edit_list_title);
			tv.setText(pc.getTitle());
		} else {
			Log.e(TAG, "No PhraseCollection included with extras.");
		}
	}
	
	public void addPhrase(View v) {
		Log.d(TAG, "Add phrase button clicked.");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_list, menu);
		return true;
	}

}
