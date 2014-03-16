package com.ddiehl.flashcard.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ddiehl.flashcard.Activity_ListSelection;
import com.ddiehl.flashcard.Activity_LoadListData;
import com.ddiehl.flashcard.Activity_Main;
import com.ddiehl.flashcard.Activity_QuizSession_Results;
import com.ddiehl.flashcard.Activity_Quiz_NativePhonetic;
import com.ddiehl.flashcard.QuizSessionController;
import com.ddiehl.flashcard.R;
import com.robotium.solo.Solo;

public class QuizSessionE2ETest extends ActivityInstrumentationTestCase2<Activity_Main> {
	private static final String TAG = "QuizSessionE2ETest";
	private static final int TIMEOUT = 5000;
	private Solo solo;
	
	public QuizSessionE2ETest() {
		super(Activity_Main.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@MediumTest
	public void testE2E() throws InterruptedException {
		assertTrue("Activity_Main not loaded", solo.waitForActivity(Activity_Main.class, TIMEOUT));
		assertTrue("Start button not loaded", solo.waitForView(R.id.button_start));
		solo.clickOnView(solo.getView(R.id.button_start));
		
		assertTrue("Activity_ListSelection not loaded", solo.waitForActivity(Activity_ListSelection.class, TIMEOUT));
		assertTrue("ListView not loaded", solo.waitForView(R.id.vocabulary_lists));
		solo.clickInList(0);
		
		assertTrue("Activity_LoadListData not loaded", solo.waitForActivity(Activity_LoadListData.class, TIMEOUT));
		assertTrue("Start button not loaded", solo.waitForView(R.id.list_data_start));
		solo.clickOnView(solo.getView(R.id.list_data_start));
		
		while (solo.waitForActivity(Activity_Quiz_NativePhonetic.class, TIMEOUT)) {
			Activity_Quiz_NativePhonetic act = (Activity_Quiz_NativePhonetic) solo.getCurrentActivity();
			String correctAnswer = act.getQuiz().getQuizPhrase().getPhrasePhonetic();
			assertTrue("Choice1 not loaded", solo.waitForView(R.id.choice1));
			assertTrue("Choice2 not loaded", solo.waitForView(R.id.choice2));
			String choice1 = String.valueOf(((Button) act.findViewById(R.id.choice1)).getText());
			View myChoice = (correctAnswer.equals(choice1)) ? solo.getView(R.id.choice1) : solo.getView(R.id.choice2);
			solo.clickOnView(myChoice);
			solo.waitForActivity(QuizSessionController.class, TIMEOUT);
		} // Ends after we run out of quizzes
		
		assertTrue("Results screen not loaded", solo.waitForActivity(Activity_QuizSession_Results.class, TIMEOUT));
		
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
