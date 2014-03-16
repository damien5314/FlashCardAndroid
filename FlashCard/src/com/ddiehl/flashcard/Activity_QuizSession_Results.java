package com.ddiehl.flashcard;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class Activity_QuizSession_Results extends Activity {
	private QuizCollection qc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz_session_results);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey("QuizCollection")) {
				qc = extras.getParcelable("QuizCollection");
				initializeContent();
			}
		}
		// Calculate results and populate layout
	}
	
	private void initializeContent() {
		// Set title of list
		TextView tv = (TextView) findViewById(R.id.sessionResults_listTitle_value);
		tv.setText(qc.getTitle());
		// Set number of phrases studied
		// List out phrases in ListView
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
