package com.ddiehl.flashcard;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

public class QuizSessionController extends Activity {
	private static final String TAG = "QuizSessionController";
	private String mFilename;
	private InputStream mVocabularyList;
	private QuizSession session;
	private PhraseCollection phrases;
	private QuizCollection quizzesAll;
	private QuizCollection quizzesIncomplete;
	private QuizCollection quizzesComplete;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		session = new QuizSession();
		Bundle extras = getIntent().getExtras();
		
		if (extras.containsKey("mFilename")) {
			mFilename = extras.getString("mFilename");
			AssetManager assets = getAssets();
			try {
				Log.d(TAG, "mFilename = " + mFilename);
				mVocabularyList = assets.open(mFilename);
			} catch (IOException e) {
				mVocabularyList = null;
				Log.e(TAG, "Error opening asset.");
				e.printStackTrace();
			}
	        this.quizzesAll = generateQuizCollection(mVocabularyList);
		}
		
		if (extras.containsKey("PhraseCollection")) {
			phrases = extras.getParcelable("PhraseCollection");
	    	quizzesAll = new QuizCollection(phrases);
	    	quizzesIncomplete = (QuizCollection) quizzesAll.clone();
	    	quizzesComplete = new QuizCollection();
		}
		
        Log.i(TAG,"QuizCollection generated");
        startQuizSession();
	}
    
    private QuizCollection generateQuizCollection(InputStream in_s) {
    	phrases = new PhraseCollection(in_s);
    	quizzesAll = new QuizCollection(phrases);
    	quizzesIncomplete = (QuizCollection) quizzesAll.clone();
    	quizzesComplete = new QuizCollection();
    	return quizzesAll;
    }
    
    private void startQuizSession() {
    	Log.i(TAG, "Starting quiz session.");
    	// Add an animation screen here?
		Quiz q = quizzesIncomplete.get(0);
		startQuiz(q);
	}
    
    private void endQuizSession() {
    	Log.i(TAG, "Ending quiz session.");
    	finish();
    }
    
    private void startQuiz(Quiz q) {
		Intent intent = null;
    	switch (q.getQuizType()) {
		case 1:
			intent = new Intent(this, Activity_Quiz_NativePhonetic.class);
			intent.putExtra("Quiz", q);
			intent.putExtra("QuizSession", session);
			intent.putExtra("QuizCollection", quizzesAll);
			intent.putExtra("PhraseCollection", (Parcelable)phrases);
			break;
    	// Add cases for additional quiz types when developed
		}
		if (intent != null)
			startActivityForResult(intent, 1);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 1) { // This is always the case for now
			Quiz q = quizzesIncomplete.get(0);
    		if (resultCode == 1) {
				q.setActualScore(1);
				q.setPotentialScore(q.getPotentialScore()+1);
				q.setCorrectlyAnswered(true);
    			quizzesComplete.add(quizzesIncomplete.remove(0)); // Remove from incomplete set, add to complete set
    		} else if (resultCode == 2) {
				q.setPotentialScore(q.getPotentialScore()+1); // Only set potentialScore, leave other variables as default 0/false
    			quizzesIncomplete.add(quizzesIncomplete.remove(0)); // Push to the end of the list to be executed again
    		}
    		
    		if (resultCode == RESULT_CANCELED) {
				endQuizSession();
    		} else if (!quizzesIncomplete.isEmpty()) {
    			Quiz nextQuiz = quizzesIncomplete.get(0);
    			startQuiz(nextQuiz);
			} else { // No more quizzes
				// Display results screen
				Intent intent = new Intent(getBaseContext(), Activity_QuizSession_Results.class);
				intent.putExtra("QuizCollection", quizzesAll);
				
				startActivityForResult(intent, 2);
//				endQuizSession();
			}
    	} else if (requestCode == 2) {
    		// Confirmation page has finished, end quiz session
    		endQuizSession();
    	}
    }
}
