package com.ddiehl.flashcard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.adapters.QuizResultsAdapter;
import com.ddiehl.flashcard.quizsession.Quiz;
import com.ddiehl.flashcard.quizsession.QuizCollection;

import java.util.ArrayList;
import java.util.List;

public class QuizSessionResultsActivity extends Activity {
	private static final String TAG = QuizSessionResultsActivity.class.getSimpleName();
	private QuizCollection qc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz_session_results);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey("QuizCollection")) {
				qc = extras.getParcelable("QuizCollection");
				displayQuizResults();
			}
		}
	}
	
	private void displayQuizResults() {
		// Set title of list
		TextView vTitle = (TextView) findViewById(R.id.sessionResults_listTitle_value);
		vTitle.setText(qc.getTitle());
		// Set number of phrases studied
		TextView vPhrasesStudied = (TextView) findViewById(R.id.sessionResults_phrasesTotal_value);
		vPhrasesStudied.setText(String.valueOf(qc.size()));
		// List out phrases in ListView
		List<Quiz> correctAnswers = new ArrayList<Quiz>();
		List<Quiz> incorrectAnswers = new ArrayList<Quiz>();
		for (int i = 0; i < qc.size(); i++) {
			Quiz q = qc.get(i);
			if (q.getPotentialScore() == q.getActualScore())
				correctAnswers.add(q);
			else
				incorrectAnswers.add(q);
		}
		QuizCollection list = new QuizCollection();
		for (int i = 0; i < incorrectAnswers.size(); i++) {
			list.add(incorrectAnswers.get(i));
		}
		for (int i = 0; i < correctAnswers.size(); i++) {
			list.add(correctAnswers.get(i));
		}
		QuizResultsAdapter adapter = new QuizResultsAdapter(this, R.layout.activity_quiz_session_results_phrase, list);
		ListView vLists = (ListView) findViewById(R.id.phraseList);
		vLists.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// If we want any onClick behavior, set it here
			}
		});
		vLists.setAdapter(adapter);
	}
	
	public void returnToListDetails(View v) {
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
