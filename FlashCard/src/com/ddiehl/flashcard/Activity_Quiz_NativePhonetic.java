package com.ddiehl.flashcard;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Activity_Quiz_NativePhonetic extends Activity {
	private static final String TAG = "Activity_Quiz_KanjiKana";
	private Quiz q;
	private QuizCollection qc;
	private QuizSession qs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz_native_phonetic);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("Quiz"))
			q = extras.getParcelable("Quiz");
		if (extras.containsKey("QuizCollection"))
			qc = extras.getParcelable("QuizCollection");
		if (extras.containsKey("QuizSession"))
			qs = extras.getParcelable("QuizSession");
		
		Log.d(TAG, "Quiz Type = " + q.getQuizType());
		Log.d(TAG, "Phrase Native = " + q.getQuizPhrase().getPhraseNative());
		Log.d(TAG, "Phrase Phonetic = " + q.getQuizPhrase().getPhrasePhonetic());
		
		try {
			String kanji = q.getQuizPhrase().getPhraseNative();
			TextView tvQuestion = (TextView) findViewById(R.id.quizNative);
			tvQuestion.setText(kanji);
		} catch (Exception e) {
			Log.e(TAG, "Error setting question TextView");
			e.printStackTrace();
		}
		
		Phrase randomPhrase = null;
		if (extras.containsKey("QuizCollection")) {
			QuizCollection qc = extras.getParcelable("QuizCollection");
			do { // Perform until you get a random that isn't the same as the current word
				int randomIndex = (int) (Math.random()*qc.size());
				randomPhrase = qc.get(randomIndex).getQuizPhrase();
			} while (q.getQuizPhrase().getPhraseNative().equals(randomPhrase.getPhraseNative()));
		}
		
		int rand = (int) Math.floor(Math.random() + 0.5); // Will = 0 or 1 with equal probability
		Button button1, button2;
		switch (rand) {
			case 0: // Read the buttons in order
				button1 = (Button) findViewById(R.id.choice1);
				button2 = (Button) findViewById(R.id.choice2);
				break;
			case 1: // Flip the order of buttons
				button1 = (Button) findViewById(R.id.choice2);
				button2 = (Button) findViewById(R.id.choice1);
				break;
			default:
				button1 = null;
				button2 = null;
				Log.e(TAG, "Bad random input.");
		}
		
		if (button1 != null && button2 != null) {
			button1.setText(q.getQuizPhrase().getPhrasePhonetic());
			button2.setText(randomPhrase.getPhrasePhonetic());
			setCorrectAnswer(button1, true);
			setCorrectAnswer(button2, false);
		}
	}
	
	private void setCorrectAnswer(Button b, boolean isCorrectAnswer) {
		if (isCorrectAnswer) {
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Log.i(TAG, "CORRECT answer selected.");
					removeButtonOnClickListeners();
					v.getBackground().setColorFilter(Color.GREEN,PorterDuff.Mode.MULTIPLY);
					Intent returnIntent = new Intent();
					setResult(1, returnIntent);
					Handler h = new Handler();
					h.postDelayed(new Runnable() {
						public void run() {
							finish();	
						}
					}, getQuizAnswerDelay());
				}
			});
		} else {
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Log.i(TAG, "INCORRECT answer selected.");
					removeButtonOnClickListeners();
					v.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.MULTIPLY);
					Intent returnIntent = new Intent();
					setResult(2, returnIntent);
					Handler h = new Handler();
					h.postDelayed(new Runnable() {
						public void run() {
							finish();	
						}
					}, getQuizAnswerDelay());
				}
			});
		}
	}
	
	private int getQuizAnswerDelay() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String quizAnswerDelay = sharedPref.getString("pref_quizAnswerDelay", "750");
		return Integer.parseInt(quizAnswerDelay);
	}
	
	private void removeButtonOnClickListeners() {
		Button[] buttons = new Button[2];
		buttons[0] = (Button) findViewById(R.id.choice1);
		buttons[1] = (Button) findViewById(R.id.choice2);
		buttons[0].setOnClickListener(null);
		buttons[1].setOnClickListener(null);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	// Display prompt asking user if he or she wants to quit or save session
	    	// see http://androidsnippets.com/prompt-user-input-with-an-alertdialog
	    	FragmentManager fm = getFragmentManager();
	        final ConfirmSessionEndDialog dialog = ConfirmSessionEndDialog.newInstance();
	        dialog.show(fm, "dialog_confirm_end_session");
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public Quiz getQuiz() {
		return q;
	}
	
	public void quitAndSaveSession(View v) {
    	finish();
	}
	
	public void quitAndDiscardSession(View v) {
		finish();
	}
}
