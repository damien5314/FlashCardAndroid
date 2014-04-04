package com.ddiehl.flashcard.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ddiehl.flashcard.activities.MainActivity;
import com.ddiehl.flashcard.activities.QuizSession_Results;
import com.ddiehl.flashcard.quizsession.Phrase;
import com.ddiehl.flashcard.quizsession.Quiz;
import com.ddiehl.flashcard.quizsession.QuizCollection;
import com.robotium.solo.Solo;

public class Activity_QuizSession_Results_Test extends ActivityInstrumentationTestCase2<MainActivity> {
	private static final String TAG = "Activity_QuizSession_Results_Test";
	private static final int TIMEOUT = 5000;
	private Solo solo;
	
	public Activity_QuizSession_Results_Test() {
		super(MainActivity.class);
}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		Intent intent = new Intent(getActivity().getApplicationContext(), QuizSession_Results.class);
		QuizCollection qc = new QuizCollection();
		qc.setTitle(getInstrumentation().getContext().getString(R.string.results_test_string_1));
		Phrase p;
		for (int i = 0; i < 15; i++) {
			p = new Phrase();
			String s = getInstrumentation().getContext().getString(com.ddiehl.flashcard.test.R.string.results_test_phrase_1);
			p.setPhraseNative(s);
			Quiz q = new Quiz(1, p);
			double chance = Math.random();
			if (chance < .2)
				q.setPotentialScore(1);
			qc.add(q);
		}
		intent.putExtra("QuizCollection", qc);
		this.getActivity().startActivity(intent);
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@MediumTest
	public void testResultsScreen() {
		String v;
		
		v = ((TextView) solo.getView(com.ddiehl.flashcard.R.id.sessionResults_listTitle_value)).getText().toString();
		assertNotNull(v);
		assertFalse(v.equals(getActivity().getString(com.ddiehl.flashcard.R.string.sessionResults_listTitle_default)));
		
		v = ((TextView) solo.getView(com.ddiehl.flashcard.R.id.sessionResults_phrasesTotal_value)).getText().toString();
		assertNotNull(v);
		assertFalse(v.equals(getActivity().getString(com.ddiehl.flashcard.R.string.sessionResults_phrasesTotal_default)));
		
		ListView lv = ((ListView) solo.getView(com.ddiehl.flashcard.R.id.phraseList));
		assertNotNull(lv);
		assertFalse(lv.getChildCount() == 0);
		
		Button b = (Button) solo.getButton(0);
		assertNotNull(b);
	}

	@Override
	public void tearDown() throws Exception {
		solo.sleep(5000);
		super.tearDown();
	}
	
}
