package com.ddiehl.flashcard.activities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.adapters.ListSelectionAdapter;
import com.ddiehl.flashcard.quizsession.PhraseCollection;

public class Activity_ListSelection extends Activity {
	private static final String TAG = "Activity_ListSelection";
	ArrayList<PhraseCollection> vocabularyLists = new ArrayList<PhraseCollection>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_selection);
		
		AssetManager assets = getAssets();
		String[] list_filenames = null;
		
		try {
			list_filenames = assets.list(getString(R.string.assetListGroup));
		} catch (IOException e) {
			Log.e(TAG, "Error retrieving assets.");
			e.printStackTrace();
		}
		
		for (int i = 0; i < list_filenames.length; i++) {
			InputStream thisList;
			try {
				thisList = assets.open(getString(R.string.assetListGroup) + "/" + list_filenames[i]);
			} catch (IOException e) {
				thisList = null;
				Log.e(TAG, "Error opening asset.");
				e.printStackTrace();
			}
	        vocabularyLists.add(new PhraseCollection(thisList));
		}
		ListSelectionAdapter adapter =
				new ListSelectionAdapter(this, R.layout.activity_list_selection_item, vocabularyLists);
		
		ListView vLists = (ListView) findViewById(R.id.vocabulary_lists);
		vLists.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(getBaseContext(), Activity_LoadListData.class);
				intent.putExtra("PhraseCollection", vocabularyLists.get(position));
//				intent.putExtra("listnumber", position);
				view.getContext().startActivity(intent);
			}
			
		});
		vLists.setAdapter(adapter);
	}
	
	public void loadListData(View view) {
		Intent i = new Intent(this, Activity_LoadListData.class);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
	    switch(item.getItemId()){
	    case R.id.action_settings:
	        Intent intent = new Intent(this, SettingsActivity.class);
	        startActivity(intent);
	        return true;
	    }
	    return false;
	}
}
