package com.ddiehl.flashcard.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ddiehl.flashcard.Activity_ListSelection;
import com.ddiehl.flashcard.Activity_LoadListData;
import com.ddiehl.flashcard.R;
import com.robotium.solo.Solo;

public class Activity_ListSelection_Test extends ActivityInstrumentationTestCase2<Activity_ListSelection> {
	private static final String TAG = "Activity_ListSelection_Test";
	private static final int TIMEOUT = 5000;
	private Solo solo;
	
	public Activity_ListSelection_Test() {
		super(Activity_ListSelection.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@MediumTest
	public void testListSelection() {
		assertNotNull(solo);
		ListView lv = (ListView) solo.getView(R.id.vocabulary_lists);
		assertNotNull(lv);
		View vToClick = lv.getChildAt(0);
		assertNotNull(vToClick);
		solo.clickInList(0);
		solo.waitForActivity(Activity_LoadListData.class, TIMEOUT);
		
		TextView tv;
		Button b;
		tv = (TextView) solo.getView(R.id.list_data_title);
		assertTrue("default title", !tv.getText().equals(solo.getString(R.string.list_data_title_default)));
		tv = (TextView) solo.getView(R.id.list_data_wordcount_total_value);
		assertTrue("default total wordcount", !tv.getText().equals(solo.getString(R.string.list_data_wordcount_total_default)));
		tv = (TextView) solo.getView(R.id.list_data_wordcount_started_value);
		assertTrue("default started wordcount", !tv.getText().equals(solo.getString(R.string.list_data_wordcount_started_default)));
		tv = (TextView) solo.getView(R.id.list_data_wordcount_completed_value);
		assertTrue("default completed wordcount", !tv.getText().equals(solo.getString(R.string.list_data_wordcount_completed_default)));
		b = (Button) solo.getView(R.id.list_data_start);
		assertNotNull("null button", b);
	}
}
