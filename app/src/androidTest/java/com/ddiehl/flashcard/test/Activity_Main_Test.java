package com.ddiehl.flashcard.test;

import android.test.SingleLaunchActivityTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.ListView;

import com.ddiehl.android.flashcard.R;
import com.ddiehl.android.flashcard.activities.ListSelectionActivity;
import com.ddiehl.android.flashcard.activities.MainActivity;
import com.robotium.solo.Solo;

public class Activity_Main_Test extends SingleLaunchActivityTestCase<MainActivity> {
	private Solo solo;
	private int TIMEOUT = 10000;

	public Activity_Main_Test() {
		super("com.ddiehl.flashcard", MainActivity.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testPreConditions() {
		assertNotNull("Activity is null", solo.getCurrentActivity());
	}
	
	@MediumTest
	public void testStartButtonClick() {
		assertNotNull(solo.getView(R.id.main_header));
//		assertEquals(getActivity().getString(R.id.main_header), mHeaderText.getText()); // not working
		assertTrue(solo.waitForView(R.id.button_start));
		Button mStartButton = (Button) solo.getView(R.id.button_start);
		assertNotNull("Start button is null", mStartButton);
		solo.clickOnView(mStartButton);
		assertTrue("ListSelection not loaded", solo.waitForActivity(ListSelectionActivity.class, TIMEOUT));
		assertTrue("ListView not loaded", solo.waitForView(R.id.vocabulary_lists));
		ListView vVocabularyLists = (ListView) solo.getView(R.id.vocabulary_lists);
		assertNotNull("ListView is null", vVocabularyLists);
		assertTrue("ListView has no children", vVocabularyLists.getChildCount() != 0);
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
