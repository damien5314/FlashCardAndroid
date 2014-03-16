package com.ddiehl.flashcard.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;

import com.ddiehl.flashcard.Activity_LoadListData;
import com.ddiehl.flashcard.R;
import com.robotium.solo.Solo;

public class Activity_LoadListData_Test extends	ActivityInstrumentationTestCase2<Activity_LoadListData> {
	private static final String TAG = "Activity_LoadListData_Test";
	private static final int TIMEOUT = 5000;
	private Solo solo;
	
	public Activity_LoadListData_Test() {
		super(Activity_LoadListData.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(this.getInstrumentation(), this.getActivity());
	}
	
	@MediumTest
	public void testContentLoaded() {
		assertNotNull(solo.getView(R.id.list_data_title));
		assertNotNull(solo.getView(R.id.list_data_wordcount_total_value));
		assertNotNull(solo.getView(R.id.list_data_wordcount_started_value));
		assertNotNull(solo.getView(R.id.list_data_wordcount_completed_value));
        Button vStartButton = (Button) solo.getView(R.id.list_data_start);
        assertNotNull(vStartButton);
        // test button onclick?
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
