package com.ddiehl.flashcard;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
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
	private String mFilename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_list_data);
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
				PhraseCollection pc = new PhraseCollection(vocabularyList);
				populateListData(pc);
			}
		}
		
		// Add onClick functionality to the options buttons
		int[] optionValues = {5,10,20};
		LinearLayout vButtons = (LinearLayout) findViewById(R.id.list_data_sessionOptions_buttons);
		int numOptions = vButtons.getChildCount();
		for (int i = 0; i < numOptions; i++) {
			Button b = (Button) vButtons.getChildAt(i);
			b.setText(String.valueOf(optionValues[i]));
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Set global variable representing # phrases to study
					// Refresh ListView to display correct icons
				}
			});
		}
	}
	
	public void populateListData(PhraseCollection pc) {
		TextView vTitle, vPhrasesTotal, vPhrasesStarted, vPhrasesMastered;
		vTitle = (TextView) findViewById(R.id.list_data_title);
		vPhrasesTotal = (TextView) findViewById(R.id.list_data_wordcount_total_value);
		vPhrasesStarted = (TextView) findViewById(R.id.list_data_wordcount_started_value);
		vPhrasesMastered = (TextView) findViewById(R.id.list_data_wordcount_completed_value);
		vTitle.setText(pc.getTitle());
		vPhrasesTotal.setText(String.valueOf(pc.getPhrasesTotal()));
		vPhrasesStarted.setText(String.valueOf(pc.getPhrasesStarted()));
		vPhrasesMastered.setText(String.valueOf(pc.getPhrasesMastered()));
		
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < pc.size(); i++) {
			list.add(pc.get(i).getPhraseNative());
		}
		
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
		intent.putExtra("mFilename", mFilename);
		startActivity(intent);
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
