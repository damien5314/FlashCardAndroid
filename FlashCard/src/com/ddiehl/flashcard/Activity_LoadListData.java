package com.ddiehl.flashcard;

import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Activity_LoadListData extends Activity {
	private static final String TAG = "Activity_LoadListData";
	private final int[] optionValues = { 5, 10, 20 };
	private String mFilename;
	private int numPhrasesToStudy;
	private PhraseCollection pc;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_list_data);
		setNumPhrasesToStudy(10); // Initialize to the default number of Phrases
    	getSharedPreferences("com.ddiehl.flashcard", Context.MODE_PRIVATE).edit().clear().commit(); // Clear SharedPrefs
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int listNumber;
			if (!extras.containsKey("listnumber")) {
				listNumber = 0;
			} else {
				listNumber = extras.getInt("listnumber");
				AssetManager assets = getAssets();
				String[] filenameList = null;
				try {
					filenameList = assets.list(getString(R.string.assetListGroup));
				} catch (IOException e) {
					Log.e(TAG, "Error retrieving assets.");
					e.printStackTrace();
				}
				mFilename = getString(R.string.assetListGroup) + "/" + filenameList[listNumber];
				InputStream vocabularyList;
				try {
					vocabularyList = assets.open(mFilename);
				} catch (IOException e) {
					vocabularyList = null;
					Log.e(TAG, "Error opening asset.");
					e.printStackTrace();
				}
				pc = new PhraseCollection(vocabularyList);
				refreshListData();
			}
		}

		// Initialize options buttons
		LinearLayout vButtons = (LinearLayout) findViewById(R.id.list_data_sessionOptions_buttons);
		final int numOptions = vButtons.getChildCount();
		for (int i = 0; i < numOptions; i++) {
			Button b = (Button) vButtons.getChildAt(i);
			// Set text to correct value from optionValues
			b.setText(String.valueOf(optionValues[i]));
			// Set onClick functionality of the button
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Remove color filter of all other buttons 
					for (int k = 0; k < numOptions; k++) {
						Button b2 = (Button) ((LinearLayout)v.getParent()).getChildAt(k);
						b2.getBackground().setColorFilter(null);
//						b2.setBackgroundResource(R.drawable.option_button_unpressed);
					}
					// Set color filter on button pressed
					v.getBackground().setColorFilter(Color.CYAN,PorterDuff.Mode.MULTIPLY);
//					v.setBackgroundResource(R.drawable.option_button_pressed);
					// Set global variable tracking number of phrases to study
					setNumPhrasesToStudy(Integer.parseInt(((Button)v).getText().toString()));
					// Refresh ListView to display correct icons
					ListView vPhrases = (ListView) findViewById(R.id.list_data_phrases);
					ListPhrasesAdapter adapter = (ListPhrasesAdapter) vPhrases.getAdapter();
					for (int j = 0; j < adapter.getCount(); j++) {
						Phrase p = (Phrase) adapter.getItem(j);
						if (j+1 > getNumPhrasesToStudy())
							p.setIncludedInSession(false);
						else
							p.setIncludedInSession(true);
					}
					refreshListData();
				}
			});
		}
		
		if (android.os.Build.VERSION.SDK_INT >= 15)
			vButtons.getChildAt(1).callOnClick();
		else
			vButtons.getChildAt(1).performClick();
	}
	
	public void refreshListData() {
		TextView vTitle, vPhrasesTotal, vPhrasesStarted, vPhrasesMastered;
		vTitle = (TextView) findViewById(R.id.list_data_title);
		vPhrasesTotal = (TextView) findViewById(R.id.list_data_wordcount_total_value);
		vPhrasesStarted = (TextView) findViewById(R.id.list_data_wordcount_started_value);
		vPhrasesMastered = (TextView) findViewById(R.id.list_data_wordcount_completed_value);
		vTitle.setText(pc.getTitle());
		vPhrasesTotal.setText(String.valueOf(pc.getPhrasesTotal()));
		vPhrasesStarted.setText(String.valueOf(pc.getPhrasesStarted()));
		vPhrasesMastered.setText(String.valueOf(pc.getPhrasesMastered()));
		
		ListPhrasesAdapter adapter = new ListPhrasesAdapter(this, R.layout.activity_load_list_data_phrase, pc);
		ListView vLists = (ListView) findViewById(R.id.list_data_phrases);
		vLists.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// If we want any onClick behavior, set it here
			}
		});
		vLists.setAdapter(adapter);
	}
	
    public void startQuizSession(View view) {
		Intent intent = new Intent(this, QuizSessionController.class);
//		intent.putExtra("mFilename", mFilename);
		intent.putExtra("PhraseCollection", (Parcelable)pc);
		startActivity(intent);
    }
    
    public int getNumPhrasesToStudy() {
		return numPhrasesToStudy;
	}

	public void setNumPhrasesToStudy(int numPhrasesToStudy) {
		this.numPhrasesToStudy = numPhrasesToStudy;
	}

	@Override
    protected void onPause() {
    	super.onPause();
    	SharedPreferences prefs = this.getSharedPreferences("com.ddiehl.flashcard", Context.MODE_PRIVATE);
    	prefs.edit().putString("mFilename", mFilename).commit();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	SharedPreferences prefs = this.getSharedPreferences("com.ddiehl.flashcard", Context.MODE_PRIVATE);
    	mFilename = prefs.getString("mFilename", mFilename);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
