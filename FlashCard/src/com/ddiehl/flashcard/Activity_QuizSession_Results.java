package com.ddiehl.flashcard;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.view.Menu;

public class Activity_QuizSession_Results extends Activity {
	private ArrayList<Quiz> qc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz_session_results);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey("QuizCollection")) {
				qc = extras.getParcelableArrayList("QuizCollection");
			}
		}
		// Calculate results and populate layout
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
