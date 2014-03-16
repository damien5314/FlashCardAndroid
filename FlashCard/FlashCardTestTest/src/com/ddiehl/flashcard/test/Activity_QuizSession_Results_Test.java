package com.ddiehl.flashcard.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.ddiehl.flashcard.Activity_QuizSession_Results;
import com.robotium.solo.Solo;

public class Activity_QuizSession_Results_Test extends ActivityInstrumentationTestCase2<Activity_QuizSession_Results> {
	private static final String TAG = "Activity_QuizSession_Results_Test";
	private static final int TIMEOUT = 5000;
	private Solo solo;
	
	public Activity_QuizSession_Results_Test() {
		super(Activity_QuizSession_Results.class);
}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@MediumTest
	public void testResultsScreen() {
		
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
}
